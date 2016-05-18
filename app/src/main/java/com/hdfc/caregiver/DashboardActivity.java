package com.hdfc.caregiver;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hdfc.app42service.App42GCMController;
import com.hdfc.app42service.App42GCMService;
import com.hdfc.caregiver.fragments.ClientFragment;
import com.hdfc.caregiver.fragments.DashboardFragment;
import com.hdfc.caregiver.fragments.RatingsFragment;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbCon;
import com.hdfc.libs.AppUtils;
import com.hdfc.libs.NetworkStateReceiver;
import com.shephertz.app42.paas.sdk.android.App42API;

/**
 * Created by yuyidong on 16/1/23.
 */
public class DashboardActivity extends AppCompatActivity implements App42GCMController.App42GCMListener, NetworkStateReceiver.NetworkStateReceiverListener {


    private static Handler threadHandler;
    private static ProgressDialog progressDialog;
    private static AppUtils appUtils;
    private static AppCompatActivity appCompatActivity;
    private static RelativeLayout loadingPanel;
    private static LinearLayout net_error_layout;
    final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent
                    .getStringExtra(App42GCMService.ExtraMessage);
            /*Log.i("mBroadcastReceiver", "" + " : "
                    + message);*/

            if (message != null && !message.equalsIgnoreCase("")) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                builder.setTitle(getString(R.string.app_name));
                builder.setMessage(message);
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
              /*  builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });*/
                builder.show();
            }

        }
    };
    private NetworkStateReceiver networkStateReceiver;
    private ImageView mytask, clients, feedback;
    private TextView textViewTasks, textViewClients, textViewFeedback;

    public static void gotoSimpleActivity() {

        // if (Config.intSelectedMenu != Config.intSimpleActivityScreen) {
        Config.intSelectedMenu = Config.intDashboardScreen;

        DashboardFragment fragment = DashboardFragment.newInstance();
        FragmentTransaction transaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        // }
    }

    public static void gotoSimpleActivityMenu() {

        /*progressDialog.setMessage(appCompatActivity.getString(R.string.loading));
        progressDialog.setCancelable(true);
        progressDialog.show();*/

        //loadingPanel.setVisibility(View.INVISIBLE);

        Config.intSelectedMenu = Config.intDashboardScreen;

        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tasks);

        try {

            appUtils = new AppUtils(DashboardActivity.this);

            mytask = (ImageView) findViewById(R.id.buttonMyTasks);
            clients = (ImageView) findViewById(R.id.buttonClients);
            feedback = (ImageView) findViewById(R.id.buttonFeedback);

            loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);

            progressDialog = new ProgressDialog(DashboardActivity.this);
            threadHandler = new ThreadHandler();

            textViewTasks = (TextView) findViewById(R.id.textViewTasks);
            textViewClients = (TextView) findViewById(R.id.textViewClients);
            textViewFeedback = (TextView) findViewById(R.id.textViewFeedback);

            net_error_layout = (LinearLayout) findViewById(R.id.pnd_net_error);

            setMenu();

            try {
                networkStateReceiver = new NetworkStateReceiver();
                networkStateReceiver.addListener(this);
                this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
            } catch (Exception e) {
                e.printStackTrace();
            }

            mytask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuDashboard();
                }
            });

            textViewTasks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuDashboard();
                }
            });

            clients.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuClients();
                }
            });

            textViewClients.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuClients();
                }
            });

            feedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            textViewFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuFeedback();
                }
            });

            if (Config.intSelectedMenu == Config.intClientScreen) {
                //Config.intSelectedMenu = 0;
            /*clients.setImageDrawable(getResources().getDrawable(R.mipmap.clients_blue));
            textViewClients.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            gotoClient();*/
                menuClients();
            }

            if (Config.intSelectedMenu == Config.intRatingsScreen) {
            /*Config.intSelectedMenu = 0;
            feedback.setImageDrawable(getResources().getDrawable(R.mipmap.feedback_blue));
            textViewFeedback.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            gotoFeedback();*/
                menuFeedback();
            }
            App42API.setLoggedInUser(Config.providerModel.getStrEmail());

            appCompatActivity = DashboardActivity.this;

            loadingPanel.setVisibility(View.VISIBLE);

            CareGiver.dbCon = DbCon.getInstance(DashboardActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void menuDashboard() {
        setMenu();
        mytask.setImageDrawable(getResources().getDrawable(R.mipmap.my_tasks_blue));
        textViewTasks.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        //Config.intSelectedMenu = 0;
        refreshData();
    }

    public void menuClients() {
        setMenu();
        clients.setImageDrawable(getResources().getDrawable(R.mipmap.clients_blue));
        textViewClients.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        //Config.intSelectedMenu = 0;
        gotoClient();
    }

    public void menuFeedback() {
        setMenu();
        feedback.setImageDrawable(getResources().getDrawable(R.mipmap.feedback_blue));
        textViewFeedback.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        //Config.intSelectedMenu = 0;
        gotoFeedback();
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
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mBroadcastReceiver);
            if (networkStateReceiver != null)
                unregisterReceiver(networkStateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            IntentFilter filter = new IntentFilter(
                    App42GCMService.DisplayMessageAction);
            filter.setPriority(2);
            registerReceiver(mBroadcastReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (Config.providerModel == null || Config.providerModel.getStrName() == null) {
            AppUtils.logout();
        } else {

            try {
            /*boolean isPushReceived = getIntent().getBooleanExtra("message_delivered", false);
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
            } else {*/

                if (Config.intSelectedMenu == Config.intDashboardScreen) {

                    mytask.setImageDrawable(getResources().getDrawable(R.mipmap.my_tasks_blue));
                    textViewTasks.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                    refreshData();
                }
                //}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

       /* Config.intSelectedMenu = Config.intDashboardScreen;

        SimpleActivityFragment fragment = SimpleActivityFragment.newInstance();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();*/
    }

    public void refreshData() {

        Config.dependentIds.clear();
        Config.strActivityIds.clear();
        Config.customerIds.clear();

        Config.dependentIdsAdded.clear();
        Config.customerIdsAdded.clear();

        //Config.fileModels.clear();

        Config.activityModels.clear();
        Config.dependentModels.clear();
        Config.customerModels.clear();

        Config.clientModels.clear();
        Config.feedBackModels.clear();

       /* Config.strServcieIds.clear();
        Config.serviceModels.clear();*/
        //Config.servicelist.clear();
        Config.strDependentNames.clear();

       /* progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();*/

        //gotoSimpleActivity();

        loadingPanel.setVisibility(View.VISIBLE);

        appUtils.fetchActivities(loadingPanel);
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

    @Override
    public void networkAvailable() {
        //net_error_layout.setVisibility(View.GONE);
    }

    @Override
    public void networkUnavailable() {
        //net_error_layout.setVisibility(View.VISIBLE);
    }

    public static class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
           /* if (progressDialog.isShowing())
                progressDialog.dismiss();*/

            loadingPanel.setVisibility(View.GONE);

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
