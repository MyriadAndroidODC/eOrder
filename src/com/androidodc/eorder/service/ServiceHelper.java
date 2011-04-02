package com.androidodc.eorder.service;

import android.os.Bundle;
import android.os.Environment;

import org.json.JSONException;

import com.androidodc.eorder.datatypes.Category;
import com.androidodc.eorder.datatypes.Config;
import com.androidodc.eorder.datatypes.DiningTable;
import com.androidodc.eorder.datatypes.Dish;
import com.androidodc.eorder.datatypes.DishCategory;
import com.androidodc.eorder.datatypes.Order;
import com.androidodc.eorder.engine.OrderDetail;
import com.androidodc.eorder.engine.RequestHelper;
import com.androidodc.eorder.engine.ResponseParser;
import com.androidodc.eorder.utils.LogUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

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
    private static final String SUBMIT_PAGE = "post_order.jsp";
    private static final String STATUS_SUCCESS = "success";

    private static final String ORDER_QUERY_KEY = "status";
    private static final String ORDER_STATUS_FREE = "0";
    private static final String ORDER_ITEM_QUERY_KEY = "order_id";

    public static String getRequestUrl(String reqContext, String page) {
        if (reqContext == null) {
            reqContext = DEFAULT_URL + DEFAULT_APP;
        }
        return reqContext + "/" + page;
    }

    public static boolean submitOrderToServer(String orderInfo) {
        boolean result = true;
        String submitUrl = getRequestUrl(null, SUBMIT_PAGE);
        Bundle params = new Bundle();
        params.putString("orders", orderInfo);
        String resultStr = RequestHelper.doRequestPost(submitUrl, params);
        LogUtils.logE("Submit order result:-----------------------------&*&*&(&*(&*(-------"
                + resultStr);
        LogUtils.logE("local result:-----------------------------&*&*&(&*(&*(-------"
                + STATUS_SUCCESS);
        if (resultStr == null || resultStr.indexOf(STATUS_SUCCESS) < 0) {
            result = false;
        }
        return result;
    }

    public static boolean syncDishImage(ArrayList<Dish> dishList) {
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
                RequestHelper.getFileFromServer(imgUrl, null, fos);
                dish.setImageLocal(filePath);
            }
        } catch (IOException e) {
            LogUtils.logD("Save image error!");
            result = false;
        }
        return result;
    }

    public static ArrayList<DiningTable> getDiningTables() {
        ArrayList<DiningTable> tableList = null;
        String reqUrl = getRequestUrl(null, DINING_TABLE_PAGE);
        String respStr = RequestHelper.doRequestPost(reqUrl, null);
        try {
            tableList = ResponseParser.parseDiningTables(respStr);
        } catch (JSONException e) {
            LogUtils.logD(e.getMessage());
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
        }
        return tableList;
    }

    public static ArrayList<Category> getCategories() {
        ArrayList<Category> categoryList = null;
        String reqUrl = getRequestUrl(null, CATEGORY_PAGE);
        String respStr = RequestHelper.doRequestPost(reqUrl, null);
        try {
            categoryList = ResponseParser.parseCategories(respStr);
        } catch (JSONException e) {
            LogUtils.logD(e.getMessage());
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
        }
        return categoryList;
    }

    public static ArrayList<Dish> getDishes() {
        ArrayList<Dish> dishList = null;
        String reqUrl = getRequestUrl(null, DISH_PAGE);
        String respStr = RequestHelper.doRequestPost(reqUrl, null);
        try {
            dishList = ResponseParser.parseDishes(respStr);
        } catch (JSONException e) {
            LogUtils.logD(e.getMessage());
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
        }
        return dishList;
    }

    public static ArrayList<DishCategory> getDishCategory() {
        ArrayList<DishCategory> dishCategoryList = null;
        String reqUrl = getRequestUrl(null, DISH_CATEGORY_PAGE);
        String respStr = RequestHelper.doRequestPost(reqUrl, null);
        try {
            dishCategoryList = ResponseParser.parseDishCategory(respStr);
        } catch (JSONException e) {
            LogUtils.logD(e.getMessage());
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
        }
        return dishCategoryList;
    }

    public static ArrayList<Order> getFreeOrders() {
        ArrayList<Order> orderList = null;
        String reqUrl = getRequestUrl(null, ORDER_PAGE);
        Bundle params = new Bundle();
        params.putString(ORDER_QUERY_KEY, ORDER_STATUS_FREE);
        String respStr = RequestHelper.doRequestPost(reqUrl, params);
        try {
            orderList = ResponseParser.parseOrders(respStr);
        } catch (JSONException e) {
            LogUtils.logD(e.getMessage());
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
        }
        return orderList;
    }

    public static ArrayList<Order> getOrders() {
        ArrayList<Order> orderList = null;
        String reqUrl = getRequestUrl(null, ORDER_PAGE);
        String respStr = RequestHelper.doRequestPost(reqUrl, null);
        try {
            orderList = ResponseParser.parseOrders(respStr);
        } catch (JSONException e) {
            LogUtils.logD(e.getMessage());
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
        }
        return orderList;
    }

    public static ArrayList<OrderDetail> getOrderDetail() {
        ArrayList<OrderDetail> orderDetailList = null;
        String reqUrl = getRequestUrl(null, ORDER_DETAIL_PAGE);
        String respStr = RequestHelper.doRequestPost(reqUrl, null);
        try {
            orderDetailList = ResponseParser.parseOrderDetail(respStr);
        } catch (JSONException e) {
            LogUtils.logD(e.getMessage());
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
        }
        return orderDetailList;
    }

    public static ArrayList<OrderDetail> getOrderDetailByOrderIds(String orderIds) {
        ArrayList<OrderDetail> orderDetailList = null;
        String reqUrl = getRequestUrl(null, ORDER_DETAIL_PAGE);
        Bundle params = new Bundle();
        params.putString(ORDER_ITEM_QUERY_KEY, orderIds);
        String respStr = RequestHelper.doRequestPost(reqUrl, null);
        try {
            orderDetailList = ResponseParser.parseOrderDetail(respStr);
        } catch (JSONException e) {
            LogUtils.logD(e.getMessage());
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
        }
        return orderDetailList;
    }

    public static ArrayList<Config> getConfigs() {
        ArrayList<Config> configList = null;
        String reqUrl = getRequestUrl(null, CONFIG_PAGE);
        String respStr = RequestHelper.doRequestPost(reqUrl, null);
        try {
            configList = ResponseParser.parseConfigs(respStr);
        } catch (JSONException e) {
            LogUtils.logD(e.getMessage());
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
        }
        return configList;
    }

    public static String getLocalFileStorageUrl(String dir, String fileName) {
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
