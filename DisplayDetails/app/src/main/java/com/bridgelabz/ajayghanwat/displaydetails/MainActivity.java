package com.bridgelabz.ajayghanwat.displaydetails;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, Login.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, 3000);
    }
}
