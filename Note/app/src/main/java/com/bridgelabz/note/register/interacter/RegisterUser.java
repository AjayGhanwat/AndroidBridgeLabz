package com.bridgelabz.note.register.interacter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bridgelabz.note.constant.Constant;
import com.bridgelabz.note.register.presenter.RegisterUserData;
import com.bridgelabz.note.register.presenter.RegisterUserDataInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser implements RegisterUserInterface {

    Context context;
    RegisterUserDataInterface registerUserData;


    public RegisterUser(Context context, RegisterUserData registerUserData) {
        this.context = context;
        this.registerUserData = registerUserData;

    }

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private FirebaseDatabase database;

    @Override
    public void registerUser(final String first, final String last, String email, final String phone, String pass) {
        registerUserData.showProgressDialog(Constant.signing_in);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUser = reference.child("Users").child(userId);

                            currentUser.child(Constant.firebase_database_key_first).setValue(first);
                            currentUser.child(Constant.firebase_database_key_last).setValue(last);
                            currentUser.child(Constant.firebase_database_key_phone).setValue(phone);
                            currentUser.child("layout").setValue("linear");

                            registerUserData.registerSucces(Constant.login_success);
                            registerUserData.hideProgressDialog();

                        } else {
                            registerUserData.registerUnsucces(Constant.register_unsuccess);
                            registerUserData.hideProgressDialog();

                        }
                    }
                });
    }
}
