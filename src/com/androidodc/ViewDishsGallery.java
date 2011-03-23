package com.androidodc;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewDishsGallery extends Activity {
    private ArrayList<DishObject> mDishs = new ArrayList<DishObject>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dishsgallery);
        Gallery g = (Gallery)findViewById(R.id.gallery);
        g.setSpacing(2);
        //TODO we should using db apis to get the mDishs contents
        initDishArray();
        g.setAdapter(new GalleryAdapter(this));
    }

    /*
     * It's a fake method to fill the dish array list
     */
    private void initDishArray() {
        for(int i = 0; i<5; i++) {
            String name = "Dish" + i;
            String price = "10." + i;
            String description = "The dish : Fish-" + i + " is my favorate, it tastes cool!";
            int resourceId = R.drawable.image_fish;
            DishObject obj = new DishObject(name, price, description, resourceId);
            mDishs.add(obj);
        }
    }

    public void onReturn(View v) {
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
            View mView = mInflater.inflate(R.layout.view_dish_detail, null);
            TextView mDishName = (TextView) mView.findViewById(R.id.dish_name);
            TextView mDishPrice = (TextView) mView.findViewById(R.id.dish_price);
            ImageView mDishImage = (ImageView) mView.findViewById(R.id.dish_image);
            TextView mDishIntroduction = (TextView) mView.findViewById(R.id.dish_introduction);

            mDishName.setText(mDishs.get(position).getName());
            mDishPrice.setText(mDishs.get(position).getPrice());
            mDishImage.setImageResource(mDishs.get(position).getResourceId());
            mDishIntroduction.setText(mDishs.get(position).getDescription());

            mView.setLayoutParams(new Gallery.LayoutParams(300, 300));
            return mView;
        }
    }

    private class DishObject {
        private String mDishName;
        private String mDishPrice;
        private String mDishIntroduction;
        private int mDishResourceId;

        public DishObject(String name, String price, String description, int id) {
            mDishName = name;
            mDishPrice = price;
            mDishIntroduction = description;
            mDishResourceId = id;
        }

        private String getName() {
            return mDishName;
        }

        private String getPrice() {
            return mDishPrice;
        }

        private String getDescription() {
            return mDishIntroduction;
        }

        private int getResourceId() {
            return mDishResourceId;
        }
    }
}
