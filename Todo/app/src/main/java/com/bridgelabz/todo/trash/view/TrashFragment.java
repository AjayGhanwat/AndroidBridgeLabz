package com.bridgelabz.todo.trash.view;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bridgelabz.todo.R;
import com.bridgelabz.todo.adapter.NoteDataAdapter;
import com.bridgelabz.todo.base.BaseFragment;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.model.UserData;
import com.bridgelabz.todo.sqlitedatabase.SQLiteDatabaseHandler;
import com.bridgelabz.todo.trash.presenter.TrashFragmentPresenter;
import com.bridgelabz.todo.trash.presenter.TrashFragmentPresenterInterface;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.bridgelabz.todo.R.drawable.ic_view_list_black_24dp;
import static com.bridgelabz.todo.R.drawable.ic_view_quilt_black_24dp;
import static com.bridgelabz.todo.constant.Constant.user_firestore_data_keys;
import static com.bridgelabz.todo.constant.Constant.user_layouts;
import static com.bridgelabz.todo.constant.Constant.user_layouts_grid;
import static com.bridgelabz.todo.constant.Constant.user_layouts_linear;
import static com.bridgelabz.todo.constant.Constant.user_users_FirebaseFirestore;
import static com.bridgelabz.todo.constant.Constant.user_users_info_FirebaseFirestore;
import static com.facebook.FacebookSdk.getApplicationContext;

public class TrashFragment extends BaseFragment implements TrashFragmentInterface {

    public static RecyclerView recyclerView;
    static BroadcastReceiver receiver  = new NetworkChangeReceiver();
    public static TrashFragmentPresenterInterface presenter;
    public static NoteDataAdapter refreshDataAda;
    static LinearLayoutManager linearLayoutManager;
    static StaggeredGridLayoutManager gridLayoutManager;
    static RecyclerView.LayoutManager layoutManager;
    static CollectionReference collectionReference;
    static String userId;
    static ArrayList<DataModel> DataRefresh;
    View v;
    RelativeLayout relativeLayout;
    ProgressDialog progress;
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
                changeLayout.put(user_layouts, user_layouts_grid);
                collectionReference.document(userId).set(changeLayout);
                recyclerView.setLayoutManager(layoutManager);
            }
        }
    }

    public static void searchItem(String newText) {
        presenter.showSearchData(recyclerView, newText);
    }

    public static void resetRecyclerView() {
        presenter.refreshRecycler(recyclerView);
    }

    public static void updateNewData(CollectionReference myDoc) {

        DataRefresh = new ArrayList<>();

        getApplicationContext().registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if (NetworkConnection.isNetworkConnected(getApplicationContext())){

            if (NetworkConnection.isInternetAvailable()){

                final SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(getApplicationContext());
/*
                sqLiteDatabaseHandler.deleteAll();
*/

                myDoc.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        DataRefresh.clear();

                        /*for (DocumentSnapshot documents : documentSnapshots.getDocuments()) {

                            DataModel userData = documents.toObject(DataModel.class);

                            sqLiteDatabaseHandler.insertRecord(userData);

                        }*/

                        ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();

                        for (int i = 0; i < dataModels.size(); i++){
                            if (dataModels.get(i).getTrash()) {

                                DataRefresh.add(dataModels.get(i));
                                refreshDataAda = new NoteDataAdapter(DataRefresh);
                                recyclerView.setAdapter(refreshDataAda);
                                refreshDataAda.notifyDataSetChanged();

                            }
                        }
                    }
                });

            }

        }else{

            final SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(getApplicationContext());

            myDoc.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    DataRefresh.clear();

                    ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();

                    for (int i = 0; i < dataModels.size(); i++){
                        if (dataModels.get(i).getTrash()) {

                            DataRefresh.add(dataModels.get(i));
                            refreshDataAda = new NoteDataAdapter(DataRefresh);
                            recyclerView.setAdapter(refreshDataAda);
                            refreshDataAda.notifyDataSetChanged();

                        }
                    }
                }
            });

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_trash, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.v = view;

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        collectionReference = FirebaseFirestore.getInstance().collection(user_users_FirebaseFirestore).document(userId).collection(user_users_info_FirebaseFirestore);

        relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayoutTrash);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        gridLayoutManager = new StaggeredGridLayoutManager(2, 1);

        initView();
        clickListning();
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

    @Override
    public void onStart() {
        super.onStart();
        presenter.showRecycler(recyclerView);
        presenter.swappable(recyclerView);

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

        presenter = new TrashFragmentPresenter(getActivity(), this);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerTrash);
        recyclerView.setHasFixedSize(false);

    }

    @Override
    public void clickListning() {

    }

    @Override
    public void showRecyclerSuccess(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showRecyclerUnsuccess(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showRecyclerPregress(String msg) {
        progress = new ProgressDialog(getActivity());
        progress.setMessage(msg);
        progress.show();
    }

    @Override
    public void dismissRecyclerProgress() {
        progress.dismiss();
    }

    @Override
    public void showSnacBar(String msg) {
        Snackbar.make(relativeLayout, msg, Snackbar.LENGTH_LONG).show();
    }
}
