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

    public FeedBackModel(String strFeedBackMessage, String strFeedBackBy, int intFeedBackRating, boolean bFeedBackReport, String strFeedBackTime, String strFeedBackByUrl) {
        this.strFeedBackMessage = strFeedBackMessage;
        this.strFeedBackBy = strFeedBackBy;
        this.intFeedBackRating = intFeedBackRating;
        this.bFeedBackReport = bFeedBackReport;
        this.strFeedBackTime = strFeedBackTime;
        this.strFeedBackByUrl = strFeedBackByUrl;
    }

    public String getStrFeedBackMessage() {
        return strFeedBackMessage;
    }

    public String getStrFeedBackBy() {
        return strFeedBackBy;
    }

    public int getIntFeedBackRating() {
        return intFeedBackRating;
    }

    public boolean getBoolFeedBackReport() {
        return bFeedBackReport;
    }

    public String getStrFeedBackByUrl() {
        return strFeedBackByUrl;
    }

    public String getStrFeedBackTime() {
        return strFeedBackTime;
    }
}
