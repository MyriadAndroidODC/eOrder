package com.androidodc.eorder.datatypes;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {

    private static final long serialVersionUID = 542383610552166140L;
    private long mId;
    private long mOrderId;
    private int mStatus;
    private int mOrderTotal;
    private Date mPayTime;
    private Date mCreateTime;
    private long mTableId;
    private List<OrderItem> mOrderItems;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getOrderId() {
        return mOrderId;
    }

    public void setOrderId(long orderId) {
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

    public long getTableId() {
        return mTableId;
    }

    public void setTableId(long tableId) {
        mTableId = tableId;
    }

    public List<OrderItem> getOrderItems() {
        return mOrderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        mOrderItems = orderItems;
    }
}
