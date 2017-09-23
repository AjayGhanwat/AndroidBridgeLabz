package com.bridgelabz.note.register.view;

public interface RegisterData {

    void registerSuccesful(String message);
    void registerUnsuccessful(String message);
    void showProgress(String message);
    void dismissProgress();

}
