package com.bridgelabz.note.archivefragment.interacter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bridgelabz.note.adapter.ArchiveDataAdapter;
import com.bridgelabz.note.archivefragment.presenter.ArchiveFragmentPresenter;
import com.bridgelabz.note.archivefragment.presenter.ArchiveFragmentPresenterInterface;
import com.bridgelabz.note.model.DataModel;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;

public class ArchiveFragmentInteracter implements ArchiveFragmentInteracterInterface{

    Context context;
    ArchiveFragmentPresenterInterface presenter;

    ArrayList<DataModel> data;
    ArchiveDataAdapter dataAdapter;

    DatabaseReference reference;

    public ArchiveFragmentInteracter(Context context, ArchiveFragmentPresenter presenter){

        this.context = context;
        this.presenter = presenter;

    }

    FirebaseAuth mAuth;
    String id,date;

    @Override
    public void showRecyclerData(final RecyclerView recyclerView) {

        data = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();

        String userId = mAuth.getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Data").child(userId);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                GenericTypeIndicator<ArrayList<DataModel>> t = new GenericTypeIndicator<ArrayList<DataModel>>() {
                };

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    DataModel match = postSnapshot.getValue(DataModel.class);

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

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

                                    id = data.get(position).getKey();
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

                                    id = data.get(position).getKey();
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

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Data").child(userID).child(date).child(id);

        reference.child("archive").setValue(true);
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

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Data").child(userID).child(date).child(id);

        reference.child("archive").setValue(false);

    }
}
