package com.hdfc.libs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.hdfc.app42service.App42GCMService;
import com.hdfc.app42service.StorageService;
import com.hdfc.caregiver.DashboardActivity;
import com.hdfc.caregiver.LoginActivity;
import com.hdfc.caregiver.R;
import com.hdfc.caregiver.fragments.DashboardFragment;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbCon;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.ClientModel;
import com.hdfc.models.ClientNameModel;
import com.hdfc.models.CustomerModel;
import com.hdfc.models.DependentModel;
import com.hdfc.models.FeedBackModel;
import com.hdfc.models.FieldModel;
import com.hdfc.models.FileModel;
import com.hdfc.models.ImageModel;
import com.hdfc.models.MilestoneModel;
import com.hdfc.models.MilestoneViewModel;
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
import java.util.Date;

/**
 * Created by Admin on 4/25/2016.
 */
public class AppUtils {
    private static Context _ctxt;
    private static StorageService storageService;
    //private static ProgressDialog progressDialog;
    private static Utils utils;

    private static Date startDate, endDate;

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

        Calendar calendar = Calendar.getInstance();

        String strStartDateCopy, strEndDateCopy;

        try {
            Date dateNow = calendar.getTime();
            strEndDateCopy = Utils.writeFormatDateDB.format(dateNow) + "T23:59:59.999Z";
            strStartDateCopy = Utils.writeFormatDateDB.format(dateNow) + "T00:00:00.000Z";

            endDate = utils.convertStringToDate(strEndDateCopy);
            startDate = utils.convertStringToDate(strStartDateCopy);

        } catch (Exception e) {
            e.printStackTrace();
        }
        ///
    }

    public static void logout() {
        try {
            Config.jsonObject = null;


            Config.intSelectedMenu = 0;

            Config.boolIsLoggedIn = false;

            //Config.fileModels.clear();
            if (CareGiver.dbCon != null)
                CareGiver.dbCon.deleteFiles();

            SharedPreferences.Editor editor = _ctxt.getSharedPreferences(Config.strPreferenceName,
                    Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();

            File fileImage = Utils.createFileInternal("images/");
            Utils.deleteAllFiles(fileImage);

            unregisterGcm();

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

                    Intent dashboardIntent = new Intent(_ctxt, LoginActivity.class);
                    _ctxt.startActivity(dashboardIntent);
                    ((Activity) _ctxt).finish();

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

    private void fetchDependents(final int iFlag) {

        if (Config.dependentIds.size() > 0) {

            if (utils.isConnectingToInternet()) {

                Query query = QueryBuilder.build("_id", Config.dependentIds, QueryBuilder.Operator.INLIST);

                storageService.findDocsByQuery(Config.collectionDependent, query, new App42CallBack() {

                            @Override
                            public void onSuccess(Object o) {
                                try {
                                    //System.out.println("NACHIKET : "+(o!=null));
                                    if (o != null) {

                                        Utils.log(o.toString(), " MESS 1");

                                        Storage storage = (Storage) o;

                                        if (storage.getJsonDocList().size() > 0) {

                                            for (int i = 0; i < storage.getJsonDocList().size(); i++) {

                                                Storage.JSONDocument jsonDocument = storage.
                                                        getJsonDocList().get(i);

                                                String strDocument = jsonDocument.getJsonDoc();
                                                String strDependentDocId = jsonDocument.
                                                        getDocId();
                                                createDependentModel(strDependentDocId, strDocument, iFlag);
                                            }
                                        }
                                    }
                                    if (iFlag == 2)
                                        DashboardActivity.gotoSimpleActivityMenu();
                                    else
                                        DashboardActivity.refreshClientsData();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onException(Exception e) {
                                Utils.log(e.getMessage(), " MESS 1");
                                if (iFlag == 2)
                                    DashboardActivity.gotoSimpleActivityMenu();
                                else
                                    DashboardActivity.refreshClientsData();
                            }

                        });

            } else {
                if (iFlag == 2)
                    DashboardActivity.gotoSimpleActivityMenu();
                else
                    DashboardActivity.refreshClientsData();
            }

        } else {
            if (iFlag == 2)
                DashboardActivity.gotoSimpleActivityMenu();
            else
                DashboardActivity.refreshClientsData();
        }
    }

    private void fetchCustomers(final int iFlag) {

        if (Config.customerIds.size() > 0) {

            if (utils.isConnectingToInternet()) {

                final Query query = QueryBuilder.build("_id", Config.customerIds,
                        QueryBuilder.Operator.INLIST);

              /*  try {
                    Utils.log(query.get(), " query ");
                }catch (Exception e){
                    e.printStackTrace();
                }*/

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
                                                createCustomerModel(strDependentDocId, strDocument, iFlag);
                                            }
                                        }
                                        fetchDependents(iFlag);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    //fetchDependents(iFlag);
                                }
                            }

                            @Override
                            public void onException(Exception e) {
                                Utils.log(e.getMessage(), " MESS 0");
                                fetchDependents(iFlag);
                            }

                        });
            } else {
                fetchDependents(iFlag);
            }

        } else fetchDependents(iFlag);
    }

    private void createDependentModel(String strDocumentId, String strDocument, int iFlag) {

        try {

            JSONObject jsonObjectDependent = new JSONObject(strDocument);

            if (!Config.dependentIdsAdded.contains(strDocumentId)) {

                ///
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
                ///

                Config.dependentIdsAdded.add(strDocumentId);

                Config.dependentModels.add(dependentModel);

                if (!Config.strDependentNames.contains(jsonObjectDependent.getString("dependent_name")))
                    Config.strDependentNames.add(jsonObjectDependent.getString("dependent_name"));

                if (iFlag == 2) {
                    int iPosition = Config.customerIdsAdded.indexOf(jsonObjectDependent.getString("customer_id"));

                    if (Config.clientModels.size() > 0) {
                        if (iPosition > -1 && iPosition < Config.clientModels.size())//todo check
                            Config.clientModels.get(iPosition).setDependentModel(dependentModel);
                    }

                    if (Config.clientNameModels.size() > 0) {
                        if (iPosition > -1 && iPosition < Config.clientNameModels.size()) {
                            Config.clientNameModels.get(iPosition).removeStrDependentName(jsonObjectDependent.getString("dependent_name"));
                            Config.clientNameModels.get(iPosition).setStrDependentName(jsonObjectDependent.getString("dependent_name"));
                        }
                    }
                }

               /* Config.fileModels.add(new FileModel(strDocumentId,
                        jsonObjectDependent.getString("dependent_profile_url"), "IMAGE"));*/

                //
                String strUrl = jsonObjectDependent.getString("dependent_profile_url");

                String strUrlHash = Utils.sha512(strUrl);

                Cursor cur = CareGiver.dbCon.fetch(
                        DbHelper.strTableNameFiles, new String[]{"file_hash"}, "name=?",
                        new String[]{strDocumentId}, null, "0, 1", true, null, null
                );

                Utils.log(strUrlHash, " HASH ");


                String strHashLocal = "";

                if (cur.getCount() <= 0) {
                    CareGiver.dbCon.insert(DbHelper.strTableNameFiles, new String[]{strDocumentId,
                                    strUrl, "IMAGE", strUrlHash},
                            new String[]{"name", "url", "file_type", "file_hash"});
                } else {

                    cur.moveToFirst();
                    strHashLocal = cur.getString(0);
                    cur.moveToNext();
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
            } else {
                if (iFlag == 2) {

                    int iPosition = Config.customerIdsAdded.indexOf(jsonObjectDependent.getString("customer_id"));

                    if (Config.clientModels.size() > 0) {

                        if (iPosition > -1) {
                            int iPosition1 = Config.dependentIdsAdded.indexOf(strDocumentId);
                            if (iPosition1 > -1 && iPosition <= Config.clientModels.size()) {
                                DependentModel dependentModel = Config.dependentModels.get(iPosition1);
                                Config.clientModels.get(iPosition).setDependentModel(dependentModel);
                            }
                        }
                    }

                    //
                    if (Config.clientNameModels.size() > 0) {
                        if (iPosition > -1 && iPosition <= Config.clientNameModels.size()) {
                            Config.clientNameModels.get(iPosition).removeStrDependentName(jsonObjectDependent.getString("dependent_name"));
                            Config.clientNameModels.get(iPosition).setStrDependentName(jsonObjectDependent.getString("dependent_name"));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createCustomerModel(String strDocumentId, String strDocument, int iFlag) {
        try {

            JSONObject jsonObject = new JSONObject(strDocument);

            if (jsonObject.has("customer_name")) {

                //Utils.log(String.valueOf(Config.customerIds.contains(strDocumentId)), " 1 ");

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

                    if (!Config.strCustomerNames.contains(jsonObject.getString("customer_name")))
                        Config.strCustomerNames.add(jsonObject.getString("customer_name"));

                    if (iFlag == 2) {
                        ClientModel clientModel = new ClientModel();
                        clientModel.setCustomerModel(customerModel);
                        Config.clientModels.add(clientModel);

                        ClientNameModel clientNameModel = new ClientNameModel();
                        clientNameModel.setStrCustomerName(jsonObject.getString("customer_name"));

                        Config.clientNameModels.add(clientNameModel);
                    }

                    Config.customerModels.add(customerModel);

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
                        strHashLocal = cur.getString(0);
                        cur.moveToNext();


                        if (!strHashLocal.equalsIgnoreCase(strUrlHash)) {
                            CareGiver.dbCon.update(
                                    DbHelper.strTableNameFiles, "name=?",
                                    new String[]{strUrl, strUrlHash},
                                    new String[]{"url", "file_hash"}, new String[]{strDocumentId}
                            );
                        }
                    }

                    CareGiver.dbCon.closeCursor(cur);

                } else {
                    if (iFlag == 2) {

                        int iPosition = Config.strCustomerNames.indexOf(jsonObject.getString("customer_name"));

                        if (iPosition > -1) {
                            CustomerModel customerModel = Config.customerModels.get(iPosition);

                            ClientModel clientModel = new ClientModel();
                            clientModel.setCustomerModel(customerModel);
                            Config.clientModels.add(clientModel);
                        }

                        //
                        ClientNameModel clientNameModel = new ClientNameModel();
                        clientNameModel.setStrCustomerName(jsonObject.getString("customer_name"));

                        Config.clientNameModels.add(clientNameModel);
                        //
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fetchActivities() {


        /*String strUpdatedDate="";

        if (DbCon.isDbOpened) {

            Cursor cur = null;

            try {

                if(!strProviderId.equalsIgnoreCase("")) {

                //_$createdAt

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

        Query q1 = QueryBuilder.build("provider_id", Config.providerModel.getStrProviderId(),
                QueryBuilder.Operator.EQUALS);

        Query q2 = QueryBuilder.build("activity_date", DashboardFragment.strEndDate, QueryBuilder.
                Operator.LESS_THAN_EQUALTO);

        Query q3 = QueryBuilder.build("activity_date", DashboardFragment.strStartDate, QueryBuilder.
                Operator.GREATER_THAN_EQUALTO);

        Query q4 = QueryBuilder.compoundOperator(q2, QueryBuilder.Operator.AND, q3);

        Query q7 = QueryBuilder.build("milestones.scheduled_date", DashboardFragment.strEndDate, QueryBuilder.
                Operator.LESS_THAN_EQUALTO);

        Query q8 = QueryBuilder.build("milestones.scheduled_date", DashboardFragment.strStartDate, QueryBuilder.
                Operator.GREATER_THAN_EQUALTO);

        //
        Query q10 = QueryBuilder.build("milestones.status", Config.MilestoneStatus.COMPLETED, QueryBuilder.
                Operator.NOT_EQUALS);

        //

        Query q9 = QueryBuilder.compoundOperator(q7, QueryBuilder.Operator.AND, q8);

        Query q11 = QueryBuilder.compoundOperator(q9, QueryBuilder.Operator.AND, q10);

        Query q5 = QueryBuilder.compoundOperator(q4, QueryBuilder.Operator.OR, q11);

        Query q6 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q5);


       /* Query q7 = QueryBuilder.build("_id",  Config.strActivityIds, QueryBuilder.
                Operator.INLIST);*/

        // Query q12 = QueryBuilder.compoundOperator(q10, QueryBuilder.Operator.AND, q6);

        try {
            Utils.log(q6.get(), " QUERY ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        storageService.findDocsByQueryOrderBy(Config.collectionActivity, q6, 3000, 0,
                "milestones.scheduled_date", 1,
                new App42CallBack() {

                    @Override
                    public void onSuccess(Object o) {
                        if (o != null) {

                            Utils.log(o.toString(), " Activity All SSS ");

                            Storage storage = (Storage) o;

                            ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();

                            for (int i = 0; i < jsonDocList.size(); i++) {

                                Storage.JSONDocument jsonDocument = jsonDocList.get(i);
                                String strDocumentId = jsonDocument.getDocId();
                                String strActivities = jsonDocument.getJsonDoc();
                                createActivityModel(strDocumentId, strActivities, 1);
                            }
                        }
                        fetchCustomers(1);
                    }

                    @Override
                    public void onException(Exception ex) {
                        Utils.log(ex.getMessage(), " f1 ");
                        fetchCustomers(1);
                    }
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

    public void createActivityModel(String strDocumentId, final String strDocument, int iFlag) {

        try {

            JSONObject jsonObject = new JSONObject(strDocument);

            if (jsonObject.has("dependent_id")) {

                boolean bActivity = false, bMilestone = false, bContinue = false, bToday = false;


                /////
                if (jsonObject.has("activity_date")) {
                    Calendar calendar = Calendar.getInstance();

                    Date startDate = null, endDate = null;
                    String strStartDateCopy, strEndDateCopy;
                    Date activityDate = null;

                    try {
                        Date dateNow = calendar.getTime();
                        strEndDateCopy = Utils.writeFormatDateDB.format(dateNow) + "T23:59:59.999Z";
                        strStartDateCopy = Utils.writeFormatDateDB.format(dateNow) + "T00:00:00.000Z";

                        activityDate = utils.convertStringToDate(jsonObject.getString("activity_date"));

                        endDate = utils.convertStringToDate(strEndDateCopy);
                        startDate = utils.convertStringToDate(strStartDateCopy);

                        Utils.log(String.valueOf(endDate + " ! " + startDate + " ! " + activityDate), " CRATED ");

                        if (activityDate.before(endDate) && activityDate.after(startDate)) {
                            bToday = true;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                ///

                if (iFlag == 1 || bToday)
                    bContinue = Config.strActivityIds.contains(strDocumentId);

                if (iFlag == 2)
                    bContinue = Config.strActivityIdsNotifications.contains(strDocumentId);

                if (!bContinue) {

                    //Config.strActivityIds.add(strDocumentId);

                    if (iFlag == 1 || bToday)
                        Config.strActivityIds.add(strDocumentId);

                    if (iFlag == 2)
                        Config.strActivityIdsNotifications.add(strDocumentId);


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

                    if (jsonObject.has("activity_date")) {
                        activityModel.setStrActivityDate(jsonObject.getString("activity_date"));

                        /////////
                        try {

                            Date activityDate = utils.convertStringToDate(activityModel.getStrActivityDate());
                            if (activityDate.before(endDate) && activityDate.after(startDate))
                                bActivity = true;

                            Utils.log(String.valueOf(endDate + " ! " + startDate + " ! " + activityDate), " CRATED 0");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    /////////

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
                                    strHashLocal = cur.getString(0);
                                    cur.moveToNext();
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
                                        jsonObjectImage.getString("image_taken"),
                                        "");

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
                                    strHashLocal = cur.getString(0);
                                    cur.moveToNext();
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
                                        jsonObjectFeedback.getString("feedback_time"),
                                        jsonObjectFeedback.getString("feedback_by_type"));

                                try {
                                    feedBackModel.setbFeedBackReport(jsonObjectFeedback.getBoolean("feedback_report"));
                                } catch (Exception e) {
                                    try {
                                        String strTemp = jsonObjectFeedback.getString("feedback_report");
                                        boolean b = false;
                                        if (strTemp.equalsIgnoreCase("1"))
                                            b = true;

                                        feedBackModel.setbFeedBackReport(b);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }

                                feedBackModels.add(feedBackModel);

                                if (iFlag == 1 || bToday) {

                                    if (jsonObjectFeedback.getString("feedback_by_type").equalsIgnoreCase("customer")) {
                                        if (!Config.customerIds.contains(jsonObjectFeedback.getString("feedback_by")))
                                            Config.customerIds.add(jsonObjectFeedback.getString("feedback_by"));
                                    }

                                    if (jsonObjectFeedback.getString("feedback_by_type").equalsIgnoreCase("dependent")) {
                                        if (!Config.dependentIds.contains(jsonObjectFeedback.getString("feedback_by")))
                                            Config.dependentIds.add(jsonObjectFeedback.getString("feedback_by"));
                                    }

                                    Config.iRatings += jsonObjectFeedback.getInt("feedback_rating");

                                    Config.iRatingCount += 1;

                                    Config.feedBackModels.add(feedBackModel);
                                }
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


                            /////////////
                            if (jsonObjectMilestone.has("files")) {

                                JSONArray jsonArrayMsFiles = jsonObjectMilestone.
                                        getJSONArray("files");

                                for (int m = 0; m < jsonArrayMsFiles.length(); m++) {

                                    JSONObject jsonObjectMsFile = jsonArrayMsFiles.
                                            getJSONObject(m);

                                    if (jsonObjectMsFile.has("file_name")) {

                                        FileModel fileModel = new FileModel(
                                                jsonObjectMsFile.getString("file_name"),
                                                jsonObjectMsFile.getString("file_url"),
                                                jsonObjectMsFile.getString("file_type"),
                                                jsonObjectMsFile.getString("file_time"),
                                                jsonObjectMsFile.getString("file_desc"),
                                                jsonObjectMsFile.getString("file_path"));

                                        milestoneModel.setFileModel(fileModel);

                                        ///
                                        String strUrlHash = Utils.sha512(jsonObjectMsFile.getString("file_url"));

                                        Cursor cur = CareGiver.dbCon.fetch(
                                                DbHelper.strTableNameFiles, new String[]{"file_hash"}, "name=?",
                                                new String[]{jsonObjectMsFile.getString("file_name")}, null, "0, 1", true, null, null
                                        );

                                        String strHashLocal = "";

                                        if (cur.getCount() <= 0) {
                                            CareGiver.dbCon.insert(DbHelper.strTableNameFiles, new String[]{jsonObjectMsFile.getString("file_name"),
                                                            jsonObjectMsFile.getString("file_url"), "IMAGE", strUrlHash},
                                                    new String[]{"name", "url", "file_type", "file_hash"});
                                        } else {

                                            cur.moveToFirst();
                                            strHashLocal = cur.getString(0);
                                            cur.moveToNext();
                                            CareGiver.dbCon.closeCursor(cur);

                                            if (!strHashLocal.equalsIgnoreCase(strUrlHash)) {
                                                CareGiver.dbCon.update(
                                                        DbHelper.strTableNameFiles, "name=?",
                                                        new String[]{jsonObjectMsFile.getString("file_url"), strUrlHash},
                                                        new String[]{"url", "file_hash"}, new String[]{jsonObjectMsFile.getString("file_name")}
                                                );
                                            }
                                        }

                                        CareGiver.dbCon.closeCursor(cur);
                                    }
                                }
                            }

                            //////////////////////

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

                            if (jsonObjectMilestone.has("scheduled_date")) {
                                milestoneModel.setStrMilestoneScheduledDate(jsonObjectMilestone.
                                        getString("scheduled_date"));

                                try {

                                    if (!jsonObjectMilestone.
                                            getString("scheduled_date").equalsIgnoreCase("")) {
                                        Date activityDate = utils.convertStringToDate(jsonObjectMilestone.
                                                getString("scheduled_date"));

                                        if (activityDate.before(endDate) && activityDate.after(startDate))
                                            bMilestone = true;

                                        Utils.log(String.valueOf(endDate + " ! " + startDate + " ! " + activityDate), " CRATED 1 ");
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

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

                                    ////
                                    if (jsonObjectField.has("array_fields")) {

                                        try {
                                            fieldModel.setiArrayCount(jsonObjectField.getInt("array_fields"));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            int i = 0;
                                            try {
                                                i = Integer.parseInt(jsonObjectField.getString("array_fields"));
                                                fieldModel.setiArrayCount(i);
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                            }
                                        }

                                        if (jsonObjectField.has("array_type"))
                                            fieldModel.setStrArrayType(utils.jsonToStringArray(jsonObjectField.
                                                    getJSONArray("array_type")));

                                        if (jsonObjectField.has("array_data"))
                                            fieldModel.setStrArrayData(jsonObjectField.getString("array_data"));

                                    }
                                    ////

                                    milestoneModel.setFieldModel(fieldModel);
                                }
                            }

                            int iTemp = 0;

                            if (bActivity)
                                iTemp = 1;

                            if (bMilestone)
                                iTemp = 2;

                            if (bActivity && bMilestone)
                                iTemp = 3;

                            activityModel.setiActivityDisplayFlag(iTemp);
                            activityModel.setMilestoneModel(milestoneModel);
                        }
                    }

                    if (iFlag == 1 || bToday)
                        Config.activityModels.add(activityModel);

                    if (iFlag == 2)
                        Config.activityModelsNotifications.add(activityModel);
                }
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //MS
    private void createActivityMSModel(String strDocumentId, final String strDocument) {

        try {

            JSONObject jsonObject = new JSONObject(strDocument);

            if (jsonObject.has("dependent_id")) {

                MilestoneViewModel milestoneViewModel = null;

                if (!Config.strActivityIds.contains(strDocumentId)) {
                    Config.strActivityIds.add(strDocumentId);

                    if (!Config.dependentIds.contains(jsonObject.getString("dependent_id")))
                        Config.dependentIds.add(jsonObject.getString("dependent_id"));

                    if (!Config.customerIds.contains(jsonObject.getString("customer_id")))
                        Config.customerIds.add(jsonObject.getString("customer_id"));

                    //
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
                                    strHashLocal = cur.getString(0);
                                    cur.moveToNext();
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
                                        jsonObjectImage.getString("image_taken"), "");

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
                                    strHashLocal = cur.getString(0);
                                    cur.moveToNext();
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
                                        jsonObjectFeedback.getString("feedback_time"),
                                        jsonObjectFeedback.getString("feedback_by_type"));

                                try {
                                    feedBackModel.setbFeedBackReport(jsonObjectFeedback.getBoolean("feedback_report"));
                                } catch (Exception e) {
                                    try {
                                        String strTemp = jsonObjectFeedback.getString("feedback_report");
                                        boolean b = false;
                                        if (strTemp.equalsIgnoreCase("1"))
                                            b = true;

                                        feedBackModel.setbFeedBackReport(b);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }

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

                            milestoneViewModel = new MilestoneViewModel();

                            milestoneViewModel.setStrActivityId(strDocumentId);
                            milestoneViewModel.setStrDependentId(jsonObject.getString("dependent_id"));

                            JSONObject jsonObjectMilestone =
                                    jsonArrayMilestones.getJSONObject(k);

                            MilestoneModel milestoneModel = new MilestoneModel();

                            milestoneModel.setiMilestoneId(jsonObjectMilestone.getInt("id"));
                            milestoneModel.setStrMilestoneStatus(jsonObjectMilestone.getString("status"));
                            milestoneModel.setStrMilestoneName(jsonObjectMilestone.getString("name"));
                            milestoneModel.setStrMilestoneDate(jsonObjectMilestone.getString("date"));

                            milestoneViewModel.setStrMileStoneName(jsonObjectMilestone.getString("name"));
                            milestoneViewModel.setStrMileStoneStatus(jsonObjectMilestone.getString("status"));

                            //milestoneViewModel.setStrDependentId(jsonObject.getString("dependent_id"));

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

                            if (jsonObjectMilestone.has("scheduled_date")) {
                                milestoneModel.setStrMilestoneScheduledDate(jsonObjectMilestone.
                                        getString("scheduled_date"));
                                milestoneViewModel.setStrMilestoneDate(jsonObjectMilestone.
                                        getString("scheduled_date"));
                            }

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

                                    ////
                                    if (jsonObjectField.has("array_fields")) {

                                        try {
                                            fieldModel.setiArrayCount(jsonObjectField.getInt("array_fields"));
                                        } catch (Exception e) {
                                            int i = 0;
                                            e.printStackTrace();
                                            try {
                                                i = Integer.parseInt(jsonObjectField.getString("array_fields"));
                                                fieldModel.setiArrayCount(i);
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                            }
                                        }

                                        if (jsonObjectField.has("array_type"))
                                            fieldModel.setStrArrayType(utils.jsonToStringArray(jsonObjectField.
                                                    getJSONArray("array_type")));

                                        if (jsonObjectField.has("array_data"))
                                            fieldModel.setStrArrayData(jsonObjectField.getString("array_data"));

                                    }
                                    ////

                                    milestoneModel.setFieldModel(fieldModel);
                                }
                            }

                            if (milestoneViewModel != null
                                    && milestoneViewModel.getStrMileStoneStatus() != null
                                    && milestoneViewModel.getStrMilestoneDate() != null
                                    && !milestoneViewModel.getStrMilestoneDate().equalsIgnoreCase("")
                                    && !milestoneViewModel.getStrMileStoneStatus().equalsIgnoreCase("completed")) {

                                Date dateActual = utils.convertStringToDate(milestoneViewModel.getStrMilestoneDate());
                                Date dateStart = utils.convertStringToDate(DashboardFragment.strStartDate);
                                Date dateEnd = utils.convertStringToDate(DashboardFragment.strEndDate);

                                if (dateActual.after(dateStart) && dateActual.before(dateEnd)) {
                                    Config.milestoneModels.remove(milestoneViewModel);
                                    Config.milestoneModels.add(milestoneViewModel);
                                }
                            }

                            activityModel.setMilestoneModel(milestoneModel);
                        }
                    }
                    Config.activityModels.add(activityModel);
                    //
                } else {

                    ActivityModel activityModel = null;

                    int iPosition = Config.strActivityIds.indexOf(strDocumentId);

                    if (iPosition > -1) activityModel = Config.activityModels.get(iPosition);

                    if (activityModel != null) {

                        for (MilestoneModel milestoneModel : activityModel.getMilestoneModels()) {

                            milestoneViewModel = new MilestoneViewModel();

                            milestoneViewModel.setStrActivityId(activityModel.getStrActivityID());
                            milestoneViewModel.setStrDependentId(activityModel.getStrDependentID());

                            milestoneViewModel.setStrMileStoneName(milestoneModel.getStrMilestoneName());
                            milestoneViewModel.setStrMileStoneStatus(milestoneModel.getStrMilestoneStatus());
                            milestoneViewModel.setStrMilestoneDate(milestoneModel.getStrMilestoneScheduledDate());

                            if (milestoneViewModel != null
                                    && milestoneViewModel.getStrMileStoneStatus() != null
                                    && !milestoneViewModel.getStrMileStoneStatus().equalsIgnoreCase("completed")
                                    && milestoneViewModel.getStrMilestoneDate() != null
                                    && !milestoneViewModel.getStrMilestoneDate().equalsIgnoreCase("")
                                    ) {

                                Date dateActual = utils.convertStringToDate(milestoneViewModel.getStrMilestoneDate());
                                Date dateStart = utils.convertStringToDate(DashboardFragment.strStartDate);
                                Date dateEnd = utils.convertStringToDate(DashboardFragment.strEndDate);

                                if (dateActual.after(dateStart) && dateActual.before(dateEnd)) {
                                    Config.milestoneModels.remove(milestoneViewModel);
                                    Config.milestoneModels.add(milestoneViewModel);
                                }
                            }
                            //activityModel.setMilestoneModel(milestoneModel);
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //MS

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


            //Config.serviceModels.add(serviceModel);

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

                                //fieldModel.setChild(jsonObjectField.getBoolean("child"));

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

                            ////
                            if (jsonObjectField.has("array_fields")) {

                                try {
                                    fieldModel.setiArrayCount(jsonObjectField.getInt("array_fields"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    int i = 0;
                                    try {
                                        i = Integer.parseInt(jsonObjectField.getString("array_fields"));
                                        fieldModel.setiArrayCount(i);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }

                                if (jsonObjectField.has("array_type"))
                                    fieldModel.setStrArrayType(utils.jsonToStringArray(jsonObjectField.
                                            getJSONArray("array_type")));

                                if (jsonObjectField.has("array_data"))
                                    fieldModel.setStrArrayData(jsonObjectField.getString("array_data"));

                            }
                            ////

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

    //refresh Providers
    public void fetchClients(final int iFlag) {

        if (utils.isConnectingToInternet()) {
            //fetchCustomers(2);
            StorageService storageService = new StorageService(_ctxt);

            Query q1 = QueryBuilder.build("provider_id", Config.providerModel.getStrProviderId(),
                    QueryBuilder.Operator.EQUALS);

            storageService.findDocsByQueryOrderBy(Config.collectionProviderDependent, q1, 1000, 0,
                    "provider_id", 1, new App42CallBack() {
                        @Override
                        public void onSuccess(Object o) {
                            try {
                                if (o != null) {

                                    Storage storage = (Storage) o;

                                    if (storage.getJsonDocList().size() > 0) {

                                        ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();

                                        for (int i = 0; i < jsonDocList.size(); i++) {

                                            Storage.JSONDocument jsonDocument = storage.getJsonDocList().
                                                    get(i);

                                            String strDocument = jsonDocument.getJsonDoc();

                                            try {
                                                JSONObject jsonObject = new JSONObject(strDocument);

                                                if (!Config.customerIds.contains(jsonObject.getString("customer_id")))
                                                    Config.customerIds.add(jsonObject.getString("customer_id"));

                                                if (!Config.dependentIds.contains(jsonObject.getString("dependent_id")))
                                                    Config.dependentIds.add(jsonObject.getString("dependent_id"));

                                                //fetchActivities(relativeLayout);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                                Utils.log(" 0 ", " MESS ");
                                fetchCustomers(iFlag);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                fetchCustomers(iFlag);
                            }
                        }

                        @Override
                        public void onException(Exception e) {
                            Utils.log(e.getMessage(), " MESS ");
                            fetchCustomers(iFlag);
                        }
                    });
        } else {
            fetchCustomers(iFlag);
        }
    }

    //fetch mile stones
    public void fetchMileStone() {//final ProgressDialog progressDialog

        if (utils.isConnectingToInternet()) {

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

            Query q1 = QueryBuilder.build("provider_id", Config.providerModel.getStrProviderId(),
                    QueryBuilder.Operator.EQUALS);

            Query q2 = QueryBuilder.build("milestones.scheduled_date", DashboardFragment.strEndDate, QueryBuilder.
                    Operator.LESS_THAN_EQUALTO);

            Query q3 = QueryBuilder.build("milestones.scheduled_date", DashboardFragment.strStartDate, QueryBuilder.
                    Operator.GREATER_THAN_EQUALTO);

           /* if (Config.strActivityIds.size() > 0){

                JSONArray jsonArray = new JSONArray();

                for (String strIds : Config.strActivityIds) {

                    try {
                        JSONObject ex1 = new JSONObject();
                        ex1.put("key", "_id");
                        ex1.put("value", strIds);
                        ex1.put("operator", QueryBuilder.Operator.NOT_EQUALS);
                        query = new Query(ex1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

              *//*  [[{"key":"provider_id","value":"5715c39ee4b0d2aca6fe5d17","operator":"$eq"},
                {"compoundOpt":"$and"},[{"key":"milestones.scheduled_date",
                "value":"2016-05-26T23:59:59.999+0000","operator":"$lte"},{"compoundOpt":"$and"},
                {"key":"milestones.scheduled_date","value":"2016-05-26T00:00:00.000+0000","operator":"$gte"}]],
                {"compoundOpt":"$and"},{"key":"milestones.status","value":"COMPLETED","operator":"$ne"}]*//*


                }
            }
*/
            Query q4 = QueryBuilder.compoundOperator(q2, QueryBuilder.Operator.AND, q3);

            Query q5 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q4);

            Query q6 = QueryBuilder.build("milestones.status", Config.MilestoneStatus.COMPLETED, QueryBuilder.
                    Operator.NOT_EQUALS);

           /* Query q7 = QueryBuilder.build("_id",  Config.strActivityIds, QueryBuilder.
                    Operator.INLIST);*/

            Query q8 = QueryBuilder.compoundOperator(q5, QueryBuilder.Operator.AND, q6);

            try {
                Utils.log(q8.get(), " QUERY ");
            } catch (Exception e) {
                e.printStackTrace();
            }

            storageService.findDocsByQueryOrderBy(Config.collectionActivity, q8, 1000, 0,
                    "milestones.scheduled_date", 1,
                    new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {
                            try {

                                if (o != null) {

                                    Storage storage = (Storage) o;

                                    //Utils.log(storage.toString(), " MS ");

                                    ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();

                                    for (int i = 0; i < jsonDocList.size(); i++) {

                                        Storage.JSONDocument jsonDocument = jsonDocList.get(i);
                                        String strDocumentId = jsonDocument.getDocId();
                                        String strActivities = jsonDocument.getJsonDoc();
                                        createActivityMSModel(strDocumentId, strActivities);
                                    }
                                }
                                fetchCustomers(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                                fetchCustomers(1);
                            }
                        }

                        @Override
                        public void onException(Exception ex) {

                            if (ex != null) {
                                Utils.log(ex.getMessage(), " f2 ");
                            }
                            fetchCustomers(1);
                        }
                    });
        } else {
            fetchCustomers(1);
        }
    }
}