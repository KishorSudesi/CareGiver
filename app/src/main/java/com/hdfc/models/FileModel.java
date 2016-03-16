package com.hdfc.models;

/**
 * Created by Suhail on 2/19/2016.
 */
public class FileModel {

    private String strFileName;
    private String strFileUrl;
    private String strImageType;

    public FileModel(String strFileName, String strFileUrl, String strImageType) {
        this.strFileName = strFileName;
        this.strFileUrl = strFileUrl;
        this.strImageType = strImageType;
    }

    public String getStrFileName() {
        return strFileName;
    }

    public String getStrFileUrl() {
        return strFileUrl;
    }

    public String getStrImageType() {
        return strImageType;
    }
}
