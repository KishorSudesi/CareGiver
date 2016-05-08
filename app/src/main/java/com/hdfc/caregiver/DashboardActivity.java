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
import com.hdfc.app42service.App42GCMService;
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
    private AppUtils appUtils;
    private ImageView mytask, clients, feedback;
    private TextView textViewTasks, textViewClients, textViewFeedback;

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

        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();

        if (progressDialog != null) {
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


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
                Config.intSelectedMenu = 0;
                gotoSimpleActivity();
            }
        });

        clients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMenu();
                clients.setImageDrawable(getResources().getDrawable(R.mipmap.clients_blue));
                textViewClients.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                Config.intSelectedMenu = 0;
                gotoClient();
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMenu();
                feedback.setImageDrawable(getResources().getDrawable(R.mipmap.feedback_blue));
                textViewFeedback.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                Config.intSelectedMenu = 0;
                gotoFeedback();
            }
        });

        if (Config.intSelectedMenu == Config.intClientScreen) {
            Config.intSelectedMenu = 0;
            gotoClient();
            clients.setImageDrawable(getResources().getDrawable(R.mipmap.clients_blue));
            textViewClients.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        if (Config.intSelectedMenu == Config.intRatingsScreen) {
            Config.intSelectedMenu = 0;
            gotoFeedback();
            feedback.setImageDrawable(getResources().getDrawable(R.mipmap.feedback_blue));
            textViewFeedback.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        App42API.setLoggedInUser(Config.providerModel.getStrEmail());
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRegisterApp42(String var1) {
        App42GCMController.storeApp42Success(DashboardActivity.this);
    }

    public void unregisterGcm() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    App42GCMService.unRegisterGcm();
                } catch (Exception bug) {
                    bug.printStackTrace();
                }
            }
        });
        thread.start();
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
        if (Config.intSelectedMenu != Config.intClientScreen) {
            Config.intSelectedMenu = Config.intClientScreen;
            ClientFragment fragment = ClientFragment.newInstance();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
    public void gotoFeedback() {

        if (Config.intSelectedMenu != Config.intRatingsScreen) {
            Config.intSelectedMenu = Config.intRatingsScreen;

            RatingsFragment fragment = RatingsFragment.newInstance();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void gotoSimpleActivity() {

        if (Config.intSelectedMenu != Config.intSimpleActivityScreen) {
            Config.intSelectedMenu = Config.intSimpleActivityScreen;

            SimpleActivityFragment fragment = SimpleActivityFragment.newInstance();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            if (Config.intSelectedMenu == Config.intSimpleActivityScreen
                    || Config.intSelectedMenu == Config.intDashboardScreen) {
                Config.intSelectedMenu = 0;
                mytask.setImageDrawable(getResources().getDrawable(R.mipmap.my_tasks_blue));
                textViewTasks.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                gotoSimpleActivity();

            }
        }
    }

    public class BackgroundThread extends Thread {
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
