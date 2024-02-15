package com.msn.settlementmodule.settlementmodule.utils;

import static com.msn.settlementmodule.settlementmodule.controllers.TransactionController.LOGGER;
import com.msn.settlementmodule.settlementmodule.entities.MasterAccountEntity;
import com.msn.settlementmodule.settlementmodule.entities.PACS002;
import com.msn.settlementmodule.settlementmodule.entities.PACS008;
import com.msn.settlementmodule.settlementmodule.entities.TransactionEntity;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteTransactions;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionException;
import org.apache.ignite.transactions.TransactionIsolation;

/**
 *
 * @author mulyadi
 */
public class SttlProcess implements IgniteCallable<Integer> {

    @IgniteInstanceResource
    Ignite ignite;

    Map<String, Object> request;

    public SttlProcess(Map<String, Object> request) {
        this.request = request;
    }

    @Override
    public Integer call() throws Exception {
        return processSttl();
    }

    public int processSttl() {

        IgniteCache<String, MasterAccountEntity> masterCache = ignite.cache("masterAccountCache");
        IgniteCache<String, TransactionEntity> trxCache = ignite.cache("transactionCache");
        IgniteCache<String, PACS008> pacs008Cache = ignite.cache("pacs008Cache");
        IgniteCache<String, PACS002> pacs002Cache = ignite.cache("pacs002Cache");

        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

        String type = request.get("type").toString();
        String endToEndId = request.get("endToEndId").toString();

        int resExistCode = isTxnExist();
        if (resExistCode != 999) {
            return resExistCode;
        }

        if (type.equals("008")) {
            boolean isExistBIC = (getLocalPeekMasterAccount(request.get("bicPengirim").toString()) == null) ? false : true;
            if (!isExistBIC) {
                return -3;
            }
        }

        IgniteTransactions transactions = ignite.transactions();

        //Lock processLock = ignite.reentrantLock("processLock", true, false, true);
        int result = 0;
        try (Transaction tx = transactions.txStart(TransactionConcurrency.PESSIMISTIC,
                TransactionIsolation.REPEATABLE_READ)) {
            //processLock.lock();

            if (type.equals("008")) {

                String bicPengirim = request.get("bicPengirim").toString();
                String bicPenerima = request.get("bicPenerima").toString();
                String sttlDate = request.get("sttlDate").toString();
                double amount = (double) request.get("amount");

                //save pacs008 request 
                pacs008Cache.put(endToEndId, new PACS008(endToEndId, bicPengirim, bicPenerima, sttlDate, amount, new Date(), "", ""));

                String id = endToEndId + "_HOLD";

                TransactionEntity trxStl = insertTrx(trxCache, id, endToEndId + "_HOLD", bicPengirim, amount, 0, 8);

                MasterAccountEntity masterAccount = getLocalPeekMasterAccount(bicPengirim);
                Double currentBalance = masterAccount.getSaldo() - masterAccount.getHoldDb() - masterAccount.getDbAmount()
                        + masterAccount.getCrAmount();

                if (amount > currentBalance) {
                    result = -2;
                    trxStl.setUpdateTime(sdfTime.format(new Date()));
                    trxStl.setStatus(-1);
                    trxCache.put(id, trxStl);
                    System.out.println("FAILED: (Insufficient saldo) Process transaction hold endtoendid:" + endToEndId
                            + ", bic:" + bicPengirim + ", nominal:" + amount);

                } else {
                    MasterAccountEntity masterAccountsDebet = getLocalPeekMasterAccount(bicPengirim);
                    masterAccountsDebet.setHoldDb(masterAccountsDebet.getHoldDb() + amount);
                    masterCache.put(masterAccountsDebet.getBic(), masterAccountsDebet);

                    Date updateDate = new Date();
                    trxStl.setUpdateTime(sdfTime.format(updateDate));
                    trxStl.setStatus(1);
                    trxCache.put(id, trxStl);
                    System.out.println(id + " : 008 execute complete");
                }

                tx.commit();
                result = 1;

            } else {

                String bicPengirim = request.get("bicPengirim").toString();
                String bicPenerima = request.get("bicPenerima").toString();
                double amount = (double) request.get("amount");
                String sttlDate = request.get("sttlDate").toString();
                String msgId = request.get("msgId").toString();
                String txSts = request.get("txSts").toString();
                String txRsn = request.get("txReason").toString();

                //save pacs002
                pacs002Cache.put(msgId, new PACS002(msgId, endToEndId, sttlDate, txSts, txRsn, new Date()));

                // Debet
                String idDb = endToEndId + "_DB";

                TransactionEntity trxStlDb = insertTrx(trxCache, idDb, idDb, bicPengirim, amount, 2, 2);

                MasterAccountEntity masterAccountsDebet = getLocalPeekMasterAccount(bicPengirim);

                masterAccountsDebet.setHoldDb(masterAccountsDebet.getHoldDb() - amount);
                masterAccountsDebet.setDbAmount(masterAccountsDebet.getDbAmount() + amount);
                masterAccountsDebet.setDbTrx(masterAccountsDebet.getDbTrx() + 1);

                masterCache.put(masterAccountsDebet.getBic(), masterAccountsDebet);

                Date updateDateDb = new Date();
                trxStlDb.setUpdateTime(sdfTime.format(updateDateDb));
                trxStlDb.setStatus(1);
                trxCache.put(idDb, trxStlDb);

                // kredit
                String idCr = endToEndId + "_CR";

                TransactionEntity trxStlCr = insertTrx(trxCache, idCr, idCr, bicPenerima, amount, 2, 2);

                MasterAccountEntity masterAccountsKredit = getLocalPeekMasterAccount(bicPenerima);

                masterAccountsKredit.setCrAmount(masterAccountsKredit.getCrAmount() + amount);
                masterAccountsKredit.setCrTrx(masterAccountsKredit.getCrTrx() + 1);

                masterCache.put(masterAccountsKredit.getBic(), masterAccountsKredit);

                Date updateDateCr = new Date();
                trxStlCr.setUpdateTime(sdfTime.format(updateDateCr));
                trxStlCr.setStatus(1);
                trxCache.put(idCr, trxStlCr);

                tx.commit();
                result = 1;

                System.out.println(idDb + "/" + idCr + " : 002 execute complete");

            }

        } catch (TransactionException e) {
            // Transaction has failed. Retry
            result = -1;
            LOGGER.warn("Transaction has failed. Retry. data rollback");
        } finally {
            //processLock.unlock();
        }

        return result;
    }

    public int isTxnExist() {
        IgniteCache<String, PACS008> pacs008Cache = ignite.cache("pacs008Cache");
        String endToEndid = request.get("endToEndId").toString();
        String type = request.get("type").toString();
        String bicReq = "";
        if (type.equals("008")) {
            endToEndid = endToEndid + "_HOLD";
            bicReq = request.get("bicPengirim").toString();
        } else {
            PACS008 pacs008data = pacs008Cache.get(endToEndid);
            if (pacs008data == null) {
                return 99;
            }
            bicReq = pacs008data.getBicPengirim();
            endToEndid = endToEndid + "_CR";

        }

        //SqlFieldsQuery sql = new SqlFieldsQuery("SELECT ENDTOENDID,BIC,CASH_NOMINAL,STATUS FROM TransactionEntity WHERE ENDTOENDID='" + endToEndid + "'");
        //List<List<?>> c = trxCache.query(sql).getAll();
        IgniteCache<String, TransactionEntity> trxCache = ignite.cache("transactionCache");
        TransactionEntity trx = trxCache.get(endToEndid);
        if (trx != null) {
            String bic = trx.getBic();
            double amount = trx.getAmount();
            int status = trx.getStatus();

            if (bicReq != bic && ((double) request.get("amount")) != amount) {
                switch (status) {
                    case 0:
                        return -40;
                    case 1:
                        return -41;
                    case -1:
                        return -49;

                    default:
                        return -40;
                }
            }
            return -4;

        }

        return 999;
    }

    public TransactionEntity insertTrx(IgniteCache<String, TransactionEntity> trxCache, String id, String endtoendid, String bic_peserta, Double nominal, int status, int type) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        TransactionEntity trxStl = new TransactionEntity();
        trxStl.setEndToEndId(endtoendid);
        trxStl.setAmount(nominal);
        trxStl.setBic(bic_peserta);
        trxStl.setInsertTime(sdfTime.format(new Date()));
        trxStl.setStatus(status);
        trxStl.setTanggal(sdf.format(new Date()));
        trxStl.setTxnTipe(type);
        trxCache.put(endtoendid, trxStl);
        return trxStl;

    }

    public MasterAccountEntity getLocalPeekMasterAccount(String key) {
        IgniteCache<String, BinaryObject> master = ignite.cache("masterAccountCache").withKeepBinary();
        BinaryObject bo = master.localPeek(key, CachePeekMode.NEAR);

        MasterAccountEntity entity = new MasterAccountEntity(
                bo.field("bic"),
                bo.field("saldo"),
                bo.field("dbAmount"),
                bo.field("crAmount"),
                bo.field("dbTrx"),
                bo.field("crTrx"),
                bo.field("holdDb"),
                bo.field("saldoAkhir"),
                0.0);

        return entity;
    }

}
