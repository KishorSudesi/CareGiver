package com.hdfc.models;

/**
 * Created by Suhail on 2/19/2016.
 */
public class FileModel {

    private String strFileName;
    private String strFileUrl;
    private String strImageType;
    private String strUrlMd5;

    public FileModel(String strFileName, String strFileUrl, String strImageType) {
        this.strFileName = strFileName;
        this.strFileUrl = strFileUrl;
        this.strImageType = strImageType;
    }

    public String getStrUrlMd5() {
        return strUrlMd5;
    }

    public void setStrUrlMd5(String strUrlMd5) {
        this.strUrlMd5 = strUrlMd5;
    }

    public String getStrFileName() {
        return strFileName;
    }

    public void setStrFileName(String strFileName) {
        this.strFileName = strFileName;
    }

    public String getStrFileUrl() {
        return strFileUrl;
    }

    public void setStrFileUrl(String strFileUrl) {
        this.strFileUrl = strFileUrl;
    }

    public String getStrImageType() {
        return strImageType;
    }

    public void setStrImageType(String strImageType) {
        this.strImageType = strImageType;
    }
}
