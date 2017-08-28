package com.bridgelabz.util.samplelogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity implements View.OnClickListener{

    Button login,register;
    EditText username, password;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.btnLogin);
        register = (Button) findViewById(R.id.btnRegister);
        username = (EditText) findViewById(R.id.etName);
        password = (EditText) findViewById(R.id.etPass);

        login.setOnClickListener(this);
        register.setOnClickListener(this);

    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btnLogin:
                i = new Intent(this, HomeScreen.class);
                startActivity(i);
                break;

            case R.id.btnRegister:
                i = new Intent(this, Register.class);
                startActivity(i);
                break;

            default:

        }
    }
}
