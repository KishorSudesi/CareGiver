package com.hdfc.config;

import android.app.Application;

import com.hdfc.dbconfig.DbCon;

/**
 * Created by balamurugan@adstringo.in on 02-01-2016.
 */
public class CareGiver extends Application {

    public static DbCon dbCon = null;

    //private static Context context;

    public void onCreate() {
        super.onCreate();
        //CareGiver.context = getApplicationContext();
    }

   /* public static Context getContext() {
        return context;
    }*/
}
