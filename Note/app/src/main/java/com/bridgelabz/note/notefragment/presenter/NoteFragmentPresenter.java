package com.bridgelabz.note.notefragment.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.bridgelabz.note.notefragment.interacter.NoteFragmentInteracter;
import com.bridgelabz.note.notefragment.interacter.NoteFragmentInteracterInterface;
import com.bridgelabz.note.notefragment.View.NoteFragment;

public class NoteFragmentPresenter implements NoteFragmentPresenterInterface{

    NoteFragment fragment;
    NoteFragmentInteracterInterface interacterInterface;

    public NoteFragmentPresenter (Context context, NoteFragment fragment){
        this.fragment = fragment;
        interacterInterface = new NoteFragmentInteracter(context, this);
    }

    @Override
    public void showRecycler(RecyclerView recyclerView) {
        interacterInterface.showRecyclerData(recyclerView);
    }

    @Override
    public void showPinnedRecycler(RecyclerView recyclerView) {
        interacterInterface.showPinnedRecyclerData(recyclerView);
    }

    @Override
    public void undoChange() {
        interacterInterface.undoChange();
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
    public void resetNotePinRecycler(RecyclerView recyclerView) {
        interacterInterface.resetPinRecycler(recyclerView);
    }

    @Override
    public void showRecyclerSuccess(String msg) {
        fragment.viewNoteRecyclerSuccess(msg);
    }

    @Override
    public void showRecyclerUnsucces(String msg) {
        fragment.viewNoteRecyclerUnsuccess(msg);
    }

    @Override
    public void showRecyclerProgress(String msg) {
        fragment.viewNoteRecyclerProgressShow(msg);
    }

    @Override
    public void showRecyclerProgressDismiss() {
        fragment.viewNoteRecyclerProgressDismis();
    }

    @Override
    public void showSnacBar(String msg) {
        fragment.viewSnacBar(msg);
    }

    @Override
    public void swappable(RecyclerView recyclerView) {
        interacterInterface.swappableData(recyclerView);
    }

    @Override
    public void swappablePin(RecyclerView recyclerView) {
        interacterInterface.swappablePinData(recyclerView);
    }
}
