package com.hdfc.models;

import java.io.Serializable;

/**
 * Created by Suhail on 2/19/2016.
 */
public class FeedBackModel implements Serializable {

    private String strFeedBackMessage;
    private String strFeedBackBy;
    private int intFeedBackRating;
    private boolean bFeedBackReport;
    private String strFeedBackTime;
    private String strFeedBackByUrl;

    private String strActivityName;
    private String strDependentId;
    private String strActivityDate;
    private String strActivityId;

    public FeedBackModel(String strFeedBackMessage, String strFeedBackBy, int intFeedBackRating,
                         String strFeedBackTime, String strFeedBackByUrl) {
        this.strFeedBackMessage = strFeedBackMessage;
        this.strFeedBackBy = strFeedBackBy;
        this.intFeedBackRating = intFeedBackRating;
        this.strFeedBackTime = strFeedBackTime;
        this.strFeedBackByUrl = strFeedBackByUrl;
    }

    public String getStrActivityId() {
        return strActivityId;
    }

    public void setStrActivityId(String strActivityId) {
        this.strActivityId = strActivityId;
    }

    public String getStrActivityName() {
        return strActivityName;
    }

    public void setStrActivityName(String strActivityName) {
        this.strActivityName = strActivityName;
    }

    public String getStrDependentId() {
        return strDependentId;
    }

    public void setStrDependentId(String strDependentId) {
        this.strDependentId = strDependentId;
    }

    public String getStrActivityDate() {
        return strActivityDate;
    }

    public void setStrActivityDate(String strActivityDate) {
        this.strActivityDate = strActivityDate;
    }

    public boolean isbFeedBackReport() {
        return bFeedBackReport;
    }

    public void setbFeedBackReport(boolean bFeedBackReport) {
        this.bFeedBackReport = bFeedBackReport;
    }

    public String getStrFeedBackMessage() {
        return strFeedBackMessage;
    }

    public void setStrFeedBackMessage(String strFeedBackMessage) {
        this.strFeedBackMessage = strFeedBackMessage;
    }

    public String getStrFeedBackBy() {
        return strFeedBackBy;
    }

    public void setStrFeedBackBy(String strFeedBackBy) {
        this.strFeedBackBy = strFeedBackBy;
    }

    public int getIntFeedBackRating() {
        return intFeedBackRating;
    }

    public void setIntFeedBackRating(int intFeedBackRating) {
        this.intFeedBackRating = intFeedBackRating;
    }

    public boolean getBoolFeedBackReport() {
        return bFeedBackReport;
    }

    public String getStrFeedBackByUrl() {
        return strFeedBackByUrl;
    }

    public void setStrFeedBackByUrl(String strFeedBackByUrl) {
        this.strFeedBackByUrl = strFeedBackByUrl;
    }

    public String getStrFeedBackTime() {
        return strFeedBackTime;
    }

    public void setStrFeedBackTime(String strFeedBackTime) {
        this.strFeedBackTime = strFeedBackTime;
    }
}
