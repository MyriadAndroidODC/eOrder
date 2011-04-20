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
import android.widget.Toast;

import com.androidodc.eorder.datatypes.Order;
import com.androidodc.eorder.service.DiningService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckHistoryOrdersActivity extends Activity {

    public static final String CURRENT_ORDER = "current_order";
    private ArrayList<Order> mHistoryOrders = null;
    private SyncReceiver mReceiver = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        syncHistoryOrders();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(DiningService.SYNC_HISTORY_ORDER);
        mReceiver = new SyncReceiver();
        registerReceiver(mReceiver, filter);
    }

    private ArrayList<String> getHistoryOrdersDetails() {
        if (mHistoryOrders == null || mHistoryOrders.size() == 0) {
            return null;
        }

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
        Intent service = new Intent(CheckHistoryOrdersActivity.this, DiningService.class);
        service.putExtra(DiningService.SERVICE_COMMAND_KEY, DiningService.COMMAND_SYNC_ORDER);
        startService(service);
    }

    private void initUI() {
        setContentView(R.layout.check_history_orders_activity);

        ListView menuItemList = (ListView) findViewById(R.id.history_order_item);
        ArrayList<String> historyOrdersDetails = getHistoryOrdersDetails();
        if (historyOrdersDetails != null) {
            menuItemList.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_expandable_list_item_1, historyOrdersDetails));
            menuItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Order currentOrder = mHistoryOrders.get(position);
                    Intent intent = new Intent(CheckHistoryOrdersActivity.this,
                            OrderDetailActivity.class);
                    intent.putExtra(CURRENT_ORDER, currentOrder);
                    startActivity(intent);
                }
            });
        } else {
            Toast.makeText(CheckHistoryOrdersActivity.this,
                    getString(R.string.no_history_orders), Toast.LENGTH_SHORT).show();
        }

        Button returnButton = (Button) findViewById(R.id.return_button);
        returnButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    class SyncReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String action = arg1.getAction();

            if (action.equals(DiningService.SYNC_HISTORY_ORDER)) {
                @SuppressWarnings("unchecked")
                HashMap<Integer, List<Order>> map = (HashMap<Integer, List<Order>>) arg1
                        .getSerializableExtra("broadcast_resource");
                mHistoryOrders = (ArrayList<Order>) map.get(DiningService.ORDER_KEY);
            }

            initUI();
        }
    }
}
