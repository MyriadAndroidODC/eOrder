package com.androidodc.eorder.datatypes;

import java.io.Serializable;

public class DishCategory  implements Serializable{
    private static final long serialVersionUID = -9016781387510962160L;
    private int mCategoryId;
    private int mDishId;

    public int getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(int categoryId) {
        mCategoryId = categoryId;
    }

    public int getDishId() {
        return mDishId;
    }

    public void setDishId(int dishId) {
        mDishId = dishId;
    }
}