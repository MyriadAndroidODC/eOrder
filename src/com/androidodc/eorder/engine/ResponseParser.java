package com.androidodc.eorder.engine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.androidodc.eorder.datatypes.Category;
import com.androidodc.eorder.datatypes.Config;
import com.androidodc.eorder.datatypes.DiningTable;
import com.androidodc.eorder.datatypes.Dish;
import com.androidodc.eorder.datatypes.DishCategory;
import com.androidodc.eorder.datatypes.Order;
import com.androidodc.eorder.datatypes.OrderDetail;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResponseParser {
    public static List<Dish> parseDishes(String dishesRespStr) throws JSONException {
        if (dishesRespStr == null) {
            return null;
        }
        List<Dish> resultList = null;
        JSONObject dishes = new JSONObject(dishesRespStr);
        resultList = new ArrayList<Dish>();
        
        Dish tempDish = null;
        JSONArray dishArray = dishes.getJSONArray("dishes");
        
        for (int i = 0; i < dishArray.length(); i++) {
            tempDish = new Dish();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempDish.setId(dishObj.getInt("_id"));
            tempDish.setName(dishObj.getString("name"));
            tempDish.setPrice(dishObj.getInt("price"));
            tempDish.setDescription(dishObj.getString("description"));
            tempDish.setImageServer(dishObj.getString("image_url"));
            
            long createTimeValue = dishObj.getLong("create_time");
            long updateTimeValue = dishObj.getLong("update_time");
            Date createTime = new Date();
            Date updateTime = new Date();
            createTime.setTime(createTimeValue);
            updateTime.setTime(updateTimeValue);
            
            tempDish.setCreateTime(createTime);
            tempDish.setUpdateTime(updateTime);
            resultList.add(tempDish);
        }
        return resultList;
    }
    
    public static List<Category> parseCategories(String categoryRespStr) throws JSONException {
        if (categoryRespStr == null) {
            return null;
        }
        List<Category> resultList = null; 
        JSONObject categories = new JSONObject(categoryRespStr);
        resultList = new ArrayList<Category>();
        
        Category tempCategory = null;
        JSONArray dishArray = categories.getJSONArray("categories");
        
        for (int i = 0; i < dishArray.length(); i++) {
            tempCategory = new Category();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempCategory.setId(dishObj.getInt("_id"));
            tempCategory.setName(dishObj.getString("name"));
            tempCategory.setDescription(dishObj.getString("description"));
            tempCategory.setSortOrder(dishObj.getInt("sort_order"));
            resultList.add(tempCategory);
        }
        return resultList;
    }

    public static List<DishCategory> parseDishCategory(String dishCategoryRespStr) throws JSONException {
        if (dishCategoryRespStr == null) {
            return null;
        }
        List<DishCategory> resultList = null;
        JSONObject dishCategory = new JSONObject(dishCategoryRespStr);
        resultList = new ArrayList<DishCategory>();
        
        DishCategory tempDishCategory = null;
        JSONArray dishArray = dishCategory.getJSONArray("dish_category");
        
        for (int i = 0; i < dishArray.length(); i++) {
            tempDishCategory = new DishCategory();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempDishCategory.setDishId(dishObj.getInt("dish_id"));
            tempDishCategory.setCategoryId(dishObj.getInt("category_id"));
            resultList.add(tempDishCategory);
        }
        return resultList;
    }

    public static List<Order> parseOrders(String orderRespStr) throws JSONException {
        if (orderRespStr == null) {
            return null;
        }
        List<Order> resultList = null;
        JSONObject orders = new JSONObject(orderRespStr);
        resultList = new ArrayList<Order>();
        
        Order tempOrder = null;
        JSONArray dishArray = orders.getJSONArray("orders");
        
        for (int i = 0; i < dishArray.length(); i ++) {
            tempOrder = new Order();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempOrder.setId(dishObj.getInt("_id"));
            tempOrder.setStatus(dishObj.getInt("status"));
            tempOrder.setOrderTotal(dishObj.getInt("sum"));

            long createTimeValue = dishObj.getLong("create_time");
            long payTimeValue = dishObj.getLong("pay_time");
            Date createTime = new Date();
            Date payTime = new Date();
            createTime.setTime(createTimeValue);
            payTime.setTime(payTimeValue);
            tempOrder.setCreateTime(createTime);
            tempOrder.setPayTime(payTime);
            resultList.add(tempOrder);
        }
        return resultList;
    }

    public static List<OrderDetail> parseOrderDetail(String orderDetailRespStr) throws JSONException {
        if (orderDetailRespStr == null) {
            return null;
        }
        List<OrderDetail> resultList = null;
        JSONObject orderDetail = new JSONObject(orderDetailRespStr);
        resultList = new ArrayList<OrderDetail>();
        
        OrderDetail tempOrderDetail = null;
        JSONArray dishArray = orderDetail.getJSONArray("order_detail");
        
        for (int i = 0; i < dishArray.length(); i++) {
            tempOrderDetail = new OrderDetail();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempOrderDetail.setId(dishObj.getInt("_id"));
            tempOrderDetail.setOrderId(dishObj.getInt("order_id"));
            tempOrderDetail.setTableId(dishObj.getInt("dining_table_id"));
            tempOrderDetail.setDishId(dishObj.getInt("dish_id"));
            tempOrderDetail.setNumber(dishObj.getInt("number"));
            resultList.add(tempOrderDetail);
        }
        return resultList;
    }

    public static List<DiningTable> parseDiningTables(String diningTablesRespStr) throws JSONException {
        if (diningTablesRespStr == null) {
            return null;
        }
        List<DiningTable> resultList = null;
        JSONObject diningTables = new JSONObject(diningTablesRespStr);
        resultList = new ArrayList<DiningTable>();
        
        DiningTable tempDiningTable = null;
        JSONArray dishArray = diningTables.getJSONArray("dining_tables");
        
        for (int i = 0; i < dishArray.length(); i++) {
            tempDiningTable = new DiningTable();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempDiningTable.setId(dishObj.getInt("_id"));
            tempDiningTable.setName(dishObj.getString("name"));
            tempDiningTable.setMaxPeople(dishObj.getInt("capacity"));
            int free = dishObj.getInt("status");            
            tempDiningTable.setFree(free == 0 ? true : false);
            resultList.add(tempDiningTable);
        }
        return resultList;
    }

    public static List<Config> parseConfigs(String configsRespStr) throws JSONException {
        if (configsRespStr == null) {
            return null;
        }
        List<Config> resultList = null;
        JSONObject configs = new JSONObject(configsRespStr);
        resultList = new ArrayList<Config>();
        
        Config tempConfig = null;
        JSONArray dishArray = configs.getJSONArray("configs");
        
        for (int i = 0; i < dishArray.length(); i++) {
            tempConfig = new Config();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempConfig.setId(dishObj.getInt("_id"));
            tempConfig.setName(dishObj.getString("name"));
            tempConfig.setValue(dishObj.getString("value"));
            tempConfig.setDescription(dishObj.getString("description"));
            resultList.add(tempConfig);
        }
        return resultList;
    }
}
