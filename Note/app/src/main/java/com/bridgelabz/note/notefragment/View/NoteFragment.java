package com.bridgelabz.note.notefragment.View;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgelabz.note.R;
import com.bridgelabz.note.addnotes.view.AddActivity;
import com.bridgelabz.note.base.BaseFragment;
import com.bridgelabz.note.model.UserData;
import com.bridgelabz.note.notefragment.interacter.NoteFragmentInteracter;
import com.bridgelabz.note.notefragment.presenter.NoteFragmentPresenter;
import com.bridgelabz.note.notefragment.presenter.NoteFragmentPresenterInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.bridgelabz.note.R.drawable.ic_view_list_black_24dp;
import static com.bridgelabz.note.R.drawable.ic_view_quilt_black_24dp;
import static com.facebook.FacebookSdk.getApplicationContext;

public class NoteFragment extends BaseFragment implements NoteFragmentInterface {

    View v;
    Bundle bundle;

    public static RecyclerView recyclerView;
    ProgressDialog progress;

    static TextView pinned;
    static TextView unpinned;
    static RecyclerView pinrecyclerView;

    static DatabaseReference reference;

    static LinearLayoutManager linearLayoutManager;
    static StaggeredGridLayoutManager gridLayoutManager;
    static RecyclerView.LayoutManager layoutManager;

    RelativeLayout relativeLayout;

    String layout;

    static NoteFragmentPresenterInterface presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_note, container, false);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.v = view;
        this.bundle = savedInstanceState;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserData user = dataSnapshot.getValue(UserData.class);

                layout = user.getLayout();

                isLayout();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayoutManager);

        linearLayoutManager = new LinearLayoutManager(getContext());
        gridLayoutManager = new StaggeredGridLayoutManager(2, 1);

        initView();
        clickListning();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddActivity.class);
                startActivity(intent);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void isLayout() {
        if(layout.equals("linear")) {
            layoutManager = linearLayoutManager;
            recyclerView.setLayoutManager(layoutManager);
            pinrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }else{
            layoutManager = gridLayoutManager;
            recyclerView.setLayoutManager(layoutManager);
            pinrecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
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
        presenter.showPinnedRecycler(pinrecyclerView);
        presenter.swappable(recyclerView);
        presenter.swappablePin(pinrecyclerView);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void viewNoteRecyclerSuccess(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void viewNoteRecyclerUnsuccess(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void viewNoteRecyclerProgressShow(String msg) {

        progress = new ProgressDialog(getContext());
        progress.setMessage(msg);
        progress.show();
    }

    @Override
    public void viewNoteRecyclerProgressDismis() {
        progress.dismiss();
    }

    @Override
    public void viewSnacBar(String msg) {
        Snackbar snackbar = Snackbar
                .make(relativeLayout, msg, Snackbar.LENGTH_SHORT)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presenter.undoChange();
                        presenter.showRecycler(recyclerView);
                        presenter.showPinnedRecycler(pinrecyclerView);
                        Snackbar snackbar1 = Snackbar.make(relativeLayout,"Note Restored!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                });

        snackbar.show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void initView() {

        presenter = new NoteFragmentPresenter(getContext(),this);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerNote);
        recyclerView.setHasFixedSize(true);

        pinrecyclerView = (RecyclerView) v.findViewById(R.id.pinrecyclerNote);
        pinrecyclerView.setHasFixedSize(true);

        pinned = (TextView) v.findViewById(R.id.pinnedNotes);
        unpinned = (TextView) v.findViewById(R.id.unpinnedNotes);
    }

    @Override
    public void clickListning() {

    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void onItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.layoutManager) {

            if (!linearLayoutManager.equals(layoutManager)) {
                layoutManager = linearLayoutManager;
                item.setIcon(ic_view_quilt_black_24dp);
                reference.child("layout").setValue("linear");
                recyclerView.setLayoutManager(layoutManager);
                pinrecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            } else if (linearLayoutManager.equals(layoutManager)) {
                layoutManager = gridLayoutManager;
                item.setIcon(ic_view_list_black_24dp);
                reference.child("layout").setValue("grid");
                recyclerView.setLayoutManager(layoutManager);
                pinrecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
            }
        }
    }


    public static void searchItem(String newText) {

        presenter.searchItemData(recyclerView, newText);
        presenter.searchItemData(pinrecyclerView, newText);

    }

    public static void resetRecyclerView() {

        presenter.resetNoteRecycler(recyclerView);
        presenter.resetNotePinRecycler(pinrecyclerView);
    }

    public static void isChecked(int pinSize, int unPinSize){
        if(pinSize == 0 || unPinSize == 0){
            pinned.setVisibility(View.GONE);
            unpinned.setVisibility(View.GONE);
        }else{
            pinned.setVisibility(View.VISIBLE);
            unpinned.setVisibility(View.VISIBLE);
        }
    }

}