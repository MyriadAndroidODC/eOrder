package com.androidodc.eorder.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidodc.eorder.datatypes.DiningTable;
import com.androidodc.eorder.order.OrderManager;
import com.androidodc.eorder.service.DiningService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectTableActivity extends Activity {

    private final int DIALOG_SYNC_DATA = 0;
    private SyncReceiver mReceiver = null;
    private List<DiningTable> mTablesList = null;
    private ArrayList<Boolean> mTableStatus = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_table_activity);

        syncTablesStatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
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
            case R.id.sync_server_data:
                syncServerData();
                return true;
            case R.id.sync_tables_status:
                syncTablesStatus();
                return true;
            case R.id.check_history_orders:
                Intent intent = new Intent(SelectTableActivity.this, CheckHistoryOrdersActivity.class);
                startActivity(intent);
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
        ArrayList<Integer> tableNumbers = new ArrayList<Integer>();
        for (int i = 1; i <= getTablesCount(); i++) {
            tableNumbers.add(i);
        }

        mTableStatus = getTableStatus();
        GridViewAdapter imagesItem = new GridViewAdapter(this,
                tableNumbers, R.layout.table_item, mTableStatus);
        GridView tablesView = (GridView) findViewById(R.id.tables);
        tablesView.setAdapter(imagesItem);
        tablesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderManager.getInstance().setTableId(position + 1);
                if (mTableStatus.get(position) == true) {
                    OrderManager.getInstance().setTableId(position + 1);
                    Intent intent = new Intent(SelectTableActivity.this, MainListActivity.class);
                    startActivity(intent);
                } else {
                    // FIXME: handle else condition.
                }
            }
        });

        registerForContextMenu(tablesView);
    }

    private int getTablesCount() {
        if (mTablesList == null || mTablesList.size() == 0) {
            return 0;
        }
        return mTablesList.size();
    }

    private ArrayList<Boolean> getTableStatus() {
        ArrayList<Boolean> result = new ArrayList<Boolean>();
        int size = getTablesCount();

        for (int i = 0; i < size; i++) {
            result.add(mTablesList.get(i).isFree());
        }
        return result;
    }

    private void syncTablesStatus() {
        IntentFilter filter = new IntentFilter(DiningService.SYNC_DINING_TABLE);
        if (mReceiver == null) {
            mReceiver = new SyncReceiver();
        }
        registerReceiver(mReceiver, filter);

        Intent service = new Intent(SelectTableActivity.this, DiningService.class);
        service.putExtra(DiningService.SERVICE_COMMAND_KEY, DiningService.COMMAND_SYNC_DINING_TABLE);
        startService(service);

        showDialog(DIALOG_SYNC_DATA);
    }

    /* The operation will last long, and no answer from server. */
    private void syncServerData() {
        Intent service = new Intent(SelectTableActivity.this, DiningService.class);
        service.putExtra(DiningService.SERVICE_COMMAND_KEY, DiningService.COMMAND_SYNC_OTHER);
        startService(service);
    }

    private class SyncReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context content, Intent intent) {
            String action = intent.getAction();

            if (action.equals(DiningService.SYNC_DINING_TABLE)) {
                int result = intent.getIntExtra(DiningService.BROADCAST_RESULT_KEY,
                        DiningService.EXECUTE_ERROR);
                if (result == DiningService.EXECUTE_ERROR) {
                    mTablesList = null;
                    Toast.makeText(SelectTableActivity.this, R.string.info_sync_failture,
                            Toast.LENGTH_LONG).show();
                    dismissDialog(DIALOG_SYNC_DATA);
                    finish();
                    return;
                }

                @SuppressWarnings("unchecked")
                HashMap<Integer, List<DiningTable>> map = (HashMap<Integer, List<DiningTable>>) intent
                        .getSerializableExtra("broadcast_resource");
                mTablesList = (List<DiningTable>) map.get("dining_tables");

                dismissDialog(DIALOG_SYNC_DATA);
                Toast.makeText(SelectTableActivity.this, R.string.info_sync_success,
                        Toast.LENGTH_LONG).show();
                initUI();
            }
        }
    }

    private class GridViewAdapter extends BaseAdapter {

        private Context mContext = null;
        private List<?> mData = null;
        private int mResource = 0;
        private ArrayList<Boolean> mUsed = null;
        private LayoutInflater mInflater = null;

        final class ViewHolder {
            private ImageView mImageView = null;
            private TextView mTextView = null;
        }

        public GridViewAdapter(Context context, List<?> data, int resource, ArrayList<Boolean> used) {
            mContext = context;
            mData = data;
            mResource = resource;
            mUsed = used;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setDishItem(final ViewHolder viewHolder, final int position) {
            viewHolder.mImageView.setImageResource(R.drawable.custom_table_item);
            viewHolder.mTextView.setText(mData.get(position).toString());
            if (mUsed.get(position) == false) {
                viewHolder.mImageView.setAlpha(128);
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder = new ViewHolder();

            if (null == convertView) {
                convertView = mInflater.inflate(mResource, null);
                viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.table_bg);
                viewHolder.mTextView = (TextView) convertView.findViewById(R.id.table_num);
                setDishItem(viewHolder, position);
                convertView.setTag(viewHolder);
            }
            return convertView;
        }
    }
}
