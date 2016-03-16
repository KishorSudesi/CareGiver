package com.hdfc.models;

/**
 * Created by Suhail on 2/19/2016.
 */
public class ImageModel {

    private String strImageName;
    private String strImageUrl;
    private String strImageDesc;
    private String strImageTime;

    public ImageModel(String strImageName, String strImageUrl, String strImageDesc, String strImageTime) {
        this.strImageName = strImageName;
        this.strImageUrl = strImageUrl;
        this.strImageDesc = strImageDesc;
        this.strImageTime = strImageTime;
    }

    public String getStrImageName() {
        return strImageName;
    }

    public String getStrImageUrl() {
        return strImageUrl;
    }

    public String getStrImageDesc() {
        return strImageDesc;
    }

    public String getStrImageTime() {
        return strImageTime;
    }
}
