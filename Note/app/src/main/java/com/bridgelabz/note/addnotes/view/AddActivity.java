package com.bridgelabz.note.addnotes.view;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.bridgelabz.note.R;
import com.bridgelabz.note.addnotes.presenter.AddNotePresenter;
import com.bridgelabz.note.base.BaseActivity;
import com.kizitonwose.colorpreference.ColorDialog;
import com.kizitonwose.colorpreference.ColorShape;

import static com.bridgelabz.note.R.array.colorArray;

public class AddActivity extends BaseActivity implements AddNotesInterface, ColorDialog.OnColorSelectedListener {

    EditText mTitle, mDecs;

    String title, decs;

    AddNotePresenter presenter;
    ProgressDialog progress;

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

        int id = item.getItemId();

        if (id == R.id.alarmNote) {


        } else if (id == R.id.saveNote) {

            presenter.addnote(title, decs, userColor);

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

}