package com.bridgelabz.todo.addnotes.presenter;

public interface AddNotePresenterInterface {

    void addNoteSuccess(String msg);
    void addNoteUnsuccess(String msg);
    void showProgresss(String msg);
    void dismissProgress();
}
