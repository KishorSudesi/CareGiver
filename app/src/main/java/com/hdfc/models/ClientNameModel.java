package com.hdfc.models;

import java.util.ArrayList;

/**
 * Created by Suhail on 5/24/2016.
 */

public class ClientNameModel {

    private String strCustomerName;

    private ArrayList<String> strDependentNames = new ArrayList<>();

    public String getStrCustomerName() {
        return strCustomerName;
    }

    public void setStrCustomerName(String strCustomerName) {
        this.strCustomerName = strCustomerName;
    }

    public ArrayList<String> getStrDependentNames() {
        return strDependentNames;
    }

    public void setStrDependentName(String strDependentName) {
        this.strDependentNames.add(strDependentName);
    }

    public void removeStrDependentName(String strDependentName) {
        this.strDependentNames.remove(strDependentName);
    }


}
