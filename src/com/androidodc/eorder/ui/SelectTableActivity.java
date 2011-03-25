package com.androidodc.eorder.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.androidodc.eorder.datatypes.DiningTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectTableActivity extends Activity {

    private final int DIALOG_SYNC_DATA = 0;
    private final String CURRENT_POS = "current_position";
    private SyncReceiver mReceiver = null;
    private List<DiningTable> mTablesList = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tables);

        syncTablesStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sync_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.sync_all_data:
            syncOtherData();
            return true;
        }

        return false;
    }

    @Override
    public void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle(getString(R.string.title_context_menu));
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.view_history_orders:
            // TODO: when CheckHistoryOrdersActivity is created, remove the comments.
            // Intent intent = new Intent(SelectTableActivity.this,
            // CheckHistoryOrdersActivity.class);
            // startActivity(intent);
            return true;
        case R.id.sync_tables_status:
            syncTablesStatus();
            return true;
        }

        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_SYNC_DATA:
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.sync_info));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            return dialog;
        }
        return null;
    }

    private void initUI() {
        ArrayList<HashMap<String, Integer>> tableNumbers = new ArrayList<HashMap<String, Integer>>();
        for (int i = 1; i <= getTablesCount(); i++) {
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            map.put(CURRENT_POS, i);
            tableNumbers.add(map);
        }

        SimpleAdapter imagesItem = new SimpleAdapter(this, tableNumbers,
                R.layout.table_item, new String[] { CURRENT_POS }, new int[] { R.id.table_num });
        GridView tablesView = (GridView) findViewById(R.id.tables);
        tablesView.setAdapter(imagesItem);
        tablesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO: the following code will be used, when MainListActivity is created.
                // Intent intent = new Intent(TablesActivity.this, MainListActivity.class);
                // startActivity(intent);
                finish();
            }
        });
        registerForContextMenu(tablesView);
    }

    private int getTablesCount() {
        return mTablesList.size();
    }

    @SuppressWarnings("unused")
    private ArrayList<Boolean> getTableStatus() {

        ArrayList<Boolean> result = new ArrayList<Boolean>();
        int size = mTablesList.size();

        for (int i = 0; i < size; i++) {
            result.add(mTablesList.get(i).isFree());
        }
        return result;
    }

    private void syncTablesStatus() {/*
        IntentFilter filter = new IntentFilter(DiningService.SYNC_DINING_TABLE);
        if (mReceiver == null) {
            mReceiver = new SyncReceiver();
        }
        registerReceiver(mReceiver, filter);

        Intent service = new Intent(SelectTableActivity.this, DiningService.class);
        service.putExtra(DiningService.SERVICE_COMMAND_KEY, DiningService.COMMAND_SYNC_DINING_TABLE);
        this.startService(service);

        showDialog(DIALOG_SYNC_DATA);*/
    }

    /* The operation will last long, and no answer from server. */
    private void syncOtherData() {
        IntentFilter filter = new IntentFilter(SyncReceiver.SYNC_OTHER);
        if (mReceiver == null) {
            mReceiver = new SyncReceiver();
        }
        registerReceiver(mReceiver, filter);
/*
        Intent service = new Intent(SelectTableActivity.this, DiningService.class);
        service.putExtra(DiningService.SERVICE_COMMAND_KEY, DiningService.COMMAND_SYNC_OTHER);
        this.startService(service);*/
    }

    class SyncReceiver extends BroadcastReceiver {

        private static final String SYNC_OTHER = "com.androidodc.intent.SYNC_OTHER";

        @SuppressWarnings("unchecked")
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String action = arg1.getAction();
/*
            if (action.equals(DiningService.SYNC_DINING_TABLE)) {
                int result = arg1.getIntExtra(DiningService.BROADCAST_RESULT_KEY,
                        DiningService.EXECUTE_ERROR);
                if (result == DiningService.EXECUTE_ERROR) {
                    Toast.makeText(SelectTableActivity.this, R.string.info_sync_failture,
                            Toast.LENGTH_LONG).show();
                    removeDialog(DIALOG_SYNC_DATA);
                    return;
                }
*/
                HashMap<Integer, List<DiningTable>> map = (HashMap<Integer, List<DiningTable>>) arg1
                        .getSerializableExtra("broadcast_resource");
                mTablesList = (List<DiningTable>) map.get("dining_tables");

                removeDialog(DIALOG_SYNC_DATA);
                Toast.makeText(SelectTableActivity.this, R.string.info_sync_success,
                        Toast.LENGTH_LONG).show();
                initUI();
            //}
        }
    }
}
