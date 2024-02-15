package com.msn.settlementmodule.settlementmodule.entities;

import java.io.Serializable;
import java.util.Date;


import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class PACS008 implements Serializable{
    
    @AffinityKeyMapped
    @QuerySqlField(name = "ENDTOENDID", index = true)
    private String endToEndId;

    @QuerySqlField(name = "DBTRBIC")
    private String bicPengirim;

    @QuerySqlField(name = "CDTRBIC")
    private String bicPenerima;

    @QuerySqlField(name = "STTL_DATE")
    private String sttlDate;

    @QuerySqlField(name = "AMMOUNT")
    private Double ammount;

    @QuerySqlField(name = "INSERT_DATE")
    private Date insertDate;

    @QuerySqlField(name = "TXNSTS_RESP")
    private String txnStsResp;

    @QuerySqlField(name = "TXNRSN_RESP")
    private String txnRsnResp;

    public PACS008() {
    }

    public PACS008(String endToEndId, String bicPengirim, String bicPenerima, String sttlDate, Double ammount,
            Date insertDate, String txnStsResp, String txnRsnResp) {
        this.endToEndId = endToEndId;
        this.bicPengirim = bicPengirim;
        this.bicPenerima = bicPenerima;
        this.sttlDate = sttlDate;
        this.ammount = ammount;
        this.insertDate = insertDate;
        this.txnStsResp = txnStsResp;
        this.txnRsnResp = txnRsnResp;
    }

    public String getEndToEndId() {
        return endToEndId;
    }
    public void setEndToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
    }
    public String getBicPengirim() {
        return bicPengirim;
    }
    public void setBicPengirim(String bicPengirim) {
        this.bicPengirim = bicPengirim;
    }
    public String getBicPenerima() {
        return bicPenerima;
    }
    public void setBicPenerima(String bicPenerima) {
        this.bicPenerima = bicPenerima;
    }
    public String getSttlDate() {
        return sttlDate;
    }
    public void setSttlDate(String sttlDate) {
        this.sttlDate = sttlDate;
    }
    public Double getAmmount() {
        return ammount;
    }
    public void setAmmount(Double ammount) {
        this.ammount = ammount;
    }
    public Date getInsertDate() {
        return insertDate;
    }
    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }
    
    public String getTxnStsResp() {
        return txnStsResp;
    }
    public void setTxnStsResp(String txnStsResp) {
        this.txnStsResp = txnStsResp;
    }
    public String getTxnRsnResp() {
        return txnRsnResp;
    }
    public void setTxnRsnResp(String txnRsnResp) {
        this.txnRsnResp = txnRsnResp;
    }
}
