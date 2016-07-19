package com.hdfc.models;

import java.util.ArrayList;

/**
 * Created by Admin on 07-07-2016.
 */
public class PictureModel {
    private String strRoomName = "";
    private String strStatus = "";

    private ArrayList<ImageModel> imageModels = new ArrayList<>();

    public PictureModel() {

    }
    public PictureModel(String strRoomName, String strStatus, ArrayList<ImageModel> imageModels) {
        this.strStatus = strStatus;
        this.strRoomName = strRoomName;
        this.imageModels = imageModels;

    }

    public String getStrRoomName() {
        return strRoomName;
    }

    public void setStrRoomName(String strRoomName) {
        this.strRoomName = strRoomName;
    }

    public String getStrStatus() {
        return strStatus;
    }

    public void setStrStatus(String strStatus) {
        this.strStatus = strStatus;
    }



    public ArrayList<ImageModel> getImageModels() {
        return imageModels;
    }

    public void setImageModels(ArrayList<ImageModel> imageModels) {
        this.imageModels = imageModels;
    }
    public void clearImageModel() {
        this.imageModels.clear();
    }
}
