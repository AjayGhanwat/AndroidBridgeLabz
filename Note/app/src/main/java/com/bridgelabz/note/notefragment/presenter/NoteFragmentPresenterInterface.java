package com.bridgelabz.note.notefragment.presenter;

import android.support.v7.widget.RecyclerView;

import com.bridgelabz.note.adapter.NoteDataAdapter;

public interface NoteFragmentPresenterInterface {

    void showRecycler(RecyclerView recyclerView);
    void showRecyclerSuccess(String msg);
    void showRecyclerUnsucces(String msg);
    void showRecyclerProgress(String msg);
    void showRecyclerProgressDismiss();
    void showSnacBar(String msg);

    void swappable(RecyclerView recyclerView);

    void undoChange();

    void searchItemData(RecyclerView recyclerView, String newText);

    void resetNoteRecycler(RecyclerView recyclerView);
}
