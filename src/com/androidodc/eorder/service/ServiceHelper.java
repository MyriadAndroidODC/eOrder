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
import com.androidodc.eorder.engine.RequestHelper;
import com.androidodc.eorder.engine.ResponseParser;
import com.androidodc.eorder.utils.LogUtils;

import org.json.JSONException;

import java.util.ArrayList;

public class ServiceHelper {
    // TODO these urls should be added in a single file and supply UI for user to config
    public static final String IMAGE_STORAGE_DEFAULT_DIR = Environment.DIRECTORY_PICTURES + "/eOrder";
    private static final String DEFAULT_URL = "http://10.15.5.125:8080/";
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
//    private static final String ORDER_ITEM_QUERY_KEY = "order_id";

    public static String getRequestUrl(String reqContext, String page) {
        if (reqContext == null) {
            reqContext = DEFAULT_URL + DEFAULT_APP;
        }
        return reqContext + '/' + page;
    }

    public static boolean submitOrderToServer(String orderInfo) {
        String submitUrl = getRequestUrl(null, SUBMIT_PAGE);
        Bundle params = new Bundle();
        params.putString("orders", orderInfo);
        String resultStr = RequestHelper.doRequestPost(submitUrl, params);
        if (!STATUS_SUCCESS.equals(resultStr)) {
            return false;
        }
        return true;
    }

    public static void syncDishImage(ArrayList<Dish> dishList) {
        if (dishList == null) {
            return;
        }
        for (Dish dish : dishList) {
            String imgUrl = getRequestUrl(null, dish.getImageServer());
            String imgName = dish.getImageServer().split("/", 2)[1];
            String filePath = getLocalFileStorageUrl("", imgName);
            RequestHelper.getFileFromServer(imgUrl, null, filePath);
            dish.setImageLocal(filePath);
        }
    }

    public static ArrayList<DiningTable> getDiningTables() {
        String reqUrl = getRequestUrl(null, DINING_TABLE_PAGE);
        String respStr = RequestHelper.doRequestPost(reqUrl, null);
        try {
           return ResponseParser.parseDiningTables(respStr);
        } catch (JSONException e) {
            LogUtils.logE(e.getMessage());
        } catch (Exception e) {
            LogUtils.logE(e.getMessage());
        }
        return null;
    }

    public static ArrayList<Category> getCategories() {
        String reqUrl = getRequestUrl(null, CATEGORY_PAGE);
        String respStr = RequestHelper.doRequestPost(reqUrl, null);
        try {
            return ResponseParser.parseCategories(respStr);
        } catch (JSONException e) {
            LogUtils.logE(e.getMessage());
        } catch (Exception e) {
            LogUtils.logE(e.getMessage());
        }
        return null;
    }

    public static ArrayList<Dish> getDishes() {
        String reqUrl = getRequestUrl(null, DISH_PAGE);
        String respStr = RequestHelper.doRequestPost(reqUrl, null);
        try {
            return ResponseParser.parseDishes(respStr);
        } catch (JSONException e) {
            LogUtils.logE(e.getMessage());
        } catch (Exception e) {
            LogUtils.logE(e.getMessage());
        }
        return null;
    }

    public static ArrayList<DishCategory> getDishCategory() {
        String reqUrl = getRequestUrl(null, DISH_CATEGORY_PAGE);
        String respStr = RequestHelper.doRequestPost(reqUrl, null);
        try {
            return ResponseParser.parseDishCategory(respStr);
        } catch (JSONException e) {
            LogUtils.logE(e.getMessage());
        } catch (Exception e) {
            LogUtils.logE(e.getMessage());
        }
        return null;
    }

    public static ArrayList<Order> getFreeOrders() {
        String reqUrl = getRequestUrl(null, ORDER_PAGE);
        Bundle params = new Bundle();
        params.putString(ORDER_QUERY_KEY, ORDER_STATUS_FREE);
        String respStr = RequestHelper.doRequestPost(reqUrl, params);
        try {
            return ResponseParser.parseOrders(respStr);
        } catch (JSONException e) {
            LogUtils.logE(e.getMessage());
        } catch (Exception e) {
            LogUtils.logE(e.getMessage());
        }
        return null;
    }

    public static ArrayList<Order> getOrders() {
        String reqUrl = getRequestUrl(null, ORDER_PAGE);
        String respStr = RequestHelper.doRequestPost(reqUrl, null);
        try {
            return ResponseParser.parseOrders(respStr);
        } catch (JSONException e) {
            LogUtils.logE(e.getMessage());
        } catch (Exception e) {
            LogUtils.logE(e.getMessage());
        }
        return null;
    }

    public static ArrayList<OrderDetail> getOrderDetail() {
        String reqUrl = getRequestUrl(null, ORDER_DETAIL_PAGE);
        String respStr = RequestHelper.doRequestPost(reqUrl, null);
        try {
            return ResponseParser.parseOrderDetail(respStr);
        } catch (JSONException e) {
            LogUtils.logE(e.getMessage());
        } catch (Exception e) {
            LogUtils.logE(e.getMessage());
        }
        return null;
    }

    public static ArrayList<OrderDetail> getOrderDetailByOrderIds() {
        String reqUrl = getRequestUrl(null, ORDER_DETAIL_PAGE);
        String respStr = RequestHelper.doRequestPost(reqUrl, null);
        try {
            return ResponseParser.parseOrderDetail(respStr);
        } catch (JSONException e) {
            LogUtils.logE(e.getMessage());
        } catch (Exception e) {
            LogUtils.logE(e.getMessage());
        }
        return null;
    }

    public static ArrayList<Config> getConfigs() {
        String reqUrl = getRequestUrl(null, CONFIG_PAGE);
        String respStr = RequestHelper.doRequestPost(reqUrl, null);
        try {
            return ResponseParser.parseConfigs(respStr);
        } catch (JSONException e) {
            LogUtils.logE(e.getMessage());
        } catch (Exception e) {
            LogUtils.logE(e.getMessage());
        }
        return null;
    }

    public static String getLocalFileStorageUrl(String dir, String fileName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (fileName == null) {
                return null;
            }
            if (dir == null || dir.equals("")) {
                return Environment.getExternalStorageDirectory() + ('/' + IMAGE_STORAGE_DEFAULT_DIR + '/' + fileName);
            } else {
                return Environment.getExternalStorageDirectory() + ('/' + dir + '/' + fileName);
            }
        } else {
            LogUtils.logE("SD Card not mount!");
        }
        return null;
    }
}
