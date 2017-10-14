package com.bridgelabz.todo.archive.view;

public interface ArchiveFragmentInterface {

    void showRecyclerSuccess(String msg);
    void showRecyclerUnsuccess(String msg);
    void showRecyclerProgress(String msg);
    void dismissRecyclerProgress();

    void showSnacBar(String msg);
}
