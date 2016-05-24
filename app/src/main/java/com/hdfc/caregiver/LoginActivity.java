package com.hdfc.caregiver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UserService;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbCon;
import com.hdfc.libs.AppUtils;
import com.hdfc.libs.CrashLogger;
import com.hdfc.libs.Utils;
import com.scottyab.aescrypt.AESCrypt;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    public static Utils utils;
    private static String userName;
    private static ProgressDialog progressDialog;
    private static String strUpdatedDate = "", strDocumentLocal = "", strProviderId = "";
    private AppUtils appUtils;
    private Context _ctxt;
    private RelativeLayout relLayout;
    private EditText editEmail, editPassword;
    private RelativeLayout layoutLogin;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        relLayout = (RelativeLayout) findViewById(R.id.relativePass);
        layoutLogin = (RelativeLayout) findViewById(R.id.layoutLogin);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        _ctxt= LoginActivity.this;
        appUtils = new AppUtils(LoginActivity.this);

        sharedPreferences = getSharedPreferences(Config.strPreferenceName, MODE_PRIVATE);

        utils = new Utils(LoginActivity.this);
        progressDialog = new ProgressDialog(LoginActivity.this);

        try {
            ImageView imgBg = (ImageView) findViewById(R.id.imageBg);
            if (imgBg != null) {
                imgBg.setImageBitmap(Utils.decodeSampledBitmapFromResource(getResources(),
                        R.drawable.bg_blue, Config.intScreenWidth, Config.intScreenHeight));
            }

            // CrashLogger.getInstance().init(LoginActivity.this);
        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
        }


        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordfield();
            }
        });

        editPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                        getResources().getDrawable(R.drawable.edit_text_blue), editEmail);
            }
        });

        CrashLogger.getInstance().init(LoginActivity.this);

        CareGiver.dbCon = DbCon.getInstance(LoginActivity.this);
        //CareGiver.dbCon.open();

    }

    private void showPasswordfield() {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (CareGiver.dbCon != null) {
            CareGiver.dbCon.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void validateLogin(View v) {

        showPasswordfield();

        utils.setEditTextDrawable(editEmail, getResources().getDrawable(R.drawable.edit_text));
        utils.setEditTextDrawable(editPassword, getResources().getDrawable(R.drawable.edit_text));

        if (relLayout.getVisibility() == View.VISIBLE) {

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
                        progressDialog.setMessage(_ctxt.getString(R.string.process_login));
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }

                    UserService userService = new UserService(_ctxt);

                    userService.authenticate(userName, password, new App42CallBack() {
                        @Override
                        public void onSuccess(Object o) {

                            if(o != null){

                                Config.dependentIds.clear();
                                Config.strActivityIds.clear();
                                Config.customerIds.clear();

                                Config.dependentIdsAdded.clear();
                                Config.customerIdsAdded.clear();

                                Config.feedBackModels.clear();
                                //Config.fileModels.clear();
                                CareGiver.dbCon.deleteFiles();

                                Config.activityModels.clear();
                                Config.customerModels.clear();
                                Config.dependentModels.clear();
                                Config.clientModels.clear();

                                /*User user = (User)o;

                                ArrayList<String> roleList = user.getRoleList();*/

                                //todo check rolelist
                                //roleList.size()>0 && roleList.get(0).equalsIgnoreCase("provider")
                                if (true) {
                                    fetchProviders(progressDialog, userName);
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.invalid_credentials));
                                }

                            }else {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                utils.toast(2, 2, getString(R.string.warning_internet));
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
                                    utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                                utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                            }
                        }
                    });
                } else utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
            }
        } //
    }

    private void fetchProviders(final ProgressDialog progressDialog, final String strUserName) {

        if (utils.isConnectingToInternet()) {

            StorageService storageService = new StorageService(LoginActivity.this);

            Query q1 = QueryBuilder.build("provider_email", strUserName, QueryBuilder.
                    Operator.EQUALS);

            storageService.findDocsByQueryOrderBy(Config.collectionProvider, q1, 1, 0,
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

                                        appUtils.createProviderModel(strDocument, _strProviderId);

                                        goToDashboard();

                                    } else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        utils.toast(2, 2, _ctxt.getString(R.string.invalid_credentials));
                                    }

                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onException(Exception e) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            try {
                                if (e != null) {
                                    Utils.log(e.getMessage(), " Failure ");
                                    utils.toast(2, 2, _ctxt.getString(R.string.invalid_credentials));
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

    private void goToDashboard() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();

        Intent intent = new Intent(_ctxt, DashboardActivity.class);
        //intent.putExtra("WHICH_SCREEN", intWhichScreen);
        Config.intSelectedMenu = Config.intDashboardScreen;
        _ctxt.startActivity(intent);
        //((Activity) _ctxt).finish();
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
        finish();
    }
}
