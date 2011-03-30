/**
 * 
 */
package com.androidodc.eorder.order;

import com.androidodc.eorder.database.DatabaseHelper;

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
    //Integer in HashMap: 1:Dish Id 2:Dish copies
    private HashMap<Integer, Integer> mOrderDetail;
    //Integer in HashMap: 1:Category Id 2:Dish Id set
    private HashMap<Integer, HashSet<Integer>> mOrederCategories;

    /**
     * Initialize the OrderManager
     */
    private OrderManager() {
        mOrderListId = 0;
        mTableId = 0;
        mTotalPrice = 0;
        mOrderDetail = new HashMap<Integer, Integer>();
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
    public synchronized void addOneDish(int dishId, int categoryId) {
        updateOrderDetail(dishId, DEFAULT_DISHCOPY);
        if (mOrederCategories.containsKey(categoryId)) {
            mOrederCategories.get(categoryId).add(dishId);
        } else {
            HashSet<Integer> dishIdSet = new HashSet<Integer>();
            dishIdSet.add(dishId);
            mOrederCategories.put(categoryId, dishIdSet);
        }
    }

    /**
     * Remove one dish into the ordered list.
     * 
     * @param dishId
     *            Dish ID in one category
     * @param categoryId
     *            Category ID
     */
    public synchronized void removeDish(int dishId, int categoryId) {
        mOrderDetail.remove(dishId);
        HashSet<Integer> dishIdSet = mOrederCategories.get(categoryId);
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
    public synchronized void setDishCopy(int dishId, int copyNum) {
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
    private synchronized void updateOrderDetail(int dishId, int copyNum) {
        mOrderDetail.put(dishId, copyNum);
    }

    /**
     * Get the dish copy.
     * 
     * @param dishId
     *            Dish ID in one category
     * @return The dish's copy number
     */
    public synchronized int getDishCopy(int dishId) {
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
     * @param categoryId
     *            Category ID
     * @return HashSet<> - dishes ID
     */
    public synchronized HashSet<Integer> getOrderedDishByCategoryId(int categoryId) {
        return mOrederCategories.get(categoryId);
    }

    /**
     * Get ordered dishes by category ID.
     * 
     * @param dishId
     *            Dish ID
     * @return true: This dish is in the order detail. false: Not in.
     */
    public synchronized boolean isOrderedDish(int dishId) {
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
        Iterator<Integer> iter = mOrderDetail.keySet().iterator();
        while (iter.hasNext()) {
            int dishId = iter.next();
            int dishCopy = mOrderDetail.get(dishId);
            mTotalPrice = mDatabaseHelper.getDishById(dishId).getPrice() * dishCopy;
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
