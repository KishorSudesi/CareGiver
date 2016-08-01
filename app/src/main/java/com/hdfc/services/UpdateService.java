package com.hdfc.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;

/**
 * Created by Sudesi infotech on 7/4/2016.
 */
public class UpdateService extends Service {
    private SessionManager sessionManager = null;
    private Utils utils;
    private Handler handler;
    private boolean updateAll = false;

    @Override
    public void onCreate() {
        super.onCreate();
        utils = new Utils(UpdateService.this);
        sessionManager = new SessionManager(UpdateService.this);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        try {
            new UpdateTask().execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void dismissDialog() {
        try {
            Log.i("TAG", "In dissmiss dialog");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class UpdateTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
