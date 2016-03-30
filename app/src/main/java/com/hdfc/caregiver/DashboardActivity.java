package com.hdfc.caregiver;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdfc.app42service.StorageService;
import com.hdfc.caregiver.fragments.ClientFragment;
import com.hdfc.caregiver.fragments.RatingsFragment;
import com.hdfc.caregiver.fragments.SimpleActivityFragment;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Libs;
import com.hdfc.models.ClientModel;
import com.hdfc.models.FileModel;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 16/1/23.
 */
public class DashboardActivity extends AppCompatActivity {


    private Libs libs;
    private ImageView mytask, clients, feedback;

    private TextView textViewTasks, textViewClients, textViewFeedback;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tasks);

        libs = new Libs(DashboardActivity.this);

        mytask = (ImageView)findViewById(R.id.buttonMyTasks);
        clients = (ImageView)findViewById(R.id.buttonClients);
        feedback = (ImageView)findViewById(R.id.buttonFeedback);

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

        if (Config.intSelectedMenu == Config.intSimpleActivityScreen) {
            Config.intSelectedMenu = 0;
            gotoSimpleActivity();
            mytask.setImageDrawable(getResources().getDrawable(R.mipmap.my_tasks_blue));
            textViewTasks.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }

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




}
