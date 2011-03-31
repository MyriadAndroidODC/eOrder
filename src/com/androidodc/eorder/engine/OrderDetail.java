package com.androidodc.eorder.engine;

public class OrderDetail {
    private long mOrderItemId;
    private long mOrderId;
    private long mTableId;
    private long mDishId;
    private int mNumber;

    public long getOrderItemId() {
        return mOrderItemId;
    }

    public void setOrderItemId(long orderItemId) {
        mOrderItemId = orderItemId;
    }

    public long getOrderId() {
        return mOrderId;
    }

    public void setOrderId(long orderId) {
        mOrderId = orderId;
    }

    public long getTableId() {
        return mTableId;
    }

    public void setTableId(long tableId) {
        mTableId = tableId;
    }

    public long getDishId() {
        return mDishId;
    }

    public void setDishId(long dishId) {
        mDishId = dishId;
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        mNumber = number;
    }
}
