package com.hdfc.caregiver.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hdfc.adapters.NotificationAdapter;
import com.hdfc.app42service.App42GCMService;
import com.hdfc.app42service.StorageService;
import com.hdfc.caregiver.FeatureActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.AppUtils;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class NotificationFragment extends Fragment {

    public static NotificationAdapter notificationAdapter;
    public static ListView listViewActivities;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent
                    .getStringExtra(App42GCMService.ExtraMessage);

            if (message != null && !message.equalsIgnoreCase("")) {
                showPushDialog(message);

                AppUtils.fetchActivitiesSync(getActivity());
                AppUtils.loadNotifications(getActivity());
                //todo optional refersh
                AppUtils.refreshProvider(getActivity());

                //todo optional fetch check in cares,services
                AppUtils.fetchCheckInCareSync(getActivity());
                AppUtils.fetchServicesSync(getActivity());
            }
        }
    };
    //private static ProgressDialog progressDialog;
    private RelativeLayout loadingPanel;
    private Utils utils;
    private AppUtils appUtils;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance(boolean b) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putBoolean("RELOAD", b);
        fragment.setArguments(args);
        return fragment;
    }

    private void showPushDialog(final String strMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(strMessage);
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                refreshNotifications();
            }
        });
        builder.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(
                App42GCMService.DisplayMessageAction);
        filter.setPriority(2);
        getActivity().registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        listViewActivities = (ListView) view.findViewById(R.id.listViewActivity);
        TextView emptyTextView = (TextView) view.findViewById(android.R.id.empty);
        listViewActivities.setEmptyView(emptyTextView);

        loadingPanel = (RelativeLayout) getActivity().findViewById(R.id.loadingPanel);

        utils = new Utils(getActivity());
        appUtils = new AppUtils(getActivity());
     //   loadingPanel = (RelativeLayout) view.findViewById(R.id.loadingPanel);

        Bundle bundle = this.getArguments();
        boolean b = bundle.getBoolean("RELOAD", false);

        loadingPanel.setVisibility(View.VISIBLE);
        AppUtils.createNotificationModel();
        loadingPanel.setVisibility(View.GONE);

        notificationAdapter = new
                NotificationAdapter(getActivity(), Config.notificationModels);
        listViewActivities.setAdapter(notificationAdapter);

        listViewActivities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                findActivities(Config.notificationModels.get(position).getStrActivityId());
            }
        });

        if (Config.notificationModels.size() <= 0 || b) {
            loadNotifications();
        }

        return view;
    }

    private void loadNotifications() {

        if (utils.isConnectingToInternet()) {

            loadingPanel.setVisibility(View.VISIBLE);

            String strDate = "";

            Cursor cursor = CareGiver.getDbCon().getMaxDate(Config.collectionNotification);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                strDate = cursor.getString(0);
            }

            if (strDate == null || strDate.equalsIgnoreCase(""))
                strDate = DbHelper.DEFAULT_DB_DATE;

            CareGiver.getDbCon().closeCursor(cursor);

            StorageService storageService = new StorageService(getContext());

            Query q1 = QueryBuilder.build("user_id", Config.providerModel.getStrProviderId(),
                    QueryBuilder.Operator.EQUALS);

            Query finalQuery;

            if (!strDate.equalsIgnoreCase("")) {
                Query q12 = QueryBuilder.build("_$updatedAt", strDate,
                        QueryBuilder.Operator.GREATER_THAN);

                finalQuery = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q12);
            } else {
                finalQuery = q1;
            }


            storageService.findDocsByQueryOrderBy(Config.collectionNotification, finalQuery, 30000,
                    0, "time", 1, new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {
                            if (o != null) {

                                Storage storage = (Storage) o;

                                Utils.log(storage.toString(), "not ");

                                if (storage.getJsonDocList().size() > 0) {

                                    ArrayList<Storage.JSONDocument> jsonDocList = storage.
                                            getJsonDocList();

                                    try {

                                        CareGiver.getDbCon().beginDBTransaction();

                                        for (int i = 0; i < jsonDocList.size(); i++) {

                                            String values[] = {jsonDocList.get(i).getDocId(),
                                                    jsonDocList.get(i).getUpdatedAt(),
                                                    jsonDocList.get(i).getJsonDoc(),
                                                    Config.collectionNotification, "1", "", "1"};

                                            Utils.log(" 1 ", " 2 ");
                                            CareGiver.getDbCon().insert(
                                                    DbHelper.strTableNameCollection,
                                                    values,
                                                    DbHelper.COLLECTION_FIELDS
                                            );

                                        }
                                        CareGiver.getDbCon().dbTransactionSuccessFull();
                                        //CareGiver.getDbCon().deleteDuplicateNotifications();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        CareGiver.getDbCon().endDBTransaction();
                                    }
                                }
                                refreshNotifications();
                            } else {
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                            loadingPanel.setVisibility(View.GONE);
                        }

                        @Override
                        public void onException(Exception e) {

                            loadingPanel.setVisibility(View.GONE);

                            if (e == null)
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            else
                                utils.toast(1, 1, getString(R.string.no_new_notifications));

                        }
                    });
        } else {
            refreshNotifications();
        }
    }

    private void refreshNotifications() {
        AppUtils.createNotificationModel();
        notificationAdapter.notifyDataSetChanged();
        listViewActivities.scheduleLayoutAnimation();
    }

    private void findActivities(final String strActivityId) {

        try {

            boolean isLoaded = true;

            Cursor cursor = CareGiver.getDbCon().fetch(
                    DbHelper.strTableNameCollection,
                    new String[]{DbHelper.COLUMN_DOCUMENT},
                    DbHelper.COLUMN_COLLECTION_NAME + "=? and " + DbHelper.COLUMN_OBJECT_ID + "=? ",
                    new String[]{Config.collectionActivity, strActivityId},
                    DbHelper.COLUMN_UPDATE_DATE + " desc",
                    null, true, null, null);

            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                JSONObject jsonObject = new JSONObject(cursor.getString(0));
                ActivityModel activityModel = appUtils.createActivityModelNotification(jsonObject,
                        strActivityId);


                if (activityModel != null) {
                    Bundle args = new Bundle();
                    //
                    Intent intent = new Intent(getActivity(), FeatureActivity.class);
                    args.putSerializable("ACTIVITY", activityModel);
                    args.putBoolean("WHICH_SCREEN", true);
                    intent.putExtras(args);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    isLoaded = false;
                }
            } else {
                isLoaded = false;
            }

            if (!isLoaded) {
                utils.toast(2, 2, getString(R.string.sync_data_activity));
            }

            CareGiver.getDbCon().closeCursor(cursor);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
