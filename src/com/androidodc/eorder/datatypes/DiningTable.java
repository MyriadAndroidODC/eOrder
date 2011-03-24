package com.androidodc.eorder.datatypes;

import java.io.Serializable;

public class DiningTable implements Serializable {

    private static final long serialVersionUID = 4477564220523201176L;
    private long mId;
    private long mDiningTableId;
    private String mName;
    private int mMaxPeople;
    private boolean mIsFree;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getDiningTableId() {
        return mDiningTableId;
    }

    public void setDiningTableId(long diningTableId) {
        mDiningTableId = diningTableId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getMaxPeople() {
        return mMaxPeople;
    }

    public void setMaxPeople(int maxPeople) {
        mMaxPeople = maxPeople;
    }

    public boolean isFree() {
        return mIsFree;
    }

    public void setFree(boolean isFree) {
        mIsFree = isFree;
    }
}
