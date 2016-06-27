package com.hdfc.models;

import java.io.Serializable;

/**
 * Created by Suhail on 2/19/2016.
 */
public class ImageModel implements Serializable {

    private String strImageName;
    private String strImageUrl;
    private String strImageDesc;
    private String strImageTime;
    private String strImagePath;
    private boolean mIsNew;

    public ImageModel(String strImageName, String strImageUrl, String strImageDesc, String strImageTime, String strImagePath) {
        this.strImageName = strImageName;
        this.strImageUrl = strImageUrl;
        this.strImageDesc = strImageDesc;
        this.strImageTime = strImageTime;
        this.strImagePath = strImagePath;
    }

    public boolean ismIsNew() {
        return mIsNew;
    }

    public void setmIsNew(boolean mIsNew) {
        this.mIsNew = mIsNew;
    }

    public String getStrImageName() {
        return strImageName;
    }

    public void setStrImageName(String strImageName) {
        this.strImageName = strImageName;
    }

    public String getStrImageUrl() {
        return strImageUrl;
    }

    public void setStrImageUrl(String strImageUrl) {
        this.strImageUrl = strImageUrl;
    }

    public String getStrImageDesc() {
        return strImageDesc;
    }

    public void setStrImageDesc(String strImageDesc) {
        this.strImageDesc = strImageDesc;
    }

    public String getStrImageTime() {
        return strImageTime;
    }

    public void setStrImageTime(String strImageTime) {
        this.strImageTime = strImageTime;
    }

    public String getStrImagePath() {
        return strImagePath;
    }

    public void setStrImagePath(String strImagePath) {
        this.strImagePath = strImagePath;
    }
}
