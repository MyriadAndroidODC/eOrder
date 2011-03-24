package com.androidodc.eorder.utils;

import android.util.Log;

public class LogUtils {
    private static final String TAG = "e_order";

    public static void logI(String msg) {
        if (Log.isLoggable(TAG, Log.INFO)) {
            Log.i(TAG, msg);
        }
    }

    public static void logD(String msg) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, msg);
        }
    }

    public static void logE(String msg) {
        if (Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, msg);
        }
    }

    public static void logE(String msg, Throwable e) {
        if (Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, msg, e);
        }
    }
}
