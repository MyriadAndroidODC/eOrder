package com.androidodc.eorder.database.tables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.androidodc.eorder.database.DatabaseUtils;
import com.androidodc.eorder.datatypes.Dish;

public class DishTable {
    /**
     * Table name
     */
    public static final String TABLE_NAME = "dish";

    /**
     * The columns of this table.
     */
    public static final String _ID                  = "_id";
    public static final String DISH_ID              = "dish_id";
    public static final String NAME                 = "name";
    public static final String PRICE                = "price";
    public static final String DESCRIPTION          = "description";
    public static final String IMAGE_LOCAL          = "image_local";
    public static final String IMAGE_SERVER         = "image_server";
    public static final String CREATE_TIME          = "create_time";
    public static final String UPDATE_TIME          = "update_time";

    /**
     * The foreign key used by other tables.
     */
    public static final String FOREIGN_KEY = TABLE_NAME + "(" + _ID + ")";

    /**
     * Create table.
     * @param db writable database
     */
    public static void create(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DISH_ID + " INTEGER, " +
                NAME + " TEXT, " +
                PRICE + " INTEGER, " +
                DESCRIPTION + " TEXT, " +
                IMAGE_LOCAL + " TEXT, " +
                IMAGE_SERVER + " TEXT, " +
                CREATE_TIME + " NUMERIC, " +
                UPDATE_TIME + " NUMERIC " +
                ");");
    }

    /**
     * Add a new dish to DishTable.
     *
     * @param db the writable db
     * @param dish the dish to add
     * @return the row id of the new added dish. Or -1 if an error occurred.
     */
    public static long add(final SQLiteDatabase db, final Dish dish) {
        final ContentValues cv = new ContentValues();
        cv.put(DISH_ID, dish.getDishId());
        cv.put(NAME, dish.getName());
        cv.put(PRICE, dish.getPrice());
        cv.put(DESCRIPTION, dish.getDescription());
        cv.put(IMAGE_LOCAL, dish.getImageLocal());
        cv.put(IMAGE_SERVER, dish.getImageServer());
        if (dish.getCreateTime() != null) {
            cv.put(CREATE_TIME, dish.getCreateTime().getTime());
        }
        if (dish.getUpdateTime() != null) {
            cv.put(UPDATE_TIME, dish.getUpdateTime().getTime());
        }
        return db.insert(TABLE_NAME, _ID, cv);
    }

    /**
     * Get dish by dishId.
     *
     * @param db the readable database.
     * @param dishId the dishId from server
     * @return the Dish object of this dish.
     */
    public static Dish getDish(final SQLiteDatabase db, final long dishId) {
        final Cursor c = db.rawQuery("SELECT  * FROM " + TABLE_NAME + " WHERE " + DISH_ID + " =? ",
                new String[] { String.valueOf(dishId) });

        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    final Dish dish = new Dish();
                    dish.setId(c.getLong(c.getColumnIndex(_ID)));
                    dish.setDishId(c.getLong(c.getColumnIndex(DISH_ID)));
                    dish.setName(c.getString(c.getColumnIndex(NAME)));
                    dish.setPrice(c.getInt(c.getColumnIndex(PRICE)));
                    dish.setDescription(c.getString(c.getColumnIndex(DESCRIPTION)));
                    dish.setImageLocal(c.getString(c.getColumnIndex(IMAGE_LOCAL)));
                    dish.setImageServer(c.getString(c.getColumnIndex(IMAGE_SERVER)));
                    dish.setCreateTime(new Date(c.getLong(c.getColumnIndex(CREATE_TIME))));
                    dish.setUpdateTime(new Date(c.getLong(c.getColumnIndex(UPDATE_TIME))));
                    return dish;
                }
            } finally {
                c.close();
            }
        }
        return null;
    }

    /**
     * Get all dishes
     *
     * @param db
     * @return
     */
    public static List<Dish> getAllDishs(final SQLiteDatabase db) {
        final Cursor c = db.rawQuery("SELECT  * FROM " + TABLE_NAME, null);

        if (c != null) {
            List<Dish> list = new ArrayList<Dish>();
            try {
                while(c.moveToNext()) {
                    final Dish dish = new Dish();
                    dish.setId(c.getLong(c.getColumnIndex(_ID)));
                    dish.setDishId(c.getLong(c.getColumnIndex(DISH_ID)));
                    dish.setName(c.getString(c.getColumnIndex(NAME)));
                    dish.setPrice(c.getInt(c.getColumnIndex(PRICE)));
                    dish.setDescription(c.getString(c.getColumnIndex(DESCRIPTION)));
                    dish.setImageLocal(c.getString(c.getColumnIndex(IMAGE_LOCAL)));
                    dish.setImageServer(c.getString(c.getColumnIndex(IMAGE_SERVER)));
                    dish.setCreateTime(new Date(c.getLong(c.getColumnIndex(CREATE_TIME))));
                    dish.setUpdateTime(new Date(c.getLong(c.getColumnIndex(UPDATE_TIME))));
                    list.add(dish);
                }
            } finally {
                c.close();
            }
            return list;
        }
        return Collections.emptyList();
    }

    /**
     * Drops the entire table, any data in it will be erased.
     *
     * @param db
     */
    public static void drop(final SQLiteDatabase db) {
        DatabaseUtils.drop(db, TABLE_NAME);
    }

    /**
     * According to dishId to modify the corresponding Dish object's imageLocal property.
     *
     *@param db
     * @param dishId
     * @param imageLocal
     */
    public static void updateDishImageLocalByDishId(final SQLiteDatabase db, final long dishId,
            String imageLocal) {
        db.execSQL("UPDATE " + DishTable.TABLE_NAME + " SET " + DishTable.IMAGE_LOCAL + "="
                + imageLocal + " WHERE " + DishTable.DISH_ID + " = " + dishId);
    }

    /**
     * Remove all data in table dish
     *
     * @param db
     */
    public static void deleteAll(final SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }
}
