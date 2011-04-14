package com.androidodc.eorder.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidodc.eorder.database.DatabaseHelper;
import com.androidodc.eorder.datatypes.Category;
import com.androidodc.eorder.datatypes.Dish;
import com.androidodc.eorder.datatypes.Order;
import com.androidodc.eorder.engine.OrderDetail;
import com.androidodc.eorder.order.OrderManager;
import com.androidodc.eorder.service.DiningService;
import com.androidodc.eorder.utils.ImageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class CartTotalActivity extends Activity implements OnClickListener {

    private DatabaseHelper mDbHelper = DatabaseHelper.getInstance();

    private OrderManager mOrderManager = OrderManager.getInstance();

    private List<DishDetail> mDishesDetail = new ArrayList<DishDetail>();

    private Long mTableId = mOrderManager.getTableId();

    private final static int DISHNUM_IN_ONEROW = 3;

    private final static int HEIGHT_ONEROW = 315;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_total_activity);

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

        ArrayList<Category> allCategorys = (ArrayList<Category>) mDbHelper.getAllCategorys();
        for (int i = 0; i < allCategorys.size(); i++) {
            Category c = allCategorys.get(i);
            HashSet<Long> hs = mOrderManager.getOrderedDishByCategoryId(c.getId());
            if (hs == null) {
                continue;
            }
            for (Iterator<Long> it = hs.iterator(); it.hasNext();) {
                long dishId = it.next();
                Dish dish = mDbHelper.getDishById(dishId);
                String name = dish.getName();
                int price = dish.getPrice();
                String image = dish.getImageLocal();
                //TODO Should modify the "count"'s data type to long
                int count = (int) mOrderManager.getDishCopy(dishId);
                mDishesDetail.add(new DishDetail(dishId, c.getCategoryId(), name, c.getName(),
                        price, count, image));
            }
        }
    }

    private void initUI() {
        TextView titleView = (TextView) findViewById(R.id.cart_name);
        titleView.setText(getString(R.string.cart_name, mTableId));

        showDishCount();
        showTotalPrice(mOrderManager.getTotalPrice());

        Button ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(this);

        Button check = (Button) findViewById(R.id.check);
        check.setOnClickListener(this);

        GridView view = (GridView) findViewById(R.id.category_item);
        view.setAdapter(new categoryGridViewAdapter(this));
        int size = mDishesDetail.size();
        int rowNum = (size % DISHNUM_IN_ONEROW == 0) ? size / DISHNUM_IN_ONEROW : size
                / DISHNUM_IN_ONEROW + 1;
        view.getLayoutParams().height = rowNum * HEIGHT_ONEROW;
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

    private void submitOrder() {
        int total = mOrderManager.getTotalPrice();
        ArrayList<OrderDetail> orderDetails = new ArrayList<OrderDetail>();

        for (int i = 0; i < mDishesDetail.size(); i++) {
            Long dishId = mDishesDetail.get(i).getId();
            int dishCount = mDishesDetail.get(i).getDishCount();
            if (dishCount == 0) {
                continue;
            }

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setDishId(dishId);
            orderDetail.setTableId(mTableId);
            orderDetail.setNumber(dishCount);
            orderDetails.add(orderDetail);
        }

        Order order = new Order();
        order.setTableId(mTableId);
        order.setOrderTotal(total);

        Intent service = new Intent(CartTotalActivity.this, DiningService.class);
        service.putExtra(DiningService.SUBMIT_ORDER_KEY, order);
        service.putExtra(DiningService.SUBMIT_ORDER_DETAIL_KEY, orderDetails);
        service.putExtra(DiningService.SERVICE_COMMAND_KEY, DiningService.COMMAND_SUBMIT_ORDER);
        startService(service);
    }

    private void updateOrderManager() {
        for (int i = 0; i < mDishesDetail.size(); i++) {
            DishDetail dish = mDishesDetail.get(i);
            if (dish.getDishCount() == 0) {
                mOrderManager.removeDish(dish.getId(), dish.getCategoryId());
            }
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.ok) {
            submitOrder();
        } else if (view.getId() == R.id.check) {
            updateOrderManager();
            finish();
        }
    }

    private class categoryGridViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private LayoutInflater mInflater = null;

        final class ViewHolder {
            private ImageView mImageView = null;
            private TextView mDishName = null;
            private TextView mDishPrice = null;
            private Button mDescend = null;
            private Button mAdd = null;
            private TextView mCount = null;
        }

        public categoryGridViewAdapter(Context c) {
            mContext = c;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mDishesDetail.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressWarnings("unused")
        private void decreaseDishCopy(int position) {
            DishDetail dish = mDishesDetail.get(position);
            int dishCount = dish.getDishCount();
            if (dishCount == 0) {
                return;
            }
            dish.setDishCount(dishCount - 1);
        }

        @SuppressWarnings("unused")
        private void increaseDishCopy(int position) {
            DishDetail dish = mDishesDetail.get(position);
            dish.setDishCount(dish.getDishCount() + 1);
        }

        public void setDishItem(final ViewHolder viewHolder, final int position) {
            viewHolder.mImageView.setImageBitmap(ImageHelper.getImage(mDishesDetail.get(position)
                    .getDishImage()));
            viewHolder.mDishName.setText(mDishesDetail.get(position).getDishName());
            viewHolder.mDishPrice.setText(String
                    .valueOf(mDishesDetail.get(position).getDishPrice()));
            viewHolder.mCount.setText(String.valueOf(mDishesDetail.get(position).getDishCount()));
            viewHolder.mDescend.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DishDetail dish = mDishesDetail.get(position);
                    int count = dish.getDishCount() - 1;
                    if (count < 0) {
                        return;
                    }
                    dish.setDishCount(count);
                    mOrderManager.setDishCopy(dish.getId(), count);
                    viewHolder.mCount.setText(String.valueOf(dish.getDishCount()));
                    showDishCount();
                    showTotalPrice(mOrderManager.getTotalPrice());
                }
            });
            viewHolder.mAdd.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DishDetail dish = mDishesDetail.get(position);
                    int count = dish.getDishCount() + 1;
                    dish.setDishCount(count);
                    mOrderManager.setDishCopy(dish.getId(), count);
                    viewHolder.mCount.setText(String.valueOf(dish.getDishCount()));
                    showDishCount();
                    showTotalPrice(mOrderManager.getTotalPrice());
                }
            });
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder = new ViewHolder();

            if (null == convertView) {
                convertView = (LinearLayout) mInflater.inflate(R.layout.cateitem, null);
                viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.dish_image);
                viewHolder.mDishName = (TextView) convertView.findViewById(R.id.dish_name);
                viewHolder.mDishPrice = (TextView) convertView.findViewById(R.id.dish_price);
                viewHolder.mDescend = (Button) convertView.findViewById(R.id.descend_copy);
                viewHolder.mCount = (TextView) convertView.findViewById(R.id.count);
                viewHolder.mAdd = (Button) convertView.findViewById(R.id.add_copy);
                setDishItem(viewHolder, position);
                convertView.setTag(viewHolder);
            }
            return convertView;
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
}
