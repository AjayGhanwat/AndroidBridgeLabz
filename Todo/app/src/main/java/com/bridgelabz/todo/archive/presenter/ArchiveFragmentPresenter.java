package com.bridgelabz.todo.archive.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.bridgelabz.todo.archive.interacter.ArchiveFragmentInteracter;
import com.bridgelabz.todo.archive.interacter.ArchiveFragmentInteracterInterface;
import com.bridgelabz.todo.archive.view.ArchiveFragment;

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
    public void showSearch(RecyclerView recyclerView, String newText) {
        interacterInterface.showSearch(recyclerView, newText);
    }

    @Override
    public void refressRecycler(RecyclerView recyclerView) {
        interacterInterface.refreshRecyclerData(recyclerView);
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
