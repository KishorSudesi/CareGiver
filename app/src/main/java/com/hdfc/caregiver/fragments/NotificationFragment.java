package com.hdfc.caregiver.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hdfc.adapters.NotificationAdapter;
import com.hdfc.app42service.StorageService;
import com.hdfc.caregiver.FeatureActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONException;

import java.util.ArrayList;


public class NotificationFragment extends Fragment {

    private static NotificationAdapter notificationAdapter;
    private static ProgressDialog progressDialog;
    private static RelativeLayout loadingPanel;
    private ListView listViewActivities;
    private Utils utils;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        utils = new Utils(getActivity());
        loadingPanel = (RelativeLayout) view.findViewById(R.id.loadingPanel);

        notificationAdapter = new NotificationAdapter(getActivity(), Config.notificationModels);
        listViewActivities.setAdapter(notificationAdapter);

        listViewActivities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                findActivities(Config.notificationModels.get(position).getStrActivityId());
            }
        });

        loadNotifications();

        return view;
    }

    public void loadNotifications() {

        if (utils.isConnectingToInternet()) {

            loadingPanel.setVisibility(View.VISIBLE);

            StorageService storageService = new StorageService(getContext());

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

    public void hideLoader() {
        refreshNotifications();
        loadingPanel.setVisibility(View.GONE);
    }

    private void refreshNotifications() {
        notificationAdapter.notifyDataSetChanged();
    }

    public void findActivities(String strActivityId){

      //  System.out.println("Here is activity id : "+strActivityId);

        int iPosition = Config.strActivityIds.indexOf(strActivityId);
        /*for(int i =0 ; i<Config.strActivityIds.size(); i++) {
            System.out.println("Rushikesh Mohan Belavalekar : " + Config.strActivityIds);
        }*/

        //System.out.println("Here is positin : "+iPosition);

        ActivityModel activityModel;

        if(iPosition>-1) {
            activityModel = Config.activityModels.get(iPosition);

            Bundle args = new Bundle();
            //
            Intent intent = new Intent(getActivity(), FeatureActivity.class);
            args.putSerializable("ACTIVITY", activityModel);
            intent.putExtras(args);
            startActivity(intent);
        }else {
            /*StorageService storageService = new StorageService(getContext());

            storageService.findDocsByKeyValue(Config.collectionNotification, "activity_id",
                    Config.activityModels.get(iPosition).getStrActivityID(), new AsyncApp42ServiceApi.App42StorageServiceListener() {
                        @Override
                        public void onDocumentInserted(Storage response) {

                        }

                        @Override
                        public void onUpdateDocSuccess(Storage response) {

                        }

                        @Override
                        public void onFindDocSuccess(Storage storage) throws JSONException {

                            if (storage != null) {

                                if (storage.getJsonDocList().size() > 0) {

                                    ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();

                               *//* for (int i = 0; i < jsonDocList.size(); i++) {
                                    utils.createNotificationModel(jsonDocList.get(i).getDocId(), jsonDocList.get(i).getJsonDoc());


                                }*//*
                                }
                            }
                        }

                        @Override
                        public void onInsertionFailed(App42Exception ex) {

                        }

                        @Override
                        public void onFindDocFailed(App42Exception ex) {

                        }

                        @Override
                        public void onUpdateDocFailed(App42Exception ex) {

                        }
                    });*/
        }
    }
}
