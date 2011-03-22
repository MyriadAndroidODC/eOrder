package com.androidodc;

import com.androidodc.DishContent.DishColumns;
import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewDishDetailActivity extends Activity{
    private TextView mDishName;
    private TextView mDishPrice;
    private TextView mDishDescription;
    private ImageView mDishImage;
//    private Uri mDishUri;

    private final int CONTENT_ID_COLUMN = 0;
    private final int CONTENT_DISH_NAME_COLUMN = 1;
    private final int CONTENT_DISH_PRICE_COLUMN = 2;
    private final int CONTENT_DISH_DESCRIPTION_COLUMN = 3;
    private final int CONTENT_DISH_IMAGE_URI_COLUMN = 4;

    public static final String[] CONTENT_PROJECTION = new String[] {
        DishColumns.ID,
        DishColumns.DISH_NAME,
        DishColumns.DISH_PRICE,
        DishColumns.DISH_DESCRIPTION,
        DishColumns.DISH_IMAGE
    };

    private AsyncQueryHandler mQueryHandler;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dish_detail);
        initializeResourceRefs();
//        queryDishInfo();
        fillFakeDataForItems();
    }
    private void initializeResourceRefs() {
        mDishName = (TextView) findViewById(R.id.dish_name);
        mDishPrice = (TextView) findViewById(R.id.dish_price);
        mDishDescription = (TextView) findViewById(R.id.dish_description);
        mDishImage = (ImageView) findViewById(R.id.dish_image);
        mQueryHandler = new DishInfoQueryHandler(getContentResolver());
        //the dish item's uri will get from the intent
//        mDishUri = ContentUris.withAppendedId(DishContent.CONTENT_URI,
//                getIntent().getLongExtra(DishColumns.ID, 0));
    }

    private void queryDishInfo(){
        //this will unCommit after the real uri got 
//        mQueryHandler.startQuery(0, null, mDishUri, CONTENT_PROJECTION, null, null, null);
 
    }
    public void onReturn(View v) {
        finish();
    }

    /*
     * this method will be replaced by the read data
     */
    private void fillFakeDataForItems() {
        mDishName.setText("fish");
        mDishPrice.setText("10$");
        mDishDescription.setText("this is my favoriate dish");
        mDishImage.setImageResource(R.drawable.image_fish);
    }
    private final class DishInfoQueryHandler extends AsyncQueryHandler {
        public DishInfoQueryHandler (ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            try{
                if(cursor != null && cursor.moveToFirst()) {
                    mDishName.setText(cursor.getString(CONTENT_DISH_NAME_COLUMN));
                    mDishPrice.setText(cursor.getString(CONTENT_DISH_PRICE_COLUMN));
                    mDishDescription.setText(cursor.getString(CONTENT_DISH_DESCRIPTION_COLUMN));
                    mDishImage.setImageResource(cursor.getInt(CONTENT_DISH_IMAGE_URI_COLUMN));
                }
            } finally {
                cursor.close();
            }
        }
    }
}
