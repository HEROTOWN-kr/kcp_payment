package com.payment.module.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;

@Entity
@Table(name = "TB_SUBSCRIPTION")
public class TbSubscription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUB_ID")
    private Integer subId;

    @Column(name = "ADV_ID")
    private Integer advId;

    @Column(name = "PLN_ID")
    private Integer plnId;

    @Column(name = "SUB_START_DT")
    private java.sql.Date subStartDt;

    @Column(name = "SUB_END_DT")
    private java.sql.Date subEndDt;

    @Column(name = "SUB_STATUS")
    private String subStatus;

    @Column(name = "SUB_ACTIVE")
    private String subActive;

    @CreationTimestamp
    @Column(name = "SUB_DT")
    private java.sql.Timestamp subDt;

    public Integer getSubId() {
        return this.subId;
    }

    public void setSubId(Integer subId) {
        this.subId = subId;
    }

    public Integer getAdvId() {
        return this.advId;
    }

    public void setAdvId(Integer advId) {
        this.advId = advId;
    }

    public Integer getPlnId() {
        return this.plnId;
    }

    public void setPlnId(Integer plnId) {
        this.plnId = plnId;
    }

    public java.sql.Date getSubStartDt() {
        return this.subStartDt;
    }

    public void setSubStartDt(java.sql.Date subStartDt) {
        this.subStartDt = subStartDt;
    }

    public java.sql.Date getSubEndDt() {
        return this.subEndDt;
    }

    public void setSubEndDt(java.sql.Date subEndDt) {
        this.subEndDt = subEndDt;
    }

    public String getSubStatus() {
        return this.subStatus;
    }

    public void setSubStatus(String subStatus) {
        this.subStatus = subStatus;
    }

    public String getSubActive() {
        return this.subActive;
    }

    public void setSubActive(String subActive) {
        this.subActive = subActive;
    }

    public java.sql.Timestamp getSubDt() {
        return this.subDt;
    }

    public void setSubDt(java.sql.Timestamp subDt) {
        this.subDt = subDt;
    }
}
