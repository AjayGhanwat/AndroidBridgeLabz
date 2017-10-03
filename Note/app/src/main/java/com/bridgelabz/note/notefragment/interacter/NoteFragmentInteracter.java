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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    public static String Key;
    public static String userDate;

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

                    boolean is = match.getPin();
                    Log.i("Pinned", "onChildAdded: " + is);

                    if(match.getKey() == null && match.getDate() == null) {
                        Date cDate = new Date();

                        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
                        Key = "0";
                        Log.i("DataIs", "onChildAdded: " + Key);
                        userDate = fDate;
                        Log.i("DataIs", "onChildAdded: " + userDate);
                    }else{
                        Key = match.getKey();
                        Log.i("DataIs", "onChildAdded: " + Key);
                        userDate = match.getDate();
                        Log.i("DataIs", "onChildAdded: " + userDate);
                    }

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
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    DataModel match = postSnapshot.getValue(DataModel.class);

                    if(dataAdapter.getItemCount() != -1) {
                        Key = match.getKey();
                        Log.i("DataIs", "onChildAdded: " + Key);
                        userDate = match.getDate();
                        Log.i("DataIs", "onChildAdded: " + userDate);
                    }else{
                        Date cDate = new Date();
                        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
                        Key = "0";
                        Log.i("DataIs", "onChildAdded: " + Key);
                        userDate = fDate;
                        Log.i("DataIs", "onChildAdded: " + userDate);
                    }
                }
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
    boolean isPin;

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
                                    isPin = data.get(position).getPin();
                                    changeDataArchive(id,date,isPin);
                                    dire = "Left";
                                    dataAdapter = new NoteDataAdapter(data);
                                    recyclerView.setAdapter(dataAdapter);
                                    dataAdapter.notifyDataSetChanged();
                                    data.remove(position);
                                    dataAdapter.notifyItemRemoved(position);
                                    if(isPin){
                                        noteInterface.showSnacBar("Note Archive & Unpinned!");
                                        dire = "LeftUn";
                                    }
                                    else {
                                        noteInterface.showSnacBar("Note Archive!");
                                    }
                                }
                                dataAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    id = data.get(position).getKey();
                                    date = data.get(position).getDate();
                                    isPin = data.get(position).getPin();
                                    changeDataTrash(id,date,isPin);
                                    dire = "Right";
                                    dataAdapter = new NoteDataAdapter(data);
                                    recyclerView.setAdapter(dataAdapter);
                                    dataAdapter.notifyDataSetChanged();
                                    data.remove(position);
                                    dataAdapter.notifyItemRemoved(position);
                                    if(isPin){
                                        noteInterface.showSnacBar("Note Trashed & Unpinned!");
                                        dire = "RightUn";
                                    }
                                    else {
                                        noteInterface.showSnacBar("Note Trashed!");
                                    }
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
        }else if(dire.equals("LeftUn")){
            reference.child("archive").setValue(false);
            reference.child("pin").setValue(true);
            dataAdapter.notifyDataSetChanged();
        }else if(dire.equals("RightUn")){
            reference.child("trash").setValue(false);
            reference.child("pin").setValue(true);
            dataAdapter.notifyDataSetChanged();
        }else {
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

    void changeDataArchive(final String id,String date, boolean isPin){

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Data").child(userID).child(date).child(id);

        if(!isPin) {
            reference.child("archive").setValue(true);
        }else{
            reference.child("archive").setValue(true);
            reference.child("pin").setValue(false);
        }


    }

    void changeDataTrash(final String id,String date, boolean isPin){

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Data").child(userID).child(date).child(id);

        if (!isPin) {
            reference.child("trash").setValue(true);
        }else{
            reference.child("trash").setValue(true);
            reference.child("pin").setValue(false);
        }

    }
}
