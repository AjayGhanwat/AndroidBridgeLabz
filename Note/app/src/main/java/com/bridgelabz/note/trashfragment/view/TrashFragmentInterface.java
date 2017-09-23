package com.bridgelabz.note.trashfragment.view;

public interface TrashFragmentInterface {

    void showRecyclerSuccess(String msg);
    void showRecyclerUnsuccess(String msg);
    void showRecyclerPregress(String msg);
    void dismissRecyclerProgress();
    void showSnacBar(String msg);
}
