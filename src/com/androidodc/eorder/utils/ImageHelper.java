package com.androidodc.eorder.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/*
 * Help to get images (in Bitmap), and manage image cache.
 * FIX ME: load image files in other threads.
 */

public class ImageHelper {

    private static ImageCache mCache = ImageCache.getInstance();
    private static boolean mEnableCache = true;

    public static Bitmap getImage(String imageLocation) {
        Bitmap image = null;
        if (mEnableCache) {
            image = getImageFromCache(imageLocation);
        } else {
            image = BitmapFactory.decodeFile(imageLocation);
        }
        return image;
    }

    private static Bitmap getImageFromCache(String imageLocation) {
        Bitmap image = mCache.get(imageLocation);
        if (image == null) {
            image = BitmapFactory.decodeFile(imageLocation);
            if (image != null) {
                mCache.put(imageLocation, image);
            }
        }
        return image;
    }

    public static void enableCache() {
        mEnableCache = true;
    }

    public static void disableCache() {
        mEnableCache = false;
    }
}
