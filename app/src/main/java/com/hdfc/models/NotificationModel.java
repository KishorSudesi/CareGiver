package com.hdfc.models;

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

    public String getStrDateTime() {
        return strDateTime;
    }

    public String getStrUserType() {
        return strUserType;
    }

    public String getStrCreatedByType() {
        return strCreatedByType;
    }

    public String getStrUserID() {
        return strUserID;
    }

    public String getStrCreatedByID() {
        return strCreatedByID;
    }

    public String getStrNotificationID() {
        return strNotificationID;
    }
}
