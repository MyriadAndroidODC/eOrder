package com.androidodc.eorder.engine;

public class OrderDetail {
    private long mOrderItemId;
    private long orderId;
    private long tableId;
    private long dishId;
    private int number;
    
    public long getOrderItemId() {
        return mOrderItemId;
    }
    public void setOrderItemId(long orderItemId) {
        this.mOrderItemId = orderItemId;
    }
    public long getOrderId() {
        return orderId;
    }
    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
    public long getTableId() {
        return tableId;
    }
    public void setTableId(long tableId) {
        this.tableId = tableId;
    }
    public long getDishId() {
        return dishId;
    }
    public void setDishId(long dishId) {
        this.dishId = dishId;
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }    
}
