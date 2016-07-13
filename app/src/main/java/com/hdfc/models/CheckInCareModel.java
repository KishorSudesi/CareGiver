package com.hdfc.models;

import java.util.ArrayList;

/**
 * Created by Admin on 07-07-2016.
 */
public class CheckInCareModel {
    private String strStatus = "";
    private String strMonth = "";
    private String strYear = "";
    private String strHouseName = "";
    private String strCreatedDate = "";
    private String strUpdatedDate = "";

    private String strCustomerID;
    private String strDependentID;

    private ArrayList<PictureModel> pictureModels = new ArrayList<>();
    private ArrayList<ImageModel> imageModels = new ArrayList<>();

    private ArrayList<CheckInCareActivityModel> checkInCareActivityModels = new ArrayList<>();

    public CheckInCareModel(String strStatus, String strMonth, String strYear,
                             String strHouseName, String strCreatedDate, String strUpdatedDate,
                             String strCustomerID,String strDependentID,ArrayList<PictureModel> pictureModels,
                            ArrayList<CheckInCareActivityModel> checkInCareActivityModels) {
        this.strStatus = strStatus;
        this.strMonth = strMonth;
        this.strYear = strYear;
        this.strHouseName = strHouseName;
        this.strCreatedDate = strCreatedDate;
        this.strUpdatedDate = strUpdatedDate;
        this.strCustomerID = strCustomerID;
        this.strDependentID = strDependentID;
        this.pictureModels = pictureModels;
        this.checkInCareActivityModels = checkInCareActivityModels;


    }
    public String getStrStatus() {
        return strStatus;
    }

    public void setStrStatus(String strStatus) {
        this.strStatus = strStatus;
    }

    public String getStrMonth() {
        return strMonth;
    }

    public void setStrMonth(String strMonth) {
        this.strMonth = strMonth;
    }

    public String getStrYear() {
        return strYear;
    }

    public void setStrYear(String strYear) {
        this.strYear = strYear;
    }

    public String getStrHouseName() {
        return strHouseName;
    }

    public void setStrHouseName(String strHouseName) {
        this.strHouseName = strHouseName;
    }

    public String getStrCreatedDate() {
        return strCreatedDate;
    }

    public void setStrCreatedDate(String strCreatedDate) {
        this.strCreatedDate = strCreatedDate;
    }

    public String getStrUpdatedDate() {
        return strUpdatedDate;
    }

    public void setStrUpdatedDate(String strUpdatedDate) {
        this.strUpdatedDate = strUpdatedDate;
    }

    public String getStrDependentID() {
        return strDependentID;
    }

    public void setStrDependentID(String strDependentID) {
        this.strDependentID = strDependentID;
    }

    public String getStrCustomerID() {
        return strCustomerID;
    }

    public void setStrCustomerID(String strCustomerID) {
        this.strCustomerID = strCustomerID;
    }



    public ArrayList<PictureModel> getPictureModels() {
        return pictureModels;
    }

    public void setPictureModels(ArrayList<PictureModel> pictureModels) {
        this.pictureModels = pictureModels;
    }
    public void clearPictureModels() {
        this.pictureModels.clear();
    }

    public ArrayList<ImageModel> getImageModels() {
        return imageModels;
    }

    public void setImageModels(ArrayList<ImageModel> imageModels) {
        this.imageModels = imageModels;
    }
    public void clearImageModel() {
        this.imageModels.clear();
    }


    public ArrayList<CheckInCareActivityModel> getCheckInCareActivityModels() {
        return checkInCareActivityModels;
    }

    public void setCheckInCareActivityModels(ArrayList<CheckInCareActivityModel> checkInCareActivityModels) {
        this.checkInCareActivityModels = checkInCareActivityModels;
    }
    public void clearCheckInCareActivityModels() {
        this.checkInCareActivityModels.clear();
    }



}
