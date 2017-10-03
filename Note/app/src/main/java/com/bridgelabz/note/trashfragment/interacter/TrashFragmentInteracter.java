package com.bridgelabz.note.trashfragment.interacter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.bridgelabz.note.adapter.TrashDataAdapter;
import com.bridgelabz.note.model.DataModel;
import com.bridgelabz.note.trashfragment.presenter.TrashFragmentPresenter;
import com.bridgelabz.note.trashfragment.presenter.TrashFragmentPresenterInterface;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.android.gms.common.data.DataBuffer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrashFragmentInteracter implements TrashFragmentInteracterInterface{

    Context context;
    TrashFragmentPresenterInterface presenterInterface;

    public TrashFragmentInteracter(Context context, TrashFragmentPresenter presenterInterface){

        this.context = context;
        this.presenterInterface = presenterInterface;
    }

    ArrayList<DataModel> data;

    TrashDataAdapter dataAdapter;

    FirebaseAuth mAuth;
    String id,date;
    DatabaseReference reference;

    String userId;

    @Override
    public void showRecyclerData(final RecyclerView recyclerView) {
        data = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Data").child(userId);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                GenericTypeIndicator<ArrayList<DataModel>> t = new GenericTypeIndicator<ArrayList<DataModel>>() {
                };

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    DataModel match = postSnapshot.getValue(DataModel.class);

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
                                    changeDataDeleted(id, date);
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

                                    id = data.get(position).getKey();
                                    date = data.get(position).getDate();
                                    changeDataArchive(id,date);
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

                if(newText != null && !newText.isEmpty()){

                    ArrayList<DataModel> userNote = new ArrayList<DataModel>();
                    for(DataModel item: data){
                        if(item.getTitle().contains(newText)){
                            userNote.add(item);
                        }
                        TrashDataAdapter dataAdapter1 = new TrashDataAdapter(userNote);
                        recyclerView.refreshDrawableState();
                        recyclerView.setAdapter(dataAdapter1);
                        dataAdapter1.notifyDataSetChanged();
                    }
                }else {
                    recyclerView.setAdapter(dataAdapter);
                }

    }

   @Override
    public void refreshrecycler(RecyclerView recyclerView) {
        recyclerView.setAdapter(dataAdapter);
    }

    String givenID;
    String nextID;
    boolean isDeleted = false;

     private void changeDataDeleted( final String id, final String date) {

         givenID = id;

         reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                reference.child(date).child(givenID).removeValue();
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

    void changeDataArchive(final String id,String date){

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Data").child(userID).child(date).child(id);

        reference.child("trash").setValue(false);

    }
}
