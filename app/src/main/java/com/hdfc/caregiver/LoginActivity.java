package com.hdfc.caregiver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UserService;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbCon;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.CrashLogger;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;
import com.hdfc.models.SourceModel;
import com.hdfc.models.UpdateVersionModel;
import com.hdfc.views.CheckView;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static String userName;
    private static ProgressDialog progressDialog;
    private static StorageService storageService;
    ImageView logoimageview;
    private Utils utils;
    //private RelativeLayout relLayout;
    private EditText editEmail, editPassword, editTextCaptcha, forgotPassUsername;
    private CheckView checkView;
    //private SharedPreferences sharedPreferences;
    private char[] res = new char[4];
    private Button button;
    private String email;
    private AlertDialog alertdialog;
    private SessionManager sessionManager;
    private RelativeLayout loadingPanel, relativelayout;
    private TextView textView;
    private List<SourceModel> sourcelist = new ArrayList<>();
    private String strVersion, strUrl;
    DownloadAsync downloadAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        relativelayout = (RelativeLayout) findViewById(R.id.relativelayout);
        textView = (TextView) findViewById(R.id.id_forgot);
        logoimageview = (ImageView) findViewById(R.id.logoimageview);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //relLayout = (RelativeLayout) findViewById(R.id.relativePass);
        //RelativeLayout layoutLogin = (RelativeLayout) findViewById(R.id.layoutLogin);
        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        button = (Button) findViewById(R.id.button);
        utils = new Utils(LoginActivity.this);
        //AppUtils appUtils = new AppUtils(LoginActivity.this);

        //todo remove in live
        if (Config.DEVELOPMENT) {
            editEmail.setText("carla1@gmail.com");
            editPassword.setText("we");
        }

        // sharedPreferences = getSharedPreferences(Config.strPreferenceName, MODE_PRIVATE);

        sessionManager = new SessionManager(LoginActivity.this);

        progressDialog = new ProgressDialog(LoginActivity.this);

      /*  try {
            ImageView imgBg = (ImageView) findViewById(R.id.imageBg);
            if (imgBg != null) {
                imgBg.setImageBitmap(Utils.decodeSampledBitmapFromResource(getResources(),
                        R.drawable.bg_blue, Config.intScreenWidth, Config.intScreenHeight));
            }

            //CrashLogger.getInstance().init(LoginActivity.this);
        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
        }
*/

        /*editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showPasswordfield();
            }
        });*/

     /*   editPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showPasswordfield();
                utils.traverseEditTexts(layoutLogin, getResources().getDrawable(R.drawable.edit_text),
                        getResources().getDrawable(R.drawable.edit_text_blue), editPassword);
            }
        });

        editEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                utils.traverseEditTexts(layoutLogin, getResources().getDrawable(R.drawable.edit_text),
                        getResources().getDrawable(R.drawable.edit_text_blcue), editEmail);
            }
        });*/


        updateVersion();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPassword();
            }
        });


        try {
            CrashLogger.getInstance().init(LoginActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        editEmail.setVisibility(View.INVISIBLE);
        editPassword.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);

        loadingPanel.setVisibility(View.VISIBLE);

      /*  try {
            Utils.log("1 ", AESCrypt.encrypt(Config.string, "a02dc8aa733cae3b7adde8c0ee7291d88dded72b1a00ad66b606e02bb26135d6"));
            Utils.log("2 ", AESCrypt.encrypt(Config.string, "db0c08979b92c3b5983b86afc79024c12afaa3be5a673344ad68dd3042c9e72f"));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }*/
    }

    private void showForgotPassword() {

        LayoutInflater layoutInflater = LayoutInflater.from(LoginActivity.this);
        View dialogView = layoutInflater.inflate(R.layout.forgot_password_custom_dialog, null);

        editTextCaptcha = (EditText) dialogView.findViewById(R.id.editTextCaptcha);
        forgotPassUsername = (EditText) dialogView.findViewById(R.id.editTextUserName);
        checkView = (CheckView) dialogView.findViewById(R.id.checkview2);
        ImageButton reloadCaptcha = (ImageButton) dialogView.findViewById(R.id.reloadCaptcha);

        res = checkView.getValidataAndSetImage();

        reloadCaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                res = checkView.getValidataAndSetImage();
            }
        });

        // Create the dialog (without showing)
        alertdialog = new AlertDialog.Builder(this).setTitle(getString(R.string.forgot_password))
                .setPositiveButton(getString(R.string.ok), null)
                .setNegativeButton(getString(R.string.cancel), null).setView(dialogView).create();

        alertdialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        alertdialog.show();
        alertdialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get user input and set it to result
                String scheck = new String(res);
                String string = editTextCaptcha.getText().toString();
                boolean b = string.equals(scheck);

                email = forgotPassUsername.getText().toString();

                if (TextUtils.isEmpty(email)) {

                    Utils.toast(2, 2, getString(R.string.error_invalid_email), LoginActivity.this);

                } else if (!Utils.isEmailValid(email)) {

                    Utils.toast(2, 2, getString(R.string.error_invalid_email), LoginActivity.this);

                } else if (!b) {

                    Utils.toast(2, 2, getString(R.string.enter_captcha), LoginActivity.this);

                } else {
                    resetPassword(email);
                }
            }
        });
    }

    private void resetPassword(String userEmail) {

        if (Utils.isConnectingToInternet(LoginActivity.this)) {

            progressDialog.setMessage(getString(R.string.verify_identity_password));
            progressDialog.setCancelable(false);
            progressDialog.show();

            fetchProviders(progressDialog, userEmail, 2);

        } else Utils.toast(2, 2, getString(R.string.warning_internet), LoginActivity.this);

    }

    private void resetPasswordApp42(String userEmail) {

        UserService userService = new UserService(LoginActivity.this);

        userService.resetUserPassword(userEmail, new App42CallBack() {
            @Override
            public void onSuccess(Object o) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (o != null) {
                    alertdialog.dismiss();

                    Utils.toast(1, 1, getString(R.string.resetted_password), LoginActivity.this);
                } else {
                    Utils.toast(1, 1, getString(R.string.warning_internet), LoginActivity.this);
                }
            }

            @Override
            public void onException(Exception e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (e != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(e.getMessage());
                        JSONObject jsonObjectError = jsonObject.getJSONObject("app42Fault");
                        String strMess = jsonObjectError.getString("details");
                        utils.toast(2, 2, strMess);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Utils.toast(1, 1, getString(R.string.warning_internet), LoginActivity.this);
                }
            }
        });
    }


    /*private void showPasswordfield() {
        if (relLayout.getVisibility() == View.GONE) {
            relLayout.setVisibility(View.VISIBLE);
            try {
                TranslateAnimation ta = new TranslateAnimation(0, 0, 15, Animation.RELATIVE_TO_SELF);
                ta.setDuration(1000);
                ta.setFillAfter(true);
                relLayout.startAnimation(ta);

                TranslateAnimation ed = new TranslateAnimation(0, 0, 15, Animation.RELATIVE_TO_SELF);
                ed.setDuration(1000);
                ed.setFillAfter(true);
                editEmail.startAnimation(ed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    public void updateVersion() {

        downloadAsync = new DownloadAsync();

        if (utils.isConnectingToInternet()) {

            storageService = new StorageService(LoginActivity.this);

            storageService.findAllDocs(Config.collectionUpdateVersion, new App42CallBack() {

                @Override
                public void onSuccess(Object o) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                    Storage response = (Storage) o;

                    if (response != null) {

                        if (response.getJsonDocList().size() > 0) {
                            try {
                                for (int i = 0; i < response.getJsonDocList().size(); i++) {

                                    Storage.JSONDocument jsonDocument = response.
                                            getJsonDocList().get(i);

                                    String strDocument = jsonDocument.getJsonDoc();
                                    try {
                                        JSONObject jsonObjectActivity =
                                                new JSONObject(strDocument);
                                        //utils.createUpdateVersionModel(jsonObjectActivity);

                                        UpdateVersionModel updateVersionModel = new UpdateVersionModel();

                                        JSONArray source = jsonObjectActivity.optJSONArray("source");

                                        try {
                                            if (source != null && source.length() > 0) {

                                                for (int m = 0; m < source.length(); m++) {
                                                    JSONObject jsonObject = source.getJSONObject(m);


                                                    if (jsonObject != null && jsonObject.length() > 0) {

                                                        SourceModel sourceModels = new SourceModel();

                                                        sourceModels.setStrAppVersion(jsonObject.getString("app_version"));
                                                        sourceModels.setStrSourceName(jsonObject.getString("source_name"));
                                                        sourceModels.setStrAppUrl(jsonObject.getString("app_url"));

                                                        updateVersionModel.setSourchModels(sourceModels);
                                                        Config.updateVersionModel.add(updateVersionModel);

                                                    }

                                                }

                                            }


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        sourcelist = Config.updateVersionModel.get(i).getSourchModels();


                                        if (sourcelist != null) {

                                            for (int x = 0; x < sourcelist.size(); x++) {

                                                if (sourcelist.get(x).getStrSourceName().equalsIgnoreCase("caretaker")) {
                                                    strVersion = sourcelist.get(x).getStrAppVersion();
                                                    strUrl = sourcelist.get(x).getStrAppUrl();

                                                }
                                            }
                                        }

                                        String version = strVersion;
                                        int latestversion = Integer.parseInt(version);


                                        if (Config.iAppVersion < latestversion) {
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                                            builder1.setTitle("Update Version");
                                            builder1.setMessage("Server have Latest APK Version.Please update your App Version");
                                            builder1.setCancelable(true);
                                            builder1.setNeutralButton(android.R.string.ok,
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
//                                                            upgradeApp(strUrl);
                                                            downloadAsync.execute();
                                                            dialog.cancel();
                                                        }
                                                    });

                                            AlertDialog alert11 = builder1.create();
                                            alert11.show();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

                    } else {
                        utils.toast(2, 2, getString(R.string.warning_internet));
                    }
                }

                @Override
                public void onException(Exception e) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                    if (e != null) {
                        //TODO plz check updateversion collection in app42 that time this toast uncomment
                        //  utils.toast(2, 2, getString(R.string.error));
                    } else {
                        utils.toast(2, 2, getString(R.string.warning_internet));
                    }
                }
            });

        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    class DownloadAsync extends AsyncTask<Void, Void, Void> {

        ProgressDialog	progress = new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            super.onPreExecute();

            progress.setMessage("Downloading...Please Wait...");
            progress.setCancelable(false);
            progress.show();

//			mProgress.show(LoginActivity.this, "Downloading",
//					"Please wait Operation perform With the server.");
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            Update(strUrl);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            progress.dismiss();
        }

    }

   /* public void upgradeApp(String strUrl) {

        //get destination to update file and set Uri
        //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
        //aplication with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
        //solution, please inform us in comment
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";

        destination += "NewZeal_v0.9.apk";
        final Uri uri = Uri.parse("file://" + destination);

        //Delete update file if exists
        File file = new File(destination);
        if (file.exists())
            //file.delete() - test this, I think sometimes it doesnt work
            file.delete();

        //get url of app on server
        String url = strUrl;// "https://play.google.com/store/apps/details?id=com.imangi.templerun&hl=en";

        //set downloadmanager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(getString(R.string.not_now));
        request.setTitle(getString(R.string.app_name));

        //set destination
        request.setDestinationUri(uri);

        // get download service and enqueue file
        final DownloadManager manager = (DownloadManager) getSystemService
                (Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);

        //set BroadcastReceiver to install app when .apk is downloaded
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(final Context ctxt, Intent intent) {
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                install.setDataAndType(uri,
                        manager.getMimeTypeForDownloadedFile(downloadId));
                startActivity(install);

                unregisterReceiver(this);
                finish();
            }
        };
        //register receiver for when .apk download is compete
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }*/

    public void Update(String strUrl) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(false);
            c.connect();

            String PATH = Environment.getExternalStorageDirectory()
                    + "/download/";
            File file = new File(PATH);
            file.mkdirs();
            File outputFile = new File(file, "New Zeal Specialist.apk");
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();//

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(
                    Uri.fromFile(new File(Environment
                            .getExternalStorageDirectory()
                            + "/download/"
                            + "New Zeal Specialist.apk")),
                    "application/vnd.android.package-archive");
            startActivity(intent);

//			mProgress.dismiss();

        } catch (IOException e) {
			/*Toast.makeText(getApplicationContext(), "Update error!",
					Toast.LENGTH_LONG).show();*/

            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (CareGiver.getDbCon() == null)
            new LoadDataTask().execute();
        else {

            editEmail.setVisibility(View.VISIBLE);
            editPassword.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);

            if (sessionManager.isLoggedIn() && !sessionManager.getEmail().equalsIgnoreCase("")
                    && !sessionManager.getProviderId().equalsIgnoreCase("")) {
                goToDashboard(false);
            }

            loadingPanel.setVisibility(View.GONE);
        }
    }

    public void validateLogin(View v) {

        //showPasswordfield();

     /*   utils.setEditTextDrawable(editEmail, getResources().getDrawable(R.drawable.edit_text));
        utils.setEditTextDrawable(editPassword, getResources().getDrawable(R.drawable.edit_text));*/

        //if (relLayout.getVisibility() == View.VISIBLE) {

        editEmail.setError(null);
        editPassword.setError(null);

        String uName = editEmail.getText().toString();
        userName = uName.toLowerCase();
        String password = editPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            editPassword.setError(getString(R.string.error_field_required));
            focusView = editPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(userName)) {
            editEmail.setError(getString(R.string.error_field_required));
            focusView = editEmail;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (Utils.isConnectingToInternet(LoginActivity.this)) {

                if (progressDialog != null) {
                    progressDialog.setMessage(getString(R.string.process_login));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }

                verifyLogin(password);

            } else Utils.toast(2, 2, getString(R.string.warning_internet), LoginActivity.this);
        }
    }

    private void verifyLogin(String strPassword) {

        UserService userService = new UserService(LoginActivity.this);

        userService.authenticate(userName, strPassword, new App42CallBack() {
            @Override
            public void onSuccess(Object o) {

                if (o != null) {

                    Config.dependentIds.clear();
                    Config.strActivityIds.clear();
                    Config.customerIds.clear();

                    Config.dependentIdsAdded.clear();
                    Config.customerIdsAdded.clear();

                    Config.feedBackModels.clear();
                    //CareGiver.getDbCon().deleteFiles();

                    Config.activityModels.clear();
                    Config.customerModels.clear();
                    Config.dependentModels.clear();
                    Config.clientModels.clear();

                    User user = (User) o;

                    ArrayList<String> roleList = user.getRoleList();

                    Utils.log(String.valueOf(roleList.size()), " ROLES ");

                 /*   if(userName.equalsIgnoreCase(App42API.getLoggedInUser())){

                    }*/

                    //todo check rolelist
                    //roleList.size()>0 && roleList.get(0).equalsIgnoreCase("provider")
                    if (true) {
                        fetchProviders(progressDialog, userName, 1);
                    } else {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Utils.toast(2, 2, getString(R.string.invalid_credentials),
                                LoginActivity.this);
                    }

                } else {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Utils.toast(2, 2, getString(R.string.warning_internet), LoginActivity.this);
                }
            }

            @Override
            public void onException(Exception e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                try {
                    if (e != null) {
                        JSONObject jsonObject = new JSONObject(e.getMessage());
                        JSONObject jsonObjectError = jsonObject.
                                getJSONObject("app42Fault");
                        String strMess = jsonObjectError.getString("details");

                        Utils.toast(2, 2, strMess, LoginActivity.this);
                    } else
                        Utils.toast(2, 2, getString(R.string.warning_internet), LoginActivity.this);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    Utils.toast(2, 2, getString(R.string.error), LoginActivity.this);
                }
            }
        });
    }

    private void fetchProviders(final ProgressDialog progressDialog, final String strUserName,
                                final int iFlag) {

        if (Utils.isConnectingToInternet(LoginActivity.this)) {

            StorageService storageService = new StorageService(LoginActivity.this);

            Query q1 = QueryBuilder.build("provider_email", strUserName.toLowerCase(), QueryBuilder.
                    Operator.EQUALS);

            storageService.findDocsByQueryOrderBy(Config.collectionProvider, q1, 1, 0,
                    "updated_date", 1, new App42CallBack() {
                        @Override
                        public void onSuccess(Object o) {
                            try {
                                Storage storage = (Storage) o;

                                if (storage.isResponseSuccess() && storage.getJsonDocList().
                                        size() > 0) {

                                    if (iFlag == 1 || iFlag == 3) {

                                        Storage.JSONDocument jsonDocument = storage.getJsonDocList().
                                                get(0);

                                        if (iFlag == 1) {
                                            sessionManager.createLoginSession(userName,
                                                    jsonDocument.getDocId());
                                            putLoginLog();
                                        }

                                        try {
                                            //CareGiver.getDbCon().beginDBTransaction();
                                            String values[] = {jsonDocument.getDocId(),
                                                    jsonDocument.getUpdatedAt(),
                                                    jsonDocument.getJsonDoc(),
                                                    Config.collectionProvider, "", "1", ""};

                                            String selection = DbHelper.COLUMN_OBJECT_ID +
                                                    " = ? and " + DbHelper.COLUMN_COLLECTION_NAME
                                                    + " = ? ";

                                            // WHERE clause arguments
                                            String[] selectionArgs = {jsonDocument.getDocId(),
                                                    Config.collectionProvider};

                                            CareGiver.getDbCon().updateInsert(
                                                    DbHelper.strTableNameCollection,
                                                    selection, values, DbHelper.COLLECTION_FIELDS,
                                                    selectionArgs);

                                            //CareGiver.getDbCon().insert(DbHelper.strTableNameCollection, values, DbHelper.COLLECTION_FIELDS);
                                            //CareGiver.getDbCon().dbTransactionSucessFull();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }/*finally {
                                            CareGiver.getDbCon().endDBTransaction();
                                        }*/
                                        //Config.providerModel.setStrProviderId(_strProviderId);
                                        if (iFlag == 1)
                                            goToDashboard(true);
                                        if (iFlag == 3)
                                            goToDashboard(false);
                                    } else {
                                        resetPasswordApp42(strUserName);
                                    }

                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    if (iFlag == 1)
                                        Utils.toast(2, 2, getString(R.string.invalid_credentials),
                                                LoginActivity.this);
                                    else if (iFlag == 3)
                                        goToDashboard(false);
                                    else
                                        Utils.toast(2, 2, getString(R.string.error_invalid_email),
                                                LoginActivity.this);
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                Utils.toast(2, 2, getString(R.string.warning_internet),
                                        LoginActivity.this);
                            }
                        }

                        @Override
                        public void onException(Exception e) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            try {
                                if (e != null) {
                                    Utils.log(e.getMessage(), " Failure ");
                                    if (iFlag == 1)
                                        Utils.toast(2, 2, getString(R.string.invalid_credentials),
                                                LoginActivity.this);
                                    else if (iFlag == 3)
                                        goToDashboard(false);
                                    else
                                        Utils.toast(2, 2, getString(R.string.error_invalid_email),
                                                LoginActivity.this);
                                } else {
                                    Utils.toast(2, 2, getString(R.string.warning_internet),
                                            LoginActivity.this);
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
        } else {
            if (iFlag == 3)
                goToDashboard(false);
            else {
                Utils.toast(2, 2, getString(R.string.warning_internet),
                        LoginActivity.this);
            }
        }
    }

    private void goToDashboard(boolean isFirst) {
        if (progressDialog.isShowing())
            progressDialog.dismiss();

        //loadingPanel.setVisibility(View.GONE);

        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.putExtra("LOAD", isFirst);
        Config.intSelectedMenu = Config.intDashboardScreen;
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        if (CareGiver.getDbCon() != null) {
            CareGiver.getDbCon().close();
        }
        finish();
    }

    private void putLoginLog() {

        if (Utils.isConnectingToInternet(LoginActivity.this)) {

            JSONObject jsonObject = new JSONObject();
            try {
                SessionManager sessionManager = new SessionManager(LoginActivity.this);

                Calendar calendar = Calendar.getInstance();
                Date datNow = calendar.getTime();
                String strDateNow = Utils.readFormat.format(datNow);

                jsonObject.put("user_id", sessionManager.getProviderId());
                jsonObject.put("source", "caregiver");
                jsonObject.put("user_type", "provider");
                jsonObject.put("device_id", Utils.getDeviceID(LoginActivity.this));
                jsonObject.put("os", Config.strOs);
                jsonObject.put("sdk_version", Config.iSdkVersion);
                jsonObject.put("app_version", Config.iAppVersion);
                jsonObject.put("time", strDateNow);
                //jsonObject.put("ip", Utils.getIPAddress(true));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            StorageService storageService = new StorageService(LoginActivity.this);

            storageService.insertDocs(Config.collectionLoginLog, jsonObject,
                    new AsyncApp42ServiceApi.App42StorageServiceListener() {

                        @Override
                        public void onDocumentInserted(Storage response) {
                            try {
                                if (response.isResponseSuccess()) {
                                    Utils.log(response.toString(), " LOG ");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onUpdateDocSuccess(Storage response) {
                        }

                        @Override
                        public void onFindDocSuccess(Storage response) {
                        }

                        @Override
                        public void onInsertionFailed(App42Exception ex) {
                            ex.printStackTrace();
                        }

                        @Override
                        public void onFindDocFailed(App42Exception ex) {
                        }

                        @Override
                        public void onUpdateDocFailed(App42Exception ex) {
                        }
                    });
        }
    }

    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                CareGiver.setDbCon(new DbCon(getApplicationContext()));
            } catch (Exception e) {
                e.getMessage();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {

                editEmail.setVisibility(View.VISIBLE);
                editPassword.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);

                if (sessionManager.isLoggedIn() && !sessionManager.getEmail().equalsIgnoreCase("")
                        && !sessionManager.getProviderId().equalsIgnoreCase("")) {
                    goToDashboard(false);
                    /*if (Utils.isConnectingToInternet(LoginActivity.this)) {

                        progressDialog.setMessage(getString(R.string.loading));
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        fetchProviders(progressDialog, sessionManager.getEmail(), 3);
                    } else {
                       *//* progressDialog.setMessage(getString(R.string.log_in));
                        progressDialog.setCancelable(false);
                        progressDialog.show();*//*
                        goToDashboard(false);
                    }*/
                }
                loadingPanel.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
