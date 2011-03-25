package com.androidodc.eorder.utils;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * The cache to store images.
 */
public class ImageCache {

    private static final ImageCache sInstance = new ImageCache();

    /**
     * Returns the unique instance of this class.
     */
    synchronized public static ImageCache getInstance() {
        return sInstance;
    }

    private HashMap<String, SoftReference<Bitmap>> mCache =
        new HashMap<String, SoftReference<Bitmap>>();

    private ImageCache() {
    }

    /**
     * Try to gets a cached image, return null if it's not cached yet.
     */
    synchronized public Bitmap get(String key) {
        SoftReference<Bitmap> midValue = mCache.get(key);
        if (midValue != null) {
            return midValue.get();
        }
        return null;
    }

    /**
     * Caches a image with specified key.
     */
    synchronized public void put(String key, Bitmap value) {
        SoftReference<Bitmap> midValue = new SoftReference<Bitmap>(value);
        mCache.put(key, midValue);
    }
}
