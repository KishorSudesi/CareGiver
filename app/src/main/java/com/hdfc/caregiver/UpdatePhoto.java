package com.hdfc.caregiver;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.models.ImageModel;
import com.hdfc.views.TouchImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Sweekar on 09-06-2016.
 */
public class UpdatePhoto extends AppCompatActivity implements View.OnClickListener {

    private Button buttonHallAdd, buttonKitchenAdd, buttonWashroomAdd, buttonBedroomAdd, btn_submit;
    private TextView txthallStatus, txtKitchenStatus, txtWashroomStatus, txtBedStatus;

    private static ArrayList<String> imagePaths = new ArrayList<>();
    private static ArrayList<Bitmap> bitmapsHall = new ArrayList<>();
    private static ArrayList<Bitmap> bitmapsKitchen = new ArrayList<>();
    private static ArrayList<Bitmap> bitmapsWashroom = new ArrayList<>();
    private static ArrayList<Bitmap> bitmapsBedroom = new ArrayList<>();

    private static ArrayList<ImageModel> arrayListHallImageModel = new ArrayList<>();
    private static ArrayList<ImageModel> arrayListKitchenImageModel = new ArrayList<>();
    private static ArrayList<ImageModel> arrayListWashroomImageModel = new ArrayList<>();
    private static ArrayList<ImageModel> arrayListBedroomImageModel = new ArrayList<>();

    public static int IMAGE_COUNT = 0;

    private static Handler backgroundThreadHandlerHall;
    private static Handler backgroundThreadHandlerKitchen;
    private static Handler backgroundThreadHandlerWashroom;
    private static Handler backgroundThreadHandlerBedroom;

    private static String strName;
    private static String strImageName = "";
    private static LinearLayout linear_hall, linear_kitchen, linear_washroom, linear_bedroom;
    private Point p;
    private Utils utils;
    private String typeOfImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_photo);

        initResources();

    }

    @Override
    public void onClick(View V) {
        int id = V.getId();
        switch (id) {
            case R.id.buttonHallAdd:
                if (p != null)
                typeOfImage = "Hall";
                showStatusPopup(UpdatePhoto.this, p);
                break;

            case R.id.buttonKitchenAdd:
                typeOfImage = "Kitchen";
                showStatusPopup(UpdatePhoto.this, p);
                break;

            case R.id.buttonWashroomAdd:
                typeOfImage = "Washroom";
                showStatusPopup(UpdatePhoto.this, p);
                break;

            case R.id.buttonBedroomAdd:
                typeOfImage = "Bedroom";
                showStatusPopup(UpdatePhoto.this, p);
                break;

            case R.id.btn_submit:
                if(validate()){
                    utils.toast(2, 2, "Code to upload images");
                }else{
                    utils.toast(2, 2, "please select more images");
                }
        }
    }

    private void initResources() {
        utils = new Utils(UpdatePhoto.this);

        linear_hall = (LinearLayout) findViewById(R.id.linear_hall);
        linear_kitchen = (LinearLayout) findViewById(R.id.linear_kitchen);
        linear_washroom = (LinearLayout) findViewById(R.id.linear_washroom);
        linear_bedroom = (LinearLayout) findViewById(R.id.linear_bedroom);

        buttonHallAdd = (Button) findViewById(R.id.buttonHallAdd);
        buttonKitchenAdd = (Button) findViewById(R.id.buttonKitchenAdd);
        buttonWashroomAdd = (Button) findViewById(R.id.buttonWashroomAdd);
        buttonBedroomAdd = (Button) findViewById(R.id.buttonBedroomAdd);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        txthallStatus = (TextView) findViewById(R.id.txthallStatus);
        txtKitchenStatus = (TextView) findViewById(R.id.txtKitchenStatus);
        txtWashroomStatus = (TextView) findViewById(R.id.txtWashroomStatus);
        txtBedStatus = (TextView) findViewById(R.id.txtBedStatus);

        buttonHallAdd.setOnClickListener(this);
        buttonKitchenAdd.setOnClickListener(this);
        buttonWashroomAdd.setOnClickListener(this);
        buttonBedroomAdd.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];
        ImageView attach = (ImageView) findViewById(R.id.imgAttachHeaderTaskDetail);

        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        if (attach != null) {
            attach.getLocationOnScreen(location);
        }

        //Initialize the Point with x, and y positions
        p = new Point();
        p.x = location[0];
        p.y = location[1];
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IMAGE_COUNT = 0;
        arrayListHallImageModel.clear();
        arrayListKitchenImageModel.clear();
        arrayListWashroomImageModel.clear();
        arrayListBedroomImageModel.clear();
        bitmapsHall.clear();
        bitmapsKitchen.clear();
        bitmapsWashroom.clear();
        bitmapsBedroom.clear();
    }

    public boolean validate(){
       boolean flag = true;
        if(arrayListHallImageModel.size() < 3){
            utils.toast(2, 2, "Please add more images of hall");
            flag = false;
        }else if(arrayListKitchenImageModel.size() < 3){
            utils.toast(2, 2, "Please add more images of kitchen");
            flag = false;
        }else if(arrayListWashroomImageModel.size() < 3){
            utils.toast(2, 2, "Please add more images of washroom");
            flag = false;
        }else if(arrayListBedroomImageModel.size() < 3){
            utils.toast(2, 2, "Please add more images of bedroom");
            flag = false;
        }
        return flag;
    }

    private void showStatusPopup(final Activity context, Point p) {

        // Inflate the popup_layout.xml
        //LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.llStatusChangePopup);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.header_task_detail_attach, null);

        // Creating the PopupWindow
        PopupWindow changeStatusPopUp = new PopupWindow(context);
        changeStatusPopUp.setContentView(layout);
        changeStatusPopUp.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        changeStatusPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setFocusable(true);

        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
        int OFFSET_X = -20;
        int OFFSET_Y = 155;

        //Clear the default translucent background
        changeStatusPopUp.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);
        ImageView imageCamera = (ImageView) layout.findViewById(R.id.imageView);
        ImageView imageGallery = (ImageView) layout.findViewById(R.id.imageView2);
        //final String type1 = type;

        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hasReadExternalStoragePermission = ContextCompat.checkSelfPermission(UpdatePhoto.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                int hasWriteExternalStoragePermission = ContextCompat.checkSelfPermission(UpdatePhoto.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasReadExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdatePhoto.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                    return;
                }
                if (hasWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdatePhoto.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 124);
                    return;
                }

                strName = String.valueOf(new Date().getDate() + "" + new Date().getTime());
                strImageName = strName + ".jpeg";

                utils.selectImage(strImageName, null, UpdatePhoto.this, false);
            }
        });

        imageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hasReadExternalStoragePermission = ContextCompat.checkSelfPermission(UpdatePhoto.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                int hasWriteExternalStoragePermission = ContextCompat.checkSelfPermission(UpdatePhoto.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasReadExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdatePhoto.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                    return;
                }
                if (hasWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdatePhoto.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 124);
                    return;
                }

                strName = String.valueOf(new Date().getDate() + "" + new Date().getTime());
                strImageName = strName + ".jpeg";

                utils.selectImage(strImageName, null, UpdatePhoto.this, false);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) { //&& data != null
            try {
                // loadingPanel.setVisibility(View.VISIBLE);
                if (typeOfImage.toString().equalsIgnoreCase("Hall")) {
                    switch (requestCode) {
                        case Config.START_CAMERA_REQUEST_CODE:

                            backgroundThreadHandlerHall = new BackgroundThreadHandlerHall();
                            strImageName = Utils.customerImageUri.getPath();
                            Thread backgroundThreadCamera = new BackgroundThreadCameraHall();
                            backgroundThreadCamera.start();
                            break;

                        case Config.START_GALLERY_REQUEST_CODE:
                            backgroundThreadHandlerHall = new BackgroundThreadHandlerHall();

                            imagePaths.clear();

                            String[] all_path = intent.getStringArrayExtra("all_path");

                            if (all_path.length + IMAGE_COUNT < 3) {

                                for (int i = 0; i < (IMAGE_COUNT); i++) {
                                    imagePaths.add(all_path[i]);
                                }

                            } else {

                                for (String string : all_path) {
                                    imagePaths.add(string);
                                }
                            }
                            Thread backgroundThread = new BackgroundThreadHall();
                            backgroundThread.start();

                            break;
                    }
                } else if (typeOfImage.toString().equalsIgnoreCase("Kitchen")) {
                    switch (requestCode) {
                        case Config.START_CAMERA_REQUEST_CODE:

                            backgroundThreadHandlerKitchen = new BackgroundThreadHandlerKitchen();
                            strImageName = Utils.customerImageUri.getPath();
                            Thread backgroundThreadCamera = new BackgroundThreadCameraKitchen();
                            backgroundThreadCamera.start();
                            break;

                        case Config.START_GALLERY_REQUEST_CODE:
                            backgroundThreadHandlerKitchen = new BackgroundThreadHandlerKitchen();

                            imagePaths.clear();

                            String[] all_path = intent.getStringArrayExtra("all_path");

                            if (all_path.length + IMAGE_COUNT < 3) {

                                for (int i = 0; i < (IMAGE_COUNT); i++) {
                                    imagePaths.add(all_path[i]);
                                }

                            } else {

                                for (String string : all_path) {
                                    imagePaths.add(string);
                                }
                            }
                            Thread backgroundThread = new BackgroundThreadKitchen();
                            backgroundThread.start();

                            break;
                    }
                } else if (typeOfImage.toString().equalsIgnoreCase("Washroom")) {
                    switch (requestCode) {
                        case Config.START_CAMERA_REQUEST_CODE:

                            backgroundThreadHandlerWashroom = new BackgroundThreadHandlerWashroom();
                            strImageName = Utils.customerImageUri.getPath();
                            Thread backgroundThreadCamera = new BackgroundThreadCameraWashroom();
                            backgroundThreadCamera.start();
                            break;

                        case Config.START_GALLERY_REQUEST_CODE:
                            backgroundThreadHandlerWashroom = new BackgroundThreadHandlerWashroom();

                            imagePaths.clear();

                            String[] all_path = intent.getStringArrayExtra("all_path");

                            if (all_path.length + IMAGE_COUNT < 3) {

                                for (int i = 0; i < (IMAGE_COUNT); i++) {
                                    imagePaths.add(all_path[i]);
                                }

                            } else {

                                for (String string : all_path) {
                                    imagePaths.add(string);
                                }
                            }
                            Thread backgroundThread = new BackgroundThreadWashroom();
                            backgroundThread.start();

                            break;
                    }
                } else if (typeOfImage.toString().equalsIgnoreCase("Bedroom")) {
                    switch (requestCode) {
                        case Config.START_CAMERA_REQUEST_CODE:

                            backgroundThreadHandlerBedroom = new BackgroundThreadHandlerBedroom();
                            strImageName = Utils.customerImageUri.getPath();
                            Thread backgroundThreadCamera = new BackgroundThreadCameraBedroom();
                            backgroundThreadCamera.start();
                            break;

                        case Config.START_GALLERY_REQUEST_CODE:
                            backgroundThreadHandlerBedroom = new BackgroundThreadHandlerBedroom();

                            imagePaths.clear();

                            String[] all_path = intent.getStringArrayExtra("all_path");

                            if (all_path.length + IMAGE_COUNT < 3) {

                                for (int i = 0; i < (IMAGE_COUNT); i++) {
                                    imagePaths.add(all_path[i]);
                                }

                            } else {

                                for (String string : all_path) {
                                    imagePaths.add(string);
                                }
                            }
                            Thread backgroundThread = new BackgroundThreadBedroom();
                            backgroundThread.start();

                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class BackgroundThreadHandlerBedroom extends Handler {
        @Override
        public void handleMessage(Message msg) {

            addImagesBedroom();

            // loadingPanel.setVisibility(View.GONE);
        }
    }

    private void addImagesBedroom() {

        linear_bedroom.removeAllViews();

        for (int i = 0; i < bitmapsBedroom.size(); i++) {
            try {
                //
                ImageView imageView = new ImageView(UpdatePhoto.this);
                imageView.setPadding(0, 0, 3, 0);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(10, 10, 10, 10);
                // linearLayout1.setLayoutParams(layoutParams);

                imageView.setLayoutParams(layoutParams);
                imageView.setImageBitmap(bitmapsBedroom.get(i));
                imageView.setTag(bitmapsBedroom.get(i));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);


                //Utils.log(" 2 " + String.valueOf(i), " IN ");

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Dialog dialog = new Dialog(UpdatePhoto.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        dialog.setContentView(R.layout.image_dialog_layout);

                        TouchImageView mOriginal = (TouchImageView) dialog.findViewById(R.id.imgOriginal);
                        try {
                            mOriginal.setImageBitmap((Bitmap) v.getTag());
                        } catch (OutOfMemoryError oOm) {
                            oOm.printStackTrace();
                        }
                        dialog.setCancelable(true);

                        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT); //Controlling width and height.
                        dialog.show();

                    }
                });

                linear_bedroom.addView(imageView);
            } catch (Exception | OutOfMemoryError e) {
                //bitmap.recycle();
                e.printStackTrace();
            }
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////

    private class BackgroundThreadHandlerWashroom extends Handler {
        @Override
        public void handleMessage(Message msg) {

            addImagesWashroom();

            // loadingPanel.setVisibility(View.GONE);
        }
    }

    private void addImagesWashroom() {

        linear_washroom.removeAllViews();

        for (int i = 0; i < bitmapsWashroom.size(); i++) {
            try {
                //
                ImageView imageView = new ImageView(UpdatePhoto.this);
                imageView.setPadding(0, 0, 3, 0);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(10, 10, 10, 10);
                // linearLayout1.setLayoutParams(layoutParams);

                imageView.setLayoutParams(layoutParams);
                imageView.setImageBitmap(bitmapsWashroom.get(i));
                imageView.setTag(bitmapsWashroom.get(i));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);


                //Utils.log(" 2 " + String.valueOf(i), " IN ");

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Dialog dialog = new Dialog(UpdatePhoto.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        dialog.setContentView(R.layout.image_dialog_layout);

                        TouchImageView mOriginal = (TouchImageView) dialog.findViewById(R.id.imgOriginal);
                        try {
                            mOriginal.setImageBitmap((Bitmap) v.getTag());
                        } catch (OutOfMemoryError oOm) {
                            oOm.printStackTrace();
                        }
                        dialog.setCancelable(true);

                        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT); //Controlling width and height.
                        dialog.show();

                    }
                });

                linear_washroom.addView(imageView);
            } catch (Exception | OutOfMemoryError e) {
                //bitmap.recycle();
                e.printStackTrace();
            }
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////

    private class BackgroundThreadHandlerKitchen extends Handler {
        @Override
        public void handleMessage(Message msg) {

            addImagesKitchen();

            // loadingPanel.setVisibility(View.GONE);
        }
    }

    private void addImagesKitchen() {

        linear_kitchen.removeAllViews();

        for (int i = 0; i < bitmapsKitchen.size(); i++) {
            try {
                //
                ImageView imageView = new ImageView(UpdatePhoto.this);
                imageView.setPadding(0, 0, 3, 0);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(10, 10, 10, 10);
                // linearLayout1.setLayoutParams(layoutParams);

                imageView.setLayoutParams(layoutParams);
                imageView.setImageBitmap(bitmapsKitchen.get(i));
                imageView.setTag(bitmapsKitchen.get(i));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);


                //Utils.log(" 2 " + String.valueOf(i), " IN ");

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Dialog dialog = new Dialog(UpdatePhoto.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        dialog.setContentView(R.layout.image_dialog_layout);

                        TouchImageView mOriginal = (TouchImageView) dialog.findViewById(R.id.imgOriginal);
                        try {
                            mOriginal.setImageBitmap((Bitmap) v.getTag());
                        } catch (OutOfMemoryError oOm) {
                            oOm.printStackTrace();
                        }
                        dialog.setCancelable(true);

                        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT); //Controlling width and height.
                        dialog.show();

                    }
                });

                linear_kitchen.addView(imageView);
            } catch (Exception | OutOfMemoryError e) {
                //bitmap.recycle();
                e.printStackTrace();
            }
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////

    private class BackgroundThreadHandlerHall extends Handler {
        @Override
        public void handleMessage(Message msg) {

            addImagesHall();

            // loadingPanel.setVisibility(View.GONE);
        }
    }

    private void addImagesHall() {

        linear_hall.removeAllViews();

        for (int i = 0; i < bitmapsHall.size(); i++) {
            try {
                //
                ImageView imageView = new ImageView(UpdatePhoto.this);
                imageView.setPadding(0, 0, 3, 0);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(10, 10, 10, 10);
                // linearLayout1.setLayoutParams(layoutParams);

                imageView.setLayoutParams(layoutParams);
                imageView.setImageBitmap(bitmapsHall.get(i));
                imageView.setTag(bitmapsHall.get(i));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);


                //Utils.log(" 2 " + String.valueOf(i), " IN ");

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Dialog dialog = new Dialog(UpdatePhoto.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        dialog.setContentView(R.layout.image_dialog_layout);

                        TouchImageView mOriginal = (TouchImageView) dialog.findViewById(R.id.imgOriginal);
                        try {
                            mOriginal.setImageBitmap((Bitmap) v.getTag());
                        } catch (OutOfMemoryError oOm) {
                            oOm.printStackTrace();
                        }
                        dialog.setCancelable(true);

                        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT); //Controlling width and height.
                        dialog.show();

                    }
                });

                linear_hall.addView(imageView);
            } catch (Exception | OutOfMemoryError e) {
                //bitmap.recycle();
                e.printStackTrace();
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    private class BackgroundThreadHall extends Thread {
        @Override
        public void run() {

            try {
                for (int i = 0; i < imagePaths.size(); i++) {
                    Calendar calendar = new GregorianCalendar();
                    String strTime = String.valueOf(calendar.getTimeInMillis());
                    String strFileName = strTime + ".jpeg";
                    File galleryFile = utils.createFileInternalImage(strFileName);
                    strImageName = galleryFile.getAbsolutePath();
                    Date date = new Date();

                    ImageModel imageModel = new ImageModel(strFileName, "", strTime, utils.convertDateToString(date), galleryFile.getAbsolutePath());
                    arrayListHallImageModel.add(imageModel);

                    utils.copyFile(new File(imagePaths.get(i)), galleryFile);
                    //
                    utils.compressImageFromPath(strImageName, Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                    bitmapsHall.add(utils.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight));

                    IMAGE_COUNT++;
                }
                backgroundThreadHandlerHall.sendEmptyMessage(0);
            } catch (IOException | OutOfMemoryError e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class BackgroundThreadCameraHall extends Thread {
        @Override
        public void run() {
            try {
                if (strImageName != null && !strImageName.equalsIgnoreCase("")) {

                    utils.compressImageFromPath(strImageName, Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);
                    Date date = new Date();
                    ImageModel imageModel = new ImageModel(strName, "", strName, utils.convertDateToString(date), strImageName);
                    arrayListHallImageModel.add(imageModel);
                    bitmapsHall.add(utils.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight));
                }

                IMAGE_COUNT++;

            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
            backgroundThreadHandlerHall.sendEmptyMessage(0);
        }
    }

    //----------------------------------------------------------------------------------------

    private class BackgroundThreadKitchen extends Thread {
        @Override
        public void run() {

            try {
                for (int i = 0; i < imagePaths.size(); i++) {
                    Calendar calendar = new GregorianCalendar();
                    String strTime = String.valueOf(calendar.getTimeInMillis());
                    String strFileName = strTime + ".jpeg";
                    File galleryFile = utils.createFileInternalImage(strFileName);
                    strImageName = galleryFile.getAbsolutePath();
                    Date date = new Date();

                    ImageModel imageModel = new ImageModel(strFileName, "", strTime, utils.convertDateToString(date), galleryFile.getAbsolutePath());
                    arrayListKitchenImageModel.add(imageModel);

                    utils.copyFile(new File(imagePaths.get(i)), galleryFile);
                    //
                    utils.compressImageFromPath(strImageName, Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                    bitmapsKitchen.add(utils.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight));

                    IMAGE_COUNT++;
                }
                backgroundThreadHandlerKitchen.sendEmptyMessage(0);
            } catch (IOException | OutOfMemoryError e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class BackgroundThreadCameraKitchen extends Thread {
        @Override
        public void run() {
            try {
                if (strImageName != null && !strImageName.equalsIgnoreCase("")) {

                    utils.compressImageFromPath(strImageName, Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);
                    Date date = new Date();
                    ImageModel imageModel = new ImageModel(strName, "", strName, utils.convertDateToString(date), strImageName);
                    arrayListKitchenImageModel.add(imageModel);
                    bitmapsKitchen.add(utils.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight));
                }

                IMAGE_COUNT++;

            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
            backgroundThreadHandlerKitchen.sendEmptyMessage(0);
        }
    }

    //----------------------------------------------------------------------------------------

    private class BackgroundThreadWashroom extends Thread {
        @Override
        public void run() {

            try {
                for (int i = 0; i < imagePaths.size(); i++) {
                    Calendar calendar = new GregorianCalendar();
                    String strTime = String.valueOf(calendar.getTimeInMillis());
                    String strFileName = strTime + ".jpeg";
                    File galleryFile = utils.createFileInternalImage(strFileName);
                    strImageName = galleryFile.getAbsolutePath();
                    Date date = new Date();

                    ImageModel imageModel = new ImageModel(strFileName, "", strTime, utils.convertDateToString(date), galleryFile.getAbsolutePath());
                    arrayListWashroomImageModel.add(imageModel);

                    utils.copyFile(new File(imagePaths.get(i)), galleryFile);
                    //
                    utils.compressImageFromPath(strImageName, Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                    bitmapsWashroom.add(utils.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight));

                    IMAGE_COUNT++;
                }
                backgroundThreadHandlerWashroom.sendEmptyMessage(0);
            } catch (IOException | OutOfMemoryError e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class BackgroundThreadCameraWashroom extends Thread {
        @Override
        public void run() {
            try {
                if (strImageName != null && !strImageName.equalsIgnoreCase("")) {

                    utils.compressImageFromPath(strImageName, Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);
                    Date date = new Date();
                    ImageModel imageModel = new ImageModel(strName, "", strName, utils.convertDateToString(date), strImageName);
                    arrayListWashroomImageModel.add(imageModel);
                    bitmapsWashroom.add(utils.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight));
                }

                IMAGE_COUNT++;

            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
            backgroundThreadHandlerWashroom.sendEmptyMessage(0);
        }
    }

    //-----------------------------------------------------------------------------------------

    private class BackgroundThreadBedroom extends Thread {
        @Override
        public void run() {

            try {
                for (int i = 0; i < imagePaths.size(); i++) {
                    Calendar calendar = new GregorianCalendar();
                    String strTime = String.valueOf(calendar.getTimeInMillis());
                    String strFileName = strTime + ".jpeg";
                    File galleryFile = utils.createFileInternalImage(strFileName);
                    strImageName = galleryFile.getAbsolutePath();
                    Date date = new Date();

                    ImageModel imageModel = new ImageModel(strFileName, "", strTime, utils.convertDateToString(date), galleryFile.getAbsolutePath());
                    arrayListBedroomImageModel.add(imageModel);

                    utils.copyFile(new File(imagePaths.get(i)), galleryFile);
                    //
                    utils.compressImageFromPath(strImageName, Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                    bitmapsBedroom.add(utils.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight));

                    IMAGE_COUNT++;
                }
                backgroundThreadHandlerBedroom.sendEmptyMessage(0);
            } catch (IOException | OutOfMemoryError e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class BackgroundThreadCameraBedroom extends Thread {
        @Override
        public void run() {
            try {
                if (strImageName != null && !strImageName.equalsIgnoreCase("")) {

                    utils.compressImageFromPath(strImageName, Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);
                    Date date = new Date();
                    ImageModel imageModel = new ImageModel(strName, "", strName, utils.convertDateToString(date), strImageName);
                    arrayListBedroomImageModel.add(imageModel);
                    bitmapsBedroom.add(utils.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight));
                }

                IMAGE_COUNT++;

            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
            backgroundThreadHandlerBedroom.sendEmptyMessage(0);
        }
    }

    //------------------------------------------------------------------------------------------


}
