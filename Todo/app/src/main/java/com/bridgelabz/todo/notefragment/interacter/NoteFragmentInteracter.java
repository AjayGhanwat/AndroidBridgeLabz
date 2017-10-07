package com.bridgelabz.todo.notefragment.interacter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.bridgelabz.todo.adapter.NoteDataAdapter;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.notefragment.View.NoteFragment;
import com.bridgelabz.todo.notefragment.presenter.NoteFragmentPresenter;
import com.bridgelabz.todo.notefragment.presenter.NoteFragmentPresenterInterface;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.GenericTypeIndicator;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.GenericTypeIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    CollectionReference reference;

    public static String Key;
    public static String userDate;

    @Override
    public void showRecyclerData(final RecyclerView recyclerView) {

        unpinrecycler = recyclerView;

        data = new ArrayList<>();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseFirestore.getInstance().collection("Data").document(userID).collection("Notes");

        reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                for (DocumentSnapshot postSnapshot : documentSnapshots.getDocuments()) {

                        DataModel match = postSnapshot.toObject(DataModel.class);

                        boolean isPin = match.getPin();

                        if (match.getKey() == null && match.getDate() == null) {
                            Date cDate = new Date();

                            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
                            Key = "0";
                            userDate = fDate;
                        } else {
                            Key = match.getKey();
                            userDate = match.getDate();
                        }

                        boolean isArchive = match.getArchive();

                        if (!isArchive && !match.getTrash() && !isPin) {

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
        });
        data.clear();
        unPinSize = 0;
    }

    @Override
    public void showPinnedRecyclerData(final RecyclerView recyclerView) {

        pinrecycler = recyclerView;

        pinData = new ArrayList<>();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                for (DocumentSnapshot postSnapshot : documentSnapshots.getDocuments()) {

                    DataModel match = postSnapshot.toObject(DataModel.class);

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

                                    id = data.get(position).getId();
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

                                    id = data.get(position).getId();
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

                                    id = pinData.get(position).getId();
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

                                    id = pinData.get(position).getId();
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

        reference = FirebaseFirestore.getInstance().collection("Data").document(userID).collection("Notes");

        if(dire.equals("Left")) {
            Map<String, Object> change = new HashMap<>();
            change.put("archive", false);
            reference.document(id).update(change);
            dataAdapter.notifyDataSetChanged();
        }else if(dire.equals("LeftUn")){
            Map<String, Object> change = new HashMap<>();
            change.put("archive", false);
            change.put("pin", true);
            reference.document(id).update(change);
            dataAdapter.notifyDataSetChanged();
        }else if(dire.equals("RightUn")){
            Map<String, Object> change = new HashMap<>();
            change.put("trash", false);
            change.put("pin", true);
            reference.document(id).update(change);
            dataAdapter.notifyDataSetChanged();
        }else {
            Map<String, Object> change = new HashMap<>();
            change.put("trash", false);
            reference.document(id).update(change);
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
            unpinrecycler.setAdapter(dataAdapter);
            pinrecycler.setAdapter(pinDataAdapter);
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

        reference = FirebaseFirestore.getInstance().collection("Data").document(userID).collection("Notes");

        if(!isPin) {
            Map<String, Object> change = new HashMap<>();
            change.put("archive", true);
            reference.document(id).update(change);
        }else{
            Map<String, Object> change = new HashMap<>();
            change.put("archive", true);
            change.put("pin", false);
            reference.document(id).update(change);
        }


    }

    void changeDataTrash(final String id,String date, boolean isPin){

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseFirestore.getInstance().collection("Data").document(userID).collection("Notes");

        if(!isPin) {
            Map<String, Object> change = new HashMap<>();
            change.put("trash", true);
            reference.document(id).update(change);
        }else{
            Map<String, Object> change = new HashMap<>();
            change.put("trash", true);
            change.put("pin", false);
            reference.document(id).update(change);
        }

    }
}
