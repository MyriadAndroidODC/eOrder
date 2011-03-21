package com.androidodc.eorder.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends Activity {

    private final int BACKGROUND_ALPHA = 66;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        View welcomeLayout = findViewById(R.id.layout_welcome);
        welcomeLayout.getBackground().setAlpha(BACKGROUND_ALPHA);

        TextView hotelName = (TextView) findViewById(R.id.hotel_name);
        hotelName.setText(getHotelName());

        Button enterButton = (Button) findViewById(R.id.enter_button);
        enterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                startActivity(new Intent(WelcomeActivity.this, TablesActivity.class));
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return false;
        }
        return true;
    }

    private String getHotelName() {
        // TODO: need to get the real name of the hotel.
        return "our Restaurant";
    }

}
