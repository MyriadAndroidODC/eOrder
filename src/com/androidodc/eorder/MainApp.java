package com.androidodc.eorder;

import android.app.Application;
import android.os.StrictMode;

import com.androidodc.eorder.utils.LogUtils;

public class MainApp extends Application {

    public void onCreate() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .penaltyLog().build());

        super.onCreate();
    }
}
