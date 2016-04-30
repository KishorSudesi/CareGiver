package com.hdfc.models;

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

    public ArrayList<FeedBackModel> getFeedBackModels() {
        return feedBackModels;
    }

    public ArrayList<ActivityImageModel> getImageModels() {
        return imageModels;
    }

    public void setImageModels(ArrayList<ActivityImageModel> imageModels) {
        this.imageModels = imageModels;
    }

    public ArrayList<ActivityVideoModel> getVideoModels() {
        return videoModels;
    }

    public void setVideoModels(ArrayList<ActivityVideoModel> videoModels) {
        this.videoModels = videoModels;
    }

    public void setFeedBackModels(ArrayList<FeedBackModel> feedBackModels) {
        this.feedBackModels = feedBackModels;
    }

    private String strActivityId;
    private String strServiceName;

    private ArrayList<ActivityImageModel> imageModels = new ArrayList<>();
    private ArrayList<ActivityVideoModel> videoModels = new ArrayList<>();
    private ArrayList<FeedBackModel> feedBackModels = new ArrayList<>();

    public String getStrServiceName() {
        return strServiceName;
    }

    public void setStrServiceName(String strServiceName) {
        this.strServiceName = strServiceName;
    }

    public String getStrServiceDesc() {
        return strServiceDesc;
    }

    public void setStrServiceDesc(String strServiceDesc) {
        this.strServiceDesc = strServiceDesc;
    }

    private String strServiceDesc;

    private String[] features;
    private String[] doneFeatures;

    private String strActivityProviderEmail;
    private String strActivityProviderContactNo;
    private String strActivityProviderDesc;
    private String strActivityDependentName;

    public String getStrActivityId() {
        return strActivityId;
    }

    public void setStrActivityId(String strActivityId) {
        this.strActivityId = strActivityId;
    }

    private String strDependentId;

    public String getStrDependentId() {
        return strDependentId;
    }

    public void setStrDependentId(String strDependentId) {
        this.strDependentId = strDependentId;
    }

    private String iServiceId;
    private String strProviderImageUrl;
    private ArrayList<ActivityImageModel> activityImageModels = new ArrayList<>();
    private ArrayList<ActivityVideoModel> activityVideoModels = new ArrayList<>();

    public ActivityModel() {
    }

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

    public String getiServiceId() {
        return iServiceId;
    }

    public void setiServiceId(String iServiceId) {
        this.iServiceId = iServiceId;
    }

    public String getStrActivityDesc() {
        return strActivityDesc;
    }
    //private ArrayList<ActivityFeedBackModel> activityFeedBackModels = new ArrayList<>();

    public void setStrActivityDesc(String strActivityDesc) {
        this.strActivityDesc = strActivityDesc;
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

    public void setStrActivityProviderEmail(String strActivityProviderEmail) {
        this.strActivityProviderEmail = strActivityProviderEmail;
    }

    public String getStrActivityProviderContactNo() {
        return strActivityProviderContactNo;
    }

    public void setStrActivityProviderContactNo(String strActivityProviderContactNo) {
        this.strActivityProviderContactNo = strActivityProviderContactNo;
    }

    public String getStrActivityProviderDesc() {
        return strActivityProviderDesc;
    }

    public void setStrActivityProviderDesc(String strActivityProviderDesc) {
        this.strActivityProviderDesc = strActivityProviderDesc;
    }

    public String getStrActivityName() {
        return strActivityName;
    }

    public void setStrActivityName(String strActivityName) {
        this.strActivityName = strActivityName;
    }

    public String getStrActivityMessage() {
        return strActivityMessage;
    }

    public void setStrActivityMessage(String strActivityMessage) {
        this.strActivityMessage = strActivityMessage;
    }

    public String getStrAtivityProvider() {
        return strAtivityProvider;
    }

    public void setStrAtivityProvider(String strAtivityProvider) {
        this.strAtivityProvider = strAtivityProvider;
    }

    public String getStrActivityDate() {
        return strActivityDate;
    }

    public void setStrActivityDate(String strActivityDate) {
        this.strActivityDate = strActivityDate;
    }

    public String getStrActivityStatus() {
        return strActivityStatus;
    }

    public void setStrActivityStatus(String strActivityStatus) {
        this.strActivityStatus = strActivityStatus;
    }

    public ArrayList<ActivityImageModel> getActivityImageModels() {
        return activityImageModels;
    }

   /* public void setActivityFeedBackModels(ArrayList<ActivityFeedBackModel> activityFeedBackModels) {
        this.activityFeedBackModels = activityFeedBackModels;
    }*/

    public void setActivityImageModels(ArrayList<ActivityImageModel> activityImageModels) {
        this.activityImageModels = activityImageModels;
    }

    public ArrayList<ActivityVideoModel> getActivityVideoModels() {
        return activityVideoModels;
    }

    public void setActivityVideoModels(ArrayList<ActivityVideoModel> activityVideoModels) {
        this.activityVideoModels = activityVideoModels;
    }

    /*public ArrayList<ActivityFeedBackModel> getActivityFeedBackModels() {
        return activityFeedBackModels;
    }*/
}
