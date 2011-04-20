package com.androidodc.eorder.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidodc.eorder.database.DatabaseHelper;
import com.androidodc.eorder.datatypes.Category;
import com.androidodc.eorder.datatypes.Dish;
import com.androidodc.eorder.order.OrderManager;
import com.androidodc.eorder.utils.ImageHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MainListActivity extends Activity implements OnClickListener, OnItemClickListener {
    // Dish number in one row
    private final static int DISHNUM_IN_ONEROW = 4;
    // The height of gridview's one row
    private final static int HEIGHT_ONEROW = 220;

    // Open the detail page's static data
    public static final String SELECTED_DISH_ID = "selected_dish_id";
    public static final int OPEN_DETAILPAGE_FLAG = 1000;

    // Open the order page's static data
    public static final int OPEN_ORDERPAGE_FLAG = 1001;

    private List<Category> mCategoryList;

    private Map<Long, List<Dish>> mCategoryDishesMap;

    private static Map<Long, Bitmap> mDishImageMap;

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
        mDbHelper = DatabaseHelper.getInstance();
        mOrderManager = OrderManager.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        asyncInitUi();
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearDishImageCache();
    }

    // TODO To avoid waiting period before the view display, activity should build the view frame first,
    // when fetch all data, and then refresh the view again.
    private void asyncInitUi() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                mCategoryList = mDbHelper.getAllCategorys();
                mCategoryDishesMap = mDbHelper.getCategoryAndDishes();
                getAllDishesImage();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                initUi();
            }
        }.execute(null);
    }

    private void getAllDishesImage() {
        if (mDishImageMap == null) {
            mDishImageMap = new HashMap<Long, Bitmap>();
            for (Entry<Long, List<Dish>> entry : mCategoryDishesMap.entrySet()) {
                List<Dish> dishList = entry.getValue();
                for (Dish dish : dishList) {
                    mDishImageMap.put(dish.getDishId(), ImageHelper.getImage(dish.getImageLocal()));
                }
            }
        }
    }

    private void clearDishImageCache(){
        mDishImageMap.clear();
        mDishImageMap = null;
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
        GridView[] categoryGallery = new GridView[categoryNum];
        // Initial the grid view for every food category.
        for (int i = 0; i < categoryNum; i++) {
            LinearLayout categoryLayout = (LinearLayout) inflater.inflate(R.layout.main_list_item, null);
            TextView cagegoryNameTextView = (TextView) categoryLayout.findViewById(R.id.category_name);

            Category category = mCategoryList.get(i);
            long categoryId = category.getCategoryId();
            cagegoryNameTextView.setText(category.getName());

            List<Dish> dishList = mCategoryDishesMap.get(categoryId);
            if (dishList == null) {
                dishList = Collections.emptyList();
            }
            categoryGallery[i] = (GridView) categoryLayout.findViewById(R.id.dish_grid);
            categoryGallery[i].setId((int) categoryId);
            categoryGallery[i].setAdapter(new GridViewAdapter(this, dishList));
            categoryGallery[i].setOnItemClickListener(this);

            int dishNumber = dishList.size();
            // If dish number = 8, then row number = 2; If dish number = 9, then row number = 3
            // Every row should have 4 dishes.
            int rowNum = (dishNumber % DISHNUM_IN_ONEROW == 0) ? (dishNumber / DISHNUM_IN_ONEROW)
                    : (dishNumber / DISHNUM_IN_ONEROW + 1);
            categoryGallery[i].getLayoutParams().height = rowNum * HEIGHT_ONEROW;

            galleryFrame.addView(categoryLayout);
        }
    }

    /**
     * Adapter for galley, just in test stage.
     */
    static class GridViewAdapter extends BaseAdapter implements OnClickListener {
        final class ViewHolder {
            ImageView dishImageImageView;
            TextView dishNameTextView;
            TextView dishPriceTextView;
            CheckBox dishSelectCheckBox;
        }

        // context of the activity
        private Context mContext;
        private LayoutInflater mInflater;
        private List<Dish> mDishList;

        public GridViewAdapter(Context c, List<Dish> dishList) {
            mContext = c;
            mInflater = LayoutInflater.from(mContext);
            mDishList = dishList;
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

                long dishId = mDishList.get(position).getDishId();
                setCheckBoxClickListener(viewHolder.dishSelectCheckBox, dishId, parent.getId());
                // Set the dish attributes
                setDishDetails(viewHolder, position);
                convertView.setId((int) dishId);
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
        private void updateDishStatus(ViewHolder viewHolder, long categoryId) {
            long dishId = viewHolder.dishSelectCheckBox.getId();
            if (mOrderManager.isOrderedDish(dishId)) {
                viewHolder.dishSelectCheckBox.setChecked(true);
            } else {
                viewHolder.dishSelectCheckBox.setChecked(false);
            }
        }

        /**
         * Set check box's click listener.
         */
        private void setCheckBoxClickListener(CheckBox dishSelectCheckBox, long dishId,
                long gridViewId) {
            dishSelectCheckBox.setId((int) dishId);
            if (mOrderManager.isOrderedDish(dishId)) {
                dishSelectCheckBox.setChecked(true);
            }else {
                dishSelectCheckBox.setChecked(false);
            }

            dishSelectCheckBox.setOnClickListener(this);
        }

        /**
         * Set dish's details(image, price and name).
         */
        private void setDishDetails(ViewHolder viewHolder, int position) {
            Dish dish = mDishList.get(position);
            viewHolder.dishImageImageView.setImageBitmap(mDishImageMap.get(dish.getDishId()));
            viewHolder.dishNameTextView.setText(dish.getName());
            viewHolder.dishPriceTextView.setText(String.valueOf(dish.getPrice()));
        }

        @Override
        public void onClick(View v) {
            long dishId = v.getId();
            CheckBox checkBox = (CheckBox) v;
            long categoryId = mDbHelper.getDishCategoryId(dishId);
            if (mOrderManager.isOrderedDish(dishId)) {
                checkBox.setChecked(false);
                mOrderManager.removeDish(dishId, categoryId);
            }else {
                checkBox.setChecked(true);
                mOrderManager.addOneDish(dishId, categoryId);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.orderedlist_button:
            if (!mOrderManager.isOrderEmpty()) {
                // Open the ordered list page
                openOrderedListPage();
            }else {
                Toast.makeText(getApplicationContext(),
                        R.string.warning_emptyorder, Toast.LENGTH_LONG).show();
            }

            break;

        default:
            break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long dishId = view.getId();
        viewDishDetail(dishId);
    }

    /**
     * Open the ordered list page.
     */
    private void openOrderedListPage() {
        startActivity(new Intent(MainListActivity.this, CartTotalActivity.class));
    }

    /**
     * Open the dish detail page.
     */
    private void viewDishDetail(final long dishId) {
        Intent intent = new Intent(MainListActivity.this, ViewDishsGallery.class);
        intent.putExtra(SELECTED_DISH_ID, dishId);
        startActivity(intent);
    }
}
