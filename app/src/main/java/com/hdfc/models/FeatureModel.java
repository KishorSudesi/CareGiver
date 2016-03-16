package com.hdfc.models;

import android.widget.CheckBox;

/**
 * Created by Admin on 3/4/2016.
 */
public class FeatureModel {
    CheckBox vegetable;
    String dependentName;

    public String getDependentName() {
        return dependentName;
    }

    public void setDependentName(String dependentName) {
        this.dependentName = dependentName;
    }

    public CheckBox getVegetable() {
        return vegetable;
    }

    public void setVegetable(CheckBox vegetable) {
        this.vegetable = vegetable;
    }
}
