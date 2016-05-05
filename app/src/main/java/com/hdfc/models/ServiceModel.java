package com.hdfc.models;

import java.util.ArrayList;

/**
 * Created by balamurugan@adstringo.in on 2/19/2016.
 */
public class ServiceModel {

    private String strServiceName;
    private String strUpdatedDate;
    private String strCreatedDate;
    private String strCategoryName;
    private String strServiceType;

    private double doubleCost;

    private int iUnit;
    private int iUnitUsed;
    private int iServiceNo;

    private String strServiceId;
    private String strServiceHistoryId;
    private String strCustomerId;

    private ArrayList<MilestoneModel> milestoneModels = new ArrayList<>();

    public ServiceModel() {
    }

    public String getStrServiceType() {
        return strServiceType;
    }

    public void setStrServiceType(String strServiceType) {
        this.strServiceType = strServiceType;
    }

    public String getStrServiceName() {
        return strServiceName;
    }

    public void setStrServiceName(String strServiceName) {
        this.strServiceName = strServiceName;
    }

    public String getStrUpdatedDate() {
        return strUpdatedDate;
    }

    public void setStrUpdatedDate(String strUpdatedDate) {
        this.strUpdatedDate = strUpdatedDate;
    }

    public String getStrCreatedDate() {
        return strCreatedDate;
    }

    public void setStrCreatedDate(String strCreatedDate) {
        this.strCreatedDate = strCreatedDate;
    }

    public String getStrCategoryName() {
        return strCategoryName;
    }

    public void setStrCategoryName(String strCategoryName) {
        this.strCategoryName = strCategoryName;
    }

    public double getDoubleCost() {
        return doubleCost;
    }

    public void setDoubleCost(double doubleCost) {
        this.doubleCost = doubleCost;
    }

    public int getiUnit() {
        return iUnit;
    }

    public void setiUnit(int iUnit) {
        this.iUnit = iUnit;
    }

    public int getiUnitUsed() {
        return iUnitUsed;
    }

    public void setiUnitUsed(int iUnitUsed) {
        this.iUnitUsed = iUnitUsed;
    }

    public int getiServiceNo() {
        return iServiceNo;
    }

    public void setiServiceNo(int iServiceNo) {
        this.iServiceNo = iServiceNo;
    }

    public String getStrServiceId() {
        return strServiceId;
    }

    public void setStrServiceId(String strServiceId) {
        this.strServiceId = strServiceId;
    }

    public String getStrServiceHistoryId() {
        return strServiceHistoryId;
    }

    public void setStrServiceHistoryId(String strServiceHistoryId) {
        this.strServiceHistoryId = strServiceHistoryId;
    }

    public String getStrCustomerId() {
        return strCustomerId;
    }

    public void setStrCustomerId(String strCustomerId) {
        this.strCustomerId = strCustomerId;
    }

    public ArrayList<MilestoneModel> getMilestoneModels() {
        return milestoneModels;
    }

    public void setMilestoneModels(MilestoneModel milestoneModel) {
        this.milestoneModels.add(milestoneModel);
    }
}
