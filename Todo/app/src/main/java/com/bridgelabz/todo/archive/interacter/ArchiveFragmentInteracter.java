package com.bridgelabz.todo.archive.interacter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bridgelabz.todo.adapter.ArchiveDataAdapter;
import com.bridgelabz.todo.archive.presenter.ArchiveFragmentPresenter;
import com.bridgelabz.todo.archive.presenter.ArchiveFragmentPresenterInterface;
import com.bridgelabz.todo.model.DataModel;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArchiveFragmentInteracter implements ArchiveFragmentInteracterInterface{

    Context context;
    ArchiveFragmentPresenterInterface presenter;

    ArrayList<DataModel> data;
    ArchiveDataAdapter dataAdapter;

    public ArchiveFragmentInteracter(Context context, ArchiveFragmentPresenter presenter){

        this.context = context;
        this.presenter = presenter;

    }

    FirebaseAuth mAuth;
    String id,date;

    static String userId;
    CollectionReference reference;

    @Override
    public void showRecyclerData(final RecyclerView recyclerView) {

        data = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getCurrentUser().getUid();

        reference = FirebaseFirestore.getInstance().collection("Data").document(userId).collection("Notes");

        reference.orderBy("key", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                for (DocumentSnapshot postSnapshot : documentSnapshots.getDocuments()) {

                    DataModel match = postSnapshot.toObject(DataModel.class);

                    boolean isArchive = match.getArchive();

                    if(isArchive) {
                        if(!match.getTrash()) {
                            data.add(match);
                            Log.i("sd", "onChildAdded: " + match);
                            dataAdapter = new ArchiveDataAdapter(data);
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

                                    id = data.get(position).getId();
                                    date = data.get(position).getDate();
                                    changeDataArchive(id,date);
                                    dataAdapter = new ArchiveDataAdapter(data);
                                    recyclerView.setAdapter(dataAdapter);
                                    dataAdapter.notifyDataSetChanged();
                                    data.remove(position);
                                    dataAdapter.notifyItemRemoved(position);
                                    presenter.showSnacBar("Note Restored!");
                                }
                                dataAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    id = data.get(position).getId();
                                    date = data.get(position).getDate();
                                    changeDataArchive(id,date);
                                    dataAdapter = new ArchiveDataAdapter(data);
                                    recyclerView.setAdapter(dataAdapter);
                                    dataAdapter.notifyDataSetChanged();
                                    data.remove(position);
                                    dataAdapter.notifyItemRemoved(position);
                                    presenter.showSnacBar("Note Restored!");
                                }
                                dataAdapter.notifyDataSetChanged();
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);
    }

    @Override
    public void undoChangeData() {

        reference = FirebaseFirestore.getInstance().collection("Data").document(userId).collection("Notes");

        Map<String, Object> change = new HashMap<>();
        change.put("archive", true);
        reference.document(id).update(change);
    }

    @Override
    public void showSearch(final RecyclerView recyclerView, String newText) {

        if (newText != null && !newText.isEmpty()) {

            ArrayList<DataModel> userNote = new ArrayList<DataModel>();
            for (DataModel item : data) {
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

    void changeDataArchive(final String id,String date){

        reference = FirebaseFirestore.getInstance().collection("Data").document(userId).collection("Notes");

        Map<String, Object> change = new HashMap<>();
        change.put("archive", false);
        reference.document(id).update(change);

    }
}
