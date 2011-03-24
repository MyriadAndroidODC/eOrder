package com.androidodc.eorder.datatypes;

import java.io.Serializable;

public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1501415269530963052L;
    private Dish mDish;
    private int mAmount;

    public Dish getDish() {
        return mDish;
    }

    public void setDish(Dish dish) {
        this.mDish = dish;
    }

    public int getAmount() {
        return mAmount;
    }

    public void setAmount(int amount) {
        this.mAmount = amount;
    }
}
