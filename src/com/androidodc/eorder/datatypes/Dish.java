package com.androidodc.eorder.datatypes;

import java.util.Date;

public class Dish {

    private int mId;
    private int mDishId;
    private String mName;
    private double mPrice;
    private String mDescription;
    private String mImageLocal;
    private String mImageServer;
    private Date mCreatedOn;
    private Date mUpdatedOn;

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

    public Double getPrice() {
        return mPrice;
    }

    public void setPrice(Double price) {
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

    public Date getCreatedOn() {
        return mCreatedOn;
    }

    public void setCreatedOn(Date createdOn) {
        mCreatedOn = createdOn;
    }

    public Date getUpdatedOn() {
        return mUpdatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        mUpdatedOn = updatedOn;
    }
}
