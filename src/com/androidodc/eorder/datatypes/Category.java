package com.androidodc.eorder.datatypes;

public class Category {

    private int mId;
    private int mSortOrder;
    private int mCategoryId;
    private String mName;
    private String mDescription;

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
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
}
