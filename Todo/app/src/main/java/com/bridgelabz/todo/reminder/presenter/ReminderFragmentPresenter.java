package com.bridgelabz.todo.reminder.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.bridgelabz.todo.reminder.interacter.ReminderFragmentInteracter;
import com.bridgelabz.todo.reminder.interacter.ReminderFragmentInteracterInterface;
import com.bridgelabz.todo.reminder.view.ReminderFragment;

public class ReminderFragmentPresenter implements ReminderFragmentPresenterInterface {

    ReminderFragment fragment;
    ReminderFragmentInteracterInterface interacterInterface;

    public ReminderFragmentPresenter(Context context, ReminderFragment fragment) {
        this.fragment = fragment;
        interacterInterface = new ReminderFragmentInteracter(context, this);
    }

    @Override
    public void showRecycler(RecyclerView recyclerView) {
        interacterInterface.showRecyclerData(recyclerView);
    }

    @Override
    public void searchItemData(RecyclerView recyclerView, String newText) {
        interacterInterface.showSearchData(recyclerView, newText);
    }

    @Override
    public void resetNoteRecycler(RecyclerView recyclerView) {
        interacterInterface.resetRecycler(recyclerView);
    }

    @Override
    public void showRecyclerSuccess(String msg) {
        fragment.viewRemminderRecyclerSuccess(msg);
    }

    @Override
    public void showRecyclerUnsucces(String msg) {
        fragment.viewReminderRecyclerUnsuccess(msg);
    }

    @Override
    public void showRecyclerProgress(String msg) {
        fragment.viewReminderRecyclerProgressShow(msg);
    }

    @Override
    public void showRecyclerProgressDismiss() {
        fragment.viewReminderRecyclerProgressDismis();
    }
}
