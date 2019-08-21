package com.cenah.buymeserver.models;

public class User {

    private String Name;
    private String Password;
    private String Phone;
    private String IsStaff;


    public User(String name, String password) {
        Name = name;
        Password = password;
    }

    public User(String name, String password, String phone, String isStaff) {
        Name = name;
        Password = password;
        Phone = phone;
        IsStaff = isStaff;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public User() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }
}
