package com.hdfc.config;

import android.os.Build;

import com.hdfc.caregiver.BuildConfig;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.CheckInCareModel;
import com.hdfc.models.ClientModel;
import com.hdfc.models.ClientNameModel;
import com.hdfc.models.CustomerModel;
import com.hdfc.models.DependentModel;
import com.hdfc.models.FeedBackModel;
import com.hdfc.models.NotificationModel;
import com.hdfc.models.PictureModel;
import com.hdfc.models.ProviderModel;
import com.hdfc.models.ServiceModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by balamurugan@adstringo.in on 23-12-2015.
 */
public class Config {

    public static final String dbName = "newzeal";

    public static final String collectionService = "service";
    public static final String collectionServiceHistory = "servicehistory";
    public static final String collectionServiceCustomer = "servicecustomer";
    public static final String collectionServices = "service";
    public static final String collectionProvider = "provider";
    public static final String collectionProviderDependent = "providerdependent";
    public static final String collectionCustomer = "customer";
    public static final String collectionActivity = "activity";
    public static final String collectionDependent = "dependent";
    public static final String collectionNotification = "notification";
    public static final String collectionLoginLog = "login_log";
    public static final String collectionCheckInCare = "checkincare";

    public static final int iSdkVersion = Build.VERSION.SDK_INT;
    public static final int iAppVersion = BuildConfig.VERSION_CODE;

    public static final String strOs = "android";
    public static final String strAppId = "910502819263";

    public static final int START_CAMERA_REQUEST_CODE = 1;
    public static final int START_GALLERY_REQUEST_CODE = 2;
    //public static final int CACHE_EXPIRE = 1;//In Minutes
    public static final int intWidth = 300, intHeight = 300;
    //todo fix the dimension and quality
    public static final int intCompressWidth = 300, intCompressHeight = 300, iQuality = 70;
    /* public static final String[] weekNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};*/
    /* public static final String[] months = {"January", "February", "March", "April", "May", "June",
             "July", "August", "September", "October", "November", "December"};*/
    /*public static final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};*/
    public static final boolean isDebuggable = true;
    public static final String string = Utils.getStringJni();
    public static final boolean release = false;
    public static final boolean DEVELOPMENT = true;
    //Login specific //User Specific clear at logout or whenever needed
    public final static String strCustomerImageName = "provider_image";
    public static int intClientScreen = 2;
    public static int intRatingsScreen = 4;
    public static int intNotificationScreen = 3;
    public static int intDashboardScreen = 1;
    //public static int intMileStoneScreen = 4;
    public static int intScreenWidth = 0;
    public static int intScreenHeight = 0;
    //public static int intCreateTaskScreen = 5;
    //public static ArrayList<FileModel> fileModels = new ArrayList<>();

    public static ArrayList<ActivityModel> activityModels = new ArrayList<>();
    //public static ArrayList<ActivityModel> activityModelsNotifications = new ArrayList<>();

    // static ArrayList<MilestoneViewModel> milestoneModels = new ArrayList<>();
    public static ArrayList<String> dependentIds = new ArrayList<>();
    //public static ArrayList<String> strProviderIds = new ArrayList<>();
    public static ArrayList<String> customerIds = new ArrayList<>();
    public static ArrayList<String> customerIdsCopy = new ArrayList<>();

    public static ArrayList<String> strActivityIds = new ArrayList<>();
    //public static ArrayList<String> strActivityIdsNotifications = new ArrayList<>();

    public static ArrayList<String> dependentIdsAdded = new ArrayList<>();
    public static ArrayList<String> customerIdsAdded = new ArrayList<>();
    public static ArrayList<DependentModel> dependentModels = new ArrayList<>();
    public static ArrayList<CustomerModel> customerModels = new ArrayList<>();
    public static ProviderModel providerModel = null;
    public static CustomerModel customerModel = null;
    public static DependentModel dependentModel = null;
    public static CheckInCareModel checkInCareModel = null;
    public static JSONObject jsonObject = null;
    public static int intSelectedMenu = 0;

    public static ArrayList<ClientModel> clientModels = new ArrayList<>();

    public static ArrayList<NotificationModel> notificationModels = new ArrayList<>();
    //public static ArrayList<String> strNotificationIds = new ArrayList<>();

    public static ArrayList<CheckInCareModel> checkInCareModels = new ArrayList<>();
    //public static ArrayList<CheckInCareModel> checkInCareActivityNames = new ArrayList<>();
    public static ArrayList<PictureModel> roomtypeName = new ArrayList<>();

    public static ArrayList<ClientNameModel> clientNameModels = new ArrayList<>();
    public static Map<String, List<String>> serviceNameModels = new HashMap<>();
    //public static boolean boolIsLoggedIn = false;
    public static double iRatings = 0;
    public static int iRatingCount = 0;
    public static List<FeedBackModel> feedBackModels = new ArrayList<>();

    public static ArrayList<String> strServcieIds = new ArrayList<>();
    public static ArrayList<ServiceModel> serviceModels = new ArrayList<>();
    public static ArrayList<String> servicelist = new ArrayList<>();
    public static ArrayList<String> serviceCategorylist = new ArrayList<>();

    public static ArrayList<String> strDependentNames = new ArrayList<>();
    public static ArrayList<String> strCustomerNames = new ArrayList<>();

    public enum ActivityStatus {NEW, OPEN, INPROCESS, COMPLETED}

    public enum MilestoneStatus {INACTIVE, OPENED, INPROCESS, COMPLETED, REOPENED, PENDING}
}