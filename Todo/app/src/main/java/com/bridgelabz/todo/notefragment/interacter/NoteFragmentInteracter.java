package com.bridgelabz.todo.notefragment.interacter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.bridgelabz.todo.adapter.NoteDataAdapter;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.notefragment.View.NoteFragment;
import com.bridgelabz.todo.notefragment.presenter.NoteFragmentPresenter;
import com.bridgelabz.todo.notefragment.presenter.NoteFragmentPresenterInterface;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NoteFragmentInteracter implements NoteFragmentInteracterInterface {

    Context context;
    NoteFragmentPresenterInterface noteInterface;

    ArrayList<DataModel> data;
    ArrayList<DataModel> pinData;
    NoteDataAdapter dataAdapter;
    NoteDataAdapter pinDataAdapter;

    RecyclerView pinrecycler, unpinrecycler;

    int pinSize = 0, unPinSize = 0;
    DataModel dataIS;
    String id, date;
    CollectionReference reference;
    String dire;
    boolean isPin;
    ArrayList<DataModel> userNotes;

    public NoteFragmentInteracter(Context context, NoteFragmentPresenter presenter) {

        this.context = context;
        this.noteInterface = presenter;

    }

    @Override
    public void showRecyclerData(final RecyclerView recyclerView) {

        unpinrecycler = recyclerView;

        userNotes = new ArrayList<>();

        data = new ArrayList<>();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseFirestore.getInstance().collection("Data").document(userID).collection("Notes");

        reference.orderBy("key", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                for (DocumentSnapshot postSnapshot : documentSnapshots.getDocuments()) {

                    DataModel match = postSnapshot.toObject(DataModel.class);
                    userNotes.add(match);

                    boolean isPin = match.getPin();

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

        reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                for (DocumentSnapshot postSnapshot : documentSnapshots.getDocuments()) {

                    DataModel match = postSnapshot.toObject(DataModel.class);

                    boolean isPin = match.getPin();

                    boolean isArchive = match.getArchive();

                    if (!isArchive && !match.getTrash() && isPin) {

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
                                    dire = "Left";
                                    dataAdapter = new NoteDataAdapter(data);
                                    recyclerView.setAdapter(dataAdapter);
                                    dataAdapter.notifyDataSetChanged();
                                    data.remove(position);
                                    dataAdapter.notifyItemRemoved(position);
                                    if (isPin) {
                                        noteInterface.showSnacBar("Note Archive & Unpinned!");
                                        dire = "LeftUn";
                                    } else {
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
                                    changeDataTrash(id, date, isPin);
                                    dire = "Right";
                                    dataAdapter = new NoteDataAdapter(data);
                                    recyclerView.setAdapter(dataAdapter);
                                    dataAdapter.notifyDataSetChanged();
                                    data.remove(position);
                                    dataAdapter.notifyItemRemoved(position);
                                    if (isPin) {
                                        noteInterface.showSnacBar("Note Trashed & Unpinned!");
                                        dire = "RightUn";
                                    } else {
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
                                    changeDataArchive(id, date, isPin);
                                    dire = "Left";
                                    pinDataAdapter = new NoteDataAdapter(pinData);
                                    recyclerView.setAdapter(pinDataAdapter);
                                    pinDataAdapter.notifyDataSetChanged();
                                    pinData.remove(position);
                                    pinDataAdapter.notifyItemRemoved(position);
                                    if (isPin) {
                                        noteInterface.showSnacBar("Note Archive & Unpinned!");
                                        dire = "LeftUn";
                                    } else {
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
                                    changeDataTrash(id, date, isPin);
                                    dire = "Right";
                                    pinDataAdapter = new NoteDataAdapter(pinData);
                                    recyclerView.setAdapter(pinDataAdapter);
                                    pinDataAdapter.notifyDataSetChanged();
                                    pinData.remove(position);
                                    pinDataAdapter.notifyItemRemoved(position);
                                    if (isPin) {
                                        noteInterface.showSnacBar("Note Trashed & Unpinned!");
                                        dire = "RightUn";
                                    } else {
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

        if (dire.equals("Left")) {
            Map<String, Object> change = new HashMap<>();
            change.put("archive", false);
            reference.document(id).update(change);
            dataAdapter.notifyDataSetChanged();
        } else if (dire.equals("LeftUn")) {
            Map<String, Object> change = new HashMap<>();
            change.put("archive", false);
            change.put("pin", true);
            reference.document(id).update(change);
            dataAdapter.notifyDataSetChanged();
        } else if (dire.equals("RightUn")) {
            Map<String, Object> change = new HashMap<>();
            change.put("trash", false);
            change.put("pin", true);
            reference.document(id).update(change);
            dataAdapter.notifyDataSetChanged();
        } else {
            Map<String, Object> change = new HashMap<>();
            change.put("trash", false);
            reference.document(id).update(change);
            dataAdapter.notifyDataSetChanged();
        }
    }

    int pinSizeIS = 0;
    int unPinSizeIS = 0;

    ArrayList<DataModel> userPinNote;
    ArrayList<DataModel> userUnpinNote;

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
                        if (userPinNote.size() == 0){
                            pinSizeIS = 0;
                        }else{
                            pinSizeIS = userPinNote.size();
                        }
                        NoteDataAdapter dataAdapter1 = new NoteDataAdapter(userPinNote);
                        recyclerView.refreshDrawableState();
                        recyclerView.setAdapter(dataAdapter1);
                        dataAdapter1.notifyDataSetChanged();
                    }
                }
            } else {
                for (DataModel item : userNotes) {
                    if (item.getPin()) {
                        if (item.getTitle().contains(newText)) {
                            userUnpinNote.add(item);
                        }
                        if (userUnpinNote.size() == 0){
                            unPinSizeIS = 0;
                        }else{
                            unPinSizeIS = userUnpinNote.size();
                        }
                        NoteDataAdapter dataAdapter1 = new NoteDataAdapter(userUnpinNote);
                        recyclerView.refreshDrawableState();
                        recyclerView.setAdapter(dataAdapter1);
                        dataAdapter1.notifyDataSetChanged();
                    }
                }
            }
            NoteFragment.isChecked(pinSizeIS,unPinSizeIS);
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

    int changePinRecyData = 0;
    int changeUnPinRecyData = 0;

    void changeDataArchive(final String id, String date, boolean isPin) {

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseFirestore.getInstance().collection("Data").document(userID).collection("Notes");

        if (!isPin) {
            Map<String, Object> change = new HashMap<>();
            change.put("archive", true);
            reference.document(id).update(change);
            changePinRecyData = pinData.size();
        } else {
            Map<String, Object> change = new HashMap<>();
            change.put("archive", true);
            change.put("pin", false);
            reference.document(id).update(change);
            changeUnPinRecyData = data.size();
        }

        NoteFragment.isChecked(changePinRecyData,changeUnPinRecyData);

    }

    void changeDataTrash(final String id, String date, boolean isPin) {

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseFirestore.getInstance().collection("Data").document(userID).collection("Notes");

        if (!isPin) {
            Map<String, Object> change = new HashMap<>();
            change.put("trash", true);
            reference.document(id).update(change);
            changePinRecyData = pinData.size();

        } else {
            Map<String, Object> change = new HashMap<>();
            change.put("trash", true);
            change.put("pin", false);
            reference.document(id).update(change);
            changeUnPinRecyData = data.size();
        }

        NoteFragment.isChecked(changePinRecyData,changeUnPinRecyData);
    }
}
