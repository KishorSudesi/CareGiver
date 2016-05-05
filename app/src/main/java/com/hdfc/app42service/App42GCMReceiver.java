package com.hdfc.app42service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Admin on 1/22/2016.
 */
public class App42GCMReceiver extends WakefulBroadcastReceiver {
    public App42GCMReceiver() {

    }

    public void onReceive(Context context, Intent intent) {
        //SharedPreferences sPre = context.getSharedPreferences("NEWZEAL", context.MODE_PRIVATE);
        //Log.e("App42GCMReceiver",String.valueOf(sPre.getBoolean("LOGGED_IN", false)));
        //if(sPre.getBoolean("LOGGED_IN",false)) {
        ComponentName comp = new ComponentName(context.getPackageName(), App42GCMService.class.getName());
        //Log.e("MESSAGE RECEIVED : ", comp.toString());
        startWakefulService(context, intent.setComponent(comp));
        this.setResultCode(Activity.RESULT_OK);
        //}
    }
}
