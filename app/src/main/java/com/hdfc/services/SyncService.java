package com.hdfc.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.hdfc.config.CareGiver;
import com.hdfc.dbconfig.DbCon;
import com.hdfc.libs.AppUtils;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;

public class SyncService extends IntentService {

    //private PowerManager.WakeLock mWakeLock;

    //private static LocalBroadcastManager broadcaster;

    private static boolean isServiceRunning = false;

    private static Handler syncHandler;

    public SyncService() {
        super("SyncService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //broadcaster = LocalBroadcastManager.getInstance(this);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        isServiceRunning = true;

       /* PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SyncService");
        mWakeLock.acquire();*/

        if (CareGiver.getDbCon() == null) {
            syncHandler = new SyncHandler();

            Thread syncThread = new SyncThread();
            syncThread.start();
        } else {
            callServices();
        }
    }

    private void callServices() {

        try {
        /*mWakeLock.release();*/

            SessionManager sessionManager = new SessionManager(SyncService.this);
            //Utils utils = new Utils(this);

            if (Utils.isConnectingToInternet(SyncService.this)) {

                if (sessionManager.getProfileImage() != null
                        && !sessionManager.getProfileImage().equalsIgnoreCase("")
                        && !sessionManager.getProfileImage().equalsIgnoreCase("N")) {
                    //upload profile Imaage if changed
                    AppUtils.checkImage(SyncService.this, sessionManager);
                }

                //update data
                AppUtils.updateAllDocs(SyncService.this, sessionManager);
            }

            isServiceRunning = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);

        return START_STICKY;
    }

    /**
     * In onDestroy() we release our wake lock. This ensures that whenever the
     * Service stops (killed for resources, stopSelf() called, etc.), the wake
     * lock will be released.
     */
    public void onDestroy() {
        super.onDestroy();

        /*if(!isServiceRunning){
            mWakeLock.release();
        }*/
    }

    private class SyncThread extends Thread {
        @Override
        public void run() {
            try {
                CareGiver.setDbCon(new DbCon(getApplicationContext()));
                syncHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //

    private class SyncHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            callServices();
        }
    }
}