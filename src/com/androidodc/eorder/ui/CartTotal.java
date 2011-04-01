package com.androidodc.eorder.ui;

import android.app.Activity;
import android.content.Context;
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
import com.androidodc.eorder.datatypes.Dish;
import com.androidodc.eorder.order.OrderManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CartTotal extends Activity implements OnClickListener {
    private OrderManager orderManager = OrderManager.getInstance();
    private static DatabaseHelper mDBHelper = DatabaseHelper.getInstance();
    private List<Category> mOrderCategoryList;
    private List<Dish> mOrderDishList;
    // 1:Dish Id 2:Dish amount
    private HashMap<Long, Integer> mOrderCategoryDishesMap;

    private RelativeLayout mCartTotalLayout = null;

    // private TextView mOrderTitle = null;
    private LayoutInflater inflater = null;

    private LinearLayout mTotalDishCountLayout = null;
    private TextView mCate1 = null;
    private TextView mDish1Count = null;
    private TextView mCate2 = null;
    private TextView mDish2Count = null;

    private TextView mTotal = null;
    private TextView mPrice = null;

    private LinearLayout mCategoryDetailLayout = null;

    private Button mOK = null;
    private Button mCheck = null;

    private categoryGridViewAdapter mDishItem = null;
    private GridView[] mCategoryGallery;
    private DishDetail[] mDishList = new DishDetail[5];

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
        mOrderCategoryList = new ArrayList<Category>();
        mOrderDishList = new ArrayList<Dish>();
        mOrderCategoryDishesMap = new HashMap<Long,Integer>();
        
        List<Category> categoryList = mDBHelper.getAllCategorys();
        Set<Long> categoryIdSet = orderManager.getAllOrderedCategory();

        for (Category category : categoryList) {
            if (categoryIdSet.contains(category.getCategoryId())) {
                mOrderCategoryList.add(category);

                HashSet<Long> dishIdSet = orderManager.getOrderedDishByCategoryId(category
                        .getCategoryId());
                mOrderCategoryDishesMap.put(category.getCategoryId(), dishIdSet.size());

                for (Long dishId : dishIdSet) {
                    mOrderDishList.add(mDBHelper.getDishById(dishId));
                }
            }
        }
    }

    private void initUI() {
        inflater = LayoutInflater.from(this);

        mCartTotalLayout = (RelativeLayout) inflater.inflate(R.layout.carttotal, null);

        showHeaderInfo();

        showDishChecked();

        showConfirmButton();
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
            int mDishCount = 5;
            int rowNum = (mDishCount % DISHNUM_IN_ONEROW == 0) ? mDishCount / DISHNUM_IN_ONEROW
                    : mDishCount / DISHNUM_IN_ONEROW + 1;
            mCategoryGallery[i].getLayoutParams().height = rowNum * HEIGHT_ONEROW;

            mCategoryDetailLayout.addView(mCategoryListLayout);
        }
    }

    private void showHeaderInfo() {
        TextView orderTableInfo = (TextView) mCartTotalLayout.findViewById(R.id.orderTableInfo);
        orderTableInfo.setText(String.format(getString(R.string.orderTableInfo),(orderManager.getTableId() + 1) ));

        TextView categoryInfo = (TextView) mCartTotalLayout.findViewById(R.id.orderCategoryInfo);
        StringBuilder sb = new StringBuilder();
        for (Category category : mOrderCategoryList) {
            sb.append(category.getName()).append(": ").append(mOrderCategoryDishesMap.get(category.getCategoryId())).append("  ");
        }
        categoryInfo.setText(sb.toString());
        
        TextView totalCostInfo = (TextView) mCartTotalLayout.findViewById(R.id.orderTotalCostInfo);
        totalCostInfo.setText(String.format(getString(R.string.orderTotalCostInfo), orderManager.getTotalPrice()));
    }

    public void onClick(View view) {
        // TODO Auto-generated method stub
        if (view == mOK) {
            // TODO open orderList activity.
            System.out.println("mOK is pressed");
        } else if (view == mCheck) {
            // TODO go back to order activity.
            System.out.println("mCheck is pressed");
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
        }

        private Integer[] mThumbIds = { R.drawable.icon, R.drawable.icon, R.drawable.icon,
                R.drawable.icon, R.drawable.icon };

        public categoryGridViewAdapter(Context c, LayoutInflater li) {
            mContext = c;
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mThumbIds.length;
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
            // viewHolder.mImageView.setImageResource(mThumbIds[position]);
            // viewHolder.mDishName.setText(mDishList[position].getDishName());
            // viewHolder.mDishPrice.setText(String.valueOf(mDishList[position].getDishPrice()));
            // viewHolder.mCount.setText(String.valueOf(mDishList[position].getDishCount()));
            // viewHolder.mDescend.setOnClickListener(new OnClickListener() {
            // @Override
            // public void onClick(View v) {
            // // TODO Auto-generated method stub
            // mDishList[position].setDishCount(mDishList[position].getDishCount()
            // - 1);
            // viewHolder.mCount.setText(String.valueOf(mDishList[position].getDishCount()));
            // }
            // });
            // viewHolder.mAdd.setOnClickListener(new OnClickListener() {
            // @Override
            // public void onClick(View v) {
            // // TODO Auto-generated method stub
            // mDishList[position].setDishCount(mDishList[position].getDishCount()
            // + 1);
            // viewHolder.mCount.setText(String.valueOf(mDishList[position].getDishCount()));
            // }
            // });
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
        private Integer mDishResouceID = null;
        private String mDishName = null;
        private int mDishPrice = 0;
        private int mDishCount = 0;

        public DishDetail(Integer mRes, String mName, int mPrice, int mCount) {
            if (0 != mRes && null != mName && 0 != mPrice && 0 != mCount) {
                mDishResouceID = mRes;
                mDishName = mName;
                mDishPrice = mPrice;
                mDishCount = mCount;
            }
        }

        public void setDishResouce(Integer mRes) {
            if (null != mRes) {
                mDishResouceID = mRes;
            }
        }

        public Integer getDishResouce() {
            return mDishResouceID;
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

        public int getDishPrice() {
            return mDishPrice;
        }

        public void setDishCount(int mCount) {
            if (0 != mCount) {
                mDishCount = mCount;
            }
        }

        public int getDishCount() {
            return mDishCount;
        }
    }
}
