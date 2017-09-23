package com.bridgelabz.note.login.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bridgelabz.note.R;
import com.bridgelabz.note.base.BaseActivity;
import com.bridgelabz.note.constant.Constant;
import com.bridgelabz.note.login.presenter.LoginUserPresenter;
import com.bridgelabz.note.view.MainPanelActivity;
import com.bridgelabz.note.register.view.RegisterActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.bridgelabz.note.constant.Constant.RC_SIGN_IN;

public class LoginActivity extends BaseActivity implements View.OnClickListener, LoginActivityInterface{

    Toolbar toolbar;

    LoginUserPresenter presenter;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    String email,pass;

    Button btn_Login, btn_Signup;
    EditText mEmail_User, mPass_User;

    SignInButton googleSign;

    GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(Constant.title_welcome);
        initView();
        clickListner();

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mAuth.getCurrentUser() != null){
                    startActivity(new Intent(LoginActivity.this, MainPanelActivity.class));
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void initView() {

        presenter = new LoginUserPresenter(this, this);

        mEmail_User = (EditText) findViewById(R.id.mainEmail);
        mPass_User = (EditText) findViewById(R.id.mainPassword);
        btn_Login = (Button) findViewById(R.id.mainLogin);
        btn_Signup = (Button)findViewById(R.id.mainSignup);
        googleSign = (SignInButton) findViewById(R.id.signInGoogle);
    }

    @Override
    public void clickListner() {

        btn_Login.setOnClickListener(this);
        btn_Signup.setOnClickListener(this);
        googleSign.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainLogin:

                if(isValid()){
                    presenter.checkUserPresent(email, pass);
                }
                break;

            case R.id.mainSignup:

                Intent register = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(register);

                break;

            case R.id.signInGoogle :

                gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                                Toast.makeText(getApplicationContext(), "Connection Faild!!", Toast.LENGTH_SHORT).show();

                            }
                        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

                signIn();

                break;
        }
    }

    private void signIn() {
        progressShow("Signing In..");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDismiss();
                        } else {
                            progressDismiss();
                        }
                    }
                });
    }


    private boolean isValid() {
        boolean isValidData = false;

        getData();

        if(TextUtils.isEmpty(email) && TextUtils.isEmpty(pass)){
            Toast.makeText(this, Constant.field_empty, Toast.LENGTH_SHORT).show();
        }
        else if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail_User.setError(Constant.valid_email);
        }else{
            isValidData = true;
        }

        return isValidData;
    }

    private void getData() {
        email = mEmail_User.getText().toString();
        pass = mPass_User.getText().toString();
    }

    @Override
    public void loginUserSucces(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, MainPanelActivity.class);
        startActivity(i);
    }

    @Override
    public void loginUserUnsucces(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    ProgressDialog progress;
    @Override
    public void progressShow(String msg) {
        progress = new ProgressDialog(this);
        progress.setMessage(msg);
        progress.show();
    }

    @Override
    public void progressDismiss() {
        progress.dismiss();
    }
}
