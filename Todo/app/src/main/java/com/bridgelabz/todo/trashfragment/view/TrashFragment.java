package com.bridgelabz.todo.trashfragment.view;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bridgelabz.todo.R;
import com.bridgelabz.todo.archivefragment.presenter.ArchiveFragmentPresenter;
import com.bridgelabz.todo.base.BaseFragment;
import com.bridgelabz.todo.model.UserData;
import com.bridgelabz.todo.trashfragment.presenter.TrashFragmentPresenter;
import com.bridgelabz.todo.trashfragment.presenter.TrashFragmentPresenterInterface;
import com.firebase.client.DataSnapshot;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.bridgelabz.todo.R.drawable.ic_view_list_black_24dp;
import static com.bridgelabz.todo.R.drawable.ic_view_quilt_black_24dp;

public class TrashFragment extends BaseFragment implements TrashFragmentInterface{

    View v;
    static RecyclerView recyclerView;

    RelativeLayout relativeLayout;

    ProgressDialog progress;

    static LinearLayoutManager linearLayoutManager;
    static StaggeredGridLayoutManager gridLayoutManager;
    static RecyclerView.LayoutManager layoutManager;

    static TrashFragmentPresenterInterface presenter;

    static CollectionReference collectionReference;

    String layout;

    static String userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_trash, container, false);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.v = view;

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        collectionReference = FirebaseFirestore.getInstance().collection("User").document(userId).collection("UserInfo");

        relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayoutTrash);

        linearLayoutManager = new LinearLayoutManager(getContext());
        gridLayoutManager = new StaggeredGridLayoutManager(2, 1);

        initView();
        clickListning();
    }

    private void isLayout() {
        if(layout.equals("linear")) {
            layoutManager = linearLayoutManager;
            recyclerView.setLayoutManager(layoutManager);
        }else{
            layoutManager = gridLayoutManager;
            recyclerView.setLayoutManager(layoutManager);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isLayout();
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.showRecycler(recyclerView);
        presenter.swappable(recyclerView);

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentSnapshot document : documentSnapshots.getDocuments()) {

                    UserData user = document.toObject(UserData.class);

                    layout = user.getLayout();

                    isLayout();
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void initView() {

        presenter = new TrashFragmentPresenter(getContext(),this);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerTrash);
        recyclerView.setHasFixedSize(false);

    }

    @Override
    public void clickListning() {

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void showRecyclerSuccess(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void showRecyclerUnsuccess(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void showRecyclerPregress(String msg) {
        progress = new ProgressDialog(getContext());
        progress.setMessage(msg);
        progress.show();
    }

    @Override
    public void dismissRecyclerProgress() {
        progress.dismiss();
    }

    @Override
    public void showSnacBar(String msg) {
        Snackbar.make(relativeLayout, msg, Snackbar.LENGTH_LONG).show();
    }

    public static void onItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.layoutManager) {

            if (!linearLayoutManager.equals(layoutManager)) {
                layoutManager = linearLayoutManager;
                item.setIcon(ic_view_quilt_black_24dp);
                Map<String, Object> changeLayout = new HashMap<>();
                changeLayout.put("layout", "linear");
                collectionReference.document(userId).set(changeLayout);
                recyclerView.setLayoutManager(layoutManager);
            } else if (linearLayoutManager.equals(layoutManager)) {
                layoutManager = gridLayoutManager;
                item.setIcon(ic_view_list_black_24dp);
                Map<String, Object> changeLayout = new HashMap<>();
                changeLayout.put("layout", "grid");
                collectionReference.document(userId).set(changeLayout);
                recyclerView.setLayoutManager(layoutManager);
            }
        }
    }

    public static void searchItem(String newText) {
        presenter.showSearchData(recyclerView, newText);
    }

    public static void resetRecyclerView() {
        presenter.refreshRecycler(recyclerView);
    }

}
