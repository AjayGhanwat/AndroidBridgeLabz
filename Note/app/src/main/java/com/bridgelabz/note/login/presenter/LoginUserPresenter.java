package com.bridgelabz.note.login.presenter;

import android.content.Context;

import com.bridgelabz.note.login.interacter.LoginUserInteracter;
import com.bridgelabz.note.login.interacter.LoginUserInteracterInterface;
import com.bridgelabz.note.login.view.LoginActivity;

public class LoginUserPresenter implements LoginUserPresenterInterface {

    LoginActivity activity;
    LoginUserInteracterInterface viewInterface;

    public LoginUserPresenter(Context context, LoginActivity activity) {
        this.activity = activity;
        viewInterface = new LoginUserInteracter(context, this);
    }

    public void checkUserPresent(String email, String pass) {
        viewInterface.userPresent(email, pass);
    }


    @Override
    public void isLoginSuccces(String msg) {
        activity.loginUserSucces(msg);
    }

    @Override
    public void isLoginUnSucces(String msg) {
        activity.loginUserUnsucces(msg);
    }

    @Override
    public void showProgress(String msg) {
        activity.progressShow(msg);
    }

    @Override
    public void dismissProgress() {
        activity.progressDismiss();
    }
}
