package com.bridgelabz.note.login.view;

/**
 * Created by bridgeit on 16/9/17.
 */

public interface LoginActivityInterface {

    void loginUserSucces(String msg);
    void loginUserUnsucces(String msg);
    void progressShow(String msg);
    void progressDismiss();
}
