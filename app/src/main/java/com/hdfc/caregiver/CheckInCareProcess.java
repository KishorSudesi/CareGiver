package com.hdfc.caregiver;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UploadService;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.libs.Utils;
import com.hdfc.models.ImageModel;
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
import java.util.Collections;
import java.util.Date;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Admin on 01-07-2016.
 */
public class CheckInCareProcess extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    public static final int DIALOG_DOWNLOAD_PROGRESS1 = 1;
    private static final String[] option = {"N", "Y"};
    public static String strImageName = "", strClientName = "";
    public static Uri uri;
    public static Bitmap bitmap = null;
    public static int IMAGE_COUNT = 0;
    private static Utils utils;
    private static ProgressDialog mProgress = null;
    private static Handler backgroundThreadHandler;
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
    private static String strName;
    private static boolean bLoad, isCompleted = false;
    private static boolean bViewLoaded, mImageChanged;
    private static StorageService storageService;
    public String item = "";
    int isClicked = 0;
    int isHallFlag = 0;
    private boolean isClick = false;
    private ImageView pick_date, pick_date2, pick_date3, pick_date4;
    private ImageView client;
    private Spinner spinner, spinner1, spinner2, spinner3;
    private Button btn_submit, btn_close, buttonHallAdd, buttonKitchenAdd, buttonWashroomAdd, buttonBedroomAdd;
    private EditText electronic, homeapplience, automobile, maidservices, kitchen_equipments, grocery, mediacomment, checkincarename;
    private TextView datetxt, txtwater, txtgas, txtelectricity, txttelephone, clientnametxt;
    private TextView utilitystatus, waterstatus, gasstatus, electricitystatus, telephonestatus,
            equipmentstatus, grocerystatus, kitchenequipmentstatus, domestichelpstatus, uploadmediastatus, hallstatus,
            kitchenstatus, washroomstatus, bedroomstatus, homeessentialstatus;
    private String strwaterDate, strelectricityDate, strtelephoneDate, strgasDate, _strwaterDate,
            _strelectricityDate, _strtelephoneDate, _strgasDate, strDate, _strDate;
    private ProgressDialog progressDialog;
    private int hallImageCount, kitchenImageCount, washroomImageCount,
            bedroomImageCount, hallImageUploadCount, kitchenImageUploadCount,
            washroomImageUploadCount, bedroomImageUploadCount;
    private boolean success;
    private MultiBitmapLoader multiBitmapLoader;
    private LinearLayout layouthall, layoutkitchen, layoutwashroom, layoutbedroom, mainlinearlayout;
    private CheckBox electrocheck, homecheck, autocheck, kitchenequipcheck, grocerycheck, domesticcheck;
    private String valkitchen, valgrocery, valelectronic, valhomeapplience, valautomobile, valmaidservices, valmediacomment, valcheckincarename;
    private View focusView = null;
    private ProgressDialog mProgressDialog;
    private String items[];

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+
            // date.getTime();
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.

            strDate = Utils.writeFormatDateMY.format(date);

            strwaterDate = Utils.writeFormat.format(date);
            strelectricityDate = Utils.writeFormat.format(date);
            strtelephoneDate = Utils.writeFormat.format(date);
            strgasDate = Utils.writeFormat.format(date);

            _strDate = Utils.readFormat.format(date);

            _strwaterDate = Utils.readFormat.format(date);
            _strelectricityDate = Utils.readFormat.format(date);
            _strtelephoneDate = Utils.readFormat.format(date);
            _strgasDate = Utils.readFormat.format(date);

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
            if (isClicked == 4) {
                datetxt.setText(strDate);
            }
        }

        @Override
        public void onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }

    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    //display progress dialog
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS1:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Processing request, Please wait ...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();

                return mProgressDialog;

            default:
                return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in_care);

        mainlinearlayout = (LinearLayout) findViewById(R.id.mainlinearlayout);

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

        storageService = new StorageService(CheckInCareProcess.this);

        layouthall = (LinearLayout) findViewById(R.id.linear_hall);
        layoutkitchen = (LinearLayout) findViewById(R.id.linear_kitchen);
        layoutwashroom = (LinearLayout) findViewById(R.id.linear_washroom);
        layoutbedroom = (LinearLayout) findViewById(R.id.linear_bedroom);
        multiBitmapLoader = new MultiBitmapLoader(CheckInCareProcess.this);


        utils = new Utils(CheckInCareProcess.this);
        progressDialog = new ProgressDialog(CheckInCareProcess.this);
        mProgress = new ProgressDialog(CheckInCareProcess.this);

        Date mydate = new Date();
        strDate = Utils.writeFormatDateMY.format(mydate);
        String stDate = Utils.queryFormatday.format(mydate);
        datetxt = (TextView) findViewById(R.id.datetxt);
        datetxt.setText(strDate);

        LinearLayout layoutDate = (LinearLayout) findViewById(R.id.linearDate);

        datetxt.setText(strDate);

        layoutDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClicked = 4;
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        });

        checkincarename = (EditText) findViewById(R.id.checkincarename);
        checkincarename.setText(stDate);
        checkincarename.setTextSize(15);

        electrocheck = (CheckBox) findViewById(R.id.electrocheck);
        homecheck = (CheckBox) findViewById(R.id.homecheck);
        autocheck = (CheckBox) findViewById(R.id.autocheck);
        kitchenequipcheck = (CheckBox) findViewById(R.id.kitchenequipcheck);
        grocerycheck = (CheckBox) findViewById(R.id.grocerycheck);
        domesticcheck = (CheckBox) findViewById(R.id.domesticcheck);

        client = (ImageView) findViewById(R.id.clientimg);
        pick_date = (ImageView) findViewById(R.id.pick_date);
        pick_date2 = (ImageView) findViewById(R.id.pick_date2);
        pick_date3 = (ImageView) findViewById(R.id.pick_date3);
        pick_date4 = (ImageView) findViewById(R.id.pick_date4);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner3 = (Spinner) findViewById(R.id.spinner3);

        buttonHallAdd = (Button) findViewById(R.id.buttonHallAdd);
        buttonKitchenAdd = (Button) findViewById(R.id.buttonKitchenAdd);
        buttonWashroomAdd = (Button) findViewById(R.id.buttonWashroomAdd);
        buttonBedroomAdd = (Button) findViewById(R.id.buttonBedroomAdd);

        txtwater = (TextView) findViewById(R.id.water_propertytxt);
        txtgas = (TextView) findViewById(R.id.gastxt);
        txtelectricity = (TextView) findViewById(R.id.electricitytxt);
        txttelephone = (TextView) findViewById(R.id.telephonetxt);
        clientnametxt = (TextView) findViewById(R.id.clientnametxt);

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

      /*  mediacomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hallimageModels.size() > 0
                        || kitchenimageModels.size() > 0
                        || washroomimageModels.size() > 0
                        || bedroomimageModels.size() > 0) {

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mediacomment, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    mediacomment.setFocusable(false);
                    mediacomment.setFocusableInTouchMode(false);
                }
            }
        });*/


        if (Config.customerModel != null) {
            strClientName = Config.customerModel.getStrName();
            //strImageName = Config.customerModel.getStrCustomerID();
            strImageName = Config.customerModel.getStrImgUrl();
        }

        clientnametxt.setText(strClientName);

        Glide.with(CheckInCareProcess.this)
                .load(strImageName)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(CheckInCareProcess.this))
                .placeholder(R.drawable.person_icon)
                .crossFade()
                .into(client);

        if (electrocheck.isChecked() == true
                && homecheck.isChecked() == true
                && autocheck.isChecked() == true) {

            equipmentstatus.setVisibility(View.VISIBLE);
            equipmentstatus.setText("Done");
            equipmentstatus.setTextColor(Color.BLUE);
        } else {
            equipmentstatus.setVisibility(View.VISIBLE);
            equipmentstatus.setText("Pending");
            equipmentstatus.setTextColor(Color.RED);
        }

        if (kitchenequipcheck.isChecked() == true) {
            kitchenequipmentstatus.setVisibility(View.VISIBLE);
            kitchenequipmentstatus.setText("Done");
            kitchenequipmentstatus.setTextColor(Color.BLUE);
        } else {
            kitchenequipmentstatus.setVisibility(View.VISIBLE);
            kitchenequipmentstatus.setText("Pending");
            kitchenequipmentstatus.setTextColor(Color.RED);
        }

        if (grocerycheck.isChecked() == true) {

            grocerystatus.setVisibility(View.VISIBLE);
            grocerystatus.setText("Done");
            grocerystatus.setTextColor(Color.BLUE);
        } else {
            grocerystatus.setVisibility(View.VISIBLE);
            grocerystatus.setText("Pending");
            grocerystatus.setTextColor(Color.RED);
        }

        if (domesticcheck.isChecked() == true) {

            domestichelpstatus.setVisibility(View.VISIBLE);
            domestichelpstatus.setText("Done");
            domestichelpstatus.setTextColor(Color.BLUE);
        } else {
            domestichelpstatus.setVisibility(View.VISIBLE);
            domestichelpstatus.setText("Pending");
            domestichelpstatus.setTextColor(Color.RED);
        }

        if (layouthall != null) {
            hallstatus.setVisibility(View.VISIBLE);
            hallstatus.setText("Pending");
            hallstatus.setTextColor(Color.RED);
        } else {
            hallstatus.setVisibility(View.VISIBLE);
            hallstatus.setText("Done");
            hallstatus.setTextColor(Color.BLUE);
        }
        if (layoutkitchen != null) {
            kitchenstatus.setVisibility(View.VISIBLE);
            kitchenstatus.setText("Pending");
            kitchenstatus.setTextColor(Color.RED);
        } else {
            kitchenstatus.setVisibility(View.VISIBLE);
            kitchenstatus.setText("Done");
            kitchenstatus.setTextColor(Color.BLUE);
        }
        if (layoutwashroom != null) {
            washroomstatus.setVisibility(View.VISIBLE);
            washroomstatus.setText("Pending");
            washroomstatus.setTextColor(Color.RED);
        } else {
            washroomstatus.setVisibility(View.VISIBLE);
            washroomstatus.setText("Done");
            washroomstatus.setTextColor(Color.BLUE);
        }
        if (layoutbedroom != null) {
            bedroomstatus.setVisibility(View.VISIBLE);
            bedroomstatus.setText("Pending");
            bedroomstatus.setTextColor(Color.RED);
        } else {
            bedroomstatus.setVisibility(View.VISIBLE);
            bedroomstatus.setText("Done");
            bedroomstatus.setTextColor(Color.BLUE);
        }

        if (hallstatus.getText().equals("Done")
                && kitchenstatus.getText().equals("Done")
                && washroomstatus.getText().equals("Done")
                && bedroomstatus.getText().equals("Done")) {

            uploadmediastatus.setVisibility(View.VISIBLE);
            uploadmediastatus.setText("Done");
            uploadmediastatus.setTextColor(Color.BLUE);
        } else {
            uploadmediastatus.setVisibility(View.VISIBLE);
            uploadmediastatus.setText("Pending");
            uploadmediastatus.setTextColor(Color.RED);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CheckInCareProcess.this,
                android.R.layout.simple_spinner_item, option);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        spinner3.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
        spinner1.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);
        spinner3.setOnItemSelectedListener(this);


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
                    goBack();
                }
            });
        }

        pick_date.setOnClickListener(this);
        pick_date2.setOnClickListener(this);
        pick_date3.setOnClickListener(this);
        pick_date4.setOnClickListener(this);

        electrocheck.setOnClickListener(this);
        homecheck.setOnClickListener(this);
        autocheck.setOnClickListener(this);
        kitchenequipcheck.setOnClickListener(this);
        grocerycheck.setOnClickListener(this);
        domesticcheck.setOnClickListener(this);

        buttonHallAdd.setOnClickListener(this);
        buttonKitchenAdd.setOnClickListener(this);
        buttonWashroomAdd.setOnClickListener(this);
        buttonBedroomAdd.setOnClickListener(this);

        btn_close = (Button) findViewById(R.id.btn_close);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isClick) {
                    isClick = true;

                    boolean cancel = false;

                    kitchen_equipments.setError(null);
                    grocery.setError(null);
                    electronic.setError(null);
                    homeapplience.setError(null);
                    automobile.setError(null);
                    maidservices.setError(null);
                    mediacomment.setError(null);


                    valkitchen = kitchen_equipments.getText().toString().trim();
                    valgrocery = grocery.getText().toString().trim();
                    valelectronic = electronic.getText().toString().trim();
                    valhomeapplience = homeapplience.getText().toString().trim();
                    valautomobile = automobile.getText().toString().trim();
                    valmaidservices = maidservices.getText().toString().trim();
                    valmediacomment = mediacomment.getText().toString().trim();

                    if (kitchenequipcheck.isChecked() == false) {
                        if (TextUtils.isEmpty(valkitchen)) {
                            kitchen_equipments.setError(getString(R.string.error_field_required));
                            focusView = kitchen_equipments;
                            cancel = true;
                        }
                    }
                    if (grocerycheck.isChecked() == false) {
                        if (TextUtils.isEmpty(valgrocery)) {
                            grocery.setError(getString(R.string.error_field_required));
                            focusView = grocery;
                            cancel = true;
                        }
                    }
                    if (electrocheck.isChecked() == false) {
                        if (TextUtils.isEmpty(valelectronic)) {
                            electronic.setError(getString(R.string.error_field_required));
                            focusView = electronic;
                            cancel = true;
                        }
                    }
                    if (homecheck.isChecked() == false) {
                        if (TextUtils.isEmpty(valhomeapplience)) {
                            homeapplience.setError(getString(R.string.error_field_required));
                            focusView = homeapplience;
                            cancel = true;
                        }
                    }
                    if (autocheck.isChecked() == false) {
                        if (TextUtils.isEmpty(valautomobile)) {
                            automobile.setError(getString(R.string.error_field_required));
                            focusView = automobile;
                            cancel = true;
                        }
                    }
                    if (domesticcheck.isChecked() == false) {
                        if (TextUtils.isEmpty(valmaidservices)) {
                            maidservices.setError(getString(R.string.error_field_required));
                            focusView = maidservices;
                            cancel = true;
                        }
                    }
                    /* if (hallimageModels.size() <= 0
                            || kitchenimageModels.size() <= 0
                            || washroomimageModels.size() <= 0
                            || bedroomimageModels.size() <= 0) {
                        if (TextUtils.isEmpty(valmediacomment)) {
                            mediacomment.setError(getString(R.string.error_field_required));
                            focusView = mediacomment;
                            cancel = true;
                        }
                    }*/


                    if (cancel) {
                        focusView.requestFocus();
                        isClick = false;
                    } else {

                        if (utils.isConnectingToInternet()) {

                            ////////////////////////////
                            boolean bFuture = true;


                            if (bFuture) {

                                //  CheckInCareModel checkInCareModel = new CheckInCareModel();
                                //if (hallimageModels != null && hallimageModels.size() > 0) {
                                    uploadHallImage();
                                // }

                            } else {
                                isClick = false;
                                // utils.toast(2, 2, getString(R.string.invalid_date));
                            }

                        } else {
                            isClick = false;
                            //  utils.toast(2, 2, getString(R.string.warning_internet));
                        }

                    }
                } else {
                    isClick = false;
                    //  utils.toast(2, 2, getString(R.string.warning_internet));
                }
            }
        });

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
                utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                        + ".jpeg", null, CheckInCareProcess.this, false);

                break;
            case R.id.buttonKitchenAdd:
                utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                        + ".jpeg", null, CheckInCareProcess.this, false);

                break;
            case R.id.buttonWashroomAdd:
                utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                        + ".jpeg", null, CheckInCareProcess.this, false);

                break;
            case R.id.buttonBedroomAdd:
                utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                        + ".jpeg", null, CheckInCareProcess.this, false);

                break;


        }
        if (electrocheck.isChecked() == true
                && homecheck.isChecked() == true
                && autocheck.isChecked() == true) {

            equipmentstatus.setVisibility(View.VISIBLE);
            equipmentstatus.setText("Done");
            equipmentstatus.setTextColor(Color.BLUE);
        } else {
            equipmentstatus.setVisibility(View.VISIBLE);
            equipmentstatus.setText("Pending");
            equipmentstatus.setTextColor(Color.RED);
        }
        if (kitchenequipcheck.isChecked() == true) {
            kitchenequipmentstatus.setVisibility(View.VISIBLE);
            kitchenequipmentstatus.setText("Done");
            kitchenequipmentstatus.setTextColor(Color.BLUE);
            if (grocerystatus.getText().toString().equals("Done")
                    && waterstatus.getText().toString().equals("Yes")
                    && gasstatus.getText().toString().equals("Yes")
                    && electricitystatus.getText().toString().equals("Yes")
                    && telephonestatus.getText().toString().equals("Yes")) {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText("Done");
                homeessentialstatus.setTextColor(Color.BLUE);
            } else {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText("Pending");
                homeessentialstatus.setTextColor(Color.RED);
            }
        } else {
            kitchenequipmentstatus.setVisibility(View.VISIBLE);
            kitchenequipmentstatus.setText("Pending");
            kitchenequipmentstatus.setTextColor(Color.RED);
            if (grocerystatus.getText().toString().equals("Done")
                    && kitchenequipmentstatus.getText().toString().equals("Done")
                    && waterstatus.getText().toString().equals("Yes")
                    && gasstatus.getText().toString().equals("Yes")
                    && electricitystatus.getText().toString().equals("Yes")
                    && telephonestatus.getText().toString().equals("Yes")) {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText("Done");
                homeessentialstatus.setTextColor(Color.BLUE);
            } else {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText("Pending");
                homeessentialstatus.setTextColor(Color.RED);
            }
        }

        if (grocerycheck.isChecked() == true) {
            grocerystatus.setVisibility(View.VISIBLE);
            grocerystatus.setText("Done");
            grocerystatus.setTextColor(Color.BLUE);
            if (kitchenequipmentstatus.getText().toString().equals("Done")
                    && waterstatus.getText().toString().equals("Yes")
                    && gasstatus.getText().toString().equals("Yes")
                    && electricitystatus.getText().toString().equals("Yes")
                    && telephonestatus.getText().toString().equals("Yes")) {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText("Done");
                homeessentialstatus.setTextColor(Color.BLUE);
            } else {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText("Pending");
                homeessentialstatus.setTextColor(Color.RED);
            }
        } else {
            grocerystatus.setVisibility(View.VISIBLE);
            grocerystatus.setText("Pending");
            grocerystatus.setTextColor(Color.RED);
            if (kitchenequipmentstatus.getText().toString().equals("Done")
                    && grocerystatus.getText().toString().equals("Done")
                    && waterstatus.getText().toString().equals("Yes")
                    && gasstatus.getText().toString().equals("Yes")
                    && electricitystatus.getText().toString().equals("Yes")
                    && telephonestatus.getText().toString().equals("Yes")) {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText("Done");
                homeessentialstatus.setTextColor(Color.BLUE);
            } else {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText("Pending");
                homeessentialstatus.setTextColor(Color.RED);
            }
        }
        if (domesticcheck.isChecked() == true) {

            domestichelpstatus.setVisibility(View.VISIBLE);
            domestichelpstatus.setText("Done");
            domestichelpstatus.setTextColor(Color.BLUE);
        } else {
            domestichelpstatus.setVisibility(View.VISIBLE);
            domestichelpstatus.setText("Pending");
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
        goBack();
    }

    private void goBack() {
        Intent intent = new Intent(CheckInCareProcess.this, DashboardActivity.class);
        Config.intSelectedMenu = Config.intClientScreen;
        startActivity(intent);
        finish();
    }

    private void uploadHallImage() {

        if (hallImageCount > 0) {

            onCreateDialog(DIALOG_DOWNLOAD_PROGRESS1);

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

                                    if (response != null) {

                                        Utils.log(response.toString(), " Hall Images ");

                                        Upload upload = (Upload) response;
                                        ArrayList<Upload.File> fileList = upload.getFileList();

                                        if (fileList.size() > 0) {

                                            Upload.File file = fileList.get(0);

                                            hallimageModels.get(hallImageUploadCount).setmIsNew(false);
                                            hallimageModels.get(hallImageUploadCount).setStrImageUrl(file.getUrl());

                                            try {
                                                hallImageUploadCount++;
                                                if (hallImageUploadCount >= hallimageModels.size()) {
                                                    //    if (kitchenimageModels != null && kitchenimageModels.size() > 0) {
                                                        uploadKitchenImage();
                                                    //    }
                                                } else {
                                                    uploadHallImage();
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        } else {
                                            hallImageUploadCount++;
                                            if (hallImageUploadCount >= hallimageModels.size()) {
                                                if (kitchenimageModels != null && kitchenimageModels.size() > 0) {
                                                    uploadKitchenImage();
                                                }
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
                                    if (mProgressDialog != null)
                                        mProgressDialog.dismiss();
                                    if (e != null) {
                                        Utils.log(e.getMessage(), " Failure ");
                                        hallImageUploadCount++;
                                        if (hallImageUploadCount >= hallimageModels.size()) {
                                            if (kitchenimageModels != null && kitchenimageModels.size() > 0) {
                                                uploadKitchenImage();
                                            }
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
                        if (kitchenimageModels != null && kitchenimageModels.size() > 0) {
                            uploadKitchenImage();
                        }
                    } else {
                        uploadHallImage();
                    }
                }

            } else {
                hallImageUploadCount++;

                if (hallImageUploadCount >= hallimageModels.size()) {
                    if (kitchenimageModels != null && kitchenimageModels.size() > 0) {
                        uploadKitchenImage();
                    }
                } else {
                    uploadHallImage();
                }
            }

        } else {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();

            uploadKitchenImage();

          /*  if (mImageChanged) {
                bLoad = false;
                updateJson();
            }*/
        }
    }

    private void uploadKitchenImage() {

        if (kitchenImageCount > 0) {

            bLoad = false;

            if (kitchenImageUploadCount < kitchenimageModels.size()) {

                final ImageModel mkitchenImageModel = kitchenimageModels.get(kitchenImageUploadCount);

                if (mkitchenImageModel.ismIsNew()) {

                    UploadService uploadService = new UploadService(this);

                    uploadService.uploadImageCommon(mkitchenImageModel.getStrImagePath(),
                            mkitchenImageModel.getStrImageDesc(), mkitchenImageModel.getStrImageDesc(),
                            Config.providerModel.getStrEmail(), UploadFileType.IMAGE,
                            new App42CallBack() {
                                public void onSuccess(Object response) {

                                    if (response != null) {

                                        Utils.log(response.toString(), " Kitchen Images ");

                                        Upload upload = (Upload) response;
                                        ArrayList<Upload.File> fileList = upload.getFileList();

                                        if (fileList.size() > 0) {

                                            Upload.File file = fileList.get(0);

                                            kitchenimageModels.get(kitchenImageUploadCount).setmIsNew(false);
                                            kitchenimageModels.get(kitchenImageUploadCount).setStrImageUrl(file.getUrl());

                                            try {
                                                kitchenImageUploadCount++;
                                                if (kitchenImageUploadCount >= kitchenimageModels.size()) {
                                                    //  if (washroomimageModels != null && washroomimageModels.size() > 0) {
                                                        uploadWashroomImage();
                                                    //    }
                                                } else {
                                                    uploadKitchenImage();
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        } else {
                                            kitchenImageUploadCount++;
                                            if (kitchenImageUploadCount >= kitchenimageModels.size()) {
                                                if (washroomimageModels != null && washroomimageModels.size() > 0) {
                                                    uploadWashroomImage();
                                                }
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

                                    if (e != null) {
                                        Utils.log(e.getMessage(), " Failure ");
                                        kitchenImageUploadCount++;
                                        if (kitchenImageUploadCount >= kitchenimageModels.size()) {
                                            if (washroomimageModels != null && washroomimageModels.size() > 0) {
                                                uploadWashroomImage();
                                            }
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
                        if (washroomimageModels != null && washroomimageModels.size() > 0) {
                            uploadWashroomImage();
                        }
                    } else {
                        uploadKitchenImage();
                    }
                }
            } else {
                kitchenImageUploadCount++;

                if (kitchenImageUploadCount >= kitchenimageModels.size()) {
                    if (washroomimageModels != null && washroomimageModels.size() > 0) {
                        uploadWashroomImage();
                    }
                } else {
                    uploadKitchenImage();
                }
            }
        } else {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();

            uploadWashroomImage();

         /*   if (mImageChanged) {
                bLoad = false;
                updateJson();
            }*/
        }
    }

    private void uploadWashroomImage() {

        if (washroomImageCount > 0) {

            bLoad = false;

            if (washroomImageUploadCount < washroomimageModels.size()) {

                final ImageModel mwashroomImageModel = washroomimageModels.get(washroomImageUploadCount);

                if (mwashroomImageModel.ismIsNew()) {

                    UploadService uploadService = new UploadService(this);

                    uploadService.uploadImageCommon(mwashroomImageModel.getStrImagePath(),
                            mwashroomImageModel.getStrImageDesc(), mwashroomImageModel.getStrImageDesc(),
                            Config.providerModel.getStrEmail(), UploadFileType.IMAGE,
                            new App42CallBack() {
                                public void onSuccess(Object response) {

                                    if (response != null) {

                                        Utils.log(response.toString(), " Washroom Images ");

                                        Upload upload = (Upload) response;
                                        ArrayList<Upload.File> fileList = upload.getFileList();

                                        if (fileList.size() > 0) {

                                            Upload.File file = fileList.get(0);

                                            washroomimageModels.get(washroomImageUploadCount).setmIsNew(false);
                                            washroomimageModels.get(washroomImageUploadCount).setStrImageUrl(file.getUrl());

                                            try {
                                                washroomImageUploadCount++;
                                                if (washroomImageUploadCount >= washroomimageModels.size()) {
                                                    //  if (bedroomimageModels != null && bedroomimageModels.size() > 0) {
                                                        uploadBedroomImage();
                                                    //  }
                                                } else {
                                                    uploadWashroomImage();
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        } else {
                                            washroomImageUploadCount++;
                                            if (washroomImageUploadCount >= washroomimageModels.size()) {
                                                if (bedroomimageModels != null && bedroomimageModels.size() > 0) {
                                                    uploadBedroomImage();
                                                }
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

                                    if (e != null) {
                                        Utils.log(e.getMessage(), " Failure ");
                                        washroomImageUploadCount++;
                                        if (washroomImageUploadCount >= washroomimageModels.size()) {
                                            if (bedroomimageModels != null && bedroomimageModels.size() > 0) {
                                                uploadBedroomImage();
                                            }
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
                        if (bedroomimageModels != null && bedroomimageModels.size() > 0) {
                            uploadBedroomImage();
                        }
                    } else {
                        uploadWashroomImage();
                    }
                }
            } else {
                washroomImageUploadCount++;

                if (washroomImageUploadCount >= washroomimageModels.size()) {
                    if (bedroomimageModels != null && bedroomimageModels.size() > 0) {
                        uploadBedroomImage();
                    }
                } else {
                    uploadWashroomImage();
                }
            }
        } else {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();

            uploadBedroomImage();

         /*   if (mImageChanged) {
                bLoad = false;
                updateJson();
            }*/
        }
    }

    private void uploadBedroomImage() {

        if (bedroomImageCount > 0) {

            bLoad = false;

            if (bedroomImageUploadCount < bedroomimageModels.size()) {

                final ImageModel mbedroomImageModel = bedroomimageModels.get(bedroomImageUploadCount);

                if (mbedroomImageModel.ismIsNew()) {

                    UploadService uploadService = new UploadService(this);

                    uploadService.uploadImageCommon(mbedroomImageModel.getStrImagePath(),
                            mbedroomImageModel.getStrImageDesc(), mbedroomImageModel.getStrImageDesc(),
                            Config.providerModel.getStrEmail(), UploadFileType.IMAGE,
                            new App42CallBack() {
                                public void onSuccess(Object response) {

                                    if (response != null) {

                                        Utils.log(response.toString(), " Bedroom Images ");

                                        Upload upload = (Upload) response;
                                        ArrayList<Upload.File> fileList = upload.getFileList();

                                        if (fileList.size() > 0) {

                                            Upload.File file = fileList.get(0);

                                            bedroomimageModels.get(bedroomImageUploadCount).setmIsNew(false);
                                            bedroomimageModels.get(bedroomImageUploadCount).setStrImageUrl(file.getUrl());

                                            try {
                                                bedroomImageUploadCount++;
                                                if (bedroomImageUploadCount >= bedroomimageModels.size()) {
                                                    updateJson();
                                                } else {
                                                    uploadBedroomImage();
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        } else {
                                            bedroomImageUploadCount++;
                                            if (bedroomImageUploadCount >= bedroomimageModels.size()) {
                                                updateJson();
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

                                    if (e != null) {
                                        Utils.log(e.getMessage(), " Failure ");
                                        bedroomImageUploadCount++;
                                        if (bedroomImageUploadCount >= bedroomimageModels.size()) {
                                            updateJson();
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
                        updateJson();
                    } else {
                        uploadBedroomImage();
                    }
                }
            } else {
                updateJson();
            }
        } else {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();

                updateJson();

        }
    }

    private void updateJson() {

        if (utils.isConnectingToInternet()) {

            JSONObject jsonObjectCheckinCare = null;


            try {
                jsonObjectCheckinCare = new JSONObject();

                Date mydate = new Date();
                String strCreateDate = Utils.readFormat.format(mydate);
                String strMonth = Utils.writeFormatDateMonth.format(mydate);
                String strYear = Utils.writeFormatDateYear.format(mydate);

                jsonObjectCheckinCare.put("created_date", strCreateDate);
                jsonObjectCheckinCare.put("dependent_id", "");
                jsonObjectCheckinCare.put("provider_id", Config.providerModel.getStrProviderId());
                jsonObjectCheckinCare.put("updated_date", "");
                jsonObjectCheckinCare.put("current_date", datetxt.getText().toString());
                jsonObjectCheckinCare.put("status", "New");
                jsonObjectCheckinCare.put("month", strMonth);
                jsonObjectCheckinCare.put("year", strYear);
                jsonObjectCheckinCare.put("house_name", "Our House");
                jsonObjectCheckinCare.put("customer_id", Config.customerModel.getStrCustomerID());
                jsonObjectCheckinCare.put("media_comment", mediacomment.getText().toString());
                jsonObjectCheckinCare.put("check_in_care_name", checkincarename.getText().toString());

                JSONArray jsonArrayPicture = new JSONArray();

                ///////////////////////Hallimage
                JSONObject jsonObjectHallPicture = new JSONObject();

                jsonObjectHallPicture.put("status", "status");
                jsonObjectHallPicture.put("room_name", "hall");

                //  jsonArrayPicture.put(jsonObjectHallPicture);
                //   jsonObjectCheckinCare.put("pictures", jsonArrayPicture);

                JSONArray jsonArrayHallPictureDetails = new JSONArray();

                ArrayList<ImageModel> mTHallImageModels = new ArrayList<>();

                if (hallimageModels.size() > 0) {

                    for (ImageModel hallImageModel : hallimageModels) {

                        JSONObject jsonObjectHallImages = new JSONObject();

                        jsonObjectHallImages.put("image_url", hallImageModel.getStrImageUrl());
                        jsonObjectHallImages.put("description", hallImageModel.getStrImageDesc());
                        jsonObjectHallImages.put("date_time", hallImageModel.getStrImageTime());

                        jsonArrayHallPictureDetails.put(jsonObjectHallImages);
                        mTHallImageModels.add(hallImageModel);
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

              /*  jsonArrayPicture.put(jsonObjectKitchenPicture);
                jsonObjectCheckinCare.put("pictures", jsonArrayPicture);*/

                JSONArray jsonArrayKitchenPictureDetails = new JSONArray();

                ArrayList<ImageModel> mTKitchenImageModels = new ArrayList<>();

                if (kitchenimageModels.size() > 0) {

                    for (ImageModel kitchenImageModel : kitchenimageModels) {

                        JSONObject jsonObjectKitchenImages = new JSONObject();

                        jsonObjectKitchenImages.put("image_url", kitchenImageModel.getStrImageUrl());
                        jsonObjectKitchenImages.put("description", kitchenImageModel.getStrImageDesc());
                        jsonObjectKitchenImages.put("date_time", kitchenImageModel.getStrImageTime());

                        jsonArrayKitchenPictureDetails.put(jsonObjectKitchenImages);
                        mTKitchenImageModels.add(kitchenImageModel);
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

               /* jsonArrayPicture.put(jsonObjectWashroomPicture);
                jsonObjectCheckinCare.put("pictures", jsonArrayPicture);*/
                JSONArray jsonArrayWashroomPictureDetails = new JSONArray();

                ArrayList<ImageModel> mTWashroomImageModels = new ArrayList<>();

                if (washroomimageModels.size() > 0) {

                    for (ImageModel washroomImageModel : washroomimageModels) {

                        JSONObject jsonObjectWashroomImages = new JSONObject();

                        jsonObjectWashroomImages.put("image_url", washroomImageModel.getStrImageUrl());
                        jsonObjectWashroomImages.put("description", washroomImageModel.getStrImageDesc());
                        jsonObjectWashroomImages.put("date_time", washroomImageModel.getStrImageTime());

                        jsonArrayWashroomPictureDetails.put(jsonObjectWashroomImages);
                        mTWashroomImageModels.add(washroomImageModel);
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

                //  jsonArrayPicture.put(jsonObjectBedroomPicture);
                // jsonObjectCheckinCare.put("pictures", jsonArrayPicture);
                JSONArray jsonArrayBedroomPictureDetails = new JSONArray();

                ArrayList<ImageModel> mTBedroomImageModels = new ArrayList<>();

                if (bedroomimageModels.size() > 0) {

                    for (ImageModel bedroomImageModel : bedroomimageModels) {

                        JSONObject jsonObjectBedroomImages = new JSONObject();

                        jsonObjectBedroomImages.put("image_url", bedroomImageModel.getStrImageUrl());
                        jsonObjectBedroomImages.put("description", bedroomImageModel.getStrImageDesc());
                        jsonObjectBedroomImages.put("date_time", bedroomImageModel.getStrImageTime());

                        jsonArrayBedroomPictureDetails.put(jsonObjectBedroomImages);
                        mTBedroomImageModels.add(bedroomImageModel);
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

                jsonArraySubActivitiesHome.put(jsonObjectSubActivitiesHome);

                JSONObject jsonObjectSubActivitiesHome1 = new JSONObject();
                jsonObjectSubActivitiesHome1.put("sub_activity_name", "grocery");
                jsonObjectSubActivitiesHome1.put("status", grocery.getText().toString());


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

                jsonArraySubActivitiesDomestic.put(jsonObjectSubActivitiesDomestic);

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

                jsonArraySubActivitiesEquipment.put(jsonObjectSubActivitiesEquipment1);


                JSONObject jsonObjectSubActivitiesEquipment2 = new JSONObject();
                jsonObjectSubActivitiesEquipment2.put("sub_activity_name", "home_appliances");
                jsonObjectSubActivitiesEquipment2.put("status", homeapplience.getText().toString());

                jsonArraySubActivitiesEquipment.put(jsonObjectSubActivitiesEquipment2);


                JSONObject jsonObjectSubActivitiesEquipment3 = new JSONObject();
                jsonObjectSubActivitiesEquipment3.put("sub_activity_name", "automobile");
                jsonObjectSubActivitiesEquipment3.put("status", automobile.getText().toString());

                jsonArraySubActivitiesEquipment.put(jsonObjectSubActivitiesEquipment3);


                jsonObjectActivitiesEquipment.put("sub_activities", jsonArraySubActivitiesEquipment);

                jsonArrayActivities.put(jsonObjectActivitiesEquipment);
                ///////////////////////activity 3


                jsonObjectCheckinCare.put("activities", jsonArrayActivities);


               /* Config.checkInCareModels.get(mImageUploadCount).clearImageModel();
                Config.checkInCareModels.get(mImageUploadCount).setImageModels(mTHallImageModels);*/

                hallimageModels = mTHallImageModels;

            } catch (Exception e) {
                e.printStackTrace();
            }

            storageService.insertDocs(Config.collectionCheckInCare, jsonObjectCheckinCare,
                    new AsyncApp42ServiceApi.App42StorageServiceListener() {

                        @Override
                        public void onDocumentInserted(Storage response) {
                            try {
                                if (response.isResponseSuccess()) {
                                    IMAGE_COUNT = 0;
                                    utils.toast(2, 2, getString(R.string.data_upload));
                                    Intent intent = new Intent(CheckInCareProcess.this, DashboardActivity.class);
                                    Config.intSelectedMenu = Config.intClientScreen;
                                    startActivity(intent);
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
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            try {

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


                        Collections.addAll(imagePaths, all_path);

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
            hallstatus.setText("Done");
            hallstatus.setTextColor(Color.BLUE);
            if (kitchenstatus.getText().toString().equals("Done")
                    && washroomstatus.getText().toString().equals("Done")
                    && bedroomstatus.getText().toString().equals("Done")) {

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Done");
                uploadmediastatus.setTextColor(Color.BLUE);
            } else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Pending");
                uploadmediastatus.setTextColor(Color.RED);
            }


            for (int i = 0; i < hallimageModels.size(); i++) {
                try {
                    final ImageView imageView = new ImageView(CheckInCareProcess.this);
                    imageView.setPadding(0, 0, 3, 0);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(10, 10, 10, 10);

                    Utils.log(String.valueOf(hallimageModels.get(i).getStrImageName() + " # " +
                            hallimageModels.get(i).getStrImagePath()), " height 0 ");

                    Utils.log(String.valueOf(hallbitmaps.get(i).getHeight()), " height ");

                    imageView.setLayoutParams(layoutParams);
                    imageView.setImageBitmap(hallbitmaps.get(i));
                    imageView.setTag(hallimageModels.get(i));
                    imageView.setTag(R.id.three, i);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);


                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final ImageModel mImageModel = (ImageModel) v.getTag();

                            final int mPosition = (int) v.getTag(R.id.three);

                            final Dialog dialog = new Dialog(CheckInCareProcess.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            dialog.setContentView(R.layout.image_dialog_layout);

                            TouchImageView mOriginal = (TouchImageView) dialog.findViewById(R.id.imgOriginal);
                            TextView textViewClose = (TextView) dialog.findViewById(R.id.textViewClose);
                            Button buttonDelete = (Button) dialog.findViewById(R.id.textViewTitle);

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
                                            new AlertDialog.Builder(CheckInCareProcess.this);
                                    alertbox.setTitle(getString(R.string.delete_image));
                                    alertbox.setMessage(getString(R.string.confirm_delete_image));
                                    alertbox.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {

                                            try {
                                                File fDelete = utils.getInternalFileImages(mImageModel.getStrImageName());

                                                if (fDelete.exists()) {
                                                    success = fDelete.delete();

                                                    if (mImageModel.ismIsNew())
                                                        hallImageCount--;

                                                    mImageChanged = true;

                                                    hallimageModels.remove(mImageModel);

                                                    hallbitmaps.remove(mPosition);
                                                    if (hallImageCount < 1) {
                                                        hallstatus.setVisibility(View.VISIBLE);
                                                        hallstatus.setText("Pending");
                                                        hallstatus.setTextColor(Color.RED);
                                                        if (kitchenstatus.getText().toString().equals("Done")
                                                                && hallstatus.getText().toString().equals("Done")
                                                                && washroomstatus.getText().toString().equals("Done")
                                                                && bedroomstatus.getText().toString().equals("Done")) {

                                                            uploadmediastatus.setVisibility(View.VISIBLE);
                                                            uploadmediastatus.setText("Done");
                                                            uploadmediastatus.setTextColor(Color.BLUE);
                                                        } else {
                                                            uploadmediastatus.setVisibility(View.VISIBLE);
                                                            uploadmediastatus.setText("Pending");
                                                            uploadmediastatus.setTextColor(Color.RED);
                                                        }
                                                    }
                                                }
                                                if (success) {
                                                    utils.toast(2, 2, getString(R.string.file_deleted));

                                                }
                                                arg0.dismiss();
                                                dialog.dismiss();
                                                addHallImages();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    alertbox.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
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

                            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT); //Controlling width and height.
                            dialog.show();

                        }
                    });

                    layouthall.addView(imageView);
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
        } else {
            hallstatus.setVisibility(View.VISIBLE);
            hallstatus.setText("Pending");
            hallstatus.setTextColor(Color.RED);
            if (kitchenstatus.getText().toString().equals("Done")
                    && hallstatus.getText().toString().equals("Done")
                    && washroomstatus.getText().toString().equals("Done")
                    && bedroomstatus.getText().toString().equals("Done")) {

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Done");
                uploadmediastatus.setTextColor(Color.BLUE);
            } else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Pending");
                uploadmediastatus.setTextColor(Color.RED);
            }

        }

    }

    private void addKitchenImages() {

        layoutkitchen.removeAllViews();

        if (kitchenimageModels != null && kitchenimageModels.size() > 0) {

            kitchenstatus.setVisibility(View.VISIBLE);
            kitchenstatus.setText("Done");
            kitchenstatus.setTextColor(Color.BLUE);

            if (hallstatus.getText().toString().equals("Done")
                    && washroomstatus.getText().toString().equals("Done")
                    && bedroomstatus.getText().toString().equals("Done")) {

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Done");
                uploadmediastatus.setTextColor(Color.BLUE);
            } else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Pending");
                uploadmediastatus.setTextColor(Color.RED);
            }

            for (int i = 0; i < kitchenimageModels.size(); i++) {
                try {
                    final ImageView imageView = new ImageView(CheckInCareProcess.this);
                    imageView.setPadding(0, 0, 3, 0);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(10, 10, 10, 10);

                    Utils.log(String.valueOf(kitchenimageModels.get(i).getStrImageName() + " # " +
                            kitchenimageModels.get(i).getStrImagePath()), " height 0 ");

                    Utils.log(String.valueOf(kitchenbitmaps.get(i).getHeight()), " height ");

                    imageView.setLayoutParams(layoutParams);
                    imageView.setImageBitmap(kitchenbitmaps.get(i));
                    imageView.setTag(kitchenimageModels.get(i));
                    imageView.setTag(R.id.three, i);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);


                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final ImageModel mImageModel = (ImageModel) v.getTag();

                            final int mPosition = (int) v.getTag(R.id.three);

                            final Dialog dialog = new Dialog(CheckInCareProcess.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            dialog.setContentView(R.layout.image_dialog_layout);

                            TouchImageView mOriginal = (TouchImageView) dialog.findViewById(R.id.imgOriginal);
                            TextView textViewClose = (TextView) dialog.findViewById(R.id.textViewClose);
                            Button buttonDelete = (Button) dialog.findViewById(R.id.textViewTitle);

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
                                            new AlertDialog.Builder(CheckInCareProcess.this);
                                    alertbox.setTitle(getString(R.string.delete_image));
                                    alertbox.setMessage(getString(R.string.confirm_delete_image));
                                    alertbox.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {

                                            try {
                                                File fDelete = utils.getInternalFileImages(mImageModel.getStrImageName());

                                                if (fDelete.exists()) {
                                                    success = fDelete.delete();

                                                    if (mImageModel.ismIsNew())
                                                        kitchenImageCount--;

                                                    mImageChanged = true;

                                                    kitchenimageModels.remove(mImageModel);

                                                    kitchenbitmaps.remove(mPosition);

                                                    if (kitchenImageCount < 1) {
                                                        kitchenstatus.setVisibility(View.VISIBLE);
                                                        kitchenstatus.setText("Pending");
                                                        kitchenstatus.setTextColor(Color.RED);

                                                        if (hallstatus.getText().toString().equals("Done")
                                                                && kitchenstatus.getText().toString().equals("Done")
                                                                && washroomstatus.getText().toString().equals("Done")
                                                                && bedroomstatus.getText().toString().equals("Done")) {

                                                            uploadmediastatus.setVisibility(View.VISIBLE);
                                                            uploadmediastatus.setText("Done");
                                                            uploadmediastatus.setTextColor(Color.BLUE);
                                                        } else {
                                                            uploadmediastatus.setVisibility(View.VISIBLE);
                                                            uploadmediastatus.setText("Pending");
                                                            uploadmediastatus.setTextColor(Color.RED);
                                                        }
                                                    }
                                                }
                                                if (success) {
                                                    utils.toast(2, 2, getString(R.string.file_deleted));

                                                }
                                                arg0.dismiss();
                                                dialog.dismiss();
                                                addKitchenImages();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    alertbox.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
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

                            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT); //Controlling width and height.
                            dialog.show();

                        }
                    });

                    layoutkitchen.addView(imageView);
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
        } else {
            kitchenstatus.setVisibility(View.VISIBLE);
            kitchenstatus.setText("Pending");
            kitchenstatus.setTextColor(Color.RED);
            if (hallstatus.getText().toString().equals("Done")
                    && kitchenstatus.getText().toString().equals("Done")
                    && washroomstatus.getText().toString().equals("Done")
                    && bedroomstatus.getText().toString().equals("Done")) {

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Done");
                uploadmediastatus.setTextColor(Color.BLUE);
            } else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Pending");
                uploadmediastatus.setTextColor(Color.RED);
            }

        }
    }

    private void addWashroomImages() {

        layoutwashroom.removeAllViews();

        if (washroomimageModels != null && washroomimageModels.size() > 0) {

            washroomstatus.setVisibility(View.VISIBLE);
            washroomstatus.setText("Done");
            washroomstatus.setTextColor(Color.BLUE);

            if (hallstatus.getText().toString().equals("Done")
                    && kitchenstatus.getText().toString().equals("Done")
                    && bedroomstatus.getText().toString().equals("Done")) {

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Done");
                uploadmediastatus.setTextColor(Color.BLUE);
            } else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Pending");
                uploadmediastatus.setTextColor(Color.RED);
            }


            for (int i = 0; i < washroomimageModels.size(); i++) {
                try {
                    final ImageView imageView = new ImageView(CheckInCareProcess.this);
                    imageView.setPadding(0, 0, 3, 0);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(10, 10, 10, 10);

                    Utils.log(String.valueOf(washroomimageModels.get(i).getStrImageName() + " # " +
                            washroomimageModels.get(i).getStrImagePath()), " height 0 ");

                    Utils.log(String.valueOf(washroombitmaps.get(i).getHeight()), " height ");

                    imageView.setLayoutParams(layoutParams);
                    imageView.setImageBitmap(washroombitmaps.get(i));
                    imageView.setTag(washroomimageModels.get(i));
                    imageView.setTag(R.id.three, i);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);


                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final ImageModel mImageModel = (ImageModel) v.getTag();

                            final int mPosition = (int) v.getTag(R.id.three);

                            final Dialog dialog = new Dialog(CheckInCareProcess.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            dialog.setContentView(R.layout.image_dialog_layout);

                            TouchImageView mOriginal = (TouchImageView) dialog.findViewById(R.id.imgOriginal);
                            TextView textViewClose = (TextView) dialog.findViewById(R.id.textViewClose);
                            Button buttonDelete = (Button) dialog.findViewById(R.id.textViewTitle);

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
                                            new AlertDialog.Builder(CheckInCareProcess.this);
                                    alertbox.setTitle(getString(R.string.delete_image));
                                    alertbox.setMessage(getString(R.string.confirm_delete_image));
                                    alertbox.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {

                                            try {
                                                File fDelete = utils.getInternalFileImages(mImageModel.getStrImageName());

                                                if (fDelete.exists()) {
                                                    success = fDelete.delete();

                                                    if (mImageModel.ismIsNew())
                                                        washroomImageCount--;

                                                    mImageChanged = true;

                                                    washroomimageModels.remove(mImageModel);

                                                    washroombitmaps.remove(mPosition);
                                                    if (washroomImageCount < 1) {
                                                        washroomstatus.setVisibility(View.VISIBLE);
                                                        washroomstatus.setText("Pending");
                                                        washroomstatus.setTextColor(Color.RED);
                                                        if (hallstatus.getText().toString().equals("Done")
                                                                && washroomstatus.getText().toString().equals("Done")
                                                                && kitchenstatus.getText().toString().equals("Done")
                                                                && bedroomstatus.getText().toString().equals("Done")) {

                                                            uploadmediastatus.setVisibility(View.VISIBLE);
                                                            uploadmediastatus.setText("Done");
                                                            uploadmediastatus.setTextColor(Color.BLUE);
                                                        } else {
                                                            uploadmediastatus.setVisibility(View.VISIBLE);
                                                            uploadmediastatus.setText("Pending");
                                                            uploadmediastatus.setTextColor(Color.RED);
                                                        }
                                                    }
                                                }
                                                if (success) {
                                                    utils.toast(2, 2, getString(R.string.file_deleted));

                                                }
                                                arg0.dismiss();
                                                dialog.dismiss();
                                                addWashroomImages();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    alertbox.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
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

                            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT); //Controlling width and height.
                            dialog.show();

                        }
                    });

                    layoutwashroom.addView(imageView);
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
        } else {
            washroomstatus.setVisibility(View.VISIBLE);
            washroomstatus.setText("Pending");
            washroomstatus.setTextColor(Color.RED);
            if (hallstatus.getText().toString().equals("Done")
                    && washroomstatus.getText().toString().equals("Done")
                    && kitchenstatus.getText().toString().equals("Done")
                    && bedroomstatus.getText().toString().equals("Done")) {

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Done");
                uploadmediastatus.setTextColor(Color.BLUE);
            } else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Pending");
                uploadmediastatus.setTextColor(Color.RED);
            }

        }
    }

    private void addBedroomImages() {

        layoutbedroom.removeAllViews();

        if (bedroomimageModels != null && bedroomimageModels.size() > 0) {

            bedroomstatus.setVisibility(View.VISIBLE);
            bedroomstatus.setText("Done");
            bedroomstatus.setTextColor(Color.BLUE);
            if (hallstatus.getText().toString().equals("Done")
                    && kitchenstatus.getText().toString().equals("Done")
                    && washroomstatus.getText().toString().equals("Done")) {

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Done");
                uploadmediastatus.setTextColor(Color.BLUE);
            } else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Pending");
                uploadmediastatus.setTextColor(Color.RED);
            }

            for (int i = 0; i < bedroomimageModels.size(); i++) {
                try {
                    final ImageView imageView = new ImageView(CheckInCareProcess.this);
                    imageView.setPadding(0, 0, 3, 0);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(10, 10, 10, 10);

                    Utils.log(String.valueOf(bedroomimageModels.get(i).getStrImageName() + " # " +
                            bedroomimageModels.get(i).getStrImagePath()), " height 0 ");

                    Utils.log(String.valueOf(bedroombitmaps.get(i).getHeight()), " height ");

                    imageView.setLayoutParams(layoutParams);
                    imageView.setImageBitmap(bedroombitmaps.get(i));
                    imageView.setTag(bedroomimageModels.get(i));
                    imageView.setTag(R.id.three, i);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);


                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final ImageModel mImageModel = (ImageModel) v.getTag();

                            final int mPosition = (int) v.getTag(R.id.three);

                            final Dialog dialog = new Dialog(CheckInCareProcess.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            dialog.setContentView(R.layout.image_dialog_layout);

                            TouchImageView mOriginal = (TouchImageView) dialog.findViewById(R.id.imgOriginal);
                            TextView textViewClose = (TextView) dialog.findViewById(R.id.textViewClose);
                            Button buttonDelete = (Button) dialog.findViewById(R.id.textViewTitle);

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
                                            new AlertDialog.Builder(CheckInCareProcess.this);
                                    alertbox.setTitle(getString(R.string.delete_image));
                                    alertbox.setMessage(getString(R.string.confirm_delete_image));
                                    alertbox.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {

                                            try {
                                                File fDelete = utils.getInternalFileImages(mImageModel.getStrImageName());

                                                if (fDelete.exists()) {
                                                    success = fDelete.delete();

                                                    if (mImageModel.ismIsNew())
                                                        bedroomImageCount--;

                                                    mImageChanged = true;

                                                    bedroomimageModels.remove(mImageModel);

                                                    bedroombitmaps.remove(mPosition);

                                                    if (bedroomImageCount < 1) {
                                                        bedroomstatus.setVisibility(View.VISIBLE);
                                                        bedroomstatus.setText("Pending");
                                                        bedroomstatus.setTextColor(Color.RED);
                                                        if (hallstatus.getText().toString().equals("Done")
                                                                && bedroomstatus.getText().toString().equals("Done")
                                                                && kitchenstatus.getText().toString().equals("Done")
                                                                && washroomstatus.getText().toString().equals("Done")) {

                                                            uploadmediastatus.setVisibility(View.VISIBLE);
                                                            uploadmediastatus.setText("Done");
                                                            uploadmediastatus.setTextColor(Color.BLUE);
                                                        } else {
                                                            uploadmediastatus.setVisibility(View.VISIBLE);
                                                            uploadmediastatus.setText("Pending");
                                                            uploadmediastatus.setTextColor(Color.RED);
                                                        }

                                                    }
                                                }
                                                if (success) {
                                                    utils.toast(2, 2, getString(R.string.file_deleted));

                                                }
                                                arg0.dismiss();
                                                dialog.dismiss();
                                                addBedroomImages();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    alertbox.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
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

                            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT); //Controlling width and height.
                            dialog.show();

                        }
                    });

                    layoutbedroom.addView(imageView);
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
        } else {
            bedroomstatus.setVisibility(View.VISIBLE);
            bedroomstatus.setText("Pending");
            bedroomstatus.setTextColor(Color.RED);
            if (hallstatus.getText().toString().equals("Done")
                    && bedroomstatus.getText().toString().equals("Done")
                    && kitchenstatus.getText().toString().equals("Done")
                    && washroomstatus.getText().toString().equals("Done")) {

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Done");
                uploadmediastatus.setTextColor(Color.BLUE);
            } else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Pending");
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
                    waterstatus.setText("Yes");
                    if (grocerystatus.getText().toString().equals("Done")
                            && kitchenequipmentstatus.getText().toString().equals("Done")
                            && gasstatus.getText().toString().equals("Yes")
                            && electricitystatus.getText().toString().equals("Yes")
                            && telephonestatus.getText().toString().equals("Yes")) {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Done");
                        homeessentialstatus.setTextColor(Color.BLUE);
                    } else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Pending");
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                } else {
                    waterstatus.setText("No");
                    if (grocerystatus.getText().toString().equals("Done")
                            && kitchenequipmentstatus.getText().toString().equals("Done")
                            && waterstatus.getText().toString().equals("Yes")
                            && gasstatus.getText().toString().equals("Yes")
                            && electricitystatus.getText().toString().equals("Yes")
                            && telephonestatus.getText().toString().equals("Yes")) {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Done");
                        homeessentialstatus.setTextColor(Color.BLUE);
                    } else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Pending");
                        homeessentialstatus.setTextColor(Color.RED);
                    }

                }

                break;

            case R.id.spinner1:

                items[1] = item;

                if (item.equals("Y")) {
                    //   utils.toast(2, 2, getString(R.string.select_date));

                    gasstatus.setText("Yes");
                    if (grocerystatus.getText().toString().equals("Done")
                            && kitchenequipmentstatus.getText().toString().equals("Done")
                            && waterstatus.getText().toString().equals("Yes")
                            && electricitystatus.getText().toString().equals("Yes")
                            && telephonestatus.getText().toString().equals("Yes")) {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Done");
                        homeessentialstatus.setTextColor(Color.BLUE);
                    } else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Pending");
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                } else {
                    gasstatus.setText("No");
                    if (grocerystatus.getText().toString().equals("Done")
                            && kitchenequipmentstatus.getText().toString().equals("Done")
                            && waterstatus.getText().toString().equals("Yes")
                            && gasstatus.getText().toString().equals("Yes")
                            && electricitystatus.getText().toString().equals("Yes")
                            && telephonestatus.getText().toString().equals("Yes")) {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Done");
                        homeessentialstatus.setTextColor(Color.BLUE);
                    } else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Pending");
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                }

                break;

            case R.id.spinner2:

                items[2] = item;

                if (item.equals("Y")) {
                    //  utils.toast(2, 2, getString(R.string.select_date));

                    electricitystatus.setText("Yes");
                    if (grocerystatus.getText().toString().equals("Done")
                            && kitchenequipmentstatus.getText().toString().equals("Done")
                            && gasstatus.getText().toString().equals("Yes")
                            && waterstatus.getText().toString().equals("Yes")
                            && telephonestatus.getText().toString().equals("Yes")) {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Done");
                        homeessentialstatus.setTextColor(Color.BLUE);
                    } else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Pending");
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                } else {
                    electricitystatus.setText("No");
                    if (grocerystatus.getText().toString().equals("Done")
                            && kitchenequipmentstatus.getText().toString().equals("Done")
                            && gasstatus.getText().toString().equals("Yes")
                            && waterstatus.getText().toString().equals("Yes")
                            && electricitystatus.getText().toString().equals("Yes")
                            && telephonestatus.getText().toString().equals("Yes")) {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Done");
                        homeessentialstatus.setTextColor(Color.BLUE);
                    } else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Pending");
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                }

                break;


            case R.id.spinner3:

                items[3] = item;

                if (item.equals("Y")) {
                    //   utils.toast(2, 2, getString(R.string.select_date));

                    telephonestatus.setText("Yes");
                    if (grocerystatus.getText().toString().equals("Done")
                            && kitchenequipmentstatus.getText().toString().equals("Done")
                            && gasstatus.getText().toString().equals("Yes")
                            && electricitystatus.getText().toString().equals("Yes")
                            && waterstatus.getText().toString().equals("Yes")) {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Done");
                        homeessentialstatus.setTextColor(Color.BLUE);
                    } else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Pending");
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                } else {
                    telephonestatus.setText("No");
                    if (grocerystatus.getText().toString().equals("Done")
                            && kitchenequipmentstatus.getText().toString().equals("Done")
                            && gasstatus.getText().toString().equals("Yes")
                            && electricitystatus.getText().toString().equals("Yes")
                            && waterstatus.getText().toString().equals("Yes")
                            && telephonestatus.getText().toString().equals("Yes")) {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Done");
                        homeessentialstatus.setTextColor(Color.BLUE);
                    } else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Pending");
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                }

                break;
        }


        if (waterstatus.getText().toString().equals("Yes")
                && gasstatus.getText().toString().equals("Yes")
                && electricitystatus.getText().toString().equals("Yes")
                && telephonestatus.getText().toString().equals("Yes")) {
            utilitystatus.setVisibility(View.VISIBLE);
            utilitystatus.setText("Done");
            utilitystatus.setTextColor(Color.BLUE);
        } else {
            utilitystatus.setVisibility(View.VISIBLE);
            utilitystatus.setText("Pending");
            utilitystatus.setTextColor(Color.RED);

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onResume() {

        super.onResume();


        utils = new Utils(CheckInCareProcess.this);

        try {

            if (buttonHallAdd != null) {
                buttonHallAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isHallFlag = 1;
                        mainlinearlayout.requestFocus();
                        if (IMAGE_COUNT < 20) {

                            utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                                    + ".jpeg", null, CheckInCareProcess.this, false);
                        } else {
                            utils.toast(2, 2, "Maximum 20 Images only Allowed");
                        }
                    }
                });
            }
            if (buttonKitchenAdd != null) {
                buttonKitchenAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isHallFlag = 2;
                        mainlinearlayout.requestFocus();
                        if (IMAGE_COUNT < 20) {

                            utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                                    + ".jpeg", null, CheckInCareProcess.this, false);
                        } else {
                            utils.toast(2, 2, "Maximum 20 Images only Allowed");
                        }
                    }
                });
            }
            if (buttonWashroomAdd != null) {
                buttonWashroomAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isHallFlag = 3;
                        mainlinearlayout.requestFocus();
                        if (IMAGE_COUNT < 20) {

                            utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                                    + ".jpeg", null, CheckInCareProcess.this, false);
                        } else {
                            utils.toast(2, 2, "Maximum 20 Images only Allowed");
                        }
                    }
                });
            }
            if (buttonBedroomAdd != null) {
                buttonBedroomAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isHallFlag = 4;
                        mainlinearlayout.requestFocus();
                        if (IMAGE_COUNT < 20) {

                            utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                                    + ".jpeg", null, CheckInCareProcess.this, false);
                        } else {
                            utils.toast(2, 2, "Maximum 20 Images only Allowed");
                        }
                    }
                });
            }


            if (!bViewLoaded) {

                bViewLoaded = true;

                backgroundThreadHandler = new BackgroundThreadHandler();
                Thread backgroundThreadImages = new BackgroundThreadImages();
                backgroundThreadImages.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (hallstatus.getText().equals("Done")
                && kitchenstatus.getText().equals("Done")
                && washroomstatus.getText().equals("Done")
                && bedroomstatus.getText().equals("Done")) {

            uploadmediastatus.setVisibility(View.VISIBLE);
            uploadmediastatus.setText("Done");
            uploadmediastatus.setTextColor(Color.BLUE);
        } else {
            uploadmediastatus.setVisibility(View.VISIBLE);
            uploadmediastatus.setText("Pending");
            uploadmediastatus.setTextColor(Color.RED);
        }

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    /*public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("CheckInCareProcess Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }*/

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client2.connect();
        //AppIndex.AppIndexApi.start(client2, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //AppIndex.AppIndexApi.end(client2, getIndexApiAction());
        //client2.disconnect();
    }

    private class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            if (isHallFlag == 1) {
                addHallImages();
            }
            if (isHallFlag == 2) {
                addKitchenImages();
            }
            if (isHallFlag == 3) {
                addWashroomImages();
            }
            if (isHallFlag == 4) {
                addBedroomImages();
            }
        }
    }

    private class BackgroundThread extends Thread {
        @Override
        public void run() {

            if (isHallFlag == 1) {
                try {
                    for (int i = 0; i < imagePaths.size(); i++) {
                        Calendar calendar = Calendar.getInstance();
                        String strTime = String.valueOf(calendar.getTimeInMillis());
                        String strFileName = strTime + ".jpeg";

                        Date date = new Date();

                        File mCopyFile = utils.getInternalFileImages(strTime);
                        utils.copyFile(new File(imagePaths.get(i)), mCopyFile);

                        ImageModel imageModel = new ImageModel(strTime, "", strTime, Utils.convertDateToString(date), mCopyFile.getAbsolutePath());

                        imageModel.setmIsNew(true);

                        hallimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(), Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                        hallbitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(), Config.intWidth, Config.intHeight));

                        IMAGE_COUNT++;

                        hallImageCount++;
                    }
                    backgroundThreadHandler.sendEmptyMessage(0);
                } catch (OutOfMemoryError | Exception e) {
                    e.printStackTrace();
                }
            }
            if (isHallFlag == 2) {
                try {
                    for (int i = 0; i < imagePaths.size(); i++) {
                        Calendar calendar = Calendar.getInstance();
                        String strTime = String.valueOf(calendar.getTimeInMillis());
                        String strFileName = strTime + ".jpeg";

                        Date date = new Date();

                        File mCopyFile = utils.getInternalFileImages(strTime);
                        utils.copyFile(new File(imagePaths.get(i)), mCopyFile);

                        ImageModel imageModel = new ImageModel(strTime, "", strTime, Utils.convertDateToString(date), mCopyFile.getAbsolutePath());

                        imageModel.setmIsNew(true);

                        kitchenimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(), Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);


                        kitchenbitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(), Config.intWidth, Config.intHeight));

                        IMAGE_COUNT++;

                        kitchenImageCount++;
                    }
                    backgroundThreadHandler.sendEmptyMessage(0);
                } catch (OutOfMemoryError | Exception e) {
                    e.printStackTrace();
                }
            }
            if (isHallFlag == 3) {
                try {
                    for (int i = 0; i < imagePaths.size(); i++) {
                        Calendar calendar = Calendar.getInstance();
                        String strTime = String.valueOf(calendar.getTimeInMillis());
                        String strFileName = strTime + ".jpeg";

                        Date date = new Date();

                        File mCopyFile = utils.getInternalFileImages(strTime);
                        utils.copyFile(new File(imagePaths.get(i)), mCopyFile);

                        ImageModel imageModel = new ImageModel(strTime, "", strTime, Utils.convertDateToString(date), mCopyFile.getAbsolutePath());

                        imageModel.setmIsNew(true);

                        washroomimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(), Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);


                        washroombitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(), Config.intWidth, Config.intHeight));

                        IMAGE_COUNT++;

                        washroomImageCount++;
                    }
                    backgroundThreadHandler.sendEmptyMessage(0);
                } catch (OutOfMemoryError | Exception e) {
                    e.printStackTrace();
                }
            }
            if (isHallFlag == 4) {
                try {
                    for (int i = 0; i < imagePaths.size(); i++) {
                        Calendar calendar = Calendar.getInstance();
                        String strTime = String.valueOf(calendar.getTimeInMillis());
                        String strFileName = strTime + ".jpeg";

                        Date date = new Date();

                        File mCopyFile = utils.getInternalFileImages(strTime);
                        utils.copyFile(new File(imagePaths.get(i)), mCopyFile);

                        ImageModel imageModel = new ImageModel(strTime, "", strTime, Utils.convertDateToString(date), mCopyFile.getAbsolutePath());

                        imageModel.setmIsNew(true);

                        bedroomimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(), Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);


                        bedroombitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(), Config.intWidth, Config.intHeight));

                        IMAGE_COUNT++;

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
            if (isHallFlag == 1) {
                try {
                    if (strImageName != null && !strImageName.equalsIgnoreCase("")) {

                        Calendar calendar = Calendar.getInstance();
                        String strName = String.valueOf(calendar.getTimeInMillis());
                        String strFileName = strName + ".jpeg";

                        File mCopyFile = utils.getInternalFileImages(strName);

                        utils.copyFile(new File(strImageName), mCopyFile);

                        Date date = new Date();
                        ImageModel imageModel = new ImageModel(strName, "", strName, Utils.convertDateToString(date), mCopyFile.getAbsolutePath());
                        imageModel.setmIsNew(true);

                        hallimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(), Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                        hallbitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(), Config.intWidth, Config.intHeight));

                        hallImageCount++;

                        IMAGE_COUNT++;
                    }

                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
            if (isHallFlag == 2) {
                try {
                    if (strImageName != null && !strImageName.equalsIgnoreCase("")) {
                        Calendar calendar = Calendar.getInstance();
                        String strName = String.valueOf(calendar.getTimeInMillis());
                        String strFileName = strName + ".jpeg";

                        File mCopyFile = utils.getInternalFileImages(strName);

                        utils.copyFile(new File(strImageName), mCopyFile);

                        Date date = new Date();
                        ImageModel imageModel = new ImageModel(strName, "", strName, Utils.convertDateToString(date), mCopyFile.getAbsolutePath());
                        imageModel.setmIsNew(true);

                        kitchenimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(), Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                        kitchenbitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(), Config.intWidth, Config.intHeight));

                        kitchenImageCount++;

                        IMAGE_COUNT++;
                    }

                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
            if (isHallFlag == 3) {
                try {
                    if (strImageName != null && !strImageName.equalsIgnoreCase("")) {
                        Calendar calendar = Calendar.getInstance();
                        String strName = String.valueOf(calendar.getTimeInMillis());
                        String strFileName = strName + ".jpeg";

                        File mCopyFile = utils.getInternalFileImages(strName);

                        utils.copyFile(new File(strImageName), mCopyFile);

                        Date date = new Date();
                        ImageModel imageModel = new ImageModel(strName, "", strName, Utils.convertDateToString(date), mCopyFile.getAbsolutePath());
                        imageModel.setmIsNew(true);

                        washroomimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(), Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                        washroombitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(), Config.intWidth, Config.intHeight));

                        washroomImageCount++;

                        IMAGE_COUNT++;
                    }

                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
            if (isHallFlag == 4) {
                try {
                    if (strImageName != null && !strImageName.equalsIgnoreCase("")) {
                        Calendar calendar = Calendar.getInstance();
                        String strName = String.valueOf(calendar.getTimeInMillis());
                        String strFileName = strName + ".jpeg";

                        File mCopyFile = utils.getInternalFileImages(strName);

                        utils.copyFile(new File(strImageName), mCopyFile);

                        Date date = new Date();
                        ImageModel imageModel = new ImageModel(strName, "", strName, Utils.convertDateToString(date), mCopyFile.getAbsolutePath());
                        imageModel.setmIsNew(true);

                        bedroomimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(), Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                        bedroombitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(), Config.intWidth, Config.intHeight));

                        bedroomImageCount++;

                        IMAGE_COUNT++;
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
            if (isHallFlag == 1) {
                try {

                    for (ImageModel imageModel : hallimageModels) {
                        if (imageModel.getStrImageName() != null && !imageModel.getStrImageName().equalsIgnoreCase("")) {
                            hallbitmaps.add(utils.getBitmapFromFile(utils.getInternalFileImages(imageModel.getStrImageName()).getAbsolutePath(), Config.intWidth, Config.intHeight));

                            imageModel.setmIsNew(false);

                            IMAGE_COUNT++;
                        }
                    }
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
            if (isHallFlag == 2) {
                try {

                    for (ImageModel imageModel : kitchenimageModels) {
                        if (imageModel.getStrImageName() != null && !imageModel.getStrImageName().equalsIgnoreCase("")) {
                            kitchenbitmaps.add(utils.getBitmapFromFile(utils.getInternalFileImages(imageModel.getStrImageName()).getAbsolutePath(), Config.intWidth, Config.intHeight));

                            imageModel.setmIsNew(false);

                            IMAGE_COUNT++;
                        }
                    }
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
            if (isHallFlag == 3) {
                try {

                    for (ImageModel imageModel : washroomimageModels) {
                        if (imageModel.getStrImageName() != null && !imageModel.getStrImageName().equalsIgnoreCase("")) {
                            washroombitmaps.add(utils.getBitmapFromFile(utils.getInternalFileImages(imageModel.getStrImageName()).getAbsolutePath(), Config.intWidth, Config.intHeight));

                            imageModel.setmIsNew(false);

                            IMAGE_COUNT++;
                        }
                    }
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
            if (isHallFlag == 4) {
                try {

                    for (ImageModel imageModel : bedroomimageModels) {
                        if (imageModel.getStrImageName() != null && !imageModel.getStrImageName().equalsIgnoreCase("")) {
                            bedroombitmaps.add(utils.getBitmapFromFile(utils.getInternalFileImages(imageModel.getStrImageName()).getAbsolutePath(), Config.intWidth, Config.intHeight));

                            imageModel.setmIsNew(false);

                            IMAGE_COUNT++;
                        }
                    }
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
            backgroundThreadHandler.sendEmptyMessage(0);
        }
    }
}
