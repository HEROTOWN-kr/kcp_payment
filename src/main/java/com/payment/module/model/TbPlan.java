package com.payment.module.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;

@Entity
@Table(name = "TB_PLAN")
public class TbPlan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PLN_ID")
    private Integer plnId;

    @Column(name = "PLN_NAME")
    private String plnName;

    @Column(name = "PLN_DETAIL")
    private String plnDetail;

    @Column(name = "PLN_DETAIL2")
    private String plnDetail2;

    @Column(name = "PLN_MONTH")
    private Integer plnMonth;

    @Column(name = "PLN_INF_MONTH")
    private Integer plnInfMonth;

    @Column(name = "PLN_PRICE_MONTH")
    private Integer plnPriceMonth;

    @Column(name = "PLN_DSCNT")
    private String plnDscnt;

    @Column(name = "PLN_VISIBLE")
    private Integer plnVisible;

    @CreationTimestamp
    @Column(name = "PLN_DT")
    private java.sql.Timestamp plnDt;

    public Integer getPlnId() {
        return this.plnId;
    }

    public void setPlnId(Integer plnId) {
        this.plnId = plnId;
    }

    public String getPlnName() {
        return this.plnName;
    }

    public void setPlnName(String plnName) {
        this.plnName = plnName;
    }

    public String getPlnDetail() {
        return this.plnDetail;
    }

    public void setPlnDetail(String plnDetail) {
        this.plnDetail = plnDetail;
    }

    public String getPlnDetail2() {
        return this.plnDetail2;
    }

    public void setPlnDetail2(String plnDetail2) {
        this.plnDetail2 = plnDetail2;
    }

    public Integer getPlnMonth() {
        return this.plnMonth;
    }

    public void setPlnMonth(Integer plnMonth) {
        this.plnMonth = plnMonth;
    }

    public Integer getPlnInfMonth() {
        return this.plnInfMonth;
    }

    public void setPlnInfMonth(Integer plnInfMonth) {
        this.plnInfMonth = plnInfMonth;
    }

    public Integer getPlnPriceMonth() {
        return this.plnPriceMonth;
    }

    public void setPlnPriceMonth(Integer plnPriceMonth) {
        this.plnPriceMonth = plnPriceMonth;
    }

    public String getPlnDscnt() {
        return this.plnDscnt;
    }

    public void setPlnDscnt(String plnDscnt) {
        this.plnDscnt = plnDscnt;
    }

    public Integer getPlnVisible() {
        return this.plnVisible;
    }

    public void setPlnVisible(Integer plnVisible) {
        this.plnVisible = plnVisible;
    }

    public java.sql.Timestamp getPlnDt() {
        return this.plnDt;
    }

    public void setPlnDt(java.sql.Timestamp plnDt) {
        this.plnDt = plnDt;
    }
}
