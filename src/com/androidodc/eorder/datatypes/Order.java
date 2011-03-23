package com.androidodc.eorder.datatypes;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {

    private static final long serialVersionUID = 542383610552166140L;
    private int mId;
    private int mOrderId;
    private int mStatus;
    private int mOrderTotal;
    private Date mPayTime;
    private Date mCreateTime;
    private List<Dish> dishes;

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
    
    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }
}
