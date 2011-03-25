package com.androidodc.eorder.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.androidodc.eorder.datatypes.Order;
import com.androidodc.eorder.service.DiningService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckHistoryOrdersActivity extends Activity {

    private ArrayList<Order> mHistoryOrders = null;
    private SyncReceiver mReceiver = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_orders);

        Button returnButton = (Button) findViewById(R.id.return_button);
        returnButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

        syncHistoryOrders();
    }

    private ArrayList<String> getHistoryMenus() {
        ArrayList<String> data = new ArrayList<String>();
        String str = null;
        for (int i = 0; i < mHistoryOrders.size(); i++) {
            Order order = mHistoryOrders.get(i);
            str = String.format(getString(R.string.history_order_item), order.getOrderId(),
                    order.getTableId(), order.getOrderTotal());
            data.add(str);
        }

        return data;
    }

    private void syncHistoryOrders() {
        IntentFilter filter = new IntentFilter(DiningService.SYNC_HISTORY_ORDER);
        if (mReceiver == null) {
            mReceiver = new SyncReceiver();
        }
        registerReceiver(mReceiver, filter);

        Intent service = new Intent(CheckHistoryOrdersActivity.this, DiningService.class);
        service.putExtra(DiningService.SERVICE_COMMAND_KEY, DiningService.COMMAND_SYNC_ORDER);
        this.startService(service);
    }

    private void initUI() {
        ListView menuItemList = (ListView) findViewById(R.id.history_order_item);
        menuItemList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, getHistoryMenus()));
        menuItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
            }
        });
    }

    class SyncReceiver extends BroadcastReceiver {
        @SuppressWarnings("unchecked")
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String action = arg1.getAction();

            if (action.equals(DiningService.SYNC_HISTORY_ORDER)) {
                HashMap<Integer, List<Order>> map = (HashMap<Integer, List<Order>>) arg1
                        .getSerializableExtra("broadcast_resource");
                mHistoryOrders = (ArrayList<Order>) map.get(DiningService.ORDER_KEY);

                initUI();
            }

            unregisterReceiver(mReceiver);
        }
    }
}
