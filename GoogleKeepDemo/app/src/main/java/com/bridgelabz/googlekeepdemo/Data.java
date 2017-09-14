package com.bridgelabz.googlekeepdemo;

public class Data {

    private String title;
    private String note;
    private String id;

    public Data() {
    }

    public Data(String title, String note, String id) {
        this.title = title;
        this.note = note;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
