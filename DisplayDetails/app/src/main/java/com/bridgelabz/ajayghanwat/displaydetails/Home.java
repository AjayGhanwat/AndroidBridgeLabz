package com.bridgelabz.ajayghanwat.displaydetails;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Home extends Activity {

    TextView vtxtUserName, vtxtPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        vtxtUserName = (TextView) findViewById(R.id.vtxtUsername);
        vtxtPassword = (TextView) findViewById(R.id.vtxtPassword);

        SharedPreferences sharedPreferences = getSharedPreferences("myfile", Context.MODE_PRIVATE);

        String user1 = sharedPreferences.getString("username", "Null");
        String pass1 = sharedPreferences.getString("password", "Null");

        vtxtUserName.setText(user1);
        vtxtPassword.setText(pass1);

    }
}
