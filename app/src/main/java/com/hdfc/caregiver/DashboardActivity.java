package com.hdfc.caregiver;

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
import com.hdfc.caregiver.fragments.MileStoneFragment;
import com.hdfc.caregiver.fragments.RatingsFragment;
import com.hdfc.caregiver.fragments.SimpleActivityFragment;
import com.hdfc.config.Config;
import com.hdfc.libs.AppUtils;
import com.hdfc.libs.NetworkStateReceiver;
import com.hdfc.libs.Utils;
import com.shephertz.app42.paas.sdk.android.App42API;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by yuyidong on 16/1/23.
 */
public class DashboardActivity extends AppCompatActivity implements
        App42GCMController.App42GCMListener, NetworkStateReceiver.NetworkStateReceiverListener {


    private static RelativeLayout loadingPanel;
    private static Handler threadHandler;
    private static AppUtils appUtils;
    private static Utils utils;
    private static AppCompatActivity appCompatActivity;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent
                    .getStringExtra(App42GCMService.ExtraMessage);

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
                builder.show();
            }
        }
    };

    private LinearLayout net_error_layout;
    private NetworkStateReceiver networkStateReceiver;
    private ImageView mytask, clients, feedback;
    private TextView textViewTasks, textViewClients, textViewFeedback;

    public static void gotoSimpleActivityMenu() {

        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();
    }

    private void gotoSimpleActivity() {

        /*if (Config.intSelectedMenu != Config.intDashboardScreen) {
*/
            Config.intSelectedMenu = Config.intDashboardScreen;
            DashboardFragment fragment = DashboardFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().
                    beginTransaction();
            transaction.replace(R.id.frameLayout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        //}

        refreshDashboardData();
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tasks);

        try {
            appUtils = new AppUtils(DashboardActivity.this);
            utils = new Utils(DashboardActivity.this);

            mytask = (ImageView) findViewById(R.id.buttonMyTasks);
            clients = (ImageView) findViewById(R.id.buttonClients);
            feedback = (ImageView) findViewById(R.id.buttonFeedback);

            loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
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

            App42API.setLoggedInUser(Config.providerModel.getStrEmail());

            //App42Log.setDebug(true);


            appCompatActivity = DashboardActivity.this;

            if (Config.intSelectedMenu == Config.intDashboardScreen) {

                loadingPanel.setVisibility(View.VISIBLE);

                mytask.setImageDrawable(getResources().getDrawable(R.mipmap.my_tasks_blue));
                textViewTasks.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                gotoSimpleActivity();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void menuDashboard() {
        setMenu();
        mytask.setImageDrawable(getResources().getDrawable(R.mipmap.my_tasks_blue));
        textViewTasks.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        //Config.intSelectedMenu = 0;
        gotoSimpleActivity();
    }

    private void menuClients() {
        setMenu();
        clients.setImageDrawable(getResources().getDrawable(R.mipmap.clients_blue));
        textViewClients.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        //Config.intSelectedMenu = 0;
        gotoClient();
    }

    private void menuFeedback() {
        setMenu();
        feedback.setImageDrawable(getResources().getDrawable(R.mipmap.feedback_blue));
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
                    Config.strAppId, this);
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

            registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.
                    CONNECTIVITY_ACTION));

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Config.providerModel == null || Config.providerModel.getStrName() == null) {
            AppUtils.logout();
        }
    }


    @Override
    public void onRegisterApp42(String var1) {
        App42GCMController.storeApp42Success(DashboardActivity.this);
    }

    private void setMenu() {
        mytask.setImageDrawable(getResources().getDrawable(R.mipmap.my_tasks));
        clients.setImageDrawable(getResources().getDrawable(R.mipmap.clients));
        feedback.setImageDrawable(getResources().getDrawable(R.mipmap.feedback));

        textViewTasks.setTextColor(getResources().getColor(R.color.colorAccentDark));
        textViewClients.setTextColor(getResources().getColor(R.color.colorAccentDark));
        textViewFeedback.setTextColor(getResources().getColor(R.color.colorAccentDark));
    }

    @Override
    public void onBackPressed() {
    }

    private void refreshDashboardData() {

        if (utils.isConnectingToInternet()) {

            loadingPanel.setVisibility(View.VISIBLE);

            Config.dependentIds.clear();
            Config.strActivityIds.clear();
            Config.customerIds.clear();

            Config.dependentIdsAdded.clear();
            Config.customerIdsAdded.clear();

            Config.activityModels.clear();
            Config.dependentModels.clear();
            Config.customerModels.clear();

            //Config.clientModels.clear();
            Config.clientNames.clear();
            Config.feedBackModels.clear();

            Config.strDependentNames.clear();
            Config.milestoneModels.clear();

            Calendar calendar = Calendar.getInstance();

          /*  int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH); // Note: zero based!
            int day = calendar.get(Calendar.DAY_OF_MONTH);*/

            Date date = calendar.getTime();

            DashboardFragment.strEndDate = Utils.writeFormatDateDB.format(date) + "T23:59:59.999+0000";
            DashboardFragment.strStartDate = Utils.writeFormatDateDB.format(date) + "T00:00:00.000+0000";

            DashboardFragment.strDate = Utils.writeFormatDate.format(date);

            Config.intSelectedMenu = Config.intDashboardScreen;

            appUtils.fetchActivities();

        } else {
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    private void refreshClientsData() {

        if (utils.isConnectingToInternet()) {

            loadingPanel.setVisibility(View.VISIBLE);

            Config.dependentIds.clear();
            Config.customerIds.clear();

            Config.intSelectedMenu = Config.intClientScreen;

            appUtils.fetchClients();

        } else {
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }


    private void gotoClient() {
        /*if (Config.intSelectedMenu != Config.intClientScreen) {
            Config.intSelectedMenu = Config.intClientScreen;*/
            Config.intSelectedMenu = Config.intClientScreen;
            ClientFragment fragment = ClientFragment.newInstance();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

            refreshClientsData();
        //}
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
    }

    @Override
    public void networkUnavailable() {
        net_error_layout.setVisibility(View.VISIBLE);
        loadingPanel.setVisibility(View.GONE);
    }

    private static class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            if (!utils.isConnectingToInternet())
                utils.toast(2, 2, appCompatActivity.getString(R.string.warning_internet));

            if (Config.intSelectedMenu == Config.intDashboardScreen) {
                SimpleActivityFragment.activityModels = Config.activityModels;
                SimpleActivityFragment.mAdapter.notifyDataSetChanged();
            }

            if (Config.intSelectedMenu == Config.intMileStoneScreen) {
                MileStoneFragment.milestoneModels = Config.milestoneModels;
                MileStoneFragment.mAdapter.notifyDataSetChanged();
            }

            if (Config.intSelectedMenu == Config.intClientScreen) {
                ClientFragment.prepareListData();
            }

            loadingPanel.setVisibility(View.GONE);
        }
    }

    private static class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {
                if (utils.isConnectingToInternet())
                    appUtils.loadAllFiles();

                threadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
