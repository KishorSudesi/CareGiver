package com.hdfc.caregiver;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.services.GPSTracker;

/**
 * Created by Admin on 28-01-2016.
 */
public class ClientProfileActivity extends AppCompatActivity  {

    public static Bitmap bitmap = null;
    private static String strUrl = "";
    //private static Handler backgroundThreadHandler;
    //private static ProgressDialog mProgress = null;
    //ImageView backbutton, edit;
    //private TextView direction;
    //private ImageView location;
    private TextView editLocation = null;
    //private LocationListener locationListener = null;
    private String strClientName;
    private String strClientAddress;
    private String strMobileNo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        TextView age = (TextView) findViewById(R.id.editTextAge);
        TextView health = (TextView) findViewById(R.id.editTextHealthCondition);
        TextView account = (TextView) findViewById(R.id.editTextAccountType);
        TextView address = (TextView) findViewById(R.id.editTextAddress);
        TextView mobile = (TextView) findViewById(R.id.editTextMobileNumber);
        editLocation = (TextView) findViewById(R.id.editTextGetDirection);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ImageView btnGetLocation = (ImageView) findViewById(R.id.imageLocation);
        LinearLayout dialNubmer = (LinearLayout) findViewById(R.id.dialNumber);

        Button backbtn = (Button) findViewById(R.id.buttonBack);
        TextView txtAge = (TextView) findViewById(R.id.txtAge);
        TextView txtHealth = (TextView) findViewById(R.id.txtHealth);
        TextView txtNotes = (TextView) findViewById(R.id.txtNotes);
        TextView txtMap = (TextView) findViewById(R.id.txtMap);

        TextView clientName = (TextView) findViewById(R.id.textViewClientProfileName);
        ImageView clientProfile = (ImageView) findViewById(R.id.imageClientProfile);
        //location = (ImageView) findViewById(R.id.imageLocation);
        //mProgress = new ProgressDialog(this);

        //String strImageName;

        if (Config.customerModel != null) {
            strClientName = Config.customerModel.getStrName();
            strClientAddress = Config.customerModel.getStrCountryCode();
            // strImageName = Config.customerModel.getStrCustomerID();
            strMobileNo = Config.customerModel.getStrContacts();

            strUrl = Config.customerModel.getStrImgUrl();

            if (age != null) {
                age.setVisibility(View.GONE);
            }
            if (health != null) {
                health.setVisibility(View.GONE);
            }
            if (account != null) {
                account.setVisibility(View.GONE);
            }
            editLocation.setVisibility(View.GONE);
            if (txtAge != null) {
                txtAge.setVisibility(View.GONE);
            }
            if (txtHealth != null) {
                txtHealth.setVisibility(View.GONE);
            }
            if (txtMap != null) {
                txtMap.setVisibility(View.GONE);
            }
            if (txtNotes != null) {
                txtNotes.setVisibility(View.GONE);
            }
            if (btnGetLocation != null) {
                btnGetLocation.setVisibility(View.GONE);
            }
        }

        if (Config.dependentModel != null) {
            strClientName = Config.dependentModel.getStrName();
            strClientAddress = Config.dependentModel.getStrAddress();

            if (age != null) {
                age.setText(String.valueOf(Config.dependentModel.getIntAge()));
            }
            if (health != null) {
                health.setText(Config.dependentModel.getStrIllness());
            }
            if (account != null) {
                account.setText(Config.dependentModel.getStrNotes());
            }
            strMobileNo = Config.dependentModel.getStrContacts();
            //strImageName = Config.dependentModel.getStrDependentID();
            if (txtMap != null) {
                txtMap.setVisibility(View.GONE);
            }
            editLocation.setVisibility(View.GONE);
            if (btnGetLocation != null) {
                btnGetLocation.setVisibility(View.GONE);
            }

            strUrl = Config.dependentModel.getStrImageUrl();
        }

//        Utils utils = new Utils(ClientProfileActivity.this);

        if (clientProfile != null) {
            clientProfile.setImageResource(R.drawable.person_icon);

            clientProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.showProfileImage(strUrl, ClientProfileActivity.this);
                }
            });
        }
        if (clientName != null) {
            clientName.setText(strClientName);
        }
        if (address != null) {
            address.setText(strClientAddress);
        }
        if (mobile != null) {
            mobile.setText(strMobileNo);
        }

        if (backbtn != null) {
            backbtn.setVisibility(View.VISIBLE);
            backbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ClientProfileActivity.this, DashboardActivity.class);
                    Config.intSelectedMenu = Config.intClientScreen;
                    startActivity(intent);
                    finish();
                }
            });
        }

        if (dialNubmer != null) {
            dialNubmer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    String strNo1 = "tel:" + strMobileNo;
                    callIntent.setData(Uri.parse(strNo1));
                    startActivity(callIntent);
                }
            });
        }
        //if you want to lock screen for always Portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

       /* pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);
        */

        if (btnGetLocation != null) {
            btnGetLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GPSTracker gpsTracker = new GPSTracker(ClientProfileActivity.this);

                    if (gpsTracker.getIsGPSTrackingEnabled()) {
                        String city = gpsTracker.getLocality(ClientProfileActivity.this);
                        editLocation.setText(city);
                    } else {
                        gpsTracker.showSettingsAlert();
                    }
                }
            });
        }


      /*  LocationManager locationMangaer = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);*/

        if (clientProfile != null) {
           /* Glide.with(ClientProfileActivity.this)
                    .load(strUrl)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(ClientProfileActivity.this))
                    .placeholder(R.drawable.person_icon)
                    .crossFade()
                    .into(clientProfile);*/

            Utils.loadGlide(ClientProfileActivity.this, strUrl, clientProfile, progressBar);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

      /*  BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundThreadHandler = new BackgroundThreadHandler();

        mProgress.setMessage(getResources().getString(R.string.loading));
        mProgress.show();*/
    }

    @Override
    public void onBackPressed() {
        //

        Intent intent = new Intent(ClientProfileActivity.this, DashboardActivity.class);
        Config.intSelectedMenu = Config.intClientScreen;
        startActivity(intent);
        finish();
    }

    /*  public class BackgroundThread extends Thread {
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
    }*/
}

