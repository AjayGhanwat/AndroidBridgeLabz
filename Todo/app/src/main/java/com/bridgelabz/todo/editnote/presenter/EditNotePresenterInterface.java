package com.bridgelabz.todo.editnote.presenter;

public interface EditNotePresenterInterface {

    void editNoteSuccess(String msg);
    void editNoteUnsuccess(String msg);
    void showProgresss(String msg);
    void dismissProgress();
}
