package com.bridgelabz.ajayghanwat.displaydetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class Login extends Activity {

    EditText etxtUserName, etxtxPassword;
    Button btnLogin;
    TextView clickHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etxtUserName = (EditText) findViewById(R.id.etxtUserName);
        etxtxPassword = (EditText) findViewById(R.id.etxtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        clickHere = (TextView) findViewById(R.id.txtClickHere);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Home.class);

                String user = etxtUserName.getText().toString();
                String pass = etxtxPassword.getText().toString();

                SharedPreferences sharedPreferences = getSharedPreferences("myfile", Context.MODE_PRIVATE);

                String user1 = sharedPreferences.getString("username", "Null");
                String pass1 = sharedPreferences.getString("password", "Null");

                if(user.equals(user1) && pass.equals(pass1)){
                    startActivity(i);
                }
                else
                    Toast.makeText(Login.this, "Enter Correct Data", Toast.LENGTH_LONG).show();

            }
        });

        clickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
            }
        });
    }
}
