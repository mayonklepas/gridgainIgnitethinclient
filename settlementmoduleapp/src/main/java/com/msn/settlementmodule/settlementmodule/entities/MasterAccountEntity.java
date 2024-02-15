package com.msn.settlementmodule.settlementmodule.entities;


import java.io.Serializable;


import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.cache.query.annotations.QuerySqlField;


public class MasterAccountEntity implements Serializable{
    
    @AffinityKeyMapped
    @QuerySqlField(name = "BIC_PESERTA", index = true)
    private String bic;

    @QuerySqlField(name = "PREFUND_SALDO")
    private Double saldo;

    @QuerySqlField(name = "TOTAL_NOMINAL_DB")
    private Double dbAmount;

    @QuerySqlField(name = "TOTAL_NOMINAL_KR")
    private Double crAmount;

    @QuerySqlField(name = "TOTAL_TRANSAKSI_DB")
    private Integer dbTrx;

    @QuerySqlField(name = "TOTAL_TRANSAKSI_KR")
    private Integer crTrx;

    @QuerySqlField(name = "TOTAL_NOMINAL_HOLD_DB")
    private Double holdDb;

    @QuerySqlField(name = "PREFUND_SALDO_AKHIR")
    private Double saldoAkhir;
    
    @QuerySqlField(name = "NAMA_BANK")
    private Double namaBank;
    

    public MasterAccountEntity(String bic, Double saldo, Double dbAmount, Double crAmount, Integer dbTrx, Integer crTrx,
            Double holdDb, Double saldAkhir, double namaBank) {
        this.bic = bic;
        this.saldo = saldo;
        this.dbAmount = dbAmount;
        this.crAmount = crAmount;
        this.dbTrx = dbTrx;
        this.crTrx = crTrx;
        this.holdDb = holdDb;
        this.saldoAkhir = saldAkhir;
        this.namaBank = this.namaBank;
    }

    public MasterAccountEntity() {
    }

    public String getBic() {
        return bic;
    }
    public void setBic(String bic) {
        this.bic = bic;
    }
    public Double getSaldo() {
        if(saldo==null){
            saldo = 0.0;
        }
        return saldo;
    }
    public void setSaldo(Double balance) {
        this.saldo = balance;
    }
    public Double getDbAmount() {
        if(dbAmount==null){
            dbAmount = 0.0;
        }
        return dbAmount;
    }
    public void setDbAmount(Double dbAmount) {
        this.dbAmount = dbAmount;
    }
    public Double getCrAmount() {
        if(crAmount==null){
            crAmount = 0.0;
        }
        return crAmount;
    }
    public void setCrAmount(Double crAmount) {
        this.crAmount = crAmount;
    }
    public Integer getDbTrx() {
        return dbTrx;
    }
    public void setDbTrx(Integer dbTrx) {
        this.dbTrx = dbTrx;
    }
    public Integer getCrTrx() {
        return crTrx;
    }
    public void setCrTrx(Integer crTrx) {
        this.crTrx = crTrx;
    }

    public Double getHoldDb() {
        if(holdDb==null){
            holdDb = 0.0;
        }
        return holdDb;
    }

    public void setHoldDb(Double holdDb) {
        this.holdDb = holdDb;
    }

    public Double getSaldoAkhir() {
        if(saldoAkhir==null){
            saldoAkhir = 0.0;
        }
        return saldoAkhir;
    }

    public void setSaldoAkhir(Double saldAkhir) {
        this.saldoAkhir = saldAkhir;
    }

    public Double getNamaBank() {
        return namaBank;
    }

    public void setNamaBank(Double namaBank) {
        this.namaBank = namaBank;
    }
    
    

    
}
