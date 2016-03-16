package com.hdfc.models;

import java.io.Serializable;

/**
 * Created by Admin on 24-02-2016.
 */
public class ClientModel implements Serializable{
    String name = "";
    String age = "";
    String problem = "";
    String premium = "";
    String address = "";
    String strMobile = "";
    String strClientImageUrl = "";


    public String getStrClientImageUrl() {
        return strClientImageUrl;
    }

    public void setStrClientImageUrl(String strClientImageUrl) {
        this.strClientImageUrl = strClientImageUrl;
    }

    public String getStrMobile() {
        return strMobile;
    }

    public void setStrMobile(String strMobile) {
        this.strMobile = strMobile;
    }

    public String getStrStatus() {
        return strStatus;
    }

    public void setStrStatus(String strStatus) {
        this.strStatus = strStatus;
    }

    private String strStatus = "";

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
