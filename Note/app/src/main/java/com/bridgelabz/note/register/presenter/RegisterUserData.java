package com.bridgelabz.note.register.presenter;

import android.content.Context;

import com.bridgelabz.note.register.interacter.RegisterUser;
import com.bridgelabz.note.register.interacter.RegisterUserInterface;
import com.bridgelabz.note.register.view.RegisterData;

public class RegisterUserData implements RegisterUserDataInterface{

    RegisterData activity;
    RegisterUserInterface interacter;

    public RegisterUserData(Context context, RegisterData activity) {
        this.activity = activity;
        interacter = new RegisterUser(context, this);
    }

    @Override
    public void getData(String First, String Last, String email, String Phone, String pass) {
        interacter.registerUser(First, Last, email, Phone, pass);
    }

    @Override
    public void registerSucces(String msg) {
        activity.registerSuccesful(msg);
    }

    @Override
    public void registerUnsucces(String msg) {
        activity.registerUnsuccessful(msg);
    }

    @Override
    public void showProgressDialog(String message) {
        activity.showProgress(message);
    }

    @Override
    public void hideProgressDialog() {
        activity.dismissProgress();
    }
}
