package com.hdfc.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

import com.hdfc.caregiver.DashboardActivity;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbCon;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;

public class SyncService extends IntentService {

    //private PowerManager.WakeLock mWakeLock;

    private static LocalBroadcastManager broadcaster;

    private static boolean isServiceRunning = false;

    private static Handler dbHandler, syncHandler;

    public SyncService() {
        super("SyncService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

       /* PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SyncService");
        mWakeLock.acquire();*/

        isServiceRunning = true;

        if (CareGiver.getDbCon() == null) {
            dbHandler = new DbHandler();

            Thread dbThread = new DbThread();
            dbThread.start();
        } else {
            callServices();
        }
    }

    private void sendResult() {

         /*mWakeLock.release();*/

        if (DashboardActivity.isRunning && broadcaster != null) {
            Intent intent = new Intent(Config.SERVICE_RESULT);
            intent.putExtra(Config.SERVICE_MESSAGE, Config.SERVICE_RESULT_VALUE);
            broadcaster.sendBroadcast(intent);
        }
    }

    private void callServices() {

        syncHandler = new SyncHandler();

        Thread syncThread = new SyncThread();
        syncThread.start();

        Utils.log(" THREAD ", " Services ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);

        return START_NOT_STICKY; //START_NOT_STICKY START_STICKY
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

    private class DbThread extends Thread {
        @Override
        public void run() {
            try {
                CareGiver.setDbCon(new DbCon(getApplicationContext()));
                dbHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class DbHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Utils.log(" DB ", " 1 ");
                callServices();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class SyncThread extends Thread {
        @Override
        public void run() {
            try {
                SessionManager sessionManager = new SessionManager(SyncService.this);

                if (Utils.isConnectingToInternet(SyncService.this)) {
                    if (sessionManager.getProfileImage() != null
                            && !sessionManager.getProfileImage().equalsIgnoreCase("")
                            && !sessionManager.getProfileImage().equalsIgnoreCase("N")) {
                        //upload profile Imaage if changed
                        //AppUtils.checkImage(SyncService.this, sessionManager);
                    }

                    //update data
                    //AppUtils.updateAllDocs(SyncService.this, sessionManager);
                }
                //stopSelf(); no need to call

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
            try {
                isServiceRunning = false;
                sendResult();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}