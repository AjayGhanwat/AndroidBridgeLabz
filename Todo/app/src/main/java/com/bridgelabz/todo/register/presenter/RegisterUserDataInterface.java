package com.bridgelabz.todo.register.presenter;

public interface RegisterUserDataInterface  {

    void getData(String First, String Last, String email, String Phone, String pass);
    void registerSucces(String msg);
    void registerUnsucces(String msg);
    void showProgressDialog(String message);
    void hideProgressDialog();

}
