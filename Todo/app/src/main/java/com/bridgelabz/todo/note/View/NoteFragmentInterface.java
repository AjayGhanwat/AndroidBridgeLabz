package com.bridgelabz.todo.note.View;

public interface NoteFragmentInterface {

    void viewNoteRecyclerSuccess(String msg);
    void viewNoteRecyclerUnsuccess(String msg);
    void viewNoteRecyclerProgressShow(String msg);
    void viewNoteRecyclerProgressDismis();
    void viewSnacBar(String msg);
}
