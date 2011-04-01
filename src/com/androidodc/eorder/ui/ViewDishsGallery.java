package com.androidodc.eorder.ui;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.androidodc.eorder.database.DatabaseHelper;
import com.androidodc.eorder.datatypes.Dish;
import com.androidodc.eorder.order.OrderManager;
import com.androidodc.eorder.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class ViewDishsGallery extends Activity implements OnClickListener {
    private ArrayList<Dish> mDishs = new ArrayList<Dish>();
    public static final String SELECTED_DISH_ID = "selected_dish_id";
    private DatabaseHelper mDbHelper;
    private int mCurrentDishIndex;
    private Long mSelectedDishId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_detail_activity);
        InitUI();
    }

    private void InitUI() {
        mDbHelper = DatabaseHelper.getInstance();
        mCurrentDishIndex = 0;
        mSelectedDishId = getIntent().getLongExtra(SELECTED_DISH_ID, 0);

        getAllDishsForGallery();
        Gallery g = (Gallery) findViewById(R.id.gallery);
        g.setAdapter(new GalleryAdapter(this));
        g.setSelection(mCurrentDishIndex);

        Button returnButton = (Button)findViewById(R.id.return_button);
        returnButton.setOnClickListener(this);
    }

    private void getAllDishsForGallery() {
        List<Long> dishIdArray = mDbHelper.getSequencedDishIds();
        if (dishIdArray.isEmpty()) {
            LogUtils.logE("The dishs array is empty!!!!!!!!");
        } else if (dishIdArray.contains(mSelectedDishId)) {
            int index = 0;
            for (Long id : dishIdArray) {
                Dish dish = mDbHelper.getDishById(id);
                mDishs.add(index, dish);
                if (id == mSelectedDishId) {
                    mCurrentDishIndex = index;
                }
                index++;
            }
        } else {
            LogUtils.logE("The selected ID is wrong!!!!!!!");
        }
    }

    private class GalleryAdapter extends BaseAdapter {
        final class ViewHolder {
            ImageView dishImage;
            TextView dishName;
            TextView dishPrice;
            TextView dishIntroduction;
            CheckBox dishCheckBox;
        }

        private ViewHolder mViewHolder;
        private LayoutInflater mInflater;

        public GalleryAdapter(Context c) {
            mInflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return mDishs.size();
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
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.dish_detail_item, null);
                mViewHolder = new ViewHolder();
                mViewHolder.dishImage = (ImageView) convertView.findViewById(R.id.dish_image);
                mViewHolder.dishName = (TextView) convertView.findViewById(R.id.dish_name);
                mViewHolder.dishPrice = (TextView) convertView.findViewById(R.id.dish_price);
                mViewHolder.dishCheckBox = (CheckBox) convertView.findViewById(R.id.dish_select_unselect);
                mViewHolder.dishIntroduction = (TextView) convertView.findViewById(R.id.dish_introduction);

                int dishId = (int) mDishs.get(position).getDishId();
                setCheckBoxClickListener(dishId);
                // Set the dish attributes
                setDishDetails(position);
                convertView.setId(dishId);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
                updateDishStatus(parent.getId());
            }
            return convertView;
        }

        private void updateDishStatus(int id) {
            // TODO Auto-generated method stub

        }

        private void setDishDetails(int position) {
            mViewHolder.dishName.setText(mDishs.get(position).getName());
            mViewHolder.dishPrice.setText(String.valueOf(mDishs.get(position).getPrice()));
            mViewHolder.dishImage.setImageURI(Uri.parse(mDishs.get(position).getImageLocal()));
            mViewHolder.dishIntroduction.setText(mDishs.get(position).getDescription());
        }

        private void setCheckBoxClickListener(final int dishId) {
            mViewHolder.dishCheckBox.setId(dishId);
            OrderManager orderManager = OrderManager.getInstance();
            if (orderManager.isOrderedDish(dishId)) {
                mViewHolder.dishCheckBox.setChecked(true);
            } else {
                mViewHolder.dishCheckBox.setChecked(false);
            }
            /*mViewHolder.dishCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    OrderManager orderManager = OrderManager.getInstance();
                    DatabaseHelper dbHelper = DatabaseHelper.getInstance();
                    int categoryId = (int) dbHelper.getDishCategoryId(dishId);
                    if (isChecked) {
                        orderManager.addOneDish(dishId, categoryId);
                    } else {
                        orderManager.removeDish(dishId, categoryId);
                    }
                }
            });*/

        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (R.id.return_button == v.getId()) {
            setResult(RESULT_OK, null);
            finish();
        }
    }

}

