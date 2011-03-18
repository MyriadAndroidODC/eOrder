package com.androidodc.eorder.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends Activity {

    private TextView mWelcomeView = null;
    private Button mEnterButton = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        mWelcomeView = (TextView) findViewById(R.id.welcome);

        String welcome = getString(R.string.welcome, getHotelName());
        mWelcomeView.setText(welcome);

        mEnterButton = (Button) findViewById(R.id.enter_button);
        mEnterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO: The following code will be used, when TableAcitivity is created.
                // startActivity(new Intent(WelcomeActivity.this, TablesActivity.class));
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
