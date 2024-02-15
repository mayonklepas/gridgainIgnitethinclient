package com.msn.settlementmodule.settlementmodule.entities;

import java.io.Serializable;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class TransactionEntity implements Serializable{

    @AffinityKeyMapped
    @QuerySqlField(name = "ENDTOENDID", index = true)
    private String endToEndId;

    @QuerySqlField(name = "TANGGAL")
    private String tanggal;

    @QuerySqlField(name = "BIC")
    private String bic;

    @QuerySqlField(name = "TXN_TIPE")
    private Integer txnTipe;

    @QuerySqlField(name = "WAKTU_INSERT")
    private String insertTime;

    @QuerySqlField(name = "WAKTU_UPDATE")
    private String updateTime;

    @QuerySqlField(name = "CASH_NOMINAL")
    private Double amount;

    @QuerySqlField(name = "STATUS")
    private Integer status;

    public TransactionEntity() {
    }

    public TransactionEntity(String endToEndId, String tanggal, String bic, Integer txnTipe,String insertTime,
            String updateTime,
            Double amount, Integer status) {
        this.tanggal = tanggal;
        this.insertTime = insertTime;
        this.updateTime = updateTime;
        this.amount = amount;
        this.status = status;
        this.endToEndId = endToEndId;
        this.bic = bic;
        this.txnTipe = txnTipe;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(final String tanggal) {
        this.tanggal = tanggal;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(final String updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(final Integer status) {
        this.status = status;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(final String insertTime) {
        this.insertTime = insertTime;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(final Double amount) {
        this.amount = amount;
    }

    public String getEndToEndId() {
        return endToEndId;
    }

    public void setEndToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public Integer getTxnTipe() {
        return txnTipe;
    }

    public void setTxnTipe(Integer txnTipe) {
        this.txnTipe = txnTipe;
    }

}
