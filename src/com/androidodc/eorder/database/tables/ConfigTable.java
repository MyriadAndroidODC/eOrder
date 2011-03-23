package com.androidodc.eorder.database.tables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.androidodc.eorder.datatypes.Config;

/**
 * @author jialei.ye
 *
 */
public class ConfigTable {
    /**
     * Table name
     */
    public static final String TABLE_NAME = "dish_config";

    /**
     * The columns of this table.
     */
    public static final String _ID = "_id";
    public static final String CONFIG_ID = "config_id";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String DESCRIPTION = "description";

    /**
     * Create table.
     * @param db writable database
     */
    public static void create(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CONFIG_ID + " INTEGER NOT NULL, " +
                NAME + " TEXT, " +
                VALUE + " TEXT, " +
                DESCRIPTION + " TEXT" +
                ");");
    }

    public static long add(final SQLiteDatabase db, final Config cfg) {
        final ContentValues cv = new ContentValues();
        cv.put(CONFIG_ID, cfg.getConfigId());
        cv.put(NAME, cfg.getName());
        cv.put(VALUE, cfg.getValue());
        cv.put(DESCRIPTION, cfg.getDescription());
        return db.insert(TABLE_NAME, _ID, cv);
    }

    /**
     * Drops the entire table, any data in it will be erased.
     */
    public static void drop(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    /**
     * Get all dish-order system client configs.
     * @param db
     * @return
     */
    public static List<Config> getAllConfigs(final SQLiteDatabase db) {
        final Cursor c = db.rawQuery("SELECT  * FROM " + TABLE_NAME, null);

        if (c != null) {
            List<Config> list = new ArrayList<Config>();
            try {
                if (c.moveToFirst()) {
                    final Config cfg = new Config();
                    cfg.setId(c.getInt(c.getColumnIndex(_ID)));
                    cfg.setConfigId(c.getInt(c.getColumnIndex(CONFIG_ID)));
                    cfg.setName(c.getString(c.getColumnIndex(NAME)));
                    cfg.setValue(c.getString(c.getColumnIndex(VALUE)));
                    cfg.setDescription(c.getString(c.getColumnIndex(DESCRIPTION)));
                    list.add(cfg);
                }
            } finally {
                c.close();
            }
            return list;
        }
        return Collections.emptyList();
    }

    /**
     * Get special config's value
     * @param db
     * @param name
     * @return
     */
    public static String getVaue(final SQLiteDatabase db, String name){
        final Cursor c = db.rawQuery("SELECT " + VALUE + " FROM " + TABLE_NAME + " WHERE " + NAME
                + " =? ", new String[] { name });

        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getString(c.getColumnIndex(VALUE));
                }
            } finally {
                c.close();
            }
        }
        return null;
    }
}
