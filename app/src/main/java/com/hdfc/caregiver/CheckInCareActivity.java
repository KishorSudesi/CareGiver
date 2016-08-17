package com.hdfc.caregiver;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ayz4sci.androidfactory.permissionhelper.PermissionHelper;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.hdfc.app42service.App42GCMService;
import com.hdfc.app42service.PushNotificationService;
import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UploadService;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Utils;
import com.hdfc.models.CheckInCareActivityModel;
import com.hdfc.models.DependentModel;
import com.hdfc.models.ImageModel;
import com.hdfc.models.PictureModel;
import com.hdfc.models.SubActivityModel;
import com.hdfc.views.TouchImageView;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pl.tajchert.nammu.PermissionCallback;

/**
 * Created by Admin on 01-07-2016.
 */
public class CheckInCareActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {
    private static final int DIALOG_DOWNLOAD_PROGRESS1 = 1;
    private static final String[] option = {"N", "Y"};
    public static Uri uri;
    public static Bitmap bitmap = null;
    public static int Hall_IMAGE_COUNT = 0;
    public static int Kitchen_IMAGE_COUNT = 0;
    public static int Washroom_IMAGE_COUNT = 0;
    public static int Bedroom_IMAGE_COUNT = 0;
    private static String strImageName = "", strClientName = "";
    //private static int IMAGE_COUNT = 0;
    private static Boolean editcheckincare = false;
    private static Utils utils;
    //private static ProgressDialog mProgress = null;
    private static Handler backgroundThreadHandler, backgroundThreadHandlerFetch;
    private static boolean isImageChanged = false;
    private static ArrayList<String> imagePaths = new ArrayList<>();
    private static ArrayList<Bitmap> hallbitmaps = new ArrayList<>();
    private static ArrayList<Bitmap> kitchenbitmaps = new ArrayList<>();
    private static ArrayList<Bitmap> washroombitmaps = new ArrayList<>();
    private static ArrayList<Bitmap> bedroombitmaps = new ArrayList<>();
    private static ArrayList<ImageModel> hallimageModels = new ArrayList<>();
    private static ArrayList<ImageModel> kitchenimageModels = new ArrayList<>();
    private static ArrayList<ImageModel> washroomimageModels = new ArrayList<>();
    private static ArrayList<ImageModel> bedroomimageModels = new ArrayList<>();
    //private static String strName;
    private static boolean isCompleted = false, bLoad;
    private static boolean bViewLoaded, mImageChanged;
    private static StorageService storageService;
    private static ArrayList<DependentModel> dependent = new ArrayList<>();
    public String item = "",dependentId = "";
    private boolean isAccessible;
    private RelativeLayout loadingPanel;
    private RelativeLayout loadingPanelhall, loadingPanelkitchen, loadingPanelwash, loadingPanelbed;
    private String strCustomerEmail;
    private int isClicked = 0;
    private int isFlag = 0;
    private int isHallflag = 0;
    private int isKitchenflag = 0;
    private int isWashflag = 0;
    private int isBedflag = 0;
    private boolean isClick = false;
    private Button buttonHallAdd,buttonKitchenAdd,buttonWashroomAdd,buttonBedroomAdd;
    private Button uploadhallbtn,uploadkitchenbtn,uploadwashroombtn,uploadbedroombtn ;
    private EditText electronic, homeapplience, automobile, maidservices, kitchen_equipments,
            grocery, mediacomment,dependentname,driveredt;
    private TextView datetxt;
    private TextView txtwater;
    private TextView txtgas;
    private TextView txtelectricity;
    private boolean isAllowed;
    private TextView txttelephone;
    private ImageView client, pick_date,pick_date2,pick_date3,pick_date4;
    private TextView utilitystatus, waterstatus, gasstatus, electricitystatus, telephonestatus,
            equipmentstatus, grocerystatus, kitchenequipmentstatus, domestichelpstatus,
            uploadmediastatus, hallstatus, kitchenstatus, washroomstatus, bedroomstatus,
            homeessentialstatus;
    private String strDate,strDependentName,strcheckincare;
    private int hallImageCount, kitchenImageCount, washroomImageCount,
            bedroomImageCount, hallImageUploadCount, kitchenImageUploadCount,
            washroomImageUploadCount, bedroomImageUploadCount;
    private boolean success;
    private LinearLayout layouthall, layoutkitchen, layoutwashroom, layoutbedroom, mainlinearlayout;
    private CheckBox electrocheck, homecheck, autocheck, kitchenequipcheck, grocerycheck,
            domesticcheck,drivercheck;
    private String valdate,valkitchenequi,valkitchen, valgrocery, valequipment, valdomestic,valphoto, valautomobile,
            valmaidservices, valmediacomment, valcheckincarename;
    private View focusView = null;
    private ProgressDialog mProgressDialog;
    private String items[], strSelectedDate;
    private Spinner dependentspinner;
    private Bundle bundle;
    private PermissionHelper permissionHelper;
    private ProgressBar progressBar;

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+
            // date.getTime();
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.

            //strDate = Utils.writeFormatDateMY.format(date);
            strSelectedDate = Utils.readFormatLocalDB.format(date);

            String strwaterDate = Utils.writeFormatDateMY.format(date);
            String strelectricityDate = Utils.writeFormatDateMY.format(date);
            String strtelephoneDate = Utils.writeFormatDateMY.format(date);
            String strgasDate = Utils.writeFormatDateMY.format(date);
           // strcheckincare = Utils.queryFormatday.format(date);

         /*   if(editcheckincare){
                checkincarename.setText(Config.checkInCareModel.getStrName());
            }else {
                checkincarename.setText(Utils.queryFormatday.format(date));
            }

            if(!checkincarename.getText().toString().equals("")){
                checkincarename.setEnabled(false);
            }else{
                checkincarename.setEnabled(true);
            }*/
         /*   String _strDate = Utils.readFormat.format(date);
            String _strwaterDate = Utils.readFormat.format(date);
            String _strelectricityDate = Utils.readFormat.format(date);
            String _strtelephoneDate = Utils.readFormat.format(date);
            String _strgasDate = Utils.readFormat.format(date);*/

            if (isClicked == 0) {
                txtwater.setText(strwaterDate);
            }
            if (isClicked == 1) {
                txtgas.setText(strgasDate);
            }
            if (isClicked == 2) {
                txtelectricity.setText(strelectricityDate);
            }
            if (isClicked == 3) {
                txttelephone.setText(strtelephoneDate);
            }
          /*  if (isClicked == 4) {
                datetxt.setText(strDate);
            }*/
        }

        @Override
        public void onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }

    };

    private SlideDateTimeListener listener1 = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {

            strDate = Utils.writeFormatDateMY.format(date);

            strcheckincare = Utils.queryFormatday.format(date);

            if (isClicked == 4) {
                datetxt.setText(strDate);
            }
        }

        @Override
        public void onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }

    };

    //display progress dialog
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS1:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage(getString(R.string.loading));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();

                return mProgressDialog;

            default:
                return null;
        }
    }

    @Override
    protected void onDestroy() {
        permissionHelper.finish();
        super.onDestroy();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in_care);


        dependentspinner = (Spinner) findViewById(R.id.dependentspinner);
        dependentname = (EditText) findViewById(R.id.dependentname1);

        /////////////////////spinner for dependent
        ArrayList<String> strDependent = null;
        if(!editcheckincare) {
            try {
                dependentname.setVisibility(View.GONE);
                dependentspinner.setVisibility(View.VISIBLE);
                bundle = getIntent().getExtras();
                if (bundle != null) {
                    dependent = (ArrayList<DependentModel>) bundle.getSerializable("DEPENDENTS");

                }

                strDependent = new ArrayList<>();

                if (dependent != null && dependent.size() > 0) {
                    String name;
                    for (DependentModel dependentModel : dependent) {
                         name = dependentModel.getStrName();
                        if (name.length() > 8)
                            name = name.substring(0, 6) + "..";
                        strDependent.add(name);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayAdapter<String> dependentAdapter = new ArrayAdapter<>(CheckInCareActivity.this,
                    android.R.layout.simple_spinner_item, strDependent);

            dependentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if (dependentspinner != null) {
                dependentspinner.setAdapter(dependentAdapter);
            }

            if (dependentspinner != null) {
                dependentspinner.setOnItemSelectedListener(this);
            }
        }

        /////////////////////////////////end

        mainlinearlayout = (LinearLayout) findViewById(R.id.mainlinearlayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        permissionHelper = PermissionHelper.getInstance(this);

        items = new String[4];

        hallimageModels.clear();
        kitchenimageModels.clear();
        washroomimageModels.clear();
        bedroomimageModels.clear();

        hallbitmaps.clear();
        kitchenbitmaps.clear();
        washroombitmaps.clear();
        bedroombitmaps.clear();

        hallImageUploadCount = 0;
        kitchenImageUploadCount = 0;
        washroomImageUploadCount = 0;
        bedroomImageUploadCount = 0;

        Hall_IMAGE_COUNT = 0;
        Kitchen_IMAGE_COUNT = 0;
        Washroom_IMAGE_COUNT = 0;
        Bedroom_IMAGE_COUNT = 0;

        storageService = new StorageService(CheckInCareActivity.this);

        layouthall = (LinearLayout) findViewById(R.id.linear_hall);
        layoutkitchen = (LinearLayout) findViewById(R.id.linear_kitchen);
        layoutwashroom = (LinearLayout) findViewById(R.id.linear_washroom);
        layoutbedroom = (LinearLayout) findViewById(R.id.linear_bedroom);


        utils = new Utils(CheckInCareActivity.this);

        datetxt = (TextView) findViewById(R.id.datetxt);

        LinearLayout layoutDate = (LinearLayout) findViewById(R.id.linearDate);
        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);

        if (layoutDate != null) {
            layoutDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isClicked = 4;
                    datetxt.setError(null);
                    new SlideDateTimePicker.Builder(getSupportFragmentManager())
                            .setListener(listener1)
                            .setInitialDate(new Date())
                            .build()
                            .show();
                }
            });
        }

        /*checkincarename = (EditText) findViewById(R.id.checkincarename);
        checkincarename.requestFocus();
        checkincarename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(view);
                isClicked = 4;
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        });*/

        electrocheck = (CheckBox) findViewById(R.id.electrocheck);
        homecheck = (CheckBox) findViewById(R.id.homecheck);
        autocheck = (CheckBox) findViewById(R.id.autocheck);
        kitchenequipcheck = (CheckBox) findViewById(R.id.kitchenequipcheck);
        grocerycheck = (CheckBox) findViewById(R.id.grocerycheck);
        domesticcheck = (CheckBox) findViewById(R.id.domesticcheck);
        drivercheck = (CheckBox) findViewById(R.id.drivercheck);

         client = (ImageView) findViewById(R.id.clientimg);
         pick_date = (ImageView) findViewById(R.id.pick_date);
         pick_date2 = (ImageView) findViewById(R.id.pick_date2);
         pick_date3 = (ImageView) findViewById(R.id.pick_date3);
         pick_date4 = (ImageView) findViewById(R.id.pick_date4);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        Spinner spinner3 = (Spinner) findViewById(R.id.spinner3);

        buttonHallAdd = (Button) findViewById(R.id.buttonHallAdd);
        buttonKitchenAdd = (Button) findViewById(R.id.buttonKitchenAdd);
        buttonWashroomAdd = (Button) findViewById(R.id.buttonWashroomAdd);
        buttonBedroomAdd = (Button) findViewById(R.id.buttonBedroomAdd);

        loadingPanelhall = (RelativeLayout) findViewById(R.id.loadingPanelhall);
        loadingPanelkitchen = (RelativeLayout) findViewById(R.id.loadingPanelkitchen);
        loadingPanelwash = (RelativeLayout) findViewById(R.id.loadingPanelwash);
        loadingPanelbed = (RelativeLayout) findViewById(R.id.loadingPanelbed);

        uploadhallbtn =(Button)findViewById(R.id.uploadhallbtn);
        uploadkitchenbtn =(Button)findViewById(R.id.uploadkitchenbtn);
        uploadwashroombtn =(Button)findViewById(R.id.uploadwashroombtn);
        uploadbedroombtn =(Button)findViewById(R.id.uploadbedroombtn);

        txtwater = (TextView) findViewById(R.id.water_propertytxt);
        txtgas = (TextView) findViewById(R.id.gastxt);
        txtelectricity = (TextView) findViewById(R.id.electricitytxt);
        txttelephone = (TextView) findViewById(R.id.telephonetxt);
        TextView clientnametxt = (TextView) findViewById(R.id.clientnametxt);

        utilitystatus = (TextView) findViewById(R.id.utilitystatus);
        equipmentstatus = (TextView) findViewById(R.id.equipmentstatus);
        waterstatus = (TextView) findViewById(R.id.waterstatus);
        gasstatus = (TextView) findViewById(R.id.gasstatus);
        electricitystatus = (TextView) findViewById(R.id.electricitystatus);
        telephonestatus = (TextView) findViewById(R.id.telephonestatus);
        kitchenequipmentstatus = (TextView) findViewById(R.id.kitchenequipmentstatus);
        grocerystatus = (TextView) findViewById(R.id.grocerystatus);
        domestichelpstatus = (TextView) findViewById(R.id.domestichelpstatus);
        uploadmediastatus = (TextView) findViewById(R.id.uploadmediastatus);
        hallstatus = (TextView) findViewById(R.id.hallstatus);
        kitchenstatus = (TextView) findViewById(R.id.kitchenstatus);
        washroomstatus = (TextView) findViewById(R.id.washroomstatus);
        bedroomstatus = (TextView) findViewById(R.id.bedroomstatus);
        homeessentialstatus = (TextView) findViewById(R.id.homeessentialstatus);

        electronic = (EditText) findViewById(R.id.electronics);
        homeapplience = (EditText) findViewById(R.id.homeapplience);
        automobile = (EditText) findViewById(R.id.automobile);
        maidservices = (EditText) findViewById(R.id.maidservices);
        kitchen_equipments = (EditText) findViewById(R.id.kitchen_equipments);
        grocery = (EditText) findViewById(R.id.grocery);
        mediacomment = (EditText) findViewById(R.id.mediacomment);
        driveredt = (EditText) findViewById(R.id.driveredt);


        if (Config.customerModel != null) {
            strClientName = Config.customerModel.getStrName();
            //strImageName = Config.customerModel.getStrCustomerID();
            strImageName = Config.customerModel.getStrImgUrl();
            strCustomerEmail = Config.customerModel.getStrEmail();
        }

        if (clientnametxt != null) {
            clientnametxt.setText(strClientName);
        }

        if (client != null) {
           /* Glide.with(CheckInCareActivity.this)
                    .load(strImageName)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(CheckInCareActivity.this))
                    .placeholder(R.drawable.person_icon)
                    .crossFade()
                    .into(client);*/

            Utils.loadGlide(CheckInCareActivity.this, strImageName, client, progressBar);
        }

        if (electrocheck.isChecked() && homecheck.isChecked() && autocheck.isChecked()) {

            equipmentstatus.setVisibility(View.VISIBLE);
            equipmentstatus.setText(getString(R.string.done));
            equipmentstatus.setTextColor(Color.BLUE);
        } else {
            equipmentstatus.setVisibility(View.VISIBLE);
            equipmentstatus.setText(getString(R.string.pending));
            equipmentstatus.setTextColor(Color.RED);
        }

        if (kitchenequipcheck.isChecked()) {
            kitchenequipmentstatus.setVisibility(View.VISIBLE);
            kitchenequipmentstatus.setText(getString(R.string.done));
            kitchenequipmentstatus.setTextColor(Color.BLUE);
        } else {
            kitchenequipmentstatus.setVisibility(View.VISIBLE);
            kitchenequipmentstatus.setText(getString(R.string.pending));
            kitchenequipmentstatus.setTextColor(Color.RED);
        }

        if (grocerycheck.isChecked()) {

            grocerystatus.setVisibility(View.VISIBLE);
            grocerystatus.setText(getString(R.string.done));
            grocerystatus.setTextColor(Color.BLUE);
        } else {
            grocerystatus.setVisibility(View.VISIBLE);
            grocerystatus.setText(getString(R.string.pending));
            grocerystatus.setTextColor(Color.RED);
        }

        if (domesticcheck.isChecked()&& drivercheck.isChecked()) {

            domestichelpstatus.setVisibility(View.VISIBLE);
            domestichelpstatus.setText(getString(R.string.done));
            domestichelpstatus.setTextColor(Color.BLUE);
        } else {
            domestichelpstatus.setVisibility(View.VISIBLE);
            domestichelpstatus.setText(getString(R.string.pending));
            domestichelpstatus.setTextColor(Color.RED);
        }

        if (layouthall != null) {
            hallstatus.setVisibility(View.VISIBLE);
            hallstatus.setText(getString(R.string.pending));
            hallstatus.setTextColor(Color.RED);
        } else {
            hallstatus.setVisibility(View.VISIBLE);
            hallstatus.setText(getString(R.string.done));
            hallstatus.setTextColor(Color.WHITE);
        }
        if (layoutkitchen != null) {
            kitchenstatus.setVisibility(View.VISIBLE);
            kitchenstatus.setText(getString(R.string.pending));
            kitchenstatus.setTextColor(Color.RED);
        } else {
            kitchenstatus.setVisibility(View.VISIBLE);
            kitchenstatus.setText(getString(R.string.done));
            kitchenstatus.setTextColor(Color.WHITE);
        }
        if (layoutwashroom != null) {
            washroomstatus.setVisibility(View.VISIBLE);
            washroomstatus.setText(getString(R.string.pending));
            washroomstatus.setTextColor(Color.RED);
        } else {
            washroomstatus.setVisibility(View.VISIBLE);
            washroomstatus.setText(getString(R.string.done));
            washroomstatus.setTextColor(Color.WHITE);
        }
        if (layoutbedroom != null) {
            bedroomstatus.setVisibility(View.VISIBLE);
            bedroomstatus.setText(getString(R.string.pending));
            bedroomstatus.setTextColor(Color.RED);
        } else {
            bedroomstatus.setVisibility(View.VISIBLE);
            bedroomstatus.setText(getString(R.string.done));
            bedroomstatus.setTextColor(Color.WHITE);
        }

        if (hallstatus.getText().equals(getString(R.string.done))
                && kitchenstatus.getText().equals(getString(R.string.done))
                && washroomstatus.getText().equals(getString(R.string.done))
                && bedroomstatus.getText().equals(getString(R.string.done))) {

            uploadmediastatus.setVisibility(View.VISIBLE);
            uploadmediastatus.setText(getString(R.string.done));
            uploadmediastatus.setTextColor(Color.BLUE);
        } else {
            uploadmediastatus.setVisibility(View.VISIBLE);
            uploadmediastatus.setText(getString(R.string.pending));
            uploadmediastatus.setTextColor(Color.RED);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(CheckInCareActivity.this,
                android.R.layout.simple_spinner_item, option);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinner != null) {
            spinner.setAdapter(adapter);
        }
        if (spinner1 != null) {
            spinner1.setAdapter(adapter);
        }
        if (spinner2 != null) {
            spinner2.setAdapter(adapter);
        }
        if (spinner3 != null) {
            spinner3.setAdapter(adapter);
        }

        if (spinner != null) {
            spinner.setOnItemSelectedListener(this);
        }
        if (spinner1 != null) {
            spinner1.setOnItemSelectedListener(this);
        }
        if (spinner2 != null) {
            spinner2.setOnItemSelectedListener(this);
        }
        if (spinner3 != null) {
            spinner3.setOnItemSelectedListener(this);
        }


        Button backImage = (Button) findViewById(R.id.buttonBack);
        if (backImage != null) {
            backImage.setVisibility(View.VISIBLE);
            backImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hallimageModels.clear();
                    kitchenimageModels.clear();
                    washroomimageModels.clear();
                    bedroomimageModels.clear();

                    hallbitmaps.clear();
                    kitchenbitmaps.clear();
                    washroombitmaps.clear();
                    bedroombitmaps.clear();

                    bViewLoaded =false;
                    editcheckincare=false;

                    goBack();
                }
            });
        }

        if (pick_date != null) {
            pick_date.setOnClickListener(this);
        }
        if (pick_date2 != null) {
            pick_date2.setOnClickListener(this);
        }
        if (pick_date3 != null) {
            pick_date3.setOnClickListener(this);
        }
        if (pick_date4 != null) {
            pick_date4.setOnClickListener(this);
        }

        electrocheck.setOnClickListener(this);
        homecheck.setOnClickListener(this);
        autocheck.setOnClickListener(this);
        kitchenequipcheck.setOnClickListener(this);
        grocerycheck.setOnClickListener(this);
        domesticcheck.setOnClickListener(this);
        drivercheck.setOnClickListener(this);

        buttonHallAdd.setOnClickListener(this);
        buttonKitchenAdd.setOnClickListener(this);
        buttonWashroomAdd.setOnClickListener(this);
        buttonBedroomAdd.setOnClickListener(this);

        uploadhallbtn.setOnClickListener(this);
        uploadkitchenbtn.setOnClickListener(this);
        uploadwashroombtn.setOnClickListener(this);
        uploadbedroombtn.setOnClickListener(this);

        ////////////////////////////////editcheckincare

        try {
            bundle = getIntent().getExtras();

            if (bundle != null) {
                editcheckincare = bundle.getBoolean("editcheckincare");
            } else {
                editcheckincare = false;

            }
            if (editcheckincare && Config.checkInCareModel != null) {

                uploadhallbtn.setVisibility(View.GONE);
                uploadkitchenbtn.setVisibility(View.GONE);
                uploadwashroombtn.setVisibility(View.GONE);
                uploadbedroombtn.setVisibility(View.GONE);

                layoutDate.setEnabled(false);
                dependentspinner.setVisibility(View.GONE);
                dependentname.setVisibility(View.VISIBLE);

                String topdate = Config.checkInCareModel.getStrCurrentDate();
                String editcomment = Config.checkInCareModel.getStrMediaComment();

                //checkincarename.setText(Config.checkInCareModel.getStrName());
                datetxt.setText(topdate);
                strSelectedDate = Config.checkInCareModel.getStrCreatedActualDate();
                //Utils.log(topdate, " DATE ");
                mediacomment.setText(editcomment);


                /*if(!checkincarename.getText().toString().equals("")){
                    checkincarename.setEnabled(false);
                }else{
                    checkincarename.setEnabled(true);
                }*/

                dependentId = Config.checkInCareModel.getStrDependentID();

                ///////////////////////////////////////////////////////fetch depedent
                Cursor cursor1 = CareGiver.getDbCon().fetch(
                        DbHelper.strTableNameCollection, new String[]{DbHelper.COLUMN_DOCUMENT},
                        DbHelper.COLUMN_COLLECTION_NAME + "=? and " + DbHelper.COLUMN_OBJECT_ID
                                + "=? and " + DbHelper.COLUMN_PROVIDER_ID + "=?",
                        new String[]{Config.collectionDependent,
                                Config.checkInCareModel.getStrDependentID(),
                                Config.providerModel.getStrProviderId()
                        },
                        null, "0,1", true, null, null
                );

                JSONObject jsonObject = null;

                if (cursor1.getCount() > 0) {
                    cursor1.moveToFirst();
                    try {
                        if (cursor1.getString(0) != null
                                && !cursor1.getString(0).equalsIgnoreCase("")) {
                            jsonObject = new JSONObject(cursor1.getString(0));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                CareGiver.getDbCon().closeCursor(cursor1);


                try {
                    if (jsonObject != null) {

                        if (jsonObject.getString("dependent_name") != null
                                && !jsonObject.getString("dependent_name").equalsIgnoreCase("")) {
                            strDependentName = jsonObject.optString("dependent_name");
                        }

                    } else {
                        isAccessible = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (dependentname != null) {
                    String name=strDependentName;
                    if (name.length() > 8)
                        name = name.substring(0, 6) + "..";

                    dependentname.setText(name);
                    dependentname.setEnabled(false);


                }
               ///////////////////////////////////////////////////////////////////////////////

                ArrayList<CheckInCareActivityModel> activity = Config.checkInCareModel.getCheckInCareActivityModels();

                if (activity != null) {
                    for (int i = 0; i < activity.size(); i++) {
                        String subActivityName;
                        String status;
                        String checkboxstatus;
                        ArrayList<SubActivityModel> subActivityModels;
                        if (activity.get(i).getStrActivityName().
                                equalsIgnoreCase("home_essentials")) {
                            subActivityModels = activity.get(i).getSubActivityModels();
                            for (int j = 0; j < subActivityModels.size(); j++) {
                                subActivityName = subActivityModels.get(j).getStrSubActivityName();
                                status = subActivityModels.get(j).getStrStatus();
                                String dueStatus = subActivityModels.get(j).getStrDueStatus();
                                String dueDate = subActivityModels.get(j).getStrDueDate();
                                String utilityName = subActivityModels.get(j).getStrUtilityName();
                                checkboxstatus = subActivityModels.get(j).getStrCheckboxStatus();


                                if (subActivityName.equalsIgnoreCase("utility_bills")){

                                    if (utilityName.equalsIgnoreCase("water ")) {
                                        if(dueStatus.equals("N")) {
                                            spinner.setSelection(0);
                                        }else{
                                            spinner.setSelection(1);
                                        }
                                        txtwater.setText(dueDate);
                                        waterstatus.setText(status);

                                    }
                                    if (utilityName.equalsIgnoreCase("gas")) {
                                        if(dueStatus.equals("N")) {
                                            spinner1.setSelection(0);
                                        }else{
                                            spinner1.setSelection(1);
                                        }
                                        txtgas.setText(dueDate);
                                        gasstatus.setText(status);

                                    }
                                    if (utilityName.equalsIgnoreCase("electricity")) {
                                        if(dueStatus.equals("N")) {
                                            spinner2.setSelection(0);
                                        }else{
                                            spinner2.setSelection(1);
                                        }
                                        txtelectricity.setText(dueDate);
                                        electricitystatus.setText(status);

                                    }
                                    if (utilityName.equalsIgnoreCase("telephone")) {
                                        if(dueStatus.equals("N")) {
                                            if (spinner3 != null) {
                                                spinner3.setSelection(0);
                                            }
                                        }else{
                                            if (spinner3 != null) {
                                                spinner3.setSelection(1);
                                            }
                                        }
                                        txttelephone.setText(dueDate);
                                        telephonestatus.setText(status);

                                    }
                                }
                                if (subActivityName.equalsIgnoreCase("kitchen_equipments")){
                                    kitchen_equipments.setText(status);

                                    if(checkboxstatus.equals("true")){
                                        kitchenequipcheck.setChecked(true);
                                    }else {
                                        kitchenequipcheck.setChecked(false);
                                    }
                                    if (kitchenequipcheck.isChecked()) {
                                        kitchenequipmentstatus.setVisibility(View.VISIBLE);
                                        kitchenequipmentstatus.setText(getString(R.string.done));
                                        kitchenequipmentstatus.setTextColor(Color.BLUE);
                                    } else {
                                        kitchenequipmentstatus.setVisibility(View.VISIBLE);
                                        kitchenequipmentstatus.setText(getString(R.string.pending));
                                        kitchenequipmentstatus.setTextColor(Color.RED);
                                    }
                                }
                                if (subActivityName.equalsIgnoreCase("grocery")){
                                    grocery.setText(status);
                                    if(checkboxstatus.equals("true")){
                                        grocerycheck.setChecked(true);
                                    }else {
                                        grocerycheck.setChecked(false);
                                    }
                                    if (grocerycheck.isChecked()) {
                                        grocerystatus.setVisibility(View.VISIBLE);
                                        grocerystatus.setText(getString(R.string.done));
                                        grocerystatus.setTextColor(Color.BLUE);
                                    } else {
                                        grocerystatus.setVisibility(View.VISIBLE);
                                        grocerystatus.setText(getString(R.string.pending));
                                        grocerystatus.setTextColor(Color.RED);
                                    }
                                }

                            }
                        } else if (activity.get(i).getStrActivityName().
                                equalsIgnoreCase("domestic_help_status")) {
                            subActivityModels = activity.get(i).getSubActivityModels();
                            for (int j = 0; j < subActivityModels.size(); j++) {
                                subActivityName = subActivityModels.get(j).getStrSubActivityName();
                                status = subActivityModels.get(j).getStrStatus();
                                checkboxstatus = subActivityModels.get(j).getStrCheckboxStatus();

                                if (subActivityName.equalsIgnoreCase("maid_services")) {
                                    maidservices.setText(status);
                                    if (checkboxstatus.equals("true")) {
                                        domesticcheck.setChecked(true);
                                    } else {
                                        domesticcheck.setChecked(false);
                                    }
                                }
                                if (subActivityName.equalsIgnoreCase("driver_status")){
                                    driveredt.setText(status);
                                    if(checkboxstatus.equals("true")){
                                        drivercheck.setChecked(true);
                                    }else {
                                        drivercheck.setChecked(false);
                                    }
                                }

                                if (domesticcheck.isChecked()&& drivercheck.isChecked()) {

                                    domestichelpstatus.setVisibility(View.VISIBLE);
                                    domestichelpstatus.setText(getString(R.string.done));
                                    domestichelpstatus.setTextColor(Color.BLUE);
                                } else {
                                    domestichelpstatus.setVisibility(View.VISIBLE);
                                    domestichelpstatus.setText(getString(R.string.pending));
                                    domestichelpstatus.setTextColor(Color.RED);
                                }

                            }
                        } else if (activity.get(i).getStrActivityName().
                                equalsIgnoreCase("equipment_working_status")) {
                            subActivityModels = activity.get(i).getSubActivityModels();
                            for (int j = 0; j < subActivityModels.size(); j++) {
                                subActivityName = subActivityModels.get(j).getStrSubActivityName();
                                status = subActivityModels.get(j).getStrStatus();
                                checkboxstatus = subActivityModels.get(j).getStrCheckboxStatus();

                                if (subActivityName.equalsIgnoreCase("electronic")){
                                    electronic.setText(status);
                                    if(checkboxstatus.equals("true")){
                                        electrocheck.setChecked(true);
                                    }else {
                                        electrocheck.setChecked(false);
                                    }
                                }
                                if (subActivityName.equalsIgnoreCase("home_appliances")){
                                    homeapplience.setText(status);
                                    if(checkboxstatus.equals("true")){
                                        homecheck.setChecked(true);
                                    }else {
                                        homecheck.setChecked(false);
                                    }
                                }
                                if (subActivityName.equalsIgnoreCase("automobile")){
                                    automobile.setText(status);
                                    if(checkboxstatus.equals("true")){
                                        autocheck.setChecked(true);
                                    }else {
                                        autocheck.setChecked(false);
                                    }
                                }
                                if (electrocheck.isChecked() && homecheck.isChecked()
                                        && autocheck.isChecked()) {

                                    equipmentstatus.setVisibility(View.VISIBLE);
                                    equipmentstatus.setText(getString(R.string.done));
                                    equipmentstatus.setTextColor(Color.BLUE);
                                } else {
                                    equipmentstatus.setVisibility(View.VISIBLE);
                                    equipmentstatus.setText(getString(R.string.pending));
                                    equipmentstatus.setTextColor(Color.RED);
                                }
                            }
                        }
                    }
                }

                ArrayList<PictureModel> picture = Config.checkInCareModel.
                        getPictureModels();
                if (picture != null) {

                    for (int k = 0; k < picture.size(); k++) {

                        if (picture.get(k).getStrRoomName().equalsIgnoreCase("hall")) {
                            hallimageModels = picture.get(k).getImageModels();

                        }
                        if (picture.get(k).getStrRoomName().equalsIgnoreCase("kitchen")) {
                            kitchenimageModels = picture.get(k).getImageModels();

                        }
                        if (picture.get(k).getStrRoomName().equalsIgnoreCase("washroom")) {
                            washroomimageModels = picture.get(k).getImageModels();

                        }
                        if (picture.get(k).getStrRoomName().equalsIgnoreCase("bedroom")) {
                            bedroomimageModels = picture.get(k).getImageModels();

                        }

                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /////////////////////////////////////////end

        Button submit = (Button) findViewById(R.id.btn_close);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isClick) {
                    isClick = true;

                    boolean cancel = false;

                    datetxt.setError(null);
                    equipmentstatus.setError(null);
                    kitchenequipmentstatus.setError(null);
                    grocerystatus.setError(null);
                    domestichelpstatus.setError(null);
                    // uploadmediastatus.setError(null);
                    //mediacomment.setError(null);


                    valdate = datetxt.getText().toString().trim();
                    valkitchenequi = kitchenequipmentstatus.getText().toString().trim();
                    valequipment = equipmentstatus.getText().toString().trim();
                    valgrocery = grocerystatus.getText().toString().trim();
                    valdomestic = domestichelpstatus.getText().toString().trim();
                    // valphoto = uploadmediastatus.getText().toString().trim();
                    //valmediacomment = mediacomment.getText().toString().trim();



                    if (TextUtils.isEmpty(valdate)) {
                        datetxt.setError(getString(R.string.select_date));
                        utils.toast(2, 2, getString(R.string.select_date));
                        focusView = datetxt;
                        cancel = true;
                    }
                   /* if (TextUtils.isEmpty(valmediacomment)) {
                        mediacomment.setError(getString(R.string.error_field_required));
                        focusView = mediacomment;
                        cancel = true;
                    }*/

                    boolean editcheck=true;
                    if (!kitchenequipcheck.isChecked()) {
                        if (kitchen_equipments.getText().toString().equals("")) {
                            editcheck = false;
                            focusView = kitchen_equipments;
                        }
                    }
                    if (!grocerycheck.isChecked()) {
                        if (grocery.getText().toString().equals("")) {
                            editcheck = false;
                            focusView = grocery;
                        }
                    }
                    if (!electrocheck.isChecked()) {
                        if (electronic.getText().toString().equals("")) {
                            editcheck = false;
                            focusView = electronic;
                        }
                    }
                    if (!homecheck.isChecked()) {
                        if (homeapplience.getText().toString().equals("")) {
                            editcheck = false;
                            focusView = homeapplience;
                        }
                    }
                    if (!autocheck.isChecked()) {
                        if (automobile.getText().toString().equals("")) {
                            editcheck = false;
                            focusView = automobile;
                        }
                    }
                    if (!domesticcheck.isChecked()) {
                        if (maidservices.getText().toString().equals("")) {
                            editcheck = false;
                            focusView = maidservices;
                        }
                    }
                    if (!drivercheck.isChecked()) {
                        if (driveredt.getText().toString().equals("")) {
                            editcheck = false;
                            focusView = driveredt;
                        }
                    }
                    if(!editcheck) {
                        utils.toast(2, 2, getString(R.string.check_edit));
                        cancel = true;
                    }


                    if (isHallflag==1 && hallimageModels.size() > 0 ) {
                        hallstatus.setError(getString(R.string.upload_hall));
                        utils.toast(2, 2, getString(R.string.upload_hall));
                        focusView = hallstatus;
                        cancel = true;

                    }
                    if (isKitchenflag==1 && kitchenimageModels.size() > 0) {
                        kitchenstatus.setError(getString(R.string.upload_kitchen));
                        utils.toast(2, 2, getString(R.string.upload_kitchen));
                        focusView = kitchenstatus;
                        cancel = true;

                    }
                    if (isWashflag==1 && washroomimageModels.size() > 0 ) {
                        washroomstatus.setError(getString(R.string.upload_wash));
                        utils.toast(2, 2, getString(R.string.upload_wash));
                        focusView = washroomstatus;
                        cancel = true;

                    }
                    if (isBedflag==1 && bedroomimageModels.size() > 0 ) {
                        bedroomstatus.setError(getString(R.string.upload_bed));
                        utils.toast(2, 2, getString(R.string.upload_bed));
                        focusView = bedroomstatus;
                        cancel = true;

                    }

                    boolean bDate=true;

                    if (items[0].equals("N")) {
                        if(txtwater.getText().toString().equals("Due Date")) {
                            bDate=false;
                        }
                    }
                    if (items[1].equals("N")) {
                        if(txtgas.getText().toString().equals("Due Date")) {
                            bDate=false;
                        }
                    }
                    if (items[2].equals("N")) {
                        if(txtelectricity.getText().toString().equals("Due Date")) {
                            bDate=false;
                        }
                    }
                    if (items[3].equals("N")) {
                        if(txttelephone.getText().toString().equals("Due Date")) {
                            bDate=false;
                        }
                    }
                    if(!bDate) {
                        utils.toast(2, 2, getString(R.string.due_date));
                    }


                    if (cancel) {
                        focusView.requestFocus();
                        isClick = false;
                    } else {

                        if (utils.isConnectingToInternet()) {

                            ////////////////////////////
                            boolean bFuture = true;


                            if (bFuture) {

                                if(editcheckincare){
                                    editupdateJson();
                                }else {
                                    updateJson();
                                }

                            } else {
                                isClick = false;
                            }

                        } else {
                            isClick = false;
                        }

                    }
                } else {
                    isClick = false;

                }
            }
        });
        Button update = (Button) findViewById(R.id.btn_submit);
       /* update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isClick) {
                    isClick = true;

                    boolean cancel = false;

                    datetxt.setError(null);

                    valdate = datetxt.getText().toString().trim();

                    if (TextUtils.isEmpty(valdate)) {
                        datetxt.setError(getString(R.string.select_date));
                        utils.toast(2, 2, getString(R.string.select_date));
                        focusView = datetxt;
                        cancel = true;
                    }



                    if (cancel) {
                        focusView.requestFocus();
                        isClick = false;
                    } else {

                        if (utils.isConnectingToInternet()) {

                            ////////////////////////////
                            boolean bFuture = true;


                            if (bFuture) {

                                if(editcheckincare){
                                    editupdateJson();
                                }else {
                                    updateJson();
                                }

                            } else {
                                isClick = false;
                            }

                        } else {
                            isClick = false;
                        }

                    }
                } else {
                    isClick = false;

                }
            }
        });*/

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.pick_date:
                if (items[0].equals("N")) {

                    isClicked = 0;
                    new SlideDateTimePicker.Builder(getSupportFragmentManager())
                            .setListener(listener)
                            .setInitialDate(new Date())
                            .build()
                            .show();
                }
                break;

            case R.id.pick_date2:
                if (items[1].equals("N")) {
                    isClicked = 1;
                    new SlideDateTimePicker.Builder(getSupportFragmentManager())
                            .setListener(listener)
                            .setInitialDate(new Date())
                            .build()
                            .show();
                }
                break;

            case R.id.pick_date3:
                if (items[2].equals("N")) {
                    isClicked = 2;
                    new SlideDateTimePicker.Builder(getSupportFragmentManager())
                            .setListener(listener)
                            .setInitialDate(new Date())
                            .build()
                            .show();
                }
                break;


            case R.id.pick_date4:
                if (items[3].equals("N")) {
                    isClicked = 3;
                    new SlideDateTimePicker.Builder(getSupportFragmentManager())
                            .setListener(listener)
                            .setInitialDate(new Date())
                            .build()
                            .show();
                }
                break;
            case R.id.buttonHallAdd:
                if(isHallflag==2){
                    utils.toast(2, 2, getString(R.string.upload_hall_success));

                }else {
                    utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                            + ".jpeg", null, CheckInCareActivity.this, false);
                }

                break;
            case R.id.buttonKitchenAdd:
                if(isKitchenflag==2){
                    utils.toast(2, 2, getString(R.string.upload_kitchen_success));

                }else {
                    utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                            + ".jpeg", null, CheckInCareActivity.this, false);
                }

                break;
            case R.id.buttonWashroomAdd:
                if(isWashflag==2){
                    utils.toast(2, 2, getString(R.string.upload_wash_success));

                }else {
                    utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                            + ".jpeg", null, CheckInCareActivity.this, false);
                }

                break;
            case R.id.buttonBedroomAdd:
                if(isBedflag==2){
                    utils.toast(2, 2, getString(R.string.upload_bed_success));

                }else {
                    utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                            + ".jpeg", null, CheckInCareActivity.this, false);
                }

                break;

            case R.id.uploadhallbtn:
                hallstatus.setError(null);

                if(isHallflag==2){
                    utils.toast(2, 2, getString(R.string.upload_hall_success));

                }else {
                    uploadHallImage();
                }

                break;
            case R.id.uploadkitchenbtn:
                kitchenstatus.setError(null);
                if(isKitchenflag==2){
                    utils.toast(2, 2, getString(R.string.upload_kitchen_success));

                }else {
                    uploadKitchenImage();
                }

                break;
            case R.id.uploadwashroombtn:
                washroomstatus.setError(null);
                if(isWashflag==2){
                    utils.toast(2, 2, getString(R.string.upload_wash_success));

                }else {
                    uploadWashroomImage();
                }

                break;
            case R.id.uploadbedroombtn:
                bedroomstatus.setError(null);
                if(isBedflag==2){
                    utils.toast(2, 2, getString(R.string.upload_bed_success));

                }else {
                    uploadBedroomImage();
                }

                break;


        }
        if (electrocheck.isChecked() && homecheck.isChecked() && autocheck.isChecked()) {

            equipmentstatus.setError(null);
            equipmentstatus.setVisibility(View.VISIBLE);
            equipmentstatus.setText(getString(R.string.done));
            equipmentstatus.setTextColor(Color.BLUE);
        } else {
            equipmentstatus.setVisibility(View.VISIBLE);
            equipmentstatus.setText(getString(R.string.pending));
            equipmentstatus.setTextColor(Color.RED);
        }
        if (kitchenequipcheck.isChecked()) {
            kitchenequipmentstatus.setError(null);
            kitchenequipmentstatus.setVisibility(View.VISIBLE);
            kitchenequipmentstatus.setText(getString(R.string.done));
            kitchenequipmentstatus.setTextColor(Color.BLUE);
            if (grocerystatus.getText().toString().equals("Done")
                    && waterstatus.getText().toString().equals(getString(R.string.yes))
                    && gasstatus.getText().toString().equals(getString(R.string.yes))
                    && electricitystatus.getText().toString().equals(getString(R.string.yes))
                    && telephonestatus.getText().toString().equals(getString(R.string.yes))) {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText(getString(R.string.done));
                homeessentialstatus.setTextColor(Color.BLUE);
            } else {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText(getString(R.string.pending));
                homeessentialstatus.setTextColor(Color.RED);
            }
        } else {
            kitchenequipmentstatus.setVisibility(View.VISIBLE);
            kitchenequipmentstatus.setText(getString(R.string.pending));
            kitchenequipmentstatus.setTextColor(Color.RED);
            if (grocerystatus.getText().toString().equals("Done")
                    && kitchenequipmentstatus.getText().toString().equals("Done")
                    && waterstatus.getText().toString().equals(getString(R.string.yes))
                    && gasstatus.getText().toString().equals(getString(R.string.yes))
                    && electricitystatus.getText().toString().equals(getString(R.string.yes))
                    && telephonestatus.getText().toString().equals(getString(R.string.yes))) {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText(getString(R.string.done));
                homeessentialstatus.setTextColor(Color.BLUE);
            } else {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText(getString(R.string.pending));
                homeessentialstatus.setTextColor(Color.RED);
            }
        }

        if (grocerycheck.isChecked()) {
            grocerystatus.setError(null);
            grocerystatus.setVisibility(View.VISIBLE);
            grocerystatus.setText(getString(R.string.done));
            grocerystatus.setTextColor(Color.BLUE);
            if (kitchenequipmentstatus.getText().toString().equals("Done")
                    && waterstatus.getText().toString().equals(getString(R.string.yes))
                    && gasstatus.getText().toString().equals(getString(R.string.yes))
                    && electricitystatus.getText().toString().equals(getString(R.string.yes))
                    && telephonestatus.getText().toString().equals(getString(R.string.yes))) {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText(getString(R.string.done));
                homeessentialstatus.setTextColor(Color.BLUE);
            } else {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText(getString(R.string.pending));
                homeessentialstatus.setTextColor(Color.RED);
            }
        } else {
            grocerystatus.setVisibility(View.VISIBLE);
            grocerystatus.setText(getString(R.string.pending));
            grocerystatus.setTextColor(Color.RED);
            if (kitchenequipmentstatus.getText().toString().equals("Done")
                    && grocerystatus.getText().toString().equals("Done")
                    && waterstatus.getText().toString().equals(getString(R.string.yes))
                    && gasstatus.getText().toString().equals(getString(R.string.yes))
                    && electricitystatus.getText().toString().equals(getString(R.string.yes))
                    && telephonestatus.getText().toString().equals(getString(R.string.yes))) {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText(getString(R.string.done));
                homeessentialstatus.setTextColor(Color.BLUE);
            } else {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText(getString(R.string.pending));
                homeessentialstatus.setTextColor(Color.RED);
            }
        }
        if (domesticcheck.isChecked()&& drivercheck.isChecked()) {
            domestichelpstatus.setError(null);
            domestichelpstatus.setVisibility(View.VISIBLE);
            domestichelpstatus.setText(getString(R.string.done));
            domestichelpstatus.setTextColor(Color.BLUE);
        } else {
            domestichelpstatus.setVisibility(View.VISIBLE);
            domestichelpstatus.setText(getString(R.string.pending));
            domestichelpstatus.setTextColor(Color.RED);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        hallimageModels.clear();
        kitchenimageModels.clear();
        washroomimageModels.clear();
        bedroomimageModels.clear();

        hallbitmaps.clear();
        kitchenbitmaps.clear();
        washroombitmaps.clear();
        bedroombitmaps.clear();

        bViewLoaded =false;
        editcheckincare=false;

        goBack();
    }

    private void goBack() {
        Intent intent = new Intent(CheckInCareActivity.this, DashboardActivity.class);
        Config.intSelectedMenu = Config.intClientScreen;
        startActivity(intent);
        finish();
    }

    private void uploadHallImage() {

        loadingPanelhall.setVisibility(View.VISIBLE);

        if (hallImageCount > 0) {

            bLoad = false;

            if (hallImageUploadCount < hallimageModels.size()) {

                final ImageModel mhallImageModel = hallimageModels.get(hallImageUploadCount);

                if (mhallImageModel.ismIsNew()) {

                    UploadService uploadService = new UploadService(this);

                    uploadService.uploadImageCommon(mhallImageModel.getStrImagePath(),
                            mhallImageModel.getStrImageDesc(), mhallImageModel.getStrImageDesc(),
                            Config.providerModel.getStrEmail(), UploadFileType.IMAGE,
                            new App42CallBack() {
                                public void onSuccess(Object response) {
                                    loadingPanelhall.setVisibility(View.GONE);
                                    if (response != null) {

                                        Utils.log(response.toString(), " Hall Images ");

                                        Upload upload = (Upload) response;
                                        ArrayList<Upload.File> fileList = upload.getFileList();

                                        if (fileList.size() > 0) {

                                            Upload.File file = fileList.get(0);

                                            hallimageModels.get(hallImageUploadCount).
                                                    setmIsNew(false);
                                            hallimageModels.get(hallImageUploadCount).
                                                    setStrImageUrl(file.getUrl());

                                            try {
                                                hallImageUploadCount++;
                                                if (hallImageUploadCount >= hallimageModels.size()) {

                                                    utils.toast(2, 2, getString(R.string.upload_images));
                                                    isHallflag = 2;
                                                    Drawable icon= getResources().getDrawable( R.drawable.ok);
                                                    uploadhallbtn.setCompoundDrawablesWithIntrinsicBounds( icon, null, null, null );
                                                    uploadhallbtn.setCompoundDrawablePadding(10);
                                                    uploadhallbtn.setText(getString(R.string.upload_images_sucees));

                                                    for ( int i = 0; i < layouthall.getChildCount();  i++ ){
                                                        View view = layouthall.getChildAt(i);
                                                        view.setEnabled(false); // Or whatever you want to do with the view.
                                                        view.setFocusable(false);
                                                    }

                                                } else {
                                                    uploadHallImage();
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        } else {
                                            hallImageUploadCount++;
                                            if (hallImageUploadCount >= hallimageModels.size()) {

                                                utils.toast(2, 2, getString(R.string.upload_images));

                                            } else {
                                                uploadHallImage();
                                            }
                                        }
                                    } else {
                                        utils.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                }

                                @Override
                                public void onException(Exception e) {
                                    loadingPanelhall.setVisibility(View.GONE);
                                    hallImageUploadCount++;
                                    if (e != null) {
                                        Utils.log(e.getMessage(), " Failure ");

                                        if (hallImageUploadCount >= hallimageModels.size()) {

                                            utils.toast(2, 2, getString(R.string.upload_images));

                                        } else {
                                            uploadHallImage();
                                        }
                                    } else {
                                        utils.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                }
                            });
                } else {
                    hallImageUploadCount++;

                    if (hallImageUploadCount >= hallimageModels.size()) {

                        utils.toast(2, 2, getString(R.string.upload_images));

                    } else {
                        uploadHallImage();
                    }
                }

            } else {
                hallImageUploadCount++;

                if (hallImageUploadCount >= hallimageModels.size()) {

                    utils.toast(2, 2, getString(R.string.upload_images));

                } else {
                    uploadHallImage();
                }
            }

        } else {
            loadingPanelhall.setVisibility(View.GONE);
            utils.toast(2, 2, getString(R.string.select_image));

        }
    }

    private void uploadKitchenImage() {

        loadingPanelkitchen.setVisibility(View.VISIBLE);

        if (kitchenImageCount > 0) {

            bLoad = false;

            if (kitchenImageUploadCount < kitchenimageModels.size()) {

                final ImageModel mkitchenImageModel = kitchenimageModels.
                        get(kitchenImageUploadCount);

                if (mkitchenImageModel.ismIsNew()) {

                    UploadService uploadService = new UploadService(this);

                    uploadService.uploadImageCommon(mkitchenImageModel.getStrImagePath(),
                            mkitchenImageModel.getStrImageDesc(), mkitchenImageModel.
                                    getStrImageDesc(),
                            Config.providerModel.getStrEmail(), UploadFileType.IMAGE,
                            new App42CallBack() {
                                public void onSuccess(Object response) {
                                    loadingPanelkitchen.setVisibility(View.GONE);
                                    if (response != null) {

                                        Utils.log(response.toString(), " Kitchen Images ");

                                        Upload upload = (Upload) response;
                                        ArrayList<Upload.File> fileList = upload.getFileList();

                                        if (fileList.size() > 0) {

                                            Upload.File file = fileList.get(0);

                                            kitchenimageModels.get(kitchenImageUploadCount).
                                                    setmIsNew(false);
                                            kitchenimageModels.get(kitchenImageUploadCount).
                                                    setStrImageUrl(file.getUrl());

                                            try {
                                                kitchenImageUploadCount++;
                                                if (kitchenImageUploadCount >= kitchenimageModels.
                                                        size()) {
                                                    utils.toast(2, 2, getString(R.string.upload_images));
                                                    isKitchenflag=2;
                                                    Drawable icon= getResources().getDrawable( R.drawable.ok);
                                                    uploadkitchenbtn.setCompoundDrawablesWithIntrinsicBounds( icon, null, null, null );
                                                    uploadkitchenbtn.setCompoundDrawablePadding(10);
                                                    uploadkitchenbtn.setText(getString(R.string.upload_images_sucees));
                                                    //buttonKitchenAdd.setEnabled(false);

                                                    for ( int i = 0; i < layoutkitchen.getChildCount();  i++ ){
                                                        View view = layoutkitchen.getChildAt(i);
                                                        view.setEnabled(false); // Or whatever you want to do with the view.
                                                        view.setFocusable(false);
                                                    }

                                                } else {
                                                    uploadKitchenImage();
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        } else {
                                            kitchenImageUploadCount++;
                                            if (kitchenImageUploadCount >= kitchenimageModels.
                                                    size()) {

                                                utils.toast(2, 2, getString(R.string.upload_images));

                                            } else {
                                                uploadKitchenImage();
                                            }
                                        }
                                    } else {
                                        utils.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                }

                                @Override
                                public void onException(Exception e) {
                                    loadingPanelkitchen.setVisibility(View.GONE);
                                    if (e != null) {
                                        Utils.log(e.getMessage(), " Failure ");
                                        kitchenImageUploadCount++;
                                        if (kitchenImageUploadCount >= kitchenimageModels.size()) {

                                            utils.toast(2, 2, getString(R.string.upload_images));

                                        } else {
                                            uploadKitchenImage();
                                        }
                                    } else {
                                        utils.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                }
                            });
                } else {
                    kitchenImageUploadCount++;

                    if (kitchenImageUploadCount >= kitchenimageModels.size()) {

                        utils.toast(2, 2, getString(R.string.upload_images));

                    } else {
                        uploadKitchenImage();
                    }
                }
            } else {
                kitchenImageUploadCount++;

                if (kitchenImageUploadCount >= kitchenimageModels.size()) {

                    utils.toast(2, 2, getString(R.string.upload_images));

                } else {
                    uploadKitchenImage();
                }
            }
        } else {
            loadingPanelkitchen.setVisibility(View.GONE);
            utils.toast(2, 2, getString(R.string.select_image));


        }
    }

    private void uploadWashroomImage() {

        loadingPanelwash.setVisibility(View.VISIBLE);
        if (washroomImageCount > 0) {

            bLoad = false;

            if (washroomImageUploadCount < washroomimageModels.size()) {

                final ImageModel mwashroomImageModel = washroomimageModels.
                        get(washroomImageUploadCount);

                if (mwashroomImageModel.ismIsNew()) {

                    UploadService uploadService = new UploadService(this);

                    uploadService.uploadImageCommon(mwashroomImageModel.getStrImagePath(),
                            mwashroomImageModel.getStrImageDesc(), mwashroomImageModel.
                                    getStrImageDesc(), Config.providerModel.getStrEmail(),
                            UploadFileType.IMAGE, new App42CallBack() {
                                public void onSuccess(Object response) {

                                    loadingPanelwash.setVisibility(View.GONE);
                                    if (response != null) {

                                        Utils.log(response.toString(), " Washroom Images ");

                                        Upload upload = (Upload) response;
                                        ArrayList<Upload.File> fileList = upload.getFileList();

                                        if (fileList.size() > 0) {

                                            Upload.File file = fileList.get(0);

                                            washroomimageModels.get(washroomImageUploadCount).
                                                    setmIsNew(false);
                                            washroomimageModels.get(washroomImageUploadCount).
                                                    setStrImageUrl(file.getUrl());

                                            try {
                                                washroomImageUploadCount++;
                                                if (washroomImageUploadCount
                                                        >= washroomimageModels.size()) {

                                                    utils.toast(2, 2, getString(R.string.upload_images));
                                                    isWashflag = 2;

                                                    Drawable icon= getResources().getDrawable( R.drawable.ok);
                                                    uploadwashroombtn.setCompoundDrawablesWithIntrinsicBounds( icon, null, null, null );
                                                    uploadwashroombtn.setCompoundDrawablePadding(10);
                                                    uploadwashroombtn.setText(getString(R.string.upload_images_sucees));

                                                    for ( int i = 0; i < layoutwashroom.getChildCount();  i++ ){
                                                        View view = layoutwashroom.getChildAt(i);
                                                        view.setEnabled(false); // Or whatever you want to do with the view.
                                                        view.setFocusable(false);
                                                    }

                                                } else {
                                                    uploadWashroomImage();
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        } else {
                                            washroomImageUploadCount++;
                                            if (washroomImageUploadCount >= washroomimageModels.
                                                    size()) {

                                                utils.toast(2, 2, getString(R.string.upload_images));
                                            } else {
                                                uploadWashroomImage();
                                            }
                                        }
                                    } else {
                                        utils.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                }

                                @Override
                                public void onException(Exception e) {

                                    loadingPanelwash.setVisibility(View.GONE);
                                    if (e != null) {
                                        Utils.log(e.getMessage(), " Failure ");
                                        washroomImageUploadCount++;
                                        if (washroomImageUploadCount >= washroomimageModels.
                                                size()) {

                                            utils.toast(2, 2, getString(R.string.upload_images));
                                        } else {
                                            uploadWashroomImage();
                                        }
                                    } else {
                                        utils.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                }
                            });
                } else {
                    washroomImageUploadCount++;

                    if (washroomImageUploadCount >= washroomimageModels.size()) {

                        utils.toast(2, 2, getString(R.string.upload_images));
                    } else {
                        uploadWashroomImage();
                    }
                }
            } else {
                washroomImageUploadCount++;

                if (washroomImageUploadCount >= washroomimageModels.size()) {

                    utils.toast(2, 2, getString(R.string.upload_images));
                } else {
                    uploadWashroomImage();
                }
            }
        } else {
            loadingPanelwash.setVisibility(View.GONE);
            utils.toast(2, 2, getString(R.string.select_image));


        }
    }

    private void uploadBedroomImage() {

        loadingPanelbed.setVisibility(View.VISIBLE);
        if (bedroomImageCount > 0) {

            bLoad = false;

            if (bedroomImageUploadCount < bedroomimageModels.size()) {

                final ImageModel mbedroomImageModel = bedroomimageModels.
                        get(bedroomImageUploadCount);

                if (mbedroomImageModel.ismIsNew()) {

                    UploadService uploadService = new UploadService(this);

                    uploadService.uploadImageCommon(mbedroomImageModel.getStrImagePath(),
                            mbedroomImageModel.getStrImageDesc(), mbedroomImageModel.
                                    getStrImageDesc(),
                            Config.providerModel.getStrEmail(), UploadFileType.IMAGE,
                            new App42CallBack() {
                                public void onSuccess(Object response) {
                                    loadingPanelbed.setVisibility(View.GONE);
                                    if (response != null) {

                                        Utils.log(response.toString(), " Bedroom Images ");

                                        Upload upload = (Upload) response;
                                        ArrayList<Upload.File> fileList = upload.getFileList();

                                        if (fileList.size() > 0) {

                                            Upload.File file = fileList.get(0);

                                            bedroomimageModels.get(bedroomImageUploadCount).
                                                    setmIsNew(false);
                                            bedroomimageModels.get(bedroomImageUploadCount).
                                                    setStrImageUrl(file.getUrl());

                                            try {
                                                bedroomImageUploadCount++;
                                                if (bedroomImageUploadCount >= bedroomimageModels.
                                                        size()) {

                                                    utils.toast(2, 2, getString(R.string.upload_images));
                                                    isBedflag=2;

                                                    Drawable icon= getResources().getDrawable( R.drawable.ok);
                                                    uploadbedroombtn.setCompoundDrawablesWithIntrinsicBounds( icon, null, null, null );
                                                    uploadbedroombtn.setCompoundDrawablePadding(10);
                                                    uploadbedroombtn.setText(getString(R.string.upload_images_sucees));


                                                    for ( int i = 0; i < layoutbedroom.getChildCount();  i++ ){
                                                        View view = layoutbedroom.getChildAt(i);
                                                        view.setEnabled(false); // Or whatever you want to do with the view.
                                                        view.setFocusable(false);
                                                    }

                                                } else {
                                                    uploadBedroomImage();
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        } else {
                                            bedroomImageUploadCount++;
                                            if (bedroomImageUploadCount
                                                    >= bedroomimageModels.size()) {

                                                utils.toast(2, 2, getString(R.string.upload_images));
                                            } else {
                                                uploadBedroomImage();
                                            }
                                        }
                                    } else {
                                        utils.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                }

                                @Override
                                public void onException(Exception e) {
                                    loadingPanelbed.setVisibility(View.GONE);
                                    if (e != null) {
                                        Utils.log(e.getMessage(), " Failure ");
                                        bedroomImageUploadCount++;
                                        if (bedroomImageUploadCount >= bedroomimageModels.size()) {

                                            utils.toast(2, 2, getString(R.string.upload_images));
                                        } else {
                                            uploadBedroomImage();
                                        }
                                    } else {
                                        utils.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                }
                            });
                } else {
                    bedroomImageUploadCount++;

                    if (bedroomImageUploadCount >= bedroomimageModels.size()) {

                        utils.toast(2, 2, getString(R.string.upload_images));
                    } else {
                        uploadBedroomImage();
                    }
                }
            } else {

                uploadBedroomImage();
            }
        } else {
            loadingPanelbed.setVisibility(View.GONE);
            utils.toast(2, 2, getString(R.string.select_image));

        }
    }

    private void updateJson() {

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        if (utils.isConnectingToInternet()) {

            JSONObject jsonObjectCheckinCare = null;

            try {
                jsonObjectCheckinCare = new JSONObject();

                Date mydate = new Date();
                String strCreateDate = Utils.readFormat.format(mydate);
                String strMonth = Utils.writeFormatDateMonth.format(mydate);
                String strYear = Utils.writeFormatDateYear.format(mydate);

                jsonObjectCheckinCare.put("created_date", strCreateDate);
                jsonObjectCheckinCare.put("dependent_id", dependentId);
                jsonObjectCheckinCare.put("provider_id", Config.providerModel.getStrProviderId());
                jsonObjectCheckinCare.put("updated_date", "");
                jsonObjectCheckinCare.put("current_date", datetxt.getText().toString());
                jsonObjectCheckinCare.put("status", "New");
                jsonObjectCheckinCare.put("created_date_actual", strSelectedDate);
                jsonObjectCheckinCare.put("month", strMonth);
                jsonObjectCheckinCare.put("year", strYear);
                jsonObjectCheckinCare.put("customer_id", Config.customerModel.getStrCustomerID());
                jsonObjectCheckinCare.put("media_comment", mediacomment.getText().toString());
                jsonObjectCheckinCare.put("check_in_care_name",strcheckincare);

                JSONArray jsonArrayPicture = new JSONArray();

                ///////////////////////Hallimage
                JSONObject jsonObjectHallPicture = new JSONObject();

                jsonObjectHallPicture.put("status", "status");
                jsonObjectHallPicture.put("room_name", "hall");

                JSONArray jsonArrayHallPictureDetails = new JSONArray();


                if (hallimageModels.size() > 0) {

                    for (ImageModel hallImageModel : hallimageModels) {

                        JSONObject jsonObjectHallImages = new JSONObject();

                        jsonObjectHallImages.put("image_url", hallImageModel.getStrImageUrl());
                        jsonObjectHallImages.put("description", hallImageModel.getStrImageDesc());
                        jsonObjectHallImages.put("date_time", hallImageModel.getStrImageTime());

                        jsonArrayHallPictureDetails.put(jsonObjectHallImages);
                    }
                } else {

                    jsonArrayHallPictureDetails.put("{\"0\":\"empty\"}");
                }

                jsonObjectHallPicture.put("pictures_details", jsonArrayHallPictureDetails);
                jsonArrayPicture.put(jsonObjectHallPicture);
                ////////////////////////Hallimage

                //////////////////////kitchenimage
                JSONObject jsonObjectKitchenPicture = new JSONObject();

                jsonObjectKitchenPicture.put("status", "status");
                jsonObjectKitchenPicture.put("room_name", "kitchen");


                JSONArray jsonArrayKitchenPictureDetails = new JSONArray();


                if (kitchenimageModels.size() > 0) {

                    for (ImageModel kitchenImageModel : kitchenimageModels) {

                        JSONObject jsonObjectKitchenImages = new JSONObject();

                        jsonObjectKitchenImages.put("image_url", kitchenImageModel.
                                getStrImageUrl());
                        jsonObjectKitchenImages.put("description", kitchenImageModel.
                                getStrImageDesc());
                        jsonObjectKitchenImages.put("date_time", kitchenImageModel.
                                getStrImageTime());

                        jsonArrayKitchenPictureDetails.put(jsonObjectKitchenImages);
                    }
                } else {

                    jsonArrayKitchenPictureDetails.put("{\"0\":\"empty\"}");
                }
                jsonObjectKitchenPicture.put("pictures_details", jsonArrayKitchenPictureDetails);
                jsonArrayPicture.put(jsonObjectKitchenPicture);
                ////////////////////////kitchenimage


                //////////////////////washroomimage
                JSONObject jsonObjectWashroomPicture = new JSONObject();

                jsonObjectWashroomPicture.put("status", "status");
                jsonObjectWashroomPicture.put("room_name", "washroom");

                JSONArray jsonArrayWashroomPictureDetails = new JSONArray();


                if (washroomimageModels.size() > 0) {

                    for (ImageModel washroomImageModel : washroomimageModels) {

                        JSONObject jsonObjectWashroomImages = new JSONObject();

                        jsonObjectWashroomImages.put("image_url", washroomImageModel.
                                getStrImageUrl());
                        jsonObjectWashroomImages.put("description", washroomImageModel.
                                getStrImageDesc());
                        jsonObjectWashroomImages.put("date_time", washroomImageModel.
                                getStrImageTime());

                        jsonArrayWashroomPictureDetails.put(jsonObjectWashroomImages);
                    }
                } else {

                    jsonArrayWashroomPictureDetails.put("{\"0\":\"empty\"}");
                }
                jsonObjectWashroomPicture.put("pictures_details", jsonArrayWashroomPictureDetails);
                jsonArrayPicture.put(jsonObjectWashroomPicture);
                ////////////////////////washroomimage

                //////////////////////bedroomimage
                JSONObject jsonObjectBedroomPicture = new JSONObject();

                jsonObjectBedroomPicture.put("status", "status");
                jsonObjectBedroomPicture.put("room_name", "bedroom");

                JSONArray jsonArrayBedroomPictureDetails = new JSONArray();


                if (bedroomimageModels.size() > 0) {

                    for (ImageModel bedroomImageModel : bedroomimageModels) {

                        JSONObject jsonObjectBedroomImages = new JSONObject();

                        jsonObjectBedroomImages.put("image_url", bedroomImageModel.
                                getStrImageUrl());
                        jsonObjectBedroomImages.put("description", bedroomImageModel.
                                getStrImageDesc());
                        jsonObjectBedroomImages.put("date_time", bedroomImageModel.
                                getStrImageTime());

                        jsonArrayBedroomPictureDetails.put(jsonObjectBedroomImages);
                    }
                } else {

                    jsonArrayBedroomPictureDetails.put("{\"0\":\"empty\"}");
                }
                jsonObjectBedroomPicture.put("pictures_details", jsonArrayBedroomPictureDetails);
                jsonArrayPicture.put(jsonObjectBedroomPicture);
                ////////////////////////bedroomimage

                jsonObjectCheckinCare.put("picture", jsonArrayPicture);

                ////

                JSONArray jsonArrayActivities = new JSONArray();


                /////////activity1
                JSONObject jsonObjectActivities = new JSONObject();

                jsonObjectActivities.put("activity_name", "home_essentials");

                JSONArray jsonArraySubActivitiesHome = new JSONArray();

                JSONObject jsonObjectSubActivitiesHome = new JSONObject();
                jsonObjectSubActivitiesHome.put("sub_activity_name", "kitchen_equipments");
                jsonObjectSubActivitiesHome.put("status", kitchen_equipments.getText().toString());
                jsonObjectSubActivitiesHome.put("checkbox_status", kitchenequipcheck.isChecked());

                jsonArraySubActivitiesHome.put(jsonObjectSubActivitiesHome);

                JSONObject jsonObjectSubActivitiesHome1 = new JSONObject();
                jsonObjectSubActivitiesHome1.put("sub_activity_name", "grocery");
                jsonObjectSubActivitiesHome1.put("status", grocery.getText().toString());
                jsonObjectSubActivitiesHome1.put("checkbox_status",grocerycheck.isChecked());


                jsonArraySubActivitiesHome.put(jsonObjectSubActivitiesHome1);

                JSONObject jsonObjectSubActivitiesHome2 = new JSONObject();
                jsonObjectSubActivitiesHome2.put("sub_activity_name", "utility_bills");
                jsonObjectSubActivitiesHome2.put("utility_name", "water ");
                jsonObjectSubActivitiesHome2.put("due_status", items[0]);
                jsonObjectSubActivitiesHome2.put("due_date", txtwater.getText().toString());
                jsonObjectSubActivitiesHome2.put("status", waterstatus.getText().toString());


                jsonArraySubActivitiesHome.put(jsonObjectSubActivitiesHome2);

                JSONObject jsonObjectSubActivitiesHome3 = new JSONObject();
                jsonObjectSubActivitiesHome3.put("sub_activity_name", "utility_bills");
                jsonObjectSubActivitiesHome3.put("utility_name", "gas");
                jsonObjectSubActivitiesHome3.put("due_status", items[1]);
                jsonObjectSubActivitiesHome3.put("due_date", txtgas.getText().toString());
                jsonObjectSubActivitiesHome3.put("status", gasstatus.getText().toString());


                jsonArraySubActivitiesHome.put(jsonObjectSubActivitiesHome3);

                JSONObject jsonObjectSubActivitiesHome4 = new JSONObject();
                jsonObjectSubActivitiesHome4.put("sub_activity_name", "utility_bills");
                jsonObjectSubActivitiesHome4.put("utility_name", "electricity");
                jsonObjectSubActivitiesHome4.put("due_status", items[2]);
                jsonObjectSubActivitiesHome4.put("due_date", txtelectricity.getText().toString());
                jsonObjectSubActivitiesHome4.put("status", electricitystatus.getText().toString());


                jsonArraySubActivitiesHome.put(jsonObjectSubActivitiesHome4);

                JSONObject jsonObjectSubActivitiesHome5 = new JSONObject();
                jsonObjectSubActivitiesHome5.put("sub_activity_name", "utility_bills");
                jsonObjectSubActivitiesHome5.put("utility_name", "telephone");
                jsonObjectSubActivitiesHome5.put("due_status", items[3]);
                jsonObjectSubActivitiesHome5.put("due_date", txttelephone.getText().toString());
                jsonObjectSubActivitiesHome5.put("status", telephonestatus.getText().toString());


                jsonArraySubActivitiesHome.put(jsonObjectSubActivitiesHome5);

                jsonObjectActivities.put("sub_activities", jsonArraySubActivitiesHome);

                ///

                jsonArrayActivities.put(jsonObjectActivities);
                ////////////////activity1


                ///////////////////////activity 2
                JSONObject jsonObjectActivitiesDomestic = new JSONObject();

                jsonObjectActivitiesDomestic.put("activity_name", "domestic_help_status");

                JSONArray jsonArraySubActivitiesDomestic = new JSONArray();

                JSONObject jsonObjectSubActivitiesDomestic = new JSONObject();
                jsonObjectSubActivitiesDomestic.put("sub_activity_name", "maid_services");
                jsonObjectSubActivitiesDomestic.put("status", maidservices.getText().toString());
                jsonObjectSubActivitiesDomestic.put("checkbox_status", domesticcheck.isChecked());

                jsonArraySubActivitiesDomestic.put(jsonObjectSubActivitiesDomestic);

                JSONObject jsonObjectSubActivitiesDomestic1 = new JSONObject();
                jsonObjectSubActivitiesDomestic1.put("sub_activity_name", "driver_status");
                jsonObjectSubActivitiesDomestic1.put("status", driveredt.getText().toString());
                jsonObjectSubActivitiesDomestic1.put("checkbox_status", drivercheck.isChecked());

                jsonArraySubActivitiesDomestic.put(jsonObjectSubActivitiesDomestic1);

                jsonObjectActivitiesDomestic.put("sub_activities", jsonArraySubActivitiesDomestic);

                jsonArrayActivities.put(jsonObjectActivitiesDomestic);
                ///////////////////////activity 2


                ///////////////////////activity 3
                JSONObject jsonObjectActivitiesEquipment = new JSONObject();

                jsonObjectActivitiesEquipment.put("activity_name", "equipment_working_status");

                JSONArray jsonArraySubActivitiesEquipment = new JSONArray();

                JSONObject jsonObjectSubActivitiesEquipment1 = new JSONObject();
                jsonObjectSubActivitiesEquipment1.put("sub_activity_name", "electronic");
                jsonObjectSubActivitiesEquipment1.put("status", electronic.getText().toString());
                jsonObjectSubActivitiesEquipment1.put("checkbox_status", electrocheck.isChecked());

                jsonArraySubActivitiesEquipment.put(jsonObjectSubActivitiesEquipment1);


                JSONObject jsonObjectSubActivitiesEquipment2 = new JSONObject();
                jsonObjectSubActivitiesEquipment2.put("sub_activity_name", "home_appliances");
                jsonObjectSubActivitiesEquipment2.put("status", homeapplience.getText().toString());
                jsonObjectSubActivitiesEquipment2.put("checkbox_status", homecheck.isChecked());

                jsonArraySubActivitiesEquipment.put(jsonObjectSubActivitiesEquipment2);


                JSONObject jsonObjectSubActivitiesEquipment3 = new JSONObject();
                jsonObjectSubActivitiesEquipment3.put("sub_activity_name", "automobile");
                jsonObjectSubActivitiesEquipment3.put("status", automobile.getText().toString());
                jsonObjectSubActivitiesEquipment3.put("checkbox_status", autocheck.isChecked() );

                jsonArraySubActivitiesEquipment.put(jsonObjectSubActivitiesEquipment3);


                jsonObjectActivitiesEquipment.put("sub_activities",
                        jsonArraySubActivitiesEquipment);

                jsonArrayActivities.put(jsonObjectActivitiesEquipment);
                ///////////////////////activity 3


                jsonObjectCheckinCare.put("activities", jsonArrayActivities);


            } catch (Exception e) {
                e.printStackTrace();
            }

            storageService.insertDocs(Config.collectionCheckInCare, jsonObjectCheckinCare,
                    new AsyncApp42ServiceApi.App42StorageServiceListener() {

                        @Override
                        public void onDocumentInserted(Storage response) {
                            try {
                                if (mProgressDialog.isShowing())
                                    mProgressDialog.dismiss();
                                if (response.isResponseSuccess()) {

                                    if (response.getJsonDocList().size() > 0) {

                                        Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);

                                        ///
                                        String values[] = {jsonDocument.getDocId(),
                                                jsonDocument.getUpdatedAt(),
                                                jsonDocument.getJsonDoc(),
                                                Config.collectionCheckInCare, "", "1",
                                                Config.providerModel.getStrProviderId()
                                        };

                                        String selection = DbHelper.COLUMN_OBJECT_ID + " = ? and "
                                                + DbHelper.COLUMN_COLLECTION_NAME + "=? and"
                                                + DbHelper.COLUMN_PROVIDER_ID + "=?";

                                        // WHERE clause arguments
                                        String[] selectionArgs = {jsonDocument.getDocId(),
                                                Config.collectionCheckInCare,
                                                Config.providerModel.getStrProviderId()
                                        };
                                        CareGiver.getDbCon().updateInsert(
                                                DbHelper.strTableNameCollection,
                                                selection, values, DbHelper.COLLECTION_FIELDS,
                                                selectionArgs);


                                        //////////////


                                        //todo fetch from session if offline sync update enabled
                                        String values1[] = {jsonDocument.getDocId(),
                                                "-1",
                                                strSelectedDate,
                                                Config.customerModel.getStrCustomerID(),
                                                Config.providerModel.getStrProviderId()
                                        };

                                        String selection1 = DbHelper.COLUMN_OBJECT_ID + " = ? and "
                                                + DbHelper.COLUMN_MILESTONE_ID + "=? ";

                                        String[] selectionArgs1 = {jsonDocument.getDocId(),
                                                "-1"
                                        };

                                        CareGiver.getDbCon().updateInsert(
                                                DbHelper.strTableNameMilestone,
                                                selection1, values1, DbHelper.CCARE_FIELDS,
                                                selectionArgs1);

                                        //notification
                                        String strPushMessage = getString(R.string.notification_checkincare);

                                        final JSONObject jsonObject = new JSONObject();

                                        try {

                                            String strDateNow;
                                            Calendar calendar = Calendar.getInstance();
                                            Date dateNow = calendar.getTime();
                                            strDateNow = Utils.convertDateToString(dateNow);

                                            jsonObject.put("created_by", Config.providerModel.getStrProviderId());
                                            jsonObject.put("time", strDateNow);
                                            jsonObject.put("user_type", "dependent");
                                            jsonObject.put("user_id", dependentId);
                                            jsonObject.put("checkincare", true);
                                            jsonObject.put("checkincare_id", jsonDocument.getDocId());

                                            //todo add for customer
                                            jsonObject.put("created_by_type", "provider");
                                            jsonObject.put(App42GCMService.ExtraMessage, strPushMessage);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        sendPush(jsonObject);
                                        insertNotification(jsonObject);

                                    }
                                    ///


                                    //IMAGE_COUNT = 0;
                                    bViewLoaded =false;

                                    utils.toast(1, 1, getString(R.string.data_upload));
                                    Intent intent = new Intent(CheckInCareActivity.this,
                                            DashboardActivity.class);
                                    Config.intSelectedMenu = Config.intClientScreen;
                                    startActivity(intent);
                                    finish();
                                } else {
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onUpdateDocSuccess(Storage response) {
                        }

                        @Override
                        public void onFindDocSuccess(Storage response) {
                        }

                        @Override
                        public void onInsertionFailed(App42Exception ex) {
                            try {
                                if (mProgressDialog.isShowing())
                                    mProgressDialog.dismiss();
                                if (ex != null) {
                                    JSONObject jsonObject = new JSONObject(ex.getMessage());
                                    JSONObject jsonObjectError = jsonObject.
                                            getJSONObject("app42Fault");
                                    String strMess = jsonObjectError.getString("details");
                                    utils.toast(2, 2, strMess);
                                } else {
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                                utils.toast(2, 2, getString(R.string.error));
                            }
                        }

                        @Override
                        public void onFindDocFailed(App42Exception ex) {
                        }

                        @Override
                        public void onUpdateDocFailed(App42Exception ex) {
                        }
                    });

        } else {
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    private void editupdateJson(){

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

            JSONObject jsonObjectCheckinCare = null;

            try {
                jsonObjectCheckinCare = new JSONObject();

                Date mydate = new Date();
                String strCreateDate = Utils.readFormat.format(mydate);
                String strMonth = Utils.writeFormatDateMonth.format(mydate);
                String strYear = Utils.writeFormatDateYear.format(mydate);

                jsonObjectCheckinCare.put("created_date", strCreateDate);
                jsonObjectCheckinCare.put("dependent_id", dependentId);
                jsonObjectCheckinCare.put("provider_id", Config.providerModel.getStrProviderId());
                jsonObjectCheckinCare.put("updated_date", "");
                jsonObjectCheckinCare.put("current_date", datetxt.getText().toString());
                jsonObjectCheckinCare.put("created_date_actual", strSelectedDate);
                jsonObjectCheckinCare.put("status", "New");
                jsonObjectCheckinCare.put("month", strMonth);
                jsonObjectCheckinCare.put("year", strYear);
                jsonObjectCheckinCare.put("customer_id", Config.customerModel.getStrCustomerID());
                jsonObjectCheckinCare.put("media_comment", mediacomment.getText().toString());
                jsonObjectCheckinCare.put("check_in_care_name", Config.checkInCareModel.getStrName());

                JSONArray jsonArrayPicture = new JSONArray();

                ///////////////////////Hallimage
                JSONObject jsonObjectHallPicture = new JSONObject();

                jsonObjectHallPicture.put("status", "status");
                jsonObjectHallPicture.put("room_name", "hall");

                JSONArray jsonArrayHallPictureDetails = new JSONArray();


                if (hallimageModels.size() > 0) {

                    for (ImageModel hallImageModel : hallimageModels) {

                        JSONObject jsonObjectHallImages = new JSONObject();

                        jsonObjectHallImages.put("image_url", hallImageModel.getStrImageUrl());
                        jsonObjectHallImages.put("description", hallImageModel.getStrImageDesc());
                        jsonObjectHallImages.put("date_time", hallImageModel.getStrImageTime());

                        jsonArrayHallPictureDetails.put(jsonObjectHallImages);
                    }
                } else {

                    jsonArrayHallPictureDetails.put("{\"0\":\"empty\"}");
                }

                jsonObjectHallPicture.put("pictures_details", jsonArrayHallPictureDetails);
                jsonArrayPicture.put(jsonObjectHallPicture);
                ////////////////////////Hallimage

                //////////////////////kitchenimage
                JSONObject jsonObjectKitchenPicture = new JSONObject();

                jsonObjectKitchenPicture.put("status", "status");
                jsonObjectKitchenPicture.put("room_name", "kitchen");

                JSONArray jsonArrayKitchenPictureDetails = new JSONArray();

                if (kitchenimageModels.size() > 0) {

                    for (ImageModel kitchenImageModel : kitchenimageModels) {

                        JSONObject jsonObjectKitchenImages = new JSONObject();

                        jsonObjectKitchenImages.put("image_url", kitchenImageModel.
                                getStrImageUrl());
                        jsonObjectKitchenImages.put("description", kitchenImageModel.
                                getStrImageDesc());
                        jsonObjectKitchenImages.put("date_time", kitchenImageModel.
                                getStrImageTime());

                        jsonArrayKitchenPictureDetails.put(jsonObjectKitchenImages);
                    }
                } else {

                    jsonArrayKitchenPictureDetails.put("{\"0\":\"empty\"}");
                }
                jsonObjectKitchenPicture.put("pictures_details", jsonArrayKitchenPictureDetails);
                jsonArrayPicture.put(jsonObjectKitchenPicture);
                ////////////////////////kitchenimage


                //////////////////////washroomimage
                JSONObject jsonObjectWashroomPicture = new JSONObject();

                jsonObjectWashroomPicture.put("status", "status");
                jsonObjectWashroomPicture.put("room_name", "washroom");

                JSONArray jsonArrayWashroomPictureDetails = new JSONArray();

                if (washroomimageModels.size() > 0) {

                    for (ImageModel washroomImageModel : washroomimageModels) {

                        JSONObject jsonObjectWashroomImages = new JSONObject();

                        jsonObjectWashroomImages.put("image_url", washroomImageModel.
                                getStrImageUrl());
                        jsonObjectWashroomImages.put("description", washroomImageModel.
                                getStrImageDesc());
                        jsonObjectWashroomImages.put("date_time", washroomImageModel.
                                getStrImageTime());

                        jsonArrayWashroomPictureDetails.put(jsonObjectWashroomImages);
                    }
                } else {

                    jsonArrayWashroomPictureDetails.put("{\"0\":\"empty\"}");
                }
                jsonObjectWashroomPicture.put("pictures_details", jsonArrayWashroomPictureDetails);
                jsonArrayPicture.put(jsonObjectWashroomPicture);
                ////////////////////////washroomimage

                //////////////////////bedroomimage
                JSONObject jsonObjectBedroomPicture = new JSONObject();

                jsonObjectBedroomPicture.put("status", "status");
                jsonObjectBedroomPicture.put("room_name", "bedroom");

                JSONArray jsonArrayBedroomPictureDetails = new JSONArray();


                if (bedroomimageModels.size() > 0) {

                    for (ImageModel bedroomImageModel : bedroomimageModels) {

                        JSONObject jsonObjectBedroomImages = new JSONObject();

                        jsonObjectBedroomImages.put("image_url", bedroomImageModel.
                                getStrImageUrl());
                        jsonObjectBedroomImages.put("description", bedroomImageModel.
                                getStrImageDesc());
                        jsonObjectBedroomImages.put("date_time", bedroomImageModel.
                                getStrImageTime());

                        jsonArrayBedroomPictureDetails.put(jsonObjectBedroomImages);
                    }
                } else {

                    jsonArrayBedroomPictureDetails.put("{\"0\":\"empty\"}");
                }
                jsonObjectBedroomPicture.put("pictures_details", jsonArrayBedroomPictureDetails);
                jsonArrayPicture.put(jsonObjectBedroomPicture);
                ////////////////////////bedroomimage

                jsonObjectCheckinCare.put("picture", jsonArrayPicture);

                ////

                JSONArray jsonArrayActivities = new JSONArray();


                /////////activity1
                JSONObject jsonObjectActivities = new JSONObject();

                jsonObjectActivities.put("activity_name", "home_essentials");

                JSONArray jsonArraySubActivitiesHome = new JSONArray();

                JSONObject jsonObjectSubActivitiesHome = new JSONObject();
                jsonObjectSubActivitiesHome.put("sub_activity_name", "kitchen_equipments");
                jsonObjectSubActivitiesHome.put("status", kitchen_equipments.getText().toString());
                jsonObjectSubActivitiesHome.put("checkbox_status", kitchenequipcheck.isChecked());

                jsonArraySubActivitiesHome.put(jsonObjectSubActivitiesHome);

                JSONObject jsonObjectSubActivitiesHome1 = new JSONObject();
                jsonObjectSubActivitiesHome1.put("sub_activity_name", "grocery");
                jsonObjectSubActivitiesHome1.put("status", grocery.getText().toString());
                jsonObjectSubActivitiesHome1.put("checkbox_status", grocerycheck.isChecked());


                jsonArraySubActivitiesHome.put(jsonObjectSubActivitiesHome1);

                JSONObject jsonObjectSubActivitiesHome2 = new JSONObject();
                jsonObjectSubActivitiesHome2.put("sub_activity_name", "utility_bills");
                jsonObjectSubActivitiesHome2.put("utility_name", "water ");
                jsonObjectSubActivitiesHome2.put("due_status", items[0]);
                jsonObjectSubActivitiesHome2.put("due_date", txtwater.getText().toString());
                jsonObjectSubActivitiesHome2.put("status", waterstatus.getText().toString());


                jsonArraySubActivitiesHome.put(jsonObjectSubActivitiesHome2);

                JSONObject jsonObjectSubActivitiesHome3 = new JSONObject();
                jsonObjectSubActivitiesHome3.put("sub_activity_name", "utility_bills");
                jsonObjectSubActivitiesHome3.put("utility_name", "gas");
                jsonObjectSubActivitiesHome3.put("due_status", items[1]);
                jsonObjectSubActivitiesHome3.put("due_date", txtgas.getText().toString());
                jsonObjectSubActivitiesHome3.put("status", gasstatus.getText().toString());


                jsonArraySubActivitiesHome.put(jsonObjectSubActivitiesHome3);

                JSONObject jsonObjectSubActivitiesHome4 = new JSONObject();
                jsonObjectSubActivitiesHome4.put("sub_activity_name", "utility_bills");
                jsonObjectSubActivitiesHome4.put("utility_name", "electricity");
                jsonObjectSubActivitiesHome4.put("due_status", items[2]);
                jsonObjectSubActivitiesHome4.put("due_date", txtelectricity.getText().toString());
                jsonObjectSubActivitiesHome4.put("status", electricitystatus.getText().toString());


                jsonArraySubActivitiesHome.put(jsonObjectSubActivitiesHome4);

                JSONObject jsonObjectSubActivitiesHome5 = new JSONObject();
                jsonObjectSubActivitiesHome5.put("sub_activity_name", "utility_bills");
                jsonObjectSubActivitiesHome5.put("utility_name", "telephone");
                jsonObjectSubActivitiesHome5.put("due_status", items[3]);
                jsonObjectSubActivitiesHome5.put("due_date", txttelephone.getText().toString());
                jsonObjectSubActivitiesHome5.put("status", telephonestatus.getText().toString());


                jsonArraySubActivitiesHome.put(jsonObjectSubActivitiesHome5);

                jsonObjectActivities.put("sub_activities", jsonArraySubActivitiesHome);

                ///

                jsonArrayActivities.put(jsonObjectActivities);
                ////////////////activity1


                ///////////// //////////activity 2
                JSONObject jsonObjectActivitiesDomestic = new JSONObject();

                jsonObjectActivitiesDomestic.put("activity_name", "domestic_help_status");

                JSONArray jsonArraySubActivitiesDomestic = new JSONArray();

                JSONObject jsonObjectSubActivitiesDomestic = new JSONObject();
                jsonObjectSubActivitiesDomestic.put("sub_activity_name", "maid_services");
                jsonObjectSubActivitiesDomestic.put("status", maidservices.getText().toString());
                jsonObjectSubActivitiesDomestic.put("checkbox_status", domesticcheck.isChecked());

                jsonArraySubActivitiesDomestic.put(jsonObjectSubActivitiesDomestic);

                JSONObject jsonObjectSubActivitiesDomestic1 = new JSONObject();
                jsonObjectSubActivitiesDomestic1.put("sub_activity_name", "driver_status");
                jsonObjectSubActivitiesDomestic1.put("status", driveredt.getText().toString());
                jsonObjectSubActivitiesDomestic1.put("checkbox_status", drivercheck.isChecked());

                jsonArraySubActivitiesDomestic.put(jsonObjectSubActivitiesDomestic1);

                jsonObjectActivitiesDomestic.put("sub_activities", jsonArraySubActivitiesDomestic);

                jsonArrayActivities.put(jsonObjectActivitiesDomestic);
                ///////////////////////activity 2


                ///////////////////////activity 3
                JSONObject jsonObjectActivitiesEquipment = new JSONObject();

                jsonObjectActivitiesEquipment.put("activity_name", "equipment_working_status");

                JSONArray jsonArraySubActivitiesEquipment = new JSONArray();

                JSONObject jsonObjectSubActivitiesEquipment1 = new JSONObject();
                jsonObjectSubActivitiesEquipment1.put("sub_activity_name", "electronic");
                jsonObjectSubActivitiesEquipment1.put("status", electronic.getText().toString());
                jsonObjectSubActivitiesEquipment1.put("checkbox_status", electrocheck.isChecked());

                jsonArraySubActivitiesEquipment.put(jsonObjectSubActivitiesEquipment1);


                JSONObject jsonObjectSubActivitiesEquipment2 = new JSONObject();
                jsonObjectSubActivitiesEquipment2.put("sub_activity_name", "home_appliances");
                jsonObjectSubActivitiesEquipment2.put("status", homeapplience.getText().toString());
                jsonObjectSubActivitiesEquipment2.put("checkbox_status", homecheck.isChecked());

                jsonArraySubActivitiesEquipment.put(jsonObjectSubActivitiesEquipment2);


                JSONObject jsonObjectSubActivitiesEquipment3 = new JSONObject();
                jsonObjectSubActivitiesEquipment3.put("sub_activity_name", "automobile");
                jsonObjectSubActivitiesEquipment3.put("status", automobile.getText().toString());
                jsonObjectSubActivitiesEquipment3.put("checkbox_status", autocheck.isChecked());

                jsonArraySubActivitiesEquipment.put(jsonObjectSubActivitiesEquipment3);


                jsonObjectActivitiesEquipment.put("sub_activities",
                        jsonArraySubActivitiesEquipment);

                jsonArrayActivities.put(jsonObjectActivitiesEquipment);
                ///////////////////////activity 3


                jsonObjectCheckinCare.put("activities", jsonArrayActivities);


            } catch (Exception e) {
                e.printStackTrace();
            }

        if (utils.isConnectingToInternet()) {


            final JSONObject jsonObject = jsonObjectCheckinCare;

            storageService.updateDocs(jsonObjectCheckinCare,
                    Config.checkInCareModel.getStrDocumentID(),
                    Config.collectionCheckInCare, new App42CallBack() {
                        @Override
                        public void onSuccess(Object response) {
                            if (mProgressDialog.isShowing())
                                mProgressDialog.dismiss();

                            ////////////////////////////////////////
                            /////////////////////////udpate to DB
                            ///todo enable offline sync
                            String values[] = {Config.checkInCareModel.getStrDocumentID(),
                                    "",
                                    jsonObject.toString(),
                                    Config.collectionCheckInCare, "", "1",
                                    Config.providerModel.getStrProviderId()
                            };

                            String selection = DbHelper.COLUMN_OBJECT_ID + " = ? and "
                                    + DbHelper.COLUMN_COLLECTION_NAME + "=? and "
                                    + DbHelper.COLUMN_PROVIDER_ID + "=?";

                            // WHERE clause arguments
                            String[] selectionArgs = {Config.checkInCareModel.getStrDocumentID(),
                                    Config.collectionCheckInCare,
                                    Config.providerModel.getStrProviderId()
                            };
                            CareGiver.getDbCon().updateInsert(
                                    DbHelper.strTableNameCollection,
                                    selection, values, DbHelper.COLLECTION_FIELDS,
                                    selectionArgs);


                            //todo fetch from session if offline sync update enabled
                            String values1[] = {Config.checkInCareModel.getStrDocumentID(),
                                    "-1",
                                    strSelectedDate,
                                    Config.customerModel.getStrCustomerID(),
                                    Config.providerModel.getStrProviderId()
                            };

                            String selection1 = DbHelper.COLUMN_OBJECT_ID + " = ? and "
                                    + DbHelper.COLUMN_MILESTONE_ID + "=? ";

                            String[] selectionArgs1 = {Config.checkInCareModel.getStrDocumentID(),
                                    "-1"
                            };

                            CareGiver.getDbCon().updateInsert(
                                    DbHelper.strTableNameMilestone,
                                    selection1, values1, DbHelper.CCARE_FIELDS,
                                    selectionArgs1);

                            //notification
                            String strPushMessage = getString(R.string.notification_checkincare);

                            final JSONObject jsonObject1 = new JSONObject();

                            try {

                                String strDateNow;
                                Calendar calendar = Calendar.getInstance();
                                Date dateNow = calendar.getTime();
                                strDateNow = Utils.convertDateToString(dateNow);

                                jsonObject1.put("created_by", Config.providerModel.getStrProviderId());
                                jsonObject1.put("time", strDateNow);
                                jsonObject1.put("user_type", "dependent");
                                jsonObject1.put("user_id", dependentId);
                                jsonObject1.put("checkincare", true);
                                jsonObject1.put("checkincare_id", Config.checkInCareModel.getStrDocumentID());

                                //todo add for customer
                                jsonObject1.put("created_by_type", "provider");
                                jsonObject1.put(App42GCMService.ExtraMessage, strPushMessage);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            sendPush(jsonObject1);
                            insertNotification(jsonObject1);
                            /////////////////////////udpate to DB
                            ////////////////////////////////////////

                            //Utils.log(response.toString(), "Success");
                            //IMAGE_COUNT = 0;
                            bViewLoaded =false;
                            utils.toast(1, 1, getString(R.string.data_upload));
                            editcheckincare=false;
                            Intent intent = new Intent(CheckInCareActivity.this,
                                    DashboardActivity.class);
                            Config.intSelectedMenu = Config.intClientScreen;
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onException(Exception e) {
                            e.printStackTrace();
                            if (mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            utils.toast(2, 2, getString(R.string.warning_internet));
                        }
                    });

        } else {
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        permissionHelper.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            try {

                if(isFlag==1) {
                    uploadhallbtn.setVisibility(View.VISIBLE);
                    isHallflag = 1;
                }
                if(isFlag==2) {
                    uploadkitchenbtn.setVisibility(View.VISIBLE);
                    isKitchenflag=1;
                }
                if(isFlag==3) {
                    uploadwashroombtn.setVisibility(View.VISIBLE);
                    isWashflag=1;
                }
                if(isFlag==4) {
                    uploadbedroombtn.setVisibility(View.VISIBLE);
                    isBedflag=1;
                }

                /*   mProgress.setMessage(getString(R.string.loading));
                mProgress.show();*/
                switch (requestCode) {
                    case Config.START_CAMERA_REQUEST_CODE:

                        backgroundThreadHandler = new BackgroundThreadHandler();
                        strImageName = Utils.customerImageUri.getPath();
                        Thread backgroundThreadCamera = new BackgroundThreadCamera();
                        backgroundThreadCamera.start();
                        break;

                    case Config.START_GALLERY_REQUEST_CODE:
                        backgroundThreadHandler = new BackgroundThreadHandler();

                        imagePaths.clear();

                        String[] all_path = intent.getStringArrayExtra("all_path");

                        if(isFlag==1) {
                            if (all_path.length + Hall_IMAGE_COUNT > 4) {

                                for (int i = 0; i < (4 - Hall_IMAGE_COUNT); i++) {
                                    imagePaths.add(all_path[i]);
                                }

                            } else {

                                for (String string : all_path) {
                                    imagePaths.add(string);
                                }
                            }
                        }
                        if(isFlag==2) {
                            if (all_path.length + Kitchen_IMAGE_COUNT > 4) {

                                for (int i = 0; i < (4 - Kitchen_IMAGE_COUNT); i++) {
                                    imagePaths.add(all_path[i]);
                                }

                            } else {

                                for (String string : all_path) {
                                    imagePaths.add(string);
                                }
                            }
                        }

                        if(isFlag==3) {
                            if (all_path.length + Washroom_IMAGE_COUNT > 4) {

                                for (int i = 0; i < (4 - Washroom_IMAGE_COUNT); i++) {
                                    imagePaths.add(all_path[i]);
                                }

                            } else {

                                for (String string : all_path) {
                                    imagePaths.add(string);
                                }
                            }
                        }

                        if(isFlag==4) {
                            if (all_path.length + Bedroom_IMAGE_COUNT > 4) {

                                for (int i = 0; i < (4 - Bedroom_IMAGE_COUNT); i++) {
                                    imagePaths.add(all_path[i]);
                                }

                            } else {

                                for (String string : all_path) {
                                    imagePaths.add(string);
                                }
                            }
                        }

                        //Collections.addAll(imagePaths, all_path);

                        Thread backgroundThread = new BackgroundThread();
                        backgroundThread.start();

                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private void addHallImages() {

        layouthall.removeAllViews();

        if (hallimageModels != null && hallimageModels.size() > 0) {

            hallstatus.setVisibility(View.VISIBLE);
            hallstatus.setText(getString(R.string.done));
            hallstatus.setTextColor(Color.WHITE);
            if (kitchenstatus.getText().toString().equals("Done")
                    && washroomstatus.getText().toString().equals("Done")
                    && bedroomstatus.getText().toString().equals("Done")) {

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText(getString(R.string.done));
                uploadmediastatus.setTextColor(Color.BLUE);
            } else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText(getString(R.string.pending));
                uploadmediastatus.setTextColor(Color.RED);
            }

            for (int i = 0; i < hallimageModels.size(); i++) {
                try {
                    final ImageView imageView = new ImageView(CheckInCareActivity.this);
                    imageView.setPadding(0, 0, 3, 0);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(10, 10, 10, 10);


                    imageView.setLayoutParams(layoutParams);
                    //imageView.setImageBitmap(hallbitmaps.get(i));

                    if (hallbitmaps.size() > 0 && i < hallbitmaps.size()) {
                        imageView.setImageBitmap(hallbitmaps.get(i));

                    }

                    imageView.setTag(hallimageModels.get(i));
                    imageView.setTag(R.id.three, i);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);



                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (isHallflag==1) {
                                final ImageModel mImageModel = (ImageModel) v.getTag();

                                final int mPosition = (int) v.getTag(R.id.three);

                                final Dialog dialog = new Dialog(CheckInCareActivity.this);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                                dialog.setContentView(R.layout.image_dialog_layout);

                                TouchImageView mOriginal = (TouchImageView) dialog.findViewById(
                                        R.id.imgOriginal);
                                TextView textViewClose = (TextView) dialog.findViewById(
                                        R.id.textViewClose);
                                Button buttonDelete = (Button) dialog.findViewById(
                                        R.id.textViewTitle);

                                if (isCompleted)
                                    buttonDelete.setVisibility(View.INVISIBLE);


                                textViewClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //mOriginal.
                                        dialog.dismiss();
                                    }
                                });

                                buttonDelete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        //
                                        final AlertDialog.Builder alertbox =
                                                new AlertDialog.Builder(CheckInCareActivity.this);
                                        alertbox.setTitle(getString(R.string.delete_image));
                                        alertbox.setMessage(getString(R.string.confirm_delete_image));
                                        alertbox.setPositiveButton(getString(R.string.yes),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface arg0, int arg1) {

                                                        try {
                                                            File fDelete = utils.getInternalFileImages(
                                                                    mImageModel.getStrImageName());

                                                            if (fDelete.exists()) {
                                                                success = fDelete.delete();

                                                                if(hallimageModels.size()<=0){
                                                                    isHallflag=0;
                                                                }
                                                                if (mImageModel.ismIsNew())
                                                                    hallImageCount--;
                                                                    Hall_IMAGE_COUNT--;

                                                                mImageChanged = true;

                                                                hallimageModels.remove(mImageModel);

                                                                if (hallbitmaps.size() > 0 && mPosition < hallbitmaps.size()) {
                                                                    hallbitmaps.remove(mPosition);
                                                                }

                                                                if (hallImageCount < 1) {
                                                                    // uploadhallbtn.setVisibility(View.GONE);
                                                                    hallstatus.setVisibility(View.VISIBLE);
                                                                    hallstatus.setText(getString(
                                                                            R.string.pending));
                                                                    hallstatus.setTextColor(Color.RED);
                                                                    if (kitchenstatus.
                                                                            getText().toString().equals("Done")
                                                                            && hallstatus.
                                                                            getText().toString().equals("Done")
                                                                            && washroomstatus.
                                                                            getText().toString().equals("Done")
                                                                            && bedroomstatus.
                                                                            getText().toString().
                                                                            equals("Done")) {

                                                                        uploadmediastatus.setVisibility(
                                                                                View.VISIBLE);
                                                                        uploadmediastatus.setText(getString(
                                                                                R.string.done));
                                                                        uploadmediastatus.setTextColor(
                                                                                Color.BLUE);
                                                                    } else {
                                                                        uploadmediastatus.setVisibility(
                                                                                View.VISIBLE);
                                                                        uploadmediastatus.setText(getString(
                                                                                R.string.pending));
                                                                        uploadmediastatus.setTextColor(
                                                                                Color.RED);
                                                                    }
                                                                }
                                                            }
                                                            if (success) {
                                                                utils.toast(2, 2, getString(
                                                                        R.string.file_deleted));

                                                            }
                                                            arg0.dismiss();
                                                            dialog.dismiss();
                                                            addHallImages();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                        alertbox.setNegativeButton(getString(R.string.no),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        arg0.dismiss();
                                                    }
                                                });
                                        alertbox.show();
                                        //
                                    }
                                });


                                try {
                                    mOriginal.setImageBitmap(hallbitmaps.get(mPosition));
                                    //, Config.intWidth, Config.intHeight)
                                } catch (OutOfMemoryError oOm) {
                                    oOm.printStackTrace();
                                }
                                dialog.setCancelable(true);

                                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT); //Controlling width and height.
                                dialog.show();

                            }
                        }
                    });

                    layouthall.addView(imageView);
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }

        } else {
            hallstatus.setVisibility(View.VISIBLE);
            hallstatus.setText(getString(R.string.pending));
            hallstatus.setTextColor(Color.RED);
            if (kitchenstatus.getText().toString().equals("Done")
                    && hallstatus.getText().toString().equals("Done")
                    && washroomstatus.getText().toString().equals("Done")
                    && bedroomstatus.getText().toString().equals("Done")) {

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText(getString(R.string.done));
                uploadmediastatus.setTextColor(Color.BLUE);
            } else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText(getString(R.string.pending));
                uploadmediastatus.setTextColor(Color.RED);
            }

        }

    }

    private void addKitchenImages() {

        layoutkitchen.removeAllViews();

        if (kitchenimageModels != null && kitchenimageModels.size() > 0) {

            kitchenstatus.setVisibility(View.VISIBLE);
            kitchenstatus.setText(getString(R.string.done));
            kitchenstatus.setTextColor(Color.WHITE);

            if (hallstatus.getText().toString().equals("Done")
                    && washroomstatus.getText().toString().equals("Done")
                    && bedroomstatus.getText().toString().equals("Done")) {

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText(getString(R.string.done));
                uploadmediastatus.setTextColor(Color.BLUE);
            } else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText(getString(R.string.pending));
                uploadmediastatus.setTextColor(Color.RED);
            }

            for (int i = 0; i < kitchenimageModels.size(); i++) {
                try {
                    final ImageView imageView = new ImageView(CheckInCareActivity.this);
                    imageView.setPadding(0, 0, 3, 0);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(10, 10, 10, 10);


                    imageView.setLayoutParams(layoutParams);

                    imageView.setTag(kitchenimageModels.get(i));

                    if (kitchenbitmaps.size() > 0 && i < kitchenbitmaps.size()) {
                        imageView.setImageBitmap(kitchenbitmaps.get(i));
                    }

                    imageView.setTag(R.id.three, i);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);


                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (isKitchenflag==1) {
                                final ImageModel mImageModel = (ImageModel) v.getTag();

                                final int mPosition = (int) v.getTag(R.id.three);

                                final Dialog dialog = new Dialog(CheckInCareActivity.this);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                                dialog.setContentView(R.layout.image_dialog_layout);

                                TouchImageView mOriginal = (TouchImageView) dialog.findViewById(
                                        R.id.imgOriginal);
                                TextView textViewClose = (TextView) dialog.findViewById(
                                        R.id.textViewClose);
                                Button buttonDelete = (Button) dialog.findViewById(
                                        R.id.textViewTitle);

                                if (isCompleted)
                                    buttonDelete.setVisibility(View.INVISIBLE);


                                textViewClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //mOriginal.
                                        dialog.dismiss();
                                    }
                                });

                                buttonDelete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        //
                                        final AlertDialog.Builder alertbox =
                                                new AlertDialog.Builder(CheckInCareActivity.this);
                                        alertbox.setTitle(getString(R.string.delete_image));
                                        alertbox.setMessage(getString(R.string.confirm_delete_image));
                                        alertbox.setPositiveButton(getString(R.string.yes),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface arg0, int arg1) {

                                                        try {
                                                            File fDelete = utils.getInternalFileImages(
                                                                    mImageModel.getStrImageName());

                                                            if (fDelete.exists()) {
                                                                success = fDelete.delete();

                                                                if(kitchenimageModels.size()<=0){
                                                                    isKitchenflag=0;
                                                                }

                                                                if (mImageModel.ismIsNew())
                                                                    kitchenImageCount--;
                                                                    Kitchen_IMAGE_COUNT--;

                                                                mImageChanged = true;

                                                                kitchenimageModels.remove(mImageModel);

                                                                if (kitchenbitmaps.size() > 0 && mPosition < kitchenbitmaps.size()) {
                                                                    kitchenbitmaps.remove(mPosition);
                                                                }

                                                                if (kitchenImageCount < 1) {
                                                                    //  uploadkitchenbtn.setVisibility(View.GONE);
                                                                    kitchenstatus.setVisibility(
                                                                            View.VISIBLE);
                                                                    kitchenstatus.setText(getString(
                                                                            R.string.pending));
                                                                    kitchenstatus.setTextColor(Color.RED);

                                                                    if (hallstatus.getText().toString().
                                                                            equals("Done")
                                                                            && kitchenstatus.
                                                                            getText().toString().equals("Done")
                                                                            && washroomstatus.
                                                                            getText().toString().equals("Done")
                                                                            && bedroomstatus.
                                                                            getText().toString().
                                                                            equals("Done")) {

                                                                        uploadmediastatus.setVisibility(
                                                                                View.VISIBLE);
                                                                        uploadmediastatus.setText(
                                                                                getString(R.string.done));
                                                                        uploadmediastatus.setTextColor(
                                                                                Color.BLUE);
                                                                    } else {
                                                                        uploadmediastatus.setVisibility(
                                                                                View.VISIBLE);
                                                                        uploadmediastatus.setText(getString(
                                                                                R.string.pending));
                                                                        uploadmediastatus.setTextColor(
                                                                                Color.RED);
                                                                    }
                                                                }
                                                            }
                                                            if (success) {
                                                                utils.toast(2, 2, getString(
                                                                        R.string.file_deleted));

                                                            }
                                                            arg0.dismiss();
                                                            dialog.dismiss();
                                                            addKitchenImages();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                        alertbox.setNegativeButton(getString(R.string.no),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        arg0.dismiss();
                                                    }
                                                });
                                        alertbox.show();
                                        //
                                    }
                                });


                                try {
                                    mOriginal.setImageBitmap(kitchenbitmaps.get(mPosition));
                                    //, Config.intWidth, Config.intHeight)
                                } catch (OutOfMemoryError oOm) {
                                    oOm.printStackTrace();
                                }
                                dialog.setCancelable(true);

                                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT); //Controlling width and height.
                                dialog.show();

                            }
                        }
                    });

                    layoutkitchen.addView(imageView);
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }

        } else {
            kitchenstatus.setVisibility(View.VISIBLE);
            kitchenstatus.setText(getString(R.string.pending));
            kitchenstatus.setTextColor(Color.RED);
            if (hallstatus.getText().toString().equals("Done")
                    && kitchenstatus.getText().toString().equals("Done")
                    && washroomstatus.getText().toString().equals("Done")
                    && bedroomstatus.getText().toString().equals("Done")) {

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText(getString(R.string.done));
                uploadmediastatus.setTextColor(Color.BLUE);
            } else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText(getString(R.string.pending));
                uploadmediastatus.setTextColor(Color.RED);
            }

        }
    }

    private void addWashroomImages() {

        layoutwashroom.removeAllViews();

        if (washroomimageModels != null && washroomimageModels.size() > 0) {

            washroomstatus.setVisibility(View.VISIBLE);
            washroomstatus.setText(getString(R.string.done));
            washroomstatus.setTextColor(Color.WHITE);

            if (hallstatus.getText().toString().equals(getString(R.string.done))
                    && kitchenstatus.getText().toString().equals(getString(R.string.done))
                    && bedroomstatus.getText().toString().equals(getString(R.string.done))) {

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText(getString(R.string.done));
                uploadmediastatus.setTextColor(Color.BLUE);
            } else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText(getString(R.string.pending));
                uploadmediastatus.setTextColor(Color.RED);
            }


            for (int i = 0; i < washroomimageModels.size(); i++) {
                try {
                    final ImageView imageView = new ImageView(CheckInCareActivity.this);
                    imageView.setPadding(0, 0, 3, 0);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.
                            LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(10, 10, 10, 10);


                    imageView.setLayoutParams(layoutParams);

                    if (washroombitmaps.size() > 0 && i < washroombitmaps.size()) {
                        imageView.setImageBitmap(washroombitmaps.get(i));
                    }

                    imageView.setTag(washroomimageModels.get(i));
                    imageView.setTag(R.id.three, i);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);


                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (isWashflag==1) {
                                final ImageModel mImageModel = (ImageModel) v.getTag();

                                final int mPosition = (int) v.getTag(R.id.three);

                                final Dialog dialog = new Dialog(CheckInCareActivity.this);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                                dialog.setContentView(R.layout.image_dialog_layout);

                                TouchImageView mOriginal = (TouchImageView) dialog.findViewById(
                                        R.id.imgOriginal);
                                TextView textViewClose = (TextView) dialog.findViewById(
                                        R.id.textViewClose);
                                Button buttonDelete = (Button) dialog.findViewById(
                                        R.id.textViewTitle);

                                if (isCompleted)
                                    buttonDelete.setVisibility(View.INVISIBLE);


                                textViewClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //mOriginal.
                                        dialog.dismiss();
                                    }
                                });

                                buttonDelete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        //
                                        final AlertDialog.Builder alertbox =
                                                new AlertDialog.Builder(CheckInCareActivity.this);
                                        alertbox.setTitle(getString(R.string.delete_image));
                                        alertbox.setMessage(getString(R.string.confirm_delete_image));
                                        alertbox.setPositiveButton(getString(R.string.yes),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface arg0, int arg1) {

                                                        try {
                                                            File fDelete = utils.getInternalFileImages(
                                                                    mImageModel.getStrImageName());

                                                            if (fDelete.exists()) {
                                                                success = fDelete.delete();

                                                                if(washroomimageModels.size()<=0){
                                                                    isWashflag=0;
                                                                }

                                                                if (mImageModel.ismIsNew())
                                                                    washroomImageCount--;
                                                                    Washroom_IMAGE_COUNT--;

                                                                mImageChanged = true;

                                                                washroomimageModels.remove(mImageModel);

                                                                //washroombitmaps.remove(mPosition);

                                                                if (washroombitmaps.size() > 0 && mPosition < washroombitmaps.size()) {
                                                                    washroombitmaps.remove(mPosition);
                                                                }

                                                                if (washroomImageCount < 1) {
                                                                    //  uploadwashroombtn.setVisibility(View.GONE);
                                                                    washroomstatus.setVisibility(View.VISIBLE);
                                                                    washroomstatus.setText(getString(R.string.
                                                                            pending));
                                                                    washroomstatus.setTextColor(Color.RED);
                                                                    if (hallstatus.getText().toString().
                                                                            equals(getString(R.string.done))
                                                                            && washroomstatus.getText().
                                                                            toString().equals(getString(
                                                                            R.string.done))
                                                                            && kitchenstatus.getText().
                                                                            toString().equals(getString(
                                                                            R.string.done))
                                                                            && bedroomstatus.getText().
                                                                            toString().equals(getString(
                                                                            R.string.done))) {

                                                                        uploadmediastatus.setVisibility(
                                                                                View.VISIBLE);
                                                                        uploadmediastatus.setText(
                                                                                getString(R.string.done));
                                                                        uploadmediastatus.setTextColor(
                                                                                Color.BLUE);
                                                                    } else {
                                                                        uploadmediastatus.setVisibility(
                                                                                View.VISIBLE);
                                                                        uploadmediastatus.setText(getString(
                                                                                R.string.pending));
                                                                        uploadmediastatus.setTextColor(
                                                                                Color.RED);
                                                                    }
                                                                }
                                                            }
                                                            if (success) {
                                                                utils.toast(2, 2, getString(
                                                                        R.string.file_deleted));

                                                            }
                                                            arg0.dismiss();
                                                            dialog.dismiss();
                                                            addWashroomImages();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                        alertbox.setNegativeButton(getString(R.string.no),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        arg0.dismiss();
                                                    }
                                                });
                                        alertbox.show();
                                        //
                                    }
                                });


                                try {
                                    mOriginal.setImageBitmap(washroombitmaps.get(mPosition));
                                    //, Config.intWidth, Config.intHeight)
                                } catch (OutOfMemoryError oOm) {
                                    oOm.printStackTrace();
                                }
                                dialog.setCancelable(true);

                                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT); //Controlling width and height.
                                dialog.show();

                            }
                        }
                    });

                    layoutwashroom.addView(imageView);
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }

        } else {
            washroomstatus.setVisibility(View.VISIBLE);
            washroomstatus.setText(getString(R.string.pending));
            washroomstatus.setTextColor(Color.RED);
            if (hallstatus.getText().toString().equals(getString(R.string.done))
                    && washroomstatus.getText().toString().equals(getString(R.string.done))
                    && kitchenstatus.getText().toString().equals(getString(R.string.done))
                    && bedroomstatus.getText().toString().equals(getString(R.string.done))) {

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText(getString(R.string.done));
                uploadmediastatus.setTextColor(Color.BLUE);
            } else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText(getString(R.string.pending));
                uploadmediastatus.setTextColor(Color.RED);
            }

        }
    }

    private void addBedroomImages() {

        layoutbedroom.removeAllViews();

        if (bedroomimageModels != null && bedroomimageModels.size() > 0) {

            bedroomstatus.setVisibility(View.VISIBLE);
            bedroomstatus.setText(getString(R.string.done));
            bedroomstatus.setTextColor(Color.WHITE);
            if (hallstatus.getText().toString().equals("Done")
                    && kitchenstatus.getText().toString().equals("Done")
                    && washroomstatus.getText().toString().equals("Done")) {

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText(getString(R.string.done));
                uploadmediastatus.setTextColor(Color.BLUE);
            } else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText(getString(R.string.pending));
                uploadmediastatus.setTextColor(Color.RED);
            }

            for (int i = 0; i < bedroomimageModels.size(); i++) {
                try {
                    final ImageView imageView = new ImageView(CheckInCareActivity.this);
                    imageView.setPadding(0, 0, 3, 0);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(10, 10, 10, 10);


                    imageView.setLayoutParams(layoutParams);


                    if (bedroombitmaps.size() > 0 && i < bedroombitmaps.size()) {
                        imageView.setImageBitmap(bedroombitmaps.get(i));
                    }

                    imageView.setTag(bedroomimageModels.get(i));
                    imageView.setTag(R.id.three, i);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);


                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (isBedflag==1) {
                                final ImageModel mImageModel = (ImageModel) v.getTag();

                                final int mPosition = (int) v.getTag(R.id.three);

                                final Dialog dialog = new Dialog(CheckInCareActivity.this);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                                dialog.setContentView(R.layout.image_dialog_layout);

                                TouchImageView mOriginal = (TouchImageView) dialog.findViewById(
                                        R.id.imgOriginal);
                                TextView textViewClose = (TextView) dialog.findViewById(
                                        R.id.textViewClose);
                                Button buttonDelete = (Button) dialog.findViewById(
                                        R.id.textViewTitle);

                                if (isCompleted)
                                    buttonDelete.setVisibility(View.INVISIBLE);


                                textViewClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //mOriginal.
                                        dialog.dismiss();
                                    }
                                });

                                buttonDelete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        //
                                        final AlertDialog.Builder alertbox =
                                                new AlertDialog.Builder(CheckInCareActivity.this);
                                        alertbox.setTitle(getString(R.string.delete_image));
                                        alertbox.setMessage(getString(R.string.confirm_delete_image));
                                        alertbox.setPositiveButton(getString(R.string.yes),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface arg0, int arg1) {

                                                        try {
                                                            File fDelete = utils.getInternalFileImages
                                                                    (mImageModel.getStrImageName());

                                                            if (fDelete.exists()) {
                                                                success = fDelete.delete();

                                                                if (mImageModel.ismIsNew())
                                                                    bedroomImageCount--;
                                                                    Bedroom_IMAGE_COUNT--;

                                                                mImageChanged = true;

                                                                bedroomimageModels.remove(mImageModel);

                                                                // bedroombitmaps.remove(mPosition);

                                                                if (bedroombitmaps.size() > 0 && mPosition < bedroombitmaps.size()) {
                                                                    bedroombitmaps.remove(mPosition);
                                                                }


                                                                if (bedroomImageCount < 1) {
                                                                    //  uploadbedroombtn.setVisibility(View.GONE);
                                                                    bedroomstatus.setVisibility(View.VISIBLE);
                                                                    bedroomstatus.setText(getString(
                                                                            R.string.pending));
                                                                    bedroomstatus.setTextColor(Color.RED);
                                                                    if (hallstatus.getText().toString().
                                                                            equals(getString(R.string.done))
                                                                            && bedroomstatus.getText().
                                                                            toString().equals(getString(
                                                                            R.string.done))
                                                                            && kitchenstatus.getText().
                                                                            toString().equals(getString(
                                                                            R.string.done))
                                                                            && washroomstatus.getText().
                                                                            toString().equals(getString(
                                                                            R.string.done))) {

                                                                        uploadmediastatus.
                                                                                setVisibility(View.VISIBLE);
                                                                        uploadmediastatus.
                                                                                setText(getString(R.string.done));
                                                                        uploadmediastatus.
                                                                                setTextColor(Color.BLUE);
                                                                    } else {
                                                                        uploadmediastatus.setVisibility(
                                                                                View.VISIBLE);
                                                                        uploadmediastatus.setText
                                                                                (getString(R.string.pending));
                                                                        uploadmediastatus.setTextColor(
                                                                                Color.RED);
                                                                    }

                                                                }
                                                            }
                                                            if (success) {
                                                                utils.toast(2, 2, getString(R.string.
                                                                        file_deleted));

                                                            }
                                                            arg0.dismiss();
                                                            dialog.dismiss();
                                                            addBedroomImages();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                        alertbox.setNegativeButton(getString(R.string.no),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        arg0.dismiss();
                                                    }
                                                });
                                        alertbox.show();
                                        //
                                    }
                                });


                                try {
                                    mOriginal.setImageBitmap(bedroombitmaps.get(mPosition));
                                    //, Config.intWidth, Config.intHeight)
                                } catch (OutOfMemoryError oOm) {
                                    oOm.printStackTrace();
                                }
                                dialog.setCancelable(true);

                                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT); //Controlling width and height.
                                dialog.show();

                            }
                        }
                    });

                    layoutbedroom.addView(imageView);
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }

        } else {
            bedroomstatus.setVisibility(View.VISIBLE);
            bedroomstatus.setText(getString(R.string.pending));
            bedroomstatus.setTextColor(Color.RED);
            if (hallstatus.getText().toString().equals(getString(R.string.done))
                    && bedroomstatus.getText().toString().equals(getString(R.string.done))
                    && kitchenstatus.getText().toString().equals(getString(R.string.done))
                    && washroomstatus.getText().toString().equals(getString(R.string.done))) {

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText(getString(R.string.done));
                uploadmediastatus.setTextColor(Color.BLUE);
            } else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText(getString(R.string.pending));
                uploadmediastatus.setTextColor(Color.RED);
            }


        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        item = parent.getItemAtPosition(position).toString();

        switch (parent.getId()) {
            case R.id.spinner:

                items[0] = item;

                if (item.equals("Y")) {
                    //  utils.toast(2, 2, getString(R.string.select_date));
                    waterstatus.setText(getString(R.string.yes));
                    txtwater.setText("Due Date");
                    pick_date.setImageResource(R.drawable.calender_date_picked);
                    if (grocerystatus.getText().toString().equals(getString(R.string.done))
                            && kitchenequipmentstatus.getText().toString().
                            equals(getString(R.string.done))
                            && gasstatus.getText().toString().equals(getString(R.string.yes))
                            && electricitystatus.getText().toString().
                            equals(getString(R.string.yes))
                            && telephonestatus.getText().toString().
                            equals(getString(R.string.yes))) {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText(getString(R.string.done));
                        homeessentialstatus.setTextColor(Color.BLUE);
                    } else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText(getString(R.string.pending));
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                } else {
                    waterstatus.setText(getString(R.string.no));
                    pick_date.setImageResource(R.mipmap.calendar_date_picker);
                    if (grocerystatus.getText().toString().equals(getString(R.string.done))
                            && kitchenequipmentstatus.getText().toString().
                            equals(getString(R.string.done))
                            && waterstatus.getText().toString().equals(getString(R.string.yes))
                            && gasstatus.getText().toString().equals(getString(R.string.yes))
                            && electricitystatus.getText().toString().
                            equals(getString(R.string.yes))
                            && telephonestatus.getText().toString().
                            equals(getString(R.string.yes))) {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText(getString(R.string.done));
                        homeessentialstatus.setTextColor(Color.BLUE);
                    } else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText(getString(R.string.pending));
                        homeessentialstatus.setTextColor(Color.RED);
                    }

                }

                break;

            case R.id.spinner1:

                items[1] = item;

                if (item.equals("Y")) {

                    gasstatus.setText(getString(R.string.yes));
                    txtgas.setText("Due Date");
                    pick_date2.setImageResource(R.drawable.calender_date_picked);
                    if (grocerystatus.getText().toString().equals(getString(R.string.done))
                            && kitchenequipmentstatus.getText().toString().
                            equals(getString(R.string.done))
                            && waterstatus.getText().toString().equals(getString(R.string.yes))
                            && electricitystatus.getText().toString().
                            equals(getString(R.string.yes))
                            && telephonestatus.getText().toString().
                            equals(getString(R.string.yes))) {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText(getString(R.string.done));
                        homeessentialstatus.setTextColor(Color.BLUE);
                    } else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText(getString(R.string.pending));
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                } else {
                    gasstatus.setText(getString(R.string.no));
                    pick_date2.setImageResource(R.mipmap.calendar_date_picker);
                    if (grocerystatus.getText().toString().equals(getString(R.string.done))
                            && kitchenequipmentstatus.getText().toString().
                            equals(getString(R.string.done))
                            && waterstatus.getText().toString().equals(getString(R.string.yes))
                            && gasstatus.getText().toString().equals(getString(R.string.yes))
                            && electricitystatus.getText().toString().
                            equals(getString(R.string.yes))
                            && telephonestatus.getText().toString().
                            equals(getString(R.string.yes))) {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText(getString(R.string.done));
                        homeessentialstatus.setTextColor(Color.BLUE);
                    } else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText(getString(R.string.pending));
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                }

                break;

            case R.id.spinner2:

                items[2] = item;

                if (item.equals("Y")) {

                    electricitystatus.setText(getString(R.string.yes));
                    txtelectricity.setText("Due Date");
                    pick_date3.setImageResource(R.drawable.calender_date_picked);
                    if (grocerystatus.getText().toString().equals(getString(R.string.done))
                            && kitchenequipmentstatus.getText().toString().
                            equals(getString(R.string.done))
                            && gasstatus.getText().toString().equals(getString(R.string.yes))
                            && waterstatus.getText().toString().equals(getString(R.string.yes))
                            && telephonestatus.getText().toString().
                            equals(getString(R.string.yes))) {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText(getString(R.string.done));
                        homeessentialstatus.setTextColor(Color.BLUE);
                    } else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText(getString(R.string.pending));
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                } else {
                    electricitystatus.setText(getString(R.string.no));
                    pick_date3.setImageResource(R.mipmap.calendar_date_picker);
                    if (grocerystatus.getText().toString().equals(getString(R.string.done))
                            && kitchenequipmentstatus.getText().toString().
                            equals(getString(R.string.done))
                            && gasstatus.getText().toString().equals(getString(R.string.yes))
                            && waterstatus.getText().toString().equals(getString(R.string.yes))
                            && electricitystatus.getText().toString().
                            equals(getString(R.string.yes))
                            && telephonestatus.getText().toString().
                            equals(getString(R.string.yes))) {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText(getString(R.string.done));
                        homeessentialstatus.setTextColor(Color.BLUE);
                    } else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText(getString(R.string.pending));
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                }

                break;


            case R.id.spinner3:

                items[3] = item;

                if (item.equals("Y")) {

                    telephonestatus.setText(getString(R.string.yes));
                    txttelephone.setText("Due Date");
                    pick_date4.setImageResource(R.drawable.calender_date_picked);
                    if (grocerystatus.getText().toString().equals(getString(R.string.done))
                            && kitchenequipmentstatus.getText().toString().
                            equals(getString(R.string.done))
                            && gasstatus.getText().toString().equals(getString(R.string.yes))
                            && electricitystatus.getText().toString().
                            equals(getString(R.string.yes))
                            && waterstatus.getText().toString().equals(getString(R.string.yes))) {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText(getString(R.string.done));
                        homeessentialstatus.setTextColor(Color.BLUE);
                    } else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText(getString(R.string.pending));
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                } else {
                    telephonestatus.setText(getString(R.string.no));
                    pick_date4.setImageResource(R.mipmap.calendar_date_picker);
                    if (grocerystatus.getText().toString().equals(getString(R.string.done))
                            && kitchenequipmentstatus.getText().toString().
                            equals(getString(R.string.done))
                            && gasstatus.getText().toString().equals(getString(R.string.yes))
                            && electricitystatus.getText().toString().
                            equals(getString(R.string.yes))
                            && waterstatus.getText().toString().equals(getString(R.string.yes))
                            && telephonestatus.getText().toString().
                            equals(getString(R.string.yes))) {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText(getString(R.string.done));
                        homeessentialstatus.setTextColor(Color.BLUE);
                    } else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText(getString(R.string.pending));
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                }

                break;

            case R.id.dependentspinner:

                dependentId = dependent.get(position).getStrDependentID();

                break;

        }


        if (waterstatus.getText().toString().equals(getString(R.string.yes))
                && gasstatus.getText().toString().equals(getString(R.string.yes))
                && electricitystatus.getText().toString().equals(getString(R.string.yes))
                && telephonestatus.getText().toString().equals(getString(R.string.yes))) {
            utilitystatus.setVisibility(View.VISIBLE);
            utilitystatus.setText(getString(R.string.done));
            utilitystatus.setTextColor(Color.BLUE);
        } else {
            utilitystatus.setVisibility(View.VISIBLE);
            utilitystatus.setText(getString(R.string.pending));
            utilitystatus.setTextColor(Color.RED);

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void selectImage() {

        permissionHelper.verifyPermission(
                new String[]{getString(R.string.access_storage)},
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new PermissionCallback() {
                    @Override
                    public void permissionGranted() {
                        //action to perform when permission granteed
                        isAllowed = true;

                        utils.selectImage(String.valueOf(new Date().getDate() + ""
                                + new Date().getTime())
                                + ".jpeg", null, CheckInCareActivity.this, false);
                    }

                    @Override
                    public void permissionRefused() {
                        //action to perform when permission refused
                        isAllowed = false;
                    }
                }
        );
    }

    @Override
    protected void onResume() {

        super.onResume();


        utils = new Utils(CheckInCareActivity.this);

        try {


            if (buttonHallAdd != null) {
                buttonHallAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isFlag = 1;
                        mainlinearlayout.requestFocus();
                        if(isHallflag ==2){
                            utils.toast(2, 2, getString(R.string.upload_hall_success));

                        }else {

                            if (Hall_IMAGE_COUNT < 4) {

                                selectImage();
                            } else {
                                utils.toast(2, 2, "Maximum 4 Images only Allowed");
                            }
                        }
                    }
                });
            }

            if (buttonKitchenAdd != null) {
                buttonKitchenAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isFlag = 2;
                        mainlinearlayout.requestFocus();
                        if(isKitchenflag==2){
                            utils.toast(2, 2, getString(R.string.upload_kitchen_success));

                        }else {
                            if (Kitchen_IMAGE_COUNT < 4) {

                                selectImage();
                            } else {
                                utils.toast(2, 2, "Maximum 4 Images only Allowed");
                            }
                        }
                        }
                });
            }
            if (buttonWashroomAdd != null) {
                buttonWashroomAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isFlag = 3;
                        mainlinearlayout.requestFocus();
                        if(isWashflag==2){
                            utils.toast(2, 2, getString(R.string.upload_wash_success));

                        }else {
                            if (Washroom_IMAGE_COUNT < 4) {

                                selectImage();
                            } else {
                                utils.toast(2, 2, "Maximum 4 Images only Allowed");
                            }
                        }
                        }
                });
            }
            if (buttonBedroomAdd != null) {
                buttonBedroomAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isFlag = 4;
                        mainlinearlayout.requestFocus();
                        if(isBedflag==2){
                            utils.toast(2, 2, getString(R.string.upload_bed_success));

                        }else {
                            if (Bedroom_IMAGE_COUNT < 4) {

                                selectImage();
                            } else {
                                utils.toast(2, 2, "Maximum 4 Images only Allowed");
                            }
                        }
                    }
                });
            }


            if (!bViewLoaded) {

               bViewLoaded =true;

                loadingPanel.setVisibility(View.VISIBLE);

                if (Utils.isConnectingToInternet(CheckInCareActivity.this)) {
                    backgroundThreadHandlerFetch = new BackgroundThreadHandlerFetchImages();
                    Thread backgroundThreadImagesFetch = new BackgroundThreadFetchImages();
                    backgroundThreadImagesFetch.start();
                } else {
                    backgroundThreadHandler = new BackgroundThreadHandler();
                    Thread backgroundThreadImages = new BackgroundThreadImages();
                    backgroundThreadImages.start();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (hallstatus.getText().equals(getString(R.string.done))
                && kitchenstatus.getText().equals(getString(R.string.done))
                && washroomstatus.getText().equals(getString(R.string.done))
                && bedroomstatus.getText().equals(getString(R.string.done))) {

            uploadmediastatus.setVisibility(View.VISIBLE);
            uploadmediastatus.setText(getString(R.string.done));
            uploadmediastatus.setTextColor(Color.BLUE);
        } else {
            uploadmediastatus.setVisibility(View.VISIBLE);
            uploadmediastatus.setText(getString(R.string.pending));
            uploadmediastatus.setTextColor(Color.RED);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void sendPush(JSONObject jsonObject) {

        if (utils.isConnectingToInternet()) {

            PushNotificationService pushNotificationService = new PushNotificationService(
                    CheckInCareActivity.this);

            if (!strCustomerEmail.equalsIgnoreCase("")) {

                pushNotificationService.sendPushToUser(strCustomerEmail, jsonObject.toString(),
                        new App42CallBack() {

                            @Override
                            public void onSuccess(Object o) {
                                if (o != null)
                                    Utils.log(o.toString(), " PUSH2 1");
                            }

                            @Override
                            public void onException(Exception ex) {
                                if (ex != null)
                                    Utils.log(ex.getMessage(), " PUSH2 0");
                            }
                        });
            }
        }
    }

    private void insertNotification(final JSONObject jsonObject) {

        if (utils.isConnectingToInternet()) {

            storageService.insertDocs(Config.collectionNotification, jsonObject,
                    new AsyncApp42ServiceApi.App42StorageServiceListener() {

                        @Override
                        public void onDocumentInserted(Storage response) {
                            try {
                                if (response.isResponseSuccess()) {
                                    //sendPush(jsonObject);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onUpdateDocSuccess(Storage response) {
                        }

                        @Override
                        public void onFindDocSuccess(Storage response) {
                        }

                        @Override
                        public void onInsertionFailed(App42Exception ex) {
                        }

                        @Override
                        public void onFindDocFailed(App42Exception ex) {
                        }

                        @Override
                        public void onUpdateDocFailed(App42Exception ex) {
                        }
                    });
        }
    }

    private void hideSoftKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            if (isFlag == 1) {
                addHallImages();
            }else{
                addHallImages();
            }

            if (isFlag == 2) {
                addKitchenImages();
            }else{
                addKitchenImages();
            }

            if (isFlag == 3) {
                addWashroomImages();
            }else{
                addWashroomImages();
            }


            if (isFlag == 4) {
                addBedroomImages();
            }else{
                addBedroomImages();
            }

            loadingPanel.setVisibility(View.GONE);

        }
    }

    private class BackgroundThread extends Thread {
        @Override
        public void run() {

            if (isFlag == 1) {
                try {
                    for (int i = 0; i < imagePaths.size(); i++) {
                        Calendar calendar = Calendar.getInstance();
                        String strTime = String.valueOf(calendar.getTimeInMillis());
                        String strFileName = strTime + ".jpeg";

                        Date date = new Date();

                        File mCopyFile = utils.getInternalFileImages(strFileName);
                        utils.copyFile(new File(imagePaths.get(i)), mCopyFile);

                        ImageModel imageModel = new ImageModel(strFileName, "", strFileName,
                                Utils.convertDateToString(date), mCopyFile.getAbsolutePath());

                        imageModel.setmIsNew(true);

                        hallimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(),
                                Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                        hallbitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(),
                                Config.intWidth, Config.intHeight));

                        Hall_IMAGE_COUNT++;

                        hallImageCount++;
                    }
                    backgroundThreadHandler.sendEmptyMessage(0);
                } catch (OutOfMemoryError | Exception e) {
                    e.printStackTrace();
                }
            }
            if (isFlag == 2) {
                try {
                    for (int i = 0; i < imagePaths.size(); i++) {
                        Calendar calendar = Calendar.getInstance();
                        String strTime = String.valueOf(calendar.getTimeInMillis());
                        String strFileName = strTime + ".jpeg";

                        Date date = new Date();

                        File mCopyFile = utils.getInternalFileImages(strFileName);
                        utils.copyFile(new File(imagePaths.get(i)), mCopyFile);

                        ImageModel imageModel = new ImageModel(strFileName, "", strFileName,
                                Utils.convertDateToString(date), mCopyFile.getAbsolutePath());

                        imageModel.setmIsNew(true);

                        kitchenimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(),
                                Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);


                        kitchenbitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(),
                                Config.intWidth, Config.intHeight));

                        Kitchen_IMAGE_COUNT++;

                        kitchenImageCount++;
                    }
                    backgroundThreadHandler.sendEmptyMessage(0);
                } catch (OutOfMemoryError | Exception e) {
                    e.printStackTrace();
                }
            }
            if (isFlag == 3) {
                try {
                    for (int i = 0; i < imagePaths.size(); i++) {
                        Calendar calendar = Calendar.getInstance();
                        String strTime = String.valueOf(calendar.getTimeInMillis());
                        String strFileName = strTime + ".jpeg";

                        Date date = new Date();

                        File mCopyFile = utils.getInternalFileImages(strFileName);
                        utils.copyFile(new File(imagePaths.get(i)), mCopyFile);

                        ImageModel imageModel = new ImageModel(strFileName, "", strFileName,
                                Utils.convertDateToString(date), mCopyFile.getAbsolutePath());

                        imageModel.setmIsNew(true);

                        washroomimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(),
                                Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);


                        washroombitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(),
                                Config.intWidth, Config.intHeight));

                        Washroom_IMAGE_COUNT++;

                        washroomImageCount++;
                    }
                    backgroundThreadHandler.sendEmptyMessage(0);
                } catch (OutOfMemoryError | Exception e) {
                    e.printStackTrace();
                }
            }
            if (isFlag == 4) {
                try {
                    for (int i = 0; i < imagePaths.size(); i++) {
                        Calendar calendar = Calendar.getInstance();
                        String strTime = String.valueOf(calendar.getTimeInMillis());
                        String strFileName = strTime + ".jpeg";

                        Date date = new Date();

                        File mCopyFile = utils.getInternalFileImages(strFileName);
                        utils.copyFile(new File(imagePaths.get(i)), mCopyFile);

                        ImageModel imageModel = new ImageModel(strFileName, "", strFileName,
                                Utils.convertDateToString(date), mCopyFile.getAbsolutePath());

                        imageModel.setmIsNew(true);

                        bedroomimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(),
                                Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                        bedroombitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(),
                                Config.intWidth, Config.intHeight));

                        Bedroom_IMAGE_COUNT++;

                        bedroomImageCount++;
                    }
                    backgroundThreadHandler.sendEmptyMessage(0);
                } catch (OutOfMemoryError | Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class BackgroundThreadCamera extends Thread {
        @Override
        public void run() {
            if (isFlag == 1) {
                try {
                    if (strImageName != null && !strImageName.equalsIgnoreCase("")) {

                        Calendar calendar = Calendar.getInstance();
                        String strName = String.valueOf(calendar.getTimeInMillis());
                        String strFileName = strName + ".jpeg";

                        File mCopyFile = utils.getInternalFileImages(strFileName);

                        utils.copyFile(new File(strImageName), mCopyFile);

                        Date date = new Date();
                        ImageModel imageModel = new ImageModel(strFileName, "", strFileName,
                                Utils.convertDateToString(date), mCopyFile.getAbsolutePath());
                        imageModel.setmIsNew(true);

                        hallimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(),
                                Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                        hallbitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(),
                                Config.intWidth, Config.intHeight));

                        hallImageCount++;

                        Hall_IMAGE_COUNT++;
                    }

                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
            if (isFlag == 2) {
                try {
                    if (strImageName != null && !strImageName.equalsIgnoreCase("")) {
                        Calendar calendar = Calendar.getInstance();
                        String strName = String.valueOf(calendar.getTimeInMillis());
                        String strFileName = strName + ".jpeg";

                        File mCopyFile = utils.getInternalFileImages(strFileName);

                        utils.copyFile(new File(strImageName), mCopyFile);

                        Date date = new Date();
                        ImageModel imageModel = new ImageModel(strFileName, "", strFileName,
                                Utils.convertDateToString(date), mCopyFile.getAbsolutePath());
                        imageModel.setmIsNew(true);

                        kitchenimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(),
                                Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                        kitchenbitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(),
                                Config.intWidth, Config.intHeight));

                        kitchenImageCount++;

                        Kitchen_IMAGE_COUNT++;
                    }

                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
            if (isFlag == 3) {
                try {
                    if (strImageName != null && !strImageName.equalsIgnoreCase("")) {
                        Calendar calendar = Calendar.getInstance();
                        String strName = String.valueOf(calendar.getTimeInMillis());
                        String strFileName = strName + ".jpeg";

                        File mCopyFile = utils.getInternalFileImages(strFileName);

                        utils.copyFile(new File(strImageName), mCopyFile);

                        Date date = new Date();
                        ImageModel imageModel = new ImageModel(strFileName, "", strFileName,
                                Utils.convertDateToString(date), mCopyFile.getAbsolutePath());
                        imageModel.setmIsNew(true);

                        washroomimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(),
                                Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                        washroombitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(),
                                Config.intWidth, Config.intHeight));

                        washroomImageCount++;

                        Washroom_IMAGE_COUNT++;
                    }

                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
            if (isFlag == 4) {
                try {
                    if (strImageName != null && !strImageName.equalsIgnoreCase("")) {
                        Calendar calendar = Calendar.getInstance();
                        String strName = String.valueOf(calendar.getTimeInMillis());
                        String strFileName = strName + ".jpeg";

                        File mCopyFile = utils.getInternalFileImages(strFileName);

                        utils.copyFile(new File(strImageName), mCopyFile);

                        Date date = new Date();
                        ImageModel imageModel = new ImageModel(strFileName, "", strFileName,
                                Utils.convertDateToString(date), mCopyFile.getAbsolutePath());
                        imageModel.setmIsNew(true);

                        bedroomimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(),
                                Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                        bedroombitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(),
                                Config.intWidth, Config.intHeight));

                        bedroomImageCount++;

                        Bedroom_IMAGE_COUNT++;
                    }

                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
            backgroundThreadHandler.sendEmptyMessage(0);
        }
    }

    private class BackgroundThreadImages extends Thread {
        @Override
        public void run() {

            if(editcheckincare) {

                try {
                    for (ImageModel imageModel : hallimageModels) {
                        if (imageModel.getStrImageUrl() != null
                                && !imageModel.getStrImageUrl().equalsIgnoreCase("")) {
                            hallbitmaps.add(utils.getBitmapFromFile(
                                    utils.getInternalFileImages(imageModel.getStrImageDesc()).
                                            getAbsolutePath(), Config.intWidth, Config.intHeight));

                            imageModel.setmIsNew(false);

                            Hall_IMAGE_COUNT++;
                        }
                    }
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }


                try {

                    for (ImageModel imageModel : kitchenimageModels) {
                        if (imageModel.getStrImageUrl() != null && !imageModel.getStrImageUrl().
                                equalsIgnoreCase("")) {
                            kitchenbitmaps.add(utils.getBitmapFromFile(utils.
                                    getInternalFileImages(imageModel.getStrImageDesc()).
                                    getAbsolutePath(), Config.intWidth, Config.intHeight));

                            imageModel.setmIsNew(false);

                            Kitchen_IMAGE_COUNT++;
                        }
                    }
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }


                try {

                    for (ImageModel imageModel : washroomimageModels) {
                        if (imageModel.getStrImageUrl() != null && !imageModel.getStrImageUrl().
                                equalsIgnoreCase("")) {
                            washroombitmaps.add(utils.getBitmapFromFile(utils.
                                    getInternalFileImages(imageModel.getStrImageDesc()).
                                    getAbsolutePath(), Config.intWidth, Config.intHeight));

                            imageModel.setmIsNew(false);

                            Washroom_IMAGE_COUNT++;
                        }
                    }
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }


                try {

                    for (ImageModel imageModel : bedroomimageModels) {
                        if (imageModel.getStrImageUrl() != null && !imageModel.getStrImageUrl().
                                equalsIgnoreCase("")) {
                            bedroombitmaps.add(utils.getBitmapFromFile(utils.
                                    getInternalFileImages(imageModel.getStrImageDesc()).
                                    getAbsolutePath(), Config.intWidth, Config.intHeight));

                            imageModel.setmIsNew(false);

                            Bedroom_IMAGE_COUNT++;
                        }
                    }
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
            backgroundThreadHandler.sendEmptyMessage(0);
        }
    }

    private class BackgroundThreadFetchImages extends Thread {
        @Override
        public void run() {

            if (editcheckincare) {

                try {
                    for (ImageModel imageModel : hallimageModels) {
                        if (imageModel.getStrImageUrl() != null
                                && !imageModel.getStrImageUrl().equalsIgnoreCase("")) {

                            File file = utils.getInternalFileImages(imageModel.getStrImageDesc());

                            if (!file.exists() || file.length() <= 0) {
                                Utils.loadImageFromWeb(imageModel.getStrImageDesc(),
                                        imageModel.getStrImageUrl());
                            }
                        }
                    }
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }


                try {

                    for (ImageModel imageModel : kitchenimageModels) {
                        if (imageModel.getStrImageUrl() != null && !imageModel.getStrImageUrl().
                                equalsIgnoreCase("")) {
                            File file = utils.getInternalFileImages(imageModel.getStrImageDesc());

                            if (!file.exists() || file.length() <= 0) {
                                Utils.loadImageFromWeb(imageModel.getStrImageDesc(),
                                        imageModel.getStrImageUrl());
                            }
                        }
                    }
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }


                try {

                    for (ImageModel imageModel : washroomimageModels) {
                        if (imageModel.getStrImageUrl() != null && !imageModel.getStrImageUrl().
                                equalsIgnoreCase("")) {
                            File file = utils.getInternalFileImages(imageModel.getStrImageDesc());

                            if (!file.exists() || file.length() <= 0) {
                                Utils.loadImageFromWeb(imageModel.getStrImageDesc(),
                                        imageModel.getStrImageUrl());
                            }
                        }
                    }
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }


                try {

                    for (ImageModel imageModel : bedroomimageModels) {
                        if (imageModel.getStrImageUrl() != null && !imageModel.getStrImageUrl().
                                equalsIgnoreCase("")) {
                            File file = utils.getInternalFileImages(imageModel.getStrImageDesc());

                            if (!file.exists() || file.length() <= 0) {
                                Utils.loadImageFromWeb(imageModel.getStrImageDesc(),
                                        imageModel.getStrImageUrl());
                            }
                        }
                    }
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
            backgroundThreadHandlerFetch.sendEmptyMessage(0);
        }
    }

    private class BackgroundThreadHandlerFetchImages extends Handler {
        @Override
        public void handleMessage(Message msg) {
            backgroundThreadHandler = new BackgroundThreadHandler();
            Thread backgroundThreadImages = new BackgroundThreadImages();
            backgroundThreadImages.start();
        }
    }


}
