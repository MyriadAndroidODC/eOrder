package com.androidodc.eorder.database;

import android.database.sqlite.SQLiteDatabase;

public class DatabaseUtils {

    public static void drop(final SQLiteDatabase db, String tableName) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }
}
