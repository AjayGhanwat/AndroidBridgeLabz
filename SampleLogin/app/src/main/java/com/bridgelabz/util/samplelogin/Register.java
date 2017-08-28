package com.bridgelabz.util.samplelogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends AppCompatActivity implements View.OnClickListener {

    Intent i;
    EditText user,pass;
    Button signup;
    TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user = (EditText) findViewById(R.id.etUsername);
        pass = (EditText) findViewById(R.id.etPassword);
        signup = (Button) findViewById(R.id.btnSignUp);
        back = (TextView) findViewById(R.id.tvBack);

        signup.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignUp:
                Toast.makeText(this, "Data Saved!!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.tvBack:
                i = new Intent(this, Login.class);
                startActivity(i);
                break;

            default:

        }
    }
}
