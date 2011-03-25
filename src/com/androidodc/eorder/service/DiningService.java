package com.androidodc.eorder.service;

import android.app.Service; 
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import com.androidodc.eorder.database.DatabaseHelper;
import com.androidodc.eorder.datatypes.Category;
import com.androidodc.eorder.datatypes.Config;
import com.androidodc.eorder.datatypes.DiningTable;
import com.androidodc.eorder.datatypes.Dish;
import com.androidodc.eorder.datatypes.DishCategory;
import com.androidodc.eorder.datatypes.Order;
import com.androidodc.eorder.datatypes.OrderItem;
import com.androidodc.eorder.engine.OrderDetail;
import com.androidodc.eorder.utils.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;

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
    public static final String PARAM_INTENT_KEY = "param_intent";
    
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
    private DatabaseHelper dbHelper = DatabaseHelper.getInstance();

    @Override  
    public void onCreate() {  
        super.onCreate();
    }
    
    @Override  
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        final int commandType = intent.getIntExtra(SERVICE_COMMAND_KEY, COMMAND_BLANK);
        final Intent executeParamIntent = intent;

        Bundle taskParams = new Bundle();
        taskParams.putInt(SERVICE_COMMAND_KEY, commandType);
        taskParams.putParcelable(PARAM_INTENT_KEY, executeParamIntent);
        new AsyncTask<Bundle, Void, Void>(){
            @Override
            protected Void doInBackground(Bundle... objs) {
                try {
                    executeCommand(commandType, executeParamIntent);
                } catch (Exception e) {
                    LogUtils.logD(e.getMessage());
                }
                return null;
            }
        }.execute(new Bundle[]{taskParams});
    }
    
    private void executeCommand(int commandType, Intent intent) {
        /*try{
            if (dbHelper == null) {
                DatabaseHelper.init(this.getApplicationContext());
                dbHelper = DatabaseHelper.getInstance();
            }
        } catch (Exception e) {
            LogUtils.logD("Database initialize error! \n" + e.getMessage());
        }*/
        boolean opSymbol = true;
        if (commandType == COMMAND_SYNC_DINING_TABLE) {
            HashMap diningTableMap = new HashMap();
            ArrayList<DiningTable> diningTableList = serviceHelper.getDiningTables();
            if (diningTableList != null) {
                diningTableMap.put(DINING_TABLE_KEY, diningTableList);
                sendMsg(SYNC_DINING_TABLE, EXECUTE_DINING_TABLE_SUCCESS, diningTableMap);
            } else {
                sendMsg(SYNC_DINING_TABLE, EXECUTE_ERROR, null);
            }
            
        } else if (commandType == COMMAND_SYNC_ORDER) {
            /*if (dbHelper == null) {
                sendMsg(SYNC_HISTORY_ORDER, EXECUTE_ERROR, null);
                return;
            }*/
        	ArrayList<Order> orderList = serviceHelper.getFreeOrders();
            StringBuffer orderIdBuffer = new StringBuffer("");
            for (Order order : orderList) {
                orderIdBuffer.append(order.getOrderId() + ",");
            }
            orderIdBuffer.deleteCharAt(orderIdBuffer.length() - 1); //delete the last character
            ArrayList<OrderDetail> orderDetailList = serviceHelper.getOrderDetailByOrderIds(orderIdBuffer.toString());
            
            if (orderList != null && orderDetailList != null) {
                HashMap orderMap = new HashMap();
                HashMap orderTableMap = new HashMap();
                orderMap.put(ORDER_KEY, orderList);
                
                for (OrderDetail orderDetail : orderDetailList) {
                    long orderId = orderDetail.getOrderId();
                    long dishId = orderDetail.getDishId();
                    long tableId = orderDetail.getTableId();
                    int number = orderDetail.getNumber();
                    String key = "" + orderId;
                    ArrayList<OrderItem> eachOrderItemList = (ArrayList<OrderItem>)orderMap.get(key);
                    
                    OrderItem orderItem = new OrderItem();
                    orderItem.setAmount(number);
                    orderItem.setDish(dbHelper.getDishById(dishId));
                    if (null == eachOrderItemList) {
                        eachOrderItemList = new ArrayList();
                        eachOrderItemList.add(orderItem);
                        orderMap.put(key, eachOrderItemList);
                    } else {
                        eachOrderItemList.add(orderItem);
                    }
                    
                    String tableKey = "" + tableId;
                    if (orderTableMap.get(tableKey) == null) {
                        orderTableMap.put(key, tableKey);
                    }
                }
                
                for (Order order : orderList) {
                    long orderId = order.getOrderId();
                    String key = "" + orderId;
                    ArrayList<OrderItem> eachOrderItemList = (ArrayList<OrderItem>)orderMap.get(key);
                    order.setOrderItems(eachOrderItemList);

                    String tableIdStr = (String)orderTableMap.get(key);
                    if (tableIdStr != null) {
                        Long tableId = Long.parseLong(tableIdStr);
                        order.setTableId(tableId);
                    }
                    
                }
                sendMsg(SYNC_HISTORY_ORDER, EXECUTE_ORDER_SUCCESS, orderMap);
            } else {
                sendMsg(SYNC_HISTORY_ORDER, EXECUTE_ERROR, null);
            }
            
        } else if (commandType == COMMAND_SYNC_OTHER) {
            /*if (dbHelper == null) {
                sendMsg(null, EXECUTE_ERROR, null);
                return;
            }*/
            try{            
                opSymbol = (opSymbol == true ? synCategories() : false);
                opSymbol = (opSymbol == true ? synDishCategory() : false);
                opSymbol = (opSymbol == true ? synDishesAndImages() : false);
                //opSymbol = (opSymbol == true ? synConfigs() : false); // TODO
            } catch (Exception e) {
                LogUtils.logD("Synchronize other information error! \n" + e.getMessage());
                opSymbol = false;
            }
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
            ArrayList<OrderDetail> orderDetailList = (ArrayList<OrderDetail>)orderMap.get(SUBMIT_ORDER_DETAIL_KEY);
            
            /*List<OrderItem> orderItemList = order.getOrderItems();
            for (OrderItem orderItem : orderItemList) {
                submitStr.append("{");                
                submitStr.append("\"dining_table_id\":" + order.getTableId() + ",");
                submitStr.append("\"dish_id\":" + orderItem.getDish().getDishId() + ",");
                submitStr.append("\"number\":" + orderItem.getAmount());
                submitStr.append("},");
            }*/
            
            submitStr.append("{\"sum\":");
            submitStr.append(order.getOrderTotal() + ",");
            submitStr.append("\"order_detail\":[");
            for (OrderDetail eachOrderDetail : orderDetailList) {
                submitStr.append("{");                
                submitStr.append("\"dining_table_id\":" + eachOrderDetail.getTableId() + ",");
                submitStr.append("\"dish_id\":" + eachOrderDetail.getDishId() + ",");
                submitStr.append("\"number\":" + eachOrderDetail.getNumber());
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
        ArrayList<Category> categoryList = serviceHelper.getCategories();
        if (categoryList == null) {
            result = false;
        } else {
            for (Category category : categoryList) {
                dbHelper.addCategory(category);
            }
        }
        return result;
    }

    private boolean synDishesAndImages() {
        boolean result = true;
        ArrayList<Dish> dishList = serviceHelper.getDishes();
        if (dishList == null) {
            result = false;
        } else {            
            //synchronize the image and update the local file path
            result = serviceHelper.syncDishImage(dishList);
            for (Dish dish : dishList) {
                dbHelper.addDish(dish);
            }
        }
        return result;
    }
    
    private boolean synDishCategory() {
        boolean result = true;
        ArrayList<DishCategory> dishCategoryList = serviceHelper.getDishCategory();
        if (dishCategoryList == null) {
            result = false;
        } else {
            for (DishCategory dishCategory : dishCategoryList) {
                dbHelper.addDishCategory(dishCategory);
            }
        }
        return result;
    }

    private boolean synConfigs() {
        boolean result = true;
        ArrayList<Config> configList = serviceHelper.getConfigs();
        if (configList == null) {
            result = false;
        } else {
            for (Config config : configList) {
                dbHelper.addConfig(config);
            }
        }
        return result;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}