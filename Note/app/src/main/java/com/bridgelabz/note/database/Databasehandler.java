//package com.bridgelabz.note.database;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.DatabaseErrorHandler;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import com.bridgelabz.note.model.DataModel;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Databasehandler extends SQLiteOpenHelper{
//
//    private static final int DATABASE_VERSION = 1;
//
//    private static final String DATABASE_NAME = "notes";
//
//    private static final String TABLE_NOTE = "note";
//
//    private static final String KEY_ID = "key";
//    private static final String KEY_TITLE = "title";
//    private static final String KEY_DESC = "desc";
//    private static final String KEY_DATE = "date";
//    private static final String KEY_TIME = "time";
//    private static final String KEY_ARCHIVE = "archive";
//    private static final String KEY_TRASH = "trash";
//    private static final String KEY_COLOR = "color";
//    private static final String KEY_REMINDER = "reminder";
//    private static final String KEY_REMINDER_DATE = "reminderdate";
//    private static final String KEY_REMINDER_TIME = "remindertime";
//    private static final String KEY_USER_ID = "userid";
//
//
//
//    public Databasehandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
//        super(context, DATABASE_NAME, factory, DATABASE_VERSION, errorHandler);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//
//        String CREATE_TABLE_NOTE = "create table " + TABLE_NOTE + "(" + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_TITLE + " TEXT, "
//                + KEY_DESC + "TEXT, " + KEY_DATE + " TEXT, " + KEY_TIME + " TEXT, " + KEY_COLOR + " TEXT, " + KEY_ARCHIVE + " TEXT, " + KEY_TRASH + " TEXT, "
//                + KEY_REMINDER + " TEXT, " + KEY_REMINDER_DATE + " TEXT, " + KEY_REMINDER_TIME + " TEXT, "+ KEY_USER_ID + " TEXT " + ")";
//
//        db.execSQL(CREATE_TABLE_NOTE);
//
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
//        onCreate(db);
//    }
//
//    public void addNote(DataModel dataModel) {
//
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//
//        values.put(KEY_TITLE, dataModel.getTitle());
//        values.put(KEY_DESC, dataModel.getDesc());
//        values.put(KEY_ID, dataModel.getKey());
//        values.put(KEY_COLOR, dataModel.getColor());
//        values.put(KEY_DATE, dataModel.getDate());
//        values.put(KEY_TIME, dataModel.getTime());
//        values.put(KEY_ARCHIVE, dataModel.getArchive());
//        values.put(KEY_TRASH, dataModel.getTrash());
//        values.put(KEY_REMINDER, dataModel.getReminder());
//        values.put(KEY_REMINDER_DATE, dataModel.getReminderDate());
//        values.put(KEY_REMINDER_TIME, dataModel.getReminderTime());
//        values.put(KEY_USER_ID, dataModel.getUserid());
//
//        db.insert(TABLE_NOTE, null, values);
//        db.close();
//    }
//
//    public DataModel getNote(int id) {
//
//        SQLiteDatabase db = getWritableDatabase();
//
//        Cursor cursor = db.query(TABLE_NOTE, new String[] { KEY_TITLE, KEY_DESC, KEY_ID, KEY_COLOR, KEY_DATE,
//                        KEY_TIME, KEY_ARCHIVE, KEY_TRASH, KEY_REMINDER, KEY_REMINDER_DATE, KEY_REMINDER_TIME, KEY_USER_ID},
//                KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
//
//        if(cursor != null)
//            cursor.moveToFirst();
//
//        DataModel dataModel = new DataModel(cursor.getString(0), cursor.getString(1), cursor.getString(2),
//                Integer.parseInt(cursor.getString(3)), cursor.getString(4),
//                cursor.getString(5), Boolean.parseBoolean(cursor.getString(6)),
//                Boolean.parseBoolean(cursor.getString(7)), Boolean.parseBoolean(cursor.getString(8)),
//                cursor.getString(9), cursor.getString(10), cursor.getString(11));
//
//        return dataModel;
//    }
//
//    public List<DataModel> getAllNotes() {
//
//        ArrayList<DataModel> dataModels = new ArrayList<>();
//
//        String select_data = "SELECT * FROM " + TABLE_NOTE;
//
//        SQLiteDatabase db = getReadableDatabase();
//
//        Cursor cursor = db.rawQuery(select_data, null);
//
//        if(cursor.moveToFirst()){
//
//            do {
//
//                DataModel data = new DataModel();
//                data.setTitle(cursor.getString(0));
//                data.setDesc(cursor.getString(1));
//                data.setKey(cursor.getString(2));
//                data.setColor(Integer.parseInt(cursor.getString(3)));
//                data.setDate(cursor.getString(4));
//                data.setTime(cursor.getString(5));
//                data.setArchive(Boolean.parseBoolean(cursor.getString(6)));
//                data.setTrash(Boolean.parseBoolean(cursor.getString(7)));
//                data.setReminder(Boolean.parseBoolean(cursor.getString(8)));
//                data.setReminderDate(cursor.getString(9));
//                data.setReminderTime(cursor.getString(10));
//                data.setUserid(cursor.getString(11));
//
//                dataModels.add(data);
//
//            }while (cursor.moveToNext());
//        }
//
//        return dataModels;
//    }
//
//    public int getNoteCount() {
//
//        String select_all_data = "SELECT * FROM " + TABLE_NOTE;
//
//        SQLiteDatabase db = getReadableDatabase();
//
//        Cursor cursor = db.rawQuery(select_all_data, null);
//
//        cursor.close();
//
//        return cursor.getCount();
//
//    }
//
//    public int updateNote(DataModel dataModel) {
//
//        SQLiteDatabase db = getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//
//        values.put(KEY_TITLE, dataModel.getTitle());
//        values.put(KEY_DESC, dataModel.getDesc());
//        values.put(KEY_ID, dataModel.getKey());
//        values.put(KEY_COLOR, dataModel.getColor());
//        values.put(KEY_DATE, dataModel.getDate());
//        values.put(KEY_TIME, dataModel.getTime());
//        values.put(KEY_ARCHIVE, dataModel.getArchive());
//        values.put(KEY_TRASH, dataModel.getTrash());
//        values.put(KEY_REMINDER, dataModel.getReminder());
//        values.put(KEY_REMINDER_DATE, dataModel.getReminderDate());
//        values.put(KEY_REMINDER_TIME, dataModel.getReminderTime());
//        values.put(KEY_USER_ID, dataModel.getUserid());
//
//        return db.update(TABLE_NOTE, values, KEY_ID + " = ? ", new String[] {String.valueOf(dataModel.getKey())} );
//
//    }
//
//    public void deleteNote(DataModel dataModel) {
//
//        SQLiteDatabase db = getWritableDatabase();
//
//        db.delete(TABLE_NOTE, KEY_ID + " = ? ", new String[] {String.valueOf(dataModel.getKey())});
//
//        db.close();
//
//    }
//}
