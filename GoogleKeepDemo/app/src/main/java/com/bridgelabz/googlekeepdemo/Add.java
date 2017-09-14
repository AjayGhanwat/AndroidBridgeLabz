package com.bridgelabz.googlekeepdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Add extends AppCompatActivity {

    private static int id = -1;

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference reference;

    String userId;

    EditText title, note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.setTitle("          ");

        reference = FirebaseDatabase.getInstance().getReference();

        title = (EditText) findViewById(R.id.addTitle);
        note = (EditText) findViewById(R.id.addNotes);


        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

        userId = user.getUid();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.saveNote) {

            String userId = mAuth.getCurrentUser().getUid();

            String mTitle = title.getText().toString();
            String mNote = note.getText().toString();

            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

            int date1 = getIddata();
            String date ="" + date1 ;

            reference.child("data").child(userId).child(fDate).child(date).child("title").setValue(mTitle);
            reference.child("data").child(userId).child(fDate).child(date).child("note").setValue(mNote);
            reference.child("data").child(userId).child(fDate).child(date).child("id").setValue(date);
            finish();


        }

        return super.onOptionsItemSelected(item);
    }

    public static int getIddata(){

        Date cDate = new Date();

        String priviouseDate= "2017-09-14";

        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

        if(currentDate.equals(priviouseDate)){
            id++;
        }
        else
        {
            id = 0;
            String temp = priviouseDate;
            priviouseDate = currentDate;
            currentDate = temp;
        }
        return id;
    }

}
