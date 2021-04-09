package com.payment.module.model;

import javax.persistence.*;

@Entity
@Table(name = "TB_ADVERTISER")
public class TbAdvertiser {
    @Id
    @Column(name = "ADV_ID")
    private Integer advId;

    @Column(name = "ADV_PASS")
    private String advPass;

    @Column(name = "ADV_NAME")
    private String advName;

    @Column(name = "ADV_TEL")
    private String advTel;

    @Column(name = "ADV_EMAIL")
    private String advEmail;

    @Column(name = "ADV_POST_CODE")
    private String advPostCode;

    @Column(name = "ADV_ROAD_ADDR")
    private String advRoadAddr;

    @Column(name = "ADV_DETAIL_ADDR")
    private String advDetailAddr;

    @Column(name = "ADV_EXTR_ADDR")
    private String advExtrAddr;

    @Column(name = "ADV_PHOTO")
    private String advPhoto;

    @Column(name = "ADV_PHOTO_URL")
    private String advPhotoUrl;

    @Column(name = "ADV_PHOTO_KEY")
    private String advPhotoKey;

    @Column(name = "ADV_REG_ID")
    private String advRegId;

    @Column(name = "ADV_CLASS")
    private String advClass;

    @Column(name = "ADV_REG_NUM")
    private String advRegNum;

    @Column(name = "ADV_TYPE")
    private String advType;

    @Column(name = "ADV_COM_NAME")
    private String advComName;

    @Column(name = "ADV_COM_URL")
    private String advComUrl;

    @Column(name = "ADV_SUB_AIM")
    private String advSubAim;

    @Column(name = "ADV_BLOG_TYPE")
    private String advBlogType;

    @Column(name = "ADV_MESSAGE")
    private Integer advMessage;

    @Column(name = "ADV_FULL_REG")
    private String advFullReg;

    @Column(name = "ADV_ACTIVATED")
    private Integer advActivated;

    @Column(name = "ADV_DT")
    private java.sql.Timestamp advDt;

    public Integer getAdvId() {
        return this.advId;
    }

    public void setAdvId(Integer advId) {
        this.advId = advId;
    }

    public String getAdvPass() {
        return this.advPass;
    }

    public void setAdvPass(String advPass) {
        this.advPass = advPass;
    }

    public String getAdvName() {
        return this.advName;
    }

    public void setAdvName(String advName) {
        this.advName = advName;
    }

    public String getAdvTel() {
        return this.advTel;
    }

    public void setAdvTel(String advTel) {
        this.advTel = advTel;
    }

    public String getAdvEmail() {
        return this.advEmail;
    }

    public void setAdvEmail(String advEmail) {
        this.advEmail = advEmail;
    }

    public String getAdvPostCode() {
        return this.advPostCode;
    }

    public void setAdvPostCode(String advPostCode) {
        this.advPostCode = advPostCode;
    }

    public String getAdvRoadAddr() {
        return this.advRoadAddr;
    }

    public void setAdvRoadAddr(String advRoadAddr) {
        this.advRoadAddr = advRoadAddr;
    }

    public String getAdvDetailAddr() {
        return this.advDetailAddr;
    }

    public void setAdvDetailAddr(String advDetailAddr) {
        this.advDetailAddr = advDetailAddr;
    }

    public String getAdvExtrAddr() {
        return this.advExtrAddr;
    }

    public void setAdvExtrAddr(String advExtrAddr) {
        this.advExtrAddr = advExtrAddr;
    }

    public String getAdvPhoto() {
        return this.advPhoto;
    }

    public void setAdvPhoto(String advPhoto) {
        this.advPhoto = advPhoto;
    }

    public String getAdvPhotoUrl() {
        return this.advPhotoUrl;
    }

    public void setAdvPhotoUrl(String advPhotoUrl) {
        this.advPhotoUrl = advPhotoUrl;
    }

    public String getAdvPhotoKey() {
        return this.advPhotoKey;
    }

    public void setAdvPhotoKey(String advPhotoKey) {
        this.advPhotoKey = advPhotoKey;
    }

    public String getAdvRegId() {
        return this.advRegId;
    }

    public void setAdvRegId(String advRegId) {
        this.advRegId = advRegId;
    }

    public String getAdvClass() {
        return this.advClass;
    }

    public void setAdvClass(String advClass) {
        this.advClass = advClass;
    }

    public String getAdvRegNum() {
        return this.advRegNum;
    }

    public void setAdvRegNum(String advRegNum) {
        this.advRegNum = advRegNum;
    }

    public String getAdvType() {
        return this.advType;
    }

    public void setAdvType(String advType) {
        this.advType = advType;
    }

    public String getAdvComName() {
        return this.advComName;
    }

    public void setAdvComName(String advComName) {
        this.advComName = advComName;
    }

    public String getAdvComUrl() {
        return this.advComUrl;
    }

    public void setAdvComUrl(String advComUrl) {
        this.advComUrl = advComUrl;
    }

    public String getAdvSubAim() {
        return this.advSubAim;
    }

    public void setAdvSubAim(String advSubAim) {
        this.advSubAim = advSubAim;
    }

    public String getAdvBlogType() {
        return this.advBlogType;
    }

    public void setAdvBlogType(String advBlogType) {
        this.advBlogType = advBlogType;
    }

    public Integer getAdvMessage() {
        return this.advMessage;
    }

    public void setAdvMessage(Integer advMessage) {
        this.advMessage = advMessage;
    }

    public String getAdvFullReg() {
        return this.advFullReg;
    }

    public void setAdvFullReg(String advFullReg) {
        this.advFullReg = advFullReg;
    }

    public Integer getAdvActivated() {
        return this.advActivated;
    }

    public void setAdvActivated(Integer advActivated) {
        this.advActivated = advActivated;
    }

    public java.sql.Timestamp getAdvDt() {
        return this.advDt;
    }

    public void setAdvDt(java.sql.Timestamp advDt) {
        this.advDt = advDt;
    }
}
