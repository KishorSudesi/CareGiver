package com.hdfc.models;

import java.io.Serializable;

/**
 * Created by Suhail on 2/19/2016.
 */
public class ActivityFeedBackModel implements Serializable {

    private String strFeedBackMess;
    private String strFeedBackBy;
    private int intFeedBackRating;
    private int intFeedBackReport;
    private String strFeedBackTime;
    private String strFeedBackByUrl;

    public ActivityFeedBackModel(String strFeedBackMess, String strFeedBackBy, int intFeedBackRating, int intFeedBackReport, String strFeedBackTime, String strFeedBackByUrl) {
        this.strFeedBackMess = strFeedBackMess;
        this.strFeedBackBy = strFeedBackBy;
        this.intFeedBackRating = intFeedBackRating;
        this.intFeedBackReport = intFeedBackReport;
        this.strFeedBackTime = strFeedBackTime;
        this.strFeedBackByUrl = strFeedBackByUrl;
    }

    public String getStrFeedBackMess() {
        return strFeedBackMess;
    }

    public String getStrFeedBackBy() {
        return strFeedBackBy;
    }

    public int getIntFeedBackRating() {
        return intFeedBackRating;
    }

    public int getIntFeedBackReport() {
        return intFeedBackReport;
    }

    public String getStrFeedBackByUrl() {
        return strFeedBackByUrl;
    }

    public String getStrFeedBackTime() {
        return strFeedBackTime;


    }
}
