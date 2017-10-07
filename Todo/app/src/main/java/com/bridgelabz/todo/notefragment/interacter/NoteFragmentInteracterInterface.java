package com.bridgelabz.todo.notefragment.interacter;

import android.support.v7.widget.RecyclerView;

import com.bridgelabz.todo.adapter.NoteDataAdapter;

public interface NoteFragmentInteracterInterface {
    void showRecyclerData(RecyclerView recyclerView);

    void showPinnedRecyclerData(RecyclerView recyclerView);

    void swappableData(RecyclerView recyclerView);

    void swappablePinData(RecyclerView recyclerView);

    void undoChange();

    void showSearchData(RecyclerView recyclerView, String newText);

    void resetRecycler(RecyclerView recyclerView);

    void resetPinRecycler(RecyclerView recyclerView);
}
