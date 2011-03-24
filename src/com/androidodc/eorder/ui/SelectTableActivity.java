package com.androidodc.eorder.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class SelectTableActivity extends Activity {

    private final String CURRENT_POS = "current_position";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tables);

        ArrayList<HashMap<String, Integer>> tableNumbers = new ArrayList<HashMap<String, Integer>>();
        for (int i = 1; i <= getTablesCount(); i++) {
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            map.put(CURRENT_POS, i);
            tableNumbers.add(map);
        }

        SimpleAdapter imagesItem = new SimpleAdapter(this, tableNumbers, R.layout.table_item,
                new String[] { CURRENT_POS }, new int[] { R.id.table_num });
        GridView tablesView = (GridView) findViewById(R.id.tables);
        tablesView.setAdapter(imagesItem);
        tablesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO: the following code will be used, when MainListActivity is created.
                Intent intent = new Intent(SelectTableActivity.this, MainListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private int getTablesCount() {
        // TODO: need to get the real count of tables.
        return 18;
    }

}
