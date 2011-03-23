package com.androidodc.eorder.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

public class ResponseParser {
    public List<Dish> parseDishes(String dishesRespStr) throws JSONException {
        List<Dish> resultList = null;
        JSONObject dishes = new JSONObject(dishesRespStr);
        resultList = new ArrayList<Dish>();
        
        Dish tempDish = null;
        JSONArray dishArray = dishes.getJSONArray("dishes");
        
        for (int i = 0; i < dishArray.length(); i++) {
            tempDish = new Dish();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempDish.setId(Integer.parseInt(dishObj.getString("_id")));
            tempDish.setName(dishObj.getString("name"));
            tempDish.setPrice(Integer.parseInt(dishObj.getString("price")));
            tempDish.setDescription(dishObj.getString("description"));
            tempDish.setImageServer(dishObj.getString("image_url"));
            
            long createTimeValue = Long.parseLong(dishObj.getString("create_time"));
            long updateTimeValue = Long.parseLong(dishObj.getString("update_time"));
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
    
    public List<Category> parseCategories(String categoryRespStr) throws JSONException {
        List<Category> resultList = null; 
        JSONObject categories = new JSONObject(categoryRespStr);
        resultList = new ArrayList<Category>();
        
        Category tempCategory = null;
        JSONArray dishArray = categories.getJSONArray("categories");
        
        for (int i = 0; i < dishArray.length(); i++) {
            tempCategory = new Category();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempCategory.setId(Integer.parseInt(dishObj.getString("_id")));
            tempCategory.setName(dishObj.getString("name"));
            tempCategory.setDescription(dishObj.getString("description"));
            tempCategory.setSortOrder(Integer.parseInt(dishObj.getString("sort_order")));
            resultList.add(tempCategory);
        }
        return resultList;
    }

    public List<DishCategory> parseDishCategory(String dishCategoryRespStr) throws JSONException {
        List<DishCategory> resultList = null;
        JSONObject dishCategory = new JSONObject(dishCategoryRespStr);
        resultList = new ArrayList<DishCategory>();
        
        DishCategory tempDishCategory = null;
        JSONArray dishArray = dishCategory.getJSONArray("dish_category");
        
        for (int i = 0; i < dishArray.length(); i++) {
            tempDishCategory = new DishCategory();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempDishCategory.setDishId(Integer.parseInt(dishObj.getString("dish_id")));
            tempDishCategory.setCategoryId(Integer.parseInt(dishObj.getString("category_id")));
            resultList.add(tempDishCategory);
        }
        return resultList;
    }

    public List<Order> parseOrders(String orderRespStr) throws JSONException {
        List<Order> resultList = null;
        JSONObject orders = new JSONObject(orderRespStr);
        resultList = new ArrayList<Order>();
        
        Order tempOrder = null;
        JSONArray dishArray = orders.getJSONArray("orders");
        
        for (int i = 0; i < dishArray.length(); i ++) {
            tempOrder = new Order();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempOrder.setId(Integer.parseInt(dishObj.getString("_id")));
            tempOrder.setStatus(Integer.parseInt(dishObj.getString("status")));
            tempOrder.setOrderTotal(Integer.parseInt(dishObj.getString("sum")));

            long createTimeValue = Long.parseLong(dishObj.getString("create_time"));
            long payTimeValue = Long.parseLong(dishObj.getString("pay_time"));
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

    public List<OrderDetail> parseOrderDetail(String orderDetailRespStr) throws JSONException {
        List<OrderDetail> resultList = null;
        JSONObject orderDetail = new JSONObject(orderDetailRespStr);
        resultList = new ArrayList<OrderDetail>();
        
        OrderDetail tempOrderDetail = null;
        JSONArray dishArray = orderDetail.getJSONArray("order_detail");
        
        for (int i = 0; i < dishArray.length(); i++) {
            tempOrderDetail = new OrderDetail();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempOrderDetail.setId(Integer.parseInt(dishObj.getString("_id")));
            tempOrderDetail.setOrderId(Integer.parseInt(dishObj.getString("order_id")));
            tempOrderDetail.setTableId(Integer.parseInt(dishObj.getString("dining_table_id")));
            tempOrderDetail.setDishId(Integer.parseInt(dishObj.getString("dish_id")));
            tempOrderDetail.setNumber(Integer.parseInt(dishObj.getString("number")));
            resultList.add(tempOrderDetail);
        }
        return resultList;
    }

    public List<DiningTable> parseDiningTables(String diningTablesRespStr) throws JSONException {
        List<DiningTable> resultList = null;
        JSONObject diningTables = new JSONObject(diningTablesRespStr);
        resultList = new ArrayList<DiningTable>();
        
        DiningTable tempDiningTable = null;
        JSONArray dishArray = diningTables.getJSONArray("dining_tables");
        
        for (int i = 0; i < dishArray.length(); i++) {
            tempDiningTable = new DiningTable();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempDiningTable.setId(Integer.parseInt(dishObj.getString("_id")));
            tempDiningTable.setName(dishObj.getString("name"));
            tempDiningTable.setMaxPeople(Integer.parseInt(dishObj.getString("capacity")));
            int free = Integer.parseInt(dishObj.getString("status"));            
            tempDiningTable.setFree(free == 0 ? true : false);
            resultList.add(tempDiningTable);
        }
        return resultList;
    }

    public List<Config> parseConfigs(String configsRespStr) throws JSONException {
        
        List<Config> resultList = null;
        JSONObject configs = new JSONObject(configsRespStr);
        resultList = new ArrayList<Config>();
        
        Config tempConfig = null;
        JSONArray dishArray = configs.getJSONArray("configs");
        
        for (int i = 0; i < dishArray.length(); i++) {
            tempConfig = new Config();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempConfig.setId(Integer.parseInt(dishObj.getString("_id")));
            tempConfig.setName(dishObj.getString("name"));
            tempConfig.setValue(dishObj.getString("value"));
            tempConfig.setDescription(dishObj.getString("description"));
            resultList.add(tempConfig);
        }
        return resultList;
    }
}
