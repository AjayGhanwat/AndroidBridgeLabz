package com.bridgelabz.todo.register.view;

public interface RegisterData {

    void registerSuccesful(String message);
    void registerUnsuccessful(String message);
    void showProgress(String message);
    void dismissProgress();

}
