package com.bridgelabz.todo.register.interacter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bridgelabz.todo.constant.Constant;
import com.bridgelabz.todo.register.presenter.RegisterUserData;
import com.bridgelabz.todo.register.presenter.RegisterUserDataInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterUser implements RegisterUserInterface {

    Context context;
    RegisterUserDataInterface registerUserData;

    FirebaseFirestore db;
    CollectionReference mRef;

    public RegisterUser(Context context, RegisterUserData registerUserData) {
        this.context = context;
        this.registerUserData = registerUserData;

    }

    private FirebaseAuth mAuth;

    @Override
    public void registerUser(final String first, final String last, String email, final String phone, String pass) {
       registerUserData.showProgressDialog(Constant.signing_in);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();

                            mRef = db.collection("User").document(userId).collection("UserInfo");

                            Map<String, Object> userInfo = new HashMap<>();

                            userInfo.put(Constant.firebase_database_key_first, first);
                            userInfo.put(Constant.firebase_database_key_last, last);
                            userInfo.put(Constant.firebase_database_key_phone, phone);
                            userInfo.put("layout", "linear");

                            mRef.document(userId).set(userInfo);

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
