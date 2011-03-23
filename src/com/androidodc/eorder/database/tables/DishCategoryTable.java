package com.androidodc.eorder.database.tables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.androidodc.eorder.datatypes.Dish;
import com.androidodc.eorder.datatypes.DishCategory;

public class DishCategoryTable {
    /**
     * Table name
     */
    public static final String TABLE_NAME = "dish_category";

    /**
     * The columns of this table.
     */
    public static final String _ID                       ="_id";
    public static final String DISH_ID                  = "dish_id";
    public static final String CATEGORY_ID              = "category_id";

    /**
     * Create table.
     * @param db writable database
     */
    public static void create(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DISH_ID + " INTEGER, "  +
                CATEGORY_ID + " INTEGER" +
                ");");
    }

    /**
     * Add a new dish to DishTable.
     *
     * @param db the writable db
     * @param dishCategory the DishCategory to add
     * @return the row id of the new added DishCategory. Or -1 if an error occurred.
     */
    public static long add(final SQLiteDatabase db, final DishCategory dishCategory) {
        final ContentValues cv = new ContentValues();
        cv.put(DISH_ID, dishCategory.getDishId());
        cv.put(CATEGORY_ID, dishCategory.getCategoryId());
        return db.insert(TABLE_NAME, DISH_ID, cv);
    }

    /**
     * Get dishes by categoryId.
     *
     * @param db the readable database.
     * @param categoryId the categoryId
     * @return Dish objects of specified category.
     */
    public static List<Dish> getDishsByCategory(final SQLiteDatabase db, final int categoryId) {
        final Cursor c = db.rawQuery("SELECT d.* FROM " + DishTable.TABLE_NAME + " d, "
                + TABLE_NAME + " dc WHERE dc." + CATEGORY_ID + " =? AND d."+DishTable._ID + " = dc." + DISH_ID
                + " ORDER BY d." + DishTable._ID, new String[] { String.valueOf(categoryId) });
        if (c != null) {
            List<Dish> list = new ArrayList<Dish>();
            try {
                while(c.moveToNext()) {
                    Dish dish = new Dish();
                    dish.setId(c.getInt(c.getColumnIndex(DishTable._ID)));
                    dish.setDishId(c.getInt(c.getColumnIndex(DishTable.DISH_ID)));
                    dish.setName(c.getString(c.getColumnIndex(DishTable.NAME)));
                    dish.setPrice(c.getInt(c.getColumnIndex(DishTable.PRICE)));
                    dish.setDescription(c.getString(c.getColumnIndex(DishTable.DESCRIPTION)));
                    dish.setImageLocal(c.getString(c.getColumnIndex(DishTable.IMAGE_LOCAL)));
                    dish.setImageServer(c.getString(c.getColumnIndex(DishTable.IMAGE_SERVER)));
                    dish.setCreateTime(new Date(c.getLong(c.getColumnIndex(DishTable.CREATE_TIME))));
                    dish.setUpdateTime(new Date(c.getLong(c.getColumnIndex(DishTable.UPDATE_TIME))));
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
     */
    public static void drop(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
}
