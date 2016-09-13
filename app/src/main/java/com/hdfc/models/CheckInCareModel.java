package com.hdfc.models;

import java.util.ArrayList;

/**
 * Created by Admin on 07-07-2016.
 */
public class CheckInCareModel {

    private String strDocumentID;
    private String strName = "";
    private String strStatus = "";
    private String strMonth = "";
    private String strYear = "";
    private String strDate = "";
    private String strCreatedDate = "";
    private String strCurrentDate = "";
    private String strUpdatedDate = "";
    private String strCheckInCareName = "";
    private String strMediaComment = "";
    private String strCreatedActualDate = "";


    private String strCustomerID;
    private String strDependentID;
    private String strProviderID;

    private ArrayList<PictureModel> pictureModels = new ArrayList<>();
    private ArrayList<ImageModel> imageModels = new ArrayList<>();

    private ArrayList<CheckInCareActivityModel> checkInCareActivityModels = new ArrayList<>();

    public CheckInCareModel() {
    }

    public CheckInCareModel(String strDocumentID, String strName, String strStatus, String strMonth, String strYear,
                            String strCreatedDate, String strUpdatedDate,
                            String strCustomerID, String strDependentID, String strCurrentDate,
                            String strCheckInCareName, String strMediaComment, ArrayList<PictureModel> pictureModels,
                            ArrayList<CheckInCareActivityModel> checkInCareActivityModels) {
        this.strDocumentID = strDocumentID;
        this.strName = strName;
        this.strStatus = strStatus;
        this.strMonth = strMonth;
        this.strYear = strYear;
        this.strCreatedDate = strCreatedDate;
        this.strUpdatedDate = strUpdatedDate;
        this.strCustomerID = strCustomerID;
        this.strDependentID = strDependentID;
        this.strCheckInCareName = strCheckInCareName;
        this.pictureModels = pictureModels;
        this.strCurrentDate = strCurrentDate;
        this.strMediaComment = strMediaComment;
        this.checkInCareActivityModels = checkInCareActivityModels;


    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public String getStrStatus() {
        return strStatus;
    }

    public void setStrStatus(String strStatus) {
        this.strStatus = strStatus;
    }

    public String getStrCreatedActualDate() {
        return strCreatedActualDate;
    }

    public void setStrCreatedActualDate(String strCreatedActualDate) {
        this.strCreatedActualDate = strCreatedActualDate;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
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

    public String getStrProviderID() {
        return strProviderID;
    }

    public void setStrProviderID(String strProviderID) {
        this.strProviderID = strProviderID;
    }


    public String getStrDocumentID() {
        return strDocumentID;
    }

    public void setStrDocumentID(String strDocumentID) {
        this.strDocumentID = strDocumentID;
    }

    public String getStrCheckInCareName() {
        return strCheckInCareName;
    }

    public void setStrCheckInCareName(String strCheckInCareName) {
        this.strCheckInCareName = strCheckInCareName;
    }

    public String getStrMediaComment() {
        return strMediaComment;
    }

    public void setStrMediaComment(String strMediaComment) {
        this.strMediaComment = strMediaComment;
    }

    public String getStrCurrentDate() {
        return strCurrentDate;
    }

    public void setStrCurrentDate(String strCurrentDate) {
        this.strCurrentDate = strCurrentDate;
    }


    public ArrayList<PictureModel> getPictureModels() {
        return pictureModels;
    }

    public void setPictureModels(ArrayList<PictureModel> pictureModels) {
        this.pictureModels = pictureModels;
    }

    public void setPictureModel(PictureModel pictureModel) {
        this.pictureModels.add(pictureModel);
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
