/**
 * 
 */
package com.androidodc.eorder.order;

import com.androidodc.eorder.database.DatabaseHelper;
import com.androidodc.eorder.utils.LogUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author brady.geng
 * 
 */
public class OrderManager {
    private static final OrderManager sInstance = new OrderManager();
    private static final Integer DEFAULT_DISHCOPY = 1;
    private DatabaseHelper mDatabaseHelper;

    // The data of the order
    private int mOrderListId;
    private int mTableId;
    private int mTotalPrice;
    // Integer in HashMap: 1:Dish Id 2:Dish copies
    private HashMap<Long, Integer> mOrderDetail;
    // Integer in HashMap: 1:Category Id 2:Dish Id set
    private HashMap<Long, HashSet<Long>> mOrederCategories;

    /**
     * Initialize the OrderManager
     */
    private OrderManager() {
        mOrderListId = 0;
        mTableId = 0;
        mTotalPrice = 0;
        mOrderDetail = new HashMap<Long, Integer>();
        mOrederCategories = new HashMap<Long, HashSet<Long>>();
        mDatabaseHelper = DatabaseHelper.getInstance();
    }

    /**
     * Returns the unique instance of this class.
     */
    public static OrderManager getInstance() {
        return sInstance;
    }

    /**
     * Add one dish into the ordered list.
     * 
     * @param dishId
     *            Dish ID in one category
     * @param categoryId
     *            Category ID
     */
    public synchronized void addOneDish(long dishId, long categoryId) {
        LogUtils.logE("OrderManager " + dishId + "  " + categoryId);
        updateOrderDetail(dishId, DEFAULT_DISHCOPY);
        if (mOrederCategories.containsKey(categoryId)) {
            mOrederCategories.get(categoryId).add(dishId);
        } else {
            HashSet<Long> dishIdSet = new HashSet<Long>();
            dishIdSet.add(dishId);
            mOrederCategories.put(categoryId, dishIdSet);
        }
    }

    /**
     * Remove one dish into the ordered list.
     * 
     * @param dishId
     *            Dish ID in one category
     * @param l
     *            Category ID
     */
    public synchronized void removeDish(long dishId, long l) {
        mOrderDetail.remove(dishId);
        HashSet<Long> dishIdSet = mOrederCategories.get(l);
        if (dishIdSet.contains(dishId)) {
            dishIdSet.remove(dishId);
        }
    }

    /**
     * Set the dish copy.
     * 
     * @param dishId
     *            Dish ID in one category
     * @param copyNum
     *            Dish copies number
     */
    public synchronized void setDishCopy(long dishId, int copyNum) {
        updateOrderDetail(dishId, copyNum);
    }

    /**
     * Set the dish copy.
     * 
     * @param dishId
     *            Dish ID in one category
     * @param copyNum
     *            Dish copies number
     */
    private synchronized void updateOrderDetail(long dishId, int copyNum) {
        mOrderDetail.put(dishId, copyNum);
    }

    /**
     * Get the dish copy.
     * 
     * @param dishId
     *            Dish ID in one category
     * @return The dish's copy number
     */
    public synchronized int getDishCopy(Long dishId) {
        LogUtils.logE("getDishCopy " + mOrderDetail.get(dishId));
        return mOrderDetail.get(dishId);
    }

    /**
     * Check if this category in the ordered list.
     * 
     * @param categoryId
     *            Category ID
     * @return true: in, false: not
     */
    public synchronized boolean isOrderedCategory(int categoryId) {
        return mOrederCategories.containsKey(categoryId);
    }

    /**
     * Get ordered dishes by category ID.
     * 
     * @param l
     *            Category ID
     * @return HashSet<> - dishes ID
     */
    public synchronized HashSet<Long> getOrderedDishByCategoryId(long l) {
        LogUtils.logE("OrderManager.category.size" + mOrederCategories.size());
        if (mOrederCategories.get(l) != null) {
            LogUtils.logE("OrderManager.hashSet.size" + mOrederCategories.get(l).size());
        }
        return mOrederCategories.get(l);
    }

    /**
     * Get ordered dishes by category ID.
     * 
     * @param dishId
     *            Dish ID
     * @return true: This dish is in the order detail. false: Not in.
     */
    public synchronized boolean isOrderedDish(long dishId) {
        return mOrderDetail.containsKey(dishId);
    }

    /**
     * Set the table ID in the ordered list.
     * 
     * @param tableId
     *            table ID
     */
    public synchronized void setTableId(int tableId) {
        mTableId = tableId;
    }

    /**
     * Set the table ID in the ordered list.
     * 
     * @return tableId table ID
     */
    public synchronized int getTableId() {
        return mTableId;
    }

    /**
     * Generate and return total price.
     * 
     * @return totalPrice The dishes' total price.
     */
    public synchronized int getTotalPrice() {
        Iterator<Long> iter = mOrderDetail.keySet().iterator();
        while (iter.hasNext()) {
            Long dishId = iter.next();
            LogUtils.logE("OrderManager dishId = " + dishId);
            int dishCopy = mOrderDetail.get(dishId);
            LogUtils.logE("OrderManager dishCopy = " + dishCopy);
            if (mDatabaseHelper.getDishById(dishId) != null) {
                LogUtils.logE("mDatabaseHelper.getDishById(dishId) != null");
                mTotalPrice += mDatabaseHelper.getDishById(dishId).getPrice() * dishCopy;
            }
        }
        return mTotalPrice;
    }

    public synchronized void setOrderListId(int orderListId) {
        mOrderListId = orderListId;
    }

    public synchronized int getOrderListId() {
        return mOrderListId;
    }
}
