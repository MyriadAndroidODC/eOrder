package com.androidodc.eorder.datatypes;

import java.io.Serializable;

public class Category implements Serializable {

    private static final long serialVersionUID = 3948922015270201885L;
    private long mId;
    private long mCategoryId;
    private String mName;
    private String mDescription;
    private int mSortOrder;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(long categoryId) {
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
}
