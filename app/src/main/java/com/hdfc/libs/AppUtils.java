package com.hdfc.libs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RelativeLayout;

import com.hdfc.app42service.App42GCMService;
import com.hdfc.app42service.StorageService;
import com.hdfc.caregiver.DashboardActivity;
import com.hdfc.caregiver.LoginActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbCon;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.ClientModel;
import com.hdfc.models.CustomerModel;
import com.hdfc.models.DependentModel;
import com.hdfc.models.FeedBackModel;
import com.hdfc.models.FieldModel;
import com.hdfc.models.ImageModel;
import com.hdfc.models.MilestoneModel;
import com.hdfc.models.ProviderModel;
import com.hdfc.models.ServiceModel;
import com.hdfc.models.VideoModel;
import com.scottyab.aescrypt.AESCrypt;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import net.sqlcipher.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Admin on 4/25/2016.
 */
public class AppUtils {
    private static Context _ctxt;
    private static StorageService storageService;
    //private static ProgressDialog progressDialog;
    private static Utils utils;

    private static SharedPreferences sharedPreferences;

    private static String strDocumentLocal = "", strProviderId = "";


    public AppUtils(Context context) {
        _ctxt = context;
        utils = new Utils(_ctxt);
        //progressDialog = new ProgressDialog(_ctxt);
        storageService = new StorageService(_ctxt);
        sharedPreferences = _ctxt.getSharedPreferences(Config.strPreferenceName,
                Context.MODE_PRIVATE);

        strProviderId = sharedPreferences.getString("PROVIDER_ID", "");
    }

    public static void logout() {
        try {
            Config.jsonObject = null;


            Config.intSelectedMenu = 0;

            Config.boolIsLoggedIn = false;

            //Config.fileModels.clear();
            CareGiver.dbCon.deleteFiles();

            //todo clear shared pref.

            SharedPreferences.Editor editor = _ctxt.getSharedPreferences(Config.strPreferenceName,
                    Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.commit();

            File fileImage = Utils.createFileInternal("images/");
            Utils.deleteAllFiles(fileImage);

            unregisterGcm();

            Intent dashboardIntent = new Intent(_ctxt, LoginActivity.class);
            _ctxt.startActivity(dashboardIntent);
            ((Activity) _ctxt).finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unregisterGcm() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    App42GCMService.unRegisterGcm();
                } catch (Exception bug) {
                    bug.printStackTrace();
                }
            }
        });
        thread.start();
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

                Config.providerModel.setStrCountry(jsonObject.getString("provider_country"));
                Config.providerModel.setStrState(jsonObject.getString("provider_state"));
                Config.providerModel.setStrCity(jsonObject.getString("provider_city"));
                Config.providerModel.setStrPinCode(jsonObject.getString("provider_pin_code"));

                String strUrl = jsonObject.getString("provider_profile_url");

                String strUrlHash = Utils.sha512(strUrl);

                Cursor cur = CareGiver.dbCon.fetch(
                        DbHelper.strTableNameFiles, new String[]{"file_hash"}, "name=?",
                        new String[]{strProviderId}, null, "0, 1", true, null, null
                );

                String strHashLocal = "";

                if (cur.getCount() <= 0) {
                    CareGiver.dbCon.insert(DbHelper.strTableNameFiles, new String[]{strProviderId,
                                    strUrl, "IMAGE", strUrlHash},
                            new String[]{"name", "url", "file_type", "file_hash"});
                } else {

                    cur.moveToFirst();

                    while (!cur.isAfterLast()) {
                        //strUrlLocal=cur.getString(0);
                        strHashLocal = cur.getString(0);
                        cur.moveToNext();
                    }

                    //CareGiver.dbCon.closeCursor(cur);

                    if (!strHashLocal.equalsIgnoreCase(strUrlHash)) {
                        CareGiver.dbCon.update(
                                DbHelper.strTableNameFiles, "name=?",
                                new String[]{strUrl, strUrlHash},
                                new String[]{"url", "file_hash"}, new String[]{strProviderId}
                        );
                    }
                }

                CareGiver.dbCon.closeCursor(cur);

                //Config.fileModels.add(new FileModel(strProviderId, strUrl, "IMAGE"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fetchDependents(final RelativeLayout relativeLayout) {

        if (Config.dependentIds.size() > 0) {

            if (utils.isConnectingToInternet()) {

                Query query = QueryBuilder.build("_id", Config.dependentIds,
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

                                        DashboardActivity.gotoSimpleActivityMenu();
                                    } else {
                                       /* if (progressDialog.isShowing())
                                            progressDialog.dismiss();*/
                                        relativeLayout.setVisibility(View.GONE);
                                        utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                  /*  if (progressDialog.isShowing())
                                        progressDialog.dismiss();*/
                                    relativeLayout.setVisibility(View.GONE);
                                    utils.toast(2, 2, _ctxt.getString(R.string.error));
                                }

                            }

                            @Override
                            public void onException(Exception e) {
                                try {
                                    if (e != null) {
                                        //Utils.log(e.getMessage(), " fetchDependents failure ");
                                        DashboardActivity.gotoSimpleActivityMenu();
                                    } else {
                                       /* if (progressDialog.isShowing())
                                            progressDialog.dismiss();*/
                                        relativeLayout.setVisibility(View.GONE);
                                        utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                    }
                                } catch (Exception e1) {
                                   /* if (progressDialog.isShowing())
                                        progressDialog.dismiss();*/
                                    relativeLayout.setVisibility(View.GONE);
                                    e1.printStackTrace();
                                }
                            }

                        });

            } else {
               /* if (progressDialog.isShowing())
                    progressDialog.dismiss();*/
                relativeLayout.setVisibility(View.GONE);
                utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
            }

        } else DashboardActivity.gotoSimpleActivityMenu();
    }

    public void fetchCustomers(final RelativeLayout relativeLayout) {

        if (Config.customerIds.size() > 0) {

            if (utils.isConnectingToInternet()) {

                final Query query = QueryBuilder.build("_id", Config.customerIds,
                        QueryBuilder.Operator.INLIST);

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
                                        fetchDependents(relativeLayout);
                                    } else {
                                       /* if (progressDialog.isShowing())
                                            progressDialog.dismiss();*/
                                        relativeLayout.setVisibility(View.GONE);
                                        utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                   /* if (progressDialog.isShowing())
                                        progressDialog.dismiss();*/
                                    relativeLayout.setVisibility(View.GONE);
                                    utils.toast(2, 2, _ctxt.getString(R.string.error));
                                }
                            }

                            @Override
                            public void onException(Exception e) {
                                try {
                                    if (e != null) {
                                        fetchDependents(relativeLayout);
                                    } else {
                                        /*if (progressDialog.isShowing())
                                            progressDialog.dismiss();*/
                                        relativeLayout.setVisibility(View.GONE);
                                        utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                    /*if (progressDialog.isShowing())
                                        progressDialog.dismiss();*/
                                    relativeLayout.setVisibility(View.GONE);
                                }
                            }

                        });
            } else {
               /* if (progressDialog.isShowing())
                    progressDialog.dismiss();*/
                relativeLayout.setVisibility(View.GONE);

                utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
            }

        } else fetchDependents(relativeLayout);
    }

    public void createDependentModel(String strDocumentId, String strDocument) {

        try {

            JSONObject jsonObjectDependent = new JSONObject(strDocument);

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
                    strDocumentId,
                    jsonObjectDependent.getString("customer_id"));

            dependentModel.setStrDob(jsonObjectDependent.getString("dependent_dob"));


            if (jsonObjectDependent.has("dependent_age")) {

                try {
                    dependentModel.setIntAge(jsonObjectDependent.getInt("dependent_age"));
                } catch (Exception e) {
                    try {
                        String strAge = jsonObjectDependent.getString("dependent_age");

                        int iAge = 0;
                        if (!strAge.equalsIgnoreCase(""))
                            iAge = Integer.parseInt(strAge);

                        dependentModel.setIntAge(iAge);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }

            if (jsonObjectDependent.has("health_bp")) {

                try {
                    dependentModel.setIntHealthBp(jsonObjectDependent.getInt("health_bp"));
                } catch (Exception e) {
                    try {
                        String strBp = jsonObjectDependent.getString("health_bp");

                        int iBp = 0;
                        if (!strBp.equalsIgnoreCase(""))
                            iBp = Integer.parseInt(strBp);

                        dependentModel.setIntHealthBp(iBp);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }

            if (jsonObjectDependent.has("health_heart_rate")) {

                try {
                    dependentModel.setIntHealthHeartRate(jsonObjectDependent.getInt("health_heart_rate"));
                } catch (Exception e) {
                    try {

                        String strPulse = jsonObjectDependent.getString("health_heart_rate");

                        int iPulse = 0;
                        if (!strPulse.equalsIgnoreCase(""))
                            iPulse = Integer.parseInt(strPulse);

                        dependentModel.setIntHealthHeartRate(iPulse);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }


            if (!Config.dependentIdsAdded.contains(strDocumentId)) {

                Config.dependentIdsAdded.add(strDocumentId);

                Config.dependentModels.add(dependentModel);

                Config.strDependentNames.add(jsonObjectDependent.getString("dependent_name"));

               /* if (Config.clientModels.size() > 0) {
                    int iPosition = Config.customerIdsAdded.indexOf(jsonObjectDependent.getString("customer_id"));
                    Config.clientModels.get(iPosition).setDependentModel(dependentModel);
                }*/

               /* Config.fileModels.add(new FileModel(strDocumentId,
                        jsonObjectDependent.getString("dependent_profile_url"), "IMAGE"));*/

                //
                String strUrl = jsonObjectDependent.getString("dependent_profile_url");

                String strUrlHash = Utils.sha512(strUrl);

                Cursor cur = CareGiver.dbCon.fetch(
                        DbHelper.strTableNameFiles, new String[]{"file_hash"}, "name=?",
                        new String[]{strDocumentId}, null, "0, 1", true, null, null
                );


                String strHashLocal = "";

                if (cur.getCount() <= 0) {
                    CareGiver.dbCon.insert(DbHelper.strTableNameFiles, new String[]{strDocumentId,
                                    strUrl, "IMAGE", strUrlHash},
                            new String[]{"name", "url", "file_type", "file_hash"});
                } else {

                    cur.moveToFirst();

                    while (!cur.isAfterLast()) {
                        //strUrlLocal=cur.getString(0);
                        strHashLocal = cur.getString(0);
                        cur.moveToNext();
                    }

                    CareGiver.dbCon.closeCursor(cur);

                    if (!strHashLocal.equalsIgnoreCase(strUrlHash)) {
                        CareGiver.dbCon.update(
                                DbHelper.strTableNameFiles, "name=?",
                                new String[]{strUrl, strUrlHash},
                                new String[]{"url", "file_hash"}, new String[]{strDocumentId}
                        );
                    }
                }

                CareGiver.dbCon.closeCursor(cur);
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

                    Config.customerModels.add(customerModel);

                    Config.strCustomerNames.add(jsonObject.getString("customer_name"));

                    /*Config.fileModels.add(new FileModel(strDocumentId,
                            jsonObject.getString("customer_profile_url"), "IMAGE"));*/
                    //
                    String strUrl = jsonObject.getString("customer_profile_url");

                    String strUrlHash = Utils.sha512(strUrl);

                    Cursor cur = CareGiver.dbCon.fetch(
                            DbHelper.strTableNameFiles, new String[]{"file_hash"}, "name=?",
                            new String[]{strDocumentId}, null, "0, 1", true, null, null
                    );


                    String strHashLocal = "";

                    if (cur.getCount() <= 0) {
                        CareGiver.dbCon.insert(DbHelper.strTableNameFiles, new String[]{strDocumentId,
                                        strUrl, "IMAGE", strUrlHash},
                                new String[]{"name", "url", "file_type", "file_hash"});
                    } else {

                        cur.moveToFirst();

                        while (!cur.isAfterLast()) {
                            //strUrlLocal=cur.getString(0);
                            strHashLocal = cur.getString(0);
                            cur.moveToNext();
                        }

                        //CareGiver.dbCon.closeCursor(cur);

                        if (!strHashLocal.equalsIgnoreCase(strUrlHash)) {
                            CareGiver.dbCon.update(
                                    DbHelper.strTableNameFiles, "name=?",
                                    new String[]{strUrl, strUrlHash},
                                    new String[]{"url", "file_hash"}, new String[]{strDocumentId}
                            );
                        }
                    }

                    CareGiver.dbCon.closeCursor(cur);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fetchActivities(final RelativeLayout relativeLayout) {//final ProgressDialog progressDialog

        //////////////////////////
        ///
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Note: zero based!
        int day = calendar.get(Calendar.DAY_OF_MONTH);
      /*  int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millis = calendar.get(Calendar.MILLISECOND);*/
        ///

        String strDay = String.valueOf(day);
        String strMonth = String.valueOf(month);

        if (day <= 9)
            strDay = String.valueOf("0" + day);

        if (month <= 9)
            strMonth = String.valueOf("0" + month);

        String strDate = String.valueOf(year + "-" + strMonth + "-" + strDay) + "T23:59:59.999+0000";

        Utils.log(strDate, " FDATE ");


        //////////////////////////////////
        /*String strUpdatedDate="";

        if (DbCon.isDbOpened) {

            Cursor cur = null;

            try {

                if(!strProviderId.equalsIgnoreCase("")) {

                    strProviderId = AESCrypt.decrypt(Config.string, strProviderId);
                    cur = CareGiver.dbCon.fetch(DbHelper.strTableNameCollection,
                            new String[]{"updated_date", "document"}, "object_id=?",
                            new String[]{strProviderId}, null, "0, 1", true, null, null);

                    if (cur.getCount() > 0) {
                        cur.moveToFirst();
                           *//* while (!cur.isAfterLast()) {*//*
                        strUpdatedDate = cur.getString(0);
                        strDocumentLocal = cur.getString(1);
                            *//*    cur.moveToNext();
                            }*//*
                    }
                    CareGiver.dbCon.closeCursor(cur);
                }
            } catch (Exception e) {
                CareGiver.dbCon.closeCursor(cur);
                e.printStackTrace();
            }
        }*/
        //////////////////////////////////

        Query q1 = QueryBuilder.build("provider_id", Config.providerModel.getStrProviderId(),
                QueryBuilder.Operator.EQUALS);

        Query q2 = QueryBuilder.build("activity_date", strDate, QueryBuilder.
                Operator.LESS_THAN_EQUALTO);

        Query q3 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);

           /* int max = 1;
            int offset = 0;
*/
        ///////////////////////////

        storageService.findDocsByQueryOrderBy(Config.collectionActivity, q3, 100, 0,
                "activity_date", 1,
                new App42CallBack() {

                    @Override
                    public void onSuccess(Object o) {
                        if (o != null) {

                            Storage storage = (Storage) o;

                            Utils.log(storage.toString(), " Activity ");

                            ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();

                            for (int i = 0; i < jsonDocList.size(); i++) {

                                Storage.JSONDocument jsonDocument = jsonDocList.get(i);
                                String strDocumentId = jsonDocument.getDocId();
                                String strActivities = jsonDocument.getJsonDoc();
                                createActivityModel(strDocumentId, strActivities);
                            }
                            fetchCustomers(relativeLayout); //progressDialog
                            //fetchDependents(relativeLayout);
                        } else {
                   /* if (progressDialog.isShowing())
                        progressDialog.dismiss();*/
                            relativeLayout.setVisibility(View.GONE);
                            utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                        }
                    }

                    @Override
                    public void onException(Exception ex) {
                        try {
                            if (ex != null) {
                                fetchCustomers(relativeLayout);
                                //fetchDependents(relativeLayout);
                            } else {
                        /*if (progressDialog.isShowing())
                            progressDialog.dismiss();*/
                                relativeLayout.setVisibility(View.GONE);
                                utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                            }
                        } catch (Exception e1) {
                   /* if (progressDialog.isShowing())
                        progressDialog.dismiss();*/
                            relativeLayout.setVisibility(View.GONE);
                            e1.printStackTrace();
                        }
                    }

                    /* storageService.findDocsByKeyValue(Config.collectionActivity, "provider_id",
                                    Config.providerModel.getStrProviderId(),
                                    new AsyncApp42ServiceApi.App42StorageServiceListener() {*/
        });
    }

    public void loadAllFiles() {

        Cursor cur = null;

        try {

            cur = CareGiver.dbCon.fetch(
                    DbHelper.strTableNameFiles, new String[]{"name", "url"}, null,
                    null, null, null, true, null, null
            );

            if (cur.getCount() > 0) {
                cur.moveToFirst();
                while (!cur.isAfterLast()) {
                    utils.loadImageFromWeb(cur.getString(0), cur.getString(1));
                    cur.moveToNext();
                }
            }

            CareGiver.dbCon.closeCursor(cur);

        } catch (Exception e) {
            e.printStackTrace();
            CareGiver.dbCon.closeCursor(cur);
        }

        /*for (int i = 0; i < Config.fileModels.size(); i++) {
            FileModel fileModel = Config.fileModels.get(i);

            if (fileModel != null && fileModel.getStrFileUrl() != null &&
                    !fileModel.getStrFileUrl().equalsIgnoreCase("")) {
                utils.loadImageFromWeb(fileModel.getStrFileName(),
                        fileModel.getStrFileUrl());
            }
        }*/
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

                    ActivityModel activityModel = new ActivityModel();

                    activityModel.setStrActivityName(jsonObject.getString("activity_name"));
                    activityModel.setStrActivityID(strDocumentId);
                    activityModel.setStrProviderID(jsonObject.getString("provider_id"));
                    activityModel.setStrDependentID(jsonObject.getString("dependent_id"));
                    activityModel.setStrCustomerID(jsonObject.getString("customer_id"));
                    activityModel.setStrActivityStatus(jsonObject.getString("status"));
                    activityModel.setStrActivityDesc(jsonObject.getString("activity_desc"));

                    activityModel.setStrServcieID(jsonObject.getString("service_id"));
                    activityModel.setStrServiceName(jsonObject.getString("service_name"));

                    if (jsonObject.has("activity_date"))
                        activityModel.setStrActivityDate(jsonObject.getString("activity_date"));

                    activityModel.setStrActivityDoneDate(jsonObject.
                            getString("activity_done_date"));

                    //activityModel.setbActivityOverdue(jsonObject.getBoolean("overdue"));

                    activityModel.setStrActivityProviderStatus(jsonObject.
                            getString("provider_status"));

                    activityModel.setStrActivityProviderMessage(jsonObject.
                            getString("provider_message"));

                    ArrayList<FeedBackModel> feedBackModels = new ArrayList<>();
                    ArrayList<VideoModel> videoModels = new ArrayList<>();
                    ArrayList<ImageModel> imageModels = new ArrayList<>();

                    if (jsonObject.has("videos")) {

                        JSONArray jsonArrayVideos = jsonObject.
                                getJSONArray("videos");

                        for (int k = 0; k < jsonArrayVideos.length(); k++) {

                            JSONObject jsonObjectVideo = jsonArrayVideos.
                                    getJSONObject(k);

                            if (jsonObjectVideo.has("video_name")) {

                                VideoModel videoModel = new VideoModel(
                                        jsonObjectVideo.getString("video_name"),
                                        jsonObjectVideo.getString("video_url"),
                                        jsonObjectVideo.getString("video_description"),
                                        jsonObjectVideo.getString("video_taken"));

                                //

                                String strUrlHash = Utils.sha512(jsonObjectVideo.getString("video_url"));

                                Cursor cur = CareGiver.dbCon.fetch(
                                        DbHelper.strTableNameFiles, new String[]{"file_hash"}, "name=?",
                                        new String[]{jsonObjectVideo.getString("video_name")}, null, "0, 1", true, null, null
                                );


                                String strHashLocal = "";

                                if (cur.getCount() <= 0) {
                                    CareGiver.dbCon.insert(DbHelper.strTableNameFiles, new String[]{jsonObjectVideo.getString("video_name"),
                                                    jsonObjectVideo.getString("video_url"), "VIDEO", strUrlHash},
                                            new String[]{"name", "url", "file_type", "file_hash"});
                                } else {

                                    cur.moveToFirst();

                                    while (!cur.isAfterLast()) {
                                        //strUrlLocal=cur.getString(0);
                                        strHashLocal = cur.getString(0);
                                        cur.moveToNext();
                                    }

                                    CareGiver.dbCon.closeCursor(cur);

                                    if (!strHashLocal.equalsIgnoreCase(strUrlHash)) {
                                        CareGiver.dbCon.update(
                                                DbHelper.strTableNameFiles, "name=?",
                                                new String[]{jsonObjectVideo.getString("video_url"), strUrlHash},
                                                new String[]{"url", "file_hash"}, new String[]{jsonObjectVideo.getString("video_name")}
                                        );
                                    }
                                }

                                CareGiver.dbCon.closeCursor(cur);
                                //

                                /*Config.fileModels.add(new FileModel(jsonObjectVideo.getString("video_name"),
                                        jsonObjectVideo.getString("video_url"), "VIDEO"));*/

                                videoModels.add(videoModel);
                            }
                        }
                        activityModel.setVideoModels(videoModels);
                    }

                    if (jsonObject.has("images")) {

                        JSONArray jsonArrayVideos = jsonObject.
                                getJSONArray("images");

                        for (int k = 0; k < jsonArrayVideos.length(); k++) {

                            JSONObject jsonObjectImage = jsonArrayVideos.
                                    getJSONObject(k);

                            if (jsonObjectImage.has("image_name")) {

                                ImageModel imageModel = new ImageModel(
                                        jsonObjectImage.getString("image_name"),
                                        jsonObjectImage.getString("image_url"),
                                        jsonObjectImage.getString("image_description"),
                                        jsonObjectImage.getString("image_taken"));

                               /* Config.fileModels.add(new FileModel(jsonObjectImage.getString("image_name"),
                                        jsonObjectImage.getString("image_url"), "IMAGE"));*/

                                //
                                String strUrlHash = Utils.sha512(jsonObjectImage.getString("image_url"));

                                Cursor cur = CareGiver.dbCon.fetch(
                                        DbHelper.strTableNameFiles, new String[]{"file_hash"}, "name=?",
                                        new String[]{jsonObjectImage.getString("image_name")}, null, "0, 1", true, null, null
                                );


                                String strHashLocal = "";

                                if (cur.getCount() <= 0) {
                                    CareGiver.dbCon.insert(DbHelper.strTableNameFiles, new String[]{jsonObjectImage.getString("image_name"),
                                                    jsonObjectImage.getString("image_url"), "IMAGE", strUrlHash},
                                            new String[]{"name", "url", "file_type", "file_hash"});
                                } else {

                                    cur.moveToFirst();

                                    while (!cur.isAfterLast()) {
                                        //strUrlLocal=cur.getString(0);
                                        strHashLocal = cur.getString(0);
                                        cur.moveToNext();
                                    }

                                    CareGiver.dbCon.closeCursor(cur);

                                    if (!strHashLocal.equalsIgnoreCase(strUrlHash)) {
                                        CareGiver.dbCon.update(
                                                DbHelper.strTableNameFiles, "name=?",
                                                new String[]{jsonObjectImage.getString("image_url"), strUrlHash},
                                                new String[]{"url", "file_hash"}, new String[]{jsonObjectImage.getString("image_name")}
                                        );
                                    }
                                }

                                CareGiver.dbCon.closeCursor(cur);
                                //

                                imageModels.add(imageModel);

                            }
                        }
                        activityModel.setImageModels(imageModels);
                    }

                    if (jsonObject.has("feedbacks")) {

                        JSONArray jsonArrayFeedback = jsonObject.getJSONArray("feedbacks");

                        for (int k = 0; k < jsonArrayFeedback.length(); k++) {

                            JSONObject jsonObjectFeedback = jsonArrayFeedback.getJSONObject(k);

                            if (jsonObjectFeedback.has("feedback_message")) {

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
                        }
                        activityModel.setFeedBackModels(feedBackModels);
                    }

                    if (jsonObject.has("milestones")) {

                        JSONArray jsonArrayMilestones = jsonObject.
                                getJSONArray("milestones");

                        for (int k = 0; k < jsonArrayMilestones.length(); k++) {

                            JSONObject jsonObjectMilestone =
                                    jsonArrayMilestones.getJSONObject(k);

                            MilestoneModel milestoneModel = new MilestoneModel();

                            milestoneModel.setiMilestoneId(jsonObjectMilestone.getInt("id"));
                            milestoneModel.setStrMilestoneStatus(jsonObjectMilestone.getString("status"));
                            milestoneModel.setStrMilestoneName(jsonObjectMilestone.getString("name"));
                            milestoneModel.setStrMilestoneDate(jsonObjectMilestone.getString("date"));

                            if (jsonObjectMilestone.has("show")) {

                                try {
                                    milestoneModel.setVisible(jsonObjectMilestone.getBoolean("show"));
                                } catch (Exception e) {
                                    boolean b = true;
                                    try {
                                        if (jsonObjectMilestone.getInt("show") == 0)
                                            b = false;
                                        milestoneModel.setVisible(b);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }

                            if (jsonObjectMilestone.has("reschedule")) {

                                try {
                                    milestoneModel.setReschedule(jsonObjectMilestone.getBoolean("reschedule"));
                                } catch (Exception e) {
                                    boolean b = true;
                                    try {
                                        if (jsonObjectMilestone.getInt("reschedule") == 0)
                                            b = false;
                                        milestoneModel.setReschedule(b);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }

                            if (jsonObjectMilestone.has("scheduled_date"))
                                milestoneModel.setStrMilestoneScheduledDate(jsonObjectMilestone.
                                        getString("scheduled_date"));

                            if (jsonObjectMilestone.has("fields")) {

                                JSONArray jsonArrayFields = jsonObjectMilestone.
                                        getJSONArray("fields");

                                for (int l = 0; l < jsonArrayFields.length(); l++) {

                                    JSONObject jsonObjectField =
                                            jsonArrayFields.getJSONObject(l);

                                    FieldModel fieldModel = new FieldModel();

                                    fieldModel.setiFieldID(jsonObjectField.getInt("id"));

                                    if (jsonObjectField.has("hide")) {

                                        try {
                                            fieldModel.setFieldView(jsonObjectField.getBoolean("hide"));
                                        } catch (Exception e) {
                                            boolean b = true;
                                            try {
                                                if (jsonObjectField.getInt("hide") == 0)
                                                    b = false;
                                                fieldModel.setFieldView(b);
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                    }

                                    if (jsonObjectField.has("required")) {

                                        try {
                                            fieldModel.setFieldRequired(jsonObjectField.getBoolean("required"));
                                        } catch (Exception e) {
                                            boolean b = true;
                                            try {
                                                if (jsonObjectField.getInt("required") == 0)
                                                    b = false;
                                                fieldModel.setFieldRequired(b);
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                    }

                                    fieldModel.setStrFieldData(jsonObjectField.getString("data"));
                                    fieldModel.setStrFieldLabel(jsonObjectField.getString("label"));
                                    fieldModel.setStrFieldType(jsonObjectField.getString("type"));

                                    if (jsonObjectField.has("values")) {

                                        fieldModel.setStrFieldValues(utils.jsonToStringArray(jsonObjectField.
                                                getJSONArray("values")));
                                    }

                                    if (jsonObjectField.has("child")) {

                                        if (jsonObjectField.has("child")) {

                                            try {
                                                fieldModel.setChild(jsonObjectField.getBoolean("child"));
                                            } catch (Exception e) {
                                                boolean b = true;
                                                try {
                                                    if (jsonObjectField.getInt("child") == 0)
                                                        b = false;
                                                    fieldModel.setChild(b);
                                                } catch (Exception e1) {
                                                    e1.printStackTrace();
                                                }
                                            }
                                        }


                                        if (jsonObjectField.has("child_type"))
                                            fieldModel.setStrChildType(utils.jsonToStringArray(jsonObjectField.
                                                    getJSONArray("child_type")));

                                        if (jsonObjectField.has("child_value"))
                                            fieldModel.setStrChildValue(utils.jsonToStringArray(jsonObjectField.
                                                    getJSONArray("child_value")));

                                        if (jsonObjectField.has("child_condition"))
                                            fieldModel.setStrChildCondition(utils.jsonToStringArray(jsonObjectField.
                                                    getJSONArray("child_condition")));

                                        if (jsonObjectField.has("child_field"))
                                            fieldModel.setiChildfieldID(utils.jsonToIntArray(jsonObjectField.
                                                    getJSONArray("child_field")));
                                    }

                                    milestoneModel.setFieldModel(fieldModel);
                                }
                            }
                            activityModel.setMilestoneModel(milestoneModel);
                        }
                    }

                    Config.activityModels.add(activityModel);
                }
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createServiceModel(String strDocumentId, JSONObject jsonObject) {

        try {
            ServiceModel serviceModel = new ServiceModel();

            serviceModel.setDoubleCost(jsonObject.getDouble("cost"));
            serviceModel.setStrServiceName(jsonObject.getString("service_name"));
            serviceModel.setiServiceNo(jsonObject.getInt("service_no"));
            serviceModel.setStrCategoryName(jsonObject.getString("category_name"));
            serviceModel.setiUnit(jsonObject.getInt("unit"));
            serviceModel.setiUnitValue(jsonObject.getInt("unit_value"));
            //serviceModel.setStrServiceType(jsonObject.getString("service_type"));


            Config.serviceModels.add(serviceModel);

            if (jsonObject.has("milestones")) {


                JSONArray jsonArrayMilestones = jsonObject.
                        getJSONArray("milestones");

                for (int k = 0; k < jsonArrayMilestones.length(); k++) {

                    JSONObject jsonObjectMilestone =
                            jsonArrayMilestones.getJSONObject(k);

                    MilestoneModel milestoneModel = new MilestoneModel();

                    milestoneModel.setiMilestoneId(jsonObjectMilestone.getInt("id"));
                    milestoneModel.setStrMilestoneStatus(jsonObjectMilestone.getString("status"));
                    milestoneModel.setStrMilestoneName(jsonObjectMilestone.getString("name"));
                    milestoneModel.setStrMilestoneDate(jsonObjectMilestone.getString("date"));
                    //milestoneModel.setVisible(jsonObjectMilestone.getBoolean("show"));

                    if (jsonObjectMilestone.has("show"))
                        milestoneModel.setVisible(jsonObjectMilestone.getBoolean("show"));

                    if (jsonObjectMilestone.has("reschedule"))
                        milestoneModel.setReschedule(jsonObjectMilestone.getBoolean("reschedule"));

                    if (jsonObjectMilestone.has("scheduled_date"))
                        milestoneModel.setStrMilestoneScheduledDate(jsonObjectMilestone.
                                getString("scheduled_date"));

                    //
                    if (jsonObjectMilestone.has("fields")) {

                        JSONArray jsonArrayFields = jsonObjectMilestone.
                                getJSONArray("fields");

                        for (int l = 0; l < jsonArrayFields.length(); l++) {

                            JSONObject jsonObjectField =
                                    jsonArrayFields.getJSONObject(l);

                            FieldModel fieldModel = new FieldModel();

                            fieldModel.setiFieldID(jsonObjectField.getInt("id"));

                            if (jsonObjectField.has("hide"))
                                fieldModel.setFieldView(jsonObjectField.getBoolean("hide"));

                            fieldModel.setFieldRequired(jsonObjectField.getBoolean("required"));
                            fieldModel.setStrFieldData(jsonObjectField.getString("data"));
                            fieldModel.setStrFieldLabel(jsonObjectField.getString("label"));
                            fieldModel.setStrFieldType(jsonObjectField.getString("type"));

                            if (jsonObjectField.has("values")) {

                                fieldModel.setStrFieldValues(utils.jsonToStringArray(jsonObjectField.
                                        getJSONArray("values")));
                            }

                            if (jsonObjectField.has("child")) {

                                fieldModel.setChild(jsonObjectField.getBoolean("child"));

                                if (jsonObjectField.has("child_type"))
                                    fieldModel.setStrChildType(utils.jsonToStringArray(jsonObjectField.
                                            getJSONArray("child_type")));

                                if (jsonObjectField.has("child_value"))
                                    fieldModel.setStrChildValue(utils.jsonToStringArray(jsonObjectField.
                                            getJSONArray("child_value")));

                                if (jsonObjectField.has("child_condition"))
                                    fieldModel.setStrChildCondition(utils.jsonToStringArray(jsonObjectField.
                                            getJSONArray("child_condition")));

                                if (jsonObjectField.has("child_field"))
                                    fieldModel.setiChildfieldID(utils.jsonToIntArray(jsonObjectField.
                                            getJSONArray("child_field")));
                            }

                            milestoneModel.setFieldModel(fieldModel);
                        }
                    }

                    serviceModel.setMilestoneModels(milestoneModel);
                }
            }

            serviceModel.setStrServiceId(strDocumentId);

            if (!Config.strServcieIds.contains(strDocumentId)) {
                Config.serviceModels.add(serviceModel);
                Config.strServcieIds.add(strDocumentId);

                Config.servicelist.add(jsonObject.getString("service_name"));


                //
               /* if (!Config.strServiceCategoryNames.contains(jsonObject.getString("category_name"))) {
                    Config.strServiceCategoryNames.add(jsonObject.getString("category_name"));

                    CategoryServiceModel categoryServiceModel = new CategoryServiceModel();
                    categoryServiceModel.setStrCategoryName(jsonObject.getString("category_name"));
                    categoryServiceModel.setServiceModels(serviceModel);

                    Config.categoryServiceModels.add(categoryServiceModel);
                } else {
                    int iPosition = Config.strServiceCategoryNames.indexOf(jsonObject.getString("category_name"));
                    Config.categoryServiceModels.get(iPosition).setServiceModels(serviceModel);
                }*/
                //
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //refresh Providers
    private void fetchProviders(final String strUserName) {

        if (utils.isConnectingToInternet()) {

            String strUpdatedDate = "";

            if (DbCon.isDbOpened) {

                Cursor cur = null;

                try {

                    if (!strProviderId.equalsIgnoreCase("")) {

                        strProviderId = AESCrypt.decrypt(Config.string, strProviderId);
                        cur = CareGiver.dbCon.fetch(DbHelper.strTableNameCollection,
                                new String[]{"updated_date", "document"}, "object_id=?",
                                new String[]{strProviderId}, null, "0, 1", true, null, null);

                        if (cur.getCount() > 0) {
                            cur.moveToFirst();
                           /* while (!cur.isAfterLast()) {*/
                            strUpdatedDate = cur.getString(0);
                            strDocumentLocal = cur.getString(1);
                            /*    cur.moveToNext();
                            }*/
                        }
                        CareGiver.dbCon.closeCursor(cur);
                    }
                } catch (Exception e) {
                    CareGiver.dbCon.closeCursor(cur);
                    e.printStackTrace();
                }
            }

            StorageService storageService = new StorageService(_ctxt);

            Query q1 = QueryBuilder.build("provider_email", strUserName, QueryBuilder.
                    Operator.EQUALS);

            Query q4, q2, q3;

            if (!strUpdatedDate.equalsIgnoreCase("")) {
                q2 = QueryBuilder.build("_updatedAt", strUpdatedDate, QueryBuilder.
                        Operator.GREATER_THAN);

                q3 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);

                q4 = q3;

            } else q4 = q1;

            storageService.findDocsByQueryOrderBy(Config.collectionProvider, q4, 1, 0,
                    "updated_date", 1, new App42CallBack() {
                        @Override
                        public void onSuccess(Object o) {
                            try {
                                if (o != null) {

                                    Storage storage = (Storage) o;

                                    if (storage.getJsonDocList().size() > 0) {

                                        Storage.JSONDocument jsonDocument = storage.getJsonDocList().
                                                get(0);
                                        String strDocument = jsonDocument.getJsonDoc();
                                        String _strProviderId = jsonDocument.getDocId();

                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("PROVIDER_ID", AESCrypt.encrypt(
                                                Config.string, _strProviderId));
                                        editor.apply();

                                        String strUpdatedDate = jsonDocument.getUpdatedAt();

                                        Cursor cur = null;

                                        try {

                                            cur = CareGiver.dbCon.fetch(
                                                    DbHelper.strTableNameCollection,
                                                    new String[]{"id"}, "object_id=?",
                                                    new String[]{_strProviderId}, null, null, false,
                                                    null, null);

                                            if (cur.getCount() <= 0) {
                                                CareGiver.dbCon.insert(
                                                        DbHelper.strTableNameCollection,
                                                        new String[]{_strProviderId, strUpdatedDate,
                                                                strDocument,
                                                                Config.collectionProvider, "1"},
                                                        new String[]{"object_id", "updated_date",
                                                                "document", "collection_name",
                                                                "status"});
                                            } else {
                                                CareGiver.dbCon.update(
                                                        DbHelper.strTableNameCollection,
                                                        "object_id=? and collection_name=?",
                                                        new String[]{strUpdatedDate, strDocument},
                                                        new String[]{"updated_date", "document"},
                                                        new String[]{_strProviderId,
                                                                Config.collectionProvider});
                                            }

                                            CareGiver.dbCon.closeCursor(cur);
                                        } catch (Exception e) {
                                            CareGiver.dbCon.closeCursor(cur);
                                            e.printStackTrace();
                                        }

                                        createProviderModel(strDocument, _strProviderId);

                                    } else {

                                        if (!strDocumentLocal.equalsIgnoreCase("")) {
                                            createProviderModel(strDocumentLocal, strProviderId);

                                        } else {

                                            utils.toast(2, 2, _ctxt.getString(R.string.error));
                                        }
                                    }

                                } else {

                                    utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();

                            }
                        }

                        @Override
                        public void onException(Exception e) {

                            try {
                                if (e != null) {
                                    utils.toast(2, 2, _ctxt.getString(R.string.error));
                                } else {
                                    utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
        }
    }
}