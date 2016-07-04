package com.hdfc.caregiver;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
import com.hdfc.libs.Utils;
import com.hdfc.models.ImageModel;
import com.hdfc.views.RoundedImageView;
import com.hdfc.views.TouchImageView;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Admin on 01-07-2016.
 */
public class CheckInCareProcess extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {
    private ImageView pick_date,pick_date2,pick_date3,pick_date4;
    static RoundedImageView client;
    private Spinner spinner,spinner1,spinner2,spinner3;
    private Button btn_submit,buttonHallAdd,buttonKitchenAdd,buttonWashroomAdd,buttonBedroomAdd;
    private EditText electronic,homeapplience,automobile,maidservices,kitchen_equipments,grocery;
    private TextView txtwater,txtgas,txtelectricity,txttelephone;
    private TextView utilitystatus,waterstatus,gasstatus,electricitystatus,telephonestatus,equipmentstatus;
    private String strwaterDate,strelectricityDate,strtelephoneDate,strgasDate,_strwaterDate,_strelectricityDate,_strtelephoneDate,_strgasDate;
    private static final String[]option = {"N", "Y"};
    int isClicked = 0;
    private static Utils utils;
    private static ProgressDialog mProgress = null;
    private ProgressDialog progressDialog;
    public static String strImageName = "";
    public static Uri uri;
    private static Handler backgroundThreadHandler;
    public static Bitmap bitmap = null;
    private static boolean isImageChanged=false;
    private static ArrayList<String> imagePaths = new ArrayList<>();
    private static ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private static ArrayList<ImageModel> imageModels;
    public static int IMAGE_COUNT = 0;
    private int mImageCount, mImageUploadCount;
    private static String strName;
    private static boolean bLoad, isCompleted = false;
    private boolean success;
    private static boolean bViewLoaded, mImageChanged;
    private LinearLayout layout;
    private CheckBox electrocheck,homecheck,autocheck;




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

        layout = (LinearLayout) findViewById(R.id.linear_hall);


        utils = new Utils(CheckInCareProcess.this);
        progressDialog = new ProgressDialog(CheckInCareProcess.this);
        mProgress = new ProgressDialog(CheckInCareProcess.this);

        electrocheck = (CheckBox)findViewById(R.id.electrocheck);
        homecheck = (CheckBox)findViewById(R.id.homecheck);
        autocheck= (CheckBox)findViewById(R.id.autocheck);

        client = (RoundedImageView)findViewById(R.id.clientimg);
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

        utilitystatus = (TextView)findViewById(R.id.utilitystatus);
        equipmentstatus = (TextView)findViewById(R.id.equipmentstatus);
        waterstatus = (TextView)findViewById(R.id.waterstatus);
        gasstatus = (TextView)findViewById(R.id.gasstatus);
        electricitystatus = (TextView)findViewById(R.id.electricitystatus);
        telephonestatus = (TextView)findViewById(R.id.telephonestatus);



        electronic = (EditText)findViewById(R.id.electronics);
        homeapplience = (EditText)findViewById(R.id.homeapplience);
        automobile = (EditText)findViewById(R.id.automobile);
        maidservices = (EditText)findViewById(R.id.maidservices);
        kitchen_equipments = (EditText)findViewById(R.id.kitchen_equipments);
        grocery  =(EditText)findViewById(R.id.grocery);

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
         /*   case R.id.buttonHallAdd:
                utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                        + ".jpeg", null, CheckInCareProcess.this, true);

                break;
            case R.id.buttonKitchenAdd:
                utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                        + ".jpeg", null, CheckInCareProcess.this, true);

                break;
            case R.id.buttonWashroomAdd:
                utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                        + ".jpeg", null, CheckInCareProcess.this, true);

                break;
            case R.id.buttonBedroomAdd:
                utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                        + ".jpeg", null, CheckInCareProcess.this, true);

                break;*/


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

                mProgress.setMessage(getString(R.string.loading));
                mProgress.show();
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

    private class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            addImages();
        }
    }

    private class BackgroundThread extends Thread {
        @Override
        public void run() {

            try {
                for(int i=0;i<imagePaths.size();i++) {
                    Calendar calendar = Calendar.getInstance();
                    String strTime = String.valueOf(calendar.getTimeInMillis());
                    String strFileName = strTime + ".jpeg";

                    //File galleryFile = utils.createFileInternalImage(strFileName);
                    //strImageName = galleryFile.getAbsolutePath();
                    Date date = new Date();

                    File mCopyFile = utils.getInternalFileImages(strTime);
                    utils.copyFile(new File(imagePaths.get(i)), mCopyFile);


                    //utils.copyFile(new File(strImageName), mCopyFile);
                    //

                    ImageModel imageModel = new ImageModel(strTime, "", strTime, utils.convertDateToString(date), mCopyFile.getAbsolutePath());

                    imageModel.setmIsNew(true);

                    imageModels.add(imageModel);
                    //

                    utils.compressImageFromPath(mCopyFile.getAbsolutePath(), Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);


                    bitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(), Config.intWidth, Config.intHeight));

                    IMAGE_COUNT++;

                    mImageCount++;
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (OutOfMemoryError | Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class BackgroundThreadCamera extends Thread {
        @Override
        public void run() {
            try {
                if (strImageName != null && !strImageName.equalsIgnoreCase("")) {

                    File mCopyFile = utils.getInternalFileImages(strName);

                    utils.copyFile(new File(strImageName), mCopyFile);

                    Date date = new Date();
                    ImageModel imageModel = new ImageModel(strName, "", strName, utils.convertDateToString(date), mCopyFile.getAbsolutePath());
                    imageModel.setmIsNew(true);
                    //arrayListImageModel.add(imageModel);

                    imageModels.add(imageModel);

                    utils.compressImageFromPath(mCopyFile.getAbsolutePath(), Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                    bitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(), Config.intWidth, Config.intHeight));

                    mImageCount++;

                    IMAGE_COUNT++;
                }

            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
            backgroundThreadHandler.sendEmptyMessage(0);
        }
    }

    private void addImages() {

        layout.removeAllViews();

        if (imageModels != null) {
            for (int i = 0; i < imageModels.size(); i++) {
                try {
                    final ImageView imageView = new ImageView(CheckInCareProcess.this);
                    imageView.setPadding(0, 0, 3, 0);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(10, 10, 10, 10);

                    Utils.log(String.valueOf(imageModels.get(i).getStrImageName() + " # " +
                            imageModels.get(i).getStrImagePath()), " height 0 ");

                    Utils.log(String.valueOf(bitmaps.get(i).getHeight()), " height ");

                    imageView.setLayoutParams(layoutParams);
                    imageView.setImageBitmap(bitmaps.get(i));
                    imageView.setTag(imageModels.get(i));
                    imageView.setTag(R.id.three, i);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                    /*imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {



                            return false;
                        }
                    });*/

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
                                                        mImageCount--;

                                                    mImageChanged = true;

                                                    imageModels.remove(mImageModel);

                                                    bitmaps.remove(mPosition);
                                                }
                                                if (success) {
                                                    utils.toast(2, 2, getString(R.string.file_deleted));
                                                }
                                                arg0.dismiss();
                                                dialog.dismiss();
                                                addImages();
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
                                mOriginal.setImageBitmap(bitmaps.get(mPosition));
                                //, Config.intWidth, Config.intHeight)
                            } catch (OutOfMemoryError oOm) {
                                oOm.printStackTrace();
                            }
                            dialog.setCancelable(true);

                            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT); //Controlling width and height.
                            dialog.show();

                        }
                    });

                    layout.addView(imageView);
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
        }


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String item = parent.getItemAtPosition(position).toString();

        switch(parent.getId()) {
            case R.id.spinner:

                if(item.equals("Y")){
                    waterstatus.setText("Yes");
                }else{
                    waterstatus.setText("No");
                }

                break;

            case R.id.spinner1:

                if(item.equals("Y")){
                    gasstatus.setText("Yes");
                }else{
                    gasstatus.setText("No");
                }

                break;

            case R.id.spinner2:

                if(item.equals("Y")){
                    electricitystatus.setText("Yes");
                }else{
                    electricitystatus.setText("No");
                }

                break;


            case R.id.spinner3:

                if(item.equals("Y")){
                    telephonestatus.setText("Yes");
                }else{
                    telephonestatus.setText("No");
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
}
