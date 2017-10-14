package com.bridgelabz.todo.reminder.presenter;

import android.support.v7.widget.RecyclerView;

public interface ReminderFragmentPresenterInterface {
    void showRecycler(RecyclerView recyclerView);

    void searchItemData(RecyclerView recyclerView, String newText);

    void resetNoteRecycler(RecyclerView recyclerView);

    void showRecyclerSuccess(String msg);
    void showRecyclerUnsucces(String msg);
    void showRecyclerProgress(String msg);
    void showRecyclerProgressDismiss();
}
