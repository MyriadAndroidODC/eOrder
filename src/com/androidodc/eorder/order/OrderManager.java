package com.androidodc.eorder.order;

import com.androidodc.eorder.database.DatabaseHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author brady.geng
 */
public class OrderManager {
    private static final OrderManager sInstance = new OrderManager();
    private static final Integer DEFAULT_DISHCOPY = 1;

    // The data of the order
    private long mOrderListId;
    private long mTableId;
    private int mTotalPrice;
    // Integer in HashMap: 1:Dish Id 2:Dish copies
    private HashMap<Long, Long> mOrderDetail;
    // Integer in HashMap: 1:Category Id 2:Dish Id set
    private HashMap<Long, HashSet<Long>> mOrederCategories;

    /**
     * Initialize the OrderManager
     */
    private OrderManager() {
        mOrderListId = 0;
        mTableId = 0;
        mTotalPrice = 0;
        mOrderDetail = new HashMap<Long, Long>();
        mOrederCategories = new HashMap<Long, HashSet<Long>>();
    }

    /**
     * Clear the OrderManager's data
     */
    public void ClearOrderManager() {
        mOrderListId = 0;
        mTableId = 0;
        mTotalPrice = 0;

        if (null != mOrderDetail) {
            mOrderDetail.clear();
        }

        if (null != mOrederCategories) {
            mOrederCategories.clear();
        }
    }

    /**
     * Returns the unique instance of this class.
     */
    public static OrderManager getInstance() {
        return sInstance;
    }

    /**
     * Add one dish into the ordered list.
     * @param dishId
     *            Dish ID in one category
     * @param categoryId
     *            Category ID
     */
    public void addOneDish(final long dishId, final long categoryId) {
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
     * @param dishId
     *            Dish ID in one category
     * @param categoryId
     *            Category ID
     */
    public void removeDish(final long dishId, final long categoryId) {
        mOrderDetail.remove(dishId);
        HashSet<Long> dishIdSet = mOrederCategories.get(categoryId);
        if (dishIdSet.contains(dishId)) {
            dishIdSet.remove(dishId);
        }
    }

    /**
     * Set the dish copy.
     * @param dishId
     *            Dish ID in one category
     * @param copyNum
     *            Dish copies number
     */
    public void setDishCopy(final long dishId, final long copyNum) {
        updateOrderDetail(dishId, copyNum);
    }

    /**
     * Set the dish copy.
     * @param dishId
     *            Dish ID in one category
     * @param copyNum
     *            Dish copies number
     */
    private void updateOrderDetail(final long dishId, final long copyNum) {
        mOrderDetail.put(dishId, copyNum);
    }

    /**
     * Get the dish copy.
     * @param dishId
     *            Dish ID in one category
     * @return The dish's copy number
     */
    public long getDishCopy(final long dishId) {
        return mOrderDetail.get(dishId);
    }

    /**
     * Check if this category in the ordered list.
     * @param categoryId
     *            Category ID
     * @return true: in, false: not
     */
    public boolean isOrderedCategory(final long categoryId) {
        return mOrederCategories.containsKey(categoryId);
    }

    /**
     * Get ordered dishes by category ID.
     * @param categoryId
     *            Category ID
     * @return HashSet<> - dishes ID
     */
    public HashSet<Long> getOrderedDishByCategoryId(final long categoryId) {
        return mOrederCategories.get(categoryId);
    }

    /**
     * Get all ordered dishes.
     * @return HashMap<> - dishes ID and dishes' copy
     */
    public HashMap<Long, Long> getAllOrderedDishes() {
        return mOrderDetail;
    }

    /**
     * Get all ordered category.
     * @return Set<Long> - Ordered category ID
     */
    public Set<Long> getAllOrderedCategory() {
        return mOrederCategories.keySet();
    }

    /**
     * Get ordered dishes by category ID.
     * @param dishId
     *            Dish ID
     * @return true: This dish is in the order detail. false: Not in.
     */
    public boolean isOrderedDish(final long dishId) {
        return mOrderDetail.containsKey(dishId);
    }

    /**
     * Judge if the order is empty.
     * @return true: The order is not empty. false: The order is empty.
     */
    public boolean isOrderEmpty() {
        return mOrderDetail.isEmpty();
    }

    /**
     * Set the table ID in the ordered list.
     * @param tableId
     *            table ID
     */
    public void setTableId(long tableId) {
        mTableId = tableId;
    }

    /**
     * Set the table ID in the ordered list.
     * @return tableId table ID
     */
    public long getTableId() {
        return mTableId;
    }

    /**
     * Generate and return total price.
     * @return totalPrice The dishes' total price.
     */
    public int getTotalPrice() {
        mTotalPrice = 0;
        if (null != mOrderDetail) {
            DatabaseHelper dbHelper = DatabaseHelper.getInstance();
            Iterator<Long> iter = mOrderDetail.keySet().iterator();
            while (iter.hasNext()) {
                long dishId = iter.next();
                long dishCopy = mOrderDetail.get(dishId);
                mTotalPrice += (int) (dbHelper.getDishById(dishId).getPrice() * dishCopy);
            }
        }

        return mTotalPrice;
    }

    public void setOrderListId(int orderListId) {
        mOrderListId = orderListId;
    }

    public long getOrderListId() {
        return mOrderListId;
    }
}
