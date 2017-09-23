package com.bridgelabz.note.view;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bridgelabz.note.R;
import com.bridgelabz.note.archivefragment.view.ArchiveFragment;
import com.bridgelabz.note.login.view.LoginActivity;
import com.bridgelabz.note.notefragment.View.NoteFragment;
import com.bridgelabz.note.trashfragment.view.TrashFragment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainPanelActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    ProgressDialog progress;

    FirebaseAuth mAuth;

    String userName;
    String userEmail;
    Uri userPic;

    NavigationView nav_view;

    TextView user_Name, user_Email;
    ImageView user_Pic;

    String fragName = "Note";

    String userLastName, userFirstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_panel);

        mAuth = FirebaseAuth.getInstance();

        progress = new ProgressDialog(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerMainMenu);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        toggle.setDrawerIndicatorEnabled(true);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nav_view = (NavigationView) findViewById(R.id.mainNavigationMenu);

        user_Name = (TextView) nav_view.getHeaderView(0).findViewById(R.id.textView2);
        user_Email = (TextView) nav_view.getHeaderView(0).findViewById(R.id.textView);
        user_Pic = (ImageView) nav_view.getHeaderView(0).findViewById(R.id.profile_image);

        showUserInfo();

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                displaySelectedFragment(id);

                return false;
            }
        });

        displaySelectedFragment(R.id.userNotes);


    }

    private void showUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userName = user.getDisplayName();
        userEmail = user.getEmail();
        userPic = user.getPhotoUrl();

        user_Name.setText(userName);
        user_Email.setText(userEmail);

        if (!userPic.equals(null)) {
            Glide.with(getApplicationContext())
                    .load(userPic.toString())
                    .into(user_Pic);
        }
    }

    private void displaySelectedFragment(int id) {


        Fragment fragment = null;
        fragName = "Note";

        if (id == R.id.userNotes) {
            toolbar.setTitle("Note");
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            fragName = "Note";
            fragment = new NoteFragment();
        } else if (id == R.id.userReminders) {
            toolbar.setTitle("Reminder");
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorReminder));
            fragName = "Reminder";
            fragment = new ReminderFragment();
        } else if (id == R.id.userArchive) {
            toolbar.setTitle("Archive");
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorArchive));
            fragName = "Archive";
            fragment = new ArchiveFragment();
        } else if (id == R.id.userTrash) {
            toolbar.setTitle("Trash");
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorTrash));
            fragName = "Trash";
            fragment = new TrashFragment();
        } else if (id == R.id.userLogout) {
            progress.setMessage("Loging Out");
            progress.show();
            mAuth.signOut();
            
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            progress.dismiss();
            startActivity(i);
        }

        if (fragment != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.userMain, fragment);
            ft.commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_panel_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.search_bar) {
            
        }else if(item.getItemId() == R.id.layoutManager) {
            if (fragName.equals("Note"))
                NoteFragment.onItemSelected(item);
            else if (fragName.equals("Archive"))
                ArchiveFragment.onItemSelected(item);
            else if (fragName.equals("Trash"))
                TrashFragment.onItemSelected(item);
        }

        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.closeDrawer(Gravity.START);
        else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            super.onBackPressed();
        }
    }
}
