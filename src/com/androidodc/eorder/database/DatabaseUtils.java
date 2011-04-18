package com.androidodc.eorder.database;

import android.database.sqlite.SQLiteDatabase;

public class DatabaseUtils {
    public static final long NO_DATA_ID                    = -1;

    public static void drop(final SQLiteDatabase db, String tableName) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }
}
