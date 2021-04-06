package com.payment.module.model;

import javax.persistence.*;

@Entity
@Table(name = "TB_ADVERTISER")
public class TbAdvertiser {
    @Id
    @Column(name = "ADV_ID")
    private Integer advId;

    @Column(name = "ADV_NAME")
    private String advName;

    @Column(name = "ADV_DT")
    private java.sql.Timestamp advDt;

    public Integer getAdvId() {
        return this.advId;
    }

    public void setAdvId(Integer advId) {
        this.advId = advId;
    }

    public String getAdvName() {
        return this.advName;
    }

    public void setAdvName(String advName) {
        this.advName = advName;
    }

    public java.sql.Timestamp getAdvDt() {
        return this.advDt;
    }

    public void setAdvDt(java.sql.Timestamp advDt) {
        this.advDt = advDt;
    }
}
