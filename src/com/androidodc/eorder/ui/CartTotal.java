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
import com.androidodc.eorder.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class CartTotal extends Activity implements OnClickListener {
    private RelativeLayout mCartTotalLayout = null;

    private TextView mOrderTitle = null;
    private LayoutInflater inflater = null;

    private DatabaseHelper mDbHelper = null;
    private LinearLayout mTotalDishCountLayout = null;
    private TextView mCate1 = null;
    private TextView mDish1Count = null;
    private TextView mCate2 = null;
    private TextView mDish2Count = null;

    private OrderManager mOrderManager = null;

    private TextView mTotal = null;
    private TextView mPrice = null;

    private ArrayList<Long> mDishIds = null;
    private LinearLayout mCategoryDetailLayout = null;

    private Button mOK = null;
    private Button mCheck = null;

    private categoryGridViewAdapter mDishItem = null;
    private GridView[] mCategoryGallery;
    private List<DishDetail> mDishList = new ArrayList<DishDetail>();

    // Dish number in one row
    private final static int DISHNUM_IN_ONEROW = 3;
    // The height of gridview's one row
    private final static int HEIGHT_ONEROW = 315;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDishItem = new categoryGridViewAdapter(this, inflater);
        initData();
        initUI();
        this.setContentView(mCartTotalLayout);

    }

    private void initData() {
        // TODO init data here
        mOrderManager = OrderManager.getInstance();
        mDbHelper = DatabaseHelper.getInstance();
        mDishIds = new ArrayList<Long>();
        ArrayList<Category> allCategorys = (ArrayList<Category>) mDbHelper.getAllCategorys();
        for (int i = 0; i < allCategorys.size(); i++) {
            LogUtils.logE("" + allCategorys.get(i).getId());
            HashSet<Long> hs = mOrderManager
                    .getOrderedDishByCategoryId(allCategorys.get(i).getId());
            if (hs == null) {
                LogUtils.logE("CartTotal " + i);
                continue;
            }
            for (Iterator<Long> it = hs.iterator(); it.hasNext();) {
                mDishIds.add(it.next());
            }
        }
        for (int i = 0; i < mDishIds.size(); i++) {
            String dishName = mDbHelper.getDishById(mDishIds.get(i)).getName();
            String dishImageLocal = mDbHelper.getDishById(mDishIds.get(i)).getImageLocal();
            long dishPrice = mDbHelper.getDishById(mDishIds.get(i)).getPrice();
            int dishCount = mOrderManager.getDishCopy(mDishIds.get(i));
            LogUtils.logE("dishCount=" + dishCount);
            mDishList.add(new DishDetail(i, dishImageLocal, dishName, dishPrice, dishCount));
        }

    }

    private void initUI() {
        LogUtils.logE(new File("/mnt/sdcard/jzsjd.jpg").exists() + "+++++++++++++++++++");
        inflater = LayoutInflater.from(this);

        mCartTotalLayout = (RelativeLayout) inflater.inflate(R.layout.carttotal, null);

        mOrderTitle = (TextView) this.findViewById(R.id.mOrderTitle);

        showDishCount();

        showTotalPrice();

        showDishChecked();

        showConfirmButton();
    }

    private void showTotalPrice() {
        mTotal = (TextView) mCartTotalLayout.findViewById(R.id.mTotal);
        mPrice = (TextView) mCartTotalLayout.findViewById(R.id.mPrice);

        // TODO calculate the real price here.
        int mRealPrice = 1307;

        mTotal.append("Total" + " : ");
        mPrice.append(mRealPrice + "dollar");
    }

    private void showConfirmButton() {
        mOK = (Button) mCartTotalLayout.findViewById(R.id.mOK);
        mOK.setOnClickListener(this);

        mCheck = (Button) mCartTotalLayout.findViewById(R.id.mCheck);
        mCheck.setOnClickListener(this);

    }

    private void showDishChecked() {
        // TODO get the dish category count here;
        int dishCategory = 1;

        mCategoryDetailLayout = (LinearLayout) mCartTotalLayout
                .findViewById(R.id.mCategoryDetailLayout);

        mCategoryGallery = new GridView[dishCategory];
        for (int i = 0; i < dishCategory; i++) {
            LinearLayout mCategoryListLayout = (LinearLayout) inflater.inflate(R.layout.catlist,
                    null);

            mCategoryGallery[i] = (GridView) mCategoryListLayout.findViewById(R.id.mCategoryItem);
            mCategoryGallery[i].setId(i);
            mCategoryGallery[i].setAdapter(mDishItem);
            // TODO get dish count of each category
            int mDishCount = mDishIds.size();
            int rowNum = (mDishCount % DISHNUM_IN_ONEROW == 0) ? mDishCount / DISHNUM_IN_ONEROW
                    : mDishCount / DISHNUM_IN_ONEROW + 1;
            mCategoryGallery[i].getLayoutParams().height = rowNum * HEIGHT_ONEROW;

            mCategoryDetailLayout.addView(mCategoryListLayout);
        }
    }

    private void showDishCount() {
        // TODO need to query the exact number of dish categories.
        int mdishCategory = 4;
        mTotalDishCountLayout = (LinearLayout) mCartTotalLayout
                .findViewById(R.id.mTotalDishCountLayout);

        LinearLayout mDishCountLayout = (LinearLayout) inflater.inflate(R.layout.catvalue, null);

        // TODO need to quey the categories of the dish, and each count of one type
        String mCate1Name = new String("Salad");
        String mCate1Count = new String("" + mDishList.size());

        mCate1 = (TextView) mDishCountLayout.findViewById(R.id.categoray1);
        mCate1.setText(mCate1Name + " : ");
        mDish1Count = (TextView) mDishCountLayout.findViewById(R.id.dishcount1);
        mDish1Count.setText(" " + mCate1Count);

        mCate2 = (TextView) mDishCountLayout.findViewById(R.id.categoray2);
        mCate2.setText(" hot food : ");
        mDish2Count = (TextView) mDishCountLayout.findViewById(R.id.dishcount2);
        mDish2Count.setText(" 4 ");
        mTotalDishCountLayout.addView(mDishCountLayout);

        LogUtils.logE("mCate1Count=" + mCate1Count);
    }

    public void onClick(View view) {
        // TODO Auto-generated method stub
        if (view == mOK) {
            // TODO open orderList activity.
            System.out.println("mOK is pressed");
            int total = mOrderManager.getTotalPrice();
            int tableId = mOrderManager.getTableId();
            ArrayList<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
            for (int i = 0; i < mDishIds.size(); i++) {
                Long dishId = mDishIds.get(i);
                int dishCount = mOrderManager.getDishCopy(dishId);
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setDishId(dishId);
                orderDetail.setNumber(tableId);
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

        } else if (view == mCheck) {
            // TODO go back to order activity.
            System.out.println("mCheck is pressed");
            finish();
        } else if (null == view) {
        }
    }

    public class categoryGridViewAdapter extends BaseAdapter {
        private Context mContext = null;

        private LayoutInflater inflater = null;

        final class ViewHolder {
            private ImageView mImageView = null;
            private TextView mDishName = null;
            private TextView mDishPrice = null;
            private Button mDescend = null;
            private Button mAdd = null;
            private TextView mCount = null;
            private int mId;
        }

        public categoryGridViewAdapter(Context c, LayoutInflater li) {
            mContext = c;
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mDishList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public void setDishItem(final ViewHolder viewHolder, final int position) {
            viewHolder.mImageView.setImageBitmap(ImageHelper.getImage(mDishList.get(position)
                    .getDishResouce()));
            LogUtils.logE("set dish item : position = " + position);
            viewHolder.mDishName.setText(mDishList.get(position).getDishName());
            viewHolder.mDishPrice.setText(String.valueOf(mDishList.get(position).getDishPrice()));
            viewHolder.mCount.setText(String.valueOf(mDishList.get(position).getDishCount()));
            LogUtils.logE(mDishList.get(position).getDishResouce());
            LogUtils.logE(mDishList.get(position).getDishName());
            LogUtils.logE("" + mDishList.get(position).getDishPrice());
            LogUtils.logE("" + mDishList.get(position).getDishCount());
            viewHolder.mDescend.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    mDishList.get(position)
                            .setDishCount(mDishList.get(position).getDishCount() - 1);
                    viewHolder.mCount.setText(String
                            .valueOf(mDishList.get(position).getDishCount()));
                    if (mDishList.get(position).getDishCount() == 0) {
                        mOrderManager.removeDish(mDishIds.get(mDishList.get(position).getId()),
                                mDbHelper.getDishCategoryId(mDishIds.get(mDishList.get(position)
                                        .getId())));
                    }
                    mOrderManager.setDishCopy(mDishIds.get(mDishList.get(position).getId()),
                            mDishList.get(position).getDishCount());
                }
            });
            viewHolder.mAdd.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.logE("mDishIds.get(mDishList.get(position).getId()) = "
                            + mDishIds.get(mDishList.get(position).getId()));
                    // TODO Auto-generated method stub
                    mDishList.get(position)
                            .setDishCount(mDishList.get(position).getDishCount() + 1);
                    viewHolder.mCount.setText(String
                            .valueOf(mDishList.get(position).getDishCount()));
                    mOrderManager.setDishCopy(mDishIds.get(mDishList.get(position).getId()),
                            mDishList.get(position).getDishCount());
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

    public class DishDetail {
        private int mId = 0;
        private String mDishResouceID = null;
        private String mDishName = null;
        private long mDishPrice = 0;
        private int mDishCount = 0;

        public DishDetail(int id, String mRes, String mName, long mPrice, int mCount) {
            mId = id;
            mDishResouceID = mRes;
            mDishName = mName;
            mDishPrice = mPrice;
            mDishCount = mCount;
        }

        public void setDishResouce(String mRes) {
            mDishResouceID = mRes;
        }

        public String getDishResouce() {
            return mDishResouceID;
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
