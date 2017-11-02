package com.bridgelabz.todo.archive.interacter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.bridgelabz.todo.adapter.ArchiveDataAdapter;
import com.bridgelabz.todo.archive.presenter.ArchiveFragmentPresenter;
import com.bridgelabz.todo.archive.presenter.ArchiveFragmentPresenterInterface;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.sqlitedatabase.SQLiteDatabaseHandler;
import com.bridgelabz.todo.util.NetworkConnection;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.id;
import static com.bridgelabz.todo.constant.Constant.user_data_FirebaseFirestore;
import static com.bridgelabz.todo.constant.Constant.user_deleted_notes_restored;
import static com.bridgelabz.todo.constant.Constant.user_firestore_data_keys;
import static com.bridgelabz.todo.constant.Constant.user_note_FirebaseFirestore;
import static com.bridgelabz.todo.constant.Constant.user_note_firebase_database_arch;

public class ArchiveFragmentInteracter implements ArchiveFragmentInteracterInterface {

    static String userId;
    Context context;
    ArchiveFragmentPresenterInterface presenter;
    ArrayList<DataModel> mData;
    ArchiveDataAdapter dataAdapter;
    FirebaseAuth mAuth;
    String mId, mDate;
    CollectionReference mReference;

    public ArchiveFragmentInteracter(Context context, ArchiveFragmentPresenter presenter) {

        this.context = context;
        this.presenter = presenter;

    }

    @Override
    public void showRecyclerData(final RecyclerView recyclerView) {

        mData = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getCurrentUser().getUid();

        mReference = FirebaseFirestore.getInstance().collection(user_data_FirebaseFirestore).document(userId).collection(user_note_FirebaseFirestore);

        mReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(context);
                ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();

                for (int i = 0; i < dataModels.size(); i++) {

                    boolean isArchive = dataModels.get(i).getArchive();

                    if (isArchive) {
                        if (!dataModels.get(i).getTrash()) {
                            mData.add(dataModels.get(i));
                            dataAdapter = new ArchiveDataAdapter(mData);
                            recyclerView.setAdapter(dataAdapter);
                            dataAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void swappableData(RecyclerView recyclerView) {
        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(recyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    mId = mData.get(position).getId();
                                    mDate = mData.get(position).getDate();
                                    changeDataArchive(mId, mDate);
                                    dataAdapter = new ArchiveDataAdapter(mData);
                                    recyclerView.setAdapter(dataAdapter);
                                    dataAdapter.notifyDataSetChanged();
                                    mData.remove(position);
                                    dataAdapter.notifyItemRemoved(position);
                                    presenter.showSnacBar(user_deleted_notes_restored);
                                }
                                dataAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    mId = mData.get(position).getId();
                                    mDate = mData.get(position).getDate();
                                    changeDataArchive(mId, mDate);
                                    dataAdapter = new ArchiveDataAdapter(mData);
                                    recyclerView.setAdapter(dataAdapter);
                                    dataAdapter.notifyDataSetChanged();
                                    mData.remove(position);
                                    dataAdapter.notifyItemRemoved(position);
                                    presenter.showSnacBar(user_deleted_notes_restored);
                                }
                                dataAdapter.notifyDataSetChanged();
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);
    }

    @Override
    public void undoChangeData() {

        int LocationNOte = -1;

        SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(context);
        ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();
        for (int i = 0; i < dataModels.size(); i++) {
            if (mId.equals(dataModels.get(i).getId())) {
                LocationNOte = i;
            }
        }

        DataModel dataModel = dataModels.get(LocationNOte);

        mReference = FirebaseFirestore.getInstance().collection(user_data_FirebaseFirestore).document(userId).collection(user_note_FirebaseFirestore);

        if (NetworkConnection.isNetworkConnected(context)) {

            if (NetworkConnection.isInternetAvailable()) {

                Map<String, Object> change = new HashMap<>();
                change.put(user_note_firebase_database_arch, true);
                mReference.document(mId).update(change);
                dataModel.setArchive(true);
                sqLiteDatabaseHandler.updateRecord(dataModel);

            }

        } else {

            dataModel.setArchive(true);
            sqLiteDatabaseHandler.updateRecord(dataModel);
        }

    }

    @Override
    public void showSearch(final RecyclerView recyclerView, String newText) {

        if (newText != null && !newText.isEmpty()) {

            ArrayList<DataModel> userNote = new ArrayList<DataModel>();
            for (DataModel item : mData) {
                if (item.getTitle().contains(newText)) {
                    userNote.add(item);
                }
                ArchiveDataAdapter dataAdapter1 = new ArchiveDataAdapter(userNote);
                recyclerView.refreshDrawableState();
                recyclerView.setAdapter(dataAdapter1);
                dataAdapter1.notifyDataSetChanged();
            }
        } else {
            recyclerView.setAdapter(dataAdapter);
        }
    }

    @Override
    public void refreshRecyclerData(RecyclerView recyclerView) {
        recyclerView.setAdapter(dataAdapter);
    }

    void changeDataArchive(final String mId, String mDate) {

        int LocationNOte = -1;

        SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(context);
        ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();
        for (int i = 0; i < dataModels.size(); i++) {
            if (mId.equals(dataModels.get(i).getId())) {
                LocationNOte = i;
            }
        }

        DataModel dataModel = dataModels.get(LocationNOte);

        mReference = FirebaseFirestore.getInstance().collection(user_data_FirebaseFirestore).document(userId).collection(user_note_FirebaseFirestore);

        if (NetworkConnection.isNetworkConnected(context)) {

            if (NetworkConnection.isInternetAvailable()) {

                Map<String, Object> change = new HashMap<>();
                change.put(user_note_firebase_database_arch, false);
                mReference.document(mId).update(change);
                dataModel.setArchive(false);
                sqLiteDatabaseHandler.updateRecord(dataModel);
            }

        } else {

            dataModel.setArchive(false);
            sqLiteDatabaseHandler.updateRecord(dataModel);
        }

    }
}
