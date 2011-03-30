package com.androidodc.eorder.database.tables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.androidodc.eorder.database.DatabaseUtils;
import com.androidodc.eorder.datatypes.Category;

public class CategoryTable {
    /**
     * Table name
     */
    public static final String TABLE_NAME = "category";

    /**
     * The columns of this table.
     */
    public static final String _ID                  = "_id";
    public static final String CATEGORY_ID          = "category_id";
    public static final String NAME                 = "name";
    public static final String DESCRIPTION          = "description";
    public static final String SORT_ORDER           = "sort_order";

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
                CATEGORY_ID + " INTEGER," +
                NAME + " TEXT, " +
                DESCRIPTION + " TEXT, " +
                SORT_ORDER + " INTEGER" +
                ");");
    }

    /**
     * Add a new Category to CategoryTable.
     *
     * @param db the writable db
     * @param category the category to add
     * @return the row id of the new added Category. Or -1 if an error occurred.
     */
    public static long add(final SQLiteDatabase db, final Category category) {
        final ContentValues cv = new ContentValues();
        cv.put(CATEGORY_ID, category.getCategoryId());
        cv.put(NAME, category.getName());
        cv.put(DESCRIPTION, category.getDescription());
        cv.put(SORT_ORDER, category.getSortOrder());
        return db.insert(TABLE_NAME, _ID, cv);
    }

    /**
     * Get category by row id.
     *
     * @param db the readable database.
     * @param id the category row id
     * @return the Category object of this category.
     */
    public static Category getCategory(final SQLiteDatabase db, final long id) {
        final Cursor c = db.rawQuery("SELECT  * FROM " + TABLE_NAME + " WHERE " + _ID + " =? ",
                new String[] { String.valueOf(id) });

        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    final Category category = new Category();
                    category.setId(c.getLong(c.getColumnIndex(_ID)));
                    category.setCategoryId(c.getLong(c.getColumnIndex(CATEGORY_ID)));
                    category.setName(c.getString(c.getColumnIndex(NAME)));
                    category.setDescription(c.getString(c.getColumnIndex(DESCRIPTION)));
                    category.setSortOrder(c.getInt(c.getColumnIndex(SORT_ORDER)));
                    return category;
                }
            } finally {
                c.close();
            }
        }
        return null;
    }

    /**
     * Get all dish categorys, ordered by sort_order and _id fields.
     * @param db
     * @return
     */
    public static List<Category> getAllCategorys(final SQLiteDatabase db) {
        final Cursor c = db.rawQuery("SELECT  * FROM " + TABLE_NAME + " ORDER BY " + SORT_ORDER
                + ", " + CATEGORY_ID, null);

        if (c != null) {
            List<Category> list = new ArrayList<Category>();
            try {
                while(c.moveToNext()){
                    final Category category = new Category();
                    category.setId(c.getLong(c.getColumnIndex(_ID)));
                    category.setCategoryId(c.getLong(c.getColumnIndex(CATEGORY_ID)));
                    category.setName(c.getString(c.getColumnIndex(NAME)));
                    category.setDescription(c.getString(c.getColumnIndex(DESCRIPTION)));
                    category.setSortOrder(c.getInt(c.getColumnIndex(SORT_ORDER)));
                    list.add(category);
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
        DatabaseUtils.drop(db, TABLE_NAME);
    }

    /**
     * Remove all data in table category
     *
     * @param db
     */
    public static void deleteAll(final SQLiteDatabase db) {
        DatabaseUtils.truncate(db, TABLE_NAME);
    }
}
