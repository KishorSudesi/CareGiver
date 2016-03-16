package com.hdfc.models;

/**
 * Created by Suhail on 2/19/2016.
 */
public class ServiceModel {

    private String strServiceName;
    private String strServiceFeatures;
    private double doubleCost;
    private double doubleUnit;

    public ServiceModel(String strServiceName, String strServiceFeatures, double doubleCost, double doubleUnit) {
        this.strServiceName = strServiceName;
        this.strServiceFeatures = strServiceFeatures;
        this.doubleCost = doubleCost;
        this.doubleUnit = doubleUnit;
    }

    public String getStrServiceName() {
        return strServiceName;
    }

    public String getStrServiceFeatures() {
        return strServiceFeatures;
    }

    public double getDoubleCost() {
        return doubleCost;
    }

    public double getDoubleUnit() {
        return doubleUnit;
    }
}
