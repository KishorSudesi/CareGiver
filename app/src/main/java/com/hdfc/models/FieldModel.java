package com.hdfc.models;

import java.io.Serializable;

/**
 * Created by balamurugan@adstringo.in on 2/19/2016.
 */
public class FieldModel implements Serializable {

    private int iFieldID;
    private boolean isFieldRequired = true;
    private boolean isFieldView = false;
    private String strFieldType;
    private String strFieldLabel;
    private String strFieldData;
    private String strFieldValues[];

    private String strArrayData[];
    private String strArrayType[];
    private int iArrayCount;

    private boolean isChild;
    private String strChildType[];
    private String strChildValue[];
    private String strChildCondition[];
    private int iChildfieldID[];

    public boolean isFieldView() {
        return isFieldView;
    }

    public void setFieldView(boolean fieldView) {
        isFieldView = fieldView;
    }

    public int getiFieldID() {
        return iFieldID;
    }

    public void setiFieldID(int iFieldID) {
        this.iFieldID = iFieldID;
    }

    public boolean isFieldRequired() {
        return isFieldRequired;
    }

    public void setFieldRequired(boolean fieldRequired) {
        isFieldRequired = fieldRequired;
    }

    public String getStrFieldType() {
        return strFieldType;
    }

    public void setStrFieldType(String strFieldType) {
        this.strFieldType = strFieldType;
    }

    public String getStrFieldLabel() {
        return strFieldLabel;
    }

    public void setStrFieldLabel(String strFieldLabel) {
        this.strFieldLabel = strFieldLabel;
    }

    public String getStrFieldData() {
        return strFieldData;
    }

    public void setStrFieldData(String strFieldData) {
        this.strFieldData = strFieldData;
    }

    public String[] getStrFieldValues() {
        return strFieldValues;
    }

    public void setStrFieldValues(String[] strFieldValues) {
        this.strFieldValues = strFieldValues;
    }

    public boolean isChild() {
        return isChild;
    }

    public void setChild(boolean child) {
        isChild = child;
    }

    public String[] getStrChildType() {
        return strChildType;
    }

    public void setStrChildType(String[] strChildType) {
        this.strChildType = strChildType;
    }

    public String[] getStrChildValue() {
        return strChildValue;
    }

    public void setStrChildValue(String[] strChildValue) {
        this.strChildValue = strChildValue;
    }

    public String[] getStrChildCondition() {
        return strChildCondition;
    }

    public void setStrChildCondition(String[] strChildCondition) {
        this.strChildCondition = strChildCondition;
    }

    public int[] getiChildfieldID() {
        return iChildfieldID;
    }

    public void setiChildfieldID(int[] iChildfieldID) {
        this.iChildfieldID = iChildfieldID;
    }

    public String[] getStrArrayData() {
        return strArrayData;
    }

    public void setStrArrayData(String[] strArrayData) {
        this.strArrayData = strArrayData;
    }

    public String[] getStrArrayType() {
        return strArrayType;
    }

    public void setStrArrayType(String[] strArrayType) {
        this.strArrayType = strArrayType;
    }

    public int getiArrayCount() {
        return iArrayCount;
    }

    public void setiArrayCount(int iArrayCount) {
        this.iArrayCount = iArrayCount;
    }
}
