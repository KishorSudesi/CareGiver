package com.hdfc.models;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Suhail on 2/19/2016.
 */
public class ActivityModel implements Serializable {

    private String strActivityName;
    private String strActivityMessage;
    private String strActivityDesc;
    private String strAtivityProvider;
    private String strActivityDate;
    private String strActivityDoneDate;
    private String strActivityStatus;
    private String strCustomerEmail;

    private String[] features;
    private String[] doneFeatures;

    private String strActivityProviderEmail;
    private String strActivityProviderContactNo;
    private String strActivityProviderDesc;
    private String strActivityDependentName;

    private int iServiceId;
    private String strProviderImageUrl;

    public String getStrCustomerEmail() {
        return strCustomerEmail;
    }

    public void setStrCustomerEmail(String strCustomerEmail) {
        this.strCustomerEmail = strCustomerEmail;
    }

    public String[] getFeatures() {
        return features;
    }

    public void setFeatures(String[] features) {
        this.features = features;
    }

    public String[] getDoneFeatures() {
        return doneFeatures;
    }

    public void setDoneFeatures(String[] doneFeatures) {
        this.doneFeatures = doneFeatures;
    }

    public String getStrActivityDependentName() {
        return strActivityDependentName;
    }

    public void setStrActivityDependentName(String strActivityDependentName) {
        this.strActivityDependentName = strActivityDependentName;
    }

    public String getStrActivityDoneDate() {
        return strActivityDoneDate;
    }

    public void setStrActivityDoneDate(String strActivityDoneDate) {
        this.strActivityDoneDate = strActivityDoneDate;
    }

    public int getiServiceId() {
        return iServiceId;
    }

    public void setiServiceId(int iServiceId) {
        this.iServiceId = iServiceId;
    }

    public String getStrActivityDesc() {
        return strActivityDesc;
    }

    public void setStrActivityDesc(String strActivityDesc) {
        this.strActivityDesc = strActivityDesc;
    }

    private ArrayList<ActivityImageModel> activityImageModels = new ArrayList<>();
    private ArrayList<ActivityVideoModel> activityVideoModels = new ArrayList<>();
    private ArrayList<ActivityFeedBackModel> activityFeedBackModels = new ArrayList<>();

    public ActivityModel() {
    }

    public String getStrProviderImageUrl() {
        return strProviderImageUrl;
    }

    public void setStrProviderImageUrl(String strProviderImageUrl) {
        this.strProviderImageUrl = strProviderImageUrl;
    }


    public String getStrActivityProviderEmail() {
        return strActivityProviderEmail;
    }

    public String getStrActivityProviderContactNo() {
        return strActivityProviderContactNo;
    }

    public String getStrActivityProviderDesc() {
        return strActivityProviderDesc;
    }

    public String getStrActivityName() {
        return strActivityName;
    }

    public String getStrActivityMessage() {
        return strActivityMessage;
    }

    public String getStrAtivityProvider() {
        return strAtivityProvider;
    }

    public String getStrActivityDate() {
        return strActivityDate;
    }

    public void setStrActivityName(String strActivityName) {
        this.strActivityName = strActivityName;
    }

    public void setStrActivityMessage(String strActivityMessage) {
        this.strActivityMessage = strActivityMessage;
    }

    public void setStrAtivityProvider(String strAtivityProvider) {
        this.strAtivityProvider = strAtivityProvider;
    }

    public void setStrActivityDate(String strActivityDate) {
        this.strActivityDate = strActivityDate;
    }

    public void setStrActivityStatus(String strActivityStatus) {
        this.strActivityStatus = strActivityStatus;
    }

    public void setStrActivityProviderEmail(String strActivityProviderEmail) {
        this.strActivityProviderEmail = strActivityProviderEmail;
    }

    public void setStrActivityProviderContactNo(String strActivityProviderContactNo) {
        this.strActivityProviderContactNo = strActivityProviderContactNo;
    }

    public void setStrActivityProviderDesc(String strActivityProviderDesc) {
        this.strActivityProviderDesc = strActivityProviderDesc;
    }

    public void setActivityImageModels(ArrayList<ActivityImageModel> activityImageModels) {
        this.activityImageModels = activityImageModels;
    }

    public void setActivityVideoModels(ArrayList<ActivityVideoModel> activityVideoModels) {
        this.activityVideoModels = activityVideoModels;
    }

    public void setActivityFeedBackModels(ArrayList<ActivityFeedBackModel> activityFeedBackModels) {
        this.activityFeedBackModels = activityFeedBackModels;
    }

    public String getStrActivityStatus() {
        return strActivityStatus;
    }

    public ArrayList<ActivityImageModel> getActivityImageModels() {
        return activityImageModels;
    }

    public ArrayList<ActivityVideoModel> getActivityVideoModels() {
        return activityVideoModels;
    }

    public ArrayList<ActivityFeedBackModel> getActivityFeedBackModels() {
        return activityFeedBackModels;
    }
}
