package com.bridgelabz.todo.addnotes.interacter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bridgelabz.todo.addnotes.presenter.AddNotePresenter;
import com.bridgelabz.todo.addnotes.presenter.AddNotePresenterInterface;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.sqlitedatabase.SQLiteDatabaseHandler;
import com.bridgelabz.todo.util.NetworkConnection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.bridgelabz.todo.constant.Constant.firebase_data_color;
import static com.bridgelabz.todo.constant.Constant.firebase_data_date;
import static com.bridgelabz.todo.constant.Constant.firebase_data_desc;
import static com.bridgelabz.todo.constant.Constant.firebase_data_id;
import static com.bridgelabz.todo.constant.Constant.firebase_data_key;
import static com.bridgelabz.todo.constant.Constant.firebase_data_pin;
import static com.bridgelabz.todo.constant.Constant.firebase_data_reminder;
import static com.bridgelabz.todo.constant.Constant.firebase_data_reminder_date;
import static com.bridgelabz.todo.constant.Constant.firebase_data_reminder_time;
import static com.bridgelabz.todo.constant.Constant.firebase_data_title;
import static com.bridgelabz.todo.constant.Constant.login_success;
import static com.bridgelabz.todo.constant.Constant.user_data_FirebaseFirestore;
import static com.bridgelabz.todo.constant.Constant.user_date_format;
import static com.bridgelabz.todo.constant.Constant.user_note_FirebaseFirestore;
import static com.bridgelabz.todo.constant.Constant.user_note_adding;
import static com.bridgelabz.todo.constant.Constant.user_note_firebase_database_arch;
import static com.bridgelabz.todo.constant.Constant.user_note_firebase_database_trash;

public class AddNotesInteracter implements AddNotesInteracterInterface {

    Context context;
    AddNotePresenterInterface presenter;

        CollectionReference mRef;
    FirebaseAuth mAuth;

    public AddNotesInteracter(Context context, AddNotePresenter presenter) {

        this.context = context;
        this.presenter = presenter;

    }

    @Override
    public void addNoteReminderFirebaase(String title, String decs, int userColor, boolean reminder, String reminderDate, String reminderTime, boolean isPinned, String Key, String userDate) {

        presenter.showProgresss(user_note_adding);

        mAuth = FirebaseAuth.getInstance();

        String userID = mAuth.getCurrentUser().getUid();

        mRef = FirebaseFirestore.getInstance().collection(user_data_FirebaseFirestore).document(userID).collection(user_note_FirebaseFirestore);

        String noteID = mRef.document().getId();

        String key1;

        Date cDate = new Date();
        String fDate = new SimpleDateFormat(user_date_format).format(cDate);

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

        if (NetworkConnection.isNetworkConnected(context)){

            if (NetworkConnection.isInternetAvailable()){

                Map<String, Object> note = new HashMap<>();

                note.put(firebase_data_title, title);
                note.put(firebase_data_desc, decs);
                note.put(firebase_data_key, key1);
                note.put(firebase_data_date, fDate);
                note.put(firebase_data_color, userColor);
                note.put(user_note_firebase_database_arch, false);
                note.put(user_note_firebase_database_trash, false);
                note.put(firebase_data_reminder_date, reminderDate);
                note.put(firebase_data_reminder_time, reminderTime);
                note.put(firebase_data_reminder, reminder);
                note.put(firebase_data_pin, isPinned);
                note.put(firebase_data_id, noteID);

                mRef.document(noteID).set(note);

                DataModel dataModel = new DataModel();
                dataModel.setTitle(title);
                dataModel.setDesc(decs);
                dataModel.setKey(key1);
                dataModel.setDate(fDate);
                dataModel.setColor(userColor);
                dataModel.setArchive(false);
                dataModel.setTrash(false);
                dataModel.setReminderDate(reminderDate);
                dataModel.setReminderTime(reminderTime);
                dataModel.setReminder(reminder);
                dataModel.setPin(isPinned);
                dataModel.setId(noteID);

                SQLiteDatabaseHandler sqLiteDatabase = new SQLiteDatabaseHandler(context);
                sqLiteDatabase.insertRecord(dataModel);

            }

        }else{

            DataModel dataModel = new DataModel();
            dataModel.setTitle(title);
            dataModel.setDesc(decs);
            dataModel.setKey(key1);
            dataModel.setDate(fDate);
            dataModel.setColor(userColor);
            dataModel.setArchive(false);
            dataModel.setTrash(false);
            dataModel.setReminderDate(reminderDate);
            dataModel.setReminderTime(reminderTime);
            dataModel.setReminder(reminder);
            dataModel.setPin(isPinned);
            dataModel.setId(noteID);

            SQLiteDatabaseHandler sqLiteDatabase = new SQLiteDatabaseHandler(context);
            sqLiteDatabase.insertRecord(dataModel);

        }

        presenter.addNoteSuccess(login_success);
        presenter.dismissProgress();
    }
}
