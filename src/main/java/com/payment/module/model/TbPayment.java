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

    @Column(name = "PAY_DT", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
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

    public java.sql.Timestamp getPayDt() {
        return this.payDt;
    }

    public void setPayDt(java.sql.Timestamp payDt) {
        this.payDt = payDt;
    }
}
