package com.androidodc.eorder;

import com.androidodc.eorder.database.DatabaseHelper;
import com.androidodc.eorder.utils.LogUtils;

import android.app.Application;
import android.os.StrictMode;

public class MainApp extends Application {

    public static boolean DEBUG = true;

    public void onCreate() {

        if (DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog().build());
        }
        
        super.onCreate();
        
        try{
            DatabaseHelper.init(this.getApplicationContext());
        } catch (Exception e) {
            LogUtils.logD("Database initialize error! \n" + e.getMessage());
        }
    }
}
