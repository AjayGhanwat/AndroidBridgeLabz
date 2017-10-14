package com.bridgelabz.todo.archive.interacter;

import android.support.v7.widget.RecyclerView;

public interface ArchiveFragmentInteracterInterface {
    void showRecyclerData(RecyclerView recyclerView);

    void swappableData(RecyclerView recyclerView);

    void undoChangeData();

    void showSearch(RecyclerView recyclerView, String newText);

    void refreshRecyclerData(RecyclerView recyclerView);
}
