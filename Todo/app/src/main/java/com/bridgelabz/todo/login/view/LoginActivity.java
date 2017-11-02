package com.bridgelabz.todo.login.view;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bridgelabz.todo.R;
import com.bridgelabz.todo.base.BaseActivity;
import com.bridgelabz.todo.constant.Constant;
import com.bridgelabz.todo.login.presenter.LoginUserPresenter;
import com.bridgelabz.todo.register.view.RegisterActivity;
import com.bridgelabz.todo.MainPanelActivity;
import com.bridgelabz.todo.sqlitedatabase.SQLiteDatabaseHandler;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import static com.bridgelabz.todo.constant.Constant.logging_athentication_failed;
import static com.bridgelabz.todo.constant.Constant.no_internet_availabe;
import static com.bridgelabz.todo.constant.Constant.no_internet_connection;
import static com.bridgelabz.todo.constant.Constant.signing_in;
import static com.bridgelabz.todo.constant.Constant.user_firestore_data_email;
import static com.bridgelabz.todo.constant.Constant.user_firestore_fb_acc_permission;
import static com.bridgelabz.todo.constant.Constant.user_layouts;
import static com.bridgelabz.todo.constant.Constant.user_layouts_linear;
import static com.bridgelabz.todo.constant.Constant.user_login_canceled;
import static com.bridgelabz.todo.constant.Constant.user_login_connection;
import static com.bridgelabz.todo.constant.Constant.user_login_error;
import static com.bridgelabz.todo.constant.Constant.user_users_FirebaseFirestore;
import static com.bridgelabz.todo.constant.Constant.user_users_info_FirebaseFirestore;

public class LoginActivity extends BaseActivity implements View.OnClickListener, LoginActivityInterface {

    public static boolean firebaseLogin = false;
    Toolbar toolbar;
    LoginUserPresenter presenter;
    LoginButton loginButton;
    CallbackManager mCallbackManager;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    String email, pass;
    Button btn_Login, btn_Signup;
    EditText mEmail_User, mPass_User;
    SignInButton googleSign;
    GoogleSignInOptions mGoogleSignInOptions;
    CollectionReference mRef;
    ProgressDialog progress;
    private GoogleApiClient mGoogleApiClient;

    /*
     *  It will checked that the user is loggin in or note is is logging in then
     *  the activity is change to main panel otherwise if the user is note logging
     *  in the the logging activity is displayed.
     */

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

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() != null) {
                    SQLiteDatabaseHandler.DATABASE_NAME = mAuth.getCurrentUser().getUid() +".db";
                    startActivity(new Intent(LoginActivity.this, MainPanelActivity.class));
                }
            }
        };

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressShow(signing_in);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, user_login_canceled, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, user_login_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     *  AuthStateListener is used to check user is already logged in or not
     */

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void initView() {

        presenter = new LoginUserPresenter(this, this);

        mCallbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.LoginButtonFB);
        loginButton.setReadPermissions(user_firestore_data_email, user_firestore_fb_acc_permission);

        mEmail_User = (EditText) findViewById(R.id.mainEmail);
        mPass_User = (EditText) findViewById(R.id.mainPassword);
        btn_Login = (Button) findViewById(R.id.mainLogin);
        btn_Signup = (Button) findViewById(R.id.mainSignup);
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
        switch (v.getId()) {
            case R.id.mainLogin:

                if (isValid()) {
                    presenter.checkUserPresent(email, pass);
                    firebaseLogin = true;
                }
                break;

            case R.id.mainSignup:

                Intent register = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(register);

                break;

            case R.id.signInGoogle:

                mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                                Toast.makeText(getApplicationContext(), user_login_connection, Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions).build();

                signIn();

                break;

        }
    }

    /*
     *  It is used to check that the google logging susses
     *  or not if success then the activity changed otherwise logging activity is displayed
     *  as per that the facebook is worked.
     */

    private void signIn() {

        if (isNetworkConnected()) {

            if (isInternetAvailable()) {
                progressShow(signing_in);
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, Constant.RC_SIGN_IN);
            } else {
                Toast.makeText(this, no_internet_availabe, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, no_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == Constant.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /*
     *  firebaseAuthWithGoogle is used to google logging with the given auth result
     */

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            mRef = FirebaseFirestore.getInstance().collection(user_users_FirebaseFirestore).document(userId).collection(user_users_info_FirebaseFirestore);

                            Map<String, Object> userInfo = new HashMap<>();

                            userInfo.put(user_layouts, user_layouts_linear);

                            mRef.document(userId).set(userInfo);

                            progressDismiss();
                        } else {
                            progressDismiss();
                        }
                    }
                });
    }

    /*
     *  It is check that user entered is valied or not that is logging in
     */

    private boolean isValid() {
        boolean isValidData = false;

        getData();

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(pass)) {
            Toast.makeText(this, Constant.field_empty, Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail_User.setError(Constant.valid_email);
        } else {
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

    @Override
    public void progressShow(String msg) {
        progress = new ProgressDialog(this);
        progress.setMessage(msg);
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    public void progressDismiss() {
        progress.dismiss();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

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

    /*
     *  hanbdleFacebookAccess is used to check that is used success Logging then the facebook is check
     *  that the user is present or not if  present the it logging otherwise
     *  it is created  the user with that name
     */

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            mRef = FirebaseFirestore.getInstance().collection(user_users_FirebaseFirestore).document(userId).collection(user_users_info_FirebaseFirestore);

                            Map<String, Object> userInfo = new HashMap<>();

                            userInfo.put(user_layouts, user_layouts_linear);

                            mRef.document(userId).set(userInfo);

                            progressDismiss();

                            Intent i = new Intent(LoginActivity.this, MainPanelActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        } else {
                            progressDismiss();
                            LoginManager.getInstance().logOut();
                            Toast.makeText(LoginActivity.this, logging_athentication_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
