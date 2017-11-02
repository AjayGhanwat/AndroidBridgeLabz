package com.bridgelabz.todo.reminder.view;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bridgelabz.todo.R;
import com.bridgelabz.todo.adapter.NoteDataAdapter;
import com.bridgelabz.todo.addnotes.view.AddActivity;
import com.bridgelabz.todo.base.BaseFragment;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.model.UserData;
import com.bridgelabz.todo.reminder.presenter.ReminderFragmentPresenter;
import com.bridgelabz.todo.reminder.presenter.ReminderFragmentPresenterInterface;
import com.bridgelabz.todo.sqlitedatabase.SQLiteDatabaseHandler;
import com.bridgelabz.todo.util.NetworkChangeReceiver;
import com.bridgelabz.todo.util.NetworkConnection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.bridgelabz.todo.R.drawable.ic_view_list_black_24dp;
import static com.bridgelabz.todo.R.drawable.ic_view_quilt_black_24dp;
import static com.bridgelabz.todo.constant.Constant.user_date_format;
import static com.bridgelabz.todo.constant.Constant.user_firestore_data_keys;
import static com.bridgelabz.todo.constant.Constant.user_layouts;
import static com.bridgelabz.todo.constant.Constant.user_layouts_linear;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ReminderFragment extends BaseFragment implements ReminderFragmentInterface {

    public static FloatingActionButton fab;
    static RecyclerView recyclerView;
    static BroadcastReceiver receiver  = new NetworkChangeReceiver();
    static LinearLayoutManager linearLayoutManager;
    static StaggeredGridLayoutManager gridLayoutManager;
    static RecyclerView.LayoutManager layoutManager;
    static ReminderFragmentPresenterInterface presenter;
    static CollectionReference collectionReference;
    static String userId;
    static ArrayList<DataModel> DataRefresh;
    static NoteDataAdapter refreshDataAda;
    View v;
    ProgressDialog progress;
    RelativeLayout relativeLayout;
    String layout;

    public static void onItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.layoutManager) {

            if (!linearLayoutManager.equals(layoutManager)) {
                layoutManager = linearLayoutManager;
                item.setIcon(ic_view_quilt_black_24dp);
                Map<String, Object> changeLayout = new HashMap<>();
                changeLayout.put(user_layouts, user_layouts_linear);
                collectionReference.document(userId).set(changeLayout);
                recyclerView.setLayoutManager(layoutManager);
            } else if (linearLayoutManager.equals(layoutManager)) {
                layoutManager = gridLayoutManager;
                item.setIcon(ic_view_list_black_24dp);
                Map<String, Object> changeLayout = new HashMap<>();
                changeLayout.put(user_layouts, "grid");
                collectionReference.document(userId).set(changeLayout);
                recyclerView.setLayoutManager(layoutManager);
            }
        }
    }

    public static void searchItem(String newText) {

        presenter.searchItemData(recyclerView, newText);

    }

    public static void resetRecyclerView() {
        presenter.resetNoteRecycler(recyclerView);
    }

    /*
     *  As soon as the fragment opened the event listener is executed
     *  and the as per the changes in the database the it will change int running app data
     */

    public static void updateNewData(CollectionReference myDoc) {

        DataRefresh = new ArrayList<>();

        getApplicationContext().registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if (NetworkConnection.isNetworkConnected(getApplicationContext())) {

            if (NetworkConnection.isInternetAvailable()) {

                myDoc.orderBy(user_firestore_data_keys, Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        DataRefresh.clear();

                        SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(getApplicationContext());
                        /*sqLiteDatabaseHandler.deleteAll();*/

                        Date cDate = new Date();
                        String fDate = new SimpleDateFormat(user_date_format).format(cDate);

                       /* for (DocumentSnapshot documents : documentSnapshots.getDocuments()) {

                            DataModel userData = documents.toObject(DataModel.class);

                            sqLiteDatabaseHandler.insertRecord(userData);

                        }*/

                        ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();

                        for (int i = 0; i < dataModels.size(); i++) {

                            if (dataModels.get(i).getReminderDate().equals(fDate)) {

                                if (!dataModels.get(i).getPin() && !dataModels.get(i).getArchive() && !dataModels.get(i).getTrash() && dataModels.get(i).getReminder()) {

                                    DataRefresh.add(dataModels.get(i));
                                    refreshDataAda = new NoteDataAdapter(DataRefresh);
                                    recyclerView.setAdapter(refreshDataAda);
                                    refreshDataAda.notifyDataSetChanged();

                                }
                            }
                        }
                    }
                });

            }

        } else {

            myDoc.orderBy(user_firestore_data_keys, Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    DataRefresh.clear();

                    SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(getApplicationContext());

                    Date cDate = new Date();
                    String fDate = new SimpleDateFormat(user_date_format).format(cDate);

                    ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();

                    for (int i = 0; i < dataModels.size(); i++) {

                        if (dataModels.get(i).getReminderDate().equals(fDate)) {

                            if (!dataModels.get(i).getPin() && !dataModels.get(i).getArchive() && !dataModels.get(i).getTrash() && dataModels.get(i).getReminder()) {

                                DataRefresh.add(dataModels.get(i));
                                refreshDataAda = new NoteDataAdapter(DataRefresh);
                                recyclerView.setAdapter(refreshDataAda);
                                refreshDataAda.notifyDataSetChanged();

                            }
                        }
                    }
                }
            });

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_reminder, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.v = view;

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        collectionReference = FirebaseFirestore.getInstance().collection("User").document(userId).collection("UserInfo");

        relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayoutReminder);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        gridLayoutManager = new StaggeredGridLayoutManager(2, 1);

        initView();
        clickListning();

        fab = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddActivity.class);
                startActivity(intent);
            }
        });
    }

    private void isLayout() {
        if (layout.equals(user_layouts_linear)) {
            layoutManager = linearLayoutManager;
            recyclerView.setLayoutManager(layoutManager);
        } else {
            layoutManager = gridLayoutManager;
            recyclerView.setLayoutManager(layoutManager);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isLayout();
    }

    /*
     *  It is used to retrieve the last layout it is gride or linear as
     *  per that the recyclerview layout is changed
     */

    @Override
    public void onStart() {
        super.onStart();
        presenter.showRecycler(recyclerView);

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentSnapshot document : documentSnapshots.getDocuments()) {

                    UserData user = document.toObject(UserData.class);

                    layout = user.getLayout();

                    isLayout();
                }
            }
        });
    }

    @Override
    public void initView() {
        presenter = new ReminderFragmentPresenter(getActivity(), this);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerReminder);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void clickListning() {

    }

    @Override
    public void viewRemminderRecyclerSuccess(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void viewReminderRecyclerUnsuccess(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void viewReminderRecyclerProgressShow(String msg) {
        progress = new ProgressDialog(getActivity());
        progress.setMessage(msg);
        progress.show();
    }

    @Override
    public void viewReminderRecyclerProgressDismis() {
        progress.dismiss();
    }
}
