package com.bridgelabz.todo.reminder.interacter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.bridgelabz.todo.adapter.NoteDataAdapter;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.reminder.presenter.ReminderFragmentPresenter;
import com.bridgelabz.todo.reminder.presenter.ReminderFragmentPresenterInterface;
import com.bridgelabz.todo.sqlitedatabase.SQLiteDatabaseHandler;
import com.bridgelabz.todo.util.NetworkConnection;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.bridgelabz.todo.constant.Constant.user_data_FirebaseFirestore;
import static com.bridgelabz.todo.constant.Constant.user_date_format;
import static com.bridgelabz.todo.constant.Constant.user_note_FirebaseFirestore;

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

        reference = FirebaseFirestore.getInstance().collection(user_data_FirebaseFirestore).document(userID).collection(user_note_FirebaseFirestore);

        if(NetworkConnection.isNetworkConnected(context)){

            if (NetworkConnection.isInternetAvailable()){

                reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {

                        SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(context);
                        ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();

                        for (int i = 0; i < dataModels.size(); i++) {

                            Date date = new Date();
                            String fdate = new SimpleDateFormat(user_date_format).format(date);
                            String isDate = new SimpleDateFormat("yyyy-MM-d").format(date);

                            if (fdate.equals(dataModels.get(i).getReminderDate()) || isDate.equals(dataModels.get(i).getReminderDate())) {

                                if (!dataModels.get(i).getArchive() && !dataModels.get(i).getTrash() && dataModels.get(i).getReminder()) {

                                    data.add(dataModels.get(i));
                                    dataAdapter = new NoteDataAdapter(data);
                                    recyclerView.setAdapter(dataAdapter);
                                    dataAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });

            }

        }else{



        }


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
