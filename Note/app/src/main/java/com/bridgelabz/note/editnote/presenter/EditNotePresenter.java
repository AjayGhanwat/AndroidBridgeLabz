package com.bridgelabz.note.editnote.presenter;

import android.content.Context;

import com.bridgelabz.note.editnote.interacter.EditNoteInteracter;
import com.bridgelabz.note.editnote.interacter.EditNoteInteracterInterface;
import com.bridgelabz.note.editnote.view.EditNote;

public class EditNotePresenter implements EditNotePresenterInterface {

    EditNote activity;
    EditNoteInteracterInterface interacter;

    public EditNotePresenter(Context context, EditNote activity) {
        this.activity = activity;
        interacter = new EditNoteInteracter(context, this);

    }

    public void editnote(String title, String decs, String user_date, int user_color, String user_key, boolean user_reminder ,String user_reminder_date, String user_reminder_time, boolean isPinned) {
        interacter.editNotes(title, decs, user_date, user_color, user_key,user_reminder,user_reminder_date, user_reminder_time,isPinned);
    }

    @Override
    public void editNoteSuccess(String msg) {
        activity.editNoteSuccesful(msg);
    }

    @Override
    public void editNoteUnsuccess(String msg) {
        activity.editNoteUnsuccesful(msg);
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
