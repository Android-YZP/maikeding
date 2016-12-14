package com.mcwonders.mkd.demand.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by ZJ on 2016/12/2.
 * 需求付款信息
 */
@Table(name = "demand_pay_info")
public class DemandPayInfo {
    @Column(name = "id", isId = true, autoGen = true)
    private int id;
    @Column(name = "demand_id")
    private int demandID;//需求ID
    @Column(name = "pay_name")
    private String payName;//支付阶段名称
    @Column(name = "pay_type")
    private int payType;//支付阶段类型
    @Column(name = "is_deliver")
    private boolean isDeliver;//有无交付物
    @Column(name = "pay_scale")
    private int payScale;//付款比例
    @Column(name = "pay_money")
    private int payMoney;//付款金额

    public DemandPayInfo() {
    }

    public DemandPayInfo(int demandID, String payName, int payType, boolean isDeliver, int payScale, int payMoney) {
        this.demandID = demandID;
        this.payName = payName;
        this.payType = payType;
        this.isDeliver = isDeliver;
        this.payScale = payScale;
        this.payMoney = payMoney;
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

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public boolean isDeliver() {
        return isDeliver;
    }

    public void setDeliver(boolean deliver) {
        isDeliver = deliver;
    }

    public int getPayScale() {
        return payScale;
    }

    public void setPayScale(int payScale) {
        this.payScale = payScale;
    }

    public int getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(int payMoney) {
        this.payMoney = payMoney;
    }

    @Override
    public String toString() {
        return "DemandPayInfo{" +
                "id=" + id +
                ", demandID=" + demandID +
                ", payName='" + payName + '\'' +
                ", payType=" + payType +
                ", isDeliver=" + isDeliver +
                ", payScale=" + payScale +
                ", payMoney=" + payMoney +
                '}';
    }
}