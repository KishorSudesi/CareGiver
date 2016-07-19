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
    public final String KEY_CHECKIN_CARE_STATUS = "checkin_care_status";
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
     */
    public boolean getCheckInCareStatus() {


        // return customer id
        return pref.getBoolean(KEY_CHECKIN_CARE_STATUS, false);
    }

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
