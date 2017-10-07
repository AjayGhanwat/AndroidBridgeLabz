package com.bridgelabz.todo.editnote.interacter;

import android.content.Context;

import com.bridgelabz.todo.addnotes.presenter.AddNotePresenterInterface;
import com.bridgelabz.todo.editnote.presenter.EditNotePresenter;
import com.bridgelabz.todo.editnote.presenter.EditNotePresenterInterface;
import com.bridgelabz.todo.editnote.view.EditNote;
import com.bridgelabz.todo.view.ScheduleClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.bridgelabz.todo.editnote.view.EditNote.day_x;
import static com.bridgelabz.todo.editnote.view.EditNote.hour_x;
import static com.bridgelabz.todo.editnote.view.EditNote.minute_x;
import static com.bridgelabz.todo.editnote.view.EditNote.month_x;
import static com.bridgelabz.todo.editnote.view.EditNote.year_x;


public class EditNoteInteracter implements EditNoteInteracterInterface {

    Context context;
    EditNotePresenterInterface presenter;

    public EditNoteInteracter(Context context, EditNotePresenter presenter) {

        this.context = context;
        this.presenter = presenter;

    }

    FirebaseAuth mAuth;
    CollectionReference coll;

    @Override
    public void editNotes(String title, String decs, String user_date, int user_color, String user_key, boolean user_reminder, String user_reminder_date, String user_reminder_time, boolean isPinned, String userID) {

        presenter.showProgresss("Editing Note");

        mAuth = FirebaseAuth.getInstance();

        String user = mAuth.getCurrentUser().getUid();

        coll = FirebaseFirestore.getInstance().collection("Data").document(user).collection("Notes");

        Map<String, Object> userEdited = new HashMap<>();

        userEdited.put("title", title);
        userEdited.put("desc", decs);
        userEdited.put("key", user_key);
        userEdited.put("date", user_date);
        userEdited.put("color", user_color);
        userEdited.put("reminder", user_reminder);
        userEdited.put("reminderdate", user_reminder_date);
        userEdited.put("remindertime", user_reminder_time);
        userEdited.put("pin", isPinned);

        coll.document(userID).update(userEdited);

        if(user_reminder){

            Calendar c = Calendar.getInstance();
            c.set(year_x, month_x - 1, day_x);
            c.set(Calendar.HOUR_OF_DAY, hour_x);
            c.set(Calendar.MINUTE, minute_x);
            c.set(Calendar.SECOND, 0);

            EditNote.scheduleClient.setAlarmForNotification(c);
        }

        presenter.editNoteSuccess("Success");
        presenter.dismissProgress();
    }
}
