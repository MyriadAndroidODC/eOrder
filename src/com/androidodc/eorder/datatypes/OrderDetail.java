package com.androidodc.eorder.datatypes;

public class OrderDetail {

    private int mId;
    private int mOrderDetailId;
    private int mOrderId;
    private int mDishId;
    private int mTableId;
    private int mNumber;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getOrderDetailId() {
        return mOrderDetailId;
    }

    public void setOrderDetailId(int orderDetailId) {
        mOrderDetailId = orderDetailId;
    }

    public int getOrderId() {
        return mOrderId;
    }

    public void setOrderId(int orderId) {
        mOrderId = orderId;
    }

    public int getDishId() {
        return mDishId;
    }

    public void setDishId(int dishId) {
        mDishId = dishId;
    }

    public int getTableId() {
        return mTableId;
    }

    public void setTableId(int tableId) {
        mTableId = tableId;
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        mNumber = number;
    }
}
