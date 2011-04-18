package com.androidodc.eorder.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.androidodc.eorder.database.DatabaseUtils;
import com.androidodc.eorder.datatypes.Dish;
import com.androidodc.eorder.datatypes.DishCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DishCategoryTable {
    /**
     * Table name
     */
    public static final String TABLE_NAME = "dish_category";

    /**
     * The columns of this table.
     */
    private static final String _ID                      ="_id";
    private static final String DISH_ID                  = "dish_id";
    private static final String CATEGORY_ID              = "category_id";

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
     * Drops the entire table, any data in it will be erased.
     */
    public static void drop(final SQLiteDatabase db) {
        DatabaseUtils.drop(db, TABLE_NAME);
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
    public static List<Dish> getDishsByCategory(final SQLiteDatabase db, final long categoryId) {
        final Cursor c = db.rawQuery("SELECT d.* FROM " + DishTable.TABLE_NAME + " d, "
                + TABLE_NAME + " dc WHERE dc." + CATEGORY_ID + " =? AND d."+DishTable.DISH_ID + " = dc." + DISH_ID
                + " ORDER BY d." + DishTable.DISH_ID, new String[] { String.valueOf(categoryId) });
        if (c != null) {
            List<Dish> list = new ArrayList<Dish>();
            try {
                while(c.moveToNext()) {
                    Dish dish = new Dish();
                    dish.setId(c.getLong(c.getColumnIndex(DishTable._ID)));
                    dish.setDishId(c.getLong(c.getColumnIndex(DishTable.DISH_ID)));
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
     * Get category id of the current dish category.
     * @param db
     * @param dishId
     * @return categoryId, or -1 if not found relational category
     */
    public static long getDishCategoryId(final SQLiteDatabase db, final long dishId) {
        List<Long> list = getDishCategoryIds(db, dishId);
        //remove first category if current dish exist more than 1 category
        if(list.size()>0){
            return list.get(list.size()-1);
        }
        return DatabaseUtils.NO_DATA_ID;
    }

    /**
     * Get sequenced dish_ids list
     * This list was ordered by Table Category' sort_order and category_id ASC, Table DishCategory dish_id ASC
     * @param db
     * @return
     */
    public static List<Long> getSequencedDishIds(final SQLiteDatabase db) {
        long b = System.currentTimeMillis();
        final Cursor c = db.rawQuery("SELECT dc." + DISH_ID + " FROM " + TABLE_NAME + " dc, "
                + CategoryTable.TABLE_NAME + " c WHERE dc." + CATEGORY_ID + " = c."
                + CategoryTable.CATEGORY_ID + " ORDER BY c." + CategoryTable.SORT_ORDER + ", c."
                + CategoryTable.CATEGORY_ID + ", " + DISH_ID, null);
        if (c != null) {
            try {
                List<Long> list = new ArrayList<Long>();
                while (c.moveToNext()) {
                    list.add(c.getLong(c.getColumnIndex(DISH_ID)));
                }
                System.out.println("getSequencedDishIds " + (System.currentTimeMillis()-b));
                return list;
            } finally {
                c.close();
            }
        }
        return Collections.emptyList();
    }

    /**
     * Get all category ids of the current dish's categories.
     * @param db
     * @param dishId
     * @return List<categoryId>, or empty List<Long>
     */
    public static List<Long> getDishCategoryIds(final SQLiteDatabase db, final long dishId) {
        final Cursor c = db.rawQuery("SELECT " + CATEGORY_ID + " FROM " + TABLE_NAME + " WHERE "
                + DISH_ID + " =?", new String[] { String.valueOf(dishId) });
        if (c != null) {
            List<Long> list = new ArrayList<Long>();
            try {
                while (c.moveToNext()) {
                    list.add(c.getLong(c.getColumnIndex(CATEGORY_ID)));
                }
            } finally {
                c.close();
            }
            return list;
        }
        return Collections.emptyList();
    }

    /**
     * Remove all data in table dish_category
     *
     * @param db
     */
    public static void deleteAll(final SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    public static Map<Long, List<Dish>> getCategoryAndDishes(SQLiteDatabase db) {
        final Cursor c = db.rawQuery("SELECT dc." + CATEGORY_ID + ", d.* FROM " + DishTable.TABLE_NAME + " d, "
                + TABLE_NAME + " dc WHERE d."+DishTable.DISH_ID + " = dc." + DISH_ID
                + " ORDER BY d." + DishTable.DISH_ID, null);
        if (c != null) {
            Map<Long, List<Dish>> map = new HashMap<Long,List<Dish>>();
            try {
                while(c.moveToNext()) {
                    Long categoryId = c.getLong(c.getColumnIndex(CATEGORY_ID));
                    List<Dish> list = map.get(categoryId);
                    if(list == null){
                        list = new ArrayList<Dish>();
                    }
                    Dish dish = new Dish();
                    dish.setId(c.getLong(c.getColumnIndex(DishTable._ID)));
                    dish.setDishId(c.getLong(c.getColumnIndex(DishTable.DISH_ID)));
                    dish.setName(c.getString(c.getColumnIndex(DishTable.NAME)));
                    dish.setPrice(c.getInt(c.getColumnIndex(DishTable.PRICE)));
                    dish.setDescription(c.getString(c.getColumnIndex(DishTable.DESCRIPTION)));
                    dish.setImageLocal(c.getString(c.getColumnIndex(DishTable.IMAGE_LOCAL)));
                    dish.setImageServer(c.getString(c.getColumnIndex(DishTable.IMAGE_SERVER)));
                    dish.setCreateTime(new Date(c.getLong(c.getColumnIndex(DishTable.CREATE_TIME))));
                    dish.setUpdateTime(new Date(c.getLong(c.getColumnIndex(DishTable.UPDATE_TIME))));
                    list.add(dish);
                    map.put(categoryId, list);
                }
            } finally {
                c.close();
            }
            return map;
        }
        return Collections.emptyMap();
    }
}
