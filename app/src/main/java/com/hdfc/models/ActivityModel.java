package com.hdfc.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Suhail on 2/19/2016.
 */
public class ActivityModel implements Serializable {

    private String strActivityID;
    private String strustomerID;
    private String strDependentID;
    private String strProviderID;
    private String strServcieID;

    private String strActivityName;
    private String strActivityDesc;
    private String strActivityMessage;
    private String strActivityStatus;
    private String strServiceName;
    private String strServiceDesc;
    private String strActivityDate;
    private String strActivityDoneDate;
    private String strActivityProviderStatus;

    private boolean bActivityOverdue;

    private String[] strFeatures;
    private String[] strFeaturesDone;

    private ArrayList<ImageModel> imageModels = new ArrayList<>();
    private ArrayList<VideoModel> videoModels = new ArrayList<>();
    private ArrayList<FeedBackModel> feedBackModels = new ArrayList<>();

    public ActivityModel() {
    }

    public ActivityModel(String strActivityID, String strustomerID, String strDependentID,
                         String strProviderID, String strServcieID, String strActivityName,
                         String strActivityDesc, String strActivityMessage, String strActivityStatus,
                         String strServiceName, String strServiceDesc, String strActivityDate,
                         String strActivityDoneDate, String strActivityProviderStatus,
                         boolean bActivityOverdue, String[] strFeatures, String[] strFeaturesDone,
                         ArrayList<ImageModel> imageModels, ArrayList<VideoModel> videoModels,
                         ArrayList<FeedBackModel> feedBackModels) {
        this.strActivityID = strActivityID;
        this.strustomerID = strustomerID;
        this.strDependentID = strDependentID;
        this.strProviderID = strProviderID;
        this.strServcieID = strServcieID;
        this.strActivityName = strActivityName;
        this.strActivityDesc = strActivityDesc;
        this.strActivityMessage = strActivityMessage;
        this.strActivityStatus = strActivityStatus;
        this.strServiceName = strServiceName;
        this.strServiceDesc = strServiceDesc;
        this.strActivityDate = strActivityDate;
        this.strActivityDoneDate = strActivityDoneDate;
        this.strActivityProviderStatus = strActivityProviderStatus;
        this.bActivityOverdue = bActivityOverdue;
        this.strFeatures = strFeatures;
        this.strFeaturesDone = strFeaturesDone;
        this.imageModels = imageModels;
        this.videoModels = videoModels;
        this.feedBackModels = feedBackModels;
    }

    public String getStrActivityID() {
        return strActivityID;
    }

    public void setStrActivityID(String strActivityID) {
        this.strActivityID = strActivityID;
    }

    public String getStrustomerID() {
        return strustomerID;
    }

    public void setStrustomerID(String strustomerID) {
        this.strustomerID = strustomerID;
    }

    public String getStrDependentID() {
        return strDependentID;
    }

    public void setStrDependentID(String strDependentID) {
        this.strDependentID = strDependentID;
    }

    public String getStrProviderID() {
        return strProviderID;
    }

    public void setStrProviderID(String strProviderID) {
        this.strProviderID = strProviderID;
    }

    public String getStrServcieID() {
        return strServcieID;
    }

    public void setStrServcieID(String strServcieID) {
        this.strServcieID = strServcieID;
    }

    public String getStrActivityName() {
        return strActivityName;
    }

    public void setStrActivityName(String strActivityName) {
        this.strActivityName = strActivityName;
    }

    public String getStrActivityDesc() {
        return strActivityDesc;
    }

    public void setStrActivityDesc(String strActivityDesc) {
        this.strActivityDesc = strActivityDesc;
    }

    public String getStrActivityMessage() {
        return strActivityMessage;
    }

    public void setStrActivityMessage(String strActivityMessage) {
        this.strActivityMessage = strActivityMessage;
    }

    public String getStrActivityStatus() {
        return strActivityStatus;
    }

    public void setStrActivityStatus(String strActivityStatus) {
        this.strActivityStatus = strActivityStatus;
    }

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

    public String getStrActivityDate() {
        return strActivityDate;
    }

    public void setStrActivityDate(String strActivityDate) {
        this.strActivityDate = strActivityDate;
    }

    public String getStrActivityDoneDate() {
        return strActivityDoneDate;
    }

    public void setStrActivityDoneDate(String strActivityDoneDate) {
        this.strActivityDoneDate = strActivityDoneDate;
    }

    public String getStrActivityProviderStatus() {
        return strActivityProviderStatus;
    }

    public void setStrActivityProviderStatus(String strActivityProviderStatus) {
        this.strActivityProviderStatus = strActivityProviderStatus;
    }

    public boolean isbActivityOverdue() {
        return bActivityOverdue;
    }

    public void setbActivityOverdue(boolean bActivityOverdue) {
        this.bActivityOverdue = bActivityOverdue;
    }

    public String[] getStrFeatures() {
        return strFeatures;
    }

    public void setStrFeatures(String[] strFeatures) {
        this.strFeatures = strFeatures;
    }

    public String[] getStrFeaturesDone() {
        return strFeaturesDone;
    }

    public void setStrFeaturesDone(String[] strFeaturesDone) {
        this.strFeaturesDone = strFeaturesDone;
    }

    public ArrayList<ImageModel> getImageModels() {
        return imageModels;
    }

    public void setImageModels(ArrayList<ImageModel> imageModels) {
        this.imageModels = imageModels;
    }

    public ArrayList<VideoModel> getVideoModels() {
        return videoModels;
    }

    public void setVideoModels(ArrayList<VideoModel> videoModels) {
        this.videoModels = videoModels;
    }

    public ArrayList<FeedBackModel> getFeedBackModels() {
        return feedBackModels;
    }

    public void setFeedBackModels(ArrayList<FeedBackModel> feedBackModels) {
        this.feedBackModels = feedBackModels;
    }
}
