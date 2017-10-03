package com.bridgelabz.note.archivefragment.view;

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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bridgelabz.note.R;
import com.bridgelabz.note.archivefragment.presenter.ArchiveFragmentPresenter;
import com.bridgelabz.note.archivefragment.presenter.ArchiveFragmentPresenterInterface;
import com.bridgelabz.note.base.BaseFragment;
import com.bridgelabz.note.model.UserData;
import com.bridgelabz.note.notefragment.presenter.NoteFragmentPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.os.Build.VERSION_CODES.N;
import static com.bridgelabz.note.R.drawable.ic_view_list_black_24dp;
import static com.bridgelabz.note.R.drawable.ic_view_quilt_black_24dp;

public class ArchiveFragment extends BaseFragment implements ArchiveFragmentInterface{

    static RecyclerView recyclerView;
    View v;

    ProgressDialog progress;

    static DatabaseReference reference;

    static LinearLayoutManager linearLayoutManager;
    static StaggeredGridLayoutManager gridLayoutManager;
    static RecyclerView.LayoutManager layoutManager;

    RelativeLayout relativeLayout;

    static ArchiveFragmentPresenterInterface presenter;

    String layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_archive, container, false);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.v = view;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // for(DataSnapshot post : dataSnapshot.getChildren()){

                UserData user = dataSnapshot.getValue(UserData.class);

                layout = user.getLayout();

                isLayout();

                //  }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayoutArchive);

        linearLayoutManager = new LinearLayoutManager(getContext());
        gridLayoutManager = new StaggeredGridLayoutManager(2, 1);

    //    layoutManager = linearLayoutManager;

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
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void initView() {

        presenter = new ArchiveFragmentPresenter(getContext(),this);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerArchive);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public void clickListning() {

    }

    public static void onItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.layoutManager) {

            if (!linearLayoutManager.equals(layoutManager)) {
                layoutManager = linearLayoutManager;
                reference.child("layout").setValue("linear");
                item.setIcon(ic_view_quilt_black_24dp);
                recyclerView.setLayoutManager(layoutManager);
            } else if (linearLayoutManager.equals(layoutManager)) {
                layoutManager = gridLayoutManager;
                reference.child("layout").setValue("grid");
                item.setIcon(ic_view_list_black_24dp);
                recyclerView.setLayoutManager(layoutManager);
            }
        }
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
    public void showRecyclerProgress(String msg) {
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
        Snackbar snackbar = Snackbar
                .make(relativeLayout, msg, Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presenter.undoChange();
                        presenter.showRecycler(recyclerView);
                        Snackbar snackbar1 = Snackbar.make(relativeLayout,"Note Archived Again!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                });

        snackbar.show();
    }

    public static void searchItem(String newText) {
        presenter.showSearch(recyclerView, newText);
    }

    public static void resetRecyclerView() {
        presenter.refressRecycler(recyclerView);
    }
}
