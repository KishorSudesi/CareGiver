package com.hdfc.caregiver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.services.GPSTracker;

import java.io.File;

/**
 * Created by Admin on 28-01-2016.
 */
public class ClientProfileActivity extends AppCompatActivity  {

    public static Bitmap bitmap = null;
    private static Handler backgroundThreadHandler;
    private static ProgressDialog mProgress = null;
    ImageView backbutton, edit;
    TextView age, health, account, address, mobile, direction;
    TextView txtAge, txtHealth, txtNotes, txtMap;
    TextView clientName;
    Utils utils;
    ImageView clientProfile, location;
    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;
    LinearLayout dialNubmer;
    private ImageView btnGetLocation = null;
    TextView editLocation = null;
    private String strClientName, strClientAddress, strImageName, strMobileNo;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        age = (TextView) findViewById(R.id.editTextAge);
        health = (TextView) findViewById(R.id.editTextHealthCondition);
        account = (TextView) findViewById(R.id.editTextAccountType);
        address = (TextView) findViewById(R.id.editTextAddress);
        mobile = (TextView) findViewById(R.id.editTextMobileNumber);
        editLocation = (TextView) findViewById(R.id.editTextGetDirection);
        btnGetLocation = (ImageView) findViewById(R.id.imageLocation);
        dialNubmer = (LinearLayout)findViewById(R.id.dialNumber);

        ImageButton backbtn = (ImageButton)findViewById(R.id.button1Back) ;
        txtAge = (TextView)findViewById(R.id.txtAge);
        txtHealth = (TextView)findViewById(R.id.txtHealth);
        txtNotes = (TextView)findViewById(R.id.txtNotes);
        txtMap = (TextView)findViewById(R.id.txtMap);

        clientName = (TextView) findViewById(R.id.textViewClientProfileName);
        clientProfile = (ImageView) findViewById(R.id.imageClientProfile);
        //location = (ImageView) findViewById(R.id.imageLocation);
        mProgress = new ProgressDialog(this);

        if (Config.customerModel != null) {
            strClientName = Config.customerModel.getStrName();
            strClientAddress = Config.customerModel.getStrCountryCode();
            strImageName = Config.customerModel.getStrCustomerID();
            strMobileNo = Config.customerModel.getStrContacts();
            age.setVisibility(View.GONE);
            health.setVisibility(View.GONE);
            account.setVisibility(View.GONE);
            editLocation.setVisibility(View.GONE);
            txtAge.setVisibility(View.GONE);
            txtHealth.setVisibility(View.GONE);
            txtMap.setVisibility(View.GONE);
            txtNotes.setVisibility(View.GONE);
            btnGetLocation.setVisibility(View.GONE);
        }

        if (Config.dependentModel != null) {
            strClientName = Config.dependentModel.getStrName();
            strClientAddress = Config.dependentModel.getStrAddress();

            age.setText(String.valueOf(Config.dependentModel.getIntAge()));
            health.setText(Config.dependentModel.getStrIllness());
            account.setText(Config.dependentModel.getStrNotes());
            strMobileNo = Config.dependentModel.getStrContacts();
            strImageName = Config.dependentModel.getStrDependentID();
            txtMap.setVisibility(View.GONE);
            editLocation.setVisibility(View.GONE);
            btnGetLocation.setVisibility(View.GONE);
        }

        utils = new Utils(ClientProfileActivity.this);

        clientProfile.setImageResource(R.drawable.person_icon);
        clientName.setText(strClientName);
        address.setText(strClientAddress);
        mobile.setText(strMobileNo);
/*

        edit = (ImageView) findViewById(R.id.imgPen);


        age.setEnabled(false);
        age.setFocusableInTouchMode(false);
        age.clearFocus();

        health.setEnabled(false);
        health.setFocusableInTouchMode(false);
        health.clearFocus();

        account.setEnabled(false);
        account.setFocusableInTouchMode(false);
        account.clearFocus();

        address.setEnabled(false);
        address.setFocusableInTouchMode(false);
        address.clearFocus();

        mobile.setEnabled(false);
        mobile.setFocusableInTouchMode(false);
        mobile.clearFocus();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                age.setEnabled(true);
                age.setInputType(InputType.TYPE_CLASS_NUMBER);
                age.setFocusableInTouchMode(true);
                age.requestFocus();

                health.setEnabled(true);
                health.setInputType(InputType.TYPE_CLASS_TEXT);
                health.setFocusableInTouchMode(true);
                health.requestFocus();

                account.setEnabled(true);
                account.setInputType(InputType.TYPE_CLASS_TEXT);
                account.setFocusableInTouchMode(true);
                account.requestFocus();

                address.setEnabled(true);
                address.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
                address.setFocusableInTouchMode(true);
                address.requestFocus();

                mobile.setEnabled(true);
                mobile.setInputType(InputType.TYPE_CLASS_PHONE);
                mobile.setFocusableInTouchMode(true);
                mobile.requestFocus();
            }
        });
*/


//        backbutton = (ImageView) findViewById(R.id.imgBackArrow);
//        backbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ClientProfileActivity.this,DashboardActivity.class);
//                Config.intSelectedMenu=Config.intClientScreen;
//                startActivity(intent);
//            }
//        });


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClientProfileActivity.this,DashboardActivity.class);
                Config.intSelectedMenu=Config.intClientScreen;
                startActivity(intent);
            }
        });
        dialNubmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                String strNo1 = "tel:" + strMobileNo;
                callIntent.setData(Uri.parse(strNo1));
                startActivity(callIntent);
            }
        });
        //if you want to lock screen for always Portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

       /* pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);
*/



        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSTracker gpsTracker = new GPSTracker(ClientProfileActivity.this);

                if (gpsTracker.getIsGPSTrackingEnabled()) {
                    String city = gpsTracker.getLocality(ClientProfileActivity.this);
                editLocation.setText(city);
                }else {
                    gpsTracker.showSettingsAlert();
                }
            }
        });


                locationMangaer = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onResume(){
        super.onResume();

        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundThreadHandler = new BackgroundThreadHandler();

        mProgress.setMessage(getResources().getString(R.string.loading));
        mProgress.show();
    }



    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

                File f = utils.getInternalFileImages(utils.replaceSpace(strImageName));

                Utils.log(strClientName, "FP ");

                if(f!=null&&f.exists()) {
                    bitmap = utils.getBitmapFromFile(f.getAbsolutePath(), Config.intWidth, Config.intHeight);
                    bitmap = utils.roundedBitmap(bitmap);
                }

                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();

            if (clientProfile != null && bitmap != null)
                clientProfile.setImageBitmap(bitmap);
        }
    }
}

