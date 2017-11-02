package com.bridgelabz.todo.note.interacter;

import android.support.v7.widget.RecyclerView;

public interface NoteFragmentInteracterInterface {
    void showRecyclerData(RecyclerView recyclerView);

    void showPinnedRecyclerData(RecyclerView recyclerView);

    void swappableData(RecyclerView recyclerView);

    void swappablePinData(RecyclerView recyclerView);

    void undoChange();

    void showSearchData(RecyclerView recyclerView, String newText);

    void resetRecycler(RecyclerView recyclerView);

    void resetPinRecycler(RecyclerView recyclerView);

    void changeLocationNote(int startLoc, int endLoc);
}
