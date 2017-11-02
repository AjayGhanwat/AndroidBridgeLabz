package com.bridgelabz.todo.note.View;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgelabz.todo.R;
import com.bridgelabz.todo.adapter.NoteDataAdapter;
import com.bridgelabz.todo.addnotes.view.AddActivity;
import com.bridgelabz.todo.base.BaseFragment;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.model.UserData;
import com.bridgelabz.todo.note.interacter.NoteFragmentInteracter;
import com.bridgelabz.todo.note.presenter.NoteFragmentPresenter;
import com.bridgelabz.todo.note.presenter.NoteFragmentPresenterInterface;
import com.bridgelabz.todo.sqlitedatabase.SQLiteDatabaseHandler;
import com.bridgelabz.todo.util.NetworkChangeReceiver;
import com.bridgelabz.todo.util.NetworkConnection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.bridgelabz.todo.R.drawable.ic_view_list_black_24dp;
import static com.bridgelabz.todo.R.drawable.ic_view_quilt_black_24dp;
import static com.bridgelabz.todo.constant.Constant.user_layouts;
import static com.bridgelabz.todo.constant.Constant.user_layouts_grid;
import static com.bridgelabz.todo.constant.Constant.user_layouts_linear;
import static com.bridgelabz.todo.constant.Constant.user_note_changes_undo;
import static com.bridgelabz.todo.constant.Constant.user_note_restored;
import static com.bridgelabz.todo.constant.Constant.user_users_FirebaseFirestore;
import static com.bridgelabz.todo.constant.Constant.user_users_info_FirebaseFirestore;
import static com.facebook.FacebookSdk.getApplicationContext;

public class NoteFragment extends BaseFragment implements NoteFragmentInterface {

    public static RecyclerView recyclerView;
    public static ArrayList<DataModel> unpinDataRe;
    static BroadcastReceiver receiver  = new NetworkChangeReceiver();
    static TextView pinned;
    static TextView unpinned;
    static RecyclerView pinrecyclerView;
    static CollectionReference collectionReference;
    static LinearLayoutManager linearLayoutManager;
    static StaggeredGridLayoutManager gridLayoutManager;
    static RecyclerView.LayoutManager layoutManager;
    static String userId;
    static NoteFragmentPresenterInterface presenter;
    static ArrayList<DataModel> pinDataRe;
    static NoteDataAdapter pinDataAda, unpinDataAda;
    static int startLoc = -1;
    static int startChangeLoc = -1;
    static int endLoc = -1;
    static View v;
    Bundle bundle;
    ProgressDialog progress;
    RelativeLayout relativeLayout;
    String layout;

    /*
     *  As per the change in layout of recycler it will stored in the database.
     */

    public static void onItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.layoutManager) {

            if (!linearLayoutManager.equals(layoutManager)) {
                layoutManager = linearLayoutManager;
                item.setIcon(ic_view_quilt_black_24dp);
                Map<String, Object> changeLayout = new HashMap<>();
                changeLayout.put(user_layouts, user_layouts_linear);
                collectionReference.document(userId).set(changeLayout);
                recyclerView.setLayoutManager(layoutManager);
                pinrecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            } else if (linearLayoutManager.equals(layoutManager)) {
                layoutManager = gridLayoutManager;
                item.setIcon(ic_view_list_black_24dp);
                Map<String, Object> changeLayout = new HashMap<>();
                changeLayout.put(user_layouts, user_layouts_grid);
                collectionReference.document(userId).set(changeLayout);
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

    public static void updateNewData(final CollectionReference myDoc) {

        pinDataRe = new ArrayList<>();
        unpinDataRe = new ArrayList<>();

        getApplicationContext().registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if (NetworkConnection.isNetworkConnected(getApplicationContext())) {

            if (NetworkConnection.isInternetAvailable()) {

                pinDataRe.clear();
                unpinDataRe.clear();
                NoteFragmentInteracter.changeData.clear();

                myDoc.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(getApplicationContext());

                        sqLiteDatabaseHandler.deleteAll();

                        if (documentSnapshots != null) {

                            for (DocumentSnapshot documents : documentSnapshots.getDocuments()) {

                                DataModel userData = documents.toObject(DataModel.class);

                                sqLiteDatabaseHandler.insertRecord(userData);

                            }
                        }

                        pinDataRe.clear();
                        unpinDataRe.clear();

                        ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();

                        for (int i = 0; i < dataModels.size(); i++ )
                        {

                            if (dataModels.get(i).getPin()) {

                                pinDataRe.add(dataModels.get(i));
                                pinDataAda = new NoteDataAdapter(pinDataRe);
                                pinrecyclerView.setAdapter(pinDataAda);
                                pinDataAda.notifyDataSetChanged();

                            } else if (!dataModels.get(i).getPin() && !dataModels.get(i).getArchive() && !dataModels.get(i).getTrash()) {

                                unpinDataRe.add(dataModels.get(i));
                                unpinDataAda = new NoteDataAdapter(unpinDataRe);
                                recyclerView.setAdapter(unpinDataAda);
                                unpinDataAda.notifyDataSetChanged();
                                NoteFragmentInteracter.changeData.add(dataModels.get(i));
                            }
                        }
                        recyclerView.invalidate();
                    }
                });
            }

        } else {

            pinDataRe.clear();
            unpinDataRe.clear();
            NoteFragmentInteracter.changeData.clear();

            myDoc.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    SQLiteDatabaseHandler sqLiteDatabaseHandler = new SQLiteDatabaseHandler(getApplicationContext());
                    ArrayList<DataModel> dataModels = sqLiteDatabaseHandler.getAllRecord();

                    for (int i = 0; i < dataModels.size(); i++) {

                        if (dataModels.get(i).getPin()) {

                            pinDataRe.add(dataModels.get(i));
                            pinDataAda = new NoteDataAdapter(pinDataRe);
                            pinrecyclerView.setAdapter(pinDataAda);
                            pinDataAda.notifyDataSetChanged();

                        } else if (!dataModels.get(i).getPin() && !dataModels.get(i).getArchive() && !dataModels.get(i).getTrash()) {

                            unpinDataRe.add(dataModels.get(i));
                            unpinDataAda = new NoteDataAdapter(unpinDataRe);
                            recyclerView.setAdapter(unpinDataAda);
                            unpinDataAda.notifyDataSetChanged();
                            NoteFragmentInteracter.changeData.add(dataModels.get(i));
                        }
                    }
                    recyclerView.invalidate();
                }

            });
        }
    }

    /*
     *  As soon as the fragment open the database will chanck for the new data and
     *  the fragment is opened the databas echanges it will reflected on the activity
     */

    public static void changeLocationNote() {
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                if (startLoc == -1)
                    startLoc = viewHolder.getAdapterPosition();
                startChangeLoc = viewHolder.getAdapterPosition();
                endLoc = target.getAdapterPosition();

                NoteFragmentInteracter.dataAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                unpinDataAda.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                if (startLoc == -1)
                    startLoc = viewHolder.getAdapterPosition();

                if (startLoc > -1) {
                    presenter.changeLocationNote(startLoc, endLoc);
                    /*NoteFragmentInteracter.dataAdapter.notifyItemMoved(startLoc, endLoc);
                    unpinDataAda.notifyDataSetChanged();*/
                }

                startLoc = -1;
                endLoc = -1;
            }
        });
        touchHelper.attachToRecyclerView(recyclerView);
    }

    /*
     *   in the change location the it will change the location of different
      *  card view in recycler view location.
     */

    public static void isChecked(int pinSize, int unPinSize) {
        if (pinSize == 0 || unPinSize == 0) {
            pinned.setVisibility(View.GONE);
            unpinned.setVisibility(View.GONE);
        } else {
            pinned.setVisibility(View.VISIBLE);
            unpinned.setVisibility(View.VISIBLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_note, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.v = view;
        this.bundle = savedInstanceState;

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        collectionReference = FirebaseFirestore.getInstance().collection(user_users_FirebaseFirestore).document(userId).collection(user_users_info_FirebaseFirestore);

        relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayoutManager);

        linearLayoutManager = new LinearLayoutManager(getActivity());
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

        changeLocationNote();
    }

    private void isLayout() {
        if (layout.equals(user_layouts_linear)) {
            layoutManager = linearLayoutManager;
            recyclerView.setLayoutManager(layoutManager);
            pinrecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            layoutManager = gridLayoutManager;
            recyclerView.setLayoutManager(layoutManager);
            pinrecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.showRecycler(recyclerView);
        presenter.showPinnedRecycler(pinrecyclerView);
        presenter.swappable(recyclerView);
        presenter.swappablePin(pinrecyclerView);

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

    @Override
    public void viewNoteRecyclerSuccess(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void viewNoteRecyclerUnsuccess(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void viewNoteRecyclerProgressShow(String msg) {

        progress = new ProgressDialog(getActivity());
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
                .setAction(user_note_changes_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presenter.undoChange();
                        presenter.showRecycler(recyclerView);
                        presenter.showPinnedRecycler(pinrecyclerView);
                        Snackbar snackbar1 = Snackbar.make(relativeLayout, user_note_restored, Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                });

        snackbar.show();
    }

    @Override
    public void initView() {

        presenter = new NoteFragmentPresenter(getActivity(), this);

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

}