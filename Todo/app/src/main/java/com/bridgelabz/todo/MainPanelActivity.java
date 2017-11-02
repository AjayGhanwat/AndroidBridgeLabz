package com.bridgelabz.todo;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgelabz.todo.adapter.NoteDataAdapter;
import com.bridgelabz.todo.adapter.TrashDataAdapter;
import com.bridgelabz.todo.archive.view.ArchiveFragment;
import com.bridgelabz.todo.login.view.LoginActivity;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.model.UserData;
import com.bridgelabz.todo.note.View.NoteFragment;
import com.bridgelabz.todo.reminder.view.ReminderFragment;
import com.bridgelabz.todo.sqlitedatabase.SQLiteDatabaseHandler;
import com.bridgelabz.todo.trash.interacter.TrashFragmentInteracter;
import com.bridgelabz.todo.trash.view.TrashFragment;
import com.bridgelabz.todo.util.NetworkChangeReceiver;
import com.bridgelabz.todo.util.NetworkConnection;
import com.bumptech.glide.Glide;
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

import static com.bridgelabz.todo.constant.Constant.fragment_Archive;
import static com.bridgelabz.todo.constant.Constant.fragment_Note;
import static com.bridgelabz.todo.constant.Constant.fragment_Reminder;
import static com.bridgelabz.todo.constant.Constant.fragment_Trash;
import static com.bridgelabz.todo.constant.Constant.intents_selected_image;
import static com.bridgelabz.todo.constant.Constant.intents_selection_type;
import static com.bridgelabz.todo.constant.Constant.numberOf_Item_Selected;
import static com.bridgelabz.todo.constant.Constant.numberOf_Item_deleted;
import static com.bridgelabz.todo.constant.Constant.user_called_loging_out;
import static com.bridgelabz.todo.constant.Constant.user_called_logings;
import static com.bridgelabz.todo.constant.Constant.user_data_FirebaseFirestore;
import static com.bridgelabz.todo.constant.Constant.user_need_to_select_more_than_1;
import static com.bridgelabz.todo.constant.Constant.user_note_FirebaseFirestore;
import static com.bridgelabz.todo.constant.Constant.user_pic;
import static com.bridgelabz.todo.constant.Constant.user_users_FirebaseFirestore;
import static com.bridgelabz.todo.constant.Constant.user_users_info_FirebaseFirestore;
import static com.bridgelabz.todo.constant.Constant.zero_Item_Selected;

public class MainPanelActivity extends AppCompatActivity {

    public static MaterialSearchView materialSearchView;
    public static ArrayList<Integer> index;
    public static boolean isOnClickEnable = false;
    static Toolbar toolbar, toolbar1;
    static boolean isDeleteMode = false;
    static Window window;
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
    String fragName = fragment_Note;
    boolean isPicEditable = false;
    int SELECT_IMAGE;
    StorageReference storageRef;
    String userID;
    Fragment fragment = null;
    Uri downloadUri;
    CollectionReference docRef;
    String download;
    CollectionReference myDocReference;
    ArrayList<DataModel> dataModels;

    /*
    *   In trash fragment onLong Pressed the toolbar is changed to the deletion
    *   in the sigle open for deletion of note.
    *   The Completion of the deletion the it again change it to normal toolbar
    */

    public static void change() {

        toolbar.setVisibility(View.GONE);
        toolbar1.setVisibility(View.VISIBLE);
        toolbar1.setTitle(zero_Item_Selected);
        toolbar1.setDrawingCacheBackgroundColor(Color.GRAY);
        isDeleteMode = true;

    }

    // It will update the toolbar title as per the note selected

    public static void updateToolbarTitle(int toDelete) {
        toolbar1.setTitle(toDelete + numberOf_Item_Selected);
    }

    /*
     *  the multiple seletion the note id is stored in the defered arraylist to find which
     *  note need to be deleted from database
     */

    public static void getToDataAdd(int id) {
        index.add(id);
        updateToolbarTitle(index.size());
    }

    public static void getToDataDelete(int id) {

        for (int i = 0; i < index.size(); i++) {
            if (id == index.get(i)) {
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

        myDocReference = FirebaseFirestore.getInstance().collection(user_data_FirebaseFirestore).document(userID).collection(user_note_FirebaseFirestore);

        docRef = FirebaseFirestore.getInstance().collection(user_users_FirebaseFirestore).document(userID).collection(user_users_info_FirebaseFirestore);

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

                if (fragName.equals(fragment_Note)) {

                    NoteFragment.resetRecyclerView();

                } else if (fragName.equals(fragment_Reminder)) {

                    ReminderFragment.resetRecyclerView();

                } else if (fragName.equals(fragment_Archive)) {

                    ArchiveFragment.resetRecyclerView();

                } else if (fragName.equals(fragment_Trash)) {

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

                if (fragName.equals(fragment_Note)) {

                    NoteFragment.searchItem(newText);

                } else if (fragName.equals(fragment_Reminder)) {

                    ReminderFragment.searchItem(newText);

                } else if (fragName.equals(fragment_Archive)) {

                    ArchiveFragment.searchItem(newText);

                } else if (fragName.equals(fragment_Trash)) {

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

    /*
     *  after completion of search the recycler data is change to previous data (Refresh the data)
     */

    void refreshUserDataRecycler() {

        if (fragName.equals(fragment_Note)) {

            NoteFragment.updateNewData(myDocReference);

        } else if (fragName.equals(fragment_Reminder)) {

            ReminderFragment.updateNewData(myDocReference);

        } else if (fragName.equals(fragment_Archive)) {

            ArchiveFragment.updateNewData(myDocReference);

        } else if (fragName.equals(fragment_Trash)) {

            TrashFragment.updateNewData(myDocReference);

        }

    }

    /*
     *  When the DataBase changes made the auto changes has been made in data in running app.
     */

    @Override
    protected void onStart() {
        super.onStart();

        if (fragName.equals(fragment_Note)) {

            NoteFragment.updateNewData(myDocReference);

        } else if (fragName.equals(fragment_Reminder)) {

            ReminderFragment.updateNewData(myDocReference);

        } else if (fragName.equals(fragment_Archive)) {

            ArchiveFragment.updateNewData(myDocReference);

        } else if (fragName.equals(fragment_Trash)) {

            TrashFragment.updateNewData(myDocReference);

        }
    }

    // if the user logging as firebase logging (Normal Loging) then the user pic is Editable.

    private void isPicEdit() {
        if (isPicEditable) {
            user_Pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType(intents_selection_type);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, intents_selected_image), SELECT_IMAGE);
                }
            });
        }
    }

    /*
     *  onActivityResult is used to getting the result (fetch the result) after the completion
     *  task then as per the result the different action performed.
     */

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {

                    Uri uri = data.getData();

                    try {

                        if (NetworkConnection.isNetworkConnected(getApplicationContext())){

                            if (NetworkConnection.isInternetAvailable()){

                                final StorageReference mRef = storageRef.child("Pic").child(userID);

                                mRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        downloadUri = taskSnapshot.getDownloadUrl();

                                        Glide.with(getApplicationContext()).load(downloadUri).into(user_Pic);

                                        Map<String, Object> userInfo = new HashMap<>();

                                        userInfo.put(user_pic, downloadUri.toString());

                                        docRef.document(userID).update(userInfo);

                                    }
                                });

                            }

                        }else{

                            Glide.with(getApplicationContext()).load(uri).into(user_Pic);

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, user_called_logings, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     *  showUserInfo is used to display the user logging info on the drawer
     *  in that email, name and user pic
     */

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

    /*
     *   displaySelectedFragment is used to display the different fragments to the single activity
     *   eg Note, Reminder, Archive and Trash.
     */

    private void displaySelectedFragment(int id) {

        fragName = fragment_Note;

        if (id == R.id.userNotes) {
            toolbar.setTitle(fragment_Note);
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
            }
            fragName = fragment_Note;
            fragment = new NoteFragment();
            refreshUserDataRecycler();
        } else if (id == R.id.userReminders) {
            toolbar.setTitle(fragment_Reminder);
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorReminder));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.colorReminder));
            }
            fragName = fragment_Reminder;
            fragment = new ReminderFragment();
            refreshUserDataRecycler();
        } else if (id == R.id.userArchive) {
            toolbar.setTitle(fragment_Archive);
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorArchive));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.colorArchive));
            }
            fragName = fragment_Archive;
            fragment = new ArchiveFragment();
            refreshUserDataRecycler();
        } else if (id == R.id.userTrash) {
            toolbar.setTitle(fragment_Trash);
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorTrash));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(getResources().getColor(R.color.colorTrash));
            }
            fragName = fragment_Trash;
            fragment = new TrashFragment();
            refreshUserDataRecycler();
        } else if (id == R.id.userLogout) {
            progress.setMessage(user_called_loging_out);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.layoutManager) {

            if (fragName.equals(fragment_Note)) {
                NoteFragment.onItemSelected(item);
            } else if (fragName.equals(fragment_Reminder)) {
                ReminderFragment.onItemSelected(item);
            } else if (fragName.equals(fragment_Archive)) {
                ArchiveFragment.onItemSelected(item);
            } else if (fragName.equals(fragment_Trash)) {
                TrashFragment.onItemSelected(item);
            }

        } else if (item.getItemId() == R.id.deleteNote) {

            dataModels = new ArrayList<>();

            final CollectionReference coll = FirebaseFirestore.getInstance().collection(user_data_FirebaseFirestore).document(userID).collection(user_note_FirebaseFirestore);

            if (NetworkConnection.isNetworkConnected(getApplicationContext())){

                if (NetworkConnection.isInternetAvailable()){

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
                                TrashFragment.presenter.showSnacBar(index.size() + numberOf_Item_deleted);
                                TrashDataAdapter.userViewHolder.refreshListData(index);
                                refreshUserDataRecycler();
                                index.clear();
                                onBackPressed();
                            } else {
                                Toast.makeText(MainPanelActivity.this, user_need_to_select_more_than_1, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }else{

                SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(getApplicationContext());
                dataModels = sqLiteDatabaseHandler.getAllRecordDel();

                if (index.size() != 1) {
                    int x = 0;

                    for (int i = 0; i < index.size(); i++) {
                        for (int j = 0; j < index.size(); j++) {
                            x = 0;
                            if (index.get(i) == Integer.parseInt(TrashDataAdapter.list.get(j).getKey())) {
                                dataModels.add(TrashDataAdapter.list.get(j));
                            }
                        }
                    }

                    for (int i = 0; i < dataModels.size(); i++){

                        sqLiteDatabaseHandler.insertRecordDel(dataModels.get(i));
                        sqLiteDatabaseHandler.deleteRecord(dataModels.get(i));

                    }

                    TrashFragment.presenter.showSnacBar(index.size() + numberOf_Item_deleted);
                    TrashDataAdapter.userViewHolder.refreshListData(index);
                    refreshUserDataRecycler();
                    index.clear();
                    onBackPressed();
                } else {
                    Toast.makeText(MainPanelActivity.this, user_need_to_select_more_than_1, Toast.LENGTH_SHORT).show();
                }

            }
        }

        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.closeDrawer(Gravity.START);
        else if (fragName.equals(fragment_Trash)) {
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