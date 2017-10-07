package com.bridgelabz.todo.model;

public class UserData {

    private String First;
    private String Last;
    private String Phone;
    private String Pic;
    private String Layout;

    public UserData() {
    }

    public UserData(String first, String last, String phone, String pic,String layout) {
        First = first;
        Last = last;
        Phone = phone;
        Pic = pic;
        Layout = layout;
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

    public String getPic() {
        return Pic;
    }

    public void setPic(String pic) {
        Pic = pic;
    }

    public String getLayout() {
        return Layout;
    }

    public void setLayout(String layout) {
        Layout = layout;
    }
}
