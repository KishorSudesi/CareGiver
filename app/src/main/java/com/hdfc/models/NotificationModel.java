package com.hdfc.models;

import com.hdfc.libs.Utils;

/**
 * Created by Admin on 19-02-2016.
 */
public class NotificationModel {

    private String strMessage = "";
    private String strDateTime = "";
    private String strUserType;
    private String strCreatedByType;
    private String strActivityId;

    private String strUserID;
    private String strCreatedByID;
    private String strNotificationID;

    private String strDisplayDate;

    private int iNew;


    private String strDocId;

    public NotificationModel(String strMessage, String strDateTime, String strUserType,
                             String strCreatedByType, String strUserID, String strCreatedByID,
                             String strNotificationID) {
        this.strMessage = strMessage;
        this.strDateTime = strDateTime;
        this.strUserType = strUserType;
        this.strCreatedByType = strCreatedByType;
        this.strUserID = strUserID;
        this.strCreatedByID = strCreatedByID;
        this.strNotificationID = strNotificationID;

        this.strDisplayDate = Utils.formatDate(strDateTime);
    }

    public int getiNew() {
        return iNew;
    }

    public void setiNew(int iNew) {
        this.iNew = iNew;
    }

    public String getStrDisplayDate() {
        return strDisplayDate;
    }

    public void setStrDisplayDate(String strDisplayDate) {
        this.strDisplayDate = strDisplayDate;
    }

    public String getStrActivityId() {
        return strActivityId;
    }

    public void setStrActivityId(String strActivityId) {
        this.strActivityId = strActivityId;
    }

    public String getStrMessage() {
        return strMessage;
    }

    public void setStrMessage(String strMessage) {
        this.strMessage = strMessage;
    }

    public String getStrDateTime() {
        return strDateTime;
    }

    public void setStrDateTime(String strDateTime) {
        this.strDateTime = strDateTime;
    }

    public String getStrUserType() {
        return strUserType;
    }

    public void setStrUserType(String strUserType) {
        this.strUserType = strUserType;
    }

    public String getStrCreatedByType() {
        return strCreatedByType;
    }

    public void setStrCreatedByType(String strCreatedByType) {
        this.strCreatedByType = strCreatedByType;
    }

    public String getStrUserID() {
        return strUserID;
    }

    public void setStrUserID(String strUserID) {
        this.strUserID = strUserID;
    }

    public String getStrCreatedByID() {
        return strCreatedByID;
    }

    public void setStrCreatedByID(String strCreatedByID) {
        this.strCreatedByID = strCreatedByID;
    }

    public String getStrNotificationID() {
        return strNotificationID;
    }

    public void setStrNotificationID(String strNotificationID) {
        this.strNotificationID = strNotificationID;
    }

    public String getStrDocId() {
        return strDocId;
    }

    public void setStrDocId(String strDocId) {
        this.strDocId = strDocId;
    }
}
