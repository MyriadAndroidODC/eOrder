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
        Bitmap _image;
        if (mEnableCache) {
            _image = getImageFromCache(imageLocation);
        } else {
            _image = BitmapFactory.decodeFile(imageLocation);
        }
        return _image;
    }

    private static Bitmap getImageFromCache(String imageLocation) {
        Bitmap _image = mCache.get(imageLocation);
        if (_image == null) {
            _image = BitmapFactory.decodeFile(imageLocation);
            if (_image != null) {
                mCache.put(imageLocation, _image);
            }
        }
        return _image;
    }

    public static void enableCache() {
        mEnableCache = true;
    }

    public static void disableCache() {
        mEnableCache = false;
    }
}
