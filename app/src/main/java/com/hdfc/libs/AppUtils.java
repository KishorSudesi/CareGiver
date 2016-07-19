package com.hdfc.libs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hdfc.app42service.App42GCMService;
import com.hdfc.app42service.StorageService;
import com.hdfc.caregiver.DashboardActivity;
import com.hdfc.caregiver.LoginActivity;
import com.hdfc.caregiver.fragments.DashboardFragment;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
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
import com.hdfc.models.NotificationModel;
import com.hdfc.models.ProviderModel;
import com.hdfc.models.ServiceModel;
import com.hdfc.models.VideoModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import net.sqlcipher.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Admin on 4/25/2016.
 */
public class AppUtils {
    private static StorageService storageService;
    private static Date startDate, endDate;
    //private Context context;
    private Utils utils;

    private Set<String> categorySet = new HashSet<>();

    public AppUtils(Context context) {

        utils = new Utils(context);
        //progressDialog = new ProgressDialog(context);
        storageService = new StorageService(context);
       /* sharedPreferences = context.getSharedPreferences(Config.strPreferenceName, Context.MODE_PRIVATE);

        strProviderId = sharedPreferences.getString("PROVIDER_ID", "");*/

        //this.context = context;

        Calendar calendar = Calendar.getInstance();

        String strStartDateCopy, strEndDateCopy;

        try {
            Date dateNow = calendar.getTime();
            strEndDateCopy = Utils.writeFormatDateDB.format(dateNow) + "T23:59:59.999Z";
            strStartDateCopy = Utils.writeFormatDateDB.format(dateNow) + "T00:00:00.000Z";

            endDate = Utils.convertStringToDate(strEndDateCopy);
            startDate = Utils.convertStringToDate(strStartDateCopy);

        } catch (Exception e) {
            e.printStackTrace();
        }
        ///
    }

    public static void logout(Context _context) {
        try {
            Config.jsonObject = null;


            Config.intSelectedMenu = 0;

            Config.boolIsLoggedIn = false;

            //Config.fileModels.clear();
            if (CareGiver.getDbCon() != null)
                CareGiver.getDbCon().truncateDatabase();

            SharedPreferences.Editor editor = _context.getSharedPreferences(Config.strPreferenceName,
                    Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();

            File fileImage = Utils.createFileInternal("images/");
            Utils.deleteAllFiles(fileImage);

            unregisterGcm(_context);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void unregisterGcm(final Context _context) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(_context);
                    //App42GCMService.unRegisterGcm();

                    try {
                        if (gcm != null)
                            gcm.unregister();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Intent dashboardIntent = new Intent(_context, LoginActivity.class);
                    _context.startActivity(dashboardIntent);
                    ((Activity) _context).finish();

                } catch (Exception bug) {
                    bug.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static void loadNotifications(Context context) {

        //todo optimize data fetch and multiple pushes
        if (Utils.isConnectingToInternet(context)) {

            String strDate = DbHelper.DEFAULT_DB_DATE;

            Cursor cursor = CareGiver.getDbCon().getMaxDate(Config.collectionNotification);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                strDate = cursor.getString(0);
            }

            CareGiver.getDbCon().closeCursor(cursor);

            StorageService storageService = new StorageService(context);

            Query q1 = QueryBuilder.build("user_id", Config.providerModel.getStrProviderId(),
                    QueryBuilder.Operator.EQUALS);

            Query finalQuery;

            if (strDate != null && !strDate.equalsIgnoreCase("")) {
                Query q12 = QueryBuilder.build("_$updatedAt", strDate,
                        QueryBuilder.Operator.GREATER_THAN_EQUALTO);

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
                                                    Config.collectionNotification, "1", ""};

                                            CareGiver.getDbCon().insert(DbHelper.strTableNameCollection,
                                                    values,
                                                    DbHelper.COLLECTION_FIELDS);

                                        }
                                        CareGiver.getDbCon().dbTransactionSuccessFull();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        CareGiver.getDbCon().endDBTransaction();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onException(Exception e) {
                        }
                    });
        }
    }

    public static void fetchActivitiesSync(Context context) {

        String strDate = DbHelper.DEFAULT_DB_DATE;

        Cursor cursor = CareGiver.getDbCon().getMaxDate(Config.collectionActivity);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            strDate = cursor.getString(0);
        }

        CareGiver.getDbCon().closeCursor(cursor);

        SessionManager sessionManager = new SessionManager(context);

        Query q1 = QueryBuilder.build("provider_id", sessionManager.getProviderId(),
                QueryBuilder.Operator.EQUALS);

       /* Query q2 = QueryBuilder.build("activity_date", DashboardFragment.strEndDate, QueryBuilder.
                Operator.LESS_THAN_EQUALTO);

        Query q3 = QueryBuilder.build("activity_date", DashboardFragment.strStartDate, QueryBuilder.
                Operator.GREATER_THAN_EQUALTO);

        Query q4 = QueryBuilder.compoundOperator(q2, QueryBuilder.Operator.AND, q3);

        Query q7 = QueryBuilder.build("milestones.scheduled_date", DashboardFragment.strEndDate, QueryBuilder.
                Operator.LESS_THAN_EQUALTO);

        Query q8 = QueryBuilder.build("milestones.scheduled_date", DashboardFragment.strStartDate, QueryBuilder.
                Operator.GREATER_THAN_EQUALTO);

        Query q10 = QueryBuilder.build("milestones.status", Config.MilestoneStatus.COMPLETED, QueryBuilder.
                Operator.NOT_EQUALS);

        Query q9 = QueryBuilder.compoundOperator(q7, QueryBuilder.Operator.AND, q8);

        Query q11 = QueryBuilder.compoundOperator(q9, QueryBuilder.Operator.AND, q10);

        Query q5 = QueryBuilder.compoundOperator(q4, QueryBuilder.Operator.OR, q11);

        Query q6 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q5);*/

        Query finalQuery;

        if (strDate != null && !strDate.equalsIgnoreCase("")) {
            Query q12 = QueryBuilder.build("_$updatedAt", strDate, QueryBuilder.Operator.
                    GREATER_THAN_EQUALTO);

            finalQuery = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q12);
        } else {
            finalQuery = q1;
        }

       /* try {
            Utils.log(finalQuery.get(), " QUERY ");
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        StorageService storageService = new StorageService(context);

        storageService.findDocsByQueryOrderBy(Config.collectionActivity, finalQuery, 30000, 0,
                "milestones.scheduled_date", 1,
                new App42CallBack() {

                    @Override
                    public void onSuccess(Object o) {
                        if (o != null) {

                            Utils.log(o.toString(), " Activity All 90 ");

                            Storage storage = (Storage) o;

                            ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();

                            try {
                                CareGiver.getDbCon().beginDBTransaction();
                                for (int i = 0; i < jsonDocList.size(); i++) {

                                    Storage.JSONDocument jsonDocument = jsonDocList.get(i);

                                    ///
                                    String values[] = {jsonDocument.getDocId(),
                                            jsonDocument.getUpdatedAt(),
                                            jsonDocument.getJsonDoc(),
                                            Config.collectionActivity, "0", ""};

                                    String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                                    // WHERE clause arguments
                                    String[] selectionArgs = {jsonDocument.getDocId()};
                                    CareGiver.getDbCon().updateInsert(
                                            DbHelper.strTableNameCollection,
                                            selection, values, DbHelper.COLLECTION_FIELDS,
                                            selectionArgs);

                                    insertActivityDate(jsonDocument.getDocId(),
                                            jsonDocument.getJsonDoc());
                                }
                                CareGiver.getDbCon().dbTransactionSuccessFull();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                CareGiver.getDbCon().endDBTransaction();
                            }
                        }
                        //fetchCustomers(1);
                        if (DashboardActivity.isLoaded)
                            DashboardActivity.gotoSimpleActivityMenu();
                    }

                    @Override
                    public void onException(Exception ex) {
                        Utils.log(ex.getMessage(), " f90 ");
                        if (DashboardActivity.isLoaded)
                            DashboardActivity.gotoSimpleActivityMenu();
                    }
                });
    }

    ///////////////////////////insert into DB
    public static void insertActivityDate(String strDocumentId, final String strDocument) {

        try {

            JSONObject jsonObject = new JSONObject(strDocument);

            if (jsonObject.has("dependent_id")) {

                //activity

                String selectionActivity = DbHelper.COLUMN_OBJECT_ID + " = ? and "
                        + DbHelper.COLUMN_MILESTONE_ID + "=? ";

                String[] selectionArgsActivity = {strDocumentId,
                        "0"
                };

                Utils.log(jsonObject.getString("activity_date").substring(0,
                        jsonObject.getString("activity_date").length() - 1), " Actual Date 0");

                String strActivityDate = Utils.convertDateToStringQueryLocal(Utils.
                        convertStringToDateQuery(jsonObject.getString("activity_date").substring(0,
                                jsonObject.getString("activity_date").length() - 1)));

                Utils.log(strActivityDate, " Converted Date 0 ");

                String valuesActivity[] = {strDocumentId,
                        "0",
                        strActivityDate};

                CareGiver.getDbCon().updateInsert(
                        DbHelper.strTableNameMilestone,
                        selectionActivity, valuesActivity, DbHelper.MILESTONE_FIELDS,
                        selectionArgsActivity);
                ///

                if (!Config.dependentIds.contains(jsonObject.getString("dependent_id")))
                    Config.dependentIds.add(jsonObject.getString("dependent_id"));

                if (!Config.customerIds.contains(jsonObject.getString("customer_id")))
                    Config.customerIds.add(jsonObject.getString("customer_id"));

                if (jsonObject.has("milestones")) {

                    JSONArray jsonArrayMilestones = jsonObject.
                            getJSONArray("milestones");

                    for (int k = 0; k < jsonArrayMilestones.length(); k++) {

                        JSONObject jsonObjectMilestone =
                                jsonArrayMilestones.getJSONObject(k);

                        if (jsonObjectMilestone.has("scheduled_date")
                                && !jsonObjectMilestone.getString("scheduled_date")
                                .equalsIgnoreCase("")) {

                            String strMilestoneDate = Utils.convertDateToStringQueryLocal(
                                    Utils.convertStringToDateQuery(jsonObjectMilestone.getString(
                                            "scheduled_date").substring(0, jsonObjectMilestone.
                                            getString("scheduled_date").length() - 1)));

                            String values[] = {strDocumentId,
                                    jsonObjectMilestone.getString("id"),
                                    strMilestoneDate};

                            String selection = DbHelper.COLUMN_OBJECT_ID + " = ? and "
                                    + DbHelper.COLUMN_MILESTONE_ID + "=? ";

                            String[] selectionArgs = {strDocumentId,
                                    jsonObjectMilestone.getString("id")
                            };

                            CareGiver.getDbCon().updateInsert(
                                    DbHelper.strTableNameMilestone,
                                    selection, values, DbHelper.MILESTONE_FIELDS,
                                    selectionArgs);
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createProviderModel(String strProviderId) {

        try {

            Cursor cursor = CareGiver.getDbCon().fetch(
                    DbHelper.strTableNameCollection, new String[]{DbHelper.COLUMN_DOCUMENT},
                    DbHelper.COLUMN_COLLECTION_NAME + "=? and " + DbHelper.COLUMN_OBJECT_ID + "=?",
                    new String[]{Config.collectionProvider, strProviderId}, null, "0, 1", true,
                    null, null
            );

            String strDocument = "";

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                strDocument = cursor.getString(0);
            }
            CareGiver.getDbCon().closeCursor(cursor);

            if (!strDocument.equalsIgnoreCase("")) {

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

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateProviderJson(String strProviderId, boolean bWhich) {

        try {

            Cursor cursor = CareGiver.getDbCon().fetch(
                    DbHelper.strTableNameCollection, new String[]{DbHelper.COLUMN_DOCUMENT},
                    DbHelper.COLUMN_COLLECTION_NAME + "=? and " + DbHelper.COLUMN_OBJECT_ID + "=?",
                    new String[]{Config.collectionProvider, strProviderId}, null, "0, 1", true,
                    null, null
            );

            String strDocument = "";

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                strDocument = cursor.getString(0);
            }
            CareGiver.getDbCon().closeCursor(cursor);

            if (!strDocument.equalsIgnoreCase("")) {

                JSONObject jsonObject = new JSONObject(strDocument);

                if (jsonObject.has("provider_email")) {

                    if (bWhich) {
                        jsonObject.put("provider_contact_no", Config.providerModel.getStrContacts());
                        jsonObject.put("provider_address", Config.providerModel.getStrAddress());
                        jsonObject.put("provider_name", Config.providerModel.getStrName());
                    } else {
                        jsonObject.put("provider_profile_url", Config.providerModel.getStrImgUrl());
                    }

                    CareGiver.getDbCon().updateProvider(
                            new String[]{"DateTime('now')", jsonObject.toString(), "1"},
                            new String[]{"updated_date", "document", "updated"},
                            new String[]{Config.providerModel.getStrProviderId(),
                                    Config.collectionProvider});
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchDependents(final int iFlag) {

        if (Config.dependentIds.size() > 0) {

            if (utils.isConnectingToInternet()) {

                String strDate = DbHelper.DEFAULT_DB_DATE;

                Cursor cursor = CareGiver.getDbCon().getMaxDate(Config.collectionDependent);

                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    strDate = cursor.getString(0);
                }

                CareGiver.getDbCon().closeCursor(cursor);

                //
                Cursor cursor1 = CareGiver.getDbCon().fetch(
                        DbHelper.strTableNameCollection,
                        new String[]{DbHelper.COLUMN_OBJECT_ID},
                        DbHelper.COLUMN_COLLECTION_NAME
                                + "=? and "
                                + DbHelper.COLUMN_DOCUMENT + "=?",
                        new String[]{Config.collectionDependent,
                                ""
                        },
                        null, null, true,
                        null, null
                );

                ArrayList<String> strDependentIds = new ArrayList<>();

                if (cursor1.getCount() > 0) {
                    cursor1.moveToFirst();

                    while (cursor1.isAfterLast()) {
                        strDependentIds.add(cursor1.getString(0));
                        cursor1.moveToNext();
                    }
                }
                CareGiver.getDbCon().closeCursor(cursor1);
                //

                Query mQuery1 = null;

                if (strDependentIds.size() > 0) {
                    mQuery1 = QueryBuilder.build("_id", strDependentIds,
                            QueryBuilder.Operator.INLIST);
                }

                Query finalQuery;

                Query q12 = QueryBuilder.build("_$updatedAt", strDate, QueryBuilder.Operator.
                        GREATER_THAN_EQUALTO);

                if (mQuery1 != null) { //strDate != null && !strDate.equalsIgnoreCase("")

                    finalQuery = QueryBuilder.compoundOperator(mQuery1, QueryBuilder.Operator.AND,
                            q12);
                } else {
                    finalQuery = q12;
                }

                //

                //Query query = QueryBuilder.build("_id", Config.dependentIds, QueryBuilder.Operator.INLIST);

                storageService.findDocsByQuery(Config.collectionDependent, finalQuery, new App42CallBack() {

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
/*
                                        String strDocument = jsonDocument.getJsonDoc();
                                        String strDependentDocId = jsonDocument.
                                                getDocId();*/


                                        //
                                        String values[] = {jsonDocument.getDocId(),
                                                jsonDocument.getUpdatedAt(),
                                                jsonDocument.getJsonDoc(),
                                                Config.collectionDependent,
                                                "0", ""};

                                        String selection = DbHelper.COLUMN_OBJECT_ID
                                                + " = ?";

                                        // WHERE clause arguments
                                        String[] selectionArgs = {jsonDocument.getDocId()};
                                        CareGiver.getDbCon().updateInsert(
                                                DbHelper.strTableNameCollection,
                                                selection, values,
                                                DbHelper.COLLECTION_FIELDS,
                                                selectionArgs);
                                        //

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

        // if (Config.customerIds.size() > 0) {

            if (utils.isConnectingToInternet()) {

                String strDate = DbHelper.DEFAULT_DB_DATE;

                Cursor cursor = CareGiver.getDbCon().getMaxDate(Config.collectionCustomer);

                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    strDate = cursor.getString(0);
                }

                CareGiver.getDbCon().closeCursor(cursor);

                //
                Cursor cursor1 = CareGiver.getDbCon().fetch(
                        DbHelper.strTableNameCollection,
                        new String[]{DbHelper.COLUMN_OBJECT_ID},
                        DbHelper.COLUMN_COLLECTION_NAME
                                + "=? and "
                                + DbHelper.COLUMN_DOCUMENT + "=?",
                        new String[]{Config.collectionCustomer,
                                ""
                        },
                        null, null, true,
                        null, null
                );
                // CareGiver.getDbCon().closeCursor(cursor);

                ArrayList<String> strCustomerIds = new ArrayList<>();

                if (cursor1.getCount() > 0) {
                    cursor1.moveToFirst();

                    while (cursor1.isAfterLast()) {
                        strCustomerIds.add(cursor1.getString(0));
                        cursor1.moveToNext();
                    }
                }
                CareGiver.getDbCon().closeCursor(cursor1);
                //

                Query mQuery1 = null;
                if (strCustomerIds.size() > 0) {
                    mQuery1 = QueryBuilder.build("_id", strCustomerIds,
                            QueryBuilder.Operator.INLIST);
                }

                Query finalQuery;

                //if (strDate != null && !strDate.equalsIgnoreCase("")) {
                Query q12 = QueryBuilder.build("_$updatedAt", strDate, QueryBuilder.Operator.
                        GREATER_THAN_EQUALTO);

                if (mQuery1 != null) {
                    finalQuery = QueryBuilder.compoundOperator(mQuery1, QueryBuilder.Operator.AND,
                            q12);
                } else {
                    finalQuery = q12;
                }
                /* else {
                    finalQuery = mQuery1;
                }*/


                storageService.findDocsByQuery(Config.collectionCustomer, finalQuery,
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

                                               /* String strDocument = jsonDocument.getJsonDoc();
                                                String strDependentDocId = jsonDocument.
                                                        getDocId();*/


                                                //
                                                String values[] = {jsonDocument.getDocId(),
                                                        jsonDocument.getUpdatedAt(),
                                                        jsonDocument.getJsonDoc(),
                                                        Config.collectionCustomer,
                                                        "0", ""};

                                                String selection = DbHelper.COLUMN_OBJECT_ID
                                                        + " = ?";

                                                // WHERE clause arguments
                                                String[] selectionArgs = {jsonDocument.getDocId()};
                                                CareGiver.getDbCon().updateInsert(
                                                        DbHelper.strTableNameCollection,
                                                        selection, values,
                                                        DbHelper.COLLECTION_FIELDS,
                                                        selectionArgs);
                                                //
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

        //} else fetchDependents(iFlag);
    }

    private void createDependentModel(String strDocumentId, String strDocument, int iFlag) {

        /*try {

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
                        "",
                        strDocumentId,
                        jsonObjectDependent.getString("customer_id"));

                if (jsonObjectDependent.has("dependent_profile_url"))
                    dependentModel.setStrImageUrl(jsonObjectDependent.getString("dependent_profile_url"));

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

                if (iFlag == 2) {

                    if (!Config.strDependentNames.contains(jsonObjectDependent.getString("dependent_name"))) {
                        Config.strDependentNames.add(jsonObjectDependent.getString("dependent_name"));

                        int iPosition = Config.customerIdsCopy.indexOf(jsonObjectDependent.getString("customer_id"));

                        if (Config.clientModels.size() > 0) {
                            if (iPosition > -1 && iPosition < Config.clientModels.size())
                                Config.clientModels.get(iPosition).setDependentModel(dependentModel);
                        }

                        if (Config.clientNameModels.size() > 0) {
                            if (iPosition > -1 && iPosition < Config.clientNameModels.size()) {
                                Config.clientNameModels.get(iPosition).removeStrDependentName(jsonObjectDependent.getString("dependent_name"));
                                Config.clientNameModels.get(iPosition).setStrDependentName(jsonObjectDependent.getString("dependent_name"));
                            }
                        }
                    }
                }

               *//* Config.fileModels.add(new FileModel(strDocumentId,
                        jsonObjectDependent.getString("dependent_profile_url"), "IMAGE"));*//*

                //
                if (jsonObjectDependent.has("dependent_profile_url")) {
                    String strUrl = jsonObjectDependent.getString("dependent_profile_url");

                    *//*String strUrlHash = Utils.sha512(strUrl);

                    Cursor cur = CareGiver.getDbCon().fetch(
                            DbHelper.strTableNameFiles, new String[]{"file_hash"}, "name=?",
                            new String[]{strDocumentId}, null, "0, 1", true, null, null
                    );

                    //Utils.log(strUrlHash, " HASH ");


                    String strHashLocal = "";

                    if (cur.getCount() <= 0) {
                        CareGiver.getDbCon().insert(DbHelper.strTableNameFiles, new String[]{strDocumentId,
                                        strUrl, "IMAGE", strUrlHash},
                                new String[]{"name", "url", "file_type", "file_hash"});
                    } else {

                        cur.moveToFirst();
                        strHashLocal = cur.getString(0);
                        cur.moveToNext();
                        CareGiver.getDbCon().closeCursor(cur);

                        if (!strHashLocal.equalsIgnoreCase(strUrlHash)) {
                            CareGiver.getDbCon().update(
                                    DbHelper.strTableNameFiles, "name=?",
                                    new String[]{strUrl, strUrlHash},
                                    new String[]{"url", "file_hash"}, new String[]{strDocumentId}
                            );
                        }
                    }

                    CareGiver.getDbCon().closeCursor(cur);*//*
                }
            } else {
                if (iFlag == 2) {

                    if (!Config.strDependentNames.contains(jsonObjectDependent.getString("dependent_name"))) {
                        Config.strDependentNames.add(jsonObjectDependent.getString("dependent_name"));

                        int iPosition = Config.customerIdsCopy.indexOf(jsonObjectDependent.getString("customer_id"));

                        if (Config.clientModels.size() > 0) {

                            if (iPosition > -1 && iPosition < Config.clientModels.size()) {
                                int iPosition1 = Config.dependentIdsAdded.indexOf(strDocumentId);
                                if (iPosition1 > -1 && iPosition1 < Config.dependentModels.size()) {
                                    DependentModel dependentModel = Config.dependentModels.get(iPosition1);
                                    Config.clientModels.get(iPosition).setDependentModel(dependentModel);
                                }
                            }
                        }

                        //
                        if (Config.clientNameModels.size() > 0) {
                            if (iPosition > -1 && iPosition < Config.clientNameModels.size()) {
                                Config.clientNameModels.get(iPosition).removeStrDependentName(jsonObjectDependent.getString("dependent_name"));
                                Config.clientNameModels.get(iPosition).setStrDependentName(jsonObjectDependent.getString("dependent_name"));
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    public void createNotificationModel() {

        Cursor cursor = null;
        try {

            String strDocument;

            cursor = CareGiver.getDbCon().fetch(
                    DbHelper.strTableNameCollection, new String[]{DbHelper.COLUMN_DOCUMENT},
                    DbHelper.COLUMN_COLLECTION_NAME + "=?",
                    new String[]{Config.collectionNotification}, DbHelper.COLUMN_UPDATE_DATE + " desc",
                    null, true, null, null
            );

            if (cursor != null && cursor.getCount() > 0) {
                Config.notificationModels.clear();
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    strDocument = cursor.getString(0);

                    JSONObject jsonObjectProvider = new JSONObject(strDocument);

                    if (jsonObjectProvider.has(App42GCMService.ExtraMessage)) {

                        NotificationModel notificationModel = new NotificationModel(
                                jsonObjectProvider.getString(App42GCMService.ExtraMessage),
                                jsonObjectProvider.getString("time"),
                                jsonObjectProvider.getString("user_type"),
                                jsonObjectProvider.getString("created_by_type"),
                                jsonObjectProvider.getString("user_id"),
                                jsonObjectProvider.getString("created_by"), "");

                        if (jsonObjectProvider.has("activity_id"))
                            notificationModel.setStrActivityId(jsonObjectProvider.getString("activity_id"));

                        if (jsonObjectProvider.getString("created_by_type").equalsIgnoreCase("customer")) {
                            if (!Config.customerIdsAdded.contains(jsonObjectProvider.getString("created_by")))
                                Config.customerIds.add(jsonObjectProvider.getString("created_by"));
                        }

                        if (jsonObjectProvider.getString("created_by_type").equalsIgnoreCase("dependent")) {
                            if (!Config.dependentIdsAdded.contains(jsonObjectProvider.getString("created_by")))
                                Config.dependentIds.add(jsonObjectProvider.getString("created_by"));
                        }

                        /*if (!Config.strNotificationIds.contains(strDocumentId)) {
                            Config.strNotificationIds.add(strDocumentId);*/
                        Config.notificationModels.add(notificationModel);
                        //}
                    }
                    cursor.moveToNext();
                }
            }
            CareGiver.getDbCon().closeCursor(cursor);

        } catch (JSONException e) {
            e.printStackTrace();
            CareGiver.getDbCon().closeCursor(cursor);
        }
    }

    public void createCustomerModel() {

        Config.strCustomerNames.clear();
        Config.customerIdsCopy.clear();
        Config.clientModels.clear();
        Config.clientNameModels.clear();
        Config.customerModels.clear();
        Config.customerIdsAdded.clear();

        Cursor newCursor = CareGiver.getDbCon().fetch(
                DbHelper.strTableNameCollection,
                new String[]{DbHelper.COLUMN_OBJECT_ID, DbHelper.COLUMN_DOCUMENT},
                DbHelper.COLUMN_COLLECTION_NAME
                        + "=?",
                new String[]{Config.collectionCustomer},
                null, null, true,
                null, null
        );

        if (newCursor.getCount() > 0) {

            newCursor.moveToFirst();

            try {

                while (!newCursor.isAfterLast()) {

                    if (!newCursor.getString(1).equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject(newCursor.getString(1));

                        if (jsonObject.has("customer_name")) {

                            CustomerModel customerModel = new CustomerModel(
                                    jsonObject.getString("customer_name"),
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
                                    newCursor.getString(0));


                            if (!Config.strCustomerNames.contains(jsonObject.getString("customer_name"))) {
                                Config.strCustomerNames.add(jsonObject.getString("customer_name"));

                                Config.customerIdsCopy.add(newCursor.getString(0));

                                ClientModel clientModel = new ClientModel();
                                clientModel.setCustomerModel(customerModel);
                                Config.clientModels.add(clientModel);

                                ClientNameModel clientNameModel = new ClientNameModel();
                                clientNameModel.setStrCustomerName(jsonObject.getString("customer_name"));

                                Config.clientNameModels.add(clientNameModel);
                            }

                            Config.customerModels.add(customerModel);
                            Config.customerIdsAdded.add(newCursor.getString(0));
                        }
                    }

                    newCursor.moveToNext();
                }

            } catch (JSONException e1) {
                e1.printStackTrace();
            }

        }
        CareGiver.getDbCon().closeCursor(newCursor);
        createDependentModel();
    }

    /*public void loadAllFiles() {

        Cursor cur = null;

        try {

            cur = CareGiver.getDbCon().fetch(
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

            CareGiver.getDbCon().closeCursor(cur);

        } catch (Exception e) {
            e.printStackTrace();
            CareGiver.getDbCon().closeCursor(cur);
        }

        *//*for (int i = 0; i < Config.fileModels.size(); i++) {
            FileModel fileModel = Config.fileModels.get(i);

            if (fileModel != null && fileModel.getStrFileUrl() != null &&
                    !fileModel.getStrFileUrl().equalsIgnoreCase("")) {
                utils.loadImageFromWeb(fileModel.getStrFileName(),
                        fileModel.getStrFileUrl());
            }
        }*//*
    }*/

    private void createDependentModel() {

        Config.dependentModels.clear();
        Config.strDependentNames.clear();
        //Config.customerIdsCopy.clear();
        Config.dependentIdsAdded.clear();

        Cursor newCursor = CareGiver.getDbCon().fetch(
                DbHelper.strTableNameCollection,
                new String[]{DbHelper.COLUMN_OBJECT_ID, DbHelper.COLUMN_DOCUMENT},
                DbHelper.COLUMN_COLLECTION_NAME
                        + "=?",
                new String[]{Config.collectionDependent},
                null, null, true,
                null, null
        );

        if (newCursor.getCount() > 0) {

            newCursor.moveToFirst();

            try {

                while (!newCursor.isAfterLast()) {

                    if (!newCursor.getString(1).equalsIgnoreCase("")) {
                        JSONObject jsonObjectDependent = new JSONObject(newCursor.getString(1));

                        DependentModel dependentModel = new DependentModel(
                                jsonObjectDependent.getString("dependent_name"),
                                jsonObjectDependent.getString("dependent_relation"),
                                jsonObjectDependent.getString("dependent_notes"),
                                jsonObjectDependent.getString("dependent_address"),
                                jsonObjectDependent.getString("dependent_contact_no"),
                                jsonObjectDependent.getString("dependent_email"),
                                jsonObjectDependent.getString("dependent_illness"),
                                "",
                                "",
                                newCursor.getString(0),
                                jsonObjectDependent.getString("customer_id"));

                        if (jsonObjectDependent.has("dependent_profile_url"))
                            dependentModel.setStrImageUrl(jsonObjectDependent.getString("dependent_profile_url"));

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

                        Config.dependentModels.add(dependentModel);
                        Config.dependentIdsAdded.add(newCursor.getString(0));

                        if (!Config.strDependentNames.contains(jsonObjectDependent.getString("dependent_name"))) {
                            Config.strDependentNames.add(jsonObjectDependent.getString("dependent_name"));

                            int iPosition = Config.customerIdsCopy.indexOf(jsonObjectDependent.getString("customer_id"));

                            if (Config.clientModels.size() > 0) {
                                if (iPosition > -1 && iPosition < Config.clientModels.size())
                                    Config.clientModels.get(iPosition).setDependentModel(dependentModel);
                            }

                            if (Config.clientNameModels.size() > 0) {
                                if (iPosition > -1 && iPosition < Config.clientNameModels.size()) {
                                    Config.clientNameModels.get(iPosition).removeStrDependentName(jsonObjectDependent.getString("dependent_name"));
                                    Config.clientNameModels.get(iPosition).setStrDependentName(jsonObjectDependent.getString("dependent_name"));
                                }
                            }
                        }
                    }

                    newCursor.moveToNext();
                }

            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        CareGiver.getDbCon().closeCursor(newCursor);
    }

    public void fetchActivities() {

      /* String strDate = DbHelper.DEFAULT_DB_DATE;

       Cursor cursor = CareGiver.getDbCon().getMaxDate(Config.collectionServiceCustomer);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            strDate = cursor.getString(0);
        }

        CareGiver.getDbCon().closeCursor(cursor);*/

        Query q1 = QueryBuilder.build("provider_id", Config.providerModel.getStrProviderId(),
                QueryBuilder.Operator.EQUALS);

        Query q2 = QueryBuilder.build("activity_date", DashboardFragment.strEndDate, QueryBuilder.
                Operator.LESS_THAN_EQUALTO);

        Query q3 = QueryBuilder.build("activity_date", DashboardFragment.strStartDate, QueryBuilder.
                Operator.GREATER_THAN_EQUALTO);

        Query q4 = QueryBuilder.compoundOperator(q2, QueryBuilder.Operator.AND, q3);

        Query q7 = QueryBuilder.build("milestones.scheduled_date", DashboardFragment.strEndDate,
                QueryBuilder.Operator.LESS_THAN_EQUALTO);

        Query q8 = QueryBuilder.build("milestones.scheduled_date", DashboardFragment.strStartDate,
                QueryBuilder.Operator.GREATER_THAN_EQUALTO);

        Query q10 = QueryBuilder.build("milestones.status", Config.MilestoneStatus.COMPLETED,
                QueryBuilder.Operator.NOT_EQUALS);

        Query q9 = QueryBuilder.compoundOperator(q7, QueryBuilder.Operator.AND, q8);

        Query q11 = QueryBuilder.compoundOperator(q9, QueryBuilder.Operator.AND, q10);

        Query q5 = QueryBuilder.compoundOperator(q4, QueryBuilder.Operator.OR, q11);

        Query q6 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q5);

      /*  Query finalQuery;

        if (strDate!=null&&!strDate.equalsIgnoreCase("")) {
            Query q12 = QueryBuilder.build("_$updatedAt", strDate, QueryBuilder.Operator.GREATER_THAN_EQUALTO);

            finalQuery = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q12);
        } else {
            finalQuery = q1;
        }*/

        try {
            Utils.log(q6.get(), " QUERY ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        storageService.findDocsByQueryOrderBy(Config.collectionActivity, q6, 30000, 0,
                "milestones.scheduled_date", 1,
                new App42CallBack() {

                    @Override
                    public void onSuccess(Object o) {
                        if (o != null) {

                            //Utils.log(o.toString(), " Activity All SSS ");

                            Storage storage = (Storage) o;

                            ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();

                            try {
                                CareGiver.getDbCon().beginDBTransaction();
                                for (int i = 0; i < jsonDocList.size(); i++) {

                                    Storage.JSONDocument jsonDocument = jsonDocList.get(i);

                                    ///
                                    String values[] = {jsonDocument.getDocId(),
                                            jsonDocument.getUpdatedAt(),
                                            jsonDocument.getJsonDoc(), Config.collectionActivity,
                                            "0", ""};

                                    String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                                    // WHERE clause arguments
                                    String[] selectionArgs = {jsonDocument.getDocId()};
                                    CareGiver.getDbCon().updateInsert(
                                            DbHelper.strTableNameCollection,
                                            selection, values, DbHelper.COLLECTION_FIELDS,
                                            selectionArgs);

                                    insertActivityDate(jsonDocument.getDocId(),
                                            jsonDocument.getJsonDoc());
                                }
                                CareGiver.getDbCon().dbTransactionSuccessFull();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                CareGiver.getDbCon().endDBTransaction();
                            }
                        }
                        //fetchCustomers(1);
                        //DashboardActivity.reloadActivities();
                        DashboardActivity.refreshClientsData();
                    }

                    @Override
                    public void onException(Exception ex) {
                        Utils.log(ex.getMessage(), " f1 ");
                        //fetchCustomers(1);
                        //DashboardActivity.reloadActivities();
                        DashboardActivity.refreshClientsData();
                    }
                });
    }

    public void createActivityModel(String strStartDate, String strEndDate) {

        try {

            Config.strActivityIds.clear();
            Config.activityModels.clear();

            String strQuery = "SELECT a." + DbHelper.COLUMN_DOCUMENT + " AS C1 , b."
                    + DbHelper.COLUMN_MILESTONE_ID + " AS C2, b." + DbHelper.COLUMN_OBJECT_ID
                    + " AS C3 FROM " + DbHelper.strTableNameCollection + " AS a INNER JOIN "
                    + DbHelper.strTableNameMilestone + " AS b ON a.object_id=b.object_id  WHERE b."
                    + DbHelper.COLUMN_MILESTONE_DATE + ">= Datetime('" + strStartDate + "') AND b."
                    + DbHelper.COLUMN_MILESTONE_DATE + "<= Datetime('" + strEndDate + "') ORDER BY b."
                    + DbHelper.COLUMN_MILESTONE_DATE + " DESC LIMIT 0, 30000";

            Utils.log(strQuery, " QUERY ");

            Cursor newCursor = CareGiver.getDbCon().rawQuery(strQuery);

            if (newCursor.getCount() > 0) {

                newCursor.moveToFirst();

                Boolean isActivity = true;

                while (!newCursor.isAfterLast()) {

                    if (!Config.strActivityIds.contains(newCursor.getString(2))) {
                        Config.strActivityIds.add(newCursor.getString(2));

                        if (!newCursor.getString(1).equalsIgnoreCase("0"))
                            isActivity = false;

                        JSONObject jsonObject = new JSONObject(newCursor.getString(0));

                        createActivityModel(jsonObject, newCursor.getString(2), isActivity);
                    }
                    newCursor.moveToNext();
                }
            }
            CareGiver.getDbCon().closeCursor(newCursor);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createActivityModel(JSONObject jsonObject, String strDocumentId,
                                     boolean isActivity) {
        try {

            if (jsonObject.has("dependent_id")) {

                ActivityModel activityModel = new ActivityModel();

                activityModel.setStrActivityName(jsonObject.optString("activity_name"));
                activityModel.setStrActivityID(strDocumentId);
                activityModel.setStrProviderID(jsonObject.optString("provider_id"));
                activityModel.setStrDependentID(jsonObject.optString("dependent_id"));
                activityModel.setStrCustomerID(jsonObject.optString("customer_id"));
                activityModel.setStrActivityStatus(jsonObject.optString("status"));
                activityModel.setStrActivityDesc(jsonObject.optString("activity_desc"));

                activityModel.setStrCreatedBy(jsonObject.optString("created_by"));

                activityModel.setStrServcieID(jsonObject.optString("service_id"));
                activityModel.setStrServiceName(jsonObject.optString("service_name"));

                if (jsonObject.has("activity_date")) {
                    activityModel.setStrActivityDate(jsonObject.getString("activity_date"));
                }

                activityModel.setStrActivityDoneDate(jsonObject.
                        optString("activity_done_date"));

                activityModel.setStrActivityProviderStatus(jsonObject.
                        optString("provider_status"));

                activityModel.setStrActivityProviderMessage(jsonObject.
                        optString("provider_message"));

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
                                    jsonObjectVideo.optString("video_name"),
                                    jsonObjectVideo.optString("video_url"),
                                    jsonObjectVideo.optString("video_description"),
                                    jsonObjectVideo.optString("video_taken"));

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
                                    jsonObjectImage.optString("image_name"),
                                    jsonObjectImage.optString("image_url"),
                                    jsonObjectImage.optString("image_description"),
                                    jsonObjectImage.optString("image_taken"),
                                    "");
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
                                    jsonObjectFeedback.optString("feedback_message"),
                                    jsonObjectFeedback.optString("feedback_by"),
                                    jsonObjectFeedback.getInt("feedback_rating"),
                                    jsonObjectFeedback.optString("feedback_time"),
                                    jsonObjectFeedback.optString("feedback_by_type"));

                            try {
                                feedBackModel.setbFeedBackReport(jsonObjectFeedback.
                                        getBoolean("feedback_report"));
                            } catch (Exception e) {
                                try {
                                    String strTemp = jsonObjectFeedback.
                                            optString("feedback_report");
                                    boolean b = false;
                                    if (strTemp.equalsIgnoreCase("1"))
                                        b = true;

                                    feedBackModel.setbFeedBackReport(b);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }

                            feedBackModels.add(feedBackModel);

                            if (jsonObjectFeedback.getString("feedback_by_type").
                                    equalsIgnoreCase("customer")) {
                                if (!Config.customerIds.contains(jsonObjectFeedback.
                                        optString("feedback_by")))
                                    Config.customerIds.add(jsonObjectFeedback.
                                            optString("feedback_by"));
                            }

                            if (jsonObjectFeedback.getString("feedback_by_type").
                                    equalsIgnoreCase("dependent")) {
                                if (!Config.dependentIds.contains(jsonObjectFeedback.
                                        optString("feedback_by")))
                                    Config.dependentIds.add(jsonObjectFeedback.
                                            optString("feedback_by"));
                            }

                         /*   Config.iRatings += jsonObjectFeedback.getInt("feedback_rating");

                            Config.iRatingCount += 1;

                            Config.feedBackModels.add(feedBackModel);*/

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
                        milestoneModel.setStrMilestoneStatus(jsonObjectMilestone.
                                optString("status"));
                        milestoneModel.setStrMilestoneName(jsonObjectMilestone.optString("name"));
                        milestoneModel.setStrMilestoneDate(jsonObjectMilestone.optString("date"));

                        if (jsonObjectMilestone.has("files")) {

                            JSONArray jsonArrayMsFiles = jsonObjectMilestone.
                                    getJSONArray("files");

                            for (int m = 0; m < jsonArrayMsFiles.length(); m++) {

                                JSONObject jsonObjectMsFile = jsonArrayMsFiles.
                                        getJSONObject(m);

                                if (jsonObjectMsFile.has("file_name")) {

                                    FileModel fileModel = new FileModel(
                                            jsonObjectMsFile.optString("file_name"),
                                            jsonObjectMsFile.optString("file_url"),
                                            jsonObjectMsFile.optString("file_type"),
                                            jsonObjectMsFile.optString("file_time"),
                                            jsonObjectMsFile.optString("file_desc"),
                                            jsonObjectMsFile.optString("file_path"));

                                    milestoneModel.setFileModel(fileModel);
                                }
                            }
                        }

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
                                milestoneModel.setReschedule(jsonObjectMilestone.
                                        getBoolean("reschedule"));
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
                                    optString("scheduled_date"));
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
                                        fieldModel.setFieldRequired(jsonObjectField.
                                                getBoolean("required"));
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

                                fieldModel.setStrFieldData(jsonObjectField.optString("data"));
                                fieldModel.setStrFieldLabel(jsonObjectField.optString("label"));
                                fieldModel.setStrFieldType(jsonObjectField.optString("type"));

                                if (jsonObjectField.has("values")) {

                                    fieldModel.setStrFieldValues(utils.jsonToStringArray(
                                            jsonObjectField.getJSONArray("values")));
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
                                        fieldModel.setStrChildType(utils.jsonToStringArray(
                                                jsonObjectField.getJSONArray("child_type")));

                                    if (jsonObjectField.has("child_value"))
                                        fieldModel.setStrChildValue(utils.jsonToStringArray(
                                                jsonObjectField.getJSONArray("child_value")));

                                    if (jsonObjectField.has("child_condition"))
                                        fieldModel.setStrChildCondition(utils.jsonToStringArray(
                                                jsonObjectField.getJSONArray("child_condition")));

                                    if (jsonObjectField.has("child_field"))
                                        fieldModel.setiChildfieldID(utils.jsonToIntArray(
                                                jsonObjectField.getJSONArray("child_field")));
                                }

                                if (jsonObjectField.has("array_fields")) {

                                    try {
                                        fieldModel.setiArrayCount(jsonObjectField.
                                                getInt("array_fields"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        int i = 0;
                                        try {
                                            i = Integer.parseInt(jsonObjectField.
                                                    optString("array_fields"));
                                            fieldModel.setiArrayCount(i);
                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                    if (jsonObjectField.has("array_type"))
                                        fieldModel.setStrArrayType(utils.jsonToStringArray(
                                                jsonObjectField.getJSONArray("array_type")));

                                    if (jsonObjectField.has("array_data"))
                                        fieldModel.setStrArrayData(jsonObjectField.
                                                optString("array_data"));

                                }

                                milestoneModel.setFieldModel(fieldModel);
                            }
                        }

                        activityModel.setiActivityDisplayFlag(isActivity);
                        activityModel.setMilestoneModel(milestoneModel);
                    }
                }
                Config.activityModels.add(activityModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createFeedbackyModel() {

        try {

            Config.iRatings = 0;
            Config.feedBackModels.clear();
            Config.iRatingCount = 0;

            Cursor cursor = CareGiver.getDbCon().fetch(
                    DbHelper.strTableNameCollection,
                    new String[]{DbHelper.COLUMN_DOCUMENT},
                    DbHelper.COLUMN_COLLECTION_NAME + "=?",
                    new String[]{Config.collectionActivity},
                    DbHelper.COLUMN_UPDATE_DATE + " desc",
                    null, true, null, null);

            if (cursor.getCount() > 0) {

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    JSONObject jsonObject = new JSONObject(cursor.getString(0));

                    if (jsonObject.has("dependent_id")) {

                        //ArrayList<FeedBackModel> feedBackModels = new ArrayList<>();


                        if (jsonObject.has("feedbacks")) {

                            JSONArray jsonArrayFeedback = jsonObject.getJSONArray("feedbacks");

                            for (int k = 0; k < jsonArrayFeedback.length(); k++) {

                                JSONObject jsonObjectFeedback = jsonArrayFeedback.getJSONObject(k);

                                if (jsonObjectFeedback.has("feedback_message")) {

                                    FeedBackModel feedBackModel = new FeedBackModel(
                                            jsonObjectFeedback.optString("feedback_message"),
                                            jsonObjectFeedback.optString("feedback_by"),
                                            jsonObjectFeedback.getInt("feedback_rating"),
                                            jsonObjectFeedback.optString("feedback_time"),
                                            jsonObjectFeedback.optString("feedback_by_type"));

                                    try {
                                        feedBackModel.setbFeedBackReport(jsonObjectFeedback.
                                                getBoolean("feedback_report"));
                                    } catch (Exception e) {
                                        try {
                                            String strTemp = jsonObjectFeedback.
                                                    optString("feedback_report");
                                            boolean b = false;
                                            if (strTemp.equalsIgnoreCase("1"))
                                                b = true;

                                            feedBackModel.setbFeedBackReport(b);
                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                    //feedBackModels.add(feedBackModel);

                                    if (jsonObjectFeedback.getString("feedback_by_type").
                                            equalsIgnoreCase("customer")) {
                                        if (!Config.customerIds.contains(jsonObjectFeedback.
                                                optString("feedback_by")))
                                            Config.customerIds.add(jsonObjectFeedback.
                                                    optString("feedback_by"));
                                    }

                                    if (jsonObjectFeedback.getString("feedback_by_type").
                                            equalsIgnoreCase("dependent")) {
                                        if (!Config.dependentIds.contains(jsonObjectFeedback.
                                                optString("feedback_by")))
                                            Config.dependentIds.add(jsonObjectFeedback.
                                                    optString("feedback_by"));
                                    }

                                    Config.iRatings += jsonObjectFeedback.getInt("feedback_rating");

                                    Config.iRatingCount += 1;

                                    Config.feedBackModels.add(feedBackModel);
                                }
                            }

                        }
                    }
                    cursor.moveToNext();
                }
            }
            CareGiver.getDbCon().closeCursor(cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ActivityModel createActivityModelNotification(JSONObject jsonObject,
                                                         String strDocumentId) {

        ActivityModel activityModel = null;

        try {

            if (jsonObject.has("dependent_id")) {

                activityModel = new ActivityModel();

                activityModel.setStrActivityName(jsonObject.optString("activity_name"));
                activityModel.setStrActivityID(strDocumentId);
                activityModel.setStrProviderID(jsonObject.optString("provider_id"));
                activityModel.setStrDependentID(jsonObject.optString("dependent_id"));
                activityModel.setStrCustomerID(jsonObject.optString("customer_id"));
                activityModel.setStrActivityStatus(jsonObject.optString("status"));
                activityModel.setStrActivityDesc(jsonObject.optString("activity_desc"));

                activityModel.setStrCreatedBy(jsonObject.optString("created_by"));

                activityModel.setStrServcieID(jsonObject.optString("service_id"));
                activityModel.setStrServiceName(jsonObject.optString("service_name"));

                if (jsonObject.has("activity_date")) {
                    activityModel.setStrActivityDate(jsonObject.getString("activity_date"));
                }

                activityModel.setStrActivityDoneDate(jsonObject.
                        optString("activity_done_date"));

                activityModel.setStrActivityProviderStatus(jsonObject.
                        optString("provider_status"));

                activityModel.setStrActivityProviderMessage(jsonObject.
                        optString("provider_message"));

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
                                    jsonObjectVideo.optString("video_name"),
                                    jsonObjectVideo.optString("video_url"),
                                    jsonObjectVideo.optString("video_description"),
                                    jsonObjectVideo.optString("video_taken"));

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
                                    jsonObjectImage.optString("image_name"),
                                    jsonObjectImage.optString("image_url"),
                                    jsonObjectImage.optString("image_description"),
                                    jsonObjectImage.optString("image_taken"),
                                    "");
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
                                    jsonObjectFeedback.optString("feedback_message"),
                                    jsonObjectFeedback.optString("feedback_by"),
                                    jsonObjectFeedback.getInt("feedback_rating"),
                                    jsonObjectFeedback.optString("feedback_time"),
                                    jsonObjectFeedback.optString("feedback_by_type"));

                            try {
                                feedBackModel.setbFeedBackReport(jsonObjectFeedback.
                                        getBoolean("feedback_report"));
                            } catch (Exception e) {
                                try {
                                    String strTemp = jsonObjectFeedback.
                                            optString("feedback_report");
                                    boolean b = false;
                                    if (strTemp.equalsIgnoreCase("1"))
                                        b = true;

                                    feedBackModel.setbFeedBackReport(b);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }

                            feedBackModels.add(feedBackModel);

                            if (jsonObjectFeedback.getString("feedback_by_type").
                                    equalsIgnoreCase("customer")) {
                                if (!Config.customerIds.contains(jsonObjectFeedback.
                                        optString("feedback_by")))
                                    Config.customerIds.add(jsonObjectFeedback.
                                            optString("feedback_by"));
                            }

                            if (jsonObjectFeedback.getString("feedback_by_type").
                                    equalsIgnoreCase("dependent")) {
                                if (!Config.dependentIds.contains(jsonObjectFeedback.
                                        optString("feedback_by")))
                                    Config.dependentIds.add(jsonObjectFeedback.
                                            optString("feedback_by"));
                            }

                           /* Config.iRatings += jsonObjectFeedback.getInt("feedback_rating");

                            Config.iRatingCount += 1;

                            Config.feedBackModels.add(feedBackModel);
*/
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
                        milestoneModel.setStrMilestoneStatus(jsonObjectMilestone.
                                optString("status"));
                        milestoneModel.setStrMilestoneName(jsonObjectMilestone.optString("name"));
                        milestoneModel.setStrMilestoneDate(jsonObjectMilestone.optString("date"));

                        if (jsonObjectMilestone.has("files")) {

                            JSONArray jsonArrayMsFiles = jsonObjectMilestone.
                                    getJSONArray("files");

                            for (int m = 0; m < jsonArrayMsFiles.length(); m++) {

                                JSONObject jsonObjectMsFile = jsonArrayMsFiles.
                                        getJSONObject(m);

                                if (jsonObjectMsFile.has("file_name")) {

                                    FileModel fileModel = new FileModel(
                                            jsonObjectMsFile.optString("file_name"),
                                            jsonObjectMsFile.optString("file_url"),
                                            jsonObjectMsFile.optString("file_type"),
                                            jsonObjectMsFile.optString("file_time"),
                                            jsonObjectMsFile.optString("file_desc"),
                                            jsonObjectMsFile.optString("file_path"));

                                    milestoneModel.setFileModel(fileModel);
                                }
                            }
                        }

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
                                milestoneModel.setReschedule(jsonObjectMilestone.
                                        getBoolean("reschedule"));
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
                                    optString("scheduled_date"));
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
                                        fieldModel.setFieldRequired(jsonObjectField.
                                                getBoolean("required"));
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

                                fieldModel.setStrFieldData(jsonObjectField.optString("data"));
                                fieldModel.setStrFieldLabel(jsonObjectField.optString("label"));
                                fieldModel.setStrFieldType(jsonObjectField.optString("type"));

                                if (jsonObjectField.has("values")) {

                                    fieldModel.setStrFieldValues(utils.jsonToStringArray(
                                            jsonObjectField.getJSONArray("values")));
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
                                        fieldModel.setStrChildType(utils.jsonToStringArray(
                                                jsonObjectField.getJSONArray("child_type")));

                                    if (jsonObjectField.has("child_value"))
                                        fieldModel.setStrChildValue(utils.jsonToStringArray(
                                                jsonObjectField.getJSONArray("child_value")));

                                    if (jsonObjectField.has("child_condition"))
                                        fieldModel.setStrChildCondition(utils.jsonToStringArray(
                                                jsonObjectField.getJSONArray("child_condition")));

                                    if (jsonObjectField.has("child_field"))
                                        fieldModel.setiChildfieldID(utils.jsonToIntArray(
                                                jsonObjectField.getJSONArray("child_field")));
                                }

                                if (jsonObjectField.has("array_fields")) {

                                    try {
                                        fieldModel.setiArrayCount(jsonObjectField.
                                                getInt("array_fields"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        int i = 0;
                                        try {
                                            i = Integer.parseInt(jsonObjectField.
                                                    optString("array_fields"));
                                            fieldModel.setiArrayCount(i);
                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                    if (jsonObjectField.has("array_type"))
                                        fieldModel.setStrArrayType(utils.jsonToStringArray(
                                                jsonObjectField.getJSONArray("array_type")));

                                    if (jsonObjectField.has("array_data"))
                                        fieldModel.setStrArrayData(jsonObjectField.
                                                optString("array_data"));

                                }

                                milestoneModel.setFieldModel(fieldModel);
                            }
                        }

                        //activityModel.setiActivityDisplayFlag(isActivity);
                        activityModel.setMilestoneModel(milestoneModel);
                    }
                }
                //Config.activityModels.add(activityModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return activityModel;
    }
    //////////////////////////

    public void createServiceModel(String strDocumentId, JSONObject jsonObject) {

        try {

            ServiceModel serviceModel = new ServiceModel();

            serviceModel.setDoubleCost(jsonObject.getDouble("cost"));
            serviceModel.setStrServiceName(jsonObject.getString("service_name"));
            serviceModel.setiServiceNo(jsonObject.getInt("service_no"));
            serviceModel.setStrCategoryName(jsonObject.getString("category_name"));
            serviceModel.setiUnit(jsonObject.getInt("unit"));
            serviceModel.setiUnitValue(jsonObject.getInt("unit_value"));


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
                categorySet.add(jsonObject.getString("category_name"));
                //Config.serviceCategorylist.add();
                List<String> serviceNameList = new ArrayList<>();

                if (Config.serviceNameModels != null && Config.serviceNameModels.size() > 0) {
                    if (Config.serviceNameModels.containsKey(jsonObject.getString("category_name"))) {
                        serviceNameList.addAll(Config.serviceNameModels.get(jsonObject.getString("category_name")));
                        serviceNameList.add(jsonObject.getString("service_name"));
                    } else {
                        serviceNameList.add(jsonObject.getString("service_name"));
                    }
                } else {
                    serviceNameList.add(jsonObject.getString("service_name"));
                }

                Config.serviceNameModels.put(jsonObject.getString("category_name"), serviceNameList);


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
            Config.serviceCategorylist.clear();
            List<String> serviceNameList = new ArrayList<String>(categorySet);
            Config.serviceCategorylist.addAll(serviceNameList);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //refresh Providers
    public void fetchClients(final int iFlag, Context context) {

        if (utils.isConnectingToInternet()) {
            //fetchCustomers(2);
            StorageService storageService = new StorageService(context);

            Query q1 = QueryBuilder.build("provider_id", Config.providerModel.getStrProviderId(),
                    QueryBuilder.Operator.EQUALS);

            storageService.findDocsByQueryOrderBy(Config.collectionProviderDependent, q1, 30000, 0,
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

                                                /*if (!Config.customerIds.contains(jsonObject.getString("customer_id")))
                                                    Config.customerIds.add(jsonObject.getString("customer_id"));

                                                if (!Config.dependentIds.contains(jsonObject.getString("dependent_id")))
                                                    Config.dependentIds.add(jsonObject.getString("dependent_id"));*/


                                                Cursor cursor = CareGiver.getDbCon().fetch(
                                                        DbHelper.strTableNameCollection,
                                                        new String[]{DbHelper.COLUMN_DOCUMENT},
                                                        DbHelper.COLUMN_COLLECTION_NAME
                                                                + "=? and "
                                                                + DbHelper.COLUMN_OBJECT_ID + "=?",
                                                        new String[]{Config.collectionCustomer,
                                                                jsonObject.getString("customer_id")
                                                        },
                                                        null, "0, 1", true,
                                                        null, null
                                                );

                                                if (cursor.getCount() <= 0) {
                                                    String values[] = {
                                                            jsonObject.getString("customer_id"),
                                                            "",
                                                            "", Config.collectionCustomer,
                                                            "0", ""};

                                                    CareGiver.getDbCon().insert(
                                                            DbHelper.strTableNameCollection,
                                                            values, DbHelper.COLLECTION_FIELDS);
                                                }
                                                CareGiver.getDbCon().closeCursor(cursor);


                                                //dependent

                                                Cursor cursor1 = CareGiver.getDbCon().fetch(
                                                        DbHelper.strTableNameCollection,
                                                        new String[]{DbHelper.COLUMN_DOCUMENT},
                                                        DbHelper.COLUMN_COLLECTION_NAME
                                                                + "=? and "
                                                                + DbHelper.COLUMN_OBJECT_ID + "=?",
                                                        new String[]{Config.collectionDependent,
                                                                jsonObject.getString("dependent_id")
                                                        },
                                                        null, "0, 1", true,
                                                        null, null
                                                );

                                                if (cursor1.getCount() <= 0) {

                                                    String values1[] = {
                                                            jsonObject.getString("dependent_id"),
                                                            "",
                                                            "", Config.collectionDependent,
                                                            "0", ""};


                                                    CareGiver.getDbCon().insert(
                                                            DbHelper.strTableNameCollection,
                                                            values1, DbHelper.COLLECTION_FIELDS);
                                                }
                                                CareGiver.getDbCon().closeCursor(cursor1);


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
                                //fetchCustomers(iFlag);
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
}