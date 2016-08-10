package com.hdfc.app42service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hdfc.caregiver.DashboardActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

/**
 * Created by Admin on 1/22/2016.
 */
public class App42GCMService extends IntentService {


    public static final String DisplayMessageAction = "com.hdfc.caregiver.DashboardActivity";
    public static final String ExtraMessage = "message";
    private static final String App42GeoTag = "app42_geoBase";
    private static final String Alert = "alert";
    private final static String GROUP_KEY = "activity";
    private static int msgCount = 0;

    public App42GCMService() {
        super("GcmIntentService");
    }

    /*public static void unRegisterGcm() {
        try {
            if (gcm != null)
                gcm.unregister();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {
            if ("send_error".equals(messageType)) {
                //App42Log.debug("Send error: " + extras.toString());
                App42GCMReceiver.completeWakefulIntent(intent);
            } else if ("deleted_messages".equals(messageType)) {
                //App42Log.debug("Deleted messages on server: " + extras.toString());
                App42GCMReceiver.completeWakefulIntent(intent);
            } else if ("gcm".equals(messageType)) {
                String message = intent.getExtras().getString("message");
                //App42Log.debug("Received: " + extras.toString());
                //App42Log.debug("Message: " + message);
                //AppUtils.loadNotifications(this);

                //if login
                SessionManager sessionManager = new SessionManager(App42GCMService.this);
                if (sessionManager.getProviderId() != null
                        && !sessionManager.getProviderId().equalsIgnoreCase("")) {
                    this.validatePushIfRequired(message, intent);
                }
            }
        }

    }

    private void showNotification(String message, Intent intent, String strClientId) {
       /* DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        Date date = Calendar.getInstance().getTime();
        date.getTime();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss", Locale.US);
        String dt = dateTime.format(date);
        db.createNotification(title, message, dt);
        db.close();*/
        this.broadCastMessage(message);
        this.sendNotification(message, strClientId);
        App42GCMReceiver.completeWakefulIntent(intent);
    }

    //
    private void validatePushIfRequired(String message, final Intent intent) {

        try {

            final JSONObject jsonObject = new JSONObject(message);

            if (jsonObject.has("created_by")) {

                String strMessage = null;
                String strCustomerId = "";
                try {
                    strMessage = jsonObject.getString(App42GCMService.ExtraMessage)
                            + "\n" +
                            " Created On: "
                            + Utils.writeFormat.format(Utils.readFormat.parse(
                            jsonObject.getString("time")));

                    //group by user
                    if (jsonObject.has("created_by") &&
                            !jsonObject.optString("created_by").equalsIgnoreCase("")) {
                        strCustomerId = jsonObject.optString("created_by");
                    }
                  /*  String values[] = {"",
                            "",
                            jsonObject.toString(), Config.collectionNotification,
                            "1", ""};

                    CareGiver.getDbCon().insert(DbHelper.strTableNameCollection,
                            values,
                            DbHelper.COLLECTION_FIELDS);*/

                    /*AppUtils appUtils = new AppUtils(this);

                    appUtils.fetchActivitiesSync(this);
*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showNotification(strMessage, intent, strCustomerId);

            } else {

                final String geoBaseType = jsonObject.optString(App42GeoTag, null);

                /*if (geoBaseType == null) {
                    showNotification(json.toString(),intent);
                }*/

                final String alertMessage = jsonObject.optString(Alert, null);

                if (alertMessage != null) {
                    showNotification(jsonObject.getString("alert"), intent, "");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showNotification(message, intent, "");
        }
    }

    private void sendNotification(String msg, String strClientId) {

        long when = System.currentTimeMillis();

        NotificationManager mNotificationManager = (NotificationManager) this.
                getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent;

        notificationIntent = new Intent(this, DashboardActivity.class);

        notificationIntent.putExtra("message_delivered", true);
        notificationIntent.putExtra(ExtraMessage, msg);
        notificationIntent.putExtra("LOAD", false);

        if (CareGiver.getDbCon() == null)
            Config.intSelectedMenu = Config.intNotificationScreen;// to load on app kill

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //notificationIntent.setFlags(603979776);

        //
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(DashboardActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(notificationIntent);
        //

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getString(R.string.app_name)).
                setContentText(msg).setWhen(when).setNumber(++msgCount)
                .setDefaults(1).setDefaults(2)
                .setLights(Notification.DEFAULT_LIGHTS, 5000, 5000)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setAutoCancel(true)
                .setGroup(GROUP_KEY) //GROUP_KEY strClientId
                .setVisibility(VISIBILITY_PUBLIC)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        //String[] events = msg.split(""); //new String[6]
        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle(getString(R.string.app_name));

        // Moves events into the expanded layout
       /* for (String event : events) {
            inboxStyle.addLine(event);
        }*/
        // Moves the expanded layout object into the notification object.
        inboxStyle.addLine(msg);
        mBuilder.setStyle(inboxStyle);

        mBuilder.setContentIntent(contentIntent);

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        mNotificationManager.notify(m, mBuilder.build());

        //sumamry notification
       /* Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_large_icon);

        NotificationCompat.WearableExtender wearableExtender =
            new NotificationCompat.WearableExtender()
            .setBackground(background);


        // Create an InboxStyle notification
        Notification summaryNotification = new NotificationCompat.Builder(mContext)
                .setContentTitle("2 new messages")
                .setSmallIcon(R.drawable.ic_small_icon)
                .setLargeIcon(largeIcon)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine("Alex Faaborg   Check this out")
                        .addLine("Jeff Chang   Launch Party")
                        .setBigContentTitle("2 new messages")
                        .setSummaryText("johndoe@gmail.com"))
                        .extend(wearableExtender)
                .setGroup(GROUP_KEY_EMAILS)
                .setGroupSummary(true)
                .build();

        notificationManager.notify(notificationId3, summaryNotification);*/
    }

    /**
     * @param message
     */
    private void broadCastMessage(String message) {
        Intent intent = new Intent(DisplayMessageAction);
        intent.putExtra(ExtraMessage, message);
        this.sendBroadcast(intent);
    }
}
