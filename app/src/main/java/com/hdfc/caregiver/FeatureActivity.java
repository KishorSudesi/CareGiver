package com.hdfc.caregiver;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hdfc.adapters.FeatureAdapter;
import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UploadService;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Libs;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.ImageModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class FeatureActivity extends AppCompatActivity implements Serializable{

    public static Uri uri;
    public static List<String> listFeatures;
    public static String strImageName = "";
    static ImageView imageView;
    static Bitmap bitmap = null;
    private static StorageService storageService;
    private static ArrayList<ImageModel> arrayListImageModel = new ArrayList<>();
    private static JSONArray jsonArrayFeaturesDone;
    private static FeatureAdapter featureAdapter;
    private static Handler backgroundThreadHandler;
    private static ProgressDialog mProgress = null;
    private static String strDoneDate;
    private static ActivityModel act;
    public JSONObject json;
    private JSONObject responseJSONDoc;
    private JSONObject responseJSONDocCarla;
    private Libs libs;
    private ProgressDialog progressDialog;
    private Point p;
    private JSONArray jsonArrayImagesAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_features);
        listFeatures = new ArrayList<>();
        ImageView attach = (ImageView) findViewById(R.id.imgAttachHeaderTaskDetail);
        ImageView imgLogoHeaderTaskDetail = (ImageView) findViewById(R.id.imgLogoHeaderTaskDetail);
        Button done = (Button) findViewById(R.id.buttonVegetibleDone);
        ImageView back = (ImageView) findViewById(R.id.imgBackHeaderTaskDetail);
        ListView featuresList = (ListView) findViewById(R.id.list_view);
        //surfaceView = (SurfaceView)findViewById(R.id.camerapreview);
        TextView textViewEmpty = (TextView) findViewById(android.R.id.empty);
       // featureAdapter = new FeatureAdapter(this, listFeatures);

        MultiBitmapLoader multiBitmapLoader = new MultiBitmapLoader(FeatureActivity.this);

        TextView dependentName= (TextView)findViewById(R.id.textViewHeaderTaskDetail);

        jsonArrayImagesAdded = new JSONArray();

        try {

            Bundle b = getIntent().getExtras();
            /*intWhichScreen = b.getInt("WHICH_SCREEN", Config.intSimpleActivityScreen);*/

            act = (ActivityModel) b.getSerializable("ACTIVITY");

            List<String> lstFeatures = new ArrayList<>(Arrays.asList(act.getFeatures()));

            dependentName.setText(act.getStrActivityDependentName());

            featureAdapter = new FeatureAdapter(this, lstFeatures);

            //Libs.log(act.getStrActivityDate(), " Date ");
            //Libs.log(act.getFeatures().toString(),"Features");


            //
            File fileImage = libs.createFileInternal("images/" + libs.replaceSpace(act.getStrActivityDependentName()));

            if(fileImage.exists()) {
                String filename = fileImage.getAbsolutePath();
                multiBitmapLoader.loadBitmap(filename, imgLogoHeaderTaskDetail);
            }else{
                imgLogoHeaderTaskDetail.setImageDrawable(getResources().getDrawable(R.drawable.mrs_hungal_circle2));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        libs = new Libs(FeatureActivity.this);

        //String strCustomerImagePath = getFilesDir() + "/images/" + "feature_image";

        mProgress = new ProgressDialog(FeatureActivity.this);
        progressDialog = new ProgressDialog(FeatureActivity.this);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(arrayListImageModel.size()>0)
                    if(FeatureAdapter.selectedStrings.size()>0)
                        uploadImage();
                    else
                        libs.toast(2,2, "Select a feature");
                else
                    libs.toast(2,2, "Select a Image");
                //uploadCheckBox();
                 //backgroundThreadHandler = new BackgroundThreadHandler();
            }
        });


      /*  try {
            if (Config.jsonObject.has("activities")) {
                JSONArray jsonArrayServices = Config.jsonObject.getJSONArray("activities");
                for (int i=0;i<jsonArrayServices.length();i++){
                    json = jsonArrayServices.getJSONObject(i);
                }
                     jsonArray = json.getJSONArray("features");

                for(int j = 0 ; j < jsonArray.length(); j++) {

                    veg = jsonArray.getString(j);
                    listFeatures.add(veg);

                }

                for(int k = 0 ; k < json.length() ; k++){
                    System.out.println("VALUES OF DEPENDENT_NAME ARE : "+json.getString("dependent_name"));
                    FeatureModel featureModel = new FeatureModel();
                    featureModel.setDependentName(json.getString("dependent_name"));
                    viewHolder.dependentName.setText(featureModel.getDependentName());
                    featureModelList.add(featureModel);
                }
                System.out.println(jsonArray);
                featureAdapter.notifyDataSetChanged();
            }
        } catch(JSONException e){
            e.printStackTrace();
        }*/


        featuresList.setAdapter(featureAdapter);
        featuresList.setEmptyView(textViewEmpty);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeatureActivity.this,DashboardActivity.class);
                //intent.putExtra("WHICH_SCREEN", intWhichScreen);
                Config.intSelectedMenu=Config.intDashboardScreen;
                startActivity(intent);
            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open popup window
                if (p != null)
                    showStatusPopup(FeatureActivity.this, p);
            }
        });
    }

        // The method that displays the popup.
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
        int OFFSET_Y = 95;

        //Clear the default translucent background
        changeStatusPopUp.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);
     //   imageAttach = (ImageView)layout.findViewById(R.id.imageView2);
        ImageView imageCamera = (ImageView) layout.findViewById(R.id.imageView);
       /* imageAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               *//* Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                startActivityForResult(i, RESULT_GALLERY_IMAGE);*//*
                Intent intent = new Intent();
                intent.setType("image*//*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),2);

            }
        });*/
        strImageName = String.valueOf(new Date().getDate() + "" + new Date().getTime()) + ".jpeg";
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hasReadExternalStoragePermission = ContextCompat.checkSelfPermission(FeatureActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                int hasWriteExternalStoragePermission = ContextCompat.checkSelfPermission(FeatureActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasReadExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(FeatureActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                    return;
                }
                if (hasWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(FeatureActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 124);
                    return;
                }
                libs.selectImage(strImageName, null, FeatureActivity.this);
              //  Intent intent = new Intent();
               // intent.setType("image/*");
              //  intent.setAction(Intent.ACTION_CAMERA_BUTTON);
              //  startActivityForResult(Intent.createChooser(intent, "Select Picture"),0);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];
        ImageView attach = (ImageView) findViewById(R.id.imgAttachHeaderTaskDetail);

        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        attach.getLocationOnScreen(location);


        //Initialize the Point with x, and y positions
        p = new Point();
        p.x = location[0];
        p.y = location[1];


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) { //&& data != null
            try {
                //Libs.toast(1, 1, "Getting Image...");

                mProgress.setMessage(getString(R.string.loading));
                mProgress.show();
                switch (requestCode) {
                    case Config.START_CAMERA_REQUEST_CODE:

                        backgroundThreadHandler = new BackgroundThreadHandler();
                        mProgress.setMessage(getString(R.string.loading));
                        mProgress.show();
                        strImageName = Libs.customerImageUri.getPath();
                        Thread backgroundThreadCamera = new BackgroundThreadCamera();
                        backgroundThreadCamera.start();
                        break;

                    case Config.START_GALLERY_REQUEST_CODE:
                        backgroundThreadHandler = new BackgroundThreadHandler();
                        mProgress.setMessage(getString(R.string.loading));
                        mProgress.show();
                       // strCustomerImgName = Libs.customerImageUri.getPath();
                       // if (intent.getData() != null) {
                            uri = intent.getData();
                        Thread backgroundThread = new BackgroundThread();
                            backgroundThread.start();
                      //  }
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadImage(){

        try {

            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            if (libs.isConnectingToInternet()) {

                for(final ImageModel imageModel: arrayListImageModel) {

                    UploadService uploadService = new UploadService(this);

                    uploadService.uploadImageCommon(imageModel.getStrImageName(), imageModel.getStrImageDesc(), imageModel.getStrImageDesc(), Config.myProfileModel.getEmail(), UploadFileType.IMAGE, new App42CallBack() {

                        public void onSuccess(Object response) {

                            if (response != null) {

                                Upload upload = (Upload) response;
                                ArrayList<Upload.File> fileList = upload.getFileList();

                                if (fileList.size() > 0) {

                                    Upload.File file = fileList.get(0);

                                    JSONObject jsonObjectImages = new JSONObject();

                                    try {
                                        jsonObjectImages.put("image_name", imageModel.getStrImageDesc());
                                        jsonObjectImages.put("image_url", file.getUrl());
                                        jsonObjectImages.put("image_description", imageModel.getStrImageDesc());
                                        jsonObjectImages.put("image_taken", imageModel.getStrImageUrl());

                                        jsonArrayImagesAdded.put(jsonObjectImages);

                                        arrayListImageModel.remove(imageModel);

                                        if(arrayListImageModel.size()<=0)
                                            uploadCheckBox();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    uploadImage();
                                    libs.toast(2, 2, ((Upload) response).getStrResponse());
                                }
                            } else {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                libs.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onException(Exception e) {

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            if (e != null) {
                                Libs.log(e.toString(), "response");
                                libs.toast(2, 2, e.getMessage());
                                uploadImage();
                            } else {
                                libs.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }
                    });
                }

            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                libs.toast(2, 2, getString(R.string.warning_internet));
            }

        }catch (Exception e){
            e.printStackTrace();
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            uploadImage();
            libs.toast(2, 2, getString(R.string.error));
        }
    }

    public void uploadCheckBox() {

        if (libs.isConnectingToInternet()) {
            storageService = new StorageService(FeatureActivity.this);


            try {
                jsonArrayFeaturesDone = new JSONArray();

                for(String strings: FeatureAdapter.selectedStrings){
                    jsonArrayFeaturesDone.put(strings);
                }

                Date doneDate = new Date();

                strDoneDate=libs.convertDateToString(doneDate);

            } catch (Exception e) {
                e.printStackTrace();
            }

            storageService.findDocsByIdApp42CallBack(Config.jsonDocId, Config.collectionName, new App42CallBack() {
                @Override
                public void onSuccess(Object o) {

                    if (o != null) {

                        final Storage findObj = (Storage) o;

                        try {
                            responseJSONDoc = new JSONObject(findObj.getJsonDocList().get(0).getJsonDoc());
                            if (responseJSONDoc.has("activities")) {
                                JSONArray dependantsA = responseJSONDoc.
                                        getJSONArray("activities");

                                for(int i=0; i< dependantsA.length(); i++){

                                    JSONObject jsonObjectActivity = dependantsA.getJSONObject(i);

                                    if(jsonObjectActivity.getString("activity_date").equalsIgnoreCase(act.getStrActivityDate())) {

                                        jsonObjectActivity.put("activity_done_date", strDoneDate);
                                        jsonObjectActivity.put("status", "completed");

                                        JSONArray jsonArrayFeatures = jsonObjectActivity.getJSONArray("features_done");

                                        jsonArrayFeatures.put(jsonArrayFeaturesDone);

                                        JSONArray jsonArrayImages = jsonObjectActivity.getJSONArray("images");

                                        jsonArrayImages.put(jsonArrayImagesAdded);
                                    }

                                }

                                //dependantsA.put(jsonObjectActCarla);

                            }
                        } catch (JSONException jSe) {
                            jSe.printStackTrace();
                            progressDialog.dismiss();
                        }

                        Libs.log(responseJSONDoc.toString(), " onj 1 ");

                        if (libs.isConnectingToInternet()) {//TODO check activity added

                            storageService.updateDocs(responseJSONDoc, Config.jsonDocId, Config.collectionName, new App42CallBack() {
                                @Override
                                public void onSuccess(Object o) {

                                    Config.jsonObject = responseJSONDoc;

                                    if (o != null) {

                                        storageService.findDocsByKeyValue(Config.collectionName2, "customer_email", act.getStrCustomerEmail() , new AsyncApp42ServiceApi.App42StorageServiceListener() {
                                            @Override
                                            public void onDocumentInserted(Storage response) {
                                            }

                                            @Override
                                            public void onUpdateDocSuccess(Storage response) {
                                            }

                                            @Override
                                            public void onFindDocSuccess(Storage response) {

                                                if (response != null) {

                                                    if (response.getJsonDocList().size() > 0) {

                                                        Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);

                                                        final String strCarlaJsonId = response.getJsonDocList().get(0).getDocId();

                                                        String strDocument = jsonDocument.getJsonDoc();

                                                        try {
                                                            responseJSONDocCarla = new JSONObject(strDocument);

                                                            if (responseJSONDocCarla.has("dependents")) {

                                                                JSONArray dependantsA = responseJSONDocCarla.
                                                                        getJSONArray("dependents");

                                                                for(int i=0; i< dependantsA.length(); i++){

                                                                    JSONObject jsonObjectActivities = dependantsA.
                                                                            getJSONObject(i);

                                                                    if(jsonObjectActivities.has("activities")) {

                                                                        JSONArray dependantsActivities = jsonObjectActivities.
                                                                                getJSONArray("activities");

                                                                        for (int j = 0; j < dependantsActivities.length(); j++) {

                                                                            JSONObject jsonObjectActivity = dependantsActivities.getJSONObject(j);

                                                                            if (jsonObjectActivity.getString("activity_date").equalsIgnoreCase(act.getStrActivityDate())) {

                                                                                jsonObjectActivity.put("activity_done_date", strDoneDate);
                                                                                jsonObjectActivity.put("status", "completed");

                                                                                JSONArray jsonArrayFeatures = jsonObjectActivity.getJSONArray("features_done");

                                                                                jsonArrayFeatures.put(jsonArrayFeaturesDone);

                                                                                JSONArray jsonArrayImages = jsonObjectActivity.getJSONArray("images");

                                                                                jsonArrayImages.put(jsonArrayImagesAdded);
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                //dependantsA.put(jsonObjectActCarla);

                                                            }


                                                            Libs.log(responseJSONDocCarla.toString(), " onj 2 ");

                                                            storageService.updateDocs(responseJSONDocCarla, strCarlaJsonId, Config.collectionName2, new App42CallBack() {
                                                                @Override
                                                                public void onSuccess(Object o) {

                                                                    if (o != null) {
                                                                        Intent intent = new Intent(FeatureActivity.this, DashboardActivity.class);
                                                                        if (progressDialog.isShowing())
                                                                            progressDialog.dismiss();

                                                                        Config.intSelectedMenu=Config.intDashboardScreen;
                                                                        startActivity(intent);
                                                                        finish();

                                                                    } else {
                                                                        if (progressDialog.isShowing())
                                                                            progressDialog.dismiss();
                                                                        libs.toast(2, 2, getString(R.string.warning_internet));
                                                                    }
                                                                }

                                                                @Override
                                                                public void onException(Exception e) {
                                                                    if (progressDialog.isShowing())
                                                                        progressDialog.dismiss();
                                                                    if (e != null) {
                                                                        libs.toast(2, 2, e.getMessage());
                                                                    } else {
                                                                        libs.toast(2, 2, getString(R.string.warning_internet));
                                                                    }
                                                                }
                                                            });

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                } else {
                                                    if (progressDialog.isShowing())
                                                        progressDialog.dismiss();
                                                    libs.toast(2, 2, getString(R.string.warning_internet));
                                                }
                                            }

                                            @Override
                                            public void onInsertionFailed(App42Exception ex) {

                                            }

                                            @Override
                                            public void onFindDocFailed(App42Exception ex) {
                                                if (progressDialog.isShowing())
                                                    progressDialog.dismiss();

                                                if (ex != null) {
                                                    libs.toast(2, 2, ex.getMessage());
                                                } else {
                                                    libs.toast(2, 2, getString(R.string.warning_internet));
                                                }
                                            }

                                            @Override
                                            public void onUpdateDocFailed(App42Exception ex) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onException(Exception e) {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    if (e != null) {
                                        libs.toast(2, 2, e.getMessage());
                                    } else {
                                        libs.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                }
                            });


                        } else {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            libs.toast(2, 2, getString(R.string.warning_internet));
                        }

                    } else {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        libs.toast(2, 2, getString(R.string.warning_internet));
                    }
                }

                @Override
                public void onException(Exception e) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    if (e != null) {
                        libs.toast(2, 2, e.getMessage());
                    } else {
                        libs.toast(2, 2, getString(R.string.warning_internet));
                    }
                }
            });

    }
}
    //https://github.com/balamurugan-adstringo/CareTaker.git

    public class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();
            imageView = new ImageView(FeatureActivity.this);

            LinearLayout layout = (LinearLayout) findViewById(R.id.linear);

            if ( imageView!= null && strImageName != null && !strImageName.equalsIgnoreCase("") && bitmap != null)
                try {
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e){
                    e.printStackTrace();
                }
            for (int i = 0; i < 1; i++) {
                imageView.setId(i);
                imageView.setPadding(0, 0, 10, 0);
                imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                layout.addView(imageView);
            }
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {

            try {
                System.out.println("URI IS : "+ uri);
                if (uri != null) {
                    Calendar calendar = new GregorianCalendar();
                    String strFileName = String.valueOf(calendar.getTimeInMillis()) + ".jpeg";
                    File galleryFile = libs.createFileInternalImage(strFileName);
                    strImageName = galleryFile.getAbsolutePath();

                    System.out.println("YOUR GALLERY PATH IS : "+ strImageName);

                    Date date = new Date();

                    ImageModel imageModel = new ImageModel(strImageName,"", galleryFile.getName(), libs.convertDateToString(date));
                    arrayListImageModel.add(imageModel);

                    InputStream is = getContentResolver().openInputStream(uri);
                    libs.copyInputStreamToFile(is, galleryFile);

                    bitmap = libs.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight);
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (IOException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class BackgroundThreadCamera extends Thread {
        @Override
        public void run() {
            try {
                /*if (strImageName != null && !strImageName.equalsIgnoreCase("")) {
                    bitmap = libs.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight);
                }*/
                if (uri != null) {
                    Calendar calendar = new GregorianCalendar();
                    String strFileName = String.valueOf(calendar.getTimeInMillis()) + ".jpeg";
                    File cameraFile = libs.createFileInternal(strFileName);
                    strImageName = cameraFile.getAbsolutePath();
                    System.out.println("Your CAMERA path is : " + strImageName);

                    Date date = new Date();

                    ImageModel imageModel = new ImageModel(strImageName,"", cameraFile.getName(), libs.convertDateToString(date));
                    arrayListImageModel.add(imageModel);

                    InputStream is = getContentResolver().openInputStream(uri);
                    libs.copyInputStreamToFile(is, cameraFile);
                    bitmap = libs.getBitmapFromFile(strImageName,Config.intWidth,Config.intHeight);
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}