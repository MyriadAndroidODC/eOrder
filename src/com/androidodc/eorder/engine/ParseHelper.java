package com.androidodc.eorder.engine;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class ParseHelper {
    public List<Dish> parseDishsResp(String dishsRespStr)
        throws JSONException {
        
        List<Dish> resultList = null;
        JSONObject dishs = new JSONObject(dishsRespStr);
        resultList = new ArrayList<Dish>();
        
        int i;
        Dish tempDish = null;
        JSONArray dishArray = dishs.getJSONArray("dishs");
        
        for (i = 0;i < dishArray.length();i ++) {
            tempDish = new Dish();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempDish.setId(Integer.parseInt(dishObj.getString("id")));
            tempDish.setName(dishObj.getString("name"));
            tempDish.setPrice(Double.parseDouble(dishObj.getString("price")));
            tempDish.setDescription(dishObj.getString("description"));
            tempDish.setImageServer(dishObj.getString("image"));
            tempDish.setCreatedOn(new Date(dishObj.getString("created_on")));
            tempDish.setUpdatedOn(new Date(dishObj.getString("updated_on")));
            resultList.add(tempDish);
        }
        return resultList;
    }
    public List<Category> parseCategorysResp(String categoryRespStr)
        throws JSONException {
        List<Category> resultList = null; 
        JSONObject dishs = new JSONObject(categoryRespStr);
        resultList = new ArrayList<Category>();
        
        int i;
        Category tempCategory = null;
        JSONArray dishArray = dishs.getJSONArray("categories");
        
        for (i = 0;i < dishArray.length();i ++) {
            tempCategory = new Category();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempCategory.setId(Integer.parseInt(dishObj.getString("id")));
            tempCategory.setName(dishObj.getString("name"));
            tempCategory.setDescription(dishObj.getString("description"));
            tempCategory.setSortOrder(Integer.parseInt(dishObj.getString("sort_order")));
            resultList.add(tempCategory);
        }
        return resultList;
    }

    public List<DishCategory> parseDishCategorysResp(String dishCategoryRespStr)
        throws JSONException {
        
        List<DishCategory> resultList = null;
        JSONObject dishs = new JSONObject(dishCategoryRespStr);
        resultList = new ArrayList<DishCategory>();
        
        int i;
        DishCategory tempDishCategory = null;
        JSONArray dishArray = dishs.getJSONArray("dish_categories");
        
        for (i = 0;i < dishArray.length();i ++) {
            tempDishCategory = new DishCategory();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempDishCategory.setDishId(Integer.parseInt(dishObj.getString("dish_id")));
            tempDishCategory.setCategoryId(Integer.parseInt(dishObj.getString("category_id")));
            resultList.add(tempDishCategory);
        }
        return resultList;
    }

    public List<Order> parseOrdersResp(String orderRespStr)
        throws JSONException {
        
        List<Order> resultList = null;
        JSONObject dishs = new JSONObject(orderRespStr);
        resultList = new ArrayList<Order>();
        
        int i;
        Order tempOrder = null;
        JSONArray dishArray = dishs.getJSONArray("orders");
        
        for (i = 0;i < dishArray.length();i ++) {
            tempOrder = new Order();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempOrder.setId(Integer.parseInt(dishObj.getString("_id")));
            tempOrder.setTableId(Integer.parseInt(dishObj.getString("table_id")));
            tempOrder.setStatus(Integer.parseInt(dishObj.getString("status")));
            tempOrder.setOrderTotal(Double.parseDouble(dishObj.getString("order_total")));
            tempOrder.setPaidOn(new Date(dishObj.getString("paid_on")));
            tempOrder.setCreatedOn(new Date(dishObj.getString("created_on")));
            resultList.add(tempOrder);
        }
        return resultList;
    }

    public List<OrderDetail> parseOrderDetailsResp(String orderDetailRespStr)
        throws JSONException {
        
        List<OrderDetail> resultList = null;
        JSONObject dishs = new JSONObject(orderDetailRespStr);
        resultList = new ArrayList<OrderDetail>();
        
        int i;
        OrderDetail tempOrderDetail = null;
        JSONArray dishArray = dishs.getJSONArray("order_details");
        
        for (i = 0;i < dishArray.length();i ++) {
            tempOrderDetail = new OrderDetail();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempOrderDetail.setId(Integer.parseInt(dishObj.getString("_id")));
            tempOrderDetail.setOrderId(Integer.parseInt(dishObj.getString("order_id")));
            tempOrderDetail.setOrderDetailId(Integer.parseInt(dishObj.getString("order_id")));
            tempOrderDetail.setDishId(Integer.parseInt(dishObj.getString("dish_id")));
            tempOrderDetail.setNumber(Integer.parseInt(dishObj.getString("number")));
            resultList.add(tempOrderDetail);
        }
        return resultList;
    }
    

    public List<DiningTable> parseDiningTablesResp(String diningTablesRespStr)
        throws JSONException {
        
        List<DiningTable> resultList = null;
        JSONObject dishs = new JSONObject(diningTablesRespStr);
        resultList = new ArrayList<DiningTable>();
        
        int i;
        DiningTable tempDiningTable = null;
        JSONArray dishArray = dishs.getJSONArray("dining_tables");
        
        for (i = 0;i < dishArray.length();i ++) {
            tempDiningTable = new DiningTable();
            JSONObject dishObj = dishArray.getJSONObject(i);
            tempDiningTable.setId(Integer.parseInt(dishObj.getString("_id")));
            tempDiningTable.setName(dishObj.getString("name"));
            tempDiningTable.setMaxPeople(Integer.parseInt(dishObj.getString("max_people")));
            int free = Integer.parseInt(dishObj.getString("status"));            
            tempDiningTable.setFree(free == 0 ? true : false);
            resultList.add(tempDiningTable);
        }
        return resultList;
    }
    

    public List<Config> parseConfigsResp(String configsRespStr)
        throws JSONException {
        
        List<Config> resultList = null;
        JSONObject dishs = new JSONObject(configsRespStr);
        resultList = new ArrayList<Config>();
        
        int i;
        Config tempConfig = null;
        JSONArray dishArray = dishs.getJSONArray("configs");
        
        for (i = 0;i < dishArray.length();i ++) {
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
