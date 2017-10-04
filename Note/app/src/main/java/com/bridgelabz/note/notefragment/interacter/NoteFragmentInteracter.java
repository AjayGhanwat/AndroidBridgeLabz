package com.bridgelabz.note.notefragment.interacter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.bridgelabz.note.adapter.NoteDataAdapter;
import com.bridgelabz.note.model.DataModel;
import com.bridgelabz.note.notefragment.View.NoteFragment;
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
    ArrayList<DataModel> pinData;
    NoteDataAdapter dataAdapter;
    NoteDataAdapter pinDataAdapter;

    RecyclerView pinrecycler,unpinrecycler;

    int pinSize = 0, unPinSize = 0;

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

        unpinrecycler = recyclerView;

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

                    boolean isPin = match.getPin();

                    if(match.getKey() == null && match.getDate() == null) {
                        Date cDate = new Date();

                        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
                        Key = "0";
                        userDate = fDate;
                    }else{
                        Key = match.getKey();
                        userDate = match.getDate();
                    }

                    boolean isArchive = match.getArchive();

                    if(!isArchive && !match.getTrash() && !isPin) {

                        unPinSize++;
                        data.add(match);
                        dataAdapter = new NoteDataAdapter(data);
                        unpinrecycler.setAdapter(dataAdapter);
                        unpinrecycler.invalidate();
                        dataAdapter.notifyDataSetChanged();
                        pinrecycler.invalidate();
                        if (pinDataAdapter != null) {
                            pinDataAdapter = new NoteDataAdapter(pinData);
                            pinrecycler.setAdapter(pinDataAdapter);
                            pinDataAdapter.notifyDataSetChanged();
                        }
                    }
                    NoteFragment.isChecked(pinSize, unPinSize);
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
                        userDate = match.getDate();
                    }else{
                        Date cDate = new Date();
                        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
                        Key = "0";
                        userDate = fDate;
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
        data.clear();
        unPinSize = 0;
    }

    @Override
    public void showPinnedRecyclerData(final RecyclerView recyclerView) {

        pinrecycler = recyclerView;

        pinData = new ArrayList<>();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Data").child(userID);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                GenericTypeIndicator<ArrayList<DataModel>> t = new GenericTypeIndicator<ArrayList<DataModel>>() {
                };

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    DataModel match = postSnapshot.getValue(DataModel.class);

                    boolean isPin = match.getPin();

                    if(match.getKey() == null && match.getDate() == null) {
                        Date cDate = new Date();

                        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
                        Key = "0";
                        userDate = fDate;
                    }else{
                        Key = match.getKey();
                        userDate = match.getDate();
                    }

                    boolean isArchive = match.getArchive();

                    if(!isArchive && !match.getTrash() && isPin) {

                        pinSize++;
                        pinData.add(match);
                        pinDataAdapter = new NoteDataAdapter(pinData);
                        pinrecycler.setAdapter(pinDataAdapter);
                        pinrecycler.invalidate();
                        pinDataAdapter.notifyDataSetChanged();
                        unpinrecycler.invalidate();
                        if (dataAdapter != null) {
                            dataAdapter = new NoteDataAdapter(data);
                            unpinrecycler.setAdapter(dataAdapter);
                            dataAdapter.notifyDataSetChanged();
                        }
                    }
                    NoteFragment.isChecked(pinSize, unPinSize);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    DataModel match = postSnapshot.getValue(DataModel.class);

                    if(pinDataAdapter.getItemCount() != -1) {
                        Key = match.getKey();
                        userDate = match.getDate();
                    }else{
                        Date cDate = new Date();
                        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
                        Key = "0";
                        userDate = fDate;
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
        pinData.clear();
        pinSize = 0;
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
    public void swappablePinData(RecyclerView recyclerView) {
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

                                    id = pinData.get(position).getKey();
                                    date = pinData.get(position).getDate();
                                    isPin = pinData.get(position).getPin();
                                    changeDataArchive(id,date,isPin);
                                    dire = "Left";
                                    pinDataAdapter = new NoteDataAdapter(pinData);
                                    recyclerView.setAdapter(pinDataAdapter);
                                    pinDataAdapter.notifyDataSetChanged();
                                    pinData.remove(position);
                                    pinDataAdapter.notifyItemRemoved(position);
                                    if(isPin){
                                        noteInterface.showSnacBar("Note Archive & Unpinned!");
                                        dire = "LeftUn";
                                    }
                                    else {
                                        noteInterface.showSnacBar("Note Archive!");
                                    }
                                }
                                pinDataAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    id = pinData.get(position).getKey();
                                    date = pinData.get(position).getDate();
                                    isPin = pinData.get(position).getPin();
                                    changeDataTrash(id,date,isPin);
                                    dire = "Right";
                                    pinDataAdapter = new NoteDataAdapter(pinData);
                                    recyclerView.setAdapter(pinDataAdapter);
                                    pinDataAdapter.notifyDataSetChanged();
                                    pinData.remove(position);
                                    pinDataAdapter.notifyItemRemoved(position);
                                    if(isPin){
                                        noteInterface.showSnacBar("Note Trashed & Unpinned!");
                                        dire = "RightUn";
                                    }
                                    else {
                                        noteInterface.showSnacBar("Note Trashed!");
                                    }
                                }
                                pinDataAdapter.notifyDataSetChanged();
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

    @Override
    public void resetPinRecycler(RecyclerView recyclerView) {
        recyclerView.setAdapter(pinDataAdapter);
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
