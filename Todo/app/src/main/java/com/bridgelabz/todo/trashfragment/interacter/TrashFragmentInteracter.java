package com.bridgelabz.todo.trashfragment.interacter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bridgelabz.todo.adapter.TrashDataAdapter;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.trashfragment.presenter.TrashFragmentPresenter;
import com.bridgelabz.todo.trashfragment.presenter.TrashFragmentPresenterInterface;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrashFragmentInteracter implements TrashFragmentInteracterInterface {

    public static TrashDataAdapter dataAdapter;
    Context context;
    TrashFragmentPresenterInterface presenterInterface;
    ArrayList<DataModel> data;
    FirebaseAuth mAuth;
    String id, date;
    String userId;
    CollectionReference reference;
    DataModel match;

    public TrashFragmentInteracter(Context context, TrashFragmentPresenter presenterInterface) {

        this.context = context;
        this.presenterInterface = presenterInterface;
    }

    @Override
    public void showRecyclerData(final RecyclerView recyclerView) {
        data = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getCurrentUser().getUid();

        reference = FirebaseFirestore.getInstance().collection("Data").document(userId).collection("Notes");

        reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                for (DocumentSnapshot postSnapshot : documentSnapshots.getDocuments()) {

                    match = postSnapshot.toObject(DataModel.class);

                    boolean isTrash = match.getTrash();

                    if (isTrash) {

                        data.add(match);
                        Log.i("sd", "onChildAdded: " + match);
                        dataAdapter = new TrashDataAdapter(data);
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

                                    id = data.get(position).getId();
                                    date = data.get(position).getDate();
                                    changeDataDeleted(id, date, recyclerView);
                                    dataAdapter = new TrashDataAdapter(data);
                                    recyclerView.setAdapter(dataAdapter);
                                    dataAdapter.notifyDataSetChanged();
                                    data.remove(position);
                                    dataAdapter.notifyItemRemoved(position);
                                    presenterInterface.showSnacBar("Note Deleted Forever!");
                                }
                                dataAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    id = data.get(position).getId();
                                    date = data.get(position).getDate();
                                    changeDataArchive(id, date);
                                    dataAdapter = new TrashDataAdapter(data);
                                    recyclerView.setAdapter(dataAdapter);
                                    dataAdapter.notifyDataSetChanged();
                                    data.remove(position);
                                    dataAdapter.notifyItemRemoved(position);
                                    presenterInterface.showSnacBar("Note Restored!");
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
            for (DataModel item : data) {
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

    }

    void changeDataArchive(final String id, String date) {

        reference = FirebaseFirestore.getInstance().collection("Data").document(userId).collection("Notes");

        Map<String, Object> change = new HashMap<>();
        change.put("trash", false);
        reference.document(id).update(change);

    }
}
