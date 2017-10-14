package com.bridgelabz.todo.trash.interacter;

import android.support.v7.widget.RecyclerView;

public interface TrashFragmentInteracterInterface {
    void showRecyclerData(RecyclerView recyclerView);

    void swappableData(RecyclerView recyclerView);

    void showSearch(RecyclerView recyclerView, String newText);

    void refreshrecycler(RecyclerView recyclerView);
}
