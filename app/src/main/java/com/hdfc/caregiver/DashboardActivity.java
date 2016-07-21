package com.hdfc.caregiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import com.hdfc.caregiver.fragments.ActivityFragment;
import com.hdfc.caregiver.fragments.ClientFragment;
import com.hdfc.caregiver.fragments.DashboardFragment;
import com.hdfc.caregiver.fragments.NotificationFragment;
import com.hdfc.caregiver.fragments.RatingsFragment;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.libs.AppUtils;
import com.hdfc.libs.NetworkStateReceiver;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;
import com.shephertz.app42.paas.sdk.android.App42API;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by yuyidong on 16/1/23.
 */
public class DashboardActivity extends AppCompatActivity implements
        App42GCMController.App42GCMListener, NetworkStateReceiver.NetworkStateReceiverListener {


    public static boolean isLoaded;
    private static RelativeLayout loadingPanel;
    //private Utils utils;
    private static AppCompatActivity appCompatActivity;
    //todo remove static variables with ref. context
    //private static Handler threadHandler;
    private static AppUtils appUtils;
    private LinearLayout net_error_layout;
    private NetworkStateReceiver networkStateReceiver;
    private ImageView mytask, clients, feedback, notification;
    private TextView textViewTasks, textViewClients, textViewFeedback, textViewNotification;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent
                    .getStringExtra(App42GCMService.ExtraMessage);

            if (message != null && !message.equalsIgnoreCase("")) {
                AppUtils.loadNotifications(DashboardActivity.this);
                AppUtils.fetchActivitiesSync(DashboardActivity.this);
                //appUtils.fetchClients(2, appCompatActivity);

                if (Config.intSelectedMenu != Config.intNotificationScreen)
                    showPushDialog(message);
            }
        }
    };
    private SessionManager sessionManager;

    public static void gotoSimpleActivityMenu(boolean isLoader) {

        appUtils.createCustomerModel();

        if (isLoader && loadingPanel.getVisibility() == View.VISIBLE)
            loadingPanel.setVisibility(View.GONE);

        if (Config.intSelectedMenu == Config.intDashboardScreen) {

            String strStartDate = DashboardFragment._strDate + " 00:00:00.000";
            String strEndDate = DashboardFragment._strDate + " 24:00:00.000";

            //appUtils.createCustomerModel();
            appUtils.createActivityModel(strStartDate, strEndDate);
            ActivityFragment.activityModels = Config.activityModels;
            ActivityFragment.mAdapter.notifyDataSetChanged();

            CareGiver.getDbCon().getDb();

            if (isLoader)
                AppUtils.fetchActivitiesSync(appCompatActivity);
        }
    }

    public static void refreshClientsData() {

        if (Utils.isConnectingToInternet(appCompatActivity)) {

            // AppUtils appUtils = new AppUtils(appCompatActivity);
            AppUtils.fetchClients(2, appCompatActivity);

        } else {
            Utils.toast(2, 2, appCompatActivity.getString(R.string.warning_internet),
                    appCompatActivity);
            loadingPanel.setVisibility(View.GONE);
        }
    }

    private static void reloadActivities() {

        if (Config.intSelectedMenu == Config.intDashboardScreen) {

            /*ActivityFragment.activityModels = Config.activityModels;
            ActivityFragment.mAdapter.notifyDataSetChanged();*/

            String strStartDate = DashboardFragment._strDate + " 00:00:00.000";
            String strEndDate = DashboardFragment._strDate + " 24:00:00.000";

            appUtils.createActivityModel(strStartDate, strEndDate);
            appUtils.createCustomerModel();
            ActivityFragment.activityModels = Config.activityModels;
            ActivityFragment.mAdapter.notifyDataSetChanged();
        }

        loadingPanel.setVisibility(View.GONE);
    }

    private void showPushDialog(final String strMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(strMessage);
        builder.setPositiveButton(getString(R.string.menu_notification), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                menuNotification(false);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void gotoSimpleActivity(boolean isLoad) {
        goToDashboard();
        refreshDashboardData(isLoad);
    }

    private void goToDashboard() {
        Config.intSelectedMenu = Config.intDashboardScreen;
        DashboardFragment fragment = DashboardFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tasks);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        try {
            appUtils = new AppUtils(DashboardActivity.this);
            appCompatActivity = DashboardActivity.this;
            //utils = new Utils();

            mytask = (ImageView) findViewById(R.id.buttonMyTasks);
            clients = (ImageView) findViewById(R.id.buttonClients);
            feedback = (ImageView) findViewById(R.id.buttonFeedback);
            notification = (ImageView) findViewById(R.id.buttonNotification);

            loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);

            textViewTasks = (TextView) findViewById(R.id.textViewTasks);
            textViewClients = (TextView) findViewById(R.id.textViewClients);
            textViewFeedback = (TextView) findViewById(R.id.textViewFeedback);
            textViewNotification = (TextView) findViewById(R.id.textViewNotification);

            net_error_layout = (LinearLayout) findViewById(R.id.pnd_net_error);

            setMenu();

            try {
                networkStateReceiver = new NetworkStateReceiver();
                networkStateReceiver.addListener(this);
                this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.
                        ConnectivityManager.CONNECTIVITY_ACTION));
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
                    menuClientsLoad();
                }
            });

            textViewClients.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuClientsLoad();
                }
            });

            notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuNotification(false);
                }
            });

            textViewNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuNotification(false);
                }
            });

            feedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuFeedback();
                }
            });

            textViewFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuFeedback();
                }
            });

            if (Config.intSelectedMenu == Config.intClientScreen) {
                menuClients();
            }

            if (Config.intSelectedMenu == Config.intRatingsScreen) {
                menuFeedback();
            }

            if (Config.intSelectedMenu == Config.intNotificationScreen) {
                menuNotification(false);
            }

            sessionManager = new SessionManager(DashboardActivity.this);

            appUtils.createProviderModel(sessionManager.getProviderId());

            if (Config.providerModel != null)
                App42API.setLoggedInUser(Config.providerModel.getStrEmail());

            //App42Log.setDebug(true);


            Bundle bundle = getIntent().getExtras();

            boolean b = false;

            if (bundle != null) {
                b = bundle.getBoolean("LOAD");
            }

            /*if(bCreated){
                Snackbar.make(, getString(R.string.activity_added), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }*/

            if (b) {


                //refreshClientsData();

                mytask.setImageDrawable(getResources().getDrawable(R.mipmap.my_task));
                textViewTasks.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                if (Config.intSelectedMenu == Config.intDashboardScreen) {
                    loadingPanel.setVisibility(View.VISIBLE);
                    gotoSimpleActivity(true);
                }
            } else {
                //appUtils.createCustomerModel();
                if (Config.intSelectedMenu == Config.intDashboardScreen) {
                    gotoSimpleActivity(false);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToClients() {
        Config.intSelectedMenu = Config.intClientScreen;
        ClientFragment fragment = ClientFragment.newInstance();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void goToNotification(boolean b) {
        Config.intSelectedMenu = Config.intNotificationScreen;
        NotificationFragment fragment = NotificationFragment.newInstance(b);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void menuClientsLoad() {
        setMenu();
        clients.setImageDrawable(getResources().getDrawable(R.mipmap.client));
        textViewClients.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        //Config.intSelectedMenu = 0;
        goToClients();
    }

    private void menuNotification(boolean b) {
        setMenu();
        notification.setImageDrawable(getResources().getDrawable(R.mipmap.notification_active));
        textViewNotification.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        goToNotification(b);
    }

    private void menuDashboard() {
        setMenu();
        mytask.setImageDrawable(getResources().getDrawable(R.mipmap.my_task));
        textViewTasks.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        Config.intSelectedMenu = Config.intDashboardScreen;
        goToDashboard();
    }

    private void menuClients() {
        setMenu();
        clients.setImageDrawable(getResources().getDrawable(R.mipmap.client));
        textViewClients.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        //Config.intSelectedMenu = 0;
        goToClients();
    }

    private void menuFeedback() {
        setMenu();
        feedback.setImageDrawable(getResources().getDrawable(R.mipmap.my_account_active));
        textViewFeedback.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        //Config.intSelectedMenu = Config.intRatingsScreen;
        gotoFeedback();
    }

    @Override
    public void onError(String var1) {
    }

    @Override
    public void onGCMRegistrationId(String gcmRegId) {
        App42GCMController.storeRegistrationId(this, gcmRegId);
        if (!App42GCMController.isApp42Registerd(DashboardActivity.this)) {
            App42GCMController.registerOnApp42(App42API.getLoggedInUser(), gcmRegId, this);
            //Utils.log(gcmRegId, " GCM ");
        }
    }

    @Override
    public void onApp42Response(String var1) {
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (App42GCMController.isPlayServiceAvailable(this)) {
            App42GCMController.getRegistrationId(DashboardActivity.this,
                    Config.strAppId, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            isLoaded = false;
            unregisterReceiver(mBroadcastReceiver);
            if (networkStateReceiver != null)
                unregisterReceiver(networkStateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getStringExtra(App42GCMService.ExtraMessage) != null) {
            String strMess = intent.getStringExtra(App42GCMService.ExtraMessage);

            AppUtils.loadNotifications(DashboardActivity.this);
            AppUtils.fetchActivitiesSync(DashboardActivity.this);
            //AppUtils.fetchClients(2, appCompatActivity);
            //if(Config.intSelectedMenu!=Config.intNotificationScreen)
            showPushDialog(strMess);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sessionManager.getProviderId() != null
                && !sessionManager.getProviderId().equalsIgnoreCase("")) {

            if (Config.providerModel == null || Config.providerModel.getStrName() == null)
                appUtils.createProviderModel(sessionManager.getProviderId());

            isLoaded = true;

            try {
                IntentFilter filter = new IntentFilter(
                        App42GCMService.DisplayMessageAction);
                filter.setPriority(2);
                registerReceiver(mBroadcastReceiver, filter);

                registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.
                        CONNECTIVITY_ACTION));

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            AppUtils.logout(DashboardActivity.this);
            Intent dashboardIntent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(dashboardIntent);
            finish();
        }

    }

    @Override
    public void onRegisterApp42(String var1) {
        App42GCMController.storeApp42Success(DashboardActivity.this);
    }

    private void setMenu() {
        mytask.setImageDrawable(getResources().getDrawable(R.mipmap.my_task_inactive));
        clients.setImageDrawable(getResources().getDrawable(R.mipmap.client_inactive));
        feedback.setImageDrawable(getResources().getDrawable(R.mipmap.my_account));
        notification.setImageDrawable(getResources().getDrawable(R.mipmap.notification));

        textViewTasks.setTextColor(getResources().getColor(R.color.colorAccentDark));
        textViewClients.setTextColor(getResources().getColor(R.color.colorAccentDark));
        textViewFeedback.setTextColor(getResources().getColor(R.color.colorAccentDark));
        textViewNotification.setTextColor(getResources().getColor(R.color.colorAccentDark));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
       /* if (CareGiver.dbCon != null) {
            CareGiver.dbCon.close();
        }*/
    }

    private void refreshDashboardData(boolean isLoad) {

        Calendar calendar = Calendar.getInstance();

        Date date = calendar.getTime();

        String strDate = Utils.writeFormatDateDB.format(date);

        DashboardFragment.strEndDate = Utils.convertDateToStringQuery(Utils.
                convertStringToDateQuery(strDate + "T23:59:59.999"));
        DashboardFragment.strStartDate = Utils.convertDateToStringQuery(Utils.
                convertStringToDateQuery(strDate + "T00:00:00.000"));


        DashboardFragment.strDate = Utils.writeFormatDate.format(date);
        DashboardFragment._strDate = Utils.writeFormatDateDB.format(date);

        Config.intSelectedMenu = Config.intDashboardScreen;


        if (isLoad && Utils.isConnectingToInternet(appCompatActivity)) {

            loadingPanel.setVisibility(View.VISIBLE);

          /*  Config.dependentIds.clear();
            Config.customerIds.clear();
            Config.dependentIdsAdded.clear();
            Config.customerIdsAdded.clear();
            Config.dependentModels.clear();
            Config.customerModels.clear();*/

            //Config.milestoneModels.clear();
            //Config.clientModels.clear();
            // Config.feedBackModels.clear();
            //Config.activityModels.clear();
            //Config.strActivityIds.clear();


            if (Config.providerModel != null)
                appUtils.fetchActivities();

        } else {
            reloadActivities();
            //Utils.toast(2, 2, getString(R.string.warning_internet), DashboardActivity.this);
        }
    }

    private void gotoFeedback() {

        //if (Config.intSelectedMenu != Config.intRatingsScreen) {
            Config.intSelectedMenu = Config.intRatingsScreen;

            RatingsFragment fragment = RatingsFragment.newInstance();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        //}
    }

    @Override
    public void networkAvailable() {
        net_error_layout.setVisibility(View.GONE);
        //AppUtils.fetchActivitiesSync(DashboardActivity.this);
        //refreshClientsData();
    }

    @Override
    public void networkUnavailable() {
        net_error_layout.setVisibility(View.VISIBLE);
        loadingPanel.setVisibility(View.GONE);

        net_error_layout.postDelayed(
                new Runnable() {
                    public void run() {
                        net_error_layout.setVisibility(View.GONE);
                    }
                }, 3000);

     /*   Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        net_error_layout.setVisibility(View.GONE);
                    }
                });
            }
        };
        thread.start(); //start the thread*/
    }
}
