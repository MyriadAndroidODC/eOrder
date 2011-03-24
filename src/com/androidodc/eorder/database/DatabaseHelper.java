package com.androidodc.eorder.database;

import java.util.Date;
import java.util.List;
import java.util.Observable;

import android.content.Context;
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

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper sDBHelper;

    private static final String DATABASE_NAME = "e_order.db";

    private static final int DATABASE_VERSION = 1;

    private DatabaseObservable mDBObservable;

    private static class DatabaseObservable extends Observable {
        public void doChanged() {
            setChanged();
        }
    }

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
        mDBObservable = new DatabaseObservable();
        mContext = context;
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        createAllTables(db);
    }

    @Override
    public synchronized void close() {
        if (mDBObservable != null) {
            mDBObservable.deleteObservers();
        }
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

    public List<Dish> getDishsByCategory(long categoryId) {
        return DishCategoryTable.getDishsByCategory(getReadableDatabase(), categoryId);
    }

    public List<Dish> getAllDishs() {
        return DishTable.getAllDishs(getReadableDatabase());
    }

    public Dish getDishById(int dishId) {
        return DishTable.getDish(getReadableDatabase(), dishId);
    }

    public void addCategory(final Category category) {
        CategoryTable.add(getWritableDatabase(), category);
    }

    public void addDish(final Dish dish) {
        DishTable.add(getWritableDatabase(), dish);
    }

    public void addDishCategory(final DishCategory dishCategory) {
        DishCategoryTable.add(getWritableDatabase(), dishCategory);
    }

    public void addConfig(final Config config) {
        ConfigTable.add(getWritableDatabase(), config);
    }
}
