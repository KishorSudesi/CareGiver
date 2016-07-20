package com.hdfc.models;

/**
 * Created by balamurugan@adstringo.in on 01-01-2016.
 */
public class ProviderModel {

    private String strName = "";
    private String strImgUrl = "";
    //private String strImgPath = "";
    private String strAddress = "";
    private String strContacts = "";
    private String strEmail = "";

    private String strCountry;
    private String strCity;
    private String strState;
    private String strPinCode;


    private String strProviderId;

    public ProviderModel() {
    }


    public ProviderModel(String strName, String strImgUrl, String strAddress,
                         String strContacts, String strEmail, String strProviderId) {
        this.strName = strName;
        this.strImgUrl = strImgUrl;
        //this.strImgPath = strImgPath;
        this.strAddress = strAddress;
        this.strContacts = strContacts;
        this.strEmail = strEmail;
        this.strProviderId = strProviderId;
    }

    /*public String getStrCountry() {
        return strCountry;
    }*/

    public void setStrCountry(String strCountry) {
        this.strCountry = strCountry;
    }

    /*public String getStrCity() {
        return strCity;
    }*/

    public void setStrCity(String strCity) {
        this.strCity = strCity;
    }

    /*public String getStrState() {
        return strState;
    }*/

    public void setStrState(String strState) {
        this.strState = strState;
    }

    /*public String getStrPinCode() {
        return strPinCode;
    }*/

    public void setStrPinCode(String strPinCode) {
        this.strPinCode = strPinCode;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public String getStrImgUrl() {
        return strImgUrl;
    }

    public void setStrImgUrl(String strImgUrl) {
        this.strImgUrl = strImgUrl;
    }

   /* public String getStrImgPath() {
        return strImgPath;
    }

    public void setStrImgPath(String strImgPath) {
        this.strImgPath = strImgPath;
    }*/

    public String getStrAddress() {
        return strAddress;
    }

    public void setStrAddress(String strAddress) {
        this.strAddress = strAddress;
    }

    public String getStrContacts() {
        return strContacts;
    }

    public void setStrContacts(String strContacts) {
        this.strContacts = strContacts;
    }

    public String getStrEmail() {
        return strEmail;
    }

    /*public void setStrEmail(String strEmail) {
        this.strEmail = strEmail;
    }*/

    public String getStrProviderId() {
        return strProviderId;
    }

    /*public void setStrProviderId(String strProviderId) {
        this.strProviderId = strProviderId;
    }*/
}
