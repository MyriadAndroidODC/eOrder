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

    public List<Dish> getDishsByCategory(int categoryId) {
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

    /**
     * only for test inserting variable datas to database
     */
    public static void testAddDatas() {
        Category c = new Category();
        c.setCategoryId(1);
        c.setName("¥®≤À");
        c.setDescription("¥®≤Àœµ¡–");
        c.setSortOrder(10);
        try {
            sDBHelper.addCategory(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Category c2 = new Category();
        c2.setCategoryId(2);
        c2.setName("‘¡≤À");
        c2.setDescription("‘¡≤Àœµ¡–");
        c2.setSortOrder(20);
        try {
            sDBHelper.addCategory(c2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Dish d = new Dish();
        d.setDishId(1);
        d.setName("”„œ„»‚Àø");
        d.setPrice(1200);
        d.setDescription("”„œ„»‚Àøµƒ√Ë ˆ");
        d.setImageLocal("imageLocal");
        d.setImageServer("imageLocal");
        d.setCreateTime(new Date());
        d.setUpdateTime(null);
        try {
            sDBHelper.addDish(d);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Dish d2 = new Dish();
        d2.setDishId(2);
        d2.setName("À‚ƒ‡∞◊»‚");
        d2.setPrice(1800);
        d2.setImageLocal("imageLocal2");
        d2.setImageServer("imageLocal2");
        d2.setDescription("À‚ƒ‡∞◊»‚µƒ√Ë ˆ");
        d2.setCreateTime(new Date());
        d2.setUpdateTime(null);
        try {
            sDBHelper.addDish(d2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Dish d3 = new Dish();
        d3.setDishId(3);
        d3.setName("¬È∆≈∂π∏Ø");
        d3.setPrice(800);
        d3.setImageLocal("imageLocal3");
        d3.setImageServer("imageLocal3");
        d3.setDescription("¬È∆≈∂π∏Øµƒ√Ë ˆ");
        d3.setCreateTime(new Date());
        d3.setUpdateTime(new Date());
        try {
            sDBHelper.addDish(d3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Dish d4 = new Dish();
        d4.setDishId(4);
        d4.setName("≤Ê…’±˝");
        d4.setPrice(150);
        d4.setImageLocal("imageLocal4");
        d4.setImageServer("imageLocal4");
        d4.setDescription("’˝◊⁄π„∂´≤Ó…’±˝");
        d4.setCreateTime(new Date());
        d4.setUpdateTime(new Date());
        try {
            sDBHelper.addDish(d4);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Dish d5 = new Dish();
        d5.setDishId(5);
        d5.setName("π„Œ∂œ„≥¶");
        d5.setPrice(3800);
        d5.setImageLocal("imageLocal2");
        d5.setImageServer("imageLocal2");
        d5.setDescription("π„Œ∂œ„≥¶µƒ√Ë ˆ");
        d5.setCreateTime(new Date());
        d5.setUpdateTime(null);
        try {
            sDBHelper.addDish(d5);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DishCategory dc = new DishCategory();
        dc.setCategoryId(c.getCategoryId());
        dc.setDishId(d.getDishId());
        try {
            sDBHelper.addDishCategory(dc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DishCategory dc2 = new DishCategory();
        dc2.setCategoryId(c.getCategoryId());
        dc2.setDishId(d2.getDishId());
        try {
            sDBHelper.addDishCategory(dc2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DishCategory dc3 = new DishCategory();
        dc3.setCategoryId(c.getCategoryId());
        dc3.setDishId(d3.getDishId());
        try {
            sDBHelper.addDishCategory(dc3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DishCategory dc4 = new DishCategory();
        dc4.setCategoryId(c2.getCategoryId());
        dc4.setDishId(d4.getDishId());
        try {
            sDBHelper.addDishCategory(dc4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DishCategory dc5 = new DishCategory();
        dc5.setCategoryId(c2.getCategoryId());
        dc5.setDishId(d5.getDishId());
        try {
            sDBHelper.addDishCategory(dc5);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Config cfg = new Config();
        cfg.setConfigId(1);
        cfg.setName("hotelName");
        cfg.setValue("œ„∏Ò¿Ô¿≠");
        cfg.setDescription("Ã·π©…Ã√˚≥∆");
        try {
            sDBHelper.addConfig(cfg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * only for test query data in database
     */
    public static void testQueryDatas() {
        try {
            System.out
                    .println("Test for: getAllCategorys() and getDishsByCategory(int categoryId)");
            List<Category> list = sDBHelper.getAllCategorys();
            for (Category c : list) {
                System.out.println(c.getCategoryId() + "  " + c.getName());
                List<Dish> dishList = sDBHelper.getDishsByCategory(c.getCategoryId());
                for (Dish d : dishList) {
                    System.out.println("      " + d.getName() + "  " + d.getPrice());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Test for: getDishById(int dishId)");
            Dish dish = sDBHelper.getDishById(2);
            if (dish != null) {
                System.out.println(dish.getDishId() + "  " + dish.getName() + "  "
                        + dish.getPrice() + "  " + dish.getDescription());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Test for: getAllDishs()");
            List<Dish> dishList2 = sDBHelper.getAllDishs();
            for (Dish dish2 : dishList2) {
                System.out.println(dish2.getDishId() + "  " + dish2.getName() + "  "
                        + dish2.getPrice() + "  " + dish2.getDescription());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
