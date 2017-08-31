package com.bridgelabz.ajayghanwat.displaydetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity {

    EditText txtUser, txtPass;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtUser = (EditText) findViewById(R.id.etxtuser);
        txtPass = (EditText) findViewById(R.id.etxtpass);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Register.this, Login.class);

                SharedPreferences sharedPreferences = getSharedPreferences("myfile", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                String user = txtUser.getText().toString();
                String pass = txtPass.getText().toString();

                editor.putString("username", user);
                editor.putString("password", pass);
                editor.commit();

                Toast.makeText(Register.this, "Saved", Toast.LENGTH_LONG).show();

                startActivity(i);
            }
        });
    }
}
