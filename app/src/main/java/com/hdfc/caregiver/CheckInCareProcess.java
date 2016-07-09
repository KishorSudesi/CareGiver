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

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.hdfc.config.Config;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.libs.Utils;
import com.hdfc.models.ImageModel;
import com.hdfc.views.TouchImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Admin on 01-07-2016.
 */
public class CheckInCareProcess extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {
    private static final String[]option = {"N", "Y"};
    public static String strImageName = "",strClientName = "";
    public static Uri uri;
    public static Bitmap bitmap = null;
    public static int IMAGE_COUNT = 0;
    private static Utils utils;
    private static ProgressDialog mProgress = null;
    private static Handler backgroundThreadHandler;
    private static boolean isImageChanged=false;
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
    public String item = "";
    int isClicked = 0;
    int isHallFlag = 0;
    private ImageView pick_date, pick_date2, pick_date3, pick_date4;
    private ImageView client;
    private Spinner spinner, spinner1, spinner2, spinner3;
    private Button btn_submit, buttonHallAdd, buttonKitchenAdd, buttonWashroomAdd, buttonBedroomAdd;
    private EditText electronic, homeapplience, automobile, maidservices, kitchen_equipments, grocery, mediacomment;
    private TextView txtwater, txtgas, txtelectricity, txttelephone, clientnametxt;
    private TextView utilitystatus, waterstatus, gasstatus, electricitystatus, telephonestatus,
            equipmentstatus, grocerystatus, kitchenequipmentstatus, domestichelpstatus, uploadmediastatus, hallstatus,
            kitchenstatus, washroomstatus, bedroomstatus, homeessentialstatus;
    private String strwaterDate, strelectricityDate, strtelephoneDate, strgasDate, _strwaterDate,
            _strelectricityDate, _strtelephoneDate, _strgasDate;
    private ProgressDialog progressDialog;
    private int hallImageCount, kitchenImageCount, washroomImageCount, bedroomImageCount, mImageUploadCount;
    private boolean success;
    private MultiBitmapLoader multiBitmapLoader;
    private LinearLayout layouthall,layoutkitchen,layoutwashroom,layoutbedroom;
    private CheckBox electrocheck,homecheck,autocheck,kitchenequipcheck,grocerycheck,domesticcheck;




    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+
            // date.getTime();
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.

            strwaterDate = Utils.writeFormat.format(date);
            strelectricityDate = Utils.writeFormat.format(date);
            strtelephoneDate = Utils.writeFormat.format(date);
            strgasDate = Utils.writeFormat.format(date);

            _strwaterDate = Utils.readFormat.format(date);
            _strelectricityDate = Utils.readFormat.format(date);
            _strtelephoneDate = Utils.readFormat.format(date);
            _strgasDate = Utils.readFormat.format(date);

            if(isClicked==0) {
                txtwater.setText(strwaterDate);
            }
            if(isClicked==1) {
                txtgas.setText(strgasDate);
            }
            if(isClicked==2) {
                txtelectricity.setText(strelectricityDate);
            }
            if(isClicked==3) {
                txttelephone.setText(strtelephoneDate);
            }

        }

        @Override
        public void onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in_care);


        layouthall = (LinearLayout) findViewById(R.id.linear_hall);
        layoutkitchen = (LinearLayout)findViewById(R.id.linear_kitchen);
        layoutwashroom = (LinearLayout)findViewById(R.id.linear_washroom);
        layoutbedroom = (LinearLayout)findViewById(R.id.linear_bedroom);
        multiBitmapLoader = new MultiBitmapLoader(CheckInCareProcess.this);


        utils = new Utils(CheckInCareProcess.this);
        progressDialog = new ProgressDialog(CheckInCareProcess.this);
        mProgress = new ProgressDialog(CheckInCareProcess.this);

        electrocheck = (CheckBox)findViewById(R.id.electrocheck);
        homecheck = (CheckBox)findViewById(R.id.homecheck);
        autocheck= (CheckBox)findViewById(R.id.autocheck);
        kitchenequipcheck = (CheckBox)findViewById(R.id.kitchenequipcheck);
        grocerycheck = (CheckBox)findViewById(R.id.grocerycheck);
        domesticcheck = (CheckBox)findViewById(R.id.domesticcheck);

        client = (ImageView)findViewById(R.id.clientimg);
        pick_date = (ImageView)findViewById(R.id.pick_date);
        pick_date2 = (ImageView)findViewById(R.id.pick_date2);
        pick_date3 = (ImageView)findViewById(R.id.pick_date3);
        pick_date4 = (ImageView)findViewById(R.id.pick_date4);

        spinner =(Spinner)findViewById(R.id.spinner);
        spinner1 = (Spinner)findViewById(R.id.spinner1);
        spinner2 = (Spinner)findViewById(R.id.spinner2);
        spinner3 = (Spinner)findViewById(R.id.spinner3);

        buttonHallAdd = (Button)findViewById(R.id.buttonHallAdd);
        buttonKitchenAdd = (Button)findViewById(R.id.buttonKitchenAdd);
        buttonWashroomAdd = (Button)findViewById(R.id.buttonWashroomAdd);
        buttonBedroomAdd = (Button)findViewById(R.id.buttonBedroomAdd);
        btn_submit = (Button)findViewById(R.id.btn_submit);

        txtwater = (TextView)findViewById(R.id.water_propertytxt);
        txtgas = (TextView)findViewById(R.id.gastxt);
        txtelectricity = (TextView)findViewById(R.id.electricitytxt);
        txttelephone = (TextView)findViewById(R.id.telephonetxt);
        clientnametxt = (TextView)findViewById(R.id.clientnametxt);

        utilitystatus = (TextView)findViewById(R.id.utilitystatus);
        equipmentstatus = (TextView)findViewById(R.id.equipmentstatus);
        waterstatus = (TextView)findViewById(R.id.waterstatus);
        gasstatus = (TextView)findViewById(R.id.gasstatus);
        electricitystatus = (TextView)findViewById(R.id.electricitystatus);
        telephonestatus = (TextView)findViewById(R.id.telephonestatus);
        kitchenequipmentstatus = (TextView)findViewById(R.id.kitchenequipmentstatus);
        grocerystatus = (TextView)findViewById(R.id.grocerystatus);
        domestichelpstatus = (TextView)findViewById(R.id.domestichelpstatus);
        uploadmediastatus = (TextView)findViewById(R.id.uploadmediastatus);
        hallstatus = (TextView)findViewById(R.id.hallstatus);
        kitchenstatus = (TextView)findViewById(R.id.kitchenstatus);
        washroomstatus = (TextView)findViewById(R.id.washroomstatus);
        bedroomstatus = (TextView)findViewById(R.id.bedroomstatus);
        homeessentialstatus = (TextView)findViewById(R.id.homeessentialstatus);

        electronic = (EditText)findViewById(R.id.electronics);
        homeapplience = (EditText)findViewById(R.id.homeapplience);
        automobile = (EditText)findViewById(R.id.automobile);
        maidservices = (EditText)findViewById(R.id.maidservices);
        kitchen_equipments = (EditText)findViewById(R.id.kitchen_equipments);
        grocery  =(EditText)findViewById(R.id.grocery);
        mediacomment = (EditText)findViewById(R.id.mediacomment);

        if (Config.customerModel != null) {
            strClientName = Config.customerModel.getStrName();
            strImageName = Config.customerModel.getStrCustomerID();

        }

        clientnametxt.setText(strClientName);

        File fileImage = Utils.createFileInternal("images/" + utils.replaceSpace(strImageName));

        if(fileImage.exists()) {
            String filename = fileImage.getAbsolutePath();
            multiBitmapLoader.loadBitmap(filename, client);
        }else{
            client.setImageDrawable(this.getResources().getDrawable(R.drawable.person_icon));
        }

        if(electrocheck.isChecked()==true
                &&homecheck.isChecked()==true
                &&autocheck.isChecked()==true){

            equipmentstatus.setVisibility(View.VISIBLE);
            equipmentstatus.setText("Done");
            equipmentstatus.setTextColor(Color.BLUE);
        }else{
            equipmentstatus.setVisibility(View.VISIBLE);
            equipmentstatus.setText("Pending");
            equipmentstatus.setTextColor(Color.RED);
        }

        if(kitchenequipcheck.isChecked()==true){
            kitchenequipmentstatus.setVisibility(View.VISIBLE);
            kitchenequipmentstatus.setText("Done");
            kitchenequipmentstatus.setTextColor(Color.BLUE);
        }else{
            kitchenequipmentstatus.setVisibility(View.VISIBLE);
            kitchenequipmentstatus.setText("Pending");
            kitchenequipmentstatus.setTextColor(Color.RED);
        }

        if(grocerycheck.isChecked()==true){

            grocerystatus.setVisibility(View.VISIBLE);
            grocerystatus.setText("Done");
            grocerystatus.setTextColor(Color.BLUE);
        }else{
            grocerystatus.setVisibility(View.VISIBLE);
            grocerystatus.setText("Pending");
            grocerystatus.setTextColor(Color.RED);
        }

        if(domesticcheck.isChecked()==true){

            domestichelpstatus.setVisibility(View.VISIBLE);
            domestichelpstatus.setText("Done");
            domestichelpstatus.setTextColor(Color.BLUE);
        }else{
            domestichelpstatus.setVisibility(View.VISIBLE);
            domestichelpstatus.setText("Pending");
            domestichelpstatus.setTextColor(Color.RED);
        }

        if(layouthall!=null){
            hallstatus.setVisibility(View.VISIBLE);
            hallstatus.setText("Pending");
            hallstatus.setTextColor(Color.RED);
        }else{
            hallstatus.setVisibility(View.VISIBLE);
            hallstatus.setText("Done");
            hallstatus.setTextColor(Color.BLUE);
        }
        if(layoutkitchen!=null){
            kitchenstatus.setVisibility(View.VISIBLE);
            kitchenstatus.setText("Pending");
            kitchenstatus.setTextColor(Color.RED);
        }else{
            kitchenstatus.setVisibility(View.VISIBLE);
            kitchenstatus.setText("Done");
            kitchenstatus.setTextColor(Color.BLUE);
        }
        if(layoutwashroom!=null){
            washroomstatus.setVisibility(View.VISIBLE);
            washroomstatus.setText("Pending");
            washroomstatus.setTextColor(Color.RED);
        }else{
            washroomstatus.setVisibility(View.VISIBLE);
            washroomstatus.setText("Done");
            washroomstatus.setTextColor(Color.BLUE);
        }
        if(layoutbedroom!=null){
            bedroomstatus.setVisibility(View.VISIBLE);
            bedroomstatus.setText("Pending");
            bedroomstatus.setTextColor(Color.RED);
        }else{
            bedroomstatus.setVisibility(View.VISIBLE);
            bedroomstatus.setText("Done");
            bedroomstatus.setTextColor(Color.BLUE);
        }

        if(hallstatus.getText().equals("Done")
                &&kitchenstatus.getText().equals("Done")
                &&washroomstatus.getText().equals("Done")
                &&bedroomstatus.getText().equals("Done")){

            uploadmediastatus.setVisibility(View.VISIBLE);
            uploadmediastatus.setText("Done");
            uploadmediastatus.setTextColor(Color.BLUE);
        }else{
            uploadmediastatus.setVisibility(View.VISIBLE);
            uploadmediastatus.setText("Pending");
            uploadmediastatus.setTextColor(Color.RED);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CheckInCareProcess.this,
                android.R.layout.simple_spinner_item,option);

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
            backImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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


    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch(id) {
            case R.id.pick_date:
                isClicked = 0;
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();

                break;

            case R.id.pick_date2:
                isClicked = 1;
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
                break;

            case R.id.pick_date3:
                isClicked = 2;
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
                break;


            case R.id.pick_date4:
                isClicked = 3;
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
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
        if(electrocheck.isChecked()==true
                &&homecheck.isChecked()==true
                &&autocheck.isChecked()==true){

            equipmentstatus.setVisibility(View.VISIBLE);
            equipmentstatus.setText("Done");
            equipmentstatus.setTextColor(Color.BLUE);
        }else{
            equipmentstatus.setVisibility(View.VISIBLE);
            equipmentstatus.setText("Pending");
            equipmentstatus.setTextColor(Color.RED);
        }
        if(kitchenequipcheck.isChecked()==true){
            kitchenequipmentstatus.setVisibility(View.VISIBLE);
            kitchenequipmentstatus.setText("Done");
            kitchenequipmentstatus.setTextColor(Color.BLUE);
            if(grocerystatus.getText().toString().equals("Done")
                    &&waterstatus.getText().toString().equals("Yes")
                    &&gasstatus.getText().toString().equals("Yes")
                    &&electricitystatus.getText().toString().equals("Yes")
                    &&telephonestatus.getText().toString().equals("Yes")){
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText("Done");
                homeessentialstatus.setTextColor(Color.BLUE);
            }else {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText("Pending");
                homeessentialstatus.setTextColor(Color.RED);
            }
        }else{
            kitchenequipmentstatus.setVisibility(View.VISIBLE);
            kitchenequipmentstatus.setText("Pending");
            kitchenequipmentstatus.setTextColor(Color.RED);
            if(grocerystatus.getText().toString().equals("Done")
                    &&kitchenequipmentstatus.getText().toString().equals("Done")
                    &&waterstatus.getText().toString().equals("Yes")
                    &&gasstatus.getText().toString().equals("Yes")
                    &&electricitystatus.getText().toString().equals("Yes")
                    &&telephonestatus.getText().toString().equals("Yes")){
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText("Done");
                homeessentialstatus.setTextColor(Color.BLUE);
            }else {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText("Pending");
                homeessentialstatus.setTextColor(Color.RED);
            }
        }

        if(grocerycheck.isChecked()==true){
            grocerystatus.setVisibility(View.VISIBLE);
            grocerystatus.setText("Done");
            grocerystatus.setTextColor(Color.BLUE);
            if(kitchenequipmentstatus.getText().toString().equals("Done")
                    &&waterstatus.getText().toString().equals("Yes")
                    &&gasstatus.getText().toString().equals("Yes")
                    &&electricitystatus.getText().toString().equals("Yes")
                    &&telephonestatus.getText().toString().equals("Yes")){
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText("Done");
                homeessentialstatus.setTextColor(Color.BLUE);
            }else {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText("Pending");
                homeessentialstatus.setTextColor(Color.RED);
            }
        }else{
            grocerystatus.setVisibility(View.VISIBLE);
            grocerystatus.setText("Pending");
            grocerystatus.setTextColor(Color.RED);
            if(kitchenequipmentstatus.getText().toString().equals("Done")
                    &&grocerystatus.getText().toString().equals("Done")
                    &&waterstatus.getText().toString().equals("Yes")
                    &&gasstatus.getText().toString().equals("Yes")
                    &&electricitystatus.getText().toString().equals("Yes")
                    &&telephonestatus.getText().toString().equals("Yes")){
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText("Done");
                homeessentialstatus.setTextColor(Color.BLUE);
            }else {
                homeessentialstatus.setVisibility(View.VISIBLE);
                homeessentialstatus.setText("Pending");
                homeessentialstatus.setTextColor(Color.RED);
            }
        }
        if(domesticcheck.isChecked()==true){

            domestichelpstatus.setVisibility(View.VISIBLE);
            domestichelpstatus.setText("Done");
            domestichelpstatus.setTextColor(Color.BLUE);
        }else{
            domestichelpstatus.setVisibility(View.VISIBLE);
            domestichelpstatus.setText("Pending");
            domestichelpstatus.setTextColor(Color.RED);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        goBack();
    }

    private void goBack() {
        Intent intent = new Intent(CheckInCareProcess.this, DashboardActivity.class);
        Config.intSelectedMenu = Config.intClientScreen;
        startActivity(intent);
        finish();
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

        if (hallimageModels != null && hallimageModels.size()>0) {

            hallstatus.setVisibility(View.VISIBLE);
            hallstatus.setText("Done");
            hallstatus.setTextColor(Color.BLUE);
            if(kitchenstatus.getText().toString().equals("Done")
                    &&washroomstatus.getText().toString().equals("Done")
                    &&bedroomstatus.getText().toString().equals("Done")){

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Done");
                uploadmediastatus.setTextColor(Color.BLUE);
            }else {
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
                                                    if(hallImageCount<1) {
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
        } else{
            hallstatus.setVisibility(View.VISIBLE);
            hallstatus.setText("Pending");
            hallstatus.setTextColor(Color.RED);
            if(kitchenstatus.getText().toString().equals("Done")
                    &&hallstatus.getText().toString().equals("Done")
                    &&washroomstatus.getText().toString().equals("Done")
                    &&bedroomstatus.getText().toString().equals("Done")){

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Done");
                uploadmediastatus.setTextColor(Color.BLUE);
            }else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Pending");
                uploadmediastatus.setTextColor(Color.RED);
            }

        }

    }

    private void addKitchenImages() {

        layoutkitchen.removeAllViews();

        if (kitchenimageModels != null && kitchenimageModels.size()>0) {

            kitchenstatus.setVisibility(View.VISIBLE);
            kitchenstatus.setText("Done");
            kitchenstatus.setTextColor(Color.BLUE);

            if(hallstatus.getText().toString().equals("Done")
                    &&washroomstatus.getText().toString().equals("Done")
                    &&bedroomstatus.getText().toString().equals("Done")){

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Done");
                uploadmediastatus.setTextColor(Color.BLUE);
            }else {
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

                                                    if(kitchenImageCount<1){
                                                        kitchenstatus.setVisibility(View.VISIBLE);
                                                        kitchenstatus.setText("Pending");
                                                        kitchenstatus.setTextColor(Color.RED);

                                                        if(hallstatus.getText().toString().equals("Done")
                                                                &&kitchenstatus.getText().toString().equals("Done")
                                                                &&washroomstatus.getText().toString().equals("Done")
                                                                &&bedroomstatus.getText().toString().equals("Done")){

                                                            uploadmediastatus.setVisibility(View.VISIBLE);
                                                            uploadmediastatus.setText("Done");
                                                            uploadmediastatus.setTextColor(Color.BLUE);
                                                        }else {
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
        }else{
            kitchenstatus.setVisibility(View.VISIBLE);
            kitchenstatus.setText("Pending");
            kitchenstatus.setTextColor(Color.RED);
            if(hallstatus.getText().toString().equals("Done")
                    &&kitchenstatus.getText().toString().equals("Done")
                    &&washroomstatus.getText().toString().equals("Done")
                    &&bedroomstatus.getText().toString().equals("Done")){

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Done");
                uploadmediastatus.setTextColor(Color.BLUE);
            }else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Pending");
                uploadmediastatus.setTextColor(Color.RED);
            }

        }
    }

    private void addWashroomImages() {

        layoutwashroom.removeAllViews();

        if (washroomimageModels != null && washroomimageModels.size()>0) {

            washroomstatus.setVisibility(View.VISIBLE);
            washroomstatus.setText("Done");
            washroomstatus.setTextColor(Color.BLUE);

            if(hallstatus.getText().toString().equals("Done")
                    &&kitchenstatus.getText().toString().equals("Done")
                    &&bedroomstatus.getText().toString().equals("Done")){

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Done");
                uploadmediastatus.setTextColor(Color.BLUE);
            }else {
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
                                                    if(washroomImageCount<1){
                                                        washroomstatus.setVisibility(View.VISIBLE);
                                                        washroomstatus.setText("Pending");
                                                        washroomstatus.setTextColor(Color.RED);
                                                        if(hallstatus.getText().toString().equals("Done")
                                                                &&washroomstatus.getText().toString().equals("Done")
                                                                &&kitchenstatus.getText().toString().equals("Done")
                                                                &&bedroomstatus.getText().toString().equals("Done")){

                                                            uploadmediastatus.setVisibility(View.VISIBLE);
                                                            uploadmediastatus.setText("Done");
                                                            uploadmediastatus.setTextColor(Color.BLUE);
                                                        }else {
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
        } else{
            washroomstatus.setVisibility(View.VISIBLE);
            washroomstatus.setText("Pending");
            washroomstatus.setTextColor(Color.RED);
            if(hallstatus.getText().toString().equals("Done")
                    &&washroomstatus.getText().toString().equals("Done")
                    &&kitchenstatus.getText().toString().equals("Done")
                    &&bedroomstatus.getText().toString().equals("Done")){

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Done");
                uploadmediastatus.setTextColor(Color.BLUE);
            }else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Pending");
                uploadmediastatus.setTextColor(Color.RED);
            }

        }
    }

    private void addBedroomImages() {

        layoutbedroom.removeAllViews();

        if (bedroomimageModels != null && bedroomimageModels.size()>0) {

            bedroomstatus.setVisibility(View.VISIBLE);
            bedroomstatus.setText("Done");
            bedroomstatus.setTextColor(Color.BLUE);
            if(hallstatus.getText().toString().equals("Done")
                    &&kitchenstatus.getText().toString().equals("Done")
                    &&washroomstatus.getText().toString().equals("Done")){

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Done");
                uploadmediastatus.setTextColor(Color.BLUE);
            }else {
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

                                                    if(bedroomImageCount<1){
                                                        bedroomstatus.setVisibility(View.VISIBLE);
                                                        bedroomstatus.setText("Pending");
                                                        bedroomstatus.setTextColor(Color.RED);
                                                        if(hallstatus.getText().toString().equals("Done")
                                                                &&bedroomstatus.getText().toString().equals("Done")
                                                                &&kitchenstatus.getText().toString().equals("Done")
                                                                &&washroomstatus.getText().toString().equals("Done")){

                                                            uploadmediastatus.setVisibility(View.VISIBLE);
                                                            uploadmediastatus.setText("Done");
                                                            uploadmediastatus.setTextColor(Color.BLUE);
                                                        }else {
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
        } else{
            bedroomstatus.setVisibility(View.VISIBLE);
            bedroomstatus.setText("Pending");
            bedroomstatus.setTextColor(Color.RED);
            if(hallstatus.getText().toString().equals("Done")
                    &&bedroomstatus.getText().toString().equals("Done")
                    &&kitchenstatus.getText().toString().equals("Done")
                    &&washroomstatus.getText().toString().equals("Done")){

                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Done");
                uploadmediastatus.setTextColor(Color.BLUE);
            }else {
                uploadmediastatus.setVisibility(View.VISIBLE);
                uploadmediastatus.setText("Pending");
                uploadmediastatus.setTextColor(Color.RED);
            }


        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        item = parent.getItemAtPosition(position).toString();

        switch(parent.getId()) {
            case R.id.spinner:

                if(item.equals("Y")){
                    waterstatus.setText("Yes");
                    if(grocerystatus.getText().toString().equals("Done")
                            &&kitchenequipmentstatus.getText().toString().equals("Done")
                            &&gasstatus.getText().toString().equals("Yes")
                            &&electricitystatus.getText().toString().equals("Yes")
                            &&telephonestatus.getText().toString().equals("Yes")){
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Done");
                        homeessentialstatus.setTextColor(Color.BLUE);
                    }else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Pending");
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                }else{
                    waterstatus.setText("No");
                    if(grocerystatus.getText().toString().equals("Done")
                            &&kitchenequipmentstatus.getText().toString().equals("Done")
                            &&waterstatus.getText().toString().equals("Yes")
                            &&gasstatus.getText().toString().equals("Yes")
                            &&electricitystatus.getText().toString().equals("Yes")
                            &&telephonestatus.getText().toString().equals("Yes")){
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Done");
                        homeessentialstatus.setTextColor(Color.BLUE);
                    }else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Pending");
                        homeessentialstatus.setTextColor(Color.RED);
                    }

                }

                break;

            case R.id.spinner1:

                if(item.equals("Y")){
                    gasstatus.setText("Yes");
                    if(grocerystatus.getText().toString().equals("Done")
                            &&kitchenequipmentstatus.getText().toString().equals("Done")
                            &&waterstatus.getText().toString().equals("Yes")
                            &&electricitystatus.getText().toString().equals("Yes")
                            &&telephonestatus.getText().toString().equals("Yes")){
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Done");
                        homeessentialstatus.setTextColor(Color.BLUE);
                    }else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Pending");
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                }else{
                    gasstatus.setText("No");
                    if(grocerystatus.getText().toString().equals("Done")
                            &&kitchenequipmentstatus.getText().toString().equals("Done")
                            &&waterstatus.getText().toString().equals("Yes")
                            &&gasstatus.getText().toString().equals("Yes")
                            &&electricitystatus.getText().toString().equals("Yes")
                            &&telephonestatus.getText().toString().equals("Yes")){
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Done");
                        homeessentialstatus.setTextColor(Color.BLUE);
                    }else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Pending");
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                }

                break;

            case R.id.spinner2:

                if(item.equals("Y")){
                    electricitystatus.setText("Yes");
                    if(grocerystatus.getText().toString().equals("Done")
                            &&kitchenequipmentstatus.getText().toString().equals("Done")
                            &&gasstatus.getText().toString().equals("Yes")
                            &&waterstatus.getText().toString().equals("Yes")
                            &&telephonestatus.getText().toString().equals("Yes")){
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Done");
                        homeessentialstatus.setTextColor(Color.BLUE);
                    }else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Pending");
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                }else{
                    electricitystatus.setText("No");
                    if(grocerystatus.getText().toString().equals("Done")
                            &&kitchenequipmentstatus.getText().toString().equals("Done")
                            &&gasstatus.getText().toString().equals("Yes")
                            &&waterstatus.getText().toString().equals("Yes")
                            &&electricitystatus.getText().toString().equals("Yes")
                            &&telephonestatus.getText().toString().equals("Yes")){
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Done");
                        homeessentialstatus.setTextColor(Color.BLUE);
                    }else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Pending");
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                }

                break;


            case R.id.spinner3:

                if(item.equals("Y")){
                    telephonestatus.setText("Yes");
                    if(grocerystatus.getText().toString().equals("Done")
                            &&kitchenequipmentstatus.getText().toString().equals("Done")
                            &&gasstatus.getText().toString().equals("Yes")
                            &&electricitystatus.getText().toString().equals("Yes")
                            &&waterstatus.getText().toString().equals("Yes")){
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Done");
                        homeessentialstatus.setTextColor(Color.BLUE);
                    }else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Pending");
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                }else{
                    telephonestatus.setText("No");
                    if(grocerystatus.getText().toString().equals("Done")
                            &&kitchenequipmentstatus.getText().toString().equals("Done")
                            &&gasstatus.getText().toString().equals("Yes")
                            &&electricitystatus.getText().toString().equals("Yes")
                            &&waterstatus.getText().toString().equals("Yes")
                            &&telephonestatus.getText().toString().equals("Yes")){
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Done");
                        homeessentialstatus.setTextColor(Color.BLUE);
                    }else {
                        homeessentialstatus.setVisibility(View.VISIBLE);
                        homeessentialstatus.setText("Pending");
                        homeessentialstatus.setTextColor(Color.RED);
                    }
                }

                break;
        }


        if(waterstatus.getText().toString().equals("Yes")
                &&gasstatus.getText().toString().equals("Yes")
                &&electricitystatus.getText().toString().equals("Yes")
                &&telephonestatus.getText().toString().equals("Yes")){
            utilitystatus.setVisibility(View.VISIBLE);
            utilitystatus.setText("Done");
            utilitystatus.setTextColor(Color.BLUE);
        }else {
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

        if(hallstatus.getText().equals("Done")
                &&kitchenstatus.getText().equals("Done")
                &&washroomstatus.getText().equals("Done")
                &&bedroomstatus.getText().equals("Done")){

            uploadmediastatus.setVisibility(View.VISIBLE);
            uploadmediastatus.setText("Done");
            uploadmediastatus.setTextColor(Color.BLUE);
        }else{
            uploadmediastatus.setVisibility(View.VISIBLE);
            uploadmediastatus.setText("Pending");
            uploadmediastatus.setTextColor(Color.RED);
        }

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

                        ImageModel imageModel = new ImageModel(strTime, "", strTime, utils.convertDateToString(date), mCopyFile.getAbsolutePath());

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

                        ImageModel imageModel = new ImageModel(strTime, "", strTime, utils.convertDateToString(date), mCopyFile.getAbsolutePath());

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

                        ImageModel imageModel = new ImageModel(strTime, "", strTime, utils.convertDateToString(date), mCopyFile.getAbsolutePath());

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

                        ImageModel imageModel = new ImageModel(strTime, "", strTime, utils.convertDateToString(date), mCopyFile.getAbsolutePath());

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

                        File mCopyFile = utils.getInternalFileImages(strName);

                        utils.copyFile(new File(strImageName), mCopyFile);

                        Date date = new Date();
                        ImageModel imageModel = new ImageModel(strName, "", strName, utils.convertDateToString(date), mCopyFile.getAbsolutePath());
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

                        File mCopyFile = utils.getInternalFileImages(strName);

                        utils.copyFile(new File(strImageName), mCopyFile);

                        Date date = new Date();
                        ImageModel imageModel = new ImageModel(strName, "", strName, utils.convertDateToString(date), mCopyFile.getAbsolutePath());
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

                        File mCopyFile = utils.getInternalFileImages(strName);

                        utils.copyFile(new File(strImageName), mCopyFile);

                        Date date = new Date();
                        ImageModel imageModel = new ImageModel(strName, "", strName, utils.convertDateToString(date), mCopyFile.getAbsolutePath());
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

                        File mCopyFile = utils.getInternalFileImages(strName);

                        utils.copyFile(new File(strImageName), mCopyFile);

                        Date date = new Date();
                        ImageModel imageModel = new ImageModel(strName, "", strName, utils.convertDateToString(date), mCopyFile.getAbsolutePath());
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
