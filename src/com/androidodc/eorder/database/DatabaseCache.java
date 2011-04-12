package com.androidodc.eorder.database;

import android.database.sqlite.SQLiteDatabase;

import com.androidodc.eorder.database.tables.CategoryTable;
import com.androidodc.eorder.database.tables.DishCategoryTable;
import com.androidodc.eorder.database.tables.DishTable;
import com.androidodc.eorder.datatypes.Category;
import com.androidodc.eorder.datatypes.Dish;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class DatabaseCache {
    /** ordered categories */
    private static List<Category> sCategoryList;
    /** ordered dish ids */
    private static List<Long> sSequencedDishIdList;
    /** categoryId and Category instances map cache */
    private static HashMap<Long, Category> sCategoryMap = new HashMap<Long, Category>();
    /** dishId and Dish instances map cache */
    private static HashMap<Long, Dish> sDishMap = new HashMap<Long, Dish>();
    /** categoryId and the category's dishes map cache */
    private static HashMap<Long, List<Dish>> sCategoryDishesMap = new HashMap<Long, List<Dish>>();

    public static void clearAllCache() {
        clearCategoryList();
        clearCategoryMapCache();
        clearDishMapCache();
        clearCategoryDishesMapCache();
        clearSequencedDishIdsCache();
    }

    public static void clearCategoryList() {
        if (sCategoryList != null) {
            sCategoryList.clear();
            sCategoryList = null;
        }
    }

    public static void clearCategoryMapCache() {
        sCategoryMap.clear();
    }

    public static void clearDishMapCache() {
        sDishMap.clear();
    }

    public static void clearCategoryDishesMapCache() {
        for (Entry<Long, List<Dish>> entry : sCategoryDishesMap.entrySet()) {
            List<Dish> dishList = entry.getValue();
            dishList.clear();
            dishList = null;
        }
        sCategoryDishesMap.clear();
    }

    public static void clearSequencedDishIdsCache() {
        if (sSequencedDishIdList != null) {
            sSequencedDishIdList.clear();
            sSequencedDishIdList = null;
        }
    }

    public static List<Category> getAllCategorys(final SQLiteDatabase db) {
        if (sCategoryList == null || sCategoryList.size() == 0) {
            sCategoryList = CategoryTable.getAllCategorys(db);
        }
        return sCategoryList;
    }

    public static List<Dish> getDishsByCategory(final SQLiteDatabase db, long categoryId) {
        List<Dish> dishList = sCategoryDishesMap.get(categoryId);
        if (dishList == null) {
            dishList = DishCategoryTable.getDishsByCategory(db, categoryId);
            sCategoryDishesMap.put(categoryId, dishList);
        }
        return dishList;
    }

    public static Dish getDishById(final SQLiteDatabase db, long dishId) {
        Dish dish = sDishMap.get(dishId);
        if (dish == null) {
            dish = DishTable.getDish(db, dishId);
            if (dish != null) {
                sDishMap.put(dishId, dish);
            }
        }
        return dish;
    }

    public static List<Long> getSequencedDishIds(final SQLiteDatabase db) {
        if (sSequencedDishIdList == null) {
            sSequencedDishIdList = DishCategoryTable.getSequencedDishIds(db);
        }
        return sSequencedDishIdList;
    }
}
