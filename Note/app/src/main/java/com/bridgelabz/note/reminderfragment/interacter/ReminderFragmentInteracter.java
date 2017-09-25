package com.bridgelabz.note.reminderfragment.interacter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bridgelabz.note.adapter.NoteDataAdapter;
import com.bridgelabz.note.model.DataModel;
import com.bridgelabz.note.reminderfragment.presenter.ReminderFragmentPresenter;
import com.bridgelabz.note.reminderfragment.presenter.ReminderFragmentPresenterInterface;
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

public class ReminderFragmentInteracter implements ReminderFragmentInteracterInterface {

    Context context;
    ReminderFragmentPresenterInterface reminderInterface;

    ArrayList<DataModel> data;
    NoteDataAdapter dataAdapter;

    public ReminderFragmentInteracter(Context context, ReminderFragmentPresenter presenter) {

        this.context = context;
        this.reminderInterface = presenter;

    }

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

                    Date date = new Date();
                    String fdate = new SimpleDateFormat("yyyy-MM-dd").format(date);

                    if (fdate.equals(match.getReminderDate())) {

                        boolean isArchive = match.getArchive();

                        if (!isArchive && !match.getTrash() && match.getReminder()) {

                            data.add(match);
                            Log.i("sd", "onChildAdded: " + match);
                            dataAdapter = new NoteDataAdapter(data);
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
    public void showSearchData(RecyclerView recyclerView, String newText) {
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
}
