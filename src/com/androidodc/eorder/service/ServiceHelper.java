package com.androidodc.eorder.service;

import android.os.Bundle;
import android.os.Environment;
import com.androidodc.eorder.datatypes.Category;
import com.androidodc.eorder.datatypes.Config;
import com.androidodc.eorder.datatypes.DiningTable;
import com.androidodc.eorder.datatypes.Dish;
import com.androidodc.eorder.datatypes.DishCategory;
import com.androidodc.eorder.datatypes.Order;
import com.androidodc.eorder.engine.OrderDetail;
import com.androidodc.eorder.engine.ResponseParser;
import com.androidodc.eorder.engine.RequestHelper;
import com.androidodc.eorder.utils.LogUtils;
import org.json.JSONException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ServiceHelper {
    private static final String DEFAULT_URL = "http://10.15.5.237:8080/";
    private static final String DEFAULT_APP = "eOrder";
    private static final String CATEGORY_PAGE = "categories.jsp";
    private static final String DISH_PAGE = "dishes.jsp";
    private static final String DISH_CATEGORY_PAGE = "dish_category.jsp";
    private static final String ORDER_PAGE = "orders.jsp";
    private static final String ORDER_DETAIL_PAGE = "order_detail.jsp";
    private static final String DINING_TABLE_PAGE = "dining_tables.jsp";
    private static final String CONFIG_PAGE = "configs.jsp";
    private static final String SUBMIT_PAGE = "submit_order.jsp";
    private static final String STATUS_SUCCESS = "success";
    
    private static final String ORDER_QUERY_KEY = "status";
    private static final String ORDER_STATUS_FREE = "0";
    private static final String ORDER_ITEM_QUERY_KEY = "order_id";
    
    private RequestHelper reqHelper = null;
    private ResponseParser reqParser = null;    
    
    private static ServiceHelper instance = new ServiceHelper();
    
    private ServiceHelper() {
        reqHelper = new RequestHelper();
        reqParser = new ResponseParser();
    }
    
    public static ServiceHelper getInstance() {
        return instance;
    }
    
    public String getRequestUrl(String reqContext, String page) {
        if (reqContext == null) {
            reqContext = DEFAULT_URL + DEFAULT_APP;
        }
        return reqContext + "/" + page;
    }
    
    public boolean submitOrderToServer(String orderInfo) {
        boolean result = true;        
        String submitUrl = getRequestUrl(null, SUBMIT_PAGE);
        Bundle params = new Bundle();
        params.putString("orders", orderInfo);
        String resultStr = reqHelper.doPost(submitUrl, params);
        if (resultStr == null || !resultStr.equals(STATUS_SUCCESS)) {
            result = false;
        }
        return result;
    }
    
    public boolean syncDishImage(List<Dish> dishList) {
        boolean result = true;
        if (dishList == null) {
            return result;
        }
        
        try {
            for (Dish dish : dishList) {
                String imgUrl = getRequestUrl(null, dish.getImageServer());
                String imgName = dish.getImageServer().split("/", 2)[1];
                String filePath = getLocalFileStorageUrl("", imgName);
                OutputStream fos = new FileOutputStream(filePath);
                reqHelper.getFileFromServer(imgUrl, null, fos);
                dish.setImageLocal(filePath);
            }
        } catch (IOException e) {
            LogUtils.logD("Save image error!");
            result = false;
        }        
        return result;
    }
    
    public List<DiningTable> getDiningTables() {
        List<DiningTable> tableList = null;
        String reqUrl = getRequestUrl(null, DINING_TABLE_PAGE);
        
        String respStr = reqHelper.doPost(reqUrl, null);
        try {
            tableList = reqParser.parseDiningTables(respStr);
        } catch (JSONException e) {
            LogUtils.logD(e.getMessage());
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
        }
        return tableList;
    }

    public List<Category> getCategories() {
        List<Category> categoryList = null;
        String reqUrl = getRequestUrl(null, CATEGORY_PAGE);
        
        String respStr = reqHelper.doPost(reqUrl, null);
        try {
            categoryList = reqParser.parseCategories(respStr);
        } catch (JSONException e) {
            LogUtils.logD(e.getMessage());
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
        }
        return categoryList;
    }

    public List<Dish> getDishes() {
        List<Dish> dishList = null;
        String reqUrl = getRequestUrl(null, DISH_PAGE);
        
        String respStr = reqHelper.doPost(reqUrl, null);
        try {
            dishList = reqParser.parseDishes(respStr);
        } catch (JSONException e) {
            LogUtils.logD(e.getMessage());
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
        }
        return dishList;
    }

    public List<DishCategory> getDishCategory() {
        List<DishCategory> dishCategoryList = null;
        String reqUrl = getRequestUrl(null, DISH_CATEGORY_PAGE);
        
        String respStr = reqHelper.doPost(reqUrl, null);
        try {
            dishCategoryList = reqParser.parseDishCategory(respStr);
        } catch (JSONException e) {
            LogUtils.logD(e.getMessage());
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
        }
        return dishCategoryList;
    }


    public List<Order> getFreeOrders() {
        List<Order> orderList = null;
        String reqUrl = getRequestUrl(null, ORDER_PAGE);
        
        Bundle params = new Bundle();
        params.putString(ORDER_QUERY_KEY, ORDER_STATUS_FREE);
        String respStr = reqHelper.doPost(reqUrl, params);
        try {
            orderList = reqParser.parseOrders(respStr);
        } catch (JSONException e) {
            LogUtils.logD(e.getMessage());
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
        }
        return orderList;
    }
    
    public List<Order> getOrders() {
        List<Order> orderList = null;
        String reqUrl = getRequestUrl(null, ORDER_PAGE);
        
        String respStr = reqHelper.doPost(reqUrl, null);
        try {
            orderList = reqParser.parseOrders(respStr);
        } catch (JSONException e) {
            LogUtils.logD(e.getMessage());
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
        }
        return orderList;
    }

    public List<OrderDetail> getOrderDetail() {
        List<OrderDetail> orderDetailList = null;
        String reqUrl = getRequestUrl(null, ORDER_DETAIL_PAGE);
        
        String respStr = reqHelper.doPost(reqUrl, null);
        try {
            orderDetailList = reqParser.parseOrderDetail(respStr);
        } catch (JSONException e) {
            LogUtils.logD(e.getMessage());
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
        }
        return orderDetailList;
    }


    public List<OrderDetail> getOrderDetailByOrderIds(String orderIds) {
        List<OrderDetail> orderDetailList = null;
        String reqUrl = getRequestUrl(null, ORDER_DETAIL_PAGE);

        Bundle params = new Bundle();
        params.putString(ORDER_ITEM_QUERY_KEY, orderIds);
        String respStr = reqHelper.doPost(reqUrl, null);
        try {
            orderDetailList = reqParser.parseOrderDetail(respStr);
        } catch (JSONException e) {
            LogUtils.logD(e.getMessage());
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
        }
        return orderDetailList;
    }
    
    public List<Config> getConfigs() {
        List<Config> configList = null;
        String reqUrl = getRequestUrl(null, CONFIG_PAGE);
        
        String respStr = reqHelper.doPost(reqUrl, null);
        try {
            configList = reqParser.parseConfigs(respStr);
        } catch (JSONException e) {
            LogUtils.logD(e.getMessage());
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
        }
        return configList;
    }
    
    public String getLocalFileStorageUrl(String dir, String fileName) {
        String url = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (fileName == null) {
                return null;
            }
            
            if (dir == null || dir.equals("")) {
                dir = "";
            } else {
                dir = dir + "/";
            }
            
            url = Environment.getExternalStorageDirectory() + "/" + dir + fileName;            
        } else {
            LogUtils.logD("SD Card not mount!");
        }
        return url;
    }
}
