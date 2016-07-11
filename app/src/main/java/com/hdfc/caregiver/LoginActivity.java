package com.hdfc.caregiver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UserService;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbCon;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.AppUtils;
import com.hdfc.libs.CrashLogger;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;
import com.hdfc.views.CheckView;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.user.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private static String userName;
    private static ProgressDialog progressDialog;
    private Utils utils;
    private AppUtils appUtils;
    //private RelativeLayout relLayout;
    private EditText editEmail, editPassword, editTextCaptcha, forgotPassUsername;
    private CheckView checkView;
    //private SharedPreferences sharedPreferences;
    private char[] res = new char[4];
    private Button button;
    private String email;
    private AlertDialog alertdialog;
    private SessionManager sessionManager;
    private RelativeLayout loadingPanel;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //relLayout = (RelativeLayout) findViewById(R.id.relativePass);
        //RelativeLayout layoutLogin = (RelativeLayout) findViewById(R.id.layoutLogin);
        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        textView = (TextView) findViewById(R.id.id_forgot);
        button = (Button) findViewById(R.id.button);
        utils = new Utils();
        appUtils = new AppUtils(LoginActivity.this);

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

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showPasswordfield();
            }
        });

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


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPassword();
            }
        });


        CrashLogger.getInstance().init(LoginActivity.this);

        editEmail.setVisibility(View.INVISIBLE);
        editPassword.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);

        loadingPanel.setVisibility(View.VISIBLE);
        new LoadDataTask().execute();
    }

    private void showForgotPassword(){

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

                    utils.toast(2, 2, getString(R.string.error_invalid_email), LoginActivity.this);

                } else if (!utils.isEmailValid(email)) {

                    utils.toast(2, 2, getString(R.string.error_invalid_email), LoginActivity.this);

                } else if (!b) {

                    utils.toast(2, 2, getString(R.string.enter_captcha), LoginActivity.this);

                } else {
                    resetPassword(email);
                }
            }
        });
    }

    private void resetPassword(String userEmail){

        if (utils.isConnectingToInternet()) {

            progressDialog.setMessage(getString(R.string.verify_identity_password));
            progressDialog.setCancelable(false);
            progressDialog.show();

            fetchProviders(progressDialog, userEmail, 2);

        } else utils.toast(2, 2, getString(R.string.warning_internet), LoginActivity.this);

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

                    utils.toast(1, 1, getString(R.string.resetted_password), LoginActivity.this);
                } else {
                    utils.toast(1, 1, getString(R.string.warning_internet), LoginActivity.this);
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
                    utils.toast(1, 1, getString(R.string.warning_internet), LoginActivity.this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                if (utils.isConnectingToInternet()) {

                    if (progressDialog != null) {
                        progressDialog.setMessage(getString(R.string.process_login));
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }

                    verifyLogin(password);

                } else utils.toast(2, 2, getString(R.string.warning_internet), LoginActivity.this);
            }
        //} //
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
                    //Config.fileModels.clear();
                    CareGiver.getDbCon().deleteFiles();

                    Config.activityModels.clear();
                    Config.customerModels.clear();
                    Config.dependentModels.clear();
                    Config.clientModels.clear();

                    User user = (User) o;

                    ArrayList<String> roleList = user.getRoleList();

                    Utils.log(String.valueOf(roleList.size()), " ROLES ");

                    //todo check rolelist
                    //roleList.size()>0 && roleList.get(0).equalsIgnoreCase("provider")
                    if (true) {
                        fetchProviders(progressDialog, userName, 1);
                    } else {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        utils.toast(2, 2, getString(R.string.invalid_credentials),
                                LoginActivity.this);
                    }

                } else {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    utils.toast(2, 2, getString(R.string.warning_internet), LoginActivity.this);
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

                        utils.toast(2, 2, strMess);
                    } else
                        utils.toast(2, 2, getString(R.string.warning_internet), LoginActivity.this);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    utils.toast(2, 2, getString(R.string.warning_internet), LoginActivity.this);
                }
            }
        });
    }

    private void fetchProviders(final ProgressDialog progressDialog, final String strUserName,
                                final int iFlag) {

        if (utils.isConnectingToInternet()) {

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

                                    if (iFlag == 1) {

                                        Storage.JSONDocument jsonDocument = storage.getJsonDocList().
                                                get(0);

                                        sessionManager.createLoginSession(userName, jsonDocument.getDocId());

                                        try {
                                            //CareGiver.getDbCon().beginDBTransaction();
                                            String values[] = {jsonDocument.getDocId(),
                                                    jsonDocument.getUpdatedAt(),
                                                    jsonDocument.getJsonDoc(),
                                                    Config.collectionProvider, "1", ""};

                                            CareGiver.getDbCon().insert(DbHelper.strTableNameCollection, values, DbHelper.COLLECTION_FIELDS);
                                            //CareGiver.getDbCon().dbTransactionSucessFull();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }/*finally {
                                            CareGiver.getDbCon().endDBTransaction();
                                        }*/
                                        //Config.providerModel.setStrProviderId(_strProviderId);
                                        goToDashboard();
                                    } else {
                                        resetPasswordApp42(strUserName);
                                    }

                                    } else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();

                                    if (iFlag == 1)
                                        utils.toast(2, 2, getString(R.string.invalid_credentials),
                                                LoginActivity.this);
                                    else
                                        utils.toast(2, 2, getString(R.string.error_invalid_email),
                                                LoginActivity.this);
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                utils.toast(2, 2, getString(R.string.warning_internet),
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
                                        utils.toast(2, 2, getString(R.string.invalid_credentials),
                                                LoginActivity.this);
                                    else
                                        utils.toast(2, 2, getString(R.string.error_invalid_email),
                                                LoginActivity.this);
                                } else {
                                    utils.toast(2, 2, getString(R.string.warning_internet),
                                            LoginActivity.this);
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
        }
    }

    private void goToDashboard() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();

        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.putExtra("LOAD", true);
        Config.intSelectedMenu = Config.intDashboardScreen;
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
        if (CareGiver.getDbCon() != null) {
            CareGiver.getDbCon().close();
        }
        finish();
    }

    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //CareGiver.dbCon = DbCon.getInstance(getApplicationContext());

                CareGiver.setDbCon(new DbCon(getApplicationContext()));

                //exportDB();
            } catch (Exception e) {
                e.getMessage();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            loadingPanel.setVisibility(View.GONE);

            try {

                editEmail.setVisibility(View.VISIBLE);
                editPassword.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                
                if (sessionManager.isLoggedIn() && !sessionManager.getEmail().equalsIgnoreCase("")
                        && !sessionManager.getProviderId().equalsIgnoreCase("")) {

                    goToDashboard();

                } else {


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
