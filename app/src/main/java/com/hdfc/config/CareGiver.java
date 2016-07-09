package com.hdfc.config;

import android.app.Application;
import android.content.res.Configuration;

import com.hdfc.dbconfig.DbCon;

/**
 * Created by balamurugan@adstringo.in on 02-01-2016.
 */
public class CareGiver extends Application {

    private static DbCon dbCon = null;

    //private static CareGiver careGiver;

    public static DbCon getDbCon() {
        return dbCon;
    }

   /* public static Context getContext() {
        return context;
    }*/

    public static void setDbCon(DbCon _dbCon) {
        dbCon = _dbCon;
    }

    public void onCreate() {
        super.onCreate();
        //careGiver=this;
        //CareGiver.context = getApplicationContext();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

       /* Intent i = new Intent(YourApplication.getInstance(), StartAcitivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent intent = PendingIntent.getActivity(YourApplication.getInstance().getBaseContext(), 0,  i, Intent.FLAG_ACTIVITY_CLEAR_TOP);

        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, intent);*/
    }
}
