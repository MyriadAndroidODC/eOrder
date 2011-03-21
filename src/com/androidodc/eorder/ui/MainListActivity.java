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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainListActivity extends Activity implements OnClickListener, OnItemClickListener {
    // Dish number in one row
    private final static int DISHNUM_IN_ONEROW = 4;
    // The height of gridview's one row
    private final static int HEIGHT_ONEROW = 140;
    // Test string
    private final static String CATEGORY_NAME = "�Ȳ�";
    private final static String DISH_NAME = "����";
    private final static String DISH_PRICE = "88Ԫ";

    // Every column is displayed by a gallery
    private GridView[] mCategoryGallery;

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
        // All the bellow data should get from the database, hard code for test
        // mCategoryNum = mDbHelper.getCategoryNum();
        mCategoryNum = 10;
    }

    /**
     * Initial all UI manually.
     */
    private void initUi() {
        // Set the title bar
        setTitle(R.string.dish_title);
        // Inflate the main page from XML
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        RelativeLayout mainPage = (RelativeLayout) inflater.inflate(
                R.layout.main_list_activity, null);
        LinearLayout galleryFrame = (LinearLayout) mainPage.findViewById(R.id.gallery_frame);

        mCategoryGallery = new GridView[mCategoryNum];
        // Initial the grid view for every food category.
        for (int i = 0; i < mCategoryNum; i++) {
            LinearLayout categoryLayout = (LinearLayout) inflater.inflate(
                    R.layout.main_list_item, null);
            TextView cagegoryNameTextView = (TextView) categoryLayout.findViewById(
                    R.id.id_category_name);

            cagegoryNameTextView.setText(CATEGORY_NAME); // Hard code for test

            mCategoryGallery[i] = (GridView) categoryLayout.findViewById(R.id.id_dish_grid);
            mCategoryGallery[i].setId(i);
            mCategoryGallery[i].setAdapter(new GridViewAdapter(this));
            mCategoryGallery[i].setOnItemClickListener(this);

            // hard code for no data.
            // int dishNumber = mDbHelper.getDishNumByCategory(i);
            int dishNumber = 9;
            // If dish number = 8, then row number = 2; If dish number = 9, then row number = 3
            // Every row should have 4 dishes.
            int rowNum = (dishNumber % DISHNUM_IN_ONEROW == 0) ? (dishNumber / DISHNUM_IN_ONEROW)
                    : (dishNumber / DISHNUM_IN_ONEROW + 1);
            mCategoryGallery[i].getLayoutParams().height = rowNum * HEIGHT_ONEROW;

            galleryFrame.addView(categoryLayout);
        }

        // Set the oderedList button's click listener.
        Button buttonForOrderedList = (Button) mainPage.findViewById(R.id.id_orderedlist_button);
        buttonForOrderedList.setOnClickListener(this);

        // Finish the initialization for UI
        setContentView(mainPage);
    }

    /**
     * Adapter for galley, just in test stage.
     */
    public class GridViewAdapter extends BaseAdapter implements OnCheckedChangeListener {
        final class ViewHolder {
            ImageView dishImageImageView;
            TextView dishNameTextView;
            TextView dishPriceTextView;
            CheckBox dishSelectCheckBox;
        }

        // context of the activity
        private Context mContext;
        private LayoutInflater mInflater;
        // Dish image for test
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

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.gridview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.dishImageImageView = (ImageView) convertView.findViewById(R.id.id_food_image);
                viewHolder.dishNameTextView = (TextView) convertView.findViewById(R.id.id_food_name);
                viewHolder.dishPriceTextView = (TextView) convertView.findViewById(R.id.id_food_price);
                viewHolder.dishSelectCheckBox = (CheckBox) convertView.findViewById(R.id.id_food_select);

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
                int iGridViewId) {
            // TODO Set check box's click listener
            dishSelectCheckBox.setId(position);
            dishSelectCheckBox.setTag(Integer.valueOf(iGridViewId));
            dishSelectCheckBox.setOnCheckedChangeListener(this);
        }

        /**
         * Set dish's details(image, price and name).
         */
        private void setDishDetails(ViewHolder viewHolder, int position, int iCategoryId) {
            // TODO use hard code for test
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
            int iDishId = buttonView.getId();
            int iCategoryId = (Integer) buttonView.getTag();
            changeTheDishList(iCategoryId, iDishId, isChecked);
        }

        /**
         * Add or remove a dish from the dish list.
         */
        private void changeTheDishList(int iCategoryId, int iDishId, boolean isChecked) {
            // TODO

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.id_orderedlist_button:
            // Open the ordered list page
            openOderedListPage();
            break;

        default:
            break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Handle the galley's item click event
        int iCategoryId = arg0.getId();
        int iDishId = arg2;
        openDetailPage(iCategoryId, iDishId);
    }

    /**
     * Open the ordered list page.
     */
    private void openOderedListPage() {
        // TODO Open the ordered list page
    }

    /**
     * Open the dish detail page.
     */
    private void openDetailPage(int iCategoryId, int iDishId) {
        // TODO open the detail page

    }
}