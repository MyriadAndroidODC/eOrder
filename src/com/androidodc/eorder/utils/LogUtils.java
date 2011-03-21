/*********************************************************************
 *  ____                      _____      _                           *
 * / ___|  ___  _ __  _   _  | ____|_ __(_) ___ ___ ___  ___  _ __   *
 * \___ \ / _ \| '_ \| | | | |  _| | '__| |/ __/ __/ __|/ _ \| '_ \  *
 *  ___) | (_) | | | | |_| | | |___| |  | | (__\__ \__ \ (_) | | | | *
 * |____/ \___/|_| |_|\__, | |_____|_|  |_|\___|___/___/\___/|_| |_| *
 *                    |___/                                          *
 *                                                                   *
 *********************************************************************
 * Copyright 2010 Sony Ericsson Mobile Communications AB.            *
 * All rights, including trade secret rights, reserved.              *
 *********************************************************************/

package com.androidodc.eorder.utils;

import android.util.Log;

public class LogUtils {
    private static final String TAG = "fb_media_update";

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
