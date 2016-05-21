package com.hdfc.caregiver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.CrashLogger;
import com.hdfc.libs.Utils;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    public static Utils utils;
    private static String userName;
    private static ProgressDialog progressDialog;
    private static Handler dbOpenHandler;
    private AppUtils appUtils;
    private Context _ctxt;
    //private ArrayList<DependentModel> dependentModels = Config.dependentModels;
    // ArrayList<CustomerModel> customerModels = Config.customerModels;
    //private ArrayList<ClientModel> clientModels = Config.clientModels;
    private RelativeLayout relLayout;
    private EditText editEmail, editPassword;
    private RelativeLayout layoutLogin;

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

        // Crash log
        CrashLogger.getInstance().init(LoginActivity.this);

		/*Log.e(TAG,""+ DesEnc.encrypt(Constant.uatURL));
        Log.e(TAG," 1 "+ DesEnc.decrypt(DesEnc.encrypt(Constant.uatURL)));*/

        dbOpenHandler = new DbOpenHandler();

        DbOpenThread dbOpenThread = new DbOpenThread();
        dbOpenThread.start();
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
                                Config.fileModels.clear();
                                Config.activityModels.clear();
                                Config.customerModels.clear();
                                Config.dependentModels.clear();
                                Config.clientModels.clear();

                                /*User user = (User)o;

                                ArrayList<String> roleList = user.getRoleList();*/

                                //todo check rolelist
                                //roleList.size()>0 && roleList.get(0).equalsIgnoreCase("provider")
                                if (true)
                                    fetchProviders(progressDialog, userName);
                                else {
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
                                    JSONObject jsonObjectError = jsonObject.getJSONObject("app42Fault");
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

        StorageService storageService = new StorageService(LoginActivity.this);

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

                                    String strUpdatedDate = jsonDocument.getUpdatedAt();

                                    appUtils.createProviderModel(strDocument, strProviderId,
                                            strUpdatedDate);

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
                    public void onInsertionFailed(App42Exception ex) {

                    }

                    @Override
                    public void onFindDocFailed(App42Exception ex) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        try {
                            if (ex != null) {
                                utils.toast(2, 2, _ctxt.getString(R.string.invalid_credentials));
                            } else {
                                utils.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onUpdateDocFailed(App42Exception ex) {
                    }
                });
    }

    private void goToDashboard() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();

        Intent intent = new Intent(_ctxt, DashboardActivity.class);
        //intent.putExtra("WHICH_SCREEN", intWhichScreen);
        Config.intSelectedMenu = Config.intDashboardScreen;
        _ctxt.startActivity(intent);
        ((Activity) _ctxt).finish();
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
        finish();
    }

    public class DbOpenThread extends Thread {
        @Override
        public void run() {
            try {
                CareGiver.dbCon = DbCon.getInstance(LoginActivity.this);
                CareGiver.dbCon.open();
                dbOpenHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //

    public class DbOpenHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

        }
    }

}
