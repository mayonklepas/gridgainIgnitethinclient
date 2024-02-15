package com.msn.settlementmodule.settlementmodule.entities;

import java.io.Serializable;
import java.util.Date;


import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.cache.query.annotations.QuerySqlField;


public class PACS002 implements Serializable{
    
    
    @AffinityKeyMapped
    @QuerySqlField(name = "MSGID", index = true)
    private String msgId;

    @QuerySqlField(name = "ENDTOENDID_ORI")
    private String endToEndIdOri;

    @QuerySqlField(name = "STTL_DATE")
    private String sttDate;

    @QuerySqlField(name = "TXNSTS")
    private String txnSts;

    @QuerySqlField(name = "TXNRSN")
    private String txnRsn;

    @QuerySqlField(name = "INSERT_DATE")
    private Date insertDate;

    public PACS002(String msgId, String endToEndIdOri, String sttDate, String txnSts, String txnRsn, Date insertDate) {
        this.msgId = msgId;
        this.endToEndIdOri = endToEndIdOri;
        this.sttDate = sttDate;
        this.txnSts = txnSts;
        this.txnRsn = txnRsn;
        this.insertDate = insertDate;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getEndToEndIdOri() {
        return endToEndIdOri;
    }

    public void setEndToEndIdOri(String endToEndIdOri) {
        this.endToEndIdOri = endToEndIdOri;
    }

    public String getSttDate() {
        return sttDate;
    }

    public void setSttDate(String sttDate) {
        this.sttDate = sttDate;
    }

    public String getTxnSts() {
        return txnSts;
    }

    public void setTxnSts(String txnSts) {
        this.txnSts = txnSts;
    }

    public String getTxnRsn() {
        return txnRsn;
    }

    public void setTxnRsn(String txnRsn) {
        this.txnRsn = txnRsn;
    }

    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    
}
