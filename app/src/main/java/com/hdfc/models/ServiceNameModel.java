package com.hdfc.models;

import java.util.ArrayList;

/**
 * Created by Aniket on 7/4/2016.
 */
public class ServiceNameModel {

    private String strServiceName;

    private ArrayList<String> strCategoryNames = new ArrayList<>();

    public String getStrServiceName() {
        return strServiceName;
    }

    public void setStrServiceName(String strServiceName) {
        this.strServiceName = strServiceName;
    }

    public ArrayList<String> getStrCategoryNames() {
        return strCategoryNames;
    }

    public void setStrCategoryNames(ArrayList<String> strCategoryNames) {
        this.strCategoryNames = strCategoryNames;
    }


}
