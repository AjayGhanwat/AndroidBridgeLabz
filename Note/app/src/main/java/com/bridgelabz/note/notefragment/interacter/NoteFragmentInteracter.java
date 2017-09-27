package com.bridgelabz.note.notefragment.interacter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bridgelabz.note.adapter.NoteDataAdapter;
import com.bridgelabz.note.model.DataModel;
import com.bridgelabz.note.notefragment.presenter.NoteFragmentPresenter;
import com.bridgelabz.note.notefragment.presenter.NoteFragmentPresenterInterface;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;

public class NoteFragmentInteracter implements NoteFragmentInteracterInterface {

    Context context;
    NoteFragmentPresenterInterface noteInterface;

    ArrayList<DataModel> data;
    NoteDataAdapter dataAdapter;

    public NoteFragmentInteracter(Context context, NoteFragmentPresenter presenter) {

        this.context = context;
        this.noteInterface = presenter;

    }

    String id,date;
    DatabaseReference reference;

    @Override
    public void showRecyclerData(final RecyclerView recyclerView) {

        data = new ArrayList<>();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Data").child(userID);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                GenericTypeIndicator<ArrayList<DataModel>> t = new GenericTypeIndicator<ArrayList<DataModel>>() {
                };

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    DataModel match = postSnapshot.getValue(DataModel.class);

                    boolean isArchive = match.getArchive();

                    if(!isArchive && !match.getTrash()) {

                        data.add(match);
                        Log.i("sd", "onChildAdded: " + match);
                        dataAdapter = new NoteDataAdapter(data);
                        recyclerView.setAdapter(dataAdapter);
                        dataAdapter.notifyDataSetChanged();
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

    String dire;

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
                                    dire = "Left";
                                    dataAdapter = new NoteDataAdapter(data);
                                    recyclerView.setAdapter(dataAdapter);
                                    dataAdapter.notifyDataSetChanged();
                                    data.remove(position);
                                    dataAdapter.notifyItemRemoved(position);
                                    noteInterface.showSnacBar("Note Archive!");
                                }
                                dataAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    id = data.get(position).getKey();
                                    date = data.get(position).getDate();
                                    changeDataTrash(id,date);
                                    dire = "Right";
                                    dataAdapter = new NoteDataAdapter(data);
                                    recyclerView.setAdapter(dataAdapter);
                                    dataAdapter.notifyDataSetChanged();
                                    data.remove(position);
                                    dataAdapter.notifyItemRemoved(position);
                                    noteInterface.showSnacBar("Note Trashed!");
                                }
                                dataAdapter.notifyDataSetChanged();
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);
    }

    @Override
    public void undoChange() {

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Data").child(userID).child(date).child(id);

        if(dire.equals("Left")) {
            reference.child("archive").setValue(false);
            dataAdapter.notifyDataSetChanged();
        }
        else {
            reference.child("trash").setValue(false);
            dataAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showSearchData(final RecyclerView recyclerView, String newText) {

        if (newText != null && !newText.isEmpty()) {

            ArrayList<DataModel> userNote = new ArrayList<DataModel>();
            for (DataModel item : data) {
                if (item.getTitle().contains(newText)) {
                    userNote.add(item);
                }
                NoteDataAdapter dataAdapter1 = new NoteDataAdapter(userNote);
                recyclerView.refreshDrawableState();
                recyclerView.setAdapter(dataAdapter1);
                dataAdapter1.notifyDataSetChanged();
            }
        } else {
            recyclerView.setAdapter(dataAdapter);
        }
    }

    @Override
    public void resetRecycler(RecyclerView recyclerView) {
        recyclerView.setAdapter(dataAdapter);
    }

    void changeDataArchive(final String id,String date){

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Data").child(userID).child(date).child(id);

        reference.child("archive").setValue(true);

    }

    void changeDataTrash(final String id,String date){

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Data").child(userID).child(date).child(id);

        reference.child("trash").setValue(true);

    }
}
