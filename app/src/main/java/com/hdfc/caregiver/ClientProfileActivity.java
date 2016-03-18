package com.hdfc.caregiver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.models.ClientModel;
import com.hdfc.services.GPSTracker;

import java.io.File;

/**
 * Created by Admin on 28-01-2016.
 */
public class ClientProfileActivity extends AppCompatActivity  {

    ImageView backbutton, edit;
    TextView age, health, account, address, mobile, direction;
    TextView clientName;
    Libs libs;
    ImageView clientProfile, location;
    public static Bitmap bitmap = null;
    private static Handler backgroundThreadHandler;

    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;

    private ImageView btnGetLocation = null;
    private TextView editLocation = null;

    private String strClientName;


    private static final String TAG = "Debug";
    private Boolean flag = false;
    private static ProgressDialog mProgress = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        age = (TextView) findViewById(R.id.editTextAge);
        health = (TextView) findViewById(R.id.editTextHealthCondition);
        account = (TextView) findViewById(R.id.editTextAccountType);
        address = (TextView) findViewById(R.id.editTextAddress);
        mobile = (TextView) findViewById(R.id.editTextMobileNumber);
        //  direction = (EditText) findViewById(R.id.editTextGetDirection);;
        clientName = (TextView) findViewById(R.id.textViewClientProfileName);
        clientProfile = (ImageView) findViewById(R.id.imageClientProfile);
        //location = (ImageView) findViewById(R.id.imageLocation);
        mProgress = new ProgressDialog(this);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        ClientModel client_model = (ClientModel) bundle.getSerializable("Client");

        strClientName=client_model.getName();

        libs = new Libs(ClientProfileActivity.this);

        clientProfile.setImageResource(R.drawable.carla2);
        clientName.setText(strClientName);
        age.setText(client_model.getAge());
        health.setText(client_model.getProblem());
        account.setText(client_model.getPremium());
        address.setText(client_model.getAddress());
        mobile.setText(client_model.getStrMobile());


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


        backbutton = (ImageView) findViewById(R.id.imgBackArrow);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ClientProfileActivity.this,DashboardActivity.class);
                Config.intSelectedMenu=Config.intClientScreen;
                //intent.putExtra("WHICH_SCREEN", intWhichScreen);
                startActivity(intent);

                /*Fragment anotherFragment = Fragment.instantiate(ClientProfileActivity.this, ClientFragment.class.getName());
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(R.id.frameLayout,anotherFragment);
                ft.addToBackStack(null);
                ft.commit();*/

                /*ClientFragment fragment = ClientFragment.newInstance();
                Bundle args = new Bundle();
                fragment.setArguments(args);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();*/
            }
        });


        //if you want to lock screen for always Portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

       /* pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);
*/
        editLocation = (TextView) findViewById(R.id.editTextGetDirection);

        btnGetLocation = (ImageView) findViewById(R.id.imageLocation);
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

                File f = libs.getInternalFileImages(libs.replaceSpace(strClientName));

                Libs.log(strClientName, "FP ");

                if(f!=null&&f.exists()) {
                    bitmap = libs.getBitmapFromFile(f.getAbsolutePath(), Config.intWidth, Config.intHeight);
                    bitmap = libs.roundedBitmap(bitmap);
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

