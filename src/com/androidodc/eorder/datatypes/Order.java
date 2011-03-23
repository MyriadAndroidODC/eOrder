package com.androidodc.eorder.datatypes;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class Order implements Serializable {

    private static final long serialVersionUID = 542383610552166140L;
    private int mId;
    private int mOrderId;
    private int mStatus;
    private int mOrderTotal;
    private Date mPayTime;
    private Date mCreateTime;
    private int mTableId;
    private Map<Dish, Integer> mDishes;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getOrderId() {
        return mOrderId;
    }

    public void setOrderId(int orderId) {
        mOrderId = orderId;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public int getOrderTotal() {
        return mOrderTotal;
    }

    public void setOrderTotal(int orderTotal) {
        mOrderTotal = orderTotal;
    }

    public Date getPayTime() {
        return mPayTime;
    }

    public void setPayTime(Date payTime) {
        mPayTime = payTime;
    }

    public Date getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(Date createdOn) {
        mCreateTime = createdOn;
    }

    public int getmTableId() {
        return mTableId;
    }

    public void setmTableId(int mTableId) {
        this.mTableId = mTableId;
    }

    public Map<Dish, Integer> getmDishes() {
        return mDishes;
    }

    public void setmDishes(Map<Dish, Integer> mDishes) {
        this.mDishes = mDishes;
    }
}
