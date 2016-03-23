package com.hdfc.config;

import com.hdfc.libs.Libs;
import com.hdfc.models.CreateTaskModel;
import com.hdfc.models.FileModel;
import com.hdfc.models.MyProfileModel;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by balamurugan@adstringo.in on 23-12-2015.
 */
public class Config {

    public static final String dbName = "newzeal";
    public static final String collectionNameServices = "services";
    public static final String collectionName = "providers";
    public static final String collectionName2="customer";
    public static final String strServiceDocId = "56c70aefe4b0067c8c7658bf";//remove this
    public static final String string = Libs.getStringJni();

    public static final int START_CAMERA_REQUEST_CODE = 1;
    public static final int START_GALLERY_REQUEST_CODE = 2;
    public static final int CACHE_EXPIRE = 1;//In Minutes
    public static final int intWidth = 300, intHeight = 300;
    public static final String[] weekNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    public static final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    public static final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    public static final boolean isDebuggable = false;
    public static final boolean release = true;
    public final static String strCustomerImageName = "provider_image";
    public static ArrayList<FileModel> fileModels = new ArrayList<>();
    public static int intClientScreen = 2;
    public static int intRatingsScreen = 3;
    public static int intSimpleActivityScreen = 1;
    public static int intScreenWidth = 0;
    public static int intScreenHeight = 0;
    //User Specific clear at logout or whenever needed
    public static JSONObject jsonObject = null;
    public static JSONObject jsonServer = null;
    public static String jsonDocId = "";
    public static int intSelectedMenu = 0;
    public static int intDependantsCount = 0;
    public static String strUserName = "";
    public static MyProfileModel myProfileModel = null;
    public static CreateTaskModel createTaskModel=null;
    public static int intDashboardScreen = 1;

    public static boolean boolIsLoggedIn = false;
}