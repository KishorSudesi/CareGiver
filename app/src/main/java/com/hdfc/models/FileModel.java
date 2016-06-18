package com.hdfc.models;

import java.io.Serializable;

/**
 * Created by Suhail on 2/19/2016.
 */
public class FileModel implements Serializable {

    private String strFileName;
    private String strFileUrl;
    private String strFilePath;
    private String strFileType;
    private String strUrlMd5;
    private String strFileUploadTime;
    private String strFileDescription;

    public FileModel(String strFileName, String strFileUrl, String strFileType,
                     String strFileUploadTime, String strFileDescription,
                     String strFilePath) {
        this.strFileName = strFileName;
        this.strFileUrl = strFileUrl;
        this.strFileType = strFileType;
        this.strFileUploadTime = strFileUploadTime;
        this.strFileDescription = strFileDescription;
        this.strFilePath = strFilePath;
    }

    public String getStrFilePath() {
        return strFilePath;
    }

    public void setStrFilePath(String strFilePath) {
        this.strFilePath = strFilePath;
    }

    public String getStrFileType() {
        return strFileType;
    }

    public void setStrFileType(String strFileType) {
        this.strFileType = strFileType;
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

    public String getstrFileType() {
        return strFileType;
    }

    public void setstrFileType(String strFileType) {
        this.strFileType = strFileType;
    }

    public String getStrFileUploadTime() {
        return strFileUploadTime;
    }

    public void setStrFileUploadTime(String strFileUploadTime) {
        this.strFileUploadTime = strFileUploadTime;
    }

    public String getStrFileDescription() {
        return strFileDescription;
    }

    public void setStrFileDescription(String strFileDescription) {
        this.strFileDescription = strFileDescription;
    }
}
