package com.androidodc.eorder.datatypes;

public class DishCategory {
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
