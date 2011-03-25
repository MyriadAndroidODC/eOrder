package com.androidodc.eorder.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidodc.eorder.database.DatabaseHelper;
import com.androidodc.eorder.datatypes.Dish;

import java.util.ArrayList;
import java.util.List;

public class ViewDishsGallery extends Activity {
    private ArrayList<Dish> mDishs = new ArrayList<Dish>();
    public static final String SELECTED_DISH_ID="selected_dish_id";
    private DatabaseHelper mDbHelper;
    private int mCurrentDishIndex;
    private Long mSelectedDishId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dishsgallery);
        Gallery g = (Gallery)findViewById(R.id.gallery);
        g.setSpacing(2);
        mDbHelper = DatabaseHelper.getInstance();
        mCurrentDishIndex = 0;
        mSelectedDishId = getIntent().getLongExtra(SELECTED_DISH_ID, 0);
        cacheAllDishs();
        g.setAdapter(new GalleryAdapter(this));
        g.setSelection(mCurrentDishIndex);
    }

    private void cacheAllDishs() {
        List<Long> dishIdArray =  mDbHelper.getSequencedDishIds();
        if(dishIdArray.isEmpty() || !dishIdArray.contains(mSelectedDishId)) {
            finish();
        } else {
            int index = 0;
            for(Long id : dishIdArray) {
                Dish dish = mDbHelper.getDishById(id);
                mDishs.add(index, dish);
                if(id == mSelectedDishId) {
                    mCurrentDishIndex = index;
                }
                index++;
            }
        }
    }

    public void onReturn(View v) {
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    private class GalleryAdapter extends BaseAdapter {
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
            final int dishId = (int) mDishs.get(position).getDishId();
            View mView = mInflater.inflate(R.layout.view_dish_detail, null);
            TextView mDishName = (TextView) mView.findViewById(R.id.dish_name);
            TextView mDishPrice = (TextView) mView.findViewById(R.id.dish_price);
            ImageView mDishImage = (ImageView) mView.findViewById(R.id.dish_image);
            TextView mDishIntroduction = (TextView) mView.findViewById(R.id.dish_introduction);
            final CheckBox mCheckBox = (CheckBox) mView.findViewById(R.id.dish_select_unselect);

            mDishName.setText(mDishs.get(position).getName());
            mDishPrice.setText(String.valueOf(mDishs.get(position).getPrice()));
            mDishImage.setImageURI(Uri.parse(mDishs.get(position).getImageLocal()));
            mDishIntroduction.setText(mDishs.get(position).getDescription());

            mCheckBox.setChecked(OrderManager.isOrderedDish(dishId));
            mCheckBox.setOnClickListener (new OnClickListener() {
                public void onClick(View v) {
                    int categoryId = (int) mDbHelper.getDishCategoryId(dishId);
                    if (mCheckBox.isChecked()) {
                        mCheckBox.setChecked(false);
                        OrderManager.removeDish(dishId, categoryId);
                    } else {
                        mCheckBox.setChecked(true);
                        OrderManager.addOneDish(dishId, categoryId);
                    }
                }
            });

            mView.setLayoutParams(new Gallery.LayoutParams(300, 300));
            return mView;
        }
    }

}
