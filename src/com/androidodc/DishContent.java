package com.androidodc;

import android.net.Uri;

public class DishContent {
    public static final String AUTHORITY = "xxx";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    // All classes share this
    public static final String RECORD_ID = "_id";

    private DishContent() {
        // TODO Auto-generated constructor stub
    }

    public interface DishColumns {
        public static final String ID = "_id";
        public static final String DISH_NAME = "dishname";
        public static final String DISH_PRICE = "dishprice";
        public static final String DISH_DESCRIPTION = "dishdescription";
        public static final String DISH_IMAGE = "dishimage";
    }
}
