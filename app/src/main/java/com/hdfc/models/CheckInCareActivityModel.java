package com.hdfc.models;

import java.util.ArrayList;

/**
 * Created by Admin on 07-07-2016.
 */
public class CheckInCareActivityModel {

    private String strActivityName = "";

    private ArrayList<SubActivityModel> subActivityModels = new ArrayList<>();

    public CheckInCareActivityModel(String strActivityName, ArrayList<SubActivityModel> subActivityModels) {
        this.strActivityName = strActivityName;
        this.subActivityModels = subActivityModels;

    }

    public String getStrActivityName() {
        return strActivityName;
    }

    public void setStrActivityName(String strActivityName) {
        this.strActivityName = strActivityName;
    }

    public ArrayList<SubActivityModel> getSubActivityModels() {
        return subActivityModels;
    }

    public void setSubActivityModels(ArrayList<SubActivityModel> subActivityModels) {
        this.subActivityModels = subActivityModels;
    }

    public void clearSubActivityModels() {
        this.subActivityModels.clear();
    }

}
