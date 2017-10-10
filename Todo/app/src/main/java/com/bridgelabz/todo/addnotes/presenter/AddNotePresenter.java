package com.bridgelabz.todo.addnotes.presenter;

import android.content.Context;

import com.bridgelabz.todo.addnotes.interacter.AddNotesInteracter;
import com.bridgelabz.todo.addnotes.interacter.AddNotesInteracterInterface;
import com.bridgelabz.todo.addnotes.view.AddActivity;

public class AddNotePresenter implements AddNotePresenterInterface{

    AddActivity activity;
    AddNotesInteracterInterface interacter;

    public AddNotePresenter(Context context, AddActivity activity){
        this.activity = activity;
        interacter = new AddNotesInteracter(context, this);
    }

    public void addnoteReminder(String title, String decs, int userColor,boolean reminder, String reminderDate, String reminderTime, boolean isPinned, String Key, String userDate) {
        interacter.addNoteReminderFirebaase(title,decs, userColor, reminder , reminderDate, reminderTime,isPinned, Key, userDate);
    }

    @Override
    public void addNoteSuccess(String msg) {
        activity.addNoteSuccesful(msg);
    }

    @Override
    public void addNoteUnsuccess(String msg) {
        activity.addNoteUnsuccesful(msg);
    }

    @Override
    public void showProgresss(String msg) {
        activity.showProgressBar(msg);
    }

    @Override
    public void dismissProgress() {
        activity.dismissProgressBar();
    }
}
