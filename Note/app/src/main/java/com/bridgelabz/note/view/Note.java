package com.bridgelabz.note.view;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Note extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
