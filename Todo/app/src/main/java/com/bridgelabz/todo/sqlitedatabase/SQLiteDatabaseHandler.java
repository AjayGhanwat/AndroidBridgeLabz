package com.bridgelabz.todo.sqlitedatabase;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bridgelabz.todo.model.DataModel;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

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
import static com.bridgelabz.todo.constant.Constant.user_note_firebase_database_arch;
import static com.bridgelabz.todo.constant.Constant.user_note_firebase_database_trash;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static String DATABASE_NAME;

    public static final String TABLE_NAME = "Notes";
    public static final String TABLE_NAME_DELETE = "Notes_Delete";

    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + "("
                + firebase_data_title + " VARCHAR, "
                + firebase_data_desc + " VARCHAR, "
                + firebase_data_key + " VARCHAR, "
                + firebase_data_color + " VARCHAR, "
                + firebase_data_date + " VARCHAR, "
                + firebase_data_reminder + " BOOLEAN, "
                + firebase_data_reminder_date + " VARCHAR, "
                + firebase_data_reminder_time + " VARCHAR, "
                + firebase_data_pin + " BOOLEAN, "
                + firebase_data_id + " VARCHAR, "
                + user_note_firebase_database_arch + " BOOLEAN, "
                + user_note_firebase_database_trash + " BOOLEAN, unique(" + firebase_data_id + "));");

        sqLiteDatabase.execSQL("create table " + TABLE_NAME_DELETE + "("
                + firebase_data_title + " VARCHAR, "
                + firebase_data_desc + " VARCHAR, "
                + firebase_data_key + " VARCHAR, "
                + firebase_data_color + " VARCHAR, "
                + firebase_data_date + " VARCHAR, "
                + firebase_data_reminder + " BOOLEAN, "
                + firebase_data_reminder_date + " VARCHAR, "
                + firebase_data_reminder_time + " VARCHAR, "
                + firebase_data_pin + " BOOLEAN, "
                + firebase_data_id + " VARCHAR, "
                + user_note_firebase_database_arch + " BOOLEAN, "
                + user_note_firebase_database_trash + " BOOLEAN, unique(" + firebase_data_id + "));");
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    SQLiteDatabase sqLiteDatabase;

    public void insertRecord(DataModel dataModel){

        sqLiteDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(firebase_data_title, dataModel.getTitle());
        contentValues.put(firebase_data_desc, dataModel.getDesc());
        contentValues.put(firebase_data_key, dataModel.getKey());
        contentValues.put(firebase_data_color, dataModel.getColor());
        contentValues.put(firebase_data_date, dataModel.getDate());
        contentValues.put(firebase_data_reminder, dataModel.getReminder());
        contentValues.put(firebase_data_reminder_date, dataModel.getReminderDate());
        contentValues.put(firebase_data_reminder_time, dataModel.getReminderTime());
        contentValues.put(firebase_data_pin, dataModel.getPin());
        contentValues.put(firebase_data_id, dataModel.getId());
        contentValues.put(user_note_firebase_database_arch, dataModel.getArchive());
        contentValues.put(user_note_firebase_database_trash, dataModel.getTrash());

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();

    }

    public void insertRecordDel(DataModel dataModel){

        sqLiteDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(firebase_data_title, dataModel.getTitle());
        contentValues.put(firebase_data_desc, dataModel.getDesc());
        contentValues.put(firebase_data_key, dataModel.getKey());
        contentValues.put(firebase_data_color, dataModel.getColor());
        contentValues.put(firebase_data_date, dataModel.getDate());
        contentValues.put(firebase_data_reminder, dataModel.getReminder());
        contentValues.put(firebase_data_reminder_date, dataModel.getReminderDate());
        contentValues.put(firebase_data_reminder_time, dataModel.getReminderTime());
        contentValues.put(firebase_data_pin, dataModel.getPin());
        contentValues.put(firebase_data_id, dataModel.getId());
        contentValues.put(user_note_firebase_database_arch, dataModel.getArchive());
        contentValues.put(user_note_firebase_database_trash, dataModel.getTrash());

        sqLiteDatabase.insert(TABLE_NAME_DELETE, null, contentValues);
        sqLiteDatabase.close();

    }

    public void updateRecord(DataModel dataModel){

        sqLiteDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(firebase_data_title, dataModel.getTitle());
        contentValues.put(firebase_data_desc, dataModel.getDesc());
        contentValues.put(firebase_data_key, dataModel.getKey());
        contentValues.put(firebase_data_color, dataModel.getColor());
        contentValues.put(firebase_data_date, dataModel.getDate());
        contentValues.put(firebase_data_reminder, dataModel.getReminder());
        contentValues.put(firebase_data_reminder_date, dataModel.getReminderDate());
        contentValues.put(firebase_data_reminder_time, dataModel.getReminderTime());
        contentValues.put(firebase_data_pin, dataModel.getPin());
        contentValues.put(firebase_data_id, dataModel.getId());
        contentValues.put(user_note_firebase_database_arch, dataModel.getArchive());
        contentValues.put(user_note_firebase_database_trash, dataModel.getTrash());

        sqLiteDatabase.update(TABLE_NAME, contentValues,firebase_data_id + " = ? ", new String[] {dataModel.getId()});
        sqLiteDatabase.close();

    }

    public void deleteRecord(DataModel dataModel){
        sqLiteDatabase = this.getReadableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, firebase_data_id + " = ? ", new String[] {dataModel.getId()});
        sqLiteDatabase.close();
    }

    public void deleteRecordDel(DataModel dataModel){
        sqLiteDatabase = this.getReadableDatabase();
        sqLiteDatabase.delete(TABLE_NAME_DELETE, firebase_data_id + " = ? ", new String[] {dataModel.getId()});
        sqLiteDatabase.close();
    }

    public ArrayList<DataModel> getAllRecordAsc(){
        sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, null, null, null, null, firebase_data_date + " ," + firebase_data_key);
        ArrayList<DataModel> data = new ArrayList<>();
        DataModel dataModel;
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                dataModel = new DataModel();
                dataModel.setTitle(cursor.getString(0));
                dataModel.setDesc(cursor.getString(1));
                dataModel.setKey(cursor.getString(2));
                dataModel.setColor(cursor.getInt(3));
                dataModel.setDate(cursor.getString(4));
                Boolean flag2 = (cursor.getInt(5) == 1);
                dataModel.setReminder(flag2);
                dataModel.setReminderDate(cursor.getString(6));
                dataModel.setReminderTime(cursor.getString(7));
                Boolean val = (cursor.getInt(8) == 1);
                dataModel.setPin(val);
                dataModel.setId(cursor.getString(9));
                dataModel.setArchive((cursor.getInt(10) == 1));
                dataModel.setTrash((cursor.getInt(11) == 1));
                data.add(dataModel);
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return data;
    }

    public ArrayList<DataModel> getAllRecordDel(){
        sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME_DELETE, null, null, null, null, null, firebase_data_date + " DESC ," + firebase_data_key + " DESC");
        ArrayList<DataModel> data = new ArrayList<>();
        DataModel dataModel;
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                dataModel = new DataModel();
                dataModel.setTitle(cursor.getString(0));
                dataModel.setDesc(cursor.getString(1));
                dataModel.setKey(cursor.getString(2));
                dataModel.setColor(cursor.getInt(3));
                dataModel.setDate(cursor.getString(4));
                Boolean flag2 = (cursor.getInt(5) == 1);
                dataModel.setReminder(flag2);
                dataModel.setReminderDate(cursor.getString(6));
                dataModel.setReminderTime(cursor.getString(7));
                Boolean val = (cursor.getInt(8) == 1);
                dataModel.setPin(val);
                dataModel.setId(cursor.getString(9));
                dataModel.setArchive((cursor.getInt(10) == 1));
                dataModel.setTrash((cursor.getInt(11) == 1));
                data.add(dataModel);
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return data;
    }

    public ArrayList<DataModel> getAllRecord(){
        sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, null, null, null, null, firebase_data_date + " DESC ," + firebase_data_key + " DESC");
        ArrayList<DataModel> data = new ArrayList<>();
        DataModel dataModel;
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                dataModel = new DataModel();
                dataModel.setTitle(cursor.getString(0));
                dataModel.setDesc(cursor.getString(1));
                dataModel.setKey(cursor.getString(2));
                dataModel.setColor(cursor.getInt(3));
                dataModel.setDate(cursor.getString(4));
                Boolean flag2 = (cursor.getInt(5) == 1);
                dataModel.setReminder(flag2);
                dataModel.setReminderDate(cursor.getString(6));
                dataModel.setReminderTime(cursor.getString(7));
                Boolean val = (cursor.getInt(8) == 1);
                dataModel.setPin(val);
                dataModel.setId(cursor.getString(9));
                dataModel.setArchive((cursor.getInt(10) == 1));
                dataModel.setTrash((cursor.getInt(11) == 1));
                data.add(dataModel);
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return data;
    }

    public void deleteAll()
    {
        sqLiteDatabase = this.getReadableDatabase();
        sqLiteDatabase.execSQL("delete from "+ TABLE_NAME);
        sqLiteDatabase.close();
    }

    public void deleteAllDel()
    {
        sqLiteDatabase = this.getReadableDatabase();
        sqLiteDatabase.execSQL("delete from "+ TABLE_NAME_DELETE);
        sqLiteDatabase.close();
    }
}