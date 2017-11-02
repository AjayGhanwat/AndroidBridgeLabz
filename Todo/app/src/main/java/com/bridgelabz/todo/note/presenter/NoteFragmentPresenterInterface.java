package com.bridgelabz.todo.note.presenter;

import android.support.v7.widget.RecyclerView;

public interface NoteFragmentPresenterInterface {

    void showRecycler(RecyclerView recyclerView);
    void showPinnedRecycler(RecyclerView recyclerView);
    void showRecyclerSuccess(String msg);
    void showRecyclerUnsucces(String msg);
    void showRecyclerProgress(String msg);
    void showRecyclerProgressDismiss();
    void showSnacBar(String msg);

    void swappable(RecyclerView recyclerView);

    void swappablePin(RecyclerView recyclerView);

    void undoChange();

    void searchItemData(RecyclerView recyclerView, String newText);

    void resetNoteRecycler(RecyclerView recyclerView);

    void resetNotePinRecycler(RecyclerView recyclerView);

    void changeLocationNote(int startLoc, int endLoc);
}
