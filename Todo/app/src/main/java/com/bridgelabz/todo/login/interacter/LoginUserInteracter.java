package com.bridgelabz.todo.login.interacter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.bridgelabz.todo.constant.Constant;
import com.bridgelabz.todo.login.presenter.LoginUserPresenter;
import com.bridgelabz.todo.login.presenter.LoginUserPresenterInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.net.InetAddress;

import static com.bridgelabz.todo.constant.Constant.no_internet_availabe;
import static com.bridgelabz.todo.constant.Constant.no_internet_connection;

public class LoginUserInteracter implements LoginUserInteracterInterface {

    Context context;
    LoginUserPresenterInterface presenter;

    FirebaseAuth mAuth;

    public LoginUserInteracter(Context context, LoginUserPresenter presenter) {
        this.context = context;
        this.presenter = presenter;
    }

    /*
     *  In the normal logging in that the user is present then the logging the user otherwise it toast the failed
     */

    @Override
    public void userPresent(String email, String pass) {

        if (isNetworkConnected()) {

            if (isInternetAvailable()) {
                mAuth = FirebaseAuth.getInstance();

                presenter.showProgress(Constant.logging_in);

                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            presenter.isLoginSuccces(Constant.logging_in_succes);
                            presenter.dismissProgress();
                        } else {
                            presenter.isLoginUnSucces(Constant.logging_in_faild);
                            presenter.dismissProgress();
                        }
                    }
                });

            } else
                Toast.makeText(context, no_internet_availabe, Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(context, no_internet_connection, Toast.LENGTH_SHORT).show();


    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("");
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }
}
