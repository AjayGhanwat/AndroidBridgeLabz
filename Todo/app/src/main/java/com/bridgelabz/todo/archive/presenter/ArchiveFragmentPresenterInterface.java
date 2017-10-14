package com.bridgelabz.todo.archive.presenter;

import android.support.v7.widget.RecyclerView;

public interface ArchiveFragmentPresenterInterface {
    void showRecycler(RecyclerView recyclerView);
    void showRecyclerSuccess(String msg);
    void showRecyclerUnsucces(String msg);
    void showRecyclerProgress(String msg);
    void dismissRecyclerProgress();
    void showSnacBar(String msg);

    void swappable(RecyclerView recyclerView);

    void undoChange();

    void showSearch(RecyclerView recyclerView, String newText);

    void refressRecycler(RecyclerView recyclerView);
}
