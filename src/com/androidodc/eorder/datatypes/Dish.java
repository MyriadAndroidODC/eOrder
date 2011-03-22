package com.androidodc.eorder.datatypes;

import java.io.Serializable;
import java.util.Date;

public class Dish implements Serializable {

    private static final long serialVersionUID = 59028309245200797L;
    private int mId;
    private int mDishId;
    private String mName;
    private int mPrice;
    private String mDescription;
    private String mImageLocal;
    private String mImageServer;
    private Date mCreateTime;
    private Date mUpdateTime;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getDishId() {
        return mDishId;
    }

    public void setDishId(int dishId) {
        mDishId = dishId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        mPrice = price;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getImageLocal() {
        return mImageLocal;
    }

    public void setImageLocal(String imageLocal) {
        mImageLocal = imageLocal;
    }

    public String getImageServer() {
        return mImageServer;
    }

    public void setImageServer(String imageServer) {
        mImageServer = imageServer;
    }

    public Date getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(Date createTime) {
        mCreateTime = createTime;
    }

    public Date getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(Date updateTime) {
        mUpdateTime = updateTime;
    }
}
