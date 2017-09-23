package com.bridgelabz.note.addnotes.view;

public interface AddNotesInterface {

    void addNoteSuccesful(String msg);
    void addNoteUnsuccesful(String msg);
    void showProgressBar(String msg);
    void dismissProgressBar();
}
