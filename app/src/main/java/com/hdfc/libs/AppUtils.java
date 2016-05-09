package com.hdfc.libs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.hdfc.app42service.StorageService;
import com.hdfc.caregiver.DashboardActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.Config;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.ClientModel;
import com.hdfc.models.CustomerModel;
import com.hdfc.models.DependentModel;
import com.hdfc.models.FeedBackModel;
import com.hdfc.models.FileModel;
import com.hdfc.models.ImageModel;
import com.hdfc.models.ProviderModel;
import com.hdfc.models.VideoModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Admin on 4/25/2016.
 */
public class AppUtils {
    private static Context _ctxt;
    private static StorageService storageService;
    private static ProgressDialog progressDialog;
    Utils utils;


    public AppUtils(Context context) {
        _ctxt = context;
        utils = new Utils(_ctxt);
        progressDialog = new ProgressDialog(_ctxt);
        storageService = new StorageService(_ctxt);
    }

    /*public static void logout() {
        try {
            Config.jsonObject = null;
            Config.jsonServer = null;
            Config.jsonDocId = "";

            Config.intSelectedMenu = 0;

            Config.intDependantsCount = 0;
            Config.boolIsLoggedIn = false;
            Config.strUserName = "";
            Config.fileModels.clear();

            //todo clear shared pref.

            File fileImage = createFileInternal("images/");
            deleteAllFiles(fileImage);

            Intent dashboardIntent = new Intent(_ctxt, LoginActivity.class);
            _ctxt.startActivity(dashboardIntent);
            ((Activity) _ctxt).finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    public void fetchProviders(final ProgressDialog progressDialog, final String strUserName) {

        storageService.findDocsByKeyValue(Config.collectionProvider, "provider_email", strUserName,
                new AsyncApp42ServiceApi.App42StorageServiceListener() {
                    @Override
                    public void onDocumentInserted(Storage response) {
                    }

                    @Override
                    public void onUpdateDocSuccess(Storage response) {
                    }

                    @Override
                    public void onFindDocSuccess(Storage response) {
                        try {
                            if (response != null) {

                                if (response.getJsonDocList().size() > 0) {

                                    Storage.JSONDocument jsonDocument = response.getJsonDocList().
                                            get(0);
                                    String strDocument = jsonDocument.getJsonDoc();
                                    String strProviderId = jsonDocument.getDocId();

                                    createProviderModel(strDocument, strProviderId);

                                    fetchActivities(progressDialog);
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    Utils.toast(2, 2, _ctxt.getString(R.string.error));
                                }
                            } else {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                Utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onInsertionFailed(App42Exception ex) {

                    }

                    @Override
                    public void onFindDocFailed(App42Exception ex) {
                        try {
                            if (ex != null) {
                                fetchActivities(progressDialog);
                            } else {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                Utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onUpdateDocFailed(App42Exception ex) {
                    }
                });
    }

    public void createProviderModel(String strDocument, String strProviderId) {

        try {
            JSONObject jsonObject = new JSONObject(strDocument);
            if (jsonObject.has("provider_email")) {
                Config.providerModel = new ProviderModel(
                        jsonObject.getString("provider_name"),
                        jsonObject.getString("provider_profile_url"),
                        "",
                        jsonObject.getString("provider_address"),
                        jsonObject.getString("provider_contact_no"),
                        jsonObject.getString("provider_email"),
                        strProviderId);

                Config.fileModels.add(new FileModel(strProviderId,
                        jsonObject.getString("provider_profile_url"), "IMAGE"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fetchDependents(final ProgressDialog progressDialog) {

        if (Config.customerIds.size() > 0) {

            if (utils.isConnectingToInternet()) {

                Query query = QueryBuilder.build("customer_id", Config.customerIds,
                        QueryBuilder.Operator.INLIST);

                storageService.findDocsByQuery(Config.collectionDependent, query,
                        new App42CallBack() {

                            @Override
                            public void onSuccess(Object o) {
                                try {
                                    if (o != null) {

                                        Utils.log(o.toString(), " fetchDependents ");

                                        Storage storage = (Storage) o;

                                        if (storage.getJsonDocList().size() > 0) {

                                            for (int i = 0; i < storage.getJsonDocList().size(); i++) {

                                                Storage.JSONDocument jsonDocument = storage.
                                                        getJsonDocList().get(i);

                                                String strDocument = jsonDocument.getJsonDoc();
                                                String strDependentDocId = jsonDocument.
                                                        getDocId();
                                                createDependentModel(strDependentDocId, strDocument);
                                            }
                                        }

                                        goToDashboard();
                                    } else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        Utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    Utils.toast(2, 2, _ctxt.getString(R.string.error));
                                }

                            }

                            @Override
                            public void onException(Exception e) {
                                try {
                                    if (e != null) {
                                        goToDashboard();
                                    } else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        Utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                    }
                                } catch (Exception e1) {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    e1.printStackTrace();
                                }
                            }

                        });

            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
            }

        } else goToDashboard();
    }

    public void goToDashboard() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();

        Intent intent = new Intent(_ctxt, DashboardActivity.class);
        //intent.putExtra("WHICH_SCREEN", intWhichScreen);
        Config.intSelectedMenu = Config.intDashboardScreen;
        _ctxt.startActivity(intent);
        ((Activity) _ctxt).finish();
    }

    public void fetchCustomers(final ProgressDialog progressDialog) {

        if (Config.customerIds.size() > 0) {

            if (utils.isConnectingToInternet()) {

                final Query query = QueryBuilder.build("_id", Config.customerIds,
                        QueryBuilder.Operator.INLIST);

                try {
                    System.out.println("Sudesi : " + query.get());
                } catch (Exception e) {
                    System.out.println("ddd");
                    e.printStackTrace();
                }
                storageService.findDocsByQuery(Config.collectionCustomer, query,
                        new App42CallBack() {

                            @Override
                            public void onSuccess(Object o) {
                                try {

                                    if (o != null) {

                                        Utils.log(o.toString(), " fetchCustomers ");
                                        Storage storage = (Storage) o;

                                        if (storage.getJsonDocList().size() > 0) {

                                            for (int i = 0; i < storage.getJsonDocList().size(); i++) {

                                                Storage.JSONDocument jsonDocument = storage.
                                                        getJsonDocList().get(i);

                                                String strDocument = jsonDocument.getJsonDoc();
                                                String strDependentDocId = jsonDocument.
                                                        getDocId();
                                                createCustomerModel(strDependentDocId, strDocument);
                                            }
                                        }
                                        fetchDependents(progressDialog);
                                    } else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        Utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    Utils.toast(2, 2, _ctxt.getString(R.string.error));
                                }
                            }

                            @Override
                            public void onException(Exception e) {
                                try {
                                    if (e != null) {
                                        Utils.log(e.getMessage().toString(), " fetchCustomers ");
                                        fetchDependents(progressDialog);
                                    } else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        Utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                }
                            }

                        });
            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                Utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
            }

        } else fetchDependents(progressDialog);
    }

    public void createDependentModel(String strDocumentId, String strDocument) {
        try {

            JSONObject jsonObjectDependent = new JSONObject(strDocument);

            if (jsonObjectDependent.has("dependent_name")) {

                DependentModel dependentModel = new DependentModel(
                        jsonObjectDependent.getString("dependent_name"),
                        jsonObjectDependent.getString("dependent_relation"),
                        jsonObjectDependent.getString("dependent_notes"),
                        jsonObjectDependent.getString("dependent_address"),
                        jsonObjectDependent.getString("dependent_contact_no"),
                        jsonObjectDependent.getString("dependent_email"),
                        jsonObjectDependent.getString("dependent_illness"),
                        "",
                        jsonObjectDependent.getString("dependent_profile_url"),
                        jsonObjectDependent.getInt("dependent_age"),
                        jsonObjectDependent.getInt("health_bp"),
                        jsonObjectDependent.getInt("health_heart_rate"),

                        strDocumentId,
                        jsonObjectDependent.getString("customer_id"));

                dependentModel.setStrDob(jsonObjectDependent.getString("dependent_dob"));


                if (!Config.dependentIdsAdded.contains(strDocumentId)) {

                    Config.dependentIdsAdded.add(strDocumentId);

                    Config.dependentModels.add(dependentModel);

                    Config.strDependentNames.add(jsonObjectDependent.getString("dependent_name"));

                    if (Config.clientModels.size() > 0) {
                        int iPosition = Config.customerIdsAdded.indexOf(jsonObjectDependent.getString("customer_id"));
                        Config.clientModels.get(iPosition).setDependentModel(dependentModel);
                    }

                    Config.fileModels.add(new FileModel(strDocumentId,
                            jsonObjectDependent.getString("dependent_profile_url"), "IMAGE"));

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createCustomerModel(String strDocumentId, String strDocument) {
        try {

            JSONObject jsonObject = new JSONObject(strDocument);

            if (jsonObject.has("customer_name")) {

                Utils.log(String.valueOf(Config.customerIds.contains(strDocumentId)), " 1 ");

                if (!Config.customerIdsAdded.contains(strDocumentId)) {
                    Config.customerIdsAdded.add(strDocumentId);

                    CustomerModel customerModel = new CustomerModel(jsonObject.getString("customer_name"),
                            jsonObject.getString("paytm_account"),
                            jsonObject.getString("customer_profile_url"), "",
                            jsonObject.getString("customer_address"),
                            jsonObject.getString("customer_city"),
                            jsonObject.getString("customer_state"),
                            jsonObject.getString("customer_contact_no"),
                            jsonObject.getString("customer_email"),
                            jsonObject.getString("customer_dob"),
                            jsonObject.getString("customer_country"),
                            jsonObject.getString("customer_country_code"),
                            jsonObject.getString("customer_area_code"),
                            jsonObject.getString("customer_contact_no"),
                            strDocumentId);

                    ClientModel clientModel = new ClientModel();
                    clientModel.setCustomerModel(customerModel);
                    Config.clientModels.add(clientModel);

                    Config.fileModels.add(new FileModel(strDocumentId,
                            jsonObject.getString("customer_profile_url"), "IMAGE"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fetchActivities(final ProgressDialog progressDialog) {

        storageService.findDocsByKeyValue(Config.collectionActivity, "provider_id",
                Config.providerModel.getStrProviderId(),
                new AsyncApp42ServiceApi.App42StorageServiceListener() {
            @Override
            public void onDocumentInserted(Storage response) {
            }

            @Override
            public void onUpdateDocSuccess(Storage response) {
            }

            @Override
            public void onFindDocSuccess(Storage response) throws JSONException {

                if(response != null){

                    Utils.log(response.toString(), " Activity ");

                    ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();

                    for(int i = 0 ; i < jsonDocList.size() ; i++ ){

                        Storage.JSONDocument jsonDocument = jsonDocList.get(i);
                        String strDocumentId = jsonDocument.getDocId();
                        String strActivities = jsonDocument.getJsonDoc();
                        createActivityModel(strDocumentId, strActivities);
                    }
                    fetchCustomers(progressDialog);
                } else {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                }
            }

            @Override
            public void onInsertionFailed(App42Exception ex) {
            }

            @Override
            public void onFindDocFailed(App42Exception ex) {
                try {
                    if (ex != null) {
                        fetchCustomers(progressDialog);
                    } else {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                    }
                } catch (Exception e1) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    e1.printStackTrace();
                }
            }

            @Override
            public void onUpdateDocFailed(App42Exception ex) {
            }
        });
    }

    public void loadAllFiles() {
        for (int i = 0; i < Config.fileModels.size(); i++) {
            FileModel fileModel = Config.fileModels.get(i);

            if (fileModel != null && fileModel.getStrFileUrl() != null &&
                    !fileModel.getStrFileUrl().equalsIgnoreCase("")) {
                utils.loadImageFromWeb(fileModel.getStrFileName(),
                        fileModel.getStrFileUrl());
            }
        }
    }

    public void createActivityModel(String strDocumentId, final String strDocument) {

        try {

            JSONObject jsonObject = new JSONObject(strDocument);

            if (jsonObject.has("dependent_id")) {

                if (!Config.strActivityIds.contains(strDocumentId)) {

                    Config.strActivityIds.add(strDocumentId);

                    if (!Config.dependentIds.contains(jsonObject.getString("dependent_id")))
                        Config.dependentIds.add(jsonObject.getString("dependent_id"));

                    if (!Config.customerIds.contains(jsonObject.getString("customer_id")))
                        Config.customerIds.add(jsonObject.getString("customer_id"));

                    ArrayList<ImageModel> activityImageModels = new ArrayList<>();
                    ArrayList<FeedBackModel> feedBackModels = new ArrayList<>();
                    ArrayList<VideoModel> videoModels = new ArrayList<>();

                    ActivityModel activityModel = new ActivityModel();
                    //activityModel.setStrServcieID(jsonObject.getString("service_id"));
                    //activityModel.setStrServiceDesc(jsonObject.getString("service_desc"));
                    activityModel.setStrServiceName(jsonObject.getString("service_name"));
                    activityModel.setStrActivityID(strDocumentId);
                    activityModel.setStrActivityName(jsonObject.getString("activity_name"));
                    //activityModel.setStrActivityMessage(jsonObject.getString("activity_message"));
                    //activityModel.setStrActivityDesc(jsonObject.getString("activity_description"));
                    activityModel.setStrActivityDate(jsonObject.getString("activity_date"));
                    activityModel.setStrActivityDoneDate(jsonObject.getString("activity_done_date"));
                    activityModel.setStrDependentID(jsonObject.getString("dependent_id"));

                    if (jsonObject.has("feedbacks")) {

                        JSONArray jsonArrayFeedback = jsonObject.getJSONArray("feedbacks");

                        for (int k = 0; k < jsonArrayFeedback.length(); k++) {

                            JSONObject jsonObjectFeedback = jsonArrayFeedback.getJSONObject(k);

                            FeedBackModel feedBackModel = new FeedBackModel(
                                    jsonObjectFeedback.getString("feedback_message"),
                                    jsonObjectFeedback.getString("feedback_by"),
                                    jsonObjectFeedback.getInt("feedback_rating"),
                                    jsonObjectFeedback.getBoolean("feedback_report"),
                                    jsonObjectFeedback.getString("feedback_time"),
                                    jsonObjectFeedback.getString("feedback_by_type"));

                            if (jsonObjectFeedback.getString("feedback_by_type").equalsIgnoreCase("customer")) {
                                if (!Config.customerIds.contains(jsonObjectFeedback.getString("feedback_by")))
                                    Config.customerIds.add(jsonObjectFeedback.getString("feedback_by"));
                            }

                            if (jsonObjectFeedback.getString("feedback_by_type").equalsIgnoreCase("dependent")) {
                                if (!Config.dependentIds.contains(jsonObjectFeedback.getString("feedback_by")))
                                    Config.dependentIds.add(jsonObjectFeedback.getString("feedback_by"));
                            }


                            feedBackModels.add(feedBackModel);

                            Config.iRatings += jsonObjectFeedback.getInt("feedback_rating");

                            Config.iRatingCount += 1;

                            Config.feedBackModels.add(feedBackModel);
                        }
                        activityModel.setFeedBackModels(feedBackModels);
                    }

                    if (jsonObject.has("videos")) {

                        JSONArray jsonArrayVideos = jsonObject.
                                getJSONArray("videos");

                        for (int k = 0; k < jsonArrayVideos.length(); k++) {

                            JSONObject jsonObjectVideo = jsonArrayVideos.
                                    getJSONObject(k);

                            VideoModel videoModel = new VideoModel(
                                    jsonObjectVideo.getString("video_name"),
                                    jsonObjectVideo.getString("video_url"),
                                    jsonObjectVideo.getString("video_description"),
                                    jsonObjectVideo.getString("video_taken"));

                            Config.fileModels.add(new FileModel(jsonObjectVideo.getString("video_name"),
                                    jsonObjectVideo.getString("video_url"), "VIDEO"));

                            videoModels.add(videoModel);
                        }
                        activityModel.setVideoModels(videoModels);
                    }

                    if (jsonObject.has("images")) {

                        JSONArray jsonArrayVideos = jsonObject.
                                getJSONArray("images");

                        for (int k = 0; k < jsonArrayVideos.length(); k++) {

                            JSONObject jsonObjectImage = jsonArrayVideos.
                                    getJSONObject(k);

                            ImageModel imageModel = new ImageModel(
                                    jsonObjectImage.getString("image_name"),
                                    jsonObjectImage.getString("image_url"),
                                    jsonObjectImage.getString("image_description"),
                                    jsonObjectImage.getString("image_taken"));

                            Config.fileModels.add(new FileModel(jsonObjectImage.getString("image_name"),
                                    jsonObjectImage.getString("image_url"), "IMAGE"));

                            activityImageModels.add(imageModel);
                        }
                        activityModel.setImageModels(activityImageModels);
                    }

                    Config.activityModels.add(activityModel);
                }
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}