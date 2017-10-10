package com.bridgelabz.todo.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgelabz.todo.R;
import com.bridgelabz.todo.adapter.TrashDataAdapter;
import com.bridgelabz.todo.archivefragment.view.ArchiveFragment;
import com.bridgelabz.todo.login.view.LoginActivity;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.model.UserData;
import com.bridgelabz.todo.notefragment.View.NoteFragment;
import com.bridgelabz.todo.reminderfragment.view.ReminderFragment;
import com.bridgelabz.todo.trashfragment.interacter.TrashFragmentInteracter;
import com.bridgelabz.todo.trashfragment.view.TrashFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.IntegerArrayAdapter;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.data;
import static android.R.attr.id;
import static android.R.attr.x;
import static java.security.AccessController.getContext;

public class MainPanelActivity extends AppCompatActivity {

    public static MaterialSearchView materialSearchView;
    static Toolbar toolbar, toolbar1;
    static boolean isDeleteMode = false;
    static Window window;
    public static ArrayList<Integer> index;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
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
    StorageReference storageRef;
    String userID;
    Uri downloadUri;
    CollectionReference docRef;
    String download;
    public static boolean isOnClickEnable = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void change() {

        toolbar.setVisibility(View.GONE);
        toolbar1.setVisibility(View.VISIBLE);
        toolbar1.setTitle(" 0 ItemSelected");
        toolbar1.setDrawingCacheBackgroundColor(Color.GRAY);
        isDeleteMode = true;

    }

    public static void updateToolbarTitle(int toDelete) {
        toolbar1.setTitle(toDelete + " ItemSelected");
    }

    public static void getToDataAdd(int id) {
        index.add(id);
        updateToolbarTitle(index.size());
    }

    public static void getToDataDelete(int id) {

        for (int i = 0; i < index.size(); i++){
            if(id == index.get(i)){
                index.remove(i);
            }
        }
        updateToolbarTitle(index.size());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_panel);

        index = new ArrayList<>();

        window = getWindow();

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        docRef = FirebaseFirestore.getInstance().collection("User").document(userID).collection("UserInfo");

        storageRef = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        progress = new ProgressDialog(this);

        toolbar1 = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar1);
        toolbar1.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDeleteMode = false;
                toolbar1.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                TrashFragmentInteracter.dataAdapter.notifyDataSetChanged();
                isOnClickEnable = false;
            }
        });

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

                    Uri uri = data.getData();

                    try {

                        final StorageReference mRef = storageRef.child("Pic").child(userID);

                        mRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                downloadUri = taskSnapshot.getDownloadUrl();

                                Glide.with(getApplicationContext()).load(downloadUri).into(user_Pic);

                                Map<String, Object> userInfo = new HashMap<>();

                                userInfo.put("Pic", downloadUri.toString());

                                docRef.document(userID).update(userInfo);

                            }
                        });

                    } catch (Exception e) {
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

            docRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {

                    for (DocumentSnapshot document : documentSnapshots.getDocuments()) {

                        UserData info = document.toObject(UserData.class);

                        String mFirst = info.getFirst();
                        String mLast = info.getLast();

                        userName = mFirst + " " + mLast;

                        user_Name.setText(userName);
                    }

                }
            });

            isPicEditable = true;

        } else if (userName.equals("")) {

            docRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {

                    for (DocumentSnapshot document : documentSnapshots.getDocuments()) {

                        UserData info = document.toObject(UserData.class);

                        String mFirst = info.getFirst();
                        String mLast = info.getLast();

                        userName = mFirst + " " + mLast;

                        user_Name.setText(userName);
                    }
                }
            });

            isPicEditable = true;
        } else {

            user_Name.setText(userName);

            isPicEditable = false;

        }

        user_Email.setText(userEmail);

        if (user_Pic.getDrawable() != null) {

            docRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {

                    for (DocumentSnapshot document : documentSnapshots.getDocuments()) {

                        UserData userData = document.toObject(UserData.class);

                        download = userData.getPic();

                        if (download == null) {
                            user_Pic.setImageResource(R.mipmap.ic_launcher);
                        } else {
                            Glide.with(getApplicationContext()).load(download).into(user_Pic);
                        }

                    }

                }
            });

        } else {
            Glide.with(getApplicationContext())
                    .load(userPic.toString())
                    .into(user_Pic);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void displaySelectedFragment(int id) {


        Fragment fragment = null;
        fragName = "Note";

        if (id == R.id.userNotes) {
            toolbar.setTitle("Note");
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
            fragName = "Note";
            fragment = new NoteFragment();
        } else if (id == R.id.userReminders) {
            toolbar.setTitle("Reminder");
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorReminder));
            window.setStatusBarColor(getResources().getColor(R.color.colorReminder));
            fragName = "Reminder";
            fragment = new ReminderFragment();
        } else if (id == R.id.userArchive) {
            toolbar.setTitle("Archive");
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorArchive));
            window.setStatusBarColor(getResources().getColor(R.color.colorArchive));
            fragName = "Archive";
            fragment = new ArchiveFragment();
        } else if (id == R.id.userTrash) {
            toolbar.setTitle("Trash");
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorTrash));
            window.setStatusBarColor(getResources().getColor(R.color.colorTrash));
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

        toolbar1.inflateMenu(R.menu.onlognpressed);

        MenuItem item = menu.findItem(R.id.search);
        materialSearchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    ArrayList<DataModel> dataModels;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.layoutManager) {

            if (fragName.equals("Note")) {
                NoteFragment.onItemSelected(item);
            } else if (fragName.equals("Reminder")) {
                ReminderFragment.onItemSelected(item);
            } else if (fragName.equals("Archive")) {
                ArchiveFragment.onItemSelected(item);
            } else if (fragName.equals("Trash")) {
                TrashFragment.onItemSelected(item);
            }

        } else if (item.getItemId() == R.id.deleteNote) {

            dataModels = new ArrayList<>();

            final CollectionReference coll = FirebaseFirestore.getInstance().collection("Data").document(userID).collection("Notes");

            coll.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {

                    for (DocumentSnapshot doc : documentSnapshots.getDocuments()) {

                        DataModel getUserData = doc.toObject(DataModel.class);

                        dataModels.add(getUserData);
                    }

                    if (index.size() != 1) {
                        int x = 0;

                        for (int i = 0; i < index.size(); i++) {
                            for (int j = 0; j < index.size(); j++) {
                                x = 0;
                                if (index.get(i) == Integer.parseInt(TrashDataAdapter.list.get(j).getKey())) {
                                    do {
                                        if (TrashDataAdapter.list.get(j).getId().equals(dataModels.get(x).getId())) {
                                            coll.document(dataModels.get(x).getId()).delete();
                                            TrashFragment.resetRecyclerView();
                                        }
                                        x++;
                                    } while (x < dataModels.size());
                                }
                            }
                        }
                        TrashFragment.presenter.showSnacBar(index.size() + " Item Deleted!!");
                        index.clear();
                        onBackPressed();
                    }else{
                        Toast.makeText(MainPanelActivity.this, "Plase Select More Than 1", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.closeDrawer(Gravity.START);
        else if (fragName.equals("Trash")) {
            if (isDeleteMode) {
                isDeleteMode = false;
                toolbar1.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                isOnClickEnable = false;
                TrashFragmentInteracter.dataAdapter.notifyDataSetChanged();
            } else {
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
                super.onBackPressed();
            }
        } else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            super.onBackPressed();
        }
    }
}