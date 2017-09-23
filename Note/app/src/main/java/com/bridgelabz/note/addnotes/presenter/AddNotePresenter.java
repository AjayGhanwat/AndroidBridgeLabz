package com.bridgelabz.note.addnotes.presenter;

import android.content.Context;

import com.bridgelabz.note.addnotes.interacter.AddNotesInteracter;
import com.bridgelabz.note.addnotes.interacter.AddNotesInteracterInterface;
import com.bridgelabz.note.addnotes.view.AddActivity;

public class AddNotePresenter implements AddNotePresenterInterface{

    AddActivity activity;
    AddNotesInteracterInterface interacter;

    public AddNotePresenter(Context context, AddActivity activity){
        this.activity = activity;
        interacter = new AddNotesInteracter(context, this);
    }

    public void addnote(String title, String decs, int userColor) {
        interacter.addNoteFirebaase(title,decs, userColor);
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
