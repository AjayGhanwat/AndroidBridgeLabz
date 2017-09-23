package com.bridgelabz.note.notefragment.interacter;

import android.support.v7.widget.RecyclerView;

import com.bridgelabz.note.adapter.NoteDataAdapter;

public interface NoteFragmentInteracterInterface {
    void showRecyclerData(RecyclerView recyclerView);

    void swappableData(RecyclerView recyclerView);

    void undoChange();

    void showSearchData(RecyclerView recyclerView, String newText);

    void resetRecycler(RecyclerView recyclerView);
}
