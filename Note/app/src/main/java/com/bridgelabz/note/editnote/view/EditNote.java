package com.bridgelabz.note.editnote.view;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.bridgelabz.note.R;
import com.bridgelabz.note.addnotes.presenter.AddNotePresenter;
import com.bridgelabz.note.base.BaseActivity;
import com.bridgelabz.note.editnote.presenter.EditNotePresenter;
import com.kizitonwose.colorpreference.ColorDialog;
import com.kizitonwose.colorpreference.ColorShape;

import static com.bridgelabz.note.R.array.colorArray;

public class EditNote extends BaseActivity implements EditNotesInterface, ColorDialog.OnColorSelectedListener{

    EditText usertitle, userdesc;

    String user_title, user_desc, user_key, user_date;;
    int user_color;

    String title,decs;

    EditNotePresenter presenter;

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

        usertitle.setText(user_title);
        userdesc.setText(user_desc);

        if(user_color == 16777215) {

            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        }else{
            getWindow().getDecorView().setBackgroundColor(user_color);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        getEnterData();

        int id = item.getItemId();

        if (id == R.id.alarmNote) {


        } else if (id == R.id.saveNote) {

            presenter.editnote(title, decs, user_date, user_color, user_key);

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
}
