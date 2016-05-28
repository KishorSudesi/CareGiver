package com.hdfc.caregiver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.hdfc.adapters.NotifyAdapter;
import com.hdfc.libs.Utils;

public class Notify extends AppCompatActivity {

    public static ListView listViewActivities;
    public static NotifyAdapter notificationAdapter;
    private static TextView emptyTextView;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        listViewActivities = (ListView) findViewById(R.id.listViewActivity);
        emptyTextView = (TextView) findViewById(android.R.id.empty);
        utils = new Utils(Notify.this);
    }

    @Override
    public void onResume() {
        super.onResume();

        listViewActivities.setEmptyView(emptyTextView);
        //notificationAdapter = new NotificationAdapter(getContext(), staticNotificationModels);

    }
}
