package com.bridgelabz.todo.trash.interacter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.bridgelabz.todo.adapter.TrashDataAdapter;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.sqlitedatabase.SQLiteDatabaseHandler;
import com.bridgelabz.todo.trash.presenter.TrashFragmentPresenter;
import com.bridgelabz.todo.trash.presenter.TrashFragmentPresenterInterface;
import com.bridgelabz.todo.util.NetworkConnection;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.bridgelabz.todo.constant.Constant.user_data_FirebaseFirestore;
import static com.bridgelabz.todo.constant.Constant.user_deleted_notes_forever;
import static com.bridgelabz.todo.constant.Constant.user_deleted_notes_restored;
import static com.bridgelabz.todo.constant.Constant.user_firestore_data_keys;
import static com.bridgelabz.todo.constant.Constant.user_note_FirebaseFirestore;

public class TrashFragmentInteracter implements TrashFragmentInteracterInterface {

    public static TrashDataAdapter dataAdapter;
    static ArrayList<DataModel> mData;
    Context context;
    TrashFragmentPresenterInterface presenterInterface;
    FirebaseAuth mAuth;
    String id, date;
    String userId;
    CollectionReference reference;
    CollectionReference mRefDelete;
    String deletedID, deletedDate;
    boolean isDeleted = false;

    public TrashFragmentInteracter(Context context, TrashFragmentPresenter presenterInterface) {

        this.context = context;
        this.presenterInterface = presenterInterface;
    }

    @Override
    public void showRecyclerData(final RecyclerView recyclerView) {

        mData = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getCurrentUser().getUid();

        reference = FirebaseFirestore.getInstance().collection(user_data_FirebaseFirestore).document(userId).collection(user_note_FirebaseFirestore);

        reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(context);
                ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();

                for (int i =0; i < dataModels.size();i++) {

                    boolean isTrash = dataModels.get(i).getTrash();

                    if (isTrash) {

                        mData.add(dataModels.get(i));
                        dataAdapter = new TrashDataAdapter(mData);
                        recyclerView.setAdapter(dataAdapter);
                        dataAdapter.notifyDataSetChanged();
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

                                    id = mData.get(position).getId();
                                    date = mData.get(position).getDate();
                                    changeDataDeleted(id, date, recyclerView);
                                    dataAdapter = new TrashDataAdapter(mData);
                                    recyclerView.setAdapter(dataAdapter);
                                    dataAdapter.notifyDataSetChanged();
                                    mData.remove(position);
                                    dataAdapter.notifyItemRemoved(position);
                                    presenterInterface.showSnacBar(user_deleted_notes_forever);
                                }
                                dataAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    id = mData.get(position).getId();
                                    date = mData.get(position).getDate();
                                    changeDataArchive(id, date);
                                    dataAdapter = new TrashDataAdapter(mData);
                                    recyclerView.setAdapter(dataAdapter);
                                    dataAdapter.notifyDataSetChanged();
                                    mData.remove(position);
                                    dataAdapter.notifyItemRemoved(position);
                                    presenterInterface.showSnacBar(user_deleted_notes_restored);
                                }
                                dataAdapter.notifyDataSetChanged();
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);
    }

    @Override
    public void showSearch(final RecyclerView recyclerView, String newText) {

        if (newText != null && !newText.isEmpty()) {

            ArrayList<DataModel> userNote = new ArrayList<DataModel>();
            for (DataModel item : mData) {
                if (item.getTitle().contains(newText)) {
                    userNote.add(item);
                }
                TrashDataAdapter dataAdapter1 = new TrashDataAdapter(userNote);
                recyclerView.refreshDrawableState();
                recyclerView.setAdapter(dataAdapter1);
                dataAdapter1.notifyDataSetChanged();
            }
        } else {
            recyclerView.setAdapter(dataAdapter);
        }

    }

    @Override
    public void refreshrecycler(RecyclerView recyclerView) {
        recyclerView.setAdapter(dataAdapter);
    }

    private void changeDataDeleted(final String id, final String date, RecyclerView recyclerView) {

        int LocationNOte = -1;

        final SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(context);
        ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();

        for (int i = 0; i < dataModels.size(); i++ ){
            if (id.equals(dataModels.get(i).getId())){
                LocationNOte = i;
            }
        }

        final DataModel dataModel = dataModels.get(LocationNOte);

        if (NetworkConnection.isNetworkConnected(context)){

            if (NetworkConnection.isInternetAvailable()){

                mRefDelete = FirebaseFirestore.getInstance().collection(user_data_FirebaseFirestore).document(userId).collection(user_note_FirebaseFirestore);

                do {

                    if (deletedID == null) {
                        mRefDelete.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (task.isSuccessful()) {

                                    deletedID = task.getResult().getString("key");
                                    deletedDate = task.getResult().getString("date");
                                    isDeleted = isDeletedNote(id, deletedDate);
                                }

                            }
                        });
                    }
                } while (isDeleted == true);
                isDeleted = false;

            }

        }else{

            do {

                deletedID = dataModel.getKey();
                deletedDate = dataModel.getDate();
                isDeletedNote(id,deletedDate);

            } while (isDeleted == true);
            isDeleted = false;

        }
    }

    private boolean isDeletedNote(String id, final String date) {

        if (NetworkConnection.isNetworkConnected(context)) {

            if (NetworkConnection.isInternetAvailable()) {

                mRefDelete.document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mRefDelete.orderBy(user_firestore_data_keys).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot documentSnapshots) {

                                for (DocumentSnapshot doc : documentSnapshots.getDocuments()) {

                                    DataModel getUserData = doc.toObject(DataModel.class);

                                    if (date.equals(getUserData.getDate())) {

                                        String updateKey = getUserData.getKey();
                                        String updateNote = getUserData.getId();

                                        int preID = Integer.parseInt(deletedID);
                                        int nextID = Integer.parseInt(updateKey);

                                        if (preID < nextID) {

                                            nextID--;

                                            mRefDelete.document(updateNote).update(user_firestore_data_keys, String.valueOf(nextID));

                                        }

                                    }
                                }
                            }
                        });
                    }
                });

            }
        } else {


            String updateNote = null;

            int LocationNOte = -1;

            final SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(context);
            ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecordAsc();

            for (int i = 0; i < dataModels.size(); i++ ){
                if (id.equals(dataModels.get(i).getId())){
                    LocationNOte = i;
                }
            }

            if (LocationNOte != -1) {
                DataModel dataModel = dataModels.get(LocationNOte);
                sqLiteDatabaseHandler.insertRecordDel(dataModel);
                sqLiteDatabaseHandler.deleteRecord(dataModel);
                updateNote = dataModels.get(LocationNOte).getKey();
            }

            int prevID = Integer.parseInt(updateNote);

            for (int i = LocationNOte; i < dataModels.size()-1; i++) {

                if (date.equals(dataModels.get(i).getDate())) {

                    DataModel dataModel1 = dataModels.get(i+1);
                    dataModel1.setKey(String.valueOf(prevID));
                    sqLiteDatabaseHandler.updateRecord(dataModel1);

                    prevID++;

                }

            }
        }
        return true;
    }

    void changeDataArchive(final String id, String date) {

        reference = FirebaseFirestore.getInstance().collection(user_data_FirebaseFirestore).document(userId).collection(user_note_FirebaseFirestore);

        int LocationNOte = -1;

        SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(context);
        ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();

        for (int i = 0; i < dataModels.size(); i++ ){
            if (id.equals(dataModels.get(i).getId())){
                LocationNOte = i;
            }
        }

        DataModel dataModel = dataModels.get(LocationNOte);

        if (NetworkConnection.isNetworkConnected(context)) {

            if (NetworkConnection.isInternetAvailable()) {

                Map<String, Object> change = new HashMap<>();
                change.put("trash", false);
                reference.document(id).update(change);
                dataModel.setTrash(false);
                sqLiteDatabaseHandler.updateRecord(dataModel);
            }
        }else{

            dataModel.setTrash(false);
            sqLiteDatabaseHandler.updateRecord(dataModel);

        }

    }
}
