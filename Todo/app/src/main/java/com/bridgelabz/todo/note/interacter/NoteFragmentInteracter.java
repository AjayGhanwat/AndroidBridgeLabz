package com.bridgelabz.todo.note.interacter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.bridgelabz.todo.adapter.NoteDataAdapter;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.note.View.NoteFragment;
import com.bridgelabz.todo.note.presenter.NoteFragmentPresenter;
import com.bridgelabz.todo.note.presenter.NoteFragmentPresenterInterface;
import com.bridgelabz.todo.sqlitedatabase.SQLiteDatabaseHandler;
import com.bridgelabz.todo.util.NetworkConnection;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.bridgelabz.todo.constant.Constant.firebase_data_date;
import static com.bridgelabz.todo.constant.Constant.user_data_FirebaseFirestore;
import static com.bridgelabz.todo.constant.Constant.user_firestore_data_keys;
import static com.bridgelabz.todo.constant.Constant.user_left_unppined_data;
import static com.bridgelabz.todo.constant.Constant.user_note_FirebaseFirestore;
import static com.bridgelabz.todo.constant.Constant.user_note_dire_l;
import static com.bridgelabz.todo.constant.Constant.user_note_dire_r;
import static com.bridgelabz.todo.constant.Constant.user_note_firebase_database_arch;
import static com.bridgelabz.todo.constant.Constant.user_note_firebase_database_trash;
import static com.bridgelabz.todo.constant.Constant.user_note_pin;
import static com.bridgelabz.todo.constant.Constant.user_note_pinned_data_l;
import static com.bridgelabz.todo.constant.Constant.user_note_pinned_data_r;
import static com.bridgelabz.todo.constant.Constant.user_note_undochanges_l;
import static com.bridgelabz.todo.constant.Constant.user_note_undochanges_r;
import static com.bridgelabz.todo.constant.Constant.user_right_unppined_data;

public class NoteFragmentInteracter implements NoteFragmentInteracterInterface {

    public static NoteDataAdapter dataAdapter, showDataPin, showDataUnPin;
    public static NoteDataAdapter changeDataAdapter;
    public static ArrayList<DataModel> changeData = new ArrayList<>();
    static CollectionReference coll;
    Context context;
    NoteFragmentPresenterInterface noteInterface;
    ArrayList<DataModel> data;
    ArrayList<DataModel> pinData;
    NoteDataAdapter pinDataAdapter;
    RecyclerView pinrecycler, unpinrecycler;
    int pinSize = 0, unPinSize = 0;
    String id, date;
    CollectionReference reference;
    String dire;
    boolean isPin;
    ArrayList<DataModel> userNotes = new ArrayList<>();
    int pinSizeIS = 0;
    int unPinSizeIS = 0;
    ArrayList<DataModel> userPinNote;
    ArrayList<DataModel> userUnpinNote;
    int changePinRecyData = 0;
    int changeUnPinRecyData = 0;
    int locationDestination;
    int locationSource;

    public NoteFragmentInteracter(Context context, NoteFragmentPresenter presenter) {

        this.context = context;
        this.noteInterface = presenter;

    }

    @Override
    public void showRecyclerData(final RecyclerView recyclerView) {

        unpinrecycler = recyclerView;

        data = new ArrayList<>();

        unPinSize = 0;

        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseFirestore.getInstance().collection("Data").document(userID).collection("Notes");

        reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(context);
                ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();

                for (int i = 0; i < dataModels.size(); i++) {

                    if (!dataModels.get(i).getArchive() && !dataModels.get(i).getTrash() && !dataModels.get(i).getPin()) {

                        unPinSize++;
                        data.add(dataModels.get(i));
                        userNotes.add(dataModels.get(i));
                        changeData.add(dataModels.get(i));
                        //changeDataAdapter = new NoteDataAdapter(changeData);
                        //showDataUnPin = new NoteDataAdapter(changeData);
                        dataAdapter = new NoteDataAdapter(data);
                        unpinrecycler.setAdapter(dataAdapter);
                        unpinrecycler.invalidate();
                        dataAdapter.notifyDataSetChanged();
                        if (pinDataAdapter != null) {
                            pinrecycler.invalidate();
                            pinDataAdapter = new NoteDataAdapter(pinData);
                            pinrecycler.setAdapter(pinDataAdapter);
                            pinDataAdapter.notifyDataSetChanged();
                        }
                    }
                    NoteFragment.isChecked(pinSize, unPinSize);
                }
            }
        });
        unPinSize = 0;
        changeData.clear();
        data.clear();

    }

    @Override
    public void showPinnedRecyclerData(final RecyclerView recyclerView) {

        pinrecycler = recyclerView;

        pinData = new ArrayList<>();

        pinSize = 0;

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseFirestore.getInstance().collection("Data").document(userID).collection("Notes");

        reference.orderBy(user_firestore_data_keys, Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(context);
                ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();

                for (int i = 0; i < dataModels.size(); i++) {

                    if (!dataModels.get(i).getArchive() && !dataModels.get(i).getTrash() && dataModels.get(i).getPin()) {

                        pinSize++;
                        pinData.add(dataModels.get(i));
                        pinDataAdapter = new NoteDataAdapter(pinData);
                        userNotes.add(dataModels.get(i));
                        showDataPin = new NoteDataAdapter(pinData);
                        pinrecycler.setAdapter(pinDataAdapter);
                        pinrecycler.invalidate();
                        pinDataAdapter.notifyDataSetChanged();
                        if (dataAdapter != null) {
                            unpinrecycler.invalidate();
                            dataAdapter = new NoteDataAdapter(data);
                            unpinrecycler.setAdapter(dataAdapter);
                            dataAdapter.notifyDataSetChanged();
                        }
                    }
                    NoteFragment.isChecked(pinSize, unPinSize);
                }
            }
        });
        pinSize = 0;
        pinData.clear();
    }

    /*
     *  It is used to change the data chnages has beedn \
     */

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
                                    changeDataArchive(id, date, isPin);
                                    dire = user_note_dire_l;
                                    dataAdapter = new NoteDataAdapter(data);
                                    recyclerView.setAdapter(dataAdapter);
                                    dataAdapter.notifyDataSetChanged();
                                    data.remove(position);
                                    dataAdapter.notifyItemRemoved(position);
                                    if (isPin) {
                                        noteInterface.showSnacBar(user_note_pinned_data_r);
                                        dire = user_left_unppined_data;
                                    } else {
                                        noteInterface.showSnacBar(user_note_undochanges_r);
                                    }
                                }
                                NoteFragment.isChecked(pinSize, unPinSize);
                                dataAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    id = data.get(position).getId();
                                    date = data.get(position).getDate();
                                    isPin = data.get(position).getPin();
                                    changeDataTrash(id, date, isPin);
                                    dire = user_note_dire_r;
                                    dataAdapter = new NoteDataAdapter(data);
                                    recyclerView.setAdapter(dataAdapter);
                                    dataAdapter.notifyDataSetChanged();
                                    data.remove(position);
                                    dataAdapter.notifyItemRemoved(position);
                                    if (isPin) {
                                        noteInterface.showSnacBar(user_note_pinned_data_l);
                                        dire = user_right_unppined_data;
                                    } else {
                                        noteInterface.showSnacBar(user_note_undochanges_l);
                                    }
                                }
                                NoteFragment.isChecked(pinSize, unPinSize);
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
                                    changeDataArchive(id, date, isPin);
                                    dire = user_note_dire_l;
                                    pinDataAdapter = new NoteDataAdapter(pinData);
                                    recyclerView.setAdapter(pinDataAdapter);
                                    pinDataAdapter.notifyDataSetChanged();
                                    pinData.remove(position);
                                    pinDataAdapter.notifyItemRemoved(position);
                                    if (isPin) {
                                        noteInterface.showSnacBar(user_note_pinned_data_r);
                                        dire = user_left_unppined_data;
                                    } else {
                                        noteInterface.showSnacBar(user_note_undochanges_r);
                                    }
                                }
                                pinDataAdapter.notifyDataSetChanged();
                                NoteFragment.isChecked(pinSize, unPinSize);
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    id = pinData.get(position).getId();
                                    date = pinData.get(position).getDate();
                                    isPin = pinData.get(position).getPin();
                                    changeDataTrash(id, date, isPin);
                                    dire = user_note_dire_r;
                                    pinDataAdapter = new NoteDataAdapter(pinData);
                                    recyclerView.setAdapter(pinDataAdapter);
                                    pinDataAdapter.notifyDataSetChanged();
                                    pinData.remove(position);
                                    pinDataAdapter.notifyItemRemoved(position);
                                    if (isPin) {
                                        noteInterface.showSnacBar(user_note_pinned_data_l);
                                        dire = user_right_unppined_data;
                                    } else {
                                        noteInterface.showSnacBar(user_note_undochanges_l);
                                    }
                                }
                                pinDataAdapter.notifyDataSetChanged();
                                NoteFragment.isChecked(pinSize, unPinSize);
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);
    }

    @Override
    public void undoChange() {

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        int LocationNOte = -1;

        SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(context);
        ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();
        for (int i = 0; i < dataModels.size(); i++) {
            if (id.equals(dataModels.get(i).getId())) {
                LocationNOte = i;
            }
        }

        DataModel dataModel = dataModels.get(LocationNOte);

        reference = FirebaseFirestore.getInstance().collection("Data").document(userID).collection("Notes");

        if (NetworkConnection.isNetworkConnected(context)) {

            if (NetworkConnection.isInternetAvailable()) {

                if (dire.equals(user_note_dire_l)) {
                    Map<String, Object> change = new HashMap<>();
                    change.put(user_note_firebase_database_arch, false);
                    reference.document(id).update(change);
                    dataModel.setArchive(false);
                    sqLiteDatabaseHandler.updateRecord(dataModel);
                    dataAdapter.notifyDataSetChanged();
                } else if (dire.equals(user_left_unppined_data)) {
                    Map<String, Object> change = new HashMap<>();
                    change.put(user_note_firebase_database_arch, false);
                    change.put(user_note_pin, true);
                    reference.document(id).update(change);
                    dataModel.setArchive(false);
                    dataModel.setPin(true);
                    sqLiteDatabaseHandler.updateRecord(dataModel);
                    dataAdapter.notifyDataSetChanged();
                } else if (dire.equals(user_right_unppined_data)) {
                    Map<String, Object> change = new HashMap<>();
                    change.put(user_note_firebase_database_trash, false);
                    change.put(user_note_pin, true);
                    reference.document(id).update(change);
                    dataModel.setTrash(false);
                    dataModel.setPin(true);
                    sqLiteDatabaseHandler.updateRecord(dataModel);
                    dataAdapter.notifyDataSetChanged();
                } else {
                    Map<String, Object> change = new HashMap<>();
                    change.put(user_note_firebase_database_trash, false);
                    reference.document(id).update(change);
                    dataModel.setTrash(false);
                    sqLiteDatabaseHandler.updateRecord(dataModel);
                    dataAdapter.notifyDataSetChanged();
                }

            }

        } else {

            if (dire.equals(user_note_dire_l)) {

                dataModel.setArchive(false);
                sqLiteDatabaseHandler.updateRecord(dataModel);
                dataAdapter.notifyDataSetChanged();
            } else if (dire.equals(user_left_unppined_data)) {
                dataModel.setArchive(false);
                dataModel.setPin(true);
                sqLiteDatabaseHandler.updateRecord(dataModel);
                dataAdapter.notifyDataSetChanged();
            } else if (dire.equals(user_right_unppined_data)) {
                dataModel.setTrash(false);
                dataModel.setPin(true);
                sqLiteDatabaseHandler.updateRecord(dataModel);
                dataAdapter.notifyDataSetChanged();
            } else {
                dataModel.setTrash(false);
                sqLiteDatabaseHandler.updateRecord(dataModel);
                dataAdapter.notifyDataSetChanged();
            }

        }
    }

    @Override
    public void showSearchData(final RecyclerView recyclerView, String newText) {

        userPinNote = new ArrayList<>();
        userUnpinNote = new ArrayList<>();

        if (newText != null && !newText.isEmpty()) {

            int recyclerViewID = recyclerView.getId();

            if (recyclerViewID == 2131689662) {
                for (DataModel item : userNotes) {
                    if (!item.getPin() && !item.getArchive() && !item.getTrash()) {
                        if (item.getTitle().contains(newText)) {
                            userPinNote.add(item);
                        }
                        if (userPinNote.size() == 0) {
                            pinSizeIS = 0;
                        } else {
                            pinSizeIS = userPinNote.size();
                        }
                        NoteDataAdapter dataAdapter1 = new NoteDataAdapter(userPinNote);
                        recyclerView.refreshDrawableState();
                        recyclerView.setAdapter(dataAdapter1);
                        dataAdapter1.notifyDataSetChanged();
                    }
                }
                NoteFragment.isChecked(pinSizeIS, unPinSizeIS);
            }else{
                for (DataModel item : userNotes) {
                    if (item.getPin()) {
                        if (item.getTitle().contains(newText)) {
                            userUnpinNote.add(item);
                        }
                        if (userUnpinNote.size() == 0) {
                            unPinSizeIS = 0;
                        } else {
                            unPinSizeIS = userUnpinNote.size();
                        }
                        NoteDataAdapter dataAdapter1 = new NoteDataAdapter(userUnpinNote);
                        recyclerView.refreshDrawableState();
                        recyclerView.setAdapter(dataAdapter1);
                        dataAdapter1.notifyDataSetChanged();
                    }
                }
                NoteFragment.isChecked(pinSizeIS, unPinSizeIS);
            }
            NoteFragment.isChecked(pinSizeIS, unPinSizeIS);
        } else {
            NoteFragment.isChecked(data.size(), pinData.size());
            unpinrecycler.setAdapter(dataAdapter);
            pinrecycler.setAdapter(pinDataAdapter);
        }
    }

    /*
     *  the changeLocationNote it will used to change the location of note after
     *  the note moves to different location
     */

    @Override
    public void resetRecycler(RecyclerView recyclerView) {
        recyclerView.setAdapter(dataAdapter);
    }

    @Override
    public void resetPinRecycler(RecyclerView recyclerView) {
        recyclerView.setAdapter(pinDataAdapter);
    }

    @Override
    public void changeLocationNote(int startLoc, int endLoc) {

        int noteLocation = startLoc;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        coll = FirebaseFirestore.getInstance().collection(user_data_FirebaseFirestore).document(userId).collection(user_note_FirebaseFirestore);

        if (NetworkConnection.isNetworkConnected(context)) {

            if (NetworkConnection.isInternetAvailable()) {

                SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(context);
                ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();

                if (startLoc < endLoc) {

                    int desNote = startLoc + 1;

                    for (int i = startLoc; i < endLoc; i++) {

                        noteLocation++;
                        String destinationLocation = changeData.get(i).getKey();
                        String destinationLocationDate = changeData.get(i).getDate();
                        String sourceLocation = changeData.get(noteLocation).getKey();
                        String sourceLocationDate = changeData.get(noteLocation).getDate();

                        String sourceID = changeData.get(startLoc).getId();
                        String desctinationID = changeData.get(desNote).getId();

                        coll.document(sourceID).update(user_firestore_data_keys, sourceLocation);
                        coll.document(sourceID).update(firebase_data_date, sourceLocationDate);
                        coll.document(desctinationID).update(user_firestore_data_keys, destinationLocation);
                        coll.document(desctinationID).update(firebase_data_date, destinationLocationDate);

                        for (int j = 0; j < dataModels.size(); j++) {
                            if (sourceID.equals(dataModels.get(j).getId())) {
                                locationSource = j;
                            }
                        }

                        for (int j = 0; j < dataModels.size(); j++) {
                            if (desctinationID.equals(dataModels.get(j).getId())) {
                                locationDestination = j;
                            }
                        }

                        DataModel dataModelSource = dataModels.get(locationSource);
                        DataModel dataModelDestination = dataModels.get(locationDestination);

                        dataModelSource.setKey(sourceLocation);
                        dataModelSource.setDate(sourceLocationDate);
                        dataModelDestination.setKey(destinationLocation);
                        dataModelDestination.setDate(destinationLocationDate);

                        sqLiteDatabaseHandler.updateRecord(dataModelSource);
                        sqLiteDatabaseHandler.updateRecord(dataModelDestination);

                        desNote++;

                    }
                }

                if (startLoc > endLoc) {

                    if (endLoc != -1) {

                        int desNote = startLoc - 1;

                        for (int i = startLoc; i > endLoc; i--) {

                            noteLocation--;
                            String destinationLocation = changeData.get(i).getKey();
                            String destinationLocationDate = changeData.get(i).getDate();
                            String sourceLocation = changeData.get(noteLocation).getKey();
                            String sourceLocationDate = changeData.get(noteLocation).getDate();

                            String sourceID = changeData.get(startLoc).getId();
                            String desctinationID = changeData.get(desNote).getId();

                            coll.document(sourceID).update(user_firestore_data_keys, sourceLocation);
                            coll.document(sourceID).update(firebase_data_date, sourceLocationDate);
                            coll.document(desctinationID).update(user_firestore_data_keys, destinationLocation);
                            coll.document(desctinationID).update(firebase_data_date, destinationLocationDate);

                            for (int j = 0; j < dataModels.size(); j++) {
                                if (sourceID.equals(dataModels.get(j).getId())) {
                                    locationSource = j;
                                }
                            }

                            for (int j = 0; j < dataModels.size(); j++) {
                                if (desctinationID.equals(dataModels.get(j).getId())) {
                                    locationDestination = j;
                                }
                            }

                            DataModel dataModelSource = dataModels.get(locationSource);
                            DataModel dataModelDestination = dataModels.get(locationDestination);

                            dataModelSource.setKey(sourceLocation);
                            dataModelSource.setDate(sourceLocationDate);
                            dataModelDestination.setKey(destinationLocation);
                            dataModelDestination.setDate(destinationLocationDate);

                            sqLiteDatabaseHandler.updateRecord(dataModelSource);
                            sqLiteDatabaseHandler.updateRecord(dataModelDestination);

                            desNote--;

                        }
                    }
                }

            }

        } else {

            SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(context);
            ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();

            if (startLoc < endLoc) {

                int desNote = startLoc + 1;

                for (int i = startLoc; i < endLoc; i++) {

                    noteLocation++;
                    String destinationLocation = changeData.get(i).getKey();
                    String destinationLocationDate = changeData.get(i).getDate();
                    String sourceLocation = changeData.get(noteLocation).getKey();
                    String sourceLocationDate = changeData.get(noteLocation).getDate();

                    String sourceID = changeData.get(startLoc).getId();
                    String desctinationID = changeData.get(desNote).getId();

                    for (int j = 0; j < dataModels.size(); j++) {
                        if (sourceID.equals(dataModels.get(j).getId())) {
                            locationSource = j;
                        }
                    }

                    for (int j = 0; j < dataModels.size(); j++) {
                        if (desctinationID.equals(dataModels.get(j).getId())) {
                            locationDestination = j;
                        }
                    }

                    DataModel dataModelSource = dataModels.get(locationSource);
                    DataModel dataModelDestination = dataModels.get(locationDestination);

                    dataModelSource.setKey(sourceLocation);
                    dataModelSource.setDate(sourceLocationDate);
                    dataModelDestination.setKey(destinationLocation);
                    dataModelDestination.setDate(destinationLocationDate);

                    sqLiteDatabaseHandler.updateRecord(dataModelSource);
                    sqLiteDatabaseHandler.updateRecord(dataModelDestination);

                    desNote++;

                }
            }

            if (startLoc > endLoc) {

                if (endLoc != -1) {

                    int desNote = startLoc - 1;

                    for (int i = startLoc; i > endLoc; i--) {

                        noteLocation--;
                        String destinationLocation = changeData.get(i).getKey();
                        String destinationLocationDate = changeData.get(i).getDate();
                        String sourceLocation = changeData.get(noteLocation).getKey();
                        String sourceLocationDate = changeData.get(noteLocation).getDate();

                        String sourceID = changeData.get(startLoc).getId();
                        String desctinationID = changeData.get(desNote).getId();

                        for (int j = 0; j < dataModels.size(); j++) {
                            if (sourceID.equals(dataModels.get(j).getId())) {
                                locationSource = j;
                            }
                        }

                        for (int j = 0; j < dataModels.size(); j++) {
                            if (desctinationID.equals(dataModels.get(j).getId())) {
                                locationDestination = j;
                            }
                        }

                        DataModel dataModelSource = dataModels.get(locationSource);
                        DataModel dataModelDestination = dataModels.get(locationDestination);

                        dataModelSource.setKey(sourceLocation);
                        dataModelSource.setDate(sourceLocationDate);
                        dataModelDestination.setKey(destinationLocation);
                        dataModelDestination.setDate(destinationLocationDate);

                        sqLiteDatabaseHandler.updateRecord(dataModelSource);
                        sqLiteDatabaseHandler.updateRecord(dataModelDestination);

                        desNote--;

                    }
                }
            }

        }
    }
    /*
     *  the note swapped to the left then tha database change archive data to archived true
     *  if the note is pinned then it will change the data unpinned and archived
     */

    void changeDataArchive(final String id, String date, boolean isPin) {

        int LocationNOte = -1;

        SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(context);
        ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();

        for (int i = 0; i < dataModels.size(); i++) {
            if (id.equals(dataModels.get(i).getId())) {
                LocationNOte = i;
            }
        }

        DataModel dataModel = dataModels.get(LocationNOte);


        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseFirestore.getInstance().collection(user_data_FirebaseFirestore).document(userID).collection(user_note_FirebaseFirestore);

        if (NetworkConnection.isNetworkConnected(context)) {

            if (NetworkConnection.isInternetAvailable()) {

                if (!isPin) {
                    Map<String, Object> change = new HashMap<>();
                    change.put(user_note_firebase_database_arch, true);
                    reference.document(id).update(change);
                    dataModel.setArchive(true);
                    sqLiteDatabaseHandler.updateRecord(dataModel);
                    changePinRecyData = pinData.size();
                    changeUnPinRecyData = data.size();
                } else {
                    Map<String, Object> change = new HashMap<>();
                    change.put(user_note_firebase_database_arch, true);
                    change.put(user_note_pin, false);
                    reference.document(id).update(change);
                    dataModel.setPin(false);
                    dataModel.setArchive(true);
                    sqLiteDatabaseHandler.updateRecord(dataModel);
                    changePinRecyData = pinData.size();
                    changeUnPinRecyData = data.size();
                }
            }
        } else {

            if (!isPin) {

                dataModel.setArchive(true);
                sqLiteDatabaseHandler.updateRecord(dataModel);
                changePinRecyData = pinData.size();
                changeUnPinRecyData = data.size();
            } else {

                dataModel.setPin(false);
                dataModel.setArchive(true);
                sqLiteDatabaseHandler.updateRecord(dataModel);
                changePinRecyData = pinData.size();
                changeUnPinRecyData = data.size();
            }

        }

        NoteFragment.isChecked(changePinRecyData, changeUnPinRecyData);

    }

    /*
     *  The card view sapped to the right the the it will change the trash data to trashed note
     *  if the note is pinned then it will change the pin to unpinned and then it will change to trashed
     */

    void changeDataTrash(final String id, String date, boolean isPin) {

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseFirestore.getInstance().collection(user_data_FirebaseFirestore).document(userID).collection(user_note_FirebaseFirestore);

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
                if (!isPin) {
                    Map<String, Object> change = new HashMap<>();
                    change.put(user_note_firebase_database_trash, true);
                    reference.document(id).update(change);
                    dataModel.setTrash(true);
                    sqLiteDatabaseHandler.updateRecord(dataModel);
                    changePinRecyData = pinData.size();
                    changeUnPinRecyData = data.size();
                } else {
                    Map<String, Object> change = new HashMap<>();
                    change.put(user_note_firebase_database_trash, true);
                    change.put(user_note_pin, false);
                    reference.document(id).update(change);
                    dataModel.setPin(false);
                    dataModel.setTrash(true);
                    sqLiteDatabaseHandler.updateRecord(dataModel);
                    changeUnPinRecyData = data.size();
                    changePinRecyData = pinData.size();
                }
            }
        }else {

            if (!isPin) {

                dataModel.setTrash(true);
                sqLiteDatabaseHandler.updateRecord(dataModel);
                changePinRecyData = pinData.size();
                changeUnPinRecyData = data.size();
            } else {

                dataModel.setPin(false);
                dataModel.setTrash(true);
                sqLiteDatabaseHandler.updateRecord(dataModel);
                changePinRecyData = pinData.size();
                changeUnPinRecyData = data.size();
            }
        }

        NoteFragment.isChecked(changePinRecyData, changeUnPinRecyData);
    }
}