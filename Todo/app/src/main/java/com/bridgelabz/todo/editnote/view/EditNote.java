package com.bridgelabz.todo.editnote.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bridgelabz.todo.R;
import com.bridgelabz.todo.base.BaseActivity;
import com.bridgelabz.todo.editnote.presenter.EditNotePresenter;
import com.bridgelabz.todo.view.ScheduleClient;
import com.kizitonwose.colorpreference.ColorDialog;
import com.kizitonwose.colorpreference.ColorShape;

import java.util.Calendar;

import static com.bridgelabz.todo.R.array.colorArray;
import static com.bridgelabz.todo.R.drawable.pin;
import static com.bridgelabz.todo.R.drawable.pinned;
import static com.bridgelabz.todo.constant.Constant.extra_intents_data_color;
import static com.bridgelabz.todo.constant.Constant.extra_intents_data_date;
import static com.bridgelabz.todo.constant.Constant.extra_intents_data_desc;
import static com.bridgelabz.todo.constant.Constant.extra_intents_data_key;
import static com.bridgelabz.todo.constant.Constant.extra_intents_data_noteID;
import static com.bridgelabz.todo.constant.Constant.extra_intents_data_pin;
import static com.bridgelabz.todo.constant.Constant.extra_intents_data_reminder;
import static com.bridgelabz.todo.constant.Constant.extra_intents_data_reminder_date;
import static com.bridgelabz.todo.constant.Constant.extra_intents_data_reminder_time;
import static com.bridgelabz.todo.constant.Constant.extra_intents_data_title;
import static com.bridgelabz.todo.constant.Constant.null_info;

public class EditNote extends BaseActivity implements EditNotesInterface, ColorDialog.OnColorSelectedListener {

    public static int year_x, month_x, day_x;
    public static int hour_x;
    public static int minute_x;
    public static ScheduleClient scheduleClient;
    EditText userTitle, userDesc;
    String user_title, user_desc, user_key, user_date, user_reminder_date, user_reminder_time, user_noteID;
    int user_color;
    boolean user_reminder;
    String mTitle, mDecs;
    boolean isPinned;
    EditNotePresenter presenter;
    Calendar mCalendar;
    ProgressDialog progress;
    private int Dialog_ID = 0;
    private int TimeDialog_ID = 1;
    private DatePickerDialog.OnDateSetListener datepickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month + 1;
            day_x = dayOfMonth;

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
        setContentView(R.layout.activity_edit_note);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("");

        initView();
        clickListner();

        Bundle extras = getIntent().getExtras();
        user_title = extras.getString(extra_intents_data_title);
        user_desc = extras.getString(extra_intents_data_desc);
        user_key = extras.getString(extra_intents_data_key);
        user_color = extras.getInt(extra_intents_data_color);
        user_date = extras.getString(extra_intents_data_date);
        user_reminder_date = extras.getString(extra_intents_data_reminder_date);
        user_reminder_time = extras.getString(extra_intents_data_reminder_time);
        user_reminder = extras.getBoolean(extra_intents_data_reminder);
        isPinned = extras.getBoolean(extra_intents_data_pin);
        user_noteID = extras.getString(extra_intents_data_noteID);

        userTitle.setText(user_title);
        userDesc.setText(user_desc);

        if (user_color == 16777215) {

            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        } else {
            getWindow().getDecorView().setBackgroundColor(user_color);
        }

        scheduleClient = new ScheduleClient(this);
        scheduleClient.doBindService();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public boolean onSupportNavigateUp() {

        if (scheduleClient != null)
            scheduleClient.doUnbindService();

        getDataStored();

        onBackPressed();
        return true;
    }

    /*
     *   It get the all the data while go for the onBackpressed
     *   It will go back then update the data in database if it is not null
     */

    private void getDataStored() {
        String mMonth;

        if (month_x != 0 && year_x != 0 && day_x != 0) {

            if (month_x < 10 && month_x != 0) {
                mMonth = "0" + month_x;
            } else {
                mMonth = month_x + "";
            }

            user_reminder_date = year_x + "-" + mMonth + "-" + day_x;

            user_reminder_time = hour_x + "-" + minute_x;

            if (user_reminder_date.equals("0-0-0")) {
                user_reminder = false;
            } else {
                user_reminder = true;
            }

            presenter.editnote(mTitle, mDecs, user_date, user_color, user_key, user_reminder, user_reminder_date, user_reminder_time, isPinned, user_noteID);
        } else if (!user_reminder_date.equals(null_info)) {
            presenter.editnote(mTitle, mDecs, user_date, user_color, user_key, user_reminder, user_reminder_date, user_reminder_time, isPinned, user_noteID);
        } else {
            user_reminder_date = null_info;
            user_reminder_time = null_info;

            presenter.editnote(mTitle, mDecs, user_date, user_color, user_key, user_reminder, user_reminder_date, user_reminder_time, isPinned, user_noteID);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        getEnterData();

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

            String mMonth;

            if (month_x != 0 && year_x != 0 && day_x != 0) {

                if (month_x < 10) {
                    mMonth = "0" + month_x;
                } else {
                    mMonth = month_x + "";
                }

                user_reminder_date = year_x + "-" + mMonth + "-" + day_x;

                user_reminder_time = hour_x + "-" + minute_x;

                if (user_reminder_date.equals("0-0-0")) {
                    user_reminder = false;
                } else {
                    user_reminder = true;
                }

                presenter.editnote(mTitle, mDecs, user_date, user_color, user_key, user_reminder, user_reminder_date, user_reminder_time, isPinned, user_noteID);

            } else if (!user_reminder_date.equals(null_info)) {
                presenter.editnote(mTitle, mDecs, user_date, user_color, user_key, user_reminder, user_reminder_date, user_reminder_time, isPinned, user_noteID);
            } else {
                user_reminder_date = null_info;
                user_reminder_time = null_info;

                presenter.editnote(mTitle, mDecs, user_date, user_color, user_key, user_reminder, user_reminder_date, user_reminder_time, isPinned, user_noteID);
            }

            onSupportNavigateUp();

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
        mTitle = userTitle.getText().toString();
        mDecs = userDesc.getText().toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addnote, menu);

        if (isPinned) {
            if (menu != null) {
                MenuItem item = menu.findItem(R.id.pinNote);
                if (item != null) {
                    item.setIcon(R.drawable.pinned);
                }
            }
        }

        return true;
    }

    @Override
    public void initView() {

        presenter = new EditNotePresenter(this, this);

        userTitle = (EditText) findViewById(R.id.editTitle);
        userDesc = (EditText) findViewById(R.id.editDesc);
    }

    @Override
    public void clickListner() {

    }

    @Override
    public void onColorSelected(int i, String s) {
        user_color = i;
        getWindow().getDecorView().setBackgroundColor(user_color);
    }

    @Override
    public void editNoteSuccesful(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void editNoteUnsuccesful(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressBar(String msg) {

        progress = new ProgressDialog(this);

        progress = new ProgressDialog(this);
        progress.setMessage(msg);
        progress.show();
    }

    @Override
    public void dismissProgressBar() {
        progress.dismiss();
    }

    public void showDateDialogPicker() {

        mCalendar = Calendar.getInstance();

        year_x = mCalendar.get(Calendar.YEAR);
        month_x = mCalendar.get(Calendar.MONTH);
        day_x = mCalendar.get(Calendar.DAY_OF_MONTH);

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
