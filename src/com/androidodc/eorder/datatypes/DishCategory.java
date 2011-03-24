package com.androidodc.eorder.datatypes;

import java.io.Serializable;

public class DishCategory implements Serializable{
    private static final long serialVersionUID = -9016781387510962160L;
    private long mCategoryId;
    private long mDishId;

    public long getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(long categoryId) {
        mCategoryId = categoryId;
    }

    public long getDishId() {
        return mDishId;
    }

    public void setDishId(long dishId) {
        mDishId = dishId;
    }
}
