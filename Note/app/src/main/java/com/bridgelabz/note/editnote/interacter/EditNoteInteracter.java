package com.bridgelabz.note.editnote.interacter;

import android.content.Context;

import com.bridgelabz.note.addnotes.presenter.AddNotePresenterInterface;
import com.bridgelabz.note.editnote.presenter.EditNotePresenter;
import com.bridgelabz.note.editnote.presenter.EditNotePresenterInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditNoteInteracter implements EditNoteInteracterInterface {

    Context context;
    EditNotePresenterInterface presenter;

    public EditNoteInteracter(Context context, EditNotePresenter presenter) {

        this.context = context;
        this.presenter = presenter;

    }

    FirebaseAuth mAuth;
    DatabaseReference reference;

    @Override
    public void editNotes(String title, String decs, String user_date, int user_color, String user_key, boolean user_reminder, String user_reminder_date, String user_reminder_time) {

        presenter.showProgresss("Editing Note");

        mAuth = FirebaseAuth.getInstance();

        String userID = mAuth.getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Data").child(userID).child(user_date).child(user_key);

        reference.child("title").setValue(title);
        reference.child("desc").setValue(decs);
        reference.child("key").setValue(user_key);
        reference.child("date").setValue(user_date);
        reference.child("color").setValue(user_color);
        reference.child("reminder").setValue(user_reminder);
        reference.child("reminderdate").setValue(user_reminder_date);
        reference.child("remindertime").setValue(user_reminder_time);

        presenter.editNoteSuccess("Success");
        presenter.dismissProgress();
    }
}
