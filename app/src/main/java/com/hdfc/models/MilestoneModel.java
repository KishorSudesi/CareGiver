package com.hdfc.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by balamurugan@adstringo.in on 2/19/2016.
 */
public class MilestoneModel implements Serializable {

    private int iMilestoneId;
    private String strMilestoneStatus;
    private String strMilestoneName;
    private String strMilestoneDate;
    private String strMilestoneScheduledDate;

    private boolean isVisible;
    private boolean isReschedule;
    private ArrayList<FieldModel> fieldModels = new ArrayList<>();
    private ArrayList<FileModel> fileModels = new ArrayList<>();

    public ArrayList<FileModel> getFileModels() {
        return fileModels;
    }

    public void setFileModels(ArrayList<FileModel> fileModels) {
        this.fileModels = fileModels;
    }

    public void setFileModel(FileModel fileModel) {
        this.fileModels.add(fileModel);
    }

    public void clearFileModel() {
        this.fileModels.clear();
    }

    public boolean isReschedule() {
        return isReschedule;
    }

    public void setReschedule(boolean reschedule) {
        isReschedule = reschedule;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public ArrayList<FieldModel> getFieldModels() {
        return fieldModels;
    }

    public void setFieldModels(ArrayList<FieldModel> fieldModels) {
        this.fieldModels = fieldModels;
    }

    public void setFieldModel(FieldModel fieldModel) {
        this.fieldModels.add(fieldModel);
    }

    public int getiMilestoneId() {
        return iMilestoneId;
    }

    public void setiMilestoneId(int iMilestoneId) {
        this.iMilestoneId = iMilestoneId;
    }

    public String getStrMilestoneStatus() {
        return strMilestoneStatus;
    }

    public void setStrMilestoneStatus(String strMilestoneStatus) {
        this.strMilestoneStatus = strMilestoneStatus;
    }

    public String getStrMilestoneScheduledDate() {
        return strMilestoneScheduledDate;
    }

    public void setStrMilestoneScheduledDate(String strMilestoneScheduledDate) {
        this.strMilestoneScheduledDate = strMilestoneScheduledDate;
    }

    public String getStrMilestoneName() {
        return strMilestoneName;
    }

    public void setStrMilestoneName(String strMilestoneName) {
        this.strMilestoneName = strMilestoneName;
    }

    public String getStrMilestoneDate() {
        return strMilestoneDate;
    }

    public void setStrMilestoneDate(String strMilestoneDate) {
        this.strMilestoneDate = strMilestoneDate;
    }
}
