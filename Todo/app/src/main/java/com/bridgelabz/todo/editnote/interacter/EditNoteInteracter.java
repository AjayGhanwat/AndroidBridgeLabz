package com.bridgelabz.todo.editnote.interacter;

import android.content.Context;

import com.bridgelabz.todo.editnote.presenter.EditNotePresenter;
import com.bridgelabz.todo.editnote.presenter.EditNotePresenterInterface;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.sqlitedatabase.SQLiteDatabaseHandler;
import com.bridgelabz.todo.util.NetworkConnection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.bridgelabz.todo.constant.Constant.firebase_data_color;
import static com.bridgelabz.todo.constant.Constant.firebase_data_date;
import static com.bridgelabz.todo.constant.Constant.firebase_data_desc;
import static com.bridgelabz.todo.constant.Constant.firebase_data_key;
import static com.bridgelabz.todo.constant.Constant.firebase_data_pin;
import static com.bridgelabz.todo.constant.Constant.firebase_data_reminder;
import static com.bridgelabz.todo.constant.Constant.firebase_data_reminder_date;
import static com.bridgelabz.todo.constant.Constant.firebase_data_reminder_time;
import static com.bridgelabz.todo.constant.Constant.firebase_data_title;
import static com.bridgelabz.todo.constant.Constant.user_data_FirebaseFirestore;
import static com.bridgelabz.todo.constant.Constant.user_note_FirebaseFirestore;
import static com.bridgelabz.todo.constant.Constant.user_note_editing;

public class EditNoteInteracter implements EditNoteInteracterInterface {

    Context context;
    EditNotePresenterInterface presenter;
    FirebaseAuth mAuth;
    CollectionReference mCollectionReference;

    public EditNoteInteracter(Context context, EditNotePresenter presenter) {

        this.context = context;
        this.presenter = presenter;

    }

    /*
     * It will stored the info to the database
     */

    @Override
    public void editNotes(String title, String decs, String user_date, int user_color, String user_key, boolean user_reminder, String user_reminder_date, String user_reminder_time, boolean isPinned, String userID) {

        presenter.showProgresss(user_note_editing);

        mAuth = FirebaseAuth.getInstance();

        String user = mAuth.getCurrentUser().getUid();

        mCollectionReference = FirebaseFirestore.getInstance().collection(user_data_FirebaseFirestore).document(user).collection(user_note_FirebaseFirestore);

        if (NetworkConnection.isNetworkConnected(context)){

            if (NetworkConnection.isInternetAvailable()){

                Map<String, Object> userEdited = new HashMap<>();

                userEdited.put(firebase_data_title, title);
                userEdited.put(firebase_data_desc, decs);
                userEdited.put(firebase_data_key, user_key);
                userEdited.put(firebase_data_date, user_date);
                userEdited.put(firebase_data_color, user_color);
                userEdited.put(firebase_data_reminder, user_reminder);
                userEdited.put(firebase_data_reminder_date, user_reminder_date);
                userEdited.put(firebase_data_reminder_time, user_reminder_time);
                userEdited.put(firebase_data_pin, isPinned);

                mCollectionReference.document(userID).update(userEdited);

                DataModel dataModel = new DataModel();
                dataModel.setTitle(title);
                dataModel.setDesc(decs);
                dataModel.setKey(user_key);
                dataModel.setDate(user_date);
                dataModel.setColor(user_color);
                dataModel.setReminderDate(user_reminder_date);
                dataModel.setReminderTime(user_reminder_time);
                dataModel.setReminder(user_reminder);
                dataModel.setPin(isPinned);
                dataModel.setId(userID);

                SQLiteDatabaseHandler sqLiteDatabase = new SQLiteDatabaseHandler(context);
                sqLiteDatabase.updateRecord(dataModel);

            }

        }else{

            DataModel dataModel = new DataModel();
            dataModel.setTitle(title);
            dataModel.setDesc(decs);
            dataModel.setKey(user_key);
            dataModel.setDate(user_date);
            dataModel.setColor(user_color);
            dataModel.setReminderDate(user_reminder_date);
            dataModel.setReminderTime(user_reminder_time);
            dataModel.setReminder(user_reminder);
            dataModel.setPin(isPinned);
            dataModel.setId(userID);

            SQLiteDatabaseHandler sqLiteDatabase = new SQLiteDatabaseHandler(context);
            sqLiteDatabase.updateRecord(dataModel);

        }

        presenter.editNoteSuccess("Success");
        presenter.dismissProgress();
    }
}
