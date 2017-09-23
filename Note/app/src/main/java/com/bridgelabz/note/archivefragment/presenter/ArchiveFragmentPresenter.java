package com.bridgelabz.note.archivefragment.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.bridgelabz.note.archivefragment.interacter.ArchiveFragmentInteracter;
import com.bridgelabz.note.archivefragment.interacter.ArchiveFragmentInteracterInterface;
import com.bridgelabz.note.archivefragment.view.ArchiveFragment;

public class ArchiveFragmentPresenter implements ArchiveFragmentPresenterInterface{

    ArchiveFragment archiveFragment;
    ArchiveFragmentInteracterInterface interacterInterface;

    public ArchiveFragmentPresenter(Context context, ArchiveFragment archiveFragment){
        this.archiveFragment = archiveFragment;
        interacterInterface = new ArchiveFragmentInteracter(context, this);
    }

    @Override
    public void showRecycler(RecyclerView recyclerView) {
        interacterInterface.showRecyclerData(recyclerView);
    }

    @Override
    public void swappable(RecyclerView recyclerView) {
        interacterInterface.swappableData(recyclerView);
    }

    @Override
    public void undoChange() {
        interacterInterface.undoChangeData();
    }

    @Override
    public void showRecyclerSuccess(String msg) {
        archiveFragment.showRecyclerSuccess(msg);
    }

    @Override
    public void showRecyclerUnsucces(String msg) {
        archiveFragment.showRecyclerUnsuccess(msg);
    }

    @Override
    public void showRecyclerProgress(String msg) {
        archiveFragment.showRecyclerProgress(msg);
    }

    @Override
    public void dismissRecyclerProgress() {
        archiveFragment.dismissRecyclerProgress();
    }

    @Override
    public void showSnacBar(String msg) {
        archiveFragment.showSnacBar(msg);
    }

}
