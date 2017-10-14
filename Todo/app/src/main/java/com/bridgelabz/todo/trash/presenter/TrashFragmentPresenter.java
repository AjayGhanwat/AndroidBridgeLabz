package com.bridgelabz.todo.trash.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.bridgelabz.todo.trash.interacter.TrashFragmentInteracter;
import com.bridgelabz.todo.trash.interacter.TrashFragmentInteracterInterface;
import com.bridgelabz.todo.trash.view.TrashFragment;

public class TrashFragmentPresenter implements TrashFragmentPresenterInterface{

    TrashFragment fragment;
    TrashFragmentInteracterInterface interacterInterface;

    public TrashFragmentPresenter(Context context, TrashFragment fragment){

        this.fragment = fragment;
        interacterInterface = new TrashFragmentInteracter(context, this);

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
    public void showSearchData(RecyclerView recyclerView, String newText) {
        interacterInterface.showSearch(recyclerView, newText);
    }

    @Override
    public void refreshRecycler(RecyclerView recyclerView) {
        interacterInterface.refreshrecycler(recyclerView);
    }

    @Override
    public void showRecyclerSuccess(String msg) {
        fragment.showRecyclerSuccess(msg);
    }

    @Override
    public void showRecyclerUnsuccess(String msg) {
        fragment.showRecyclerUnsuccess(msg);
    }

    @Override
    public void showRecyclerPregress(String msg) {
        fragment.showRecyclerPregress(msg);
    }

    @Override
    public void dismissRecyclerProgress() {
        fragment.dismissRecyclerProgress();
    }

    @Override
    public void showSnacBar(String msg) {
        fragment.showSnacBar(msg);
    }
}
