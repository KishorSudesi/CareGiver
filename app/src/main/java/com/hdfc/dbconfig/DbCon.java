package com.hdfc.dbconfig;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class DbCon {

    private static DbHelper dbHelper;
    private static boolean isDbOpened = false;
    private static DbCon dbConInstance = null;
    private static Handler dbOpenHandler;
    private Context context;

    public DbCon(Context context) {
        this.context = context;
        open();
    }

    public static synchronized DbCon getInstance(Context ctx) {

        if (dbConInstance == null) {
            dbConInstance = new DbCon(ctx.getApplicationContext());
        }
        return dbConInstance;
    }

    public DbCon open() {
        try {
            dbOpenHandler = new DbOpenHandler();
            Thread dbOpenThread = new DbOpenThread();
            dbOpenThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }


   /* public long insertUser(String strName, String strEmail, String strPassword, String strContactNo, long longUserId, String strImagPath, String strAddress) {

        long longInserted = 0;

        if (isDbOpened) {

            Cursor cur = null;

            try {

                cur = dbHelper.fetch("user", new String[]{"user_id"}, "user_id=?", new String[]{String.valueOf(longUserId)}, null, null, false, null, null);

                if (cur.getCount() <= 0) {

                    longInserted = dbHelper.insert(new String[]{strName, strEmail, strPassword, strContactNo, "0", strImagPath, strAddress},
                            new String[]{"name", "email", "password", "contact_no", "status", "image_path", "address"}, "user");
                } else {

                    dbHelper.update("user_id=?", new String[]{strName, strEmail, strPassword, strContactNo, strAddress},
                            new String[]{"name", "email", "password", "contact_no", "address"}, "user", new String[]{String.valueOf(longUserId)});

                    if (!strImagPath.equalsIgnoreCase("")) {
                        dbHelper.update("user_id=?", new String[]{strImagPath},
                                new String[]{"image_path"}, "user", new String[]{String.valueOf(longUserId)});
                    }

                    longInserted = longUserId;
                }

                dbHelper.closeCursor(cur);
            } catch (Exception e) {
                dbHelper.closeCursor(cur);
                e.printStackTrace();
            }
        }
        return longInserted;
    }*/

    /*public void deleteTempUsers() {

        if (isDbOpened) {
            try {
                dbHelper.delete("dependant", "status<=?", new String[]{"1"});
                //dbHelper.delete("user", "status=?", new String[]{"0"});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    /*public long insertDependant(String strName, String strContactNo, String strAddress, String strRelation, String strCustomerEmail, String strImageName, String strEmail) {

        long longInserted = 0;

        if (isDbOpened) {

            Cursor cur = null;

            try {

                cur = dbHelper.fetch("dependant", new String[]{"dependent_id"}, "name=? and customer_email=?", new String[]{strName, strCustomerEmail}, null, null, false, null, null);

                if (cur.getCount() <= 0) {

                    longInserted = dbHelper.insert(new String[]{strName, strContactNo, strAddress, strRelation, strCustomerEmail, "0", strImageName, strEmail},
                            new String[]{"name", "contact_no", "address", "relationship", "customer_email", "status", "image_path", "email"}, "dependant");
                } else {

                    dbHelper.update("name=? and customer_email=?", new String[]{strContactNo, strAddress, strRelation, strEmail},
                            new String[]{"contact_no", "address", "relationship", "email"}, "dependant", new String[]{strName, strCustomerEmail});

                    if (!strImageName.equalsIgnoreCase("")) {
                        dbHelper.update("name=? and customer_email=?", new String[]{strImageName},
                                new String[]{"image_path"}, "dependant", new String[]{strName, strCustomerEmail});
                    }
                    cur.moveToFirst();
                    longInserted = cur.getInt(0);
                }

                dbHelper.closeCursor(cur);
            } catch (Exception e) {
                dbHelper.closeCursor(cur);
            }
        }
        return longInserted;
    }

    public String retrieveDependantPersonal(String strCustomerEmail, EditText editName, EditText editContactNo, EditText editAddress, EditText editRelation, String strName, EditText editEmail) {

        String strImg = "";

        if (isDbOpened && !strCustomerEmail.equalsIgnoreCase("")) {
            Cursor cur = null;

            try {

                cur = dbHelper.fetch("dependant", new String[]{"name", "contact_no", "address", "relationship", "image_path", "email"}, "customer_email=? and name=?", new String[]{strCustomerEmail, strName}, "name DESC", "0,1", true, null, null);

                if (cur.getCount() > 0) {
                    cur.moveToFirst();

                    while (!cur.isAfterLast()) {

                        strImg = cur.getString(4);
                        editName.setText(cur.getString(0));
                        editContactNo.setText(cur.getString(1));
                        editAddress.setText(cur.getString(2));
                        editRelation.setText(cur.getString(3));
                        editEmail.setText(cur.getString(5));

                        Libs.log(strImg + " 2", "Image 2");

                        cur.moveToNext();
                    }
                }

                dbHelper.closeCursor(cur);
            } catch (Exception e) {
                dbHelper.closeCursor(cur);
            }
        }
        return strImg;
    }

    public boolean updateDependantMedicalDetails(String strName, String strAge, String strDiseases, String strNotes, String strCustomerEmail, String strImagePathServer) {

        boolean isUpdated = false;

        if (isDbOpened) {

            try {
                isUpdated = dbHelper.update("name=? and customer_email=?", new String[]{strAge, strDiseases, strNotes, "1", strImagePathServer},
                        new String[]{"age", "diseases", "notes", "status", "image_path_server"}, "dependant", new String[]{strName, strCustomerEmail});

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isUpdated;
    }*/

   /* public void deleteTempDependants() {

        if (isDbOpened) {
            try {
                dbHelper.delete("dependant", "status=?", new String[]{"0"});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    /*public int retrieveDependants(String strEmail) {

        DependentModel dpndntModel;

        AddDependentFragment.CustomListViewValuesArr.clear();

        int count = 0;

        if (isDbOpened && !strEmail.equalsIgnoreCase("")) {
            Cursor cur = null;

            try {

                cur = dbHelper.fetch("dependant", new String[]{"name", "image_path", "relationship",
                        "notes", "address", "contact_no", "email"}, "customer_email=?",
                        new String[]{strEmail}, "relationship ASC", null, true, null, null);

                if(cur.getCount()>0) {

                    cur.moveToFirst();
                    while (!cur.isAfterLast()) {

                        dpndntModel = new DependentModel();
                        dpndntModel.setStrName(cur.getString(0));
                        dpndntModel.setStrRelation(cur.getString(2));
                        dpndntModel.setStrImg(cur.getString(1));
                        dpndntModel.setStrDesc(cur.getString(3));
                        dpndntModel.setStrAddress(cur.getString(4));
                        dpndntModel.setStrContacts(cur.getString(5));
                        dpndntModel.setStrEmail(cur.getString(6));

                        Libs.log(cur.getString(1) + " 3", "Image 3");

                        AddDependentFragment.CustomListViewValuesArr.add(dpndntModel);

                        cur.moveToNext();
                        count++;
                    }
                }

                dbHelper.closeCursor(cur);
            }catch (Exception e){
                dbHelper.closeCursor(cur);
            }
        }

        dpndntModel = new DependentModel();
        dpndntModel.setStrName(context.getResources().getString(R.string.add_dependent));
        dpndntModel.setStrRelation("");
        dpndntModel.setStrImg("");
        dpndntModel.setStrDesc("");
        dpndntModel.setStrAddress("");
        dpndntModel.setStrContacts("");
        dpndntModel.setStrEmail("");

        AddDependentFragment.CustomListViewValuesArr.add(dpndntModel);

        count++;

        return count;
    }*/

   /* public int retrieveConfirmDependants(String strEmail) {

        ConfirmViewModel confirmViewModel;

        ConfirmFragment.CustomListViewValuesArr.clear();

        int count = 0;

        if (!strEmail.equalsIgnoreCase("")) {

            try {

                confirmViewModel = new ConfirmViewModel();
                confirmViewModel.setStrName(SignupActivity.strCustomerName);
                confirmViewModel.setStrDesc("");
                confirmViewModel.setStrAddress(SignupActivity.strCustomerAddress);
                confirmViewModel.setStrContacts(SignupActivity.strCustomerContactNo);
                confirmViewModel.setStrEmail(SignupActivity.strCustomerEmail);/////chk this
                confirmViewModel.setStrImg(SignupActivity.strCustomerImg);

                count++;

                Libs.log(SignupActivity.strCustomerImg + " 4", "Image 4");

                ConfirmFragment.CustomListViewValuesArr.add(confirmViewModel);

                for (DependentModel dpndntModel : AddDependentFragment.CustomListViewValuesArr) {

                    if (!dpndntModel.getStrName().equalsIgnoreCase("Add Dependant")) {
                        confirmViewModel = new ConfirmViewModel();
                        confirmViewModel.setStrName(dpndntModel.getStrName());
                        confirmViewModel.setStrDesc(dpndntModel.getStrDesc());
                        confirmViewModel.setStrAddress(dpndntModel.getStrAddress());
                        confirmViewModel.setStrContacts(dpndntModel.getStrContacts());
                        confirmViewModel.setStrEmail(dpndntModel.getStrEmail());
                        confirmViewModel.setStrImg(dpndntModel.getStrImg());

                        Libs.log(dpndntModel.getStrImg() + " 5", "Image 5");

                        ConfirmFragment.CustomListViewValuesArr.add(confirmViewModel);
                        count++;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return count;
    }*/

    /*public boolean prepareData(String strCustomerImageUrl) {

        boolean isFormed = false;

        JSONObject jsonCustomer = new JSONObject();

        Config.jsonServer = null;

        try {

            if (isDbOpened) {

                jsonCustomer.put("customer_name", SignupActivity.strCustomerName);
                jsonCustomer.put("customer_address", SignupActivity.strCustomerAddress);
                jsonCustomer.put("customer_contact_no", SignupActivity.strCustomerContactNo);
                jsonCustomer.put("customer_email", SignupActivity.strCustomerEmail);/////chk this
                jsonCustomer.put("customer_profile_url", strCustomerImageUrl);/////chk this
                jsonCustomer.put("paytm_account", "paytm_account");/////chk this

                isFormed = true;

                //Cursor cur = null;

                *//*try {

                    cur = dbHelper.fetch("user", new String[]{"password"}, "email=?", new String[]{SignupActivity.strCustomerEmail}, null, null, false, null, null);

                    if (cur.getCount() > 0) {
                        cur.moveToFirst();
                        while (!cur.isAfterLast()) {

                            String str = cur.getString(0);

                           *//**//* MyCipher myCipher = new MyCipher(Config.string);
                            MyCipherData myCipherData = myCipher.encryptUTF8(str);*//**//*

                            String strPassLocal = AESCrypt.encrypt(Config.string, str);
                            jsonCustomer.put("customer_password", strPassLocal);
                            strPass = strPassLocal;

                            //Libs.bytesToHex(myCipherData.getData());

                            isFormed = true;
                            cur.moveToNext();
                        }
                        dbHelper.closeCursor(cur);
                    } else isFormed = false;
                } catch (Exception e) {
                    dbHelper.closeCursor(cur);
                    isFormed = false;
                }
*//*
                if (isFormed) {

                    Cursor cursorSubmit = null;

                    try {

                        cursorSubmit = dbHelper.fetch("dependant", new String[]{"name", "email",
                                "contact_no", "address", "relationship", "notes", "age", "diseases",
                                "image_path", "image_path_server"}, "customer_email=?",
                                new String[]{SignupActivity.strUserId}, "relationship ASC", null,
                                true, null, null);

                        int intCount = cursorSubmit.getCount();

                        if (intCount > 0) {

                            JSONArray jsonArrayDependant = new JSONArray();

                            int intDependantCount = 0;

                            cursorSubmit.moveToFirst();
                            while (!cursorSubmit.isAfterLast()) {

                                try {

                                    Libs.log(" 1 ", " ");

                                    JSONObject jsonDependant = new JSONObject();
                                    jsonDependant.put("dependent_name", cursorSubmit.getString(0));
                                    jsonDependant.put("dependent_email", cursorSubmit.getString(1));
                                    jsonDependant.put("dependent_contact_no", cursorSubmit.getString(2));
                                    jsonDependant.put("dependent_address", cursorSubmit.getString(3));
                                    jsonDependant.put("dependent_relation", cursorSubmit.getString(4));
                                    jsonDependant.put("dependent_notes", cursorSubmit.getString(5));
                                    jsonDependant.put("dependent_age", cursorSubmit.getString(6));
                                    jsonDependant.put("dependent_illness", cursorSubmit.getString(7));

                                    jsonDependant.put("health_bp", 90);
                                    jsonDependant.put("health_heart_rate", 75);

                                    jsonDependant.put("dependent_profile_url", cursorSubmit.getString(9));


                                    JSONArray jsonArrAct = new JSONArray();

                                    JSONObject pnObj2 = new JSONObject();

                                    pnObj2.put("provider_email", "provider@gmail.com");
                                    pnObj2.put("provider_contact_no", "321423423");
                                    pnObj2.put("provider_description", "This is a Test Data from APP42");
                                    pnObj2.put("provider_name", "Carla");
                                    pnObj2.put("activity_date", "19-03-2016 18:00:00");
                                    pnObj2.put("status", "upcoming");
                                    pnObj2.put("activity_name", "Buy grocery and delivery");
                                    pnObj2.put("author_profile_url", "http://url.com/23e3WQE124");

                                    jsonArrAct.put(pnObj2);

                                    for (int i = 0; i < 1; i++) {

                                        JSONObject pnObj3 = new JSONObject();

                                        pnObj3.put("provider_email", "provider@email.com");
                                        pnObj3.put("provider_contact_no", "9842103278");
                                        pnObj3.put("provider_description", "description");
                                        pnObj3.put("provider_name", "carla1");
                                        pnObj3.put("activity_date", i + "9-02-2016 18:00:00");
                                        pnObj3.put("status", "completed");
                                        pnObj3.put("activity_name", "Medical Checkups " + i);
                                        pnObj3.put("author_profile_url", "url");

                                        JSONArray jsonArrAct1 = new JSONArray();

                                        for (int j = 0; j < 1; j++) {

                                            JSONObject pnObj4 = new JSONObject();

                                            pnObj4.put("feedback_message", "good service by carla " + j);
                                            pnObj4.put("feedback_raring", 3);
                                            pnObj4.put("feedback_by", "hungal");
                                            pnObj4.put("report", 1);
                                            pnObj4.put("feedback_time", "19-02-2016 12:00:00");
                                            pnObj4.put("feedback_by_url", "url");

                                            jsonArrAct1.put(pnObj4);
                                        }

                                        pnObj3.put("feedbacks", jsonArrAct1);

                                        //
                                        JSONArray jsonArrAct2 = new JSONArray();

                                        for (int k = 0; k < 1; k++) {

                                            JSONObject pnObj5 = new JSONObject();

                                            pnObj5.put("video_taken", "19-01-2016 18:00:00");
                                            pnObj5.put("video_description", "Video taken on hungal house");
                                            pnObj5.put("video_name", "Video name " + k);
                                            pnObj5.put("video_url", "url");

                                            jsonArrAct2.put(pnObj5);
                                        }

                                        pnObj3.put("videos", jsonArrAct2);
                                        //

                                        //
                                        JSONArray jsonArrAct3 = new JSONArray();

                                        for (int l = 0; l < 1; l++) {

                                            JSONObject pnObj6 = new JSONObject();

                                            pnObj6.put("image_name", "abc.jpg ");
                                            pnObj6.put("image_url", "http://url");
                                            pnObj6.put("image_description", "This is a Test Data from APP42");
                                            pnObj6.put("image_taken", "19-01-2016 18:00:00");

                                            jsonArrAct3.put(pnObj6);
                                        }

                                        pnObj3.put("images", jsonArrAct3);


                                        jsonArrAct.put(pnObj3);
                                    }

                                    jsonDependant.put("activities", jsonArrAct);

                                    JSONArray jsonArr0 = new JSONArray();

                                    for (int m = 0; m < 3; m++) {

                                        JSONObject pnObj0 = new JSONObject();
                                        pnObj0.put("notification_message", "This is a Test Data from APP42 " + m);
                                        pnObj0.put("author", "hungal");
                                        pnObj0.put("time", "19-01-2016 18:00:00");
                                        pnObj0.put("author_profile_url", "http://url.com/23e3WQE124");
                                        jsonArr0.put(pnObj0);
                                    }

                                    jsonDependant.put("notifications", jsonArr0);


                                    JSONArray jsonArr = new JSONArray();

                                    for (int n = 0; n < 3; n++) {

                                        JSONObject pnObj = new JSONObject();
                                        pnObj.put("service_name", "Medical Checkups and Health checkups" + n);
                                        pnObj.put("service_features", "BP, Heart Rate, etc");
                                        pnObj.put("unit", 3);
                                        pnObj.put("unit_consumed", 10);
                                        jsonArr.put(pnObj);

                                    }
                                    jsonDependant.put("services", jsonArr);

                                    //health_status
                                    JSONArray jsonArrHealth = new JSONArray();

                                    for (int o = 0; o < 3; o++) {

                                        JSONObject pnObjHealth = new JSONObject();
                                        pnObjHealth.put("bp", 80 + o);
                                        pnObjHealth.put("heart_rate", 230 + o);
                                        pnObjHealth.put("time_taken", "29-01-2016 18:00:00");
                                        jsonArrHealth.put(pnObjHealth);

                                    }
                                    jsonDependant.put("health_status", jsonArrHealth);
                                    //

                                    jsonArrayDependant.put(jsonDependant);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                intDependantCount++;
                                cursorSubmit.moveToNext();
                            }

                            jsonCustomer.put("dependents", jsonArrayDependant);

                            if (intCount != intDependantCount) {
                                isFormed = false;
                            } else {
                                Config.jsonServer = jsonCustomer;
                                Libs.log(jsonCustomer.toString(), "");
                                //
                                isFormed = true;
                            }
                        }


                        dbHelper.closeCursor(cursorSubmit);
                    } catch (Exception e) {
                        dbHelper.closeCursor(cursorSubmit);
                        e.printStackTrace();
                        isFormed = false;
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            isFormed = false;
        }

        return isFormed;
    }*/

    public static class DbOpenHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            isDbOpened = true;
        }
    }

    public class DbOpenThread extends Thread {
        @Override
        public void run() {
            try {
                dbHelper = DbHelper.getInstance(context);
                dbHelper.open();
                dbOpenHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}