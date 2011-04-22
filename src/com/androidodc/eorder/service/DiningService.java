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
import java.util.List;

public class DiningService extends Service {
    public static final String SYNC_DINING_TABLE = "com.androidodc.intent.SYNC_TABLES_STATUS"; //For the receiver to handle the dining table status
    public static final String SYNC_HISTORY_ORDER = "com.androidodc.intent.SYNC_HISTORY_ORDER"; //For the receiver to handle the history orders
    public static final String SUBMIT_ORDER = "com.androidodc.intent.SUBMIT_ORDER"; //For the receiver to get the submit result.
    public static final String SYNC_SERVER_DATA = "com.androidodc.intent.SYNC_SERVER_DATA";
    public static final String SYNC_TABLE_LASTEST_ORDER = "com.androidodc.intent.TABLE_LASTEST_ORDER";
    
    public static final String BROADCAST_RESULT_KEY = "broadcast_result"; //broadcast parameter key for return the operation status
    public static final String BROADCAST_RESOURCE_KEY = "broadcast_resource"; //broadcast parameter key for return the result object to receiver
    public static final String DINING_TABLE_KEY = "dining_tables"; //Service operation key to return the dining table objects. 
    public static final String ORDER_KEY = "orders"; //Service operation key to return the dining table objects.
    public static final String SERVICE_COMMAND_KEY = "command_type"; //Service operation key to get the operation command type.
    public static final String SUBMIT_KEY = "submit_info"; //Service operation key to get the submit information objects. 
    public static final String SUBMIT_ORDER_KEY = "submit_order"; //Service operation key to get the submit orders.
    public static final String SUBMIT_ORDER_DETAIL_KEY = "submit_order_detail"; //Service operation key to get the submit orders details.
    public static final String PARAM_INTENT_KEY = "param_intent"; //Service operation key to transfer the intent to asynctask.
    public static final String CURRENT_TABLE_ID = "current_table_id";
    
    public static final int COMMAND_BLANK = 0;
    public static final int COMMAND_SYNC_DINING_TABLE = 1;
    public static final int COMMAND_SYNC_ORDER = 2;
    public static final int COMMAND_SYNC_SERVER = 3; //Except order and dining table information
    public static final int COMMAND_SUBMIT_ORDER = 4;
    public static final int COMMAND_TABLE_LASTEST_ORDER = 5;
    
    public static final int EXECUTE_NONE = 0;
    public static final int EXECUTE_DINING_TABLE_SUCCESS = 1;
    public static final int EXECUTE_ORDER_SUCCESS = 2;
    public static final int EXECUTE_OTHER_SUCCESS = 3;
    public static final int EXECUTE_SUBMIT_ORDER_SUCCESS = 4;
    public static final int EXECUTE_ERROR = 99999;

    private DatabaseHelper dbHelper = DatabaseHelper.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Bundle taskParams = new Bundle();
        taskParams.putParcelable(PARAM_INTENT_KEY, intent);

        new AsyncTask<Bundle, Void, Void>(){
            @Override
            protected Void doInBackground(Bundle... objs) {
                try {
                	Intent executeParamIntent = objs[0].getParcelable(PARAM_INTENT_KEY);
                	int commandType = executeParamIntent.getIntExtra(SERVICE_COMMAND_KEY, COMMAND_BLANK);
                    executeCommand(commandType, executeParamIntent);
                } catch (Exception e) {
                    LogUtils.logE(e.getMessage());
                }
                return null;
            }
        }.execute(new Bundle[]{taskParams});
    }

    void executeCommand(int commandType, Intent intent) {
        boolean opSymbol = true;
        if (commandType == COMMAND_SYNC_DINING_TABLE) {
            HashMap<String, ArrayList<DiningTable>> diningTableMap = new HashMap<String, ArrayList<DiningTable>>();
            ArrayList<DiningTable> diningTableList = ServiceHelper.getDiningTables();
            if (diningTableList != null) {
                diningTableMap.put(DINING_TABLE_KEY, diningTableList);
                sendMsg(SYNC_DINING_TABLE, EXECUTE_DINING_TABLE_SUCCESS, diningTableMap, COMMAND_SYNC_DINING_TABLE);
            } else {
                sendMsg(SYNC_DINING_TABLE, EXECUTE_ERROR, null, COMMAND_BLANK);
            }
        } else if (commandType == COMMAND_SYNC_ORDER) {
            ArrayList<Order> orderList = ServiceHelper.getFreeOrders();
            ArrayList<OrderDetail> orderDetailList = ServiceHelper.getOrderDetailByOrderIds();

            if (orderList != null && orderDetailList != null) {
                HashMap<Long, ArrayList<OrderItem>> orderItemMap = new HashMap<Long, ArrayList<OrderItem>>();
                HashMap<Long, Long> orderTableMap = new HashMap<Long, Long>();

                for (OrderDetail orderDetail : orderDetailList) {
                    long orderId = orderDetail.getOrderId();
                    long dishId = orderDetail.getDishId();
                    long tableId = orderDetail.getTableId();
                    int number = orderDetail.getNumber();

                    OrderItem orderItem = new OrderItem();
                    orderItem.setAmount(number);
                    orderItem.setDish(dbHelper.getDishById(dishId));

                    ArrayList<OrderItem> eachOrderItemList = (ArrayList<OrderItem>) orderItemMap
                            .get(orderId);
                    if (null == eachOrderItemList) {
                        eachOrderItemList = new ArrayList<OrderItem>();
                        eachOrderItemList.add(orderItem);
                        orderItemMap.put(orderId, eachOrderItemList);
                    } else {
                        eachOrderItemList.add(orderItem);
                    }

                    orderTableMap.put(orderId, tableId);
                }

                for (Order order : orderList) {
                    long orderId = order.getOrderId();
                    ArrayList<OrderItem> eachOrderItemList = (ArrayList<OrderItem>) orderItemMap
                            .get(orderId);
                    order.setOrderItems(eachOrderItemList);

                    Long tableId = orderTableMap.get(orderId);
                    if (tableId != null) {
                        order.setTableId(tableId);
                    }
                }

                HashMap<String, ArrayList<Order>> orderMap = new HashMap<String, ArrayList<Order>>();
                orderMap.put(ORDER_KEY, orderList);
                sendMsg(SYNC_HISTORY_ORDER, EXECUTE_ORDER_SUCCESS, orderMap, COMMAND_SYNC_ORDER);
            } else {
                sendMsg(SYNC_HISTORY_ORDER, EXECUTE_ERROR, null, COMMAND_BLANK);
            }
        } else if (commandType == COMMAND_SYNC_SERVER) {
            try {
                dbHelper.deleteAllTableDatas();
                opSymbol = syncCategories();
                opSymbol = opSymbol ? syncDishCategory() : false;
                opSymbol = opSymbol ? syncDishesAndImages() : false;
            } catch (Exception e) {
                LogUtils.logE("Synchronize other information error! \n" + e.getMessage());
                opSymbol = false;
            }
            sendMsg(SYNC_SERVER_DATA, opSymbol == true ? EXECUTE_OTHER_SUCCESS : EXECUTE_ERROR, null, COMMAND_BLANK);
        } else if (commandType == COMMAND_SUBMIT_ORDER) {
            Order order = (Order) intent.getSerializableExtra(SUBMIT_ORDER_KEY);
            @SuppressWarnings("unchecked")
            ArrayList<OrderDetail> orderDetailList = (ArrayList<OrderDetail>) intent
                    .getSerializableExtra(SUBMIT_ORDER_DETAIL_KEY);
            opSymbol = submitOrder(order, orderDetailList);
            sendMsg(SUBMIT_ORDER, opSymbol == true ? EXECUTE_SUBMIT_ORDER_SUCCESS : EXECUTE_ERROR, null, COMMAND_BLANK);
        } else if (commandType == COMMAND_TABLE_LASTEST_ORDER) {
            // TODO this branch is for future function request
            long tableId = intent.getLongExtra(CURRENT_TABLE_ID, 0L);
            Order order = ServiceHelper.getTableLatestOrder(tableId);
            sendMsg(SYNC_TABLE_LASTEST_ORDER, EXECUTE_NONE, order, COMMAND_TABLE_LASTEST_ORDER);
        } else {
            sendMsg(null, EXECUTE_NONE, null, COMMAND_BLANK);
        }
    }

    private void sendMsg(String broadcastHandle, int executeResult, Object data, int commandType) {
        if (broadcastHandle == null) {
            return;
        }

        Intent intent = new Intent(broadcastHandle);

        Bundle msgBundle = new Bundle();
        msgBundle.putInt(BROADCAST_RESULT_KEY, executeResult);
        if (data != null) {
            if (data instanceof HashMap) {
                msgBundle.putSerializable(BROADCAST_RESOURCE_KEY, (HashMap) data);
            } else {
                if (commandType == COMMAND_TABLE_LASTEST_ORDER) {
                    HashMap<String, Order> map = new HashMap<String, Order>();
                    map.put(ORDER_KEY, (Order) data);
                    msgBundle.putSerializable(BROADCAST_RESOURCE_KEY, map);
                } else {
                    // TODO some action do not need send broadcast msg
                }
            }
        }
        intent.putExtras(msgBundle);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.sendBroadcast(intent);
    }

    private boolean submitOrder(Order order, List<OrderDetail> orderDetailList) {
        try {
            StringBuilder submitStr = new StringBuilder("");
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
            ServiceHelper.submitOrderToServer(submitStr.toString());
        } catch (Exception e) {
            LogUtils.logE("Submit order error! \n" + e.getMessage());
            return false;
        }
        return true;
    }

    private boolean syncCategories() {
        ArrayList<Category> categoryList = ServiceHelper.getCategories();
        if (categoryList == null) {
            return false;
        }
        for (Category category : categoryList) {
            dbHelper.addCategory(category);
        }
        return true;
    }

    private boolean syncDishesAndImages() {
        ArrayList<Dish> dishList = ServiceHelper.getDishes();
        if (dishList == null) {
            return false;
        } else {
            // synchronize the image and update the local file path
            ServiceHelper.syncDishImage(dishList);
            for (Dish dish : dishList) {
                dbHelper.addDish(dish);
            }
            return true;
        }
    }

    private boolean syncDishCategory() {
        ArrayList<DishCategory> dishCategoryList = ServiceHelper.getDishCategory();
        if (dishCategoryList == null) {
            return false;
        }
        for (DishCategory dishCategory : dishCategoryList) {
            dbHelper.addDishCategory(dishCategory);
        }
        return true;
    }

    @SuppressWarnings("unused")
    private boolean syncConfigs() {
        ArrayList<Config> configList = ServiceHelper.getConfigs();
        if (configList == null) {
            return false;
        }
        for (Config config : configList) {
            dbHelper.addConfig(config);
        }
        return true;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
}
