package com.bridgelabz.todo.register.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bridgelabz.todo.R;
import com.bridgelabz.todo.base.BaseActivity;
import com.bridgelabz.todo.constant.Constant;
import com.bridgelabz.todo.login.view.LoginActivity;
import com.bridgelabz.todo.register.presenter.RegisterUserData;
import com.bridgelabz.todo.view.MainPanelActivity;

public class RegisterActivity extends BaseActivity implements View.OnClickListener, RegisterData {

    Toolbar toolbar;

    String First,Last,email,phone,pass,cpass;

    RegisterUserData presenter;

    private Button btn_Register;
    private EditText user_First_name, user_Last_Name, user_Email, user_Phone, user_First_Password, user_Second_Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(Constant.title_register);
        initView();
        clickListner();

    }

    @Override
    public void initView() {

        presenter = new RegisterUserData(this, this);

        user_First_name = (EditText) findViewById(R.id.signFName);
        user_Last_Name = (EditText) findViewById(R.id.signLName);
        user_Email = (EditText) findViewById(R.id.signEmail);
        user_Phone = (EditText) findViewById(R.id.signPhone);
        user_First_Password = (EditText) findViewById(R.id.signPassword);
        user_Second_Password = (EditText) findViewById(R.id.signCPassword);
        btn_Register = (Button) findViewById(R.id.signRegister);

    }

    @Override
    public void clickListner() {
        btn_Register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.signRegister:

                if(isValid()) {
                    presenter.getData(First, Last, email, phone, pass);
                }
                break;
        }
    }

    void getEnteredData(){

        First = user_First_name.getText().toString();
        Last = user_Last_Name.getText().toString();
        email = user_Email.getText().toString();
        phone = user_Phone.getText().toString();
        pass = user_First_Password.getText().toString();
        cpass = user_Second_Password.getText().toString();

    }

    private boolean isValid() {

        getEnteredData();

        boolean isValide = true;

        if (First.isEmpty() || First.length() > 20) {

            user_First_name.setError("Enter First Name");
            isValide = false;
        }else if(!First.matches("^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$")){

            user_First_name.setError("Enter Correct Name");
            isValide = false;
        }

        if (Last.isEmpty() || Last.length() > 20) {

            user_Last_Name.setError("Enter Last Name");
            isValide = false;
        }else if(!Last.matches("^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$")){

            user_Last_Name.setError("Enter Correct Last Name");
            isValide = false;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            user_Email.setError("Enter Valide Email");
            isValide = false;
        }

        if (phone.isEmpty() || phone.length() != 10) {

            user_Phone.setError("Enter 10 digit Number");
            isValide = false;

        }else if(!phone.matches("^[789]{1}[0-9]{9}$")){
            user_Phone.setError("Phone No Must be Started With 7,8,9");
            isValide = false;
        }

        if (pass.isEmpty() || pass.length() < 6 ) {
            user_First_Password.setError("Minimum 6 Charachter");
            isValide = false;
        }else if(!pass.matches("^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}[0-9]{0,}$")){
            user_First_Password.setError("Enter Only Capital and Lower Case Character and 0-9");
            isValide = false;
        }
        if (cpass.isEmpty() || !cpass.equals(pass)) {
            user_Second_Password.setError("Password Not Matched!!");
            isValide = false;
        }
        return isValide;
    }

    @Override
    public void registerSuccesful(String message) {
        Intent i = new Intent(this, MainPanelActivity.class);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        startActivity(i);
    }

    @Override
    public void registerUnsuccessful(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    ProgressDialog progress;
    @Override
    public void showProgress(String message) {
        progress = new ProgressDialog(this);
        progress.setMessage(message);
        progress.show();
    }

    @Override
    public void dismissProgress() {
        progress.dismiss();
    }
}