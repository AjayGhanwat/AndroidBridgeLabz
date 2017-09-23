package com.bridgelabz.note.addnotes.interacter;

import android.content.Context;
import android.content.SharedPreferences;

import com.bridgelabz.note.adapter.NoteDataAdapter;
import com.bridgelabz.note.addnotes.presenter.AddNotePresenter;
import com.bridgelabz.note.addnotes.presenter.AddNotePresenterInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.key;

public class AddNotesInteracter implements AddNotesInteracterInterface{

    Context context;
    AddNotePresenterInterface presenter;

    public AddNotesInteracter(Context context, AddNotePresenter presenter){

        this.context = context;
        this.presenter = presenter;

    }

    FirebaseAuth mAuth;
    DatabaseReference reference;

    @Override
    public void addNoteReminderFirebaase(String title, String decs,int userColor,boolean reminder, String reminderDate, String reminderTime) {

        presenter.showProgresss("Adding Note");

        mAuth = FirebaseAuth.getInstance();

        String userID = mAuth.getCurrentUser().getUid();

        String key1;

        String previousDate = NoteDataAdapter.date;

        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

        if(previousDate != null ) {
            if (previousDate.equals(fDate)) {
                if (NoteDataAdapter.key != null) {
                    String id = NoteDataAdapter.key;

                    int key = Integer.parseInt(id);
                    key++;

                    key1 = key + "";
                } else {
                    key1 = "0";
                }
            } else{

                if (NoteDataAdapter.key != null) {
                    int key = -1;
                    key++;

                    key1 = key + "";

                    previousDate = fDate;

                } else {
                    key1 = "0";
                }
            }
        }else{
            previousDate = fDate;
            key1 = "0";
        }

        reference = FirebaseDatabase.getInstance().getReference().child("Data").child(userID).child(fDate).child(key1);

        reference.child("title").setValue(title);
        reference.child("desc").setValue(decs);
        reference.child("key").setValue(key1);
        reference.child("date").setValue(fDate);
        reference.child("color").setValue(userColor);
        reference.child("archive").setValue(false);
        reference.child("trash").setValue(false);
        reference.child("reminderdate").setValue(reminderDate);
        reference.child("remindertime").setValue(reminderTime);
        reference.child("reminder").setValue(reminder);

        presenter.addNoteSuccess("Success");
        presenter.dismissProgress();
    }
}
