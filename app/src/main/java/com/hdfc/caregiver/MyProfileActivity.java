package com.hdfc.caregiver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UploadService;
import com.hdfc.config.Config;
import com.hdfc.libs.AppUtils;
import com.hdfc.libs.Utils;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Admin on 28-01-2016.
 */
public class MyProfileActivity extends AppCompatActivity {

    //private static int intWhichScreen;
    private static String strCustomerImgName = "";
    //public static Bitmap bitmap = null;
    private static Uri uri;
    private static Handler backgroundThreadHandler;
    //private AppUtils appUtils;
    private static ProgressDialog mProgress = null;
    private static boolean isImageChanged=false;
    private ImageView profileImage;
    private Utils utils;
    //ImageView backbutton, edit, imageplace;
    private EditText phone, place, name, email;
    //TextView textViewName;
    private Button buttonContinue;
    private int Flag = 0;
    private ProgressDialog progressDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);
        // textViewName = (TextView) findViewById(R.id.textCaregiverName);

        name = (EditText) findViewById(R.id.input_name);
        phone = (EditText) findViewById(R.id.input_mobile);
        place = (EditText) findViewById(R.id.input_place);
        profileImage = (ImageView) findViewById(R.id.person_icon);

        email = (EditText) findViewById(R.id.input_email);
        Button signOut = (Button) findViewById(R.id.buttonLogOut);
        buttonContinue = (Button) findViewById(R.id.buttonContinue);
        //ImageView pen = (ImageView) findViewById(R.id.imgPen);
        Button buttonBack = (Button) findViewById(R.id.buttonBack);

        if (buttonBack != null) {
            buttonBack.setVisibility(View.VISIBLE);
            buttonBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBack();
                }
            });
        }

        //Bundle b = getIntent().getExtras();
        //intWhichScreen = b.getInt("WHICH_SCREEN", Config.intRatingsScreen);

        utils = new Utils(MyProfileActivity.this);
        progressDialog = new ProgressDialog(MyProfileActivity.this);
        mProgress = new ProgressDialog(MyProfileActivity.this);
        //appUtils = new AppUtils(MyProfileActivity.this);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.selectImage(String.valueOf(new Date().getDate() + "" + new Date().getTime())
                        + ".jpeg", null, MyProfileActivity.this, true);
            }
        });
       /* imageplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSTracker gpsTracker = new GPSTracker(MyProfileActivity.this);

                if (gpsTracker.getIsGPSTrackingEnabled()) {
                    String city = gpsTracker.getLocality(MyProfileActivity.this);
                    place.setText(city);
                }else {
                    gpsTracker.showSettingsAlert();
                }
            }
        });
*/

        if (signOut != null) {
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                    builder.setTitle(getString(R.string.confirm_logout));
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AppUtils.logout(MyProfileActivity.this);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });
        }


        name.setEnabled(false);
        name.setFocusableInTouchMode(false);
        name.clearFocus();

        phone.setEnabled(false);
        phone.setFocusableInTouchMode(false);
        phone.clearFocus();

        place.setEnabled(false);
        place.setFocusableInTouchMode(false);
        place.clearFocus();

        email.setEnabled(false);
        email.setFocusableInTouchMode(false);
        email.clearFocus();

        //  edit = (ImageView)findViewById(R.id.imgPen);

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name.setError(null);
                phone.setError(null);

                boolean cancel = false;
                View focusView = null;

                if (Flag == 0) {
                    buttonContinue.setText(R.string.save_settings);
                    // edit.setImageResource(R.mipmap.done);

                    name.setEnabled(true);
                    name.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    name.setFocusableInTouchMode(true);
                    name.requestFocus();

                    phone.setEnabled(true);
                    phone.setInputType(InputType.TYPE_CLASS_PHONE);
                    phone.setFocusableInTouchMode(true);

                    place.setEnabled(true);
                    place.setInputType(InputType.TYPE_CLASS_TEXT);
                    place.setFocusableInTouchMode(true);
                    place.requestFocus();
                    Flag = 1;

                } else if (Flag == 1) {

                    buttonContinue.setText(R.string.edit_settings);
                    name.setError(null);
                    phone.setError(null);
                    place.setError(null);

                    String strName = name.getText().toString().trim();
                    String strPhone= phone.getText().toString().trim();
                    String strPlace= place.getText().toString().trim();
                    //Toast.makeText(MyProfileActivity.this,"Data updated Successfully",Toast.LENGTH_LONG).show();

                    if (TextUtils.isEmpty(strName)) {
                        name.setError(getString(R.string.error_field_required));
                        focusView = name;
                        cancel = true;
                    }

                    if (TextUtils.isEmpty(strPhone)) {
                       phone.setError(getString(R.string.error_field_required));
                       focusView = phone;
                       cancel = true;
                    } else if (!utils.validCellPhone(strPhone)) {
                        phone.setError(getString(R.string.error_invalid_contact_no));
                        focusView = phone;
                        cancel = true;
                    }

                    if (TextUtils.isEmpty(strPlace)) {
                        place.setError(getString(R.string.error_field_required));
                        focusView = place;
                        cancel = true;
                    }

                    if(cancel){
                        focusView.requestFocus();
                    }else {

                        progressDialog.setMessage(getString(R.string.loading));
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        StorageService storageService = new StorageService(MyProfileActivity.this);

                        JSONObject jsonToUpdate = new JSONObject();

                        try {
                            jsonToUpdate.put("provider_name", strName);
                            jsonToUpdate.put("provider_email",email.getText());
                            jsonToUpdate.put("provider_contact_no", strPhone);
                            jsonToUpdate.put("provider_address", strPlace);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        storageService.updateDocs(jsonToUpdate,
                                Config.providerModel.getStrProviderId(),
                                Config.collectionProvider, new App42CallBack() {
                            @Override
                            public void onSuccess(Object o) {
                                progressDialog.dismiss();
                                utils.toast(1, 1, "Account Updated");

                                Config.providerModel.setStrContacts(phone.getText().toString());
                                Config.providerModel.setStrAddress(place.getText().toString());
                                Config.providerModel.setStrName(name.getText().toString());

                                // edit.setImageResource(R.mipmap.edit);
                                Flag = 0;

                                name.setEnabled(false);
                                name.setFocusableInTouchMode(false);
                                name.clearFocus();

                                phone.setEnabled(false);
                                phone.setFocusableInTouchMode(false);
                                phone.clearFocus();

                                place.setEnabled(false);
                                place.setFocusableInTouchMode(false);
                                place.clearFocus();
                            }

                            @Override
                            public void onException(Exception e) {
                                progressDialog.dismiss();
                                utils.toast(2, 2, "Error. Try Again!!!");
                            }
                        });
                    }
                }
            }
        });
       /* backbutton = (ImageView)findViewById(R.id.imgBackArrow);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
*/
        if(Config.providerModel!=null) {
            email.setText(Config.providerModel.getStrEmail());
            phone.setText(Config.providerModel.getStrContacts());
            place.setText(Config.providerModel.getStrAddress());
            //textViewName.setText(Config.providerModel.getStrName());
            name.setText(Config.providerModel.getStrName());
        }

        Glide.with(MyProfileActivity.this)
                .load(Config.strProviderUrl)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(MyProfileActivity.this))
                .placeholder(R.drawable.person_icon)
                .crossFade()
                .into(profileImage);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) { //&& data != null
            try {
                backgroundThreadHandler = new BackgroundThreadHandler();
                //Utils.toast(1, 1, "Getting Image...");
                mProgress.setMessage(getString(R.string.loading));
                mProgress.show();
                switch (requestCode) {
                    case Config.START_CAMERA_REQUEST_CODE:
                        strCustomerImgName = Utils.customerImageUri.getPath();
                        Thread backgroundThreadCamera = new BackgroundThreadCamera();
                        backgroundThreadCamera.start();
                        break;

                    case Config.START_GALLERY_REQUEST_CODE:
                        if (intent.getData() != null) {
                            uri = intent.getData();
                            Thread backgroundThread = new BackgroundThreadGallery();
                            backgroundThread.start();
                        }
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
       /* BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundThreadHandler = new BackgroundThreadHandler();

        mProgress.setMessage(getResources().getString(R.string.loading));
        mProgress.show();*/
    }

    private void goBack() {
        Intent intent = new Intent(MyProfileActivity.this, DashboardActivity.class);
        Config.intSelectedMenu = Config.intRatingsScreen;
        startActivity(intent);
        finish();
    }

    private void checkImage() {

        try {

            if (utils.isConnectingToInternet()) {

                progressDialog.setMessage(getResources().getString(R.string.uploading_image));
                progressDialog.setCancelable(false);
                progressDialog.show();

                UploadService uploadService = new UploadService(this);

                if (progressDialog.isShowing())
                    progressDialog.setProgress(1);

                uploadService.removeImage(Config.strCustomerImageName, Config.providerModel.
                                getStrEmail(),
                        new App42CallBack() {
                            public void onSuccess(Object response) {

                                if(response!=null){
                                    Utils.log(response.toString(), " Response uploadService 0 ");
                                    uploadImage();
                                }else{
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }
                            @Override
                            public void onException(Exception e) {

                                if(e!=null) {
                                    Utils.log(e.getMessage(), " Response failure uploadService 0");
                                    App42Exception exception = (App42Exception) e;
                                    int appErrorCode = exception.getAppErrorCode();

                                    if (appErrorCode != 1401 ) {
                                        uploadImage();
                                    } else {
                                        utils.toast(2, 2, getString(R.string.error));
                                    }

                                }else{
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }
                        });

            } else {
                utils.toast(2, 2, getString(R.string.warning_internet));
            }
        }catch (Exception e){
            e.printStackTrace();
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.error));
        }
    }

    private void uploadImage() {

        try {

            if (utils.isConnectingToInternet()) {

                UploadService uploadService = new UploadService(this);

                uploadService.uploadImageCommon(strCustomerImgName, "provider_image",
                        "Profile Picture", Config.providerModel.getStrEmail(),
                        UploadFileType.IMAGE, new App42CallBack() {

                            public void onSuccess(Object response) {

                                if(response!=null) {
                                    /// Utils.log(response.toString(), "response 1 ");
                                    Upload upload = (Upload) response;
                                    ArrayList<Upload.File> fileList = upload.getFileList();

                                    if (fileList.size() > 0) {//&& bitmap != null

                                        Upload.File file = fileList.get(0);

                                        final String url = file.getUrl();

                                        JSONObject jsonToUpdate = new JSONObject();

                                        StorageService storageService = new StorageService(
                                                MyProfileActivity.this);

                                        try {

                                            Config.providerModel.setStrImgUrl(url);
                                            Config.strProviderUrl = url;

                                            jsonToUpdate.put("provider_profile_url", url);

                                            Glide.with(MyProfileActivity.this)
                                                    .load(strCustomerImgName)
                                                    .centerCrop()
                                                    .bitmapTransform(new CropCircleTransformation(MyProfileActivity.this))
                                                    .placeholder(R.drawable.person_icon)
                                                    .crossFade()
                                                    .into(profileImage);

                                            //
                                            storageService.updateDocs(jsonToUpdate,
                                                    Config.providerModel.getStrProviderId(),
                                                    Config.collectionProvider, new App42CallBack() {
                                                @Override
                                                public void onSuccess(Object o) {

                                                    if (o != null) {

                                                       /* File f = utils.getInternalFileImages(Config.providerModel.getStrProviderId());

                                                        if (f.exists())
                                                            f.delete();

                                                        File newFile = new File(strCustomerImgName);
                                                        File renameFile = utils.getInternalFileImages(Config.providerModel.getStrProviderId());

                                                        try {
                                                            utils.moveFile(newFile, renameFile);
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }*/

                                                        // profileImage.setImageBitmap(bitmap);

                                                        if (progressDialog.isShowing())
                                                            progressDialog.dismiss();

                                                        /*if (Config.jsonObject.has("provider_profile_url")) {
                                                            try {
                                                                Config.jsonObject.put("provider_profile_url", url);
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }*/

                                                        utils.toast(2, 2,
                                                                getString(R.string.
                                                                        update_profile_image));
                                                        isImageChanged = false;

                                                    } else {
                                                        utils.toast(2, 2,
                                                                getString(R.string.
                                                                        warning_internet));
                                                    }
                                                }

                                                @Override
                                                public void onException(Exception e) {
                                                    if (progressDialog.isShowing())
                                                        progressDialog.dismiss();

                                                    if (e != null) {
                                                        Utils.log(e.toString(), "response onException 1 ");
                                                        utils.toast(2, 2, e.getMessage());
                                                    } else {
                                                        utils.toast(2, 2,
                                                                getString(R.string.warning_internet));
                                                    }
                                                }
                                            });
                                            //
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    } else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        utils.toast(2, 2, getString(R.string.error));
                                    }
                                }else{
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }

                            @Override
                            public void onException(Exception e) {

                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();

                                if(e!=null) {
                                    Utils.log(e.toString(), "response onException 1 ");
                                    utils.toast(2, 2, e.getMessage());
                                }else{
                                    utils.toast(2, 2, getString(R.string.warning_internet));
                                }
                            }
                        });

            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                utils.toast(2, 2, getString(R.string.warning_internet));
            }
        }catch (Exception e){
            e.printStackTrace();
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.error));
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();]
        goBack();
    }

    private class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            if (mProgress.isShowing())
                mProgress.dismiss();

            /*if (profileImage != null && strCustomerImgName != null
                    && !strCustomerImgName.equalsIgnoreCase("") && bitmap != null)
                profileImage.setImageBitmap(bitmap);*/

            /*if (!isImageChanged) {
                if (bitmap != null)
                    if (profileImage != null) {
                        profileImage.setImageBitmap(bitmap);
                    }
                else
                    utils.toast(2, 2, getString(R.string.error));
            }*/

            if (isImageChanged) { //&& bitmap != nul
                try {
                    checkImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

   /* private class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

                File f = utils.getInternalFileImages(Config.providerModel.getStrProviderId());
                Utils.log(f.getAbsolutePath(), " FP ");
                bitmap = utils.getBitmapFromFile(f.getAbsolutePath(), Config.intWidth, Config.intHeight);

                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    //
    private class BackgroundThreadGallery extends Thread {
        @Override
        public void run() {

            try {
                if (uri != null) {
                    Calendar calendar = new GregorianCalendar();
                    String strFileName = String.valueOf(calendar.getTimeInMillis()) + ".jpeg";
                    File galleryFile = utils.createFileInternalImage(strFileName);
                    strCustomerImgName = galleryFile.getAbsolutePath();
                    InputStream is = getContentResolver().openInputStream(uri);
                    utils.copyInputStreamToFile(is, galleryFile);
                    utils.compressImageFromPath(strCustomerImgName, Config.intCompressWidth,
                            Config.intCompressHeight, Config.iQuality);
                   /* bitmap = utils.getBitmapFromFile(strCustomerImgName, Config.intWidth,
                            Config.intHeight);*/
                    isImageChanged = true;
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class BackgroundThreadCamera extends Thread {
        @Override
        public void run() {
            try {
                if (strCustomerImgName != null && !strCustomerImgName.equalsIgnoreCase("")) {
                    utils.compressImageFromPath(strCustomerImgName, Config.intCompressWidth,
                            Config.intCompressHeight, Config.iQuality);
                    /*bitmap = utils.getBitmapFromFile(strCustomerImgName, Config.intWidth,
                            Config.intHeight);*/
                    isImageChanged = true;
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}