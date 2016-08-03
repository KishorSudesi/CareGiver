package com.hdfc.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.hdfc.services.SyncService;


public class BootReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        bootService(context);
    }

    private void bootService(Context context) {

        //todo if timely fetch needed
        //save last run to share pref and compare

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent serviceIntent = new Intent(context, SyncService.class);

        int frequency = 10;//get last run and add time frame

        serviceIntent.putExtra("SOURCE", "BOOT");

        PendingIntent pendingServiceIntent = PendingIntent.getService(context, 0, serviceIntent, 0);
        am.cancel(pendingServiceIntent);

        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + frequency, frequency
                , pendingServiceIntent);
    }
}
