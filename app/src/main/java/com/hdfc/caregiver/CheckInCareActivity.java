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
import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UploadService;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Utils;
import com.hdfc.models.CheckInCareActivityModel;
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
import java.util.Collections;
import java.util.Date;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Admin on 01-07-2016.
 */
public class CheckInCareActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {
    private static final int DIALOG_DOWNLOAD_PROGRESS1 = 1;
    private static final String[] option = {"N", "Y"};
    public static Uri uri;
    public static Bitmap bitmap = null;
    private static String strImageName = "", strClientName = "";
    private static int IMAGE_COUNT = 0;
    private static Boolean editcheckincare = false;
    private static Utils utils;
    //private static ProgressDialog mProgress = null;
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
    //private static String strName;
    private static boolean isCompleted = false, bLoad;
    private static boolean bViewLoaded, mImageChanged;
    private static StorageService storageService;
    public String item = "";
    private int isClicked = 0;
    private int isHallFlag = 0;
    private boolean isClick = false;
    private Button buttonHallAdd;
    private Button buttonKitchenAdd;
    private Button buttonWashroomAdd;
    private Button buttonBedroomAdd;
    private EditText electronic, homeapplience, automobile, maidservices, kitchen_equipments,
            grocery, mediacomment, checkincarename;
    private TextView datetxt;
    private TextView txtwater;
    private TextView txtgas;
    private TextView txtelectricity;
    private TextView txttelephone;
    private TextView utilitystatus, waterstatus, gasstatus, electricitystatus, telephonestatus,
            equipmentstatus, grocerystatus, kitchenequipmentstatus, domestichelpstatus,
            uploadmediastatus, hallstatus, kitchenstatus, washroomstatus, bedroomstatus,
            homeessentialstatus;
    private String strDate;
    private int hallImageCount, kitchenImageCount, washroomImageCount,
            bedroomImageCount, hallImageUploadCount, kitchenImageUploadCount,
            washroomImageUploadCount, bedroomImageUploadCount;
    private boolean success;
    private LinearLayout layouthall, layoutkitchen, layoutwashroom, layoutbedroom, mainlinearlayout;
    private CheckBox electrocheck, homecheck, autocheck, kitchenequipcheck, grocerycheck,
            domesticcheck;
    private String valkitchen, valgrocery, valelectronic, valhomeapplience, valautomobile,
            valmaidservices, valmediacomment, valcheckincarename;
    private View focusView = null;
    private ProgressDialog mProgressDialog;
    private String items[], strSelectedDate;

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+
            // date.getTime();
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.

            strDate = Utils.writeFormatDateMY.format(date);
            strSelectedDate = Utils.queryFormat.format(date);

            String strwaterDate = Utils.writeFormat.format(date);
            String strelectricityDate = Utils.writeFormat.format(date);
            String strtelephoneDate = Utils.writeFormat.format(date);
            String strgasDate = Utils.writeFormat.format(date);

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

        storageService = new StorageService(CheckInCareActivity.this);

        layouthall = (LinearLayout) findViewById(R.id.linear_hall);
        layoutkitchen = (LinearLayout) findViewById(R.id.linear_kitchen);
        layoutwashroom = (LinearLayout) findViewById(R.id.linear_washroom);
        layoutbedroom = (LinearLayout) findViewById(R.id.linear_bedroom);
        //MultiBitmapLoader multiBitmapLoader = new MultiBitmapLoader(CheckInCareActivity.this);


        utils = new Utils(CheckInCareActivity.this);
        //ProgressDialog progressDialog = new ProgressDialog(CheckInCareActivity.this);
        //mProgress = new ProgressDialog(CheckInCareActivity.this);

        Date mydate = new Date();
        //strDate = Utils.writeFormatDateMY.format(mydate);
        String stDate = Utils.queryFormatday.format(mydate);
        datetxt = (TextView) findViewById(R.id.datetxt);

        /*if (datetxt != null) {
            datetxt.setText(strDate);
        }*/

        LinearLayout layoutDate = (LinearLayout) findViewById(R.id.linearDate);

        //datetxt.setText(strDate);

        if (layoutDate != null) {
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
        }

        checkincarename = (EditText) findViewById(R.id.checkincarename);
        if (checkincarename != null) {
            checkincarename.setText(stDate);
        }
        if (checkincarename != null) {
            checkincarename.setTextSize(15);
        }

        electrocheck = (CheckBox) findViewById(R.id.electrocheck);
        homecheck = (CheckBox) findViewById(R.id.homecheck);
        autocheck = (CheckBox) findViewById(R.id.autocheck);
        kitchenequipcheck = (CheckBox) findViewById(R.id.kitchenequipcheck);
        grocerycheck = (CheckBox) findViewById(R.id.grocerycheck);
        domesticcheck = (CheckBox) findViewById(R.id.domesticcheck);

        ImageView client = (ImageView) findViewById(R.id.clientimg);
        ImageView pick_date = (ImageView) findViewById(R.id.pick_date);
        ImageView pick_date2 = (ImageView) findViewById(R.id.pick_date2);
        ImageView pick_date3 = (ImageView) findViewById(R.id.pick_date3);
        ImageView pick_date4 = (ImageView) findViewById(R.id.pick_date4);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        Spinner spinner3 = (Spinner) findViewById(R.id.spinner3);

        buttonHallAdd = (Button) findViewById(R.id.buttonHallAdd);
        buttonKitchenAdd = (Button) findViewById(R.id.buttonKitchenAdd);
        buttonWashroomAdd = (Button) findViewById(R.id.buttonWashroomAdd);
        buttonBedroomAdd = (Button) findViewById(R.id.buttonBedroomAdd);

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

        if (Config.customerModel != null) {
            strClientName = Config.customerModel.getStrName();
            //strImageName = Config.customerModel.getStrCustomerID();
            strImageName = Config.customerModel.getStrImgUrl();
        }

        if (clientnametxt != null) {
            clientnametxt.setText(strClientName);
        }

        if (client != null) {
            Glide.with(CheckInCareActivity.this)
                    .load(strImageName)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(CheckInCareActivity.this))
                    .placeholder(R.drawable.person_icon)
                    .crossFade()
                    .into(client);
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

        if (domesticcheck.isChecked()) {

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
            hallstatus.setTextColor(Color.BLUE);
        }
        if (layoutkitchen != null) {
            kitchenstatus.setVisibility(View.VISIBLE);
            kitchenstatus.setText(getString(R.string.pending));
            kitchenstatus.setTextColor(Color.RED);
        } else {
            kitchenstatus.setVisibility(View.VISIBLE);
            kitchenstatus.setText(getString(R.string.done));
            kitchenstatus.setTextColor(Color.BLUE);
        }
        if (layoutwashroom != null) {
            washroomstatus.setVisibility(View.VISIBLE);
            washroomstatus.setText(getString(R.string.pending));
            washroomstatus.setTextColor(Color.RED);
        } else {
            washroomstatus.setVisibility(View.VISIBLE);
            washroomstatus.setText(getString(R.string.done));
            washroomstatus.setTextColor(Color.BLUE);
        }
        if (layoutbedroom != null) {
            bedroomstatus.setVisibility(View.VISIBLE);
            bedroomstatus.setText(getString(R.string.pending));
            bedroomstatus.setTextColor(Color.RED);
        } else {
            bedroomstatus.setVisibility(View.VISIBLE);
            bedroomstatus.setText(getString(R.string.done));
            bedroomstatus.setTextColor(Color.BLUE);
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

        buttonHallAdd.setOnClickListener(this);
        buttonKitchenAdd.setOnClickListener(this);
        buttonWashroomAdd.setOnClickListener(this);
        buttonBedroomAdd.setOnClickListener(this);

        ////////////////////////////////editcheckincare

        try {
            Bundle getBundle = getIntent().getExtras();
            //CheckInCareModel checkInCareModel = null;
            int mPosition = -1;
            if (getBundle != null) {
                editcheckincare = getBundle.getBoolean("editcheckincare");
                mPosition = getBundle.getInt("itemposition");
            } else {
                editcheckincare = false;
                mPosition = -1;
                //checkInCareModel = null;
            }

            if (editcheckincare && mPosition > -1) {

                //checkInCareModel = Config.checkInCareModels.get(mPosition);

                String checkcareid = Config.checkInCareModels.get(mPosition).getStrName();
                String topdate = Config.checkInCareModels.get(mPosition).getStrCurrentDate();
                String editcomment = Config.checkInCareModels.get(mPosition).getStrMediaComment();

                checkincarename.setText(checkcareid);
                datetxt.setText(topdate);
                strSelectedDate = topdate;
                mediacomment.setText(editcomment);

                ArrayList<CheckInCareActivityModel> activity = Config.checkInCareModels.
                        get(mPosition).getCheckInCareActivityModels();

                if (activity != null) {
                    for (int i = 0; i < activity.size(); i++) {
                        String subActivityName;
                        String status;
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

                               /* try {
                                     myNum = Integer.parseInt(dueStatus);
                                } catch(NumberFormatException nfe) {
                                    nfe.printStackTrace();
                                }*/

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
                                    if(!kitchen_equipments.getText().toString().equals(status)){
                                        kitchenequipcheck.setChecked(true);
                                    }
                                        kitchenequipcheck.setChecked(true);
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
                                    if(!grocery.getText().toString().equals(status)){
                                        grocerycheck.setChecked(true);
                                    }
                                    grocerycheck.setChecked(true);
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

                                if (subActivityName.equalsIgnoreCase("maid_services")){
                                    maidservices.setText(status);
                                    if(!maidservices.getText().toString().equals(status)){
                                        domesticcheck.setChecked(true);
                                    }
                                    domesticcheck.setChecked(true);
                                    if (domesticcheck.isChecked()) {

                                        domestichelpstatus.setVisibility(View.VISIBLE);
                                        domestichelpstatus.setText(getString(R.string.done));
                                        domestichelpstatus.setTextColor(Color.BLUE);
                                    } else {
                                        domestichelpstatus.setVisibility(View.VISIBLE);
                                        domestichelpstatus.setText(getString(R.string.pending));
                                        domestichelpstatus.setTextColor(Color.RED);
                                    }
                                }
                            }
                        } else if (activity.get(i).getStrActivityName().
                                equalsIgnoreCase("equipment_working_status")) {
                            subActivityModels = activity.get(i).getSubActivityModels();
                            for (int j = 0; j < subActivityModels.size(); j++) {
                                subActivityName = subActivityModels.get(j).getStrSubActivityName();
                                status = subActivityModels.get(j).getStrStatus();

                                if (subActivityName.equalsIgnoreCase("electronic")){
                                    electronic.setText(status);
                                    if(!electronic.getText().toString().equals(status)){
                                        electrocheck.setChecked(true);
                                    }
                                    electrocheck.setChecked(true);
                                }
                                if (subActivityName.equalsIgnoreCase("home_appliances")){
                                    homeapplience.setText(status);
                                    if(!homeapplience.getText().toString().equals(status)){
                                        homecheck.setChecked(true);
                                    }
                                    homecheck.setChecked(true);
                                }
                                if (subActivityName.equalsIgnoreCase("automobile")){
                                    automobile.setText(status);
                                    if(!automobile.getText().toString().equals(status)){
                                        autocheck.setChecked(true);
                                    }
                                    autocheck.setChecked(true);
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

                ArrayList<PictureModel> picture = Config.checkInCareModels.get(mPosition).
                        getPictureModels();
                if (picture != null) {

                    for (int k = 0; k < picture.size(); k++) {

                        if (picture.get(k).getStrRoomName().equalsIgnoreCase("hall")) {
                            hallimageModels = picture.get(k).getImageModels();
                           // setHallImages();

                        }
                        if (picture.get(k).getStrRoomName().equalsIgnoreCase("kitchen")) {
                            kitchenimageModels = picture.get(k).getImageModels();
                           // addKitchenImages();
                        }
                        if (picture.get(k).getStrRoomName().equalsIgnoreCase("washroom")) {
                            washroomimageModels = picture.get(k).getImageModels();
                          //  addWashroomImages();
                        }
                        if (picture.get(k).getStrRoomName().equalsIgnoreCase("bedroom")) {
                            bedroomimageModels = picture.get(k).getImageModels();
                          //  addBedroomImages();
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /////////////////////////////////////////end

        Button btn_close = (Button) findViewById(R.id.btn_close);
        Button btn_submit = (Button) findViewById(R.id.btn_submit);
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

                    if (kitchenequipcheck.isChecked()) {
                        if (TextUtils.isEmpty(valkitchen)) {
                            kitchen_equipments.setError(getString(R.string.error_field_required));
                            focusView = kitchen_equipments;
                            cancel = true;
                        }
                    }
                    if (grocerycheck.isChecked()) {
                        if (TextUtils.isEmpty(valgrocery)) {
                            grocery.setError(getString(R.string.error_field_required));
                            focusView = grocery;
                            cancel = true;
                        }
                    }
                    if (electrocheck.isChecked()) {
                        if (TextUtils.isEmpty(valelectronic)) {
                            electronic.setError(getString(R.string.error_field_required));
                            focusView = electronic;
                            cancel = true;
                        }
                    }
                    if (homecheck.isChecked()) {
                        if (TextUtils.isEmpty(valhomeapplience)) {
                            homeapplience.setError(getString(R.string.error_field_required));
                            focusView = homeapplience;
                            cancel = true;
                        }
                    }
                    if (autocheck.isChecked()) {
                        if (TextUtils.isEmpty(valautomobile)) {
                            automobile.setError(getString(R.string.error_field_required));
                            focusView = automobile;
                            cancel = true;
                        }
                    }
                    if (domesticcheck.isChecked()) {
                        if (TextUtils.isEmpty(valmaidservices)) {
                            maidservices.setError(getString(R.string.error_field_required));
                            focusView = maidservices;
                            cancel = true;
                        }
                    }

                    if (cancel) {
                        focusView.requestFocus();
                        isClick = false;
                    } else {

                        if (utils.isConnectingToInternet()) {

                            ////////////////////////////
                            boolean bFuture = true;


                            if (bFuture) {

                                    uploadHallImage();

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
                        + ".jpeg", null, CheckInCareActivity.this, false);

                break;
            case R.id.buttonKitchenAdd:
                utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                        + ".jpeg", null, CheckInCareActivity.this, false);

                break;
            case R.id.buttonWashroomAdd:
                utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                        + ".jpeg", null, CheckInCareActivity.this, false);

                break;
            case R.id.buttonBedroomAdd:
                utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                        + ".jpeg", null, CheckInCareActivity.this, false);

                break;


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
        if (domesticcheck.isChecked()) {

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
        goBack();
    }

    private void goBack() {
        Intent intent = new Intent(CheckInCareActivity.this, DashboardActivity.class);
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

                                            hallimageModels.get(hallImageUploadCount).
                                                    setmIsNew(false);
                                            hallimageModels.get(hallImageUploadCount).
                                                    setStrImageUrl(file.getUrl());

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
                                                if (kitchenimageModels != null
                                                        && kitchenimageModels.size() > 0) {
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
                                            if (kitchenimageModels != null && kitchenimageModels.
                                                    size() > 0) {
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
                                            if (kitchenImageUploadCount >= kitchenimageModels.
                                                    size()) {
                                                if (washroomimageModels != null &&
                                                        washroomimageModels.size() > 0) {
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
                                            if (washroomimageModels != null && washroomimageModels.
                                                    size() > 0) {
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

                final ImageModel mwashroomImageModel = washroomimageModels.
                        get(washroomImageUploadCount);

                if (mwashroomImageModel.ismIsNew()) {

                    UploadService uploadService = new UploadService(this);

                    uploadService.uploadImageCommon(mwashroomImageModel.getStrImagePath(),
                            mwashroomImageModel.getStrImageDesc(), mwashroomImageModel.
                                    getStrImageDesc(), Config.providerModel.getStrEmail(),
                            UploadFileType.IMAGE, new App42CallBack() {
                                public void onSuccess(Object response) {

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
                                            if (washroomImageUploadCount >= washroomimageModels.
                                                    size()) {
                                                if (bedroomimageModels != null
                                                        && bedroomimageModels.size() > 0) {
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
                                        if (washroomImageUploadCount >= washroomimageModels.
                                                size()) {
                                            if (bedroomimageModels != null && bedroomimageModels.
                                                    size() > 0) {
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
                                                    if(editcheckincare) {
                                                        editupdateJson();
                                                    }else {
                                                        updateJson();
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
                                                if(editcheckincare){
                                                    editupdateJson();
                                                }else {
                                                    updateJson();
                                                }
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
                                            if(editcheckincare){
                                                editupdateJson();
                                            }else {
                                                updateJson();
                                            }
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
                        if(editcheckincare){
                            editupdateJson();
                        } else {
                            updateJson();
                        }
                    } else {
                        uploadBedroomImage();
                    }
                }
            } else {
                if(editcheckincare){
                    editupdateJson();
                }else {
                    updateJson();
                }
            }
        } else {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();

            if(editcheckincare){
                editupdateJson();
            }else {
                updateJson();
            }

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
                jsonObjectCheckinCare.put("check_in_care_name", checkincarename.getText().
                        toString());

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

                        jsonObjectKitchenImages.put("image_url", kitchenImageModel.
                                getStrImageUrl());
                        jsonObjectKitchenImages.put("description", kitchenImageModel.
                                getStrImageDesc());
                        jsonObjectKitchenImages.put("date_time", kitchenImageModel.
                                getStrImageTime());

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

                        jsonObjectWashroomImages.put("image_url", washroomImageModel.
                                getStrImageUrl());
                        jsonObjectWashroomImages.put("description", washroomImageModel.
                                getStrImageDesc());
                        jsonObjectWashroomImages.put("date_time", washroomImageModel.
                                getStrImageTime());

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

                        jsonObjectBedroomImages.put("image_url", bedroomImageModel.
                                getStrImageUrl());
                        jsonObjectBedroomImages.put("description", bedroomImageModel.
                                getStrImageDesc());
                        jsonObjectBedroomImages.put("date_time", bedroomImageModel.
                                getStrImageTime());

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


                jsonObjectActivitiesEquipment.put("sub_activities",
                        jsonArraySubActivitiesEquipment);

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

                                    if (response.getJsonDocList().size() > 0) {

                                        Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);

                                        ///
                                        String values[] = {jsonDocument.getDocId(),
                                                jsonDocument.getUpdatedAt(),
                                                jsonDocument.getJsonDoc(),
                                                Config.collectionCheckInCare, "0", "", "1"};

                                        String selection = DbHelper.COLUMN_OBJECT_ID + " = ? and "
                                                + DbHelper.COLUMN_COLLECTION_NAME + "=?";

                                        // WHERE clause arguments
                                        String[] selectionArgs = {jsonDocument.getDocId(),
                                                Config.collectionCheckInCare};
                                        CareGiver.getDbCon().updateInsert(
                                                DbHelper.strTableNameCollection,
                                                selection, values, DbHelper.COLLECTION_FIELDS,
                                                selectionArgs);


                                        //////////////

                                      /*  String strActivityDate = Utils.convertDateToStringQueryLocal(Utils.
                                                convertStringToDateQuery(jsonObject.getString("activity_date").substring(0,
                                                        jsonObject.getString("activity_date").length() - 1)));*/

                                        String values1[] = {jsonDocument.getDocId(),
                                                "0",
                                                strSelectedDate};

                                        String selection1 = DbHelper.COLUMN_OBJECT_ID + " = ? and "
                                                + DbHelper.COLUMN_MILESTONE_ID + "=? ";

                                        String[] selectionArgs1 = {jsonDocument.getDocId(),
                                                "0"
                                        };

                                        CareGiver.getDbCon().updateInsert(
                                                DbHelper.strTableNameMilestone,
                                                selection1, values1, DbHelper.MILESTONE_FIELDS,
                                                selectionArgs1);

                                    }
                                    ///


                                    IMAGE_COUNT = 0;
                                    utils.toast(2, 2, getString(R.string.data_upload));
                                    Intent intent = new Intent(CheckInCareActivity.this,
                                            DashboardActivity.class);
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

    private void editupdateJson(){
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
                jsonObjectCheckinCare.put("check_in_care_name", checkincarename.getText().
                        toString());

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

                        jsonObjectKitchenImages.put("image_url", kitchenImageModel.
                                getStrImageUrl());
                        jsonObjectKitchenImages.put("description", kitchenImageModel.
                                getStrImageDesc());
                        jsonObjectKitchenImages.put("date_time", kitchenImageModel.
                                getStrImageTime());

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

                        jsonObjectWashroomImages.put("image_url", washroomImageModel.
                                getStrImageUrl());
                        jsonObjectWashroomImages.put("description", washroomImageModel.
                                getStrImageDesc());
                        jsonObjectWashroomImages.put("date_time", washroomImageModel.
                                getStrImageTime());

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

                        jsonObjectBedroomImages.put("image_url", bedroomImageModel.
                                getStrImageUrl());
                        jsonObjectBedroomImages.put("description", bedroomImageModel.
                                getStrImageDesc());
                        jsonObjectBedroomImages.put("date_time", bedroomImageModel.
                                getStrImageTime());

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


                jsonObjectActivitiesEquipment.put("sub_activities",
                        jsonArraySubActivitiesEquipment);

                jsonArrayActivities.put(jsonObjectActivitiesEquipment);
                ///////////////////////activity 3


                jsonObjectCheckinCare.put("activities", jsonArrayActivities);


               /* Config.checkInCareModels.get(mImageUploadCount).clearImageModel();
                Config.checkInCareModels.get(mImageUploadCount).setImageModels(mTHallImageModels);*/

                hallimageModels = mTHallImageModels;

            } catch (Exception e) {
                e.printStackTrace();
            }


            storageService.updateDocs(jsonObjectCheckinCare,
                    Config.checkInCareModel.getStrDocumentID(),
                    Config.collectionCheckInCare, new App42CallBack() {
                        @Override
                        public void onSuccess(Object response) {
                            Utils.log(response.toString(), "Success");
                            IMAGE_COUNT = 0;
                            utils.toast(2, 2, getString(R.string.data_upload));
                            Intent intent = new Intent(CheckInCareActivity.this,
                                    DashboardActivity.class);
                            Config.intSelectedMenu = Config.intClientScreen;
                            startActivity(intent);
                        }

                        @Override
                        public void onException(Exception e) {
                            utils.toast(2, 2, getString(R.string.warning_internet));
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
            hallstatus.setText(getString(R.string.done));
            hallstatus.setTextColor(Color.BLUE);
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

                                                    if (mImageModel.ismIsNew())
                                                        hallImageCount--;

                                                    mImageChanged = true;

                                                    hallimageModels.remove(mImageModel);

                                                    hallbitmaps.remove(mPosition);
                                                    if (hallImageCount < 1) {
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
            kitchenstatus.setTextColor(Color.BLUE);

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

                                                    if (mImageModel.ismIsNew())
                                                        kitchenImageCount--;

                                                    mImageChanged = true;

                                                    kitchenimageModels.remove(mImageModel);

                                                    kitchenbitmaps.remove(mPosition);

                                                    if (kitchenImageCount < 1) {
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
            washroomstatus.setTextColor(Color.BLUE);

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

                                                    if (mImageModel.ismIsNew())
                                                        washroomImageCount--;

                                                    mImageChanged = true;

                                                    washroomimageModels.remove(mImageModel);

                                                    washroombitmaps.remove(mPosition);
                                                    if (washroomImageCount < 1) {
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
            bedroomstatus.setTextColor(Color.BLUE);
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

                                                    mImageChanged = true;

                                                    bedroomimageModels.remove(mImageModel);

                                                    bedroombitmaps.remove(mPosition);

                                                    if (bedroomImageCount < 1) {
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
                    //   utils.toast(2, 2, getString(R.string.select_date));

                    gasstatus.setText(getString(R.string.yes));
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
                    //  utils.toast(2, 2, getString(R.string.select_date));

                    electricitystatus.setText(getString(R.string.yes));
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
                    //   utils.toast(2, 2, getString(R.string.select_date));

                    telephonestatus.setText(getString(R.string.yes));
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

    @Override
    protected void onResume() {

        super.onResume();


        utils = new Utils(CheckInCareActivity.this);

        try {

            if (buttonHallAdd != null) {
                buttonHallAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isHallFlag = 1;
                        mainlinearlayout.requestFocus();
                        if (IMAGE_COUNT < 20) {

                            utils.selectImage(String.valueOf(new Date().getDate() + ""
                                    + new Date().getTime())
                                    + ".jpeg", null, CheckInCareActivity.this, false);
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

                            utils.selectImage(String.valueOf(new Date().getDate() + ""
                                    + new Date().getTime())
                                    + ".jpeg", null, CheckInCareActivity.this, false);
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

                            utils.selectImage(String.valueOf(new Date().getDate() + ""
                                    + new Date().getTime())
                                    + ".jpeg", null, CheckInCareActivity.this, false);
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

                            utils.selectImage(String.valueOf(new Date().getDate() + ""
                                    + new Date().getTime())
                                    + ".jpeg", null, CheckInCareActivity.this, false);
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

                        ImageModel imageModel = new ImageModel(strTime, "", strTime,
                                Utils.convertDateToString(date), mCopyFile.getAbsolutePath());

                        imageModel.setmIsNew(true);

                        hallimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(),
                                Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                        hallbitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(),
                                Config.intWidth, Config.intHeight));

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

                        ImageModel imageModel = new ImageModel(strTime, "", strTime,
                                Utils.convertDateToString(date), mCopyFile.getAbsolutePath());

                        imageModel.setmIsNew(true);

                        kitchenimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(),
                                Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);


                        kitchenbitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(),
                                Config.intWidth, Config.intHeight));

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

                        ImageModel imageModel = new ImageModel(strTime, "", strTime,
                                Utils.convertDateToString(date), mCopyFile.getAbsolutePath());

                        imageModel.setmIsNew(true);

                        washroomimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(),
                                Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);


                        washroombitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(),
                                Config.intWidth, Config.intHeight));

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

                        ImageModel imageModel = new ImageModel(strTime, "", strTime,
                                Utils.convertDateToString(date), mCopyFile.getAbsolutePath());

                        imageModel.setmIsNew(true);

                        bedroomimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(),
                                Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);


                        bedroombitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(),
                                Config.intWidth, Config.intHeight));

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
                        ImageModel imageModel = new ImageModel(strName, "", strName,
                                Utils.convertDateToString(date), mCopyFile.getAbsolutePath());
                        imageModel.setmIsNew(true);

                        hallimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(),
                                Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                        hallbitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(),
                                Config.intWidth, Config.intHeight));

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
                        ImageModel imageModel = new ImageModel(strName, "", strName,
                                Utils.convertDateToString(date), mCopyFile.getAbsolutePath());
                        imageModel.setmIsNew(true);

                        kitchenimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(),
                                Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                        kitchenbitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(),
                                Config.intWidth, Config.intHeight));

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
                        ImageModel imageModel = new ImageModel(strName, "", strName,
                                Utils.convertDateToString(date), mCopyFile.getAbsolutePath());
                        imageModel.setmIsNew(true);

                        washroomimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(),
                                Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                        washroombitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(),
                                Config.intWidth, Config.intHeight));

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
                        ImageModel imageModel = new ImageModel(strName, "", strName,
                                Utils.convertDateToString(date), mCopyFile.getAbsolutePath());
                        imageModel.setmIsNew(true);

                        bedroomimageModels.add(imageModel);

                        utils.compressImageFromPath(mCopyFile.getAbsolutePath(),
                                Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                        bedroombitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(),
                                Config.intWidth, Config.intHeight));

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
                        if (imageModel.getStrImageName() != null
                                && !imageModel.getStrImageName().equalsIgnoreCase("")) {
                            hallbitmaps.add(utils.getBitmapFromFile(
                                    utils.getInternalFileImages(imageModel.getStrImageDesc()).
                                            getAbsolutePath(), Config.intWidth, Config.intHeight));

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
                        if (imageModel.getStrImageName() != null && !imageModel.getStrImageName().
                                equalsIgnoreCase("")) {
                            kitchenbitmaps.add(utils.getBitmapFromFile(utils.
                                    getInternalFileImages(imageModel.getStrImageDesc()).
                                    getAbsolutePath(), Config.intWidth, Config.intHeight));

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
                        if (imageModel.getStrImageName() != null && !imageModel.getStrImageName().
                                equalsIgnoreCase("")) {
                            washroombitmaps.add(utils.getBitmapFromFile(utils.
                                    getInternalFileImages(imageModel.getStrImageDesc()).
                                    getAbsolutePath(), Config.intWidth, Config.intHeight));

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
                        if (imageModel.getStrImageName() != null && !imageModel.getStrImageName().
                                equalsIgnoreCase("")) {
                            bedroombitmaps.add(utils.getBitmapFromFile(utils.
                                    getInternalFileImages(imageModel.getStrImageDesc()).
                                    getAbsolutePath(), Config.intWidth, Config.intHeight));

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
