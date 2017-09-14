package com.bridgelabz.googlekeepdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private Button register;
    private EditText fname, lname, email, phone, pass1, pass2;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("Register");
        toolbar.setTitleMarginStart(20);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        progress = new ProgressDialog(this);

        fname = (EditText) findViewById(R.id.signFName);
        lname = (EditText) findViewById(R.id.signLName);
        email = (EditText) findViewById(R.id.signEmail);
        phone = (EditText) findViewById(R.id.signPhone);
        pass1 = (EditText) findViewById(R.id.signPassword);
        pass2 = (EditText) findViewById(R.id.signCPassword);
        register = (Button) findViewById(R.id.signRegister);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() != null) {
                    startActivity(new Intent(Register.this, UserMainPanel.class));
                }
            }
        };


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                valide();

                if (!valide()) {
                    Toast.makeText(getApplicationContext(), "Registration Faild!!", Toast.LENGTH_SHORT).show();
                } else {
                    signUpUser();
                }
            }

            private boolean valide() {

                String mNamef = fname.getText().toString();
                final String mNamel = lname.getText().toString();
                final String mPhone = phone.getText().toString();
                String mEmail = email.getText().toString();
                String mPassword = pass1.getText().toString();
                String mPasswordCon = pass2.getText().toString();

                boolean isValide = true;

                if (mNamef.isEmpty() || mNamef.length() > 20) {
                    fname.setError("Enter First Name");
                    isValide = false;
                }
                if (mNamel.isEmpty() || mNamel.length() > 20) {
                    lname.setError("Enter Last Name");
                    isValide = false;
                }
                if (mEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                    email.setError("Enter Valide Email");
                    isValide = false;
                }
                if (mPhone.isEmpty() || mPhone.length() != 10) {
                    phone.setError("Enter 10 digit Number");
                    isValide = false;
                }
                if (mPassword.isEmpty() || mPassword.length() < 6) {
                    pass1.setError("Minimum 6 Charachter");
                    isValide = false;
                }
                if (mPasswordCon.isEmpty() || !mPasswordCon.equals(mPassword)) {
                    pass2.setError("Password Not Matched!!");
                    isValide = false;
                }

                return isValide;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void signUpUser() {

        final String mNamef = fname.getText().toString();
        final String mNamel = lname.getText().toString();
        final String mPhone = phone.getText().toString();
        String mEmail = email.getText().toString();
        String mPassword = pass1.getText().toString();
        String mPasswordCon = pass2.getText().toString();

        if (TextUtils.isEmpty(mNamef) && TextUtils.isEmpty(mNamel) && TextUtils.isEmpty(mPhone) && TextUtils.isEmpty(mEmail) && TextUtils.isEmpty(mPassword) && TextUtils.isEmpty(mPasswordCon)) {

            Toast.makeText(getApplicationContext(), "Filled are Empty!!", Toast.LENGTH_SHORT).show();

        } else if (mPassword.equals(mPasswordCon)) {

            progress.setMessage("Signing Up..");
            progress.show();

            mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Registration Problem!!", Toast.LENGTH_SHORT).show();
                    } else {
                        String user_id = mAuth.getCurrentUser().getUid();

                        DatabaseReference current_user_data = mDatabase.child(user_id);

                        current_user_data.child("FirstName").setValue(mNamef);
                        current_user_data.child("LastName").setValue(mNamel);
                        current_user_data.child("Phone").setValue(mPhone);

                        progress.dismiss();

                        Intent i = new Intent(Register.this, UserMainPanel.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                }
            });

        }
    }
}