package com.androidodc.eorder.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.androidodc.eorder.database.DatabaseHelper;
import com.androidodc.eorder.datatypes.Category;
import com.androidodc.eorder.datatypes.Config;
import com.androidodc.eorder.datatypes.DiningTable;
import com.androidodc.eorder.datatypes.Dish;
import com.androidodc.eorder.datatypes.DishCategory;
import com.androidodc.eorder.datatypes.Order;
import com.androidodc.eorder.datatypes.OrderDetail;
import com.androidodc.eorder.utils.LogUtils;
import android.app.Service; 
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.SparseArray;

public class DiningService extends Service {    
    public static final String SYNC_DINING_TABLE = "com.androidodc.intent.SYNC_TABLES_STATUS";
    public static final String SYNC_HISTORY_ORDER = "com.androidodc.intent.SYNC_HISTORY_ORDER";
    public static final String SUBMIT_ORDER = "com.androidodc.intent.SUBMIT_ORDER";
    
    public static final String BROADCAST_RESULT_KEY = "broadcast_result";
    public static final String BROADCAST_RESOURCE_KEY = "broadcast_resource";
    public static final String DINING_TABLE_KEY = "dining_tables";
    public static final String ORDER_KEY = "dining_tables";
    public static final String SERVICE_COMMAND_KEY = "command_type";
    public static final String SUBMIT_KEY = "submit_info";
    public static final String SUBMIT_ORDER_KEY = "submit_order";
    public static final String SUBMIT_ORDER_DETAIL_KEY = "submit_order_detail";
    
    public static final int COMMAND_BLANK = 0;
    public static final int COMMAND_SYNC_DINING_TABLE = 1;
    public static final int COMMAND_SYNC_ORDER = 2;
    public static final int COMMAND_SYNC_OTHER = 3; //Except order and dining table information
    public static final int COMMAND_SUBMIT_ORDER = 4;
    
    public static final int EXECUTE_NONE = 0;
    public static final int EXECUTE_DINING_TABLE_SUCCESS = 1;
    public static final int EXECUTE_ORDER_SUCCESS = 2;
    public static final int EXECUTE_OTHER_SUCCESS = 3;
    public static final int EXECUTE_SUBMIT_ORDER_SUCCESS = 4;
    public static final int EXECUTE_ERROR = 99999;
    
    private ServiceHelper serviceHelper = ServiceHelper.getInstance();
    private DatabaseHelper dbHelper = null;

    @Override  
    public void onCreate() {  
        super.onCreate();
    }
    
    @Override  
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        final int commandType = intent.getIntExtra(SERVICE_COMMAND_KEY, COMMAND_BLANK);
        final Intent executeParamIntent = intent;

        new AsyncTask(){
            @Override
            protected ArrayList doInBackground(Object... objs) {
                try {
                    executeCommand(commandType, executeParamIntent);
                } catch (Exception e) {
                    LogUtils.logD(e.getMessage());
                }
                return null;
            }
        }.execute(null);
    }
    
    private void executeCommand(int commandType, Intent intent) {
        boolean opSymbol = true;
        if (commandType == COMMAND_SYNC_DINING_TABLE) {
            HashMap diningTableMap = new HashMap();
            List<DiningTable> diningTableList = serviceHelper.getDiningTables();
            if (diningTableList != null) {
                diningTableMap.put(DINING_TABLE_KEY, diningTableList);
                sendMsg(SYNC_DINING_TABLE, EXECUTE_DINING_TABLE_SUCCESS, diningTableMap);
            } else {
                sendMsg(SYNC_DINING_TABLE, EXECUTE_ERROR, null);
            }
            
        } else if (commandType == COMMAND_SYNC_ORDER) {
            List<Order> orderList = serviceHelper.getOrders();
            List<OrderDetail> detailList = serviceHelper.getOrderDetail();
            
            if (orderList != null && detailList != null) {
                HashMap orderMap = new HashMap();
                orderMap.put(ORDER_KEY, orderList);
                
                for (OrderDetail orderDetail : detailList) {
                    int orderId = orderDetail.getOrderId();
                    String key = "" + orderId;
                    List eachDetailList = (List)orderMap.get(key);
                    
                    if (null == eachDetailList) {
                        eachDetailList = new ArrayList();
                        eachDetailList.add(orderDetail);
                        orderMap.put(key, eachDetailList);
                    } else {
                        eachDetailList.add(orderDetail);
                    }
                }
                sendMsg(SYNC_HISTORY_ORDER, EXECUTE_ORDER_SUCCESS, orderMap);
            } else {
                sendMsg(SYNC_HISTORY_ORDER, EXECUTE_ERROR, null);
            }
            
        } else if (commandType == COMMAND_SYNC_OTHER) {
            if (dbHelper == null) {
                DatabaseHelper.init(this.getApplicationContext());
                dbHelper = DatabaseHelper.getInstance();
            }
            opSymbol = (opSymbol == true ? synCategories() : false);
            opSymbol = (opSymbol == true ? synDishes() : false);
            opSymbol = (opSymbol == true ? synDishCategory() : false);
            //opSymbol = (opSymbol == true ? synConfigs() : false); // TODO
            sendMsg(null, opSymbol == true ? EXECUTE_OTHER_SUCCESS : EXECUTE_ERROR, null);
        } else if (commandType == COMMAND_SUBMIT_ORDER) {
            HashMap submitOrderMap = (HashMap)intent.getSerializableExtra(SUBMIT_KEY);
            opSymbol = submitOrder(submitOrderMap);
            sendMsg(SUBMIT_ORDER, opSymbol == true ? EXECUTE_SUBMIT_ORDER_SUCCESS : EXECUTE_ERROR, null);
        } else {
            sendMsg(null, EXECUTE_NONE, null);
        }
    }
    
    private void sendMsg(String broadcastHandle, int executeResult, HashMap resultObj) {
        if (broadcastHandle == null) {
            return; // do nothing
        }
        
        Intent intent = new Intent(broadcastHandle);
        Bundle msgBundle = new Bundle();
        
        msgBundle.putInt(BROADCAST_RESULT_KEY, executeResult);
        msgBundle.putSerializable(BROADCAST_RESOURCE_KEY, resultObj);
        intent.putExtras(msgBundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.sendBroadcast(intent);  
    } 
    
    private boolean submitOrder(HashMap orderMap) {
        boolean result = true;
        try{
            StringBuffer submitStr = new StringBuffer("");
            Order order = (Order)orderMap.get(SUBMIT_ORDER_KEY);
            List<OrderDetail> orderDetailList = (List<OrderDetail>)orderMap.get(SUBMIT_ORDER_DETAIL_KEY);
            
            submitStr.append("{\"sum\":");
            submitStr.append(order.getOrderTotal() + ",");
            submitStr.append("\"order_detail\":[");
            for (OrderDetail eachOrderDetail : orderDetailList) {
                submitStr.append("{");                
                submitStr.append("\"dining_table_id\":" + eachOrderDetail.getTableId() + ",");
                submitStr.append("\"dish_id\":" + eachOrderDetail.getDishId() + ",");
                submitStr.append("\"number\":" + eachOrderDetail.getDishId());
                submitStr.append("},");
            }
            submitStr.deleteCharAt(submitStr.length() - 1);
            submitStr.append("]}");
            serviceHelper.submitOrderToServer(submitStr.toString());
        } catch (Exception e) {
            LogUtils.logD("Submit order error! \n" + e.getMessage());
            result = false;
        }
        return result;
    }
    
    private boolean synCategories() {
        boolean result = true;
        List<Category> categoryList = serviceHelper.getCategories();
        for (Category category : categoryList) {
            dbHelper.addCategory(category);
        }
        return result;
    }

    private boolean synDishes() {
        boolean result = true;
        List<Dish> dishList = serviceHelper.getDishes();
        for (Dish dish : dishList) {
            dbHelper.addDish(dish);
        }
        return result;
    }
    
    private boolean synDishCategory() {
        boolean result = true;
        List<DishCategory> dishCategoryList = serviceHelper.getDishCategory();
        for (DishCategory dishCategory : dishCategoryList) {
            dbHelper.addDishCategory(dishCategory);
        }
        return result;
    }

    private boolean synConfigs() {
        boolean result = true;
        List<Config> configList = serviceHelper.getConfigs();
        for (Config config : configList) {
            dbHelper.addConfig(config);
        }
        return result;
    }
    
    @Override  
    public void onDestroy() {  
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
