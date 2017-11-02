package com.bridgelabz.todo.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.sqlitedatabase.SQLiteDatabaseHandler;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        try {
            if (isOnline(context)) {


                SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(context);
                final ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                final CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Data").document(userID).collection("Notes");


                for (int i = 0; i < dataModels.size(); i++) {

                    collectionReference.document(dataModels.get(i).getId()).set(dataModels.get(i));
                }

                ArrayList<DataModel> data = sqLiteDatabaseHandler.getAllRecordDel();

                for (int i = 0; i< data.size(); i++){

                    collectionReference.document(data.get(i).getId()).delete();

                }

                sqLiteDatabaseHandler.deleteAllDel();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
