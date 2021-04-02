package com.payment.module.model;

import javax.persistence.*;

@Entity
@Table(name = "TB_PAYMENT")
public class TbPayment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAY_ID")
    private Integer payId;

    @Column(name = "ADV_ID")
    private Integer advId;

    @Column(name = "PAY_TNO")
    private String payTno;

    @Column(name = "PAY_AMOUNT")
    private String payAmount;

    @Column(name = "PAY_CARD_CD")
    private String payCardCd;

    @Column(name = "PAY_CARD_NAME")
    private String payCardName;

    @Column(name = "PAY_APP_TIME")
    private String payAppTime;

    @Column(name = "PAY_APP_NO")
    private String payAppNo;

    @Column(name = "PAY_NOINF")
    private String payNoinf;

    @Column(name = "PAY_QUOTA")
    private String payQuota;

    @Column(name = "PAY_PARTCANC_YN")
    private String payPartcancYn;

    @Column(name = "PAY_CARD_BIN_TYPE_01")
    private String payCardBinType01;

    @Column(name = "PAY_CARD_BIN_TYPE_02")
    private String payCardBinType02;

    @Column(name = "PAY_CARD_MNY")
    private String payCardMny;

    @Column(name = "PAY_DT")
    private java.sql.Timestamp payDt;

    public Integer getPayId() {
        return this.payId;
    }

    public void setPayId(Integer payId) {
        this.payId = payId;
    }

    public Integer getAdvId() {
        return this.advId;
    }

    public void setAdvId(Integer advId) {
        this.advId = advId;
    }

    public String getPayTno() {
        return this.payTno;
    }

    public void setPayTno(String payTno) {
        this.payTno = payTno;
    }

    public String getPayAmount() {
        return this.payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getPayCardCd() {
        return this.payCardCd;
    }

    public void setPayCardCd(String payCardCd) {
        this.payCardCd = payCardCd;
    }

    public String getPayCardName() {
        return this.payCardName;
    }

    public void setPayCardName(String payCardName) {
        this.payCardName = payCardName;
    }

    public String getPayAppTime() {
        return this.payAppTime;
    }

    public void setPayAppTime(String payAppTime) {
        this.payAppTime = payAppTime;
    }

    public String getPayAppNo() {
        return this.payAppNo;
    }

    public void setPayAppNo(String payAppNo) {
        this.payAppNo = payAppNo;
    }

    public String getPayNoinf() {
        return this.payNoinf;
    }

    public void setPayNoinf(String payNoinf) {
        this.payNoinf = payNoinf;
    }

    public String getPayQuota() {
        return this.payQuota;
    }

    public void setPayQuota(String payQuota) {
        this.payQuota = payQuota;
    }

    public String getPayPartcancYn() {
        return this.payPartcancYn;
    }

    public void setPayPartcancYn(String payPartcancYn) {
        this.payPartcancYn = payPartcancYn;
    }

    public String getPayCardBinType01() {
        return this.payCardBinType01;
    }

    public void setPayCardBinType01(String payCardBinType01) {
        this.payCardBinType01 = payCardBinType01;
    }

    public String getPayCardBinType02() {
        return this.payCardBinType02;
    }

    public void setPayCardBinType02(String payCardBinType02) {
        this.payCardBinType02 = payCardBinType02;
    }

    public String getPayCardMny() {
        return this.payCardMny;
    }

    public void setPayCardMny(String payCardMny) {
        this.payCardMny = payCardMny;
    }

    public java.sql.Timestamp getPayDt() {
        return this.payDt;
    }

    public void setPayDt(java.sql.Timestamp payDt) {
        this.payDt = payDt;
    }
}
