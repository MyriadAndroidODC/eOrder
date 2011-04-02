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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidodc.eorder.database.DatabaseHelper;
import com.androidodc.eorder.datatypes.Category;
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

public class CartTotal extends Activity implements OnClickListener {

    private RelativeLayout mCartTotalLayout = null;

    private LayoutInflater mInflater = null;

    private DatabaseHelper mDbHelper = null;

    private OrderManager mOrderManager = null;

    private ArrayList<Long> mDishIds = null;

    private Button mOK = null;

    private Button mCheck = null;

    private List<DishDetail> mDishList = null;

    private ArrayList<String> mCategoryName = null;

    private ArrayList<Long> mCategoryId = null;

    private final static int DISHNUM_IN_ONEROW = 3;

    private final static int HEIGHT_ONEROW = 315;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOrderManager = OrderManager.getInstance();
        mDbHelper = DatabaseHelper.getInstance();
        mDishIds = new ArrayList<Long>();
        mCategoryName = new ArrayList<String>();
        mCategoryId = new ArrayList<Long>();
        mDishList = new ArrayList<DishDetail>();
    };

    @Override
    public void onResume() {
        super.onResume();

        initData();
        initUI();
    }

    private void clearData() {
        mDishIds.clear();
        mCategoryName.clear();
        mCategoryId.clear();
        mDishList.clear();
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
            mCategoryId.add(c.getId());
            mCategoryName.add(c.getName());
            for (Iterator<Long> it = hs.iterator(); it.hasNext();) {
                mDishIds.add(it.next());
            }
        }

        for (int i = 0; i < mDishIds.size(); i++) {
            String dishName = mDbHelper.getDishById(mDishIds.get(i)).getName();
            String dishImageLocal = mDbHelper.getDishById(mDishIds.get(i)).getImageLocal();
            long dishPrice = mDbHelper.getDishById(mDishIds.get(i)).getPrice();
            int dishCount = mOrderManager.getDishCopy(mDishIds.get(i));
            mDishList.add(new DishDetail(i, dishImageLocal, dishName, dishPrice, dishCount));
        }
    }

    private void initUI() {
        mInflater = LayoutInflater.from(this);

        mCartTotalLayout = (RelativeLayout) mInflater.inflate(R.layout.carttotal, null);
        setContentView(mCartTotalLayout);

        TextView view = (TextView) this.findViewById(R.id.mOrderTitle);
        view.setText(getString(R.string.tablename, mOrderManager.getTableId()));

        showDishCount();
        showTotalPrice();
        showDishChecked();
        showConfirmButton();
    }

    private void showTotalPrice() {
        TextView view = (TextView) mCartTotalLayout.findViewById(R.id.mTotal);
        view.setText(getString(R.string.total, mOrderManager.getTotalPrice()));
    }

    private void showConfirmButton() {
        mOK = (Button) mCartTotalLayout.findViewById(R.id.mOK);
        mOK.setOnClickListener(this);

        mCheck = (Button) mCartTotalLayout.findViewById(R.id.mCheck);
        mCheck.setOnClickListener(this);
    }

    private void showDishChecked() {
        GridView mCategoryGallery = (GridView) findViewById(R.id.mCategoryItem);
        mCategoryGallery.setAdapter(new categoryGridViewAdapter(this, mInflater));

        int mDishCount = mDishIds.size();
        int rowNum = (mDishCount % DISHNUM_IN_ONEROW == 0) ? mDishCount / DISHNUM_IN_ONEROW
                : mDishCount / DISHNUM_IN_ONEROW + 1;
        mCategoryGallery.getLayoutParams().height = rowNum * HEIGHT_ONEROW;
    }

    private void showDishCount() {
        String dishCount = "";

        for (int i = 0; i < mCategoryId.size(); i++) {
            HashSet<Long> dishIds = mOrderManager.getOrderedDishByCategoryId(mCategoryId.get(i));
            int total = 0;
            for (Iterator<Long> it = dishIds.iterator(); it.hasNext();) {
                total += mOrderManager.getDishCopy(it.next());
            }
            dishCount += mCategoryName.get(i) + ": " + total + "    ";
        }

        TextView view = (TextView) findViewById(R.id.mTotalDishCount);
        view.setText(dishCount);
    }

    private void submitOrder() {
        int total = mOrderManager.getTotalPrice();
        long tableId = mOrderManager.getTableId();
        ArrayList<OrderDetail> orderDetails = new ArrayList<OrderDetail>();

        for (int i = 0; i < mDishIds.size(); i++) {
            Long dishId = mDishIds.get(i);
            int dishCount = mOrderManager.getDishCopy(dishId);
            if (dishCount == 0) {
                continue;
            }

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setDishId(dishId);
            orderDetail.setTableId(tableId);
            orderDetail.setNumber(dishCount);
            orderDetails.add(orderDetail);
        }

        Order order = new Order();
        order.setTableId(tableId);
        order.setOrderTotal(total);

        HashMap<String, Object> submitOrder = new HashMap<String, Object>();
        submitOrder.put(DiningService.SUBMIT_ORDER_KEY, order);
        submitOrder.put(DiningService.SUBMIT_ORDER_DETAIL_KEY, orderDetails);

        Intent service = new Intent(CartTotal.this, DiningService.class);
        service.putExtra(DiningService.SUBMIT_KEY, submitOrder);
        service.putExtra(DiningService.SERVICE_COMMAND_KEY, DiningService.COMMAND_SUBMIT_ORDER);
        startService(service);
    }

    private void cleanupData() {
        for (int i = 0; i < mDishList.size(); i++) {
            DishDetail dd = mDishList.get(i);
            ArrayList<Long> ids;
            if (dd.getDishCount() == 0) {
                ids = (ArrayList<Long>) mDbHelper.getDishCategoryIds(dd.getId());
                for (int j = 0; j < ids.size(); j++) {
                    for (int k = 0; k < mCategoryId.size(); k++) {
                        if (ids.get(j) == mCategoryId.get(k)) {
                            mOrderManager.removeDish(dd.getId(), mCategoryId.get(k));
                        }
                    }
                }
            }
        }
    }

    public void onClick(View view) {
        if (view == mOK) {
            submitOrder();
        } else if (view == mCheck) {
            cleanupData();
            finish();
        }
    }

    private class categoryGridViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private LayoutInflater inflater = null;

        final class ViewHolder {
            private ImageView mImageView = null;
            private TextView mDishName = null;
            private TextView mDishPrice = null;
            private Button mDescend = null;
            private Button mAdd = null;
            private TextView mCount = null;
        }

        public categoryGridViewAdapter(Context c, LayoutInflater li) {
            mContext = c;
            inflater = LayoutInflater.from(mContext);
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

        private void decreaseDishCopy(int position) {
            if (mDishList.get(position).getDishCount() == 0) {
                return;
            }
            mDishList.get(position).setDishCount(mDishList.get(position).getDishCount() - 1);
            mOrderManager.setDishCopy(mDishIds.get(mDishList.get(position).getId()),
                    mDishList.get(position).getDishCount());
        }

        private void increaseDishCopy(int position) {
            mDishList.get(position).setDishCount(mDishList.get(position).getDishCount() + 1);
            mOrderManager.setDishCopy(mDishIds.get(mDishList.get(position).getId()),
                    mDishList.get(position).getDishCount());
        }

        public void setDishItem(final ViewHolder viewHolder, final int position) {
            viewHolder.mImageView.setImageBitmap(ImageHelper.getImage(mDishList.get(position)
                    .getDishResouce()));
            viewHolder.mDishName.setText(mDishList.get(position).getDishName());
            viewHolder.mDishPrice.setText(String.valueOf(mDishList.get(position).getDishPrice()));
            viewHolder.mCount.setText(String.valueOf(mDishList.get(position).getDishCount()));
            viewHolder.mDescend.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    decreaseDishCopy(position);
                    viewHolder.mCount.setText(String
                            .valueOf(mDishList.get(position).getDishCount()));
                    showTotalPrice();
                    showDishCount();
                }
            });
            viewHolder.mAdd.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    increaseDishCopy(position);
                    viewHolder.mCount.setText(String
                            .valueOf(mDishList.get(position).getDishCount()));
                    showTotalPrice();
                    showDishCount();
                }
            });
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder = new ViewHolder();

            if (null == convertView) {
                convertView = (LinearLayout) inflater.inflate(R.layout.cateitem, null);
                viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.mDishImage);
                viewHolder.mDishName = (TextView) convertView.findViewById(R.id.mDishName);
                viewHolder.mDishPrice = (TextView) convertView.findViewById(R.id.mDishPrice);
                viewHolder.mDescend = (Button) convertView.findViewById(R.id.mDescend);
                viewHolder.mCount = (TextView) convertView.findViewById(R.id.mCount);
                viewHolder.mAdd = (Button) convertView.findViewById(R.id.mAdd);
                setDishItem(viewHolder, position);
                convertView.setTag(viewHolder);
            }
            return convertView;
        }
    }

    class DishDetail {
        private int mId = 0;
        private String mDishResouce = null;
        private String mDishName = null;
        private long mDishPrice = 0;
        private int mDishCount = 0;

        public DishDetail(int id, String mRes, String mName, long mPrice, int mCount) {
            mId = id;
            mDishResouce = mRes;
            mDishName = mName;
            mDishPrice = mPrice;
            mDishCount = mCount;
        }

        public void setDishResouce(String mRes) {
            mDishResouce = mRes;
        }

        public String getDishResouce() {
            return mDishResouce;
        }

        public int getId() {
            return mId;
        }

        public void setDishName(String mName) {
            if (null != mName) {
                mDishName = mName;
            }
        }

        public String getDishName() {
            return mDishName;
        }

        public void setDishPrice(int mPrice) {
            if (0 != mPrice) {
                mDishPrice = mPrice;
            }
        }

        public long getDishPrice() {
            return mDishPrice;
        }

        public void setDishCount(int mCount) {
            if (0 <= mCount) {
                mDishCount = mCount;
            }
        }

        public int getDishCount() {
            return mDishCount;
        }
    }
}
