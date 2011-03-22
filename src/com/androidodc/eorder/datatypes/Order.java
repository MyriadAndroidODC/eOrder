package com.androidodc.eorder.datatypes;

import java.util.Date;

public class Order {

    private int mId;
    private int mOrderId;
    private int mStatus;
    private double mOrderTotal;
    private Date mPaidOn;
    private Date mCreatedOn;

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

    public double getOrderTotal() {
        return mOrderTotal;
    }

    public void setOrderTotal(double orderTotal) {
        mOrderTotal = orderTotal;
    }

    public Date getPaidOn() {
        return mPaidOn;
    }

    public void setPaidOn(Date paidOn) {
        mPaidOn = paidOn;
    }

    public Date getCreatedOn() {
        return mCreatedOn;
    }

    public void setCreatedOn(Date createdOn) {
        mCreatedOn = createdOn;
    }
}
