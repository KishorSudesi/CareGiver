package com.hdfc.models;

import java.io.Serializable;

/**
 * Created by balamurugan@adstringo.in on 2/19/2016.
 */
public class MilestoneViewModel implements Serializable {

    private String strActivityId;
    private String strDependentId;
    private String strMilestoneDate;
    private String strMileStoneName;
    private String strMileStoneStatus;

    public MilestoneViewModel() {
    }

    public String getStrActivityId() {
        return strActivityId;
    }

    public void setStrActivityId(String strActivityId) {
        this.strActivityId = strActivityId;
    }

    public String getStrDependentId() {
        return strDependentId;
    }

    public void setStrDependentId(String strDependentId) {
        this.strDependentId = strDependentId;
    }

    public String getStrMilestoneDate() {
        return strMilestoneDate;
    }

    public void setStrMilestoneDate(String strMilestoneDate) {
        this.strMilestoneDate = strMilestoneDate;
    }

    public String getStrMileStoneName() {
        return strMileStoneName;
    }

    public void setStrMileStoneName(String strMileStoneName) {
        this.strMileStoneName = strMileStoneName;
    }

    public String getStrMileStoneStatus() {
        return strMileStoneStatus;
    }

    public void setStrMileStoneStatus(String strMileStoneStatus) {
        this.strMileStoneStatus = strMileStoneStatus;
    }
}
