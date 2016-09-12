package com.hdfc.models;

/**
 * Created by Admin on 07-07-2016.
 */
public class SubActivityModel {
    private String strSubActivityName = "";
    private String strStatus = "";
    private String strDueStatus = "";
    private String strDueDate = "";
    private String strUtilityName = "";
    private String strCheckboxStatus = "";

    public SubActivityModel(String strSubActivityName, String strStatus, String strDueStatus, String strDueDate, String strUtilityName, String strCheckboxStatus) {
        this.strSubActivityName = strSubActivityName;
        this.strStatus = strStatus;
        this.strDueStatus = strDueStatus;
        this.strDueDate = strDueDate;
        this.strUtilityName = strUtilityName;
        this.strCheckboxStatus = strCheckboxStatus;

    }

    public String getStrStatus() {
        return strStatus;
    }

    public void setStrStatus(String strStatus) {
        this.strStatus = strStatus;
    }

    public String getStrSubActivityName() {
        return strSubActivityName;
    }

    public void setStrSubActivityName(String strSubActivityName) {
        this.strSubActivityName = strSubActivityName;
    }

    public String getStrDueStatus() {
        return strDueStatus;
    }

    public void setStrDueStatus(String strDueStatus) {
        this.strDueStatus = strDueStatus;
    }

    public String getStrDueDate() {
        return strDueDate;
    }

    public void setStrDueDate(String strDueDate) {
        this.strDueDate = strDueDate;
    }

    public String getStrUtilityName() {
        return strUtilityName;
    }

    public void setStrUtilityName(String strUtilityName) {
        this.strUtilityName = strUtilityName;
    }

    public String getStrCheckboxStatus() {
        return strCheckboxStatus;
    }

    public void setStrCheckboxStatus(String strCheckboxStatus) {
        this.strUtilityName = strCheckboxStatus;
    }


}
