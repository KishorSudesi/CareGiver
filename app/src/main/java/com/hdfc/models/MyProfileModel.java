package com.hdfc.models;

/**
 * Created by Admin on 05-03-2016.
 */
public class MyProfileModel {
    String email;
    String number;
    String strAddress;
    String strName;

    public MyProfileModel(String email, String number, String strAddress, String strName) {
        this.email = email;
        this.number = number;
        this.strAddress = strAddress;
        this.strName = strName;
    }

    public void setStrAddress(String strAddress) {
        this.strAddress = strAddress;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStrAddress() {
        return strAddress;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }
}
