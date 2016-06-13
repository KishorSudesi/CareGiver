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

    public FeedBackModel(String strFeedBackMessage, String strFeedBackBy, int intFeedBackRating, String strFeedBackTime, String strFeedBackByUrl) {
        this.strFeedBackMessage = strFeedBackMessage;
        this.strFeedBackBy = strFeedBackBy;
        this.intFeedBackRating = intFeedBackRating;
        this.strFeedBackTime = strFeedBackTime;
        this.strFeedBackByUrl = strFeedBackByUrl;
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
