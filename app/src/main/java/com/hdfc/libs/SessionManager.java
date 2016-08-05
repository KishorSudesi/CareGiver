package com.hdfc.libs;

import android.content.Context;
import android.content.SharedPreferences;

import com.hdfc.config.Config;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

/**
 * Created by Sudesi infotech on 6/23/2016.
 */
public class SessionManager {


    // Sharedpref file name
    private static final String PREF_NAME = "CARE_GIVER";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "LOGGED_IN";
    // Email address (make variable public to access from outside)
    private static final String KEY_EMAIL = "EMAIL";
    // customer id (make variable public to access from outside)
    private static final String KEY_PROVIDER_ID = "PROVIDER_ID";
    private static final String KEY_PROFILE_IMAGE = "PROFILE_IMAGE";
    private static final String KEY_CLIENT_DATE = "CLIENT_DATE";
    private static final String KEY_ACTIVITY_SYNC = "ACTIVITY_SYNC";
    private static final String KEY_SYNC_DATE = "SYNC_DATE";
    private static final String KEY_DEVICE_TOKEN = "DEVICE_TOKEN";
    //private final String KEY_CHECKIN_CARE_STATUS = "checkin_care_status";
    // Shared Preferences
    private SharedPreferences pref;
    // Editor for Shared preferences
    private SharedPreferences.Editor editor;
    // Shared pref mode
    private int PRIVATE_MODE = 0;


    // Constructor
    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String email, String strProviderId) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        try {
            // Storing email in pref
            editor.putString(KEY_EMAIL, AESCrypt.encrypt(Config.string, email));

            //storing provider id in pref
            editor.putString(KEY_PROVIDER_ID, AESCrypt.encrypt(Config.string, strProviderId));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        // commit changes
        editor.commit();
    }


    public void saveProfileImage(String strPath) {
        try {

            editor.putString(KEY_PROFILE_IMAGE, AESCrypt.encrypt(Config.string, strPath));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        // commit changes
        editor.commit();
    }

    void saveClientDate(String strDate) {
        try {

            editor.putString(KEY_CLIENT_DATE, AESCrypt.encrypt(Config.string, strDate));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        // commit changes
        editor.commit();
    }

    String getClientDate() {

        String strClientDate;

        try {
            strClientDate = AESCrypt.decrypt(Config.string, pref.getString(KEY_CLIENT_DATE, ""));
        } catch (Exception e) {
            e.printStackTrace();
            strClientDate = "";
        }
        return strClientDate;
    }

    public void saveSyncDate(String strDate) {
        try {
            editor.putString(KEY_SYNC_DATE, AESCrypt.encrypt(Config.string, strDate));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        // commit changes
        editor.commit();
    }

    public String getSyncDate() {

        String strClientDate = "";

        try {
            String strTemp = pref.getString(KEY_SYNC_DATE, "");

            if (!strTemp.equalsIgnoreCase(""))
                strClientDate = AESCrypt.decrypt(Config.string, strTemp);
        } catch (Exception e) {
            e.printStackTrace();
            strClientDate = "";
        }
        return strClientDate;
    }

    boolean getActivitySync() {

        boolean b = false;

        try {
            b = pref.getBoolean(KEY_ACTIVITY_SYNC, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    void setActivitySync(boolean b) {
        try {

            editor.putBoolean(KEY_ACTIVITY_SYNC, b);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // commit changes
        editor.commit();
    }

    public String getDeviceToken() {

        String strDeviceToken;

        try {
            strDeviceToken = pref.getString(KEY_DEVICE_TOKEN, "");
        } catch (Exception e) {
            e.printStackTrace();
            strDeviceToken = "";
        }
        return strDeviceToken;
    }

    public void setDeviceToken(String strToken) {
        try {

            editor.putString(KEY_DEVICE_TOKEN, strToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // commit changes
        editor.commit();
    }

    public String getProfileImage() {

        String strProfileImage = "";

        try {
            strProfileImage = AESCrypt.decrypt(Config.string, pref.getString(KEY_PROFILE_IMAGE, ""));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return strProfileImage;
    }

    public String getEmail() {

        String strEmail = "";

        try {
            strEmail = AESCrypt.decrypt(Config.string, pref.getString(KEY_EMAIL, ""));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return strEmail;
    }

    /**
     * Get stored session data
     *//*
    public boolean getCheckInCareStatus() {
        // return customer id
        return pref.getBoolean(KEY_CHECKIN_CARE_STATUS, false);
    }*/

    public String getProviderId() {

        String strProviderId = "";

        try {
            strProviderId = AESCrypt.decrypt(Config.string, pref.getString(KEY_PROVIDER_ID, ""));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return strProviderId;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
