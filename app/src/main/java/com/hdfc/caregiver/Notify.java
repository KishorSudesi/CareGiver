package com.hdfc.caregiver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hdfc.adapters.NotifyAdapter;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.models.NotificationModel;

import java.util.ArrayList;

public class Notify extends AppCompatActivity {

    public static ListView listViewActivities;
    public static NotifyAdapter notificationAdapter;
    private static TextView emptyTextView;
    public static LinearLayout dynamicUserTab;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        listViewActivities = (ListView) findViewById(R.id.listViewActivity);
        emptyTextView = (TextView) findViewById(android.R.id.empty);
        dynamicUserTab = (LinearLayout)findViewById(R.id.dynamicUserTab);
        utils = new Utils(Notify.this);
       // ArrayList<NotificationModel> notificationModels = new ArrayList<>();
        //notificationAdapter = new NotifyAdapter();

    }

    @Override
    public void onResume() {
        super.onResume();

        listViewActivities.setEmptyView(emptyTextView);
        utils.populateHeaderDependents(dynamicUserTab, Config.intNotificationScreen);
        //notificationAdapter = new NotificationAdapter(getContext(), staticNotificationModels);

    }
}
