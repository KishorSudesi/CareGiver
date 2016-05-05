package com.hdfc.config;

import android.os.Build;

import com.hdfc.caregiver.BuildConfig;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.ClientModel;
import com.hdfc.models.DependentModel;
import com.hdfc.models.FileModel;
import com.hdfc.models.ProviderModel;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by balamurugan@adstringo.in on 23-12-2015.
 */
public class Config {

    public static final String dbName = "newzeal";
    public static final String collectionServices = "service";
    public static final String collectionProvider = "provider";
    public static final String collectionCustomer = "customer";
    public static final String collectionActivity = "activity";
    public static final String collectionDependent = "dependent";


    public static final String string = Utils.getStringJni();

    public static final int iSdkVersion = Build.VERSION.SDK_INT;
    public static final int iAppVersion = BuildConfig.VERSION_CODE;
    public static final String strOs = "android";

    public static final String strAppId = "910502819263";

    public static final int START_CAMERA_REQUEST_CODE = 1;
    public static final int START_GALLERY_REQUEST_CODE = 2;
    public static final int CACHE_EXPIRE = 1;//In Minutes
    public static final int intWidth = 300, intHeight = 300;

    public static final String[] weekNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    public static final String[] months = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};
    public static final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public static final boolean isDebuggable = true;
    public static final boolean release = false;
    //Login specific //User Specific clear at logout or whenever needed
    public final static String strCustomerImageName = "provider_image";
    public static int intClientScreen = 2;
    public static int intRatingsScreen = 3;
    public static int intSimpleActivityScreen = 1;
    public static int intScreenWidth = 0;
    public static int intScreenHeight = 0;
    public static int intDashboardScreen = 1;
    public static ArrayList<FileModel> fileModels = new ArrayList<>();
    public static ArrayList<ActivityModel> activityModels = new ArrayList<>();

    public static ArrayList<String> dependentIds = new ArrayList<>();
    public static ArrayList<String> customerIds = new ArrayList<>();

    public static ArrayList<DependentModel> dependentModels = new ArrayList<>();

    public static ProviderModel providerModel = new ProviderModel();

    public static JSONObject jsonObject = null;
    public static int intSelectedMenu = 0;

    public static ArrayList<ClientModel> clientModels = new ArrayList<>();

    public static boolean boolIsLoggedIn = false;
}