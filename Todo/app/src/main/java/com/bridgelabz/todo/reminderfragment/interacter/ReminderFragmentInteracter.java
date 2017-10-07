package com.bridgelabz.todo.reminderfragment.interacter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bridgelabz.todo.adapter.NoteDataAdapter;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.reminderfragment.presenter.ReminderFragmentPresenter;
import com.bridgelabz.todo.reminderfragment.presenter.ReminderFragmentPresenterInterface;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.GenericTypeIndicator;
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

public class ReminderFragmentInteracter implements ReminderFragmentInteracterInterface {

    Context context;
    ReminderFragmentPresenterInterface reminderInterface;

    ArrayList<DataModel> data;
    NoteDataAdapter dataAdapter;

    CollectionReference reference;

    public ReminderFragmentInteracter(Context context, ReminderFragmentPresenter presenter) {

        this.context = context;
        this.reminderInterface = presenter;

    }

    @Override
    public void showRecyclerData(final RecyclerView recyclerView) {

        data = new ArrayList<>();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseFirestore.getInstance().collection("Data").document(userID).collection("Notes");

        reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                for (DocumentSnapshot postSnapshot : documentSnapshots.getDocuments()) {

                    DataModel match = postSnapshot.toObject(DataModel.class);

                    Date date = new Date();
                    String fdate = new SimpleDateFormat("yyyy-MM-dd").format(date);
                    String isDate = new SimpleDateFormat("yyyy-MM-d").format(date);

                    if (fdate.equals(match.getReminderDate()) || isDate.equals(match.getReminderDate())) {

                        if (!match.getArchive() && !match.getTrash() && match.getReminder()) {

                            data.add(match);
                            dataAdapter = new NoteDataAdapter(data);
                            recyclerView.setAdapter(dataAdapter);
                            dataAdapter.notifyDataSetChanged();
                        }
                    }
                }

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
