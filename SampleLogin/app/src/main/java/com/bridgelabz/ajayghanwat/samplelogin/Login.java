package com.bridgelabz.ajayghanwat.samplelogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class Login extends Activity {

    EditText userName, Password;
    Button register, display;
    TextView username, password, duser, dpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = (EditText) findViewById(R.id.etUserName);
        Password = (EditText) findViewById(R.id.etPassword);
        register = (Button) findViewById(R.id.btnRegister);
        display = (Button) findViewById(R.id.btnDisplay);
        username = (TextView) findViewById(R.id.txtUserName);
        password = (TextView) findViewById(R.id.txtPassword);
        duser = (TextView) findViewById(R.id.txtDUser);
        dpass = (TextView) findViewById(R.id.txtDPass);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = getSharedPreferences("myfile", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                String user = userName.getText().toString();
                String pass = Password.getText().toString();

                editor.putString("username", user);
                editor.putString("password", pass);
                editor.commit();

                Toast.makeText(Login.this, "Saved", Toast.LENGTH_LONG).show();

            }
        });

        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("myfile", Context.MODE_PRIVATE);
                String user1 = sharedPreferences.getString("username", "Null");
                String pass1 = sharedPreferences.getString("password", "Null");

                duser.setVisibility(View.VISIBLE);
                dpass.setVisibility(View.VISIBLE);

                username.setText(user1);
                password.setText(pass1);
            }
        });
    }
}
