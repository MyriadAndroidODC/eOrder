package com.androidodc.eorder.datatypes;

public class Config {

    private int mId;
    private int mConfigId;
    private String mName;
    private String mValue;
    private String mDescription;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public int getConfigId() {
        return mConfigId;
    }

    public void setConfigId(int configId) {
        mConfigId = configId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }
}
