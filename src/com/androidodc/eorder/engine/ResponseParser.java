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
import java.util.ArrayList;
import java.util.Date;

public class ResponseParser {
    public static ArrayList<Dish> parseDishes(String dishesRespStr) throws JSONException {
        if (dishesRespStr == null) {
            return null;
        }
        ArrayList<Dish> resultList = null;
        JSONObject dishes = new JSONObject(dishesRespStr);
        resultList = new ArrayList<Dish>();
        
        JSONArray dishArray = dishes.getJSONArray("dishes");
        int length = dishArray.length();
        for (int i = 0; i < length; i++) {
            Dish dish = new Dish();
            JSONObject dishObj = dishArray.getJSONObject(i);
            dish.setDishId(dishObj.getLong("_id"));
            dish.setName(dishObj.getString("name"));
            dish.setPrice(dishObj.getInt("price"));
            dish.setDescription(dishObj.getString("description"));
            dish.setImageServer(dishObj.getString("image_url"));
            
            long createTimeValue = dishObj.getLong("create_time");
            long updateTimeValue = dishObj.getLong("update_time");
            Date createTime = new Date(createTimeValue);
            Date updateTime = new Date(updateTimeValue);
            
            dish.setCreateTime(createTime);
            dish.setUpdateTime(updateTime);
            resultList.add(dish);
        }
        return resultList;
    }
    
    public static ArrayList<Category> parseCategories(String categoryRespStr) throws JSONException {
        if (categoryRespStr == null) {
            return null;
        }
        ArrayList<Category> resultList = null; 
        JSONObject categories = new JSONObject(categoryRespStr);
        resultList = new ArrayList<Category>();
        
        JSONArray categoryArray = categories.getJSONArray("categories");
        int length = categoryArray.length();
        for (int i = 0; i < length; i++) {
            Category category = new Category();
            JSONObject categoryObj = categoryArray.getJSONObject(i);
            category.setCategoryId(categoryObj.getLong("_id"));
            category.setName(categoryObj.getString("name"));
            category.setDescription(categoryObj.getString("description"));
            category.setSortOrder(categoryObj.getInt("sort_order"));
            resultList.add(category);
        }
        return resultList;
    }

    public static ArrayList<DishCategory> parseDishCategory(String dishCategoryRespStr) throws JSONException {
        if (dishCategoryRespStr == null) {
            return null;
        }
        ArrayList<DishCategory> resultList = null;
        JSONObject dishCategories = new JSONObject(dishCategoryRespStr);
        resultList = new ArrayList<DishCategory>();
        
        JSONArray dishCategoryArray = dishCategories.getJSONArray("dish_category");
        int length = dishCategoryArray.length();
        for (int i = 0; i < length; i++) {
            DishCategory dishCategory = new DishCategory();
            JSONObject dishCategoryObj = dishCategoryArray.getJSONObject(i);
            dishCategory.setDishId(dishCategoryObj.getLong("dish_id"));
            dishCategory.setCategoryId(dishCategoryObj.getLong("category_id"));
            resultList.add(dishCategory);
        }
        return resultList;
    }

    public static ArrayList<Order> parseOrders(String orderRespStr) throws JSONException {
        if (orderRespStr == null) {
            return null;
        }
        ArrayList<Order> resultList = null;
        JSONObject orders = new JSONObject(orderRespStr);
        resultList = new ArrayList<Order>();
        
        JSONArray orderArray = orders.getJSONArray("orders");
        int length = orderArray.length();
        for (int i = 0; i < length; i ++) {
            Order order = new Order();
            JSONObject orderObj = orderArray.getJSONObject(i);
            order.setOrderId(orderObj.getLong("_id"));
            order.setStatus(orderObj.getInt("status"));
            order.setOrderTotal(orderObj.getInt("sum"));

            long createTimeValue = orderObj.getLong("create_time");
            long payTimeValue = orderObj.getLong("pay_time");
            Date createTime = new Date(createTimeValue);
            Date payTime = new Date(payTimeValue);
            order.setCreateTime(createTime);
            order.setPayTime(payTime);
            resultList.add(order);
        }
        return resultList;
    }

    public static ArrayList<OrderDetail> parseOrderDetail(String orderDetailRespStr) throws JSONException {
        if (orderDetailRespStr == null) {
            return null;
        }
        ArrayList<OrderDetail> resultList = null;
        JSONObject orderDetails = new JSONObject(orderDetailRespStr);
        resultList = new ArrayList<OrderDetail>();
        
        JSONArray orderDetailArray = orderDetails.getJSONArray("order_detail");
        int length = orderDetailArray.length();
        for (int i = 0; i < length; i++) {
            OrderDetail orderDetail = new OrderDetail();
            JSONObject orderDetailObj = orderDetailArray.getJSONObject(i);
            orderDetail.setOrderItemId(orderDetailObj.getLong("_id"));
            orderDetail.setOrderId(orderDetailObj.getLong("order_id"));
            orderDetail.setTableId(orderDetailObj.getLong("dining_table_id"));
            orderDetail.setDishId(orderDetailObj.getLong("dish_id"));
            orderDetail.setNumber(orderDetailObj.getInt("number"));
            resultList.add(orderDetail);
        }
        return resultList;
    }

    public static ArrayList<DiningTable> parseDiningTables(String diningTablesRespStr) throws JSONException {
        if (diningTablesRespStr == null) {
            return null;
        }
        ArrayList<DiningTable> resultList = null;
        JSONObject diningTables = new JSONObject(diningTablesRespStr);
        resultList = new ArrayList<DiningTable>();
        
        JSONArray diningArray = diningTables.getJSONArray("dining_tables");
        int length = diningArray.length();
        for (int i = 0; i < length; i++) {
            DiningTable diningTable = new DiningTable();
            JSONObject diningTableObj = diningArray.getJSONObject(i);
            diningTable.setDiningTableId(diningTableObj.getLong("_id"));
            diningTable.setName(diningTableObj.getString("name"));
            diningTable.setMaxPeople(diningTableObj.getInt("capacity"));
            int free = diningTableObj.getInt("status");            
            diningTable.setFree(free == 0 ? true : false);
            resultList.add(diningTable);
        }
        return resultList;
    }

    public static ArrayList<Config> parseConfigs(String configsRespStr) throws JSONException {
        if (configsRespStr == null) {
            return null;
        }
        ArrayList<Config> resultList = null;
        JSONObject configs = new JSONObject(configsRespStr);
        resultList = new ArrayList<Config>();
        
        JSONArray configArray = configs.getJSONArray("configs");
        int length = configArray.length();
        for (int i = 0; i < length; i++) {
            Config config = new Config();
            JSONObject configObj = configArray.getJSONObject(i);
            config.setConfigId(configObj.getLong("_id"));
            config.setName(configObj.getString("name"));
            config.setValue(configObj.getString("value"));
            config.setDescription(configObj.getString("description"));
            resultList.add(config);
        }
        return resultList;
    }
}
