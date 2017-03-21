package com.hdfc.models;

/**
 * Created by Admin on 01-10-2016.
 */

public class UpdateVersionModel {

    private String strAppVersion;
    private String strSourceName;
    private String strAppUrl;

    public UpdateVersionModel(String strAppVersion, String strSourceName, String strAppUrl) {
        this.strAppVersion = strAppVersion;
        this.strSourceName = strSourceName;
        this.strAppUrl = strAppUrl;
    }

    public UpdateVersionModel() {
    }

    public String getStrAppVersion() {
        return strAppVersion;
    }

    public void setStrAppVersion(String strAppVersion) {
        this.strAppVersion = strAppVersion;
    }

    public String getStrSourceName() {
        return strSourceName;
    }

    public void setStrSourceName(String strSourceName) {
        this.strSourceName = strSourceName;
    }

    public String getStrAppUrl() {
        return strAppUrl;
    }

    public void setStrAppUrl(String strAppUrl) {
        this.strAppUrl = strAppUrl;
    }

}
