package com.bridgelabz.todo.addnotes.interacter;

import android.content.Context;

import com.bridgelabz.todo.addnotes.presenter.AddNotePresenter;
import com.bridgelabz.todo.addnotes.presenter.AddNotePresenterInterface;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.notefragment.interacter.NoteFragmentInteracter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddNotesInteracter implements AddNotesInteracterInterface {

    Context context;
    AddNotePresenterInterface presenter;

    CollectionReference mRef;

    public AddNotesInteracter(Context context, AddNotePresenter presenter) {

        this.context = context;
        this.presenter = presenter;

    }

    FirebaseAuth mAuth;

    @Override
    public void addNoteReminderFirebaase(String title, String decs, int userColor, boolean reminder, String reminderDate, String reminderTime, boolean isPinned,String Key, String userDate) {

        presenter.showProgresss("Adding Note");

        mAuth = FirebaseAuth.getInstance();

        String userID = mAuth.getCurrentUser().getUid();

        mRef = FirebaseFirestore.getInstance().collection("Data").document(userID).collection("Notes");

        String noteID = mRef.document().getId();

        String key1;

        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

        if (userDate != null) {
            if (userDate.equals(fDate)) {
                if (Key != null) {

                    int key = Integer.parseInt(Key);
                    key++;

                    key1 = key + "";
                } else {
                    key1 = "0";
                }
            } else {

                if (Key != null) {
                    int key = -1;
                    key++;

                    key1 = key + "";

                    userDate = fDate;

                } else {
                    key1 = "0";
                }
            }
        } else {
            userDate = fDate;
            key1 = "0";
        }

        Map<String, Object> note = new HashMap<>();

        note.put("title", title);
        note.put("desc", decs);
        note.put("key", key1);
        note.put("date", fDate);
        note.put("color", userColor);
        note.put("archive", false);
        note.put("trash", false);
        note.put("reminderdate", reminderDate);
        note.put("remindertime", reminderTime);
        note.put("reminder", reminder);
        note.put("pin", isPinned);
        note.put("id", noteID);

        mRef.document(noteID).set(note);

        presenter.addNoteSuccess("Success");
        presenter.dismissProgress();
    }
}
