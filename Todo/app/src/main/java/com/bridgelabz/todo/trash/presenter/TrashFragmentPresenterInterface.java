package com.bridgelabz.todo.trash.presenter;

import android.support.v7.widget.RecyclerView;

public interface TrashFragmentPresenterInterface {
    void showRecycler(RecyclerView recyclerView);
    void showRecyclerSuccess(String msg);
    void showRecyclerUnsuccess(String msg);
    void showRecyclerPregress(String msg);
    void dismissRecyclerProgress();

    void showSnacBar(String msg);
    void swappable(RecyclerView recyclerView);

    void showSearchData(RecyclerView recyclerView, String newText);

    void refreshRecycler(RecyclerView recyclerView);
}
