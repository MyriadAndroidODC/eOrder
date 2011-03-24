package com.androidodc.eorder.datatypes;

public class Config {

    private long mId;
    private long mConfigId;
    private String mName;
    private String mValue;
    private String mDescription;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public long getConfigId() {
        return mConfigId;
    }

    public void setConfigId(long configId) {
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
