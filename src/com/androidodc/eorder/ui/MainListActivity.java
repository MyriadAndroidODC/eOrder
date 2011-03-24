package com.androidodc.eorder.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainListActivity extends Activity implements OnClickListener, OnItemClickListener {
    // Dish number in one row
    private final static int DISHNUM_IN_ONEROW = 4;
    // The height of gridview's one row
    private final static int HEIGHT_ONEROW = 140;
    // TODO: Test string, this will be removed in the integration stage.
    private final static String CATEGORY_NAME = "»»≤À";
    private final static String DISH_NAME = "≤‚ ‘";
    private final static String DISH_PRICE = "88‘™";

    // The number of all the food category
    private int mCategoryNum;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initial all the data needed
        initData();
        // Initial the UI manually.
        initUi();
    }

    /**
     * Initial all the data for UI.
     */
    private void initData() {
        // TODO All the bellow data should get from the database, hard code for test
        // mCategoryNum = mDbHelper.getCategoryNum();
        mCategoryNum = 10;
    }

    /**
     * Initial all UI manually.
     */
    private void initUi() {
        // Set the title bar
        setTitle(R.string.dish_title);

        setContentView(R.layout.main_list_activity);
        LinearLayout galleryFrame = (LinearLayout) findViewById(R.id.gallery_frame);
        Button orderListButton = (Button) findViewById(R.id.orderedlist_button);
        orderListButton.setOnClickListener(this);

        // Inflate the main page from XML
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        // Initial the grid view for every food category.
        for (int i = 0; i < mCategoryNum; i++) {
            LinearLayout categoryLayout = (LinearLayout) inflater.inflate(
                    R.layout.main_list_item, null);

            TextView cagegoryNameTextView = (TextView) categoryLayout.findViewById(
                    R.id.category_name);
            cagegoryNameTextView.setText(CATEGORY_NAME); // Hard code for test

            GridView categoryView = (GridView) categoryLayout.findViewById(R.id.dish_grid);
            // Because every GridView has the same ID:"id_dish_grid", 
            // I should map this id with the category ID, So I set each a new ID.
            categoryView.setId(i);
            categoryView.setAdapter(new GridViewAdapter(this));
            categoryView.setOnItemClickListener(this);

            //TODO: hard code for no data. Should get the data from data base.
            // int dishNumber = mDbHelper.getDishNumByCategory(i);
            int dishNumber = 9;
            // If dish number = 8, then row number = 2; If dish number = 9, then row number = 3
            // Every row should have 4 dishes.
            int rowNum = (dishNumber % DISHNUM_IN_ONEROW == 0) ? (dishNumber / DISHNUM_IN_ONEROW)
                    : (dishNumber / DISHNUM_IN_ONEROW + 1);
            categoryView.getLayoutParams().height = rowNum * HEIGHT_ONEROW;

            galleryFrame.addView(categoryLayout);
        }
    }

    /**
     * Adapter for galley, just in test stage.
     */
    static class GridViewAdapter extends BaseAdapter implements OnCheckedChangeListener {
        final class ViewHolder {
            ImageView dishImageImageView;
            TextView dishNameTextView;
            TextView dishPriceTextView;
            CheckBox dishSelectCheckBox;
        }

        // context of the activity
        private Context mContext;
        private LayoutInflater mInflater;
        //TODO Dish image for test, should get the data from database
        private Integer[] mThumbIds = { 
                R.drawable.test,
                R.drawable.test,
                R.drawable.test,
                R.drawable.test,
                R.drawable.test,
                R.drawable.test };

        public GridViewAdapter(Context c) {
            mContext = c;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mThumbIds.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.gridview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.dishImageImageView = (ImageView) convertView.findViewById(R.id.food_image);
                viewHolder.dishNameTextView = (TextView) convertView.findViewById(R.id.food_name);
                viewHolder.dishPriceTextView = (TextView) convertView.findViewById(R.id.food_price);
                viewHolder.dishSelectCheckBox = (CheckBox) convertView.findViewById(R.id.food_select);

                setCheckBoxClickListener(viewHolder.dishSelectCheckBox, position, parent.getId());

                // Set the dish attributes
                setDishDetails(viewHolder, position, parent.getId());
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            return convertView;
        }

        /**
         * Set check box's click listener.
         */
        private void setCheckBoxClickListener(CheckBox dishSelectCheckBox, int position,
                int gridViewId) {
            // TODO Set check box's click listener
            dishSelectCheckBox.setId(position);
            dishSelectCheckBox.setTag(gridViewId);
            dishSelectCheckBox.setOnCheckedChangeListener(this);
        }

        /**
         * Set dish's details(image, price and name).
         */
        private void setDishDetails(ViewHolder viewHolder, int position, int categoryId) {
            // TODO use hard code for test, should get data from database
            /*
             * DishDetail dishDetail = mDbhelper.getDishDetail(iCategoryId,
             * position)
             * viewHolder.dishImageImageView.setImageResource(
             * Drawable.createFromPath(dishDetail.imageUrl));
             * viewHolder.dishNameTextView.setText(dishDetail.getName());
             * viewHolder.dishPriceTextView.setText(dishDetail.getPrice());
             */
            viewHolder.dishImageImageView.setImageResource(mThumbIds[position]);
            viewHolder.dishNameTextView.setText(DISH_NAME);
            viewHolder.dishPriceTextView.setText(DISH_PRICE);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int dishId = buttonView.getId();
            updateOrder(dishId, isChecked);
        }

        /**
         * Add or remove a dish from the dish list.
         */
        private void updateOrder(
                final int dishId, 
                final boolean isChecked) {
            // TODO

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.orderedlist_button:
            // Open the ordered list page
            openOrderedListPage();
            break;

        default:
            break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Handle the galley's item click event
        int categoryId = parent.getId();
        int dishId = position;
        viewDishDetail(categoryId, dishId);
    }

    /**
     * Open the ordered list page.
     */
    private void openOrderedListPage() {
        // TODO Open the ordered list page
    }

    /**
     * Open the dish detail page.
     */
    private void viewDishDetail(final int categoryId, final int dishId) {
        // TODO open the detail page

    }
}