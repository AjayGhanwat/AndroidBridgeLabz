package com.bridgelabz.note.login.presenter;

/**
 * Created by bridgeit on 16/9/17.
 */

public interface LoginUserPresenterInterface {

    void isLoginSuccces(String msg);
    void isLoginUnSucces(String msg);
    void showProgress(String msg);
    void dismissProgress();
}
