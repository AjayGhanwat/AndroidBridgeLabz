package com.bridgelabz.googlekeepdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

public class UserMainPanel extends AppCompatActivity {


    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle abdt;
    Toolbar toolbar;

    Fragment fragment = null;

    TextView name,email;
    ImageView photo;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_panel);

        progress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
        };

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        abdt = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.Open, R.string.Close);

        abdt.setDrawerIndicatorEnabled(true);

        mDrawerLayout.addDrawerListener(abdt);
        abdt.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nav_view = (NavigationView) findViewById(R.id.navigationMain);

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                displaySelectedFragment(id);

                return false;
            }
        });

        name =(TextView) findViewById(R.id.textView2);
        email = (TextView) findViewById(R.id.textView);
        photo = (ImageView) findViewById(R.id.profile_image);
        displayUserInfo();

        displaySelectedFragment(R.id.userNotes);

    }

    private void displayUserInfo() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {


               final String uName = user.getDisplayName();
            final Uri uPhoto = user.getPhotoUrl();
            final String uEmail = user.getEmail();
      /*          name.setText(uName);
                email.setText(uEmail);

                Picasso.with(getApplicationContext())
                        .load(uPhoto.toString())
                        .resize(100, 100)
                        .into(photo);*/



        }
    }

    private void displaySelectedFragment(int id) {

        if(id == R.id.userNotes){
            fragment = new Note();
        }
        else if(id == R.id.userReminder){
            fragment = new Reminder();
        }
        else if(id == R.id.userNewLabel){
            fragment = new CreateLabel();
        }
        else if(id == R.id.userArchive){
            fragment = new Archive();
        }
        else if(id == R.id.userTrash){
            fragment = new Trash();
        }
        else if(id == R.id.userSetting){
            fragment = new Setting();
        }
        else if(id == R.id.userHelp){
            fragment = new Help();
        }
        else if(id == R.id.userLogout){
            progress.setMessage("Loging Out");
            progress.show();
            mAuth.signOut();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            progress.dismiss();
            startActivity(i);
        }

        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.userMain, fragment);
            ft.commit();
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.actionbarr, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(Gravity.START))
            mDrawerLayout.closeDrawer(Gravity.START);
        else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            super.onBackPressed();
        }
    }
}
