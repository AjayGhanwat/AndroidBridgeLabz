package com.bridgelabz.note.login.interacter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.bridgelabz.note.R;
import com.bridgelabz.note.constant.Constant;
import com.bridgelabz.note.login.presenter.LoginUserPresenter;
import com.bridgelabz.note.login.presenter.LoginUserPresenterInterface;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginUserInteracter implements LoginUserInteracterInterface{

    Context context;
    LoginUserPresenterInterface presenter;

    FirebaseAuth mAuth;


    public LoginUserInteracter(Context context, LoginUserPresenter presenter) {
        this.context = context;
        this.presenter = presenter;
    }


    @Override
    public void userPresent(String email, String pass) {

        mAuth = FirebaseAuth.getInstance();

        presenter.showProgress(Constant.logging_in);

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    presenter.isLoginSuccces(Constant.logging_in_succes);
                    presenter.dismissProgress();
                }else{
                    presenter.isLoginUnSucces(Constant.logging_in_faild);
                    presenter.dismissProgress();
                }
            }
        });


    }
}
