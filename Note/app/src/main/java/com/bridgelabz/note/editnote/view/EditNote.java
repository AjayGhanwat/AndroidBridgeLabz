package com.bridgelabz.note.editnote.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import com.bridgelabz.note.editnote.presenter.EditNotePresenter;
import com.bridgelabz.note.view.ScheduleClient;
import com.kizitonwose.colorpreference.ColorDialog;
import com.kizitonwose.colorpreference.ColorShape;

import java.util.Calendar;

import static android.R.attr.id;
import static com.bridgelabz.note.R.array.colorArray;
import static com.bridgelabz.note.R.drawable.pin;
import static com.bridgelabz.note.R.drawable.pinned;

public class EditNote extends BaseActivity implements EditNotesInterface, ColorDialog.OnColorSelectedListener{

    EditText usertitle, userdesc;

    String user_title, user_desc, user_key, user_date, user_reminder_date, user_reminder_time;
    int user_color;
    boolean user_reminder;

    String title,decs;

    boolean isPinned;

    EditNotePresenter presenter;

    private int Dialog_ID = 0;
    public static int year_x,month_x,day_x;
    Calendar calendar;
    public static int hour_x;
    public static int minute_x;
    private int TimeDialog_ID = 1;

    public static ScheduleClient scheduleClient;

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
        user_title = extras.getString("Title");
        user_desc = extras.getString("Desc");
        user_key = extras.getString("Key");
        user_color = extras.getInt("Color");
        user_date = extras.getString("Date");
        user_reminder_date = extras.getString("reminderDate");
        user_reminder_time = extras.getString("reminderTime");
        user_reminder = extras.getBoolean("reminder");
        isPinned = extras.getBoolean("pin");

        usertitle.setText(user_title);
        userdesc.setText(user_desc);

        if(user_color == 16777215) {

            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        }else{
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

    private void getDataStored() {
        String mMonth;

        if(month_x < 10 && minute_x != 0) {
            mMonth = "0" + month_x;
        }else {
            mMonth = month_x + "";
        }

        user_reminder_date = year_x + "-" + mMonth + "-" + day_x;

        user_reminder_time = hour_x + "-" + minute_x;

        if(user_reminder_date.equals("0-0-0")){
            user_reminder = false;
        }else{
            user_reminder = true;
        }

        presenter.editnote(title, decs, user_date, user_color, user_key, user_reminder,user_reminder_date, user_reminder_time, isPinned);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        getEnterData();

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

            String mMonth;

            if(month_x < 10) {
                mMonth = "0" + month_x;
            }else {
                mMonth = month_x + "";
            }

            user_reminder_date = year_x + "-" + mMonth + "-" + day_x;

            user_reminder_time = hour_x + "-" + minute_x;

            if(user_reminder_date.equals("0-0-0")){
                user_reminder = false;
            }else{
                user_reminder = true;
            }

            presenter.editnote(title, decs, user_date, user_color, user_key, user_reminder,user_reminder_date, user_reminder_time,isPinned);

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
        title = usertitle.getText().toString();
        decs = userdesc.getText().toString();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addnote, menu);

        if(isPinned) {
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

        usertitle = (EditText) findViewById(R.id.editTitle);
        userdesc = (EditText) findViewById(R.id.editDesc);
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

    ProgressDialog progress;

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
