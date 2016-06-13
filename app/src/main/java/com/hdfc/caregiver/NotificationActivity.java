package com.hdfc.caregiver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hdfc.adapters.NotificationAdapter;
import com.hdfc.app42service.StorageService;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Utils;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private static NotificationAdapter notificationAdapter;
    private static ProgressDialog progressDialog;
    private static RelativeLayout loadingPanel;
    private ListView listViewActivities;
    private Utils utils;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        listViewActivities = (ListView) findViewById(R.id.listViewActivity);
        TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
        listViewActivities.setEmptyView(emptyTextView);
        utils = new Utils(NotificationActivity.this);
        backButton = (Button) findViewById(R.id.buttonBack);
        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        notificationAdapter = new NotificationAdapter(NotificationActivity.this, Config.notificationModels);
        listViewActivities.setAdapter(notificationAdapter);

        loadNotifications();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        goBack();
    }

    private void goBack() {
        Config.intSelectedMenu = Config.intRatingsScreen;
        Intent intent = new Intent(NotificationActivity.this, DashboardActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshNotifications();
    }

    private void refreshNotifications() {
        //if (listViewActivities != null) {
        notificationAdapter.notifyDataSetChanged();
        //}
    }

    public void hideLoader() {
        refreshNotifications();
        loadingPanel.setVisibility(View.GONE);
    }

    public void loadNotifications() {

        if (utils.isConnectingToInternet()) {

            loadingPanel.setVisibility(View.VISIBLE);

            StorageService storageService = new StorageService(NotificationActivity.this);

            storageService.findDocsByKeyValue(Config.collectionNotification,
                    "user_id",
                    Config.providerModel.getStrProviderId(),
                    new AsyncApp42ServiceApi.App42StorageServiceListener() {

                        @Override
                        public void onDocumentInserted(Storage response) {
                        }

                        @Override
                        public void onUpdateDocSuccess(Storage response) {
                        }

                        @Override
                        public void onFindDocSuccess(Storage storage) {

                            if (storage != null) {

                                //Utils.log(storage.toString(), "not ");

                                if (storage.getJsonDocList().size() > 0) {

                                    ArrayList<Storage.JSONDocument> jsonDocList = storage.
                                            getJsonDocList();

                                    for (int i = 0; i < jsonDocList.size(); i++) {

                                        utils.createNotificationModel(jsonDocList.get(i).getDocId(),
                                                jsonDocList.get(i).getJsonDoc());
                                    }
                                }
                            }
                            hideLoader();
                        }

                        @Override
                        public void onInsertionFailed(App42Exception ex) {
                        }

                        @Override
                        public void onFindDocFailed(App42Exception ex) {

                            hideLoader();

                           /* if (ex != null) {
                                try {
                                       *//* JSONObject jsonObject = new JSONObject(ex.getMessage());
                                        JSONObject jsonObjectError = jsonObject.
                                                getJSONObject("app42Fault");
                                        String strMess = jsonObjectError.getString("details");

                                        toast(2, 2, strMess);*//*
                                    //toast(2, 2, _ctxt.getString(R.string.error));
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                            refreshNotifications();*/
                        }

                        @Override
                        public void onUpdateDocFailed(App42Exception ex) {
                        }
                    });

        } else {
            hideLoader();
        }
    }
}
