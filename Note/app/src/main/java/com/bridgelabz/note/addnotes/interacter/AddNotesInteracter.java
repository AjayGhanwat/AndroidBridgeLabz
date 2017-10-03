package com.bridgelabz.note.addnotes.interacter;

import android.content.Context;

import com.bridgelabz.note.addnotes.presenter.AddNotePresenter;
import com.bridgelabz.note.addnotes.presenter.AddNotePresenterInterface;
import com.bridgelabz.note.notefragment.interacter.NoteFragmentInteracter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNotesInteracter implements AddNotesInteracterInterface {

    Context context;
    AddNotePresenterInterface presenter;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    public AddNotesInteracter(Context context, AddNotePresenter presenter) {

        this.context = context;
        this.presenter = presenter;

    }

    @Override
    public void addNoteReminderFirebaase(String title, String decs, int userColor, boolean reminder, String reminderDate, String reminderTime, boolean isPinned) {

        presenter.showProgresss("Adding Note");

        mAuth = FirebaseAuth.getInstance();

        String userID = mAuth.getCurrentUser().getUid();

        String key1;

        String previousDate = NoteFragmentInteracter.userDate;

        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

        if (previousDate != null) {
            if (previousDate.equals(fDate)) {
                if (NoteFragmentInteracter.Key != null) {
                    String id = NoteFragmentInteracter.Key;

                    int key = Integer.parseInt(id);
                    key++;

                    key1 = key + "";
                } else {
                    key1 = "0";
                }
            } else {

                if (NoteFragmentInteracter.Key != null) {
                    int key = -1;
                    key++;

                    key1 = key + "";

                    previousDate = fDate;

                } else {
                    key1 = "0";
                }
            }
        } else {
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
        reference.child("pin").setValue(isPinned);

        presenter.addNoteSuccess("Success");
        presenter.dismissProgress();
    }
}
