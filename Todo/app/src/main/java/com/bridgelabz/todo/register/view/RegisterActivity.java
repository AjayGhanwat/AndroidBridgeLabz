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

import com.bridgelabz.todo.MainPanelActivity;
import com.bridgelabz.todo.R;
import com.bridgelabz.todo.base.BaseActivity;
import com.bridgelabz.todo.constant.Constant;
import com.bridgelabz.todo.register.presenter.RegisterUserData;

import static com.bridgelabz.todo.R.string.email;
import static com.bridgelabz.todo.constant.Constant.enter_correct_password;
import static com.bridgelabz.todo.constant.Constant.enter_correct_password_contains;
import static com.bridgelabz.todo.constant.Constant.enter_correct_user_email;
import static com.bridgelabz.todo.constant.Constant.enter_correct_user_last_name;
import static com.bridgelabz.todo.constant.Constant.enter_correct_user_name;
import static com.bridgelabz.todo.constant.Constant.enter_correct_user_phone_number;
import static com.bridgelabz.todo.constant.Constant.password_must_Matched;
import static com.bridgelabz.todo.constant.Constant.regex_for_validation;
import static com.bridgelabz.todo.constant.Constant.regex_for_validation_password;
import static com.bridgelabz.todo.constant.Constant.regex_for_validation_phone;

public class RegisterActivity extends BaseActivity implements View.OnClickListener, RegisterData {

    Toolbar toolbar;

    String mFirst, mLast, mEmail, mPhone, mPass, mComparePass;

    RegisterUserData presenter;
    boolean isValide = true;
    ProgressDialog progress;
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

        user_First_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {

                    mFirst = user_First_name.getText().toString();

                    if (mFirst.isEmpty() || mFirst.length() > 20) {
                        user_First_name.setError(enter_correct_user_name);
                    } else if (!mFirst.matches(regex_for_validation)) {
                        user_First_name.setError(enter_correct_user_name);
                    }

                }
            }
        });

        user_Last_Name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {

                    mLast = user_Last_Name.getText().toString();

                    if (mLast.isEmpty() || mLast.length() > 20) {

                        user_Last_Name.setError(enter_correct_user_last_name);
                    } else if (!mLast.matches(regex_for_validation)) {

                        user_Last_Name.setError(enter_correct_user_last_name);
                    }
                }
            }
        });

        user_Email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mEmail = user_Email.getText().toString();

                    if (mEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                        user_Email.setError(enter_correct_user_email);
                    }
                }
            }
        });

        user_Phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mPhone = user_Phone.getText().toString();

                    if (mPhone.isEmpty() || mPhone.length() != 10) {

                        user_Phone.setError(enter_correct_user_phone_number);

                    } else if (!mPhone.matches(regex_for_validation_phone)) {
                        user_Phone.setError(enter_correct_user_phone_number);
                    }
                }
            }
        });
        user_First_Password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mPass = user_First_Password.getText().toString();
                    if (mPass.isEmpty() || mPass.length() < 6) {
                        user_First_Password.setError(enter_correct_password);
                    } else if (!mPass.matches(regex_for_validation_password)) {
                        user_First_Password.setError(enter_correct_password_contains);
                    }
                }
            }
        });
        user_Second_Password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mComparePass = user_Second_Password.getText().toString();
                    if (mComparePass.isEmpty() || !mComparePass.equals(mPass)) {
                        user_Second_Password.setError(password_must_Matched);
                    }
                }
            }
        });

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
        switch (v.getId()) {

            case R.id.signRegister:

                if (isValid()) {
                    presenter.getData(mFirst, mLast, mEmail, mPhone, mPass);
                }
                break;
        }
    }

    // It gets the user Entered Info from edit text

    void getEnteredData() {

        mFirst = user_First_name.getText().toString();
        mLast = user_Last_Name.getText().toString();
        mEmail = user_Email.getText().toString();
        mPhone = user_Phone.getText().toString();
        mPass = user_First_Password.getText().toString();
        mComparePass = user_Second_Password.getText().toString();

    }

    /*
     *  Check the user Entered Data is Valid or not.
     *  if valid then the next activity performed
     *  if not valid then it throws the error
     */

    private boolean isValid() {

        getEnteredData();

        if (mFirst.isEmpty() || mFirst.length() > 20) {
            user_First_name.setError(enter_correct_user_name);
            isValide = false;
        } else if (!mFirst.matches(regex_for_validation)) {
            user_First_name.setError(enter_correct_user_name);
            isValide = false;
        }


        if (mLast.isEmpty() || mLast.length() > 20) {
            user_Last_Name.setError(enter_correct_user_last_name);
            isValide = false;
        } else if (!mLast.matches(regex_for_validation)) {

            user_Last_Name.setError(enter_correct_user_last_name);
            isValide = false;
        }

        if (mEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            user_Email.setError(enter_correct_user_email);
            isValide = false;
        }


        if (mPhone.isEmpty() || mPhone.length() != 10) {

            user_Phone.setError(enter_correct_user_phone_number);
            isValide = false;

        } else if (!mPhone.matches(regex_for_validation_phone)) {
            user_Phone.setError(enter_correct_user_phone_number);
            isValide = false;
        }
        if (mPass.isEmpty() || mPass.length() < 6) {
            user_First_Password.setError(enter_correct_password);
            isValide = false;
        } else if (!mPass.matches(regex_for_validation_password)) {
            user_First_Password.setError(enter_correct_password_contains);
            isValide = false;
        }

        if (mComparePass.isEmpty() || !mComparePass.equals(mPass)) {
            user_Second_Password.setError(password_must_Matched);
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
