package com.payment.module.model;

import org.hibernate.annotations.CreationTimestamp;

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

    @Column(name = "PAY_PNT_ISSUE")
    private String payPntIssue;

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

    @Column(name = "PAY_COUPON_MNY")
    private String payCouponMny;

    @Column(name = "PAY_PNT_AMOUNT")
    private String payPntAmount;

    @Column(name = "PAY_PNT_APP_TIME")
    private String payPntAppTime;

    @Column(name = "PAY_PNT_APP_NO")
    private String payPntAppNo;

    @Column(name = "PAY_ADD_PNT")
    private String payAddPnt;

    @Column(name = "PAY_USE_PNT")
    private String payUsePnt;

    @Column(name = "PAY_RSV_PNT")
    private String payRsvPnt;

    @Column(name = "PAY_TOTAL_AMOUNT")
    private String payTotalAmount;

    @Column(name = "PAY_BANK_NAME")
    private String payBank_Name;

    @Column(name = "PAY_BANK_CODE")
    private String payBankCode;

    @Column(name = "PAY_BK_MNY")
    private String payBkMny;

    @Column(name = "PAY_BANKNAME")
    private String payBankname;

    @Column(name = "PAY_DEPOSITOR")
    private String payDepositor;

    @Column(name = "PAY_ACCOUNT")
    private String payAccount;

    @Column(name = "PAY_VA_DATE")
    private String payVaDate;

    @Column(name = "PAY_COMMID")
    private String payCommid;

    @Column(name = "PAY_MOBILE_NO")
    private String payMobileNo;

    @Column(name = "PAY_TK_VAN_CODE")
    private String payTkVanCode;

    @Column(name = "PAY_TK_APP_NO")
    private String payTkAppNo;

    @Column(name = "PAY_CASH_AUTHNO")
    private String payCashAuthno;

    @Column(name = "PAY_CASH_NO")
    private String payCashNo;

    @CreationTimestamp
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

    public String getPayPntIssue() {
        return this.payPntIssue;
    }

    public void setPayPntIssue(String payPntIssue) {
        this.payPntIssue = payPntIssue;
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

    public String getPayCouponMny() {
        return this.payCouponMny;
    }

    public void setPayCouponMny(String payCouponMny) {
        this.payCouponMny = payCouponMny;
    }

    public String getPayPntAmount() {
        return this.payPntAmount;
    }

    public void setPayPntAmount(String payPntAmount) {
        this.payPntAmount = payPntAmount;
    }

    public String getPayPntAppTime() {
        return this.payPntAppTime;
    }

    public void setPayPntAppTime(String payPntAppTime) {
        this.payPntAppTime = payPntAppTime;
    }

    public String getPayPntAppNo() {
        return this.payPntAppNo;
    }

    public void setPayPntAppNo(String payPntAppNo) {
        this.payPntAppNo = payPntAppNo;
    }

    public String getPayAddPnt() {
        return this.payAddPnt;
    }

    public void setPayAddPnt(String payAddPnt) {
        this.payAddPnt = payAddPnt;
    }

    public String getPayUsePnt() {
        return this.payUsePnt;
    }

    public void setPayUsePnt(String payUsePnt) {
        this.payUsePnt = payUsePnt;
    }

    public String getPayRsvPnt() {
        return this.payRsvPnt;
    }

    public void setPayRsvPnt(String payRsvPnt) {
        this.payRsvPnt = payRsvPnt;
    }

    public String getPayTotalAmount() {
        return this.payTotalAmount;
    }

    public void setPayTotalAmount(String payTotalAmount) {
        this.payTotalAmount = payTotalAmount;
    }

    public String getPayBank_Name() {
        return this.payBank_Name;
    }

    public void setPayBank_Name(String payBank_Name) {
        this.payBank_Name = payBank_Name;
    }

    public String getPayBankCode() {
        return this.payBankCode;
    }

    public void setPayBankCode(String payBankCode) {
        this.payBankCode = payBankCode;
    }

    public String getPayBkMny() {
        return this.payBkMny;
    }

    public void setPayBkMny(String payBkMny) {
        this.payBkMny = payBkMny;
    }

    public String getPayBankname() {
        return this.payBankname;
    }

    public void setPayBankname(String payBankname) {
        this.payBankname = payBankname;
    }

    public String getPayDepositor() {
        return this.payDepositor;
    }

    public void setPayDepositor(String payDepositor) {
        this.payDepositor = payDepositor;
    }

    public String getPayAccount() {
        return this.payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount;
    }

    public String getPayVaDate() {
        return this.payVaDate;
    }

    public void setPayVaDate(String payVaDate) {
        this.payVaDate = payVaDate;
    }

    public String getPayCommid() {
        return this.payCommid;
    }

    public void setPayCommid(String payCommid) {
        this.payCommid = payCommid;
    }

    public String getPayMobileNo() {
        return this.payMobileNo;
    }

    public void setPayMobileNo(String payMobileNo) {
        this.payMobileNo = payMobileNo;
    }

    public String getPayTkVanCode() {
        return this.payTkVanCode;
    }

    public void setPayTkVanCode(String payTkVanCode) {
        this.payTkVanCode = payTkVanCode;
    }

    public String getPayTkAppNo() {
        return this.payTkAppNo;
    }

    public void setPayTkAppNo(String payTkAppNo) {
        this.payTkAppNo = payTkAppNo;
    }

    public String getPayCashAuthno() {
        return this.payCashAuthno;
    }

    public void setPayCashAuthno(String payCashAuthno) {
        this.payCashAuthno = payCashAuthno;
    }

    public String getPayCashNo() {
        return this.payCashNo;
    }

    public void setPayCashNo(String payCashNo) {
        this.payCashNo = payCashNo;
    }

    public java.sql.Timestamp getPayDt() {
        return this.payDt;
    }

    public void setPayDt(java.sql.Timestamp payDt) {
        this.payDt = payDt;
    }
}
