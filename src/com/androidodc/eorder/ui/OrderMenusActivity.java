package com.androidodc.eorder.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidodc.eorder.database.DatabaseHelper;
import com.androidodc.eorder.datatypes.Category;
import com.androidodc.eorder.datatypes.Dish;
import com.androidodc.eorder.datatypes.Order;
import com.androidodc.eorder.datatypes.OrderItem;
import com.androidodc.eorder.utils.ImageHelper;
import com.androidodc.eorder.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderMenusActivity extends Activity implements OnClickListener {

    private DatabaseHelper mDbHelper = DatabaseHelper.getInstance();

    private List<DishDetail> mDishesDetail = new ArrayList<DishDetail>();

    private Order mOrder = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_menus_activity);

        mOrder = (Order) getIntent().getSerializableExtra(CheckHistoryOrdersActivity.CURRENT_ORDER);

        update();
    }

    private void update() {
        initData();
        initUI();
    }

    private void clearData() {
        mDishesDetail.clear();
    }

    private void initData() {
        clearData();

        ArrayList<OrderItem> items = (ArrayList<OrderItem>) mOrder.getOrderItems();
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            int copy = item.getAmount();
            Dish dish = item.getDish();
            long dishId = dish.getDishId();
            long categoryId = mDbHelper.getDishCategoryId(dishId);
            String categoryName = null;
            ArrayList<Category> categories = (ArrayList<Category>) mDbHelper.getAllCategorys();
            for (int j = 0; j < categories.size(); j++) {
                Category category = categories.get(j);
                if (category.getCategoryId() == categoryId) {
                    categoryName = category.getName();
                }
            }
            mDishesDetail.add(new DishDetail(dishId, categoryId, dish.getName(), categoryName, dish
                    .getPrice(), copy, dish.getImageLocal()));
        }
    }

    private void initUI() {
        TextView titleView = (TextView) findViewById(R.id.cart_name);
        titleView.setText(getString(R.string.cart_name, mOrder.getTableId()));

        showDishCount();
        showTotalPrice(mOrder.getOrderTotal());

        Button returnButton = (Button) findViewById(R.id.ok);
        returnButton.setText(R.string.return_button);
        returnButton.setOnClickListener(this);

        Button modifyButton = (Button) findViewById(R.id.check);
        modifyButton.setText(R.string.change_order);
        modifyButton.setOnClickListener(this);

        ListView view = (ListView) findViewById(R.id.menus_order_item);
        if (view == null) {
            LogUtils.logE("view null pointer");
            if (mDishesDetail == null) {
                LogUtils.logE("mDishesDetail null pointer");
            }
            finish();
        }
        view.setAdapter(new DishListAdapter(this, R.layout.order_menus_item, mDishesDetail));
    }

    private void showTotalPrice(int total) {
        TextView totalView = (TextView) findViewById(R.id.total_price);
        totalView.setText(getString(R.string.total_price, total));
    }

    private void showDishCount() {
        StringBuilder dishCount = new StringBuilder();
        int total = 0;
        int size = mDishesDetail.size();

        for (int i = 0; i < size; i++) {
            DishDetail dish = mDishesDetail.get(i);
            long categoryId = dish.getCategoryId();

            if (i + 1 == size || categoryId != mDishesDetail.get(i + 1).getCategoryId()) {
                total += dish.getTotalPrice();
                dishCount.append(dish.getCategoryName());
                dishCount.append(": ");
                dishCount.append(total);
                dishCount.append("    ");
                total = 0;
            } else {
                total += dish.getTotalPrice();
            }
        }

        TextView view = (TextView) findViewById(R.id.total_dish_count);
        view.setText(dishCount);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.ok) {
            finish();
        } else if (view.getId() == R.id.check) {
            // TODO: need to implement
        }
    }

    private class DishDetail {
        private long mId = 0;
        private long mCategoryId = 0;
        private String mDishName = null;
        private String mCategoryName = null;
        private int mDishPrice = 0;
        private int mDishCount = 0;
        private String mDishImage = null;

        public DishDetail(long id, long categoryId, String name, String categoryName, int price,
                int count, String image) {
            mId = id;
            mCategoryId = categoryId;
            mDishName = name;
            mCategoryName = categoryName;
            mDishPrice = price;
            mDishCount = count;
            mDishImage = image;
        }

        @SuppressWarnings("unused")
        public long getId() {
            return mId;
        }

        public long getCategoryId() {
            return mCategoryId;
        }

        @SuppressWarnings("unused")
        public void setDishName(String name) {
            if (null != name) {
                mDishName = name;
            }
        }

        public String getDishName() {
            return mDishName;
        }

        @SuppressWarnings("unused")
        public void setCategoryName(String categoryName) {
            if (null != categoryName) {
                mCategoryName = categoryName;
            }
        }

        public String getCategoryName() {
            return mCategoryName;
        }

        @SuppressWarnings("unused")
        public void setDishPrice(int price) {
            if (0 != price) {
                mDishPrice = price;
            }
        }

        public int getDishPrice() {
            return mDishPrice;
        }

        public int getTotalPrice() {
            return mDishPrice * mDishCount;
        }

        @SuppressWarnings("unused")
        public void setDishCount(int count) {
            if (0 <= count) {
                mDishCount = count;
            }
        }

        public int getDishCount() {
            return mDishCount;
        }

        @SuppressWarnings("unused")
        public void setDishImage(String image) {
            mDishImage = image;
        }

        public String getDishImage() {
            return mDishImage;
        }
    }

    private class DishListAdapter extends ArrayAdapter<DishDetail> {
        protected LayoutInflater mInflater;
        private int mResource = 0;

        public DishListAdapter(Context context, int resource, List<DishDetail> mDishesDetail) {
            super(context, resource, mDishesDetail);
            mResource = resource;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView image;
            TextView name;
            TextView price;
            TextView amount;

            View view;
            if (convertView == null) {
                view = mInflater.inflate(mResource, parent, false);
            } else {
                view = convertView;
            }

            image = (ImageView) view.findViewById(R.id.menus_dish_image);
            image.setImageBitmap(ImageHelper.getImage(getItem(position).getDishImage()));

            name = (TextView) view.findViewById(R.id.menus_dish_name);
            name.setText(getItem(position).getDishName());

            price = (TextView) view.findViewById(R.id.menus_dish_price);
            price.setText(getString(R.string.menus_price, getItem(position).getDishPrice()));

            amount = (TextView) view.findViewById(R.id.menus_count);
            int copies = getItem(position).getDishCount();
            int format = R.string.menus_copy;
            if (copies > 1) {
                format = R.string.menus_copies;
            }
            amount.setText(getString(format, copies));

            return view;
        }
    }
}
