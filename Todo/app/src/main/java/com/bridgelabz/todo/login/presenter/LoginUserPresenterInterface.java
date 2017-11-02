package com.bridgelabz.todo.login.presenter;

public interface LoginUserPresenterInterface {

    void isLoginSuccces(String msg);
    void isLoginUnSucces(String msg);
    void showProgress(String msg);
    void dismissProgress();
}
