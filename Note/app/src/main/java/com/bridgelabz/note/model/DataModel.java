package com.bridgelabz.note.model;

public class DataModel {

    private String Title;
    private String Desc;
    private String Key;
    private int Color;
    private String Date;
    private boolean Archive;
    private boolean Trash;
    private boolean Reminder;
    private String reminderdate;
    private String remindertime;


    public DataModel() {
    }

    public DataModel(String title, String desc, String key, int color, String date, boolean archive,boolean trash, boolean reminder, String reminderDate, String reminderTime) {
        Title = title;
        Desc = desc;
        Key = key;
        Color = color;
        Date = date;
        Archive = archive;
        Trash = trash;
        Reminder = reminder;
        reminderdate = reminderDate;
        remindertime = reminderTime;

    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public boolean getArchive() {
        return Archive;
    }

    public void setArchive(boolean archive) {
        Archive = archive;
    }

    public boolean getTrash() {
        return Trash;
    }

    public void setTrash(boolean trash) {
        Trash = trash;
    }

    public boolean getReminder() {
        return Reminder;
    }

    public void setReminder(boolean reminder) {
        Reminder = reminder;
    }

    public String getReminderDate() {
        return reminderdate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderdate = reminderDate;
    }

    public String getReminderTime() {
        return remindertime;
    }

    public void setReminderTime(String reminderTime) {
        this.remindertime = reminderTime;
    }
}
