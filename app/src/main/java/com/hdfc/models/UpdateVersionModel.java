package com.hdfc.models;

/**
 * Created by Admin on 28-09-2016.
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

    public String getStrAppVersion() {
        return strAppVersion;
    }

    public String getStrSourceName() {
        return strSourceName;
    }

    public String getStrAppUrl() {
        return strAppUrl;
    }

}
