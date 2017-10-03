package com.bridgelabz.note.addnotes.view;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bridgelabz.note.R;
import com.bridgelabz.note.addnotes.presenter.AddNotePresenter;
import com.bridgelabz.note.base.BaseActivity;
import com.bridgelabz.note.view.ScheduleClient;
import com.kizitonwose.colorpreference.ColorDialog;
import com.kizitonwose.colorpreference.ColorShape;

import java.util.Calendar;

import static android.R.attr.x;
import static com.bridgelabz.note.R.array.colorArray;
import static com.bridgelabz.note.R.drawable.ic_view_quilt_black_24dp;
import static com.bridgelabz.note.R.drawable.pin;
import static com.bridgelabz.note.R.drawable.pinned;

public class AddActivity extends BaseActivity implements AddNotesInterface, ColorDialog.OnColorSelectedListener {

    EditText mTitle, mDecs;

    public static String title;
    public static String decs;

    boolean isPinned = false;

    AddNotePresenter presenter;
    ProgressDialog progress;
    int year_x, month_x, day_x;
    int mYear, mMonth, mDay;
    Calendar calendar;
    int hour_x;
    int minute_x;
    int userColor = 16777215;
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
    public boolean onSupportNavigateUp() {

        getDataStored();

        onBackPressed();

        return true;
    }

    private void getDataStored() {

        String ReminderDate = "null";
        String ReminderTime = "null";
        boolean reminder = false;

        if (mYear != 0 && mMonth != 0 && mDay!= 0) {
            Calendar c = Calendar.getInstance();
            c.set(mYear, mMonth -1, mDay);
            c.set(Calendar.HOUR_OF_DAY, hour_x);
            c.set(Calendar.MINUTE, minute_x);
            c.set(Calendar.SECOND, 0);

            scheduleClient.setAlarmForNotification(c);

            String mMonth;

            reminder = true;

            if (month_x < 10 && month_x != 0 ) {
                mMonth = "0" + month_x;
            } else {
                mMonth = month_x + "";
            }

            ReminderDate = year_x + "-" + mMonth + "-" + day_x;

            ReminderTime = hour_x + "-" + minute_x;

            presenter.addnoteReminder(title, decs, userColor, reminder, ReminderDate, ReminderTime,isPinned);
        } else {
            presenter.addnoteReminder(title, decs, userColor, reminder, ReminderDate, ReminderTime,isPinned);
            if (scheduleClient != null)
                scheduleClient.doUnbindService();
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
        String ReminderDate = "null";
        String ReminderTime = "null";
        boolean reminder = false;

        int id = item.getItemId();

        if(id == R.id.pinNote){

            if (!isPinned) {
                isPinned = true;
                item.setIcon(pinned);
            }else{
                isPinned = false;
                item.setIcon(pin);
            }

            Toast.makeText(this, "Pinned Clicked", Toast.LENGTH_SHORT).show();

        }else if (id == R.id.alarmNote) {

            showDateDialogPicker();

        } else if (id == R.id.saveNote) {

            if (mYear != 0 && mMonth != 0 && mDay!= 0) {
                Calendar c = Calendar.getInstance();
                c.set(mYear, mMonth -1, mDay);
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

                presenter.addnoteReminder(title, decs, userColor, reminder, ReminderDate, ReminderTime,isPinned);
            } else {
                presenter.addnoteReminder(title, decs, userColor, reminder, ReminderDate, ReminderTime,isPinned);
                if (scheduleClient != null)
                    scheduleClient.doUnbindService();
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

        title = mTitle.getText().toString();
        decs = mDecs.getText().toString();

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