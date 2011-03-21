package com.androidodc.eorder.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class TablesActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tables);

        ArrayList<HashMap<String, Integer>> tableNumbers = new ArrayList<HashMap<String, Integer>>();
        for (int i = 1; i <= getTablesCount(); i++) {
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            map.put("CurrentPosition", i);
            tableNumbers.add(map);
        }

        SimpleAdapter imagesItem = new SimpleAdapter(this, tableNumbers, R.layout.table_item,
                new String[] { "CurrentPosition" }, new int[] { R.id.table_num });
        GridView tablesView = (GridView) findViewById(R.id.tables);
        tablesView.setAdapter(imagesItem);
        tablesView.setOnItemClickListener(new ItemClickListener());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return false;
        }
        return true;
    }

    private int getTablesCount() {
        // TODO: need to get the real count of tables.
        return 18;
    }

    class ItemClickListener implements OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // TODO: the following code will be used, when MainListActivity is created.
            // Intent intent = new Intent(TablesActivity.this, MainListActivity.class);
            // intent.putExtra("TableNumber", selectedTableNum);
            // startActivity(intent);
            finish();
        }
    }

}
