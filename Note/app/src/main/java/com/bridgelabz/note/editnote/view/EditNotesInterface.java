package com.bridgelabz.note.editnote.view;

interface EditNotesInterface {

    void editNoteSuccesful(String msg);
    void editNoteUnsuccesful(String msg);
    void showProgressBar(String msg);
    void dismissProgressBar();
}
