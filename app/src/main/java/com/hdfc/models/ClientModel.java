package com.hdfc.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by balamurugan@adstringo.in on 2/19/2016.
 */
public class ClientModel implements Serializable {

    private CustomerModel customerModel;

    private ArrayList<DependentModel> dependentModels = new ArrayList<>();

    public ClientModel() {
    }

    public CustomerModel getCustomerModel() {
        return customerModel;
    }

    public void setCustomerModel(CustomerModel customerModel) {
        this.customerModel = customerModel;
    }

    public ArrayList<DependentModel> getDependentModels() {
        return dependentModels;
    }

    public void setDependentModels(ArrayList<DependentModel> dependentModels) {
        this.dependentModels = dependentModels;
    }

    public void setDependentModel(DependentModel dependentModel) {
        this.dependentModels.add(dependentModel);
    }
}
