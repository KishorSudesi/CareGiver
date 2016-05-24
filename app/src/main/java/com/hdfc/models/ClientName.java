package com.hdfc.models;

import java.util.ArrayList;

/**
 * Created by Suhail on 5/24/2016.
 */

public class ClientName {

    private String strCustomerName;

    private ArrayList<String> strDependeneNames = new ArrayList<>();

    public String getStrCustomerName() {
        return strCustomerName;
    }

    public void setStrCustomerName(String strCustomerName) {
        this.strCustomerName = strCustomerName;
    }

    public ArrayList<String> getStrDependeneNames() {
        return strDependeneNames;
    }

    public void setStrDependeneNames(ArrayList<String> strDependeneNames) {
        this.strDependeneNames = strDependeneNames;
    }

    public void setStrDependeneName(String strDependeneName) {
        this.strDependeneNames.add(strDependeneName);
    }
}
