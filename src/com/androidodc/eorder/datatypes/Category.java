package com.androidodc.eorder.datatypes;

import java.io.Serializable;

public class Category implements Serializable {

    private static final long serialVersionUID = 3948922015270201885L;
    private int mId;
    private int mCategoryId;
    private String mName;
    private String mDescription;
    private int mSortOrder;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(int categoryId) {
        mCategoryId = categoryId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public int getSortOrder() {
        return mSortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        mSortOrder = sortOrder;
    }

    public class DishCategory {
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
}
