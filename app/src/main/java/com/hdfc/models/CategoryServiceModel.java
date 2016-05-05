package com.hdfc.models;

import java.util.ArrayList;

/**
 * Created by balamurugan@adstringo.in on 2/19/2016.
 */
public class CategoryServiceModel {

    private String strCategoryName;

    private ArrayList<ServiceModel> serviceModels = new ArrayList<>();

    public CategoryServiceModel() {
    }

    public String getStrCategoryName() {
        return strCategoryName;
    }

    public void setStrCategoryName(String strCategoryName) {
        this.strCategoryName = strCategoryName;
    }

    public ArrayList<ServiceModel> getServiceModels() {
        return serviceModels;
    }

    public void setServiceModels(ArrayList<ServiceModel> serviceModels) {
        this.serviceModels = serviceModels;
    }

    public void setServiceModels(ServiceModel serviceModel) {
        this.serviceModels.add(serviceModel);
    }
}
