package com.hdfc.models;

/**
 * Created by Suhail on 3/13/2016.
 */
public class DependentModel {

    private String strDependentName;

    private String strDependentIllness;
    private String strDependentRelation;
    private String strDependentAddress;
    private String strDependentNotes;

    private String strDependentContactNo;
    private String strDependentProfileUrl;
    private String strDependentEmail;
    private String strDependentAge;
    private String strCustomerId;

    public DependentModel(){

    }
    public DependentModel(String strDependentName,String strDependentIllness, String strDependentRelation, String strDependentProfileUrl,
                          String strDependentAddress, String strDependentNotes, String strDependentContactNo, String strDependentEmail,String strDependentAge,String strCustomerId){
            this.strDependentName = strDependentName;
            this.strDependentIllness = strDependentIllness;
            this.strDependentRelation = strDependentRelation;
            this.strDependentProfileUrl = strDependentProfileUrl;
            this.strDependentAddress = strDependentAddress;
            this.strDependentNotes = strDependentNotes;
            this.strDependentContactNo = strDependentContactNo;
            this.strDependentEmail = strDependentEmail;
            this.strDependentAge = strDependentAge;
            this.strCustomerId = strCustomerId;
    }


    public String getStrDependentIllness() {
        return strDependentIllness;
    }

    public void setStrDependentIllness(String strDependentIllness) {
        this.strDependentIllness = strDependentIllness;
    }

    public String getStrDependentRelation() {
        return strDependentRelation;
    }

    public void setStrDependentRelation(String strDependentRelation) {
        this.strDependentRelation = strDependentRelation;
    }

    public String getStrDependentAddress() {
        return strDependentAddress;
    }

    public void setStrDependentAddress(String strDependentAddress) {
        this.strDependentAddress = strDependentAddress;
    }

    public String getStrDependentNotes() {
        return strDependentNotes;
    }

    public void setStrDependentNotes(String strDependentNotes) {
        this.strDependentNotes = strDependentNotes;
    }

    public String getStrDependentContactNo() {
        return strDependentContactNo;
    }

    public void setStrDependentContactNo(String strDependentContactNo) {
        this.strDependentContactNo = strDependentContactNo;
    }

    public String getStrDependentProfileUrl() {
        return strDependentProfileUrl;
    }

    public void setStrDependentProfileUrl(String strDependentProfileUrl) {
        this.strDependentProfileUrl = strDependentProfileUrl;
    }

    public String getStrDependentEmail() {
        return strDependentEmail;
    }

    public void setStrDependentEmail(String strDependentEmail) {
        this.strDependentEmail = strDependentEmail;
    }

    public String getStrDependentAge() {
        return strDependentAge;
    }

    public void setStrDependentAge(String strDependentAge) {
        this.strDependentAge = strDependentAge;
    }

    public String getStrCustomerId() {
        return strCustomerId;
    }

    public void setStrCustomerId(String strCustomerId) {
        this.strCustomerId = strCustomerId;
    }

    public String getStrDependentName() {
        return strDependentName;
    }

    public void setStrDependentName(String strDependentName) {
        this.strDependentName = strDependentName;
    }

}
