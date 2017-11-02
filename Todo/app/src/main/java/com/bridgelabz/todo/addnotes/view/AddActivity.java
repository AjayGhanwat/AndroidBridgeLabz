package com.bridgelabz.todo.addnotes.view;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bridgelabz.todo.R;
import com.bridgelabz.todo.addnotes.presenter.AddNotePresenter;
import com.bridgelabz.todo.base.BaseActivity;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.sqlitedatabase.SQLiteDatabaseHandler;
import com.bridgelabz.todo.view.ScheduleClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.kizitonwose.colorpreference.ColorDialog;
import com.kizitonwose.colorpreference.ColorShape;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.bridgelabz.todo.R.array.colorArray;
import static com.bridgelabz.todo.R.drawable.pin;
import static com.bridgelabz.todo.R.drawable.pinned;
import static com.bridgelabz.todo.constant.Constant.null_info;
import static com.bridgelabz.todo.constant.Constant.user_date_format;

public class AddActivity extends BaseActivity implements AddNotesInterface, ColorDialog.OnColorSelectedListener {

    public static String mTitleAlaram;
    public static String mDecsAlaram;
    public String mKey;
    public String userDate;
    EditText mTitle, mDecs;
    boolean isPinned = false;
    AddNotePresenter presenter;
    ProgressDialog progress;
    int year_x, month_x, day_x;
    int mYear, mMonth, mDay;
    Calendar calendar;
    int hour_x;
    int minute_x;
    int userColor = 16777215;
    CollectionReference reference;
    FirebaseAuth mAuth;
    private int Dialog_ID = 0;
    private int TimeDialog_ID = 1;
    private ScheduleClient scheduleClient;
    private DatePickerDialog.OnDateSetListener datepickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month + 1;
            day_x = dayOfMonth;

            mYear = year_x;
            mMonth = month_x;
            mDay = day_x;

            showTimeDialogPicker();

        }
    };
    private TimePickerDialog.OnTimeSetListener timePickerLitner = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnote);

        setTitle("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView();

        scheduleClient = new ScheduleClient(this);
        scheduleClient.doBindService();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();

        SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(this);
        ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecordAsc();
        int lastData = dataModels.size();

        if (lastData == 0) {

            Date cDate = new Date();
            String fDate = new SimpleDateFormat(user_date_format).format(cDate);
            mKey = null;
            userDate = fDate;
        } else {
            mKey = dataModels.get(lastData - 1).getKey();
            userDate = dataModels.get(lastData - 1).getDate();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {

        getDataStored();

        onBackPressed();

        return true;
    }

    private void getDataStored() {

        if (!TextUtils.isEmpty(mTitleAlaram) || !TextUtils.isEmpty(mDecsAlaram)) {

            String ReminderDate = null_info;
            String ReminderTime = null_info;
            boolean reminder = false;

            if (mYear != 0 && mMonth != 0 && mDay != 0) {
                Calendar c = Calendar.getInstance();
                c.set(mYear, mMonth - 1, mDay);
                c.set(Calendar.HOUR_OF_DAY, hour_x);
                c.set(Calendar.MINUTE, minute_x);
                c.set(Calendar.SECOND, 0);

                scheduleClient.setAlarmForNotification(c);

                String mMonth;

                reminder = true;

                if (month_x < 10 && month_x != 0) {
                    mMonth = "0" + month_x;
                } else {
                    mMonth = month_x + "";
                }

                ReminderDate = year_x + "-" + mMonth + "-" + day_x;

                ReminderTime = hour_x + "-" + minute_x;

                presenter.addnoteReminder(mTitleAlaram, mDecsAlaram, userColor, reminder, ReminderDate, ReminderTime, isPinned, mKey, userDate);
            } else {
                presenter.addnoteReminder(mTitleAlaram, mDecsAlaram, userColor, reminder, ReminderDate, ReminderTime, isPinned, mKey, userDate);
                if (scheduleClient != null)
                    scheduleClient.doUnbindService();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addnote, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        getEnterData();
        String ReminderDate = null_info;
        String ReminderTime = null_info;
        boolean reminder = false;

        int id = item.getItemId();

        if (id == R.id.pinNote) {

            if (!isPinned) {
                isPinned = true;
                item.setIcon(pinned);
            } else {
                isPinned = false;
                item.setIcon(pin);
            }

        } else if (id == R.id.alarmNote) {

            showDateDialogPicker();

        } else if (id == R.id.saveNote) {

            if (!TextUtils.isEmpty(mTitleAlaram) || !TextUtils.isEmpty(mDecsAlaram)) {

                if (mYear != 0 && mMonth != 0 && mDay != 0) {
                    Calendar c = Calendar.getInstance();
                    c.set(mYear, mMonth - 1, mDay);
                    c.set(Calendar.HOUR_OF_DAY, hour_x);
                    c.set(Calendar.MINUTE, minute_x);
                    c.set(Calendar.SECOND, 0);

                    scheduleClient.setAlarmForNotification(c);

                    String mMonth;

                    reminder = true;

                    if (month_x < 10 && month_x != 0) {
                        mMonth = "0" + month_x;
                    } else {
                        mMonth = month_x + "";
                    }

                    ReminderDate = year_x + "-" + mMonth + "-" + day_x;

                    ReminderTime = hour_x + "-" + minute_x;

                    presenter.addnoteReminder(mTitleAlaram, mDecsAlaram, userColor, reminder, ReminderDate, ReminderTime, isPinned, mKey, userDate);
                } else {
                    presenter.addnoteReminder(mTitleAlaram, mDecsAlaram, userColor, reminder, ReminderDate, ReminderTime, isPinned, mKey, userDate);
                    if (scheduleClient != null)
                        scheduleClient.doUnbindService();
                }
            }
            onBackPressed();
        } else if (id == R.id.colorNote) {
            new ColorDialog.Builder(this)
                    .setColorShape(ColorShape.CIRCLE)
                    .setColorChoices(colorArray)
                    .setSelectedColor(Color.WHITE)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getEnterData() {

        mTitleAlaram = mTitle.getText().toString();
        mDecsAlaram = mDecs.getText().toString();

    }

    @Override
    public void initView() {

        presenter = new AddNotePresenter(this, this);

        mTitle = (EditText) findViewById(R.id.addTitle);
        mDecs = (EditText) findViewById(R.id.addNotes);

    }

    @Override
    public void clickListner() {

    }

    @Override
    public void addNoteSuccesful(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void addNoteUnsuccesful(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressBar(String msg) {
        progress = new ProgressDialog(this);
        progress.setMessage(msg);
        progress.show();
    }

    @Override
    public void dismissProgressBar() {
        progress.dismiss();
    }

    @Override
    public void onColorSelected(int i, String s) {
        userColor = i;
        getWindow().getDecorView().setBackgroundColor(userColor);
    }

    public void showDateDialogPicker() {

        calendar = Calendar.getInstance();

        year_x = calendar.get(Calendar.YEAR);
        month_x = calendar.get(Calendar.MONTH);
        day_x = calendar.get(Calendar.DAY_OF_MONTH);

        showDialog(Dialog_ID);
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        if (Dialog_ID == id) {

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, datepickerListener, year_x, month_x, day_x);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            return datePickerDialog;
        } else if (TimeDialog_ID == id) {

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, timePickerLitner, hour_x, minute_x, false);
            return timePickerDialog;
        }

        return null;
    }

    private void showTimeDialogPicker() {
        showDialog(TimeDialog_ID);
    }

}