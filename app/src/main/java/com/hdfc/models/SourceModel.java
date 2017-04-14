package com.hdfc.models;

/**
 * Created by Admin on 11-04-2017.
 */

public class SourceModel  {


    private String strAppVersion;
    private String strSourceName;
    private String strAppUrl;

    public SourceModel() {

    }

    public SourceModel(String strAppVersion, String strSourceName, String strAppUrl) {
        this.strAppVersion = strAppVersion;
        this.strSourceName = strSourceName;
        this.strAppUrl = strAppUrl;

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
