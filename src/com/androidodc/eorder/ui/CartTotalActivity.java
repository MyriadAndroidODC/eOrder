package com.androidodc.eorder.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
                // TODO Should modify the "count"'s data type to long
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

        ListView view = (ListView) findViewById(R.id.category_item);
        view.setAdapter(new DishListAdapter(this, R.layout.cateitem, mDishesDetail));
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

        mOrderManager.ClearOrderManager();
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
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.submit_warning))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.submit_yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    submitOrder();
                                    startActivity(new Intent(CartTotalActivity.this,
                                            SelectTableActivity.class));
                                    finish();
                                }
                            })
                    .setNegativeButton(getString(R.string.submit_no),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).create().show();
        } else if (view.getId() == R.id.check) {
            updateOrderManager();
            finish();
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
            View view;
            if (convertView == null) {
                view = mInflater.inflate(mResource, parent, false);
            } else {
                view = convertView;
            }

            final DishDetail current = getItem(position);

            ImageView image = (ImageView) view.findViewById(R.id.dish_image);
            image.setImageBitmap(ImageHelper.getImage(current.getDishImage()));

            TextView name = (TextView) view.findViewById(R.id.dish_name);
            name.setText(current.getDishName());

            TextView price = (TextView) view.findViewById(R.id.dish_price);
            price.setText(String.valueOf(current.getDishPrice()));

            final TextView amount = (TextView) view.findViewById(R.id.count);
            amount.setText(String.valueOf(current.getDishCount()));

            Button rm = (Button) view.findViewById(R.id.descend_copy);
            rm.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = current.getDishCount() - 1;
                    if (count < 0) {
                        return;
                    }
                    current.setDishCount(count);
                    mOrderManager.setDishCopy(current.getId(), count);
                    amount.setText(String.valueOf(current.getDishCount()));
                    showDishCount();
                    showTotalPrice(mOrderManager.getTotalPrice());
                }
            });

            Button add = (Button) view.findViewById(R.id.add_copy);
            add.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = current.getDishCount() + 1;
                    current.setDishCount(count);
                    mOrderManager.setDishCopy(current.getId(), count);
                    amount.setText(String.valueOf(current.getDishCount()));
                    showDishCount();
                    showTotalPrice(mOrderManager.getTotalPrice());
                }
            });

            return view;
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
