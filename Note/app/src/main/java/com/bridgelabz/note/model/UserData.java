package com.bridgelabz.note.model;

import java.util.ArrayList;

public class UserData extends ArrayList<UserData> {

    private String First;
    private String Last;
    private String Phone;

    public UserData() {
    }

    public UserData(String first, String last, String phone) {
        First = first;
        Last = last;
        Phone = phone;
    }

    public String getFirst() {
        return First;
    }

    public void setFirst(String first) {
        First = first;
    }

    public String getLast() {
        return Last;
    }

    public void setLast(String last) {
        Last = last;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
