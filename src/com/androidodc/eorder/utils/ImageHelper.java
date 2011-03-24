package com.androidodc.eorder.utils;

import android.graphics.Bitmap;

public class ImageHelper {

    public static Bitmap getImage(String imageLocation) {
        return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_4444);
    }
}
