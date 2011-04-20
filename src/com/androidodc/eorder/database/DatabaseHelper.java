package com.androidodc.eorder.database;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.androidodc.eorder.database.tables.CategoryTable;
import com.androidodc.eorder.database.tables.ConfigTable;
import com.androidodc.eorder.database.tables.DishCategoryTable;
import com.androidodc.eorder.database.tables.DishTable;
import com.androidodc.eorder.datatypes.Category;
import com.androidodc.eorder.datatypes.Config;
import com.androidodc.eorder.datatypes.Dish;
import com.androidodc.eorder.datatypes.DishCategory;
import com.androidodc.eorder.utils.LogUtils;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper sDBHelper;

    private static final String DATABASE_NAME = "e_order.db";

    private static final int DATABASE_VERSION = 1;

    private static final String  TABLE_SQLITE_SEQUENCE = "sqlite_sequence";

    private Context mContext;

    public static DatabaseHelper getInstance() {
        if (sDBHelper == null) {
            throw new IllegalStateException("DatabaseHelper has NOT been initialized!");
        }
        return sDBHelper;
    }

    public synchronized static void init(final Context context) {
        if (sDBHelper != null) {
            throw new IllegalStateException("DatabaseHelper has been initialized!");
        }
        LogUtils.logD("Initializing DatabaseHelper");
        sDBHelper = new DatabaseHelper(context);
    }

    private DatabaseHelper(final Context context) {
        this(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    protected DatabaseHelper(final Context context, final String name, final CursorFactory factory,
            final int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        createAllTables(db);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        dropAllTables(db);
        onCreate(db);
    }

    public void createAllTables(final SQLiteDatabase db) {
        CategoryTable.create(db);
        DishTable.create(db);
        DishCategoryTable.create(db);
        ConfigTable.create(db);
    }

    public void dropAllTables(final SQLiteDatabase db) {
        ConfigTable.drop(db);
        DishCategoryTable.drop(db);
        DishTable.drop(db);
        CategoryTable.drop(db);
    }

    public List<Category> getAllCategorys() {
        return CategoryTable.getAllCategorys(getReadableDatabase());
    }

    public List<Dish> getDishsByCategory(final long categoryId) {
        return DishCategoryTable.getDishsByCategory(getReadableDatabase(), categoryId);
    }

    public Map<Long,List<Dish>> getCategoryAndDishes() {
        return DishCategoryTable.getCategoryAndDishes(getReadableDatabase());
    }

    public List<Dish> getAllDishs() {
        return DishTable.getAllDishs(getReadableDatabase());
    }

    public Dish getDishById(final long dishId) {
        return DishTable.getDish(getReadableDatabase(), dishId);
    }

    public long addCategory(final Category category) {
        return CategoryTable.add(getWritableDatabase(), category);
    }

    public long addDish(final Dish dish) {
        return DishTable.add(getWritableDatabase(), dish);
    }

    public long addDishCategory(final DishCategory dishCategory) {
        return DishCategoryTable.add(getWritableDatabase(), dishCategory);
    }

    public long addConfig(final Config config) {
        return ConfigTable.add(getWritableDatabase(), config);
    }

    /**
     * get dish last category id
     * @param dishId
     * @return
     */
    public long getDishCategoryId(long dishId){
        List<Long> list = getDishCategoryIds(dishId);
        if (list.size() > 0) {
            return list.get(list.size() - 1);
        }
        return DatabaseUtils.NO_DATA_ID;
    }

    public List<Long> getDishCategoryIds(long dishId){
        return DishCategoryTable.getDishCategoryIds(getReadableDatabase(), dishId);
    }

    public List<Long> getSequencedDishIds(){
        return DishCategoryTable.getSequencedDishIds(getReadableDatabase());
    }

    public void deleteAllTableDatas(){
        final SQLiteDatabase db = getWritableDatabase();
        ConfigTable.deleteAll(db);
        DishCategoryTable.deleteAll(db);
        DishTable.deleteAll(db);
        CategoryTable.deleteAll(db);
        db.execSQL("DELETE FROM " + TABLE_SQLITE_SEQUENCE);
    }
}
