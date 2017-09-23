package com.bridgelabz.note.reminderfragment.interacter;

import android.support.v7.widget.RecyclerView;

public interface ReminderFragmentInteracterInterface {
    void showRecyclerData(RecyclerView recyclerView);

    void showSearchData(RecyclerView recyclerView, String newText);

    void resetRecycler(RecyclerView recyclerView);
}
