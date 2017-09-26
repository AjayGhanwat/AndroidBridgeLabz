package com.bridgelabz.note.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgelabz.note.R;
import com.bridgelabz.note.archivefragment.view.ArchiveFragment;
import com.bridgelabz.note.login.view.LoginActivity;
import com.bridgelabz.note.model.UserData;
import com.bridgelabz.note.notefragment.View.NoteFragment;
import com.bridgelabz.note.reminderfragment.view.ReminderFragment;
import com.bridgelabz.note.trashfragment.view.TrashFragment;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.IOException;

public class MainPanelActivity extends AppCompatActivity {

    public static MaterialSearchView materialSearchView;
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
    boolean isPicEditable = false;
    int SELECT_IMAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_panel);

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mAuth = FirebaseAuth.getInstance();

        progress = new ProgressDialog(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        materialSearchView = (MaterialSearchView) findViewById(R.id.searchViewBar);

        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

                if (fragName.equals("Note")) {

                    NoteFragment.resetRecyclerView();

                } else if (fragName.equals("Reminder")) {

                    ReminderFragment.resetRecyclerView();

                } else if (fragName.equals("Archive")) {

                    ArchiveFragment.resetRecyclerView();

                } else if (fragName.equals("Trash")) {

                    TrashFragment.resetRecyclerView();

                }
            }
        });

        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (fragName.equals("Note")) {

                    NoteFragment.searchItem(newText);

                } else if (fragName.equals("Reminder")) {

                    ReminderFragment.searchItem(newText);

                } else if (fragName.equals("Archive")) {

                    ArchiveFragment.searchItem(newText);

                } else if (fragName.equals("Trash")) {

                    TrashFragment.searchItem(newText);

                }

                return true;
            }
        });

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

        isPicEdit();
    }

    private void isPicEdit() {
        if (isPicEditable == true) {
            user_Pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
                }
            });
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    userPic = data.getData();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());

                        user_Pic.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userName = user.getDisplayName();
        userEmail = user.getEmail();
        userPic = user.getPhotoUrl();

        user_Pic.setImageURI(userPic);

        if (userName == null) {

            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    UserData data = dataSnapshot.getValue(UserData.class);

                    String mFirst = data.getFirst();
                    String mLast = data.getLast();

                    userName = mFirst + " " + mLast;

                    user_Name.setText(userName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            isPicEditable = true;

        } else if (userName.equals("")) {

            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    UserData data = dataSnapshot.getValue(UserData.class);

                    String mFirst = data.getFirst();
                    String mLast = data.getLast();

                    userName = mFirst + " " + mLast;

                    user_Name.setText(userName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            isPicEditable = true;
        } else {

            user_Name.setText(userName);

            isPicEditable = false;

        }
        user_Email.setText(userEmail);

        if (user_Pic.getDrawable() != null) {

            user_Pic.setImageResource(R.mipmap.ic_launcher);

        } else {
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
            LoginManager.getInstance().logOut();

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

        MenuItem item = menu.findItem(R.id.search);
        materialSearchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.layoutManager) {

            if (fragName.equals("Note"))
                NoteFragment.onItemSelected(item);
            else if (fragName.equals("Reminder"))
                ReminderFragment.onItemSelected(item);
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
