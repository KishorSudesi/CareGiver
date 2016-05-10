package com.hdfc.caregiver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdfc.app42service.App42GCMController;
import com.hdfc.caregiver.fragments.ClientFragment;
import com.hdfc.caregiver.fragments.RatingsFragment;
import com.hdfc.caregiver.fragments.SimpleActivityFragment;
import com.hdfc.config.Config;
import com.hdfc.libs.AppUtils;
import com.shephertz.app42.paas.sdk.android.App42API;

/**
 * Created by yuyidong on 16/1/23.
 */
public class DashboardActivity extends AppCompatActivity implements App42GCMController.App42GCMListener {


    private static Handler threadHandler;
    private static ProgressDialog progressDialog;
    private static AppUtils appUtils;
    private static AppCompatActivity appCompatActivity;
    private ImageView mytask, clients, feedback;
    private TextView textViewTasks, textViewClients, textViewFeedback;

    public static void gotoSimpleActivity() {

        // if (Config.intSelectedMenu != Config.intSimpleActivityScreen) {
        Config.intSelectedMenu = Config.intDashboardScreen;

        SimpleActivityFragment fragment = SimpleActivityFragment.newInstance();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        FragmentTransaction transaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        // }
    }

    public static void gotoSimpleActivityMenu() {

        progressDialog.setMessage(appCompatActivity.getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tasks);

        appUtils = new AppUtils(DashboardActivity.this);

        mytask = (ImageView)findViewById(R.id.buttonMyTasks);
        clients = (ImageView)findViewById(R.id.buttonClients);
        feedback = (ImageView)findViewById(R.id.buttonFeedback);

        progressDialog = new ProgressDialog(DashboardActivity.this);
        threadHandler = new ThreadHandler();

        textViewTasks = (TextView) findViewById(R.id.textViewTasks);
        textViewClients = (TextView) findViewById(R.id.textViewClients);
        textViewFeedback = (TextView) findViewById(R.id.textViewFeedback);
        setMenu();

        mytask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMenu();
                mytask.setImageDrawable(getResources().getDrawable(R.mipmap.my_tasks_blue));
                textViewTasks.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                //Config.intSelectedMenu = 0;
                refreshData();
            }
        });

        clients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMenu();
                clients.setImageDrawable(getResources().getDrawable(R.mipmap.clients_blue));
                textViewClients.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                //Config.intSelectedMenu = 0;
                gotoClient();
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMenu();
                feedback.setImageDrawable(getResources().getDrawable(R.mipmap.feedback_blue));
                textViewFeedback.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                //Config.intSelectedMenu = 0;
                gotoFeedback();
            }
        });

        if (Config.intSelectedMenu == Config.intClientScreen) {
            //Config.intSelectedMenu = 0;
            clients.setImageDrawable(getResources().getDrawable(R.mipmap.clients_blue));
            textViewClients.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            gotoClient();
        }

        if (Config.intSelectedMenu == Config.intRatingsScreen) {
            Config.intSelectedMenu = 0;
            feedback.setImageDrawable(getResources().getDrawable(R.mipmap.feedback_blue));
            textViewFeedback.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            gotoFeedback();
        }
        App42API.setLoggedInUser(Config.providerModel.getStrEmail());

        appCompatActivity = DashboardActivity.this;
    }

    @Override
    public void onError(String var1) {

    }

    @Override
    public void onGCMRegistrationId(String gcmRegId) {
        //Log.e("Registr" , gcmRegId);
        App42GCMController.storeRegistrationId(this, gcmRegId);
        if (!App42GCMController.isApp42Registerd(DashboardActivity.this))
            App42GCMController.registerOnApp42(App42API.getLoggedInUser(), gcmRegId, this);
    }

    @Override
    public void onApp42Response(String var1) {

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (App42GCMController.isPlayServiceAvailable(this)) {
            App42GCMController.getRegistrationId(DashboardActivity.this,
                    Config.strAppId, this);//prod. - 272065924531
        } else {
            /*Log.i("App42PushNotification",
                    "No valid Google Play Services APK found.");*/
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            boolean isPushReceived = getIntent().getBooleanExtra("message_delivered", false);
            String strPushMess = getIntent().getStringExtra("message");

            if (isPushReceived && strPushMess != null && !strPushMess.equalsIgnoreCase("")) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                builder.setTitle(getString(R.string.app_name));
                builder.setMessage(strPushMess);
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            } else {

                if (Config.intSelectedMenu == Config.intDashboardScreen) {

                    mytask.setImageDrawable(getResources().getDrawable(R.mipmap.my_tasks_blue));
                    textViewTasks.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                    refreshData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshData() {
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        appUtils.fetchActivities(progressDialog);
    }

    @Override
    public void onRegisterApp42(String var1) {
        App42GCMController.storeApp42Success(DashboardActivity.this);
    }

    public void setMenu(){
        mytask.setImageDrawable(getResources().getDrawable(R.mipmap.my_tasks));
        clients.setImageDrawable(getResources().getDrawable(R.mipmap.clients));
        feedback.setImageDrawable(getResources().getDrawable(R.mipmap.feedback));

        textViewTasks.setTextColor(getResources().getColor(R.color.colorAccentDark));
        textViewClients.setTextColor(getResources().getColor(R.color.colorAccentDark));
        textViewFeedback.setTextColor(getResources().getColor(R.color.colorAccentDark));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    public void gotoClient() {
        // if (Config.intSelectedMenu != Config.intClientScreen) {
            Config.intSelectedMenu = Config.intClientScreen;

            ClientFragment fragment = ClientFragment.newInstance();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        //}
    }

    public void gotoFeedback() {

        //if (Config.intSelectedMenu != Config.intRatingsScreen) {
        Config.intSelectedMenu = Config.intRatingsScreen;

        RatingsFragment fragment = RatingsFragment.newInstance();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        // }
    }

    public static class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            if (Config.intSelectedMenu == Config.intDashboardScreen)
                gotoSimpleActivity();
        }
    }

    public static class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {
                appUtils.loadAllFiles();
                threadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
