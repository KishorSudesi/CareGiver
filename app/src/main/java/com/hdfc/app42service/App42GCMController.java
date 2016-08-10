package com.hdfc.app42service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Response;

/**
 * Created by Admin on 1/22/2016.
 */
public class App42GCMController {

    /*private static final int PlayServiceResolutionRequest = 9000;
    private static final String Tag = "App42PushNotification";
    public static final String KeyRegId = "registration_id";
    private static final String KeyAppVersion = "appVersion";
    private static final String PrefKey = "App42PushSample";
    private static final String KeyRegisteredOnApp42 = "app42_register";
*/
    public App42GCMController() {
    }

    public static boolean isPlayServiceAvailable(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != 0) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, 9000).show();
            } else {
                Log.i("App42PushNotification", "This device is not supported.");
            }

            return false;
        } else {
            return true;
        }
    }

    @SuppressLint({"NewApi"})
    public static String getRegistrationId(Context context) {
        SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString("registration_id", "");
        if (registrationId.isEmpty()) {
            //Log.i("App42PushNotification", "Registration not found.");
            return "";
        } else {
            int registeredVersion = prefs.getInt("appVersion", -2147483648);
            int currentVersion = getAppVersion(context);
            if (registeredVersion != currentVersion) {
                //Log.i("App42PushNotification", "App version changed.");
                return "";
            } else {
                return registrationId;
            }
        }
    }

    private static SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences("App42PushSample", 0);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo e = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return e.versionCode;
        } catch (PackageManager.NameNotFoundException var2) {
            throw new RuntimeException("Could not get package name: " + var2);
        }
    }

    public static void clearPref(Context context) {
        SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public static void storeRegistrationId(Context context, String regId) {
        SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        //Log.i("App42PushNotification", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("registration_id", regId);
        editor.putInt("appVersion", appVersion);
        editor.commit();
    }

    public static boolean isApp42Registerd(Context context) {
        return getGCMPreferences(context).getBoolean("app42_register", false);
    }

    public static void storeApp42Success(Context context) {
        SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("app42_register", true);
        editor.commit();
    }

    @SuppressLint({"NewApi"})
    public static void getRegistrationId(Context context, String googleProjectNo,
                                         App42GCMController.App42GCMListener callBack) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String regid = getRegistrationId(context);
        if (regid.isEmpty()) {
            registeronGCM(context, googleProjectNo, gcm, callBack);
        } else {
            callBack.onGCMRegistrationId(regid);
        }

    }

    public static void registeronGCM(Context context, final String googleProjectNo,
                                     final GoogleCloudMessaging gcm,
                                     final App42GCMController.App42GCMListener callback) {
        final Handler callingThreadHandler = new Handler();
        (new Thread() {
            public void run() {
                try {
                    final String ex = gcm.register(googleProjectNo);
                    callingThreadHandler.post(new Runnable() {
                        public void run() {
                            if (callback != null) {
                                callback.onGCMRegistrationId(ex);
                            }

                        }
                    });
                } catch (final Exception var2) {
                    callingThreadHandler.post(new Runnable() {
                        public void run() {
                            if (callback != null) {
                                callback.onError(var2.getMessage());
                            }

                        }
                    });
                }

            }
        }).start();
    }

    public static void registerOnApp42(String userName, String deviceToen, final App42GCMController.App42GCMListener callBack) {
        App42API.buildPushNotificationService().storeDeviceToken(userName, deviceToen, new App42CallBack() {
            public void onSuccess(Object arg0) {
                App42Response response = (App42Response) arg0;
                callBack.onRegisterApp42(response.getStrResponse());
            }

            public void onException(Exception arg0) {
                callBack.onApp42Response(arg0.getMessage());
            }
        });
    }

    public interface App42GCMListener {
        void onError(String var1);

        void onGCMRegistrationId(String var1);

        void onApp42Response(String var1);

        void onRegisterApp42(String var1);
    }
}
