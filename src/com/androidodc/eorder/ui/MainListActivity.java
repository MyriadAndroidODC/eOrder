package com.androidodc.eorder.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.androidodc.eorder.database.DatabaseHelper;
import com.androidodc.eorder.datatypes.Category;
import com.androidodc.eorder.datatypes.Dish;
import com.androidodc.eorder.order.OrderManager;
import com.androidodc.eorder.utils.ImageHelper;

import java.util.List;

public class MainListActivity extends Activity implements OnClickListener, OnItemClickListener {
    // Dish number in one row
    private final static int DISHNUM_IN_ONEROW = 4;
    // The height of gridview's one row
    private final static int HEIGHT_ONEROW = 220;

    //Open the detail page's static data
    public static final String SELECTED_DISH_ID = "selected_dish_id";
    public static final int OPEN_DETAILPAGE_FLAG = 1000;

    //Open the order page's static data
    public static final int OPEN_ORDERPAGE_FLAG = 1001;

    // Every column is displayed by a gallery
    private static GridView[] mCategoryGallery;

    // The number of all the food category
    private List<Category> mCategoryList;

    // Data base helper
    private static DatabaseHelper mDbHelper;

    // Order manager
    private static OrderManager mOrderManager;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO refresh screen
        if (RESULT_OK == resultCode) {
            for (int i = 0; i < mCategoryGallery.length; i++) {
                mCategoryGallery[i].invalidate();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Initial all the data for UI.
     */
    private void initData() {
        // All the bellow data should get from the database, hard code for test
        mOrderManager = OrderManager.getInstance();
        mDbHelper = DatabaseHelper.getInstance();
        mCategoryList = mDbHelper.getAllCategorys();
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
        int categoryNum = mCategoryList.size();
        mCategoryGallery = new GridView[categoryNum];
        // Initial the grid view for every food category.
        for (int i = 0; i < categoryNum - 1; i++) {
            LinearLayout categoryLayout = (LinearLayout) inflater.inflate(
                    R.layout.main_list_item, null);

            TextView cagegoryNameTextView = (TextView) categoryLayout.findViewById(
                    R.id.category_name);

            int categoryId = i + 1;
            Category category = mCategoryList.get(categoryId);
            cagegoryNameTextView.setText(category.getName());

            mCategoryGallery[i] = (GridView) categoryLayout.findViewById(R.id.dish_grid);
            mCategoryGallery[i].setId(categoryId);
            mCategoryGallery[i].setAdapter(new GridViewAdapter(this, categoryId));
            mCategoryGallery[i].setOnItemClickListener(this);

            int dishNumber = mDbHelper.getDishsByCategory(categoryId).size();
            // If dish number = 8, then row number = 2; If dish number = 9, then row number = 3
            // Every row should have 4 dishes.
            int rowNum = (dishNumber % DISHNUM_IN_ONEROW == 0) ? (dishNumber / DISHNUM_IN_ONEROW)
                    : (dishNumber / DISHNUM_IN_ONEROW + 1);
            mCategoryGallery[i].getLayoutParams().height = rowNum * HEIGHT_ONEROW;

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
        // Dish List
        private List<Dish> mDishList;
        //Data base helper 
        //DatabaseHelper mDbHelper;

        public GridViewAdapter(Context c, int i) {
            mContext = c;
            mInflater = LayoutInflater.from(mContext);
            mDishList = mDbHelper.getDishsByCategory(i);
        }

        @Override
        public int getCount() {
            return mDishList.size();
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

                int dishId = (int) mDishList.get(position).getDishId();
                setCheckBoxClickListener(viewHolder.dishSelectCheckBox, dishId, parent.getId());
                // Set the dish attributes
                setDishDetails(viewHolder, position);
                convertView.setId(dishId);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                updateDishStatus(viewHolder, parent.getId());
            }
            return convertView;
        }

        /**
         * Update the checkbox's .
         */
        private void updateDishStatus(ViewHolder viewHolder, int categoryId) {
            // TODO Auto-generated method stub
            int dishId = viewHolder.dishSelectCheckBox.getId();
            if (mOrderManager.isOrderedDish(dishId)) {
                viewHolder.dishSelectCheckBox.setChecked(true);
            }else {
                viewHolder.dishSelectCheckBox.setChecked(false);
            }
        }

        /**
         * Set check box's click listener.
         */
        private void setCheckBoxClickListener(CheckBox dishSelectCheckBox, int dishId,
                int gridViewId) {
            // TODO Set check box's click listener
            dishSelectCheckBox.setId(dishId);
            dishSelectCheckBox.setTag(gridViewId);
            dishSelectCheckBox.setOnCheckedChangeListener(this);
        }

        /**
         * Set dish's details(image, price and name).
         */
        private void setDishDetails(ViewHolder viewHolder, int position) {
            // TODO use hard code for test
            Dish dish = mDishList.get(position);
            viewHolder.dishImageImageView.setImageBitmap(ImageHelper.getImage(dish.getImageLocal()));
            viewHolder.dishNameTextView.setText(dish.getName());
            viewHolder.dishPriceTextView.setText(String.valueOf(dish.getPrice()));
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int dishId = buttonView.getId();
            int categoryId = (Integer) buttonView.getTag();
            if (isChecked) {
                mOrderManager.addOneDish(dishId, categoryId);
            } else {
                mOrderManager.removeDish(dishId, categoryId);
            }
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
        int dishId = view.getId();
        viewDishDetail(dishId);
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
    private void viewDishDetail(final int dishId) {
        // TODO open the detail page

        /*Intent intent = new Intent(MainListActivity.this, ViewDishsGallery.class);
        Integer dishIdInteger = dishId;
        Long dishLong = dishIdInteger.longValue();
        intent.putExtra(SELECTED_DISH_ID, dishLong);
        startActivityForResult(intent, OPEN_DETAILPAGE_FLAG);*/
    }
}