package com.mcwonders.uikit.demand.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by ZJ on 2016/12/2.
 * 需求详情
 */
@Table(name = "demand")
public class Demand {
    @Column(name = "id", isId = true, autoGen = true)
    private int id;
    @Column(name = "demand_id")
    private int demandID;//需求ID
    @Column(name = "demand_name")
    private String demandName;//需求名称
    @Column(name = "demand_price")
    private String demandPrice;//需求价格
    @Column(name = "is_person")
    private boolean isVerifyPersonOrCompany;//实名认证
    @Column(name = "is_mkj")
    private boolean isVerifyMKJ;//麦客加认证
    @Column(name = "is_lybz")
    private boolean isVerifyLYBZ;//履约保证
    @Column(name = "is_sdcy")
    private boolean isVerifySDCY;//实地查验
    @Column(name = "description")
    private String description;//需求描述
    @Column(name = "step")
    private int step;//需求进度
    @Column(name = "release_time")
    private String releaseTime;//发布时间

    public Demand() {
    }

    public Demand(int demandID, String demandName, String demandPrice, boolean isVerifyPersonOrCompany, boolean isVerifyMKJ, boolean isVerifyLYBZ, boolean isVerifySDCY, String description, int step, String releaseTime) {
        this.demandID = demandID;
        this.demandName = demandName;
        this.demandPrice = demandPrice;
        this.isVerifyPersonOrCompany = isVerifyPersonOrCompany;
        this.isVerifyMKJ = isVerifyMKJ;
        this.isVerifyLYBZ = isVerifyLYBZ;
        this.isVerifySDCY = isVerifySDCY;
        this.description = description;
        this.step = step;
        this.releaseTime = releaseTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDemandID() {
        return demandID;
    }

    public void setDemandID(int demandID) {
        this.demandID = demandID;
    }

    public String getDemandName() {
        return demandName;
    }

    public void setDemandName(String demandName) {
        this.demandName = demandName;
    }

    public String getDemandPrice() {
        return demandPrice;
    }

    public void setDemandPrice(String demandPrice) {
        this.demandPrice = demandPrice;
    }

    public boolean isVerifyPersonOrCompany() {
        return isVerifyPersonOrCompany;
    }

    public void setVerifyPersonOrCompany(boolean verifyPersonOrCompany) {
        isVerifyPersonOrCompany = verifyPersonOrCompany;
    }

    public boolean isVerifyMKJ() {
        return isVerifyMKJ;
    }

    public void setVerifyMKJ(boolean verifyMKJ) {
        isVerifyMKJ = verifyMKJ;
    }

    public boolean isVerifyLYBZ() {
        return isVerifyLYBZ;
    }

    public void setVerifyLYBZ(boolean verifyLYBZ) {
        isVerifyLYBZ = verifyLYBZ;
    }

    public boolean isVerifySDCY() {
        return isVerifySDCY;
    }

    public void setVerifySDCY(boolean verifySDCY) {
        isVerifySDCY = verifySDCY;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    @Override
    public String toString() {
        return "Demand{" +
                "id=" + id +
                ", demandID='" + demandID + '\'' +
                ", demandName='" + demandName + '\'' +
                ", demandPrice='" + demandPrice + '\'' +
                ", isVerifyPersonOrCompany=" + isVerifyPersonOrCompany +
                ", isVerifyMKJ=" + isVerifyMKJ +
                ", isVerifyLYBZ=" + isVerifyLYBZ +
                ", isVerifySDCY=" + isVerifySDCY +
                ", description='" + description + '\'' +
                ", step=" + step +
                ", releaseTime='" + releaseTime + '\'' +
                '}';
    }
}