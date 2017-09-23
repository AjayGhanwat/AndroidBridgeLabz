package com.bridgelabz.note.addnotes.view;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import com.kizitonwose.colorpreference.ColorDialog;
import com.kizitonwose.colorpreference.ColorShape;

import java.util.Calendar;

import static android.R.attr.id;
import static com.bridgelabz.note.R.array.colorArray;

public class AddActivity extends BaseActivity implements AddNotesInterface, ColorDialog.OnColorSelectedListener {

    EditText mTitle, mDecs;

    String title, decs;

    AddNotePresenter presenter;
    ProgressDialog progress;

    private int Dialog_ID = 0;
    int year_x,month_x,day_x;
    Calendar calendar;
    int hour_x;
    int minute_x;
    private int TimeDialog_ID = 1;

    int userColor = 16777215;

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
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return true;
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

        if (id == R.id.alarmNote) {

            showDateDialogPicker();

        } else if (id == R.id.saveNote) {

            if(year_x != 0 && month_x != 0 && day_x != 0) {

                String mMonth;

                reminder = true;

                if(month_x < 10) {
                    mMonth = "0" + month_x;
                }else {
                    mMonth = month_x + "";
                }


                ReminderDate = year_x + "-" + mMonth + "-" + day_x;

                ReminderTime = hour_x + "-" + minute_x;

                presenter.addnoteReminder(title, decs, userColor, reminder, ReminderDate, ReminderTime);
            }else {
                presenter.addnoteReminder(title, decs, userColor, reminder, ReminderDate, ReminderTime);
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

    public void showDateDialogPicker(){

        calendar = Calendar.getInstance();

        year_x = calendar.get(Calendar.YEAR);
        month_x = calendar.get(Calendar.MONTH);
        day_x = calendar.get(Calendar.DAY_OF_MONTH);

        showDialog(Dialog_ID);
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        if(Dialog_ID == id){

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, datepickerListener, year_x, month_x, day_x);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            return datePickerDialog;
        }else if(TimeDialog_ID == id){

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, timePickerLitner, hour_x, minute_x, false);
            return timePickerDialog;
        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener datepickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month + 1;
            day_x = dayOfMonth;

            showTimeDialogPicker();

        }
    };

    private void showTimeDialogPicker() {
        showDialog(TimeDialog_ID);
    }

    private TimePickerDialog.OnTimeSetListener timePickerLitner = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;
        }
    };

}