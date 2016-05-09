package com.hdfc.caregiver;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hdfc.adapters.ExpandableListAdapter;
import com.hdfc.adapters.FeatureAdapter;
import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UploadService;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;
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
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class FeatureActivity extends AppCompatActivity implements Serializable{

    public static Uri uri;
    public static List<String> listFeatures;
    public static String strImageName = "";
    public static int IMAGE_COUNT = 0;
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
    private static LinearLayout layout;
    private static String strName;
    private static ArrayList<String> imagePaths = new ArrayList<>();
    private static ArrayList<Bitmap> bitmaps = new ArrayList<>();
    public JSONObject json;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    //Expandable listview Adapter
    private JSONObject responseJSONDoc,jsonNotification;
    private JSONObject responseJSONDocCarla;
    private Utils utils;
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
        //ListView featuresList = (ListView) findViewById(R.id.list_view);
        //surfaceView = (SurfaceView)findViewById(R.id.camerapreview);
        TextView textViewEmpty = (TextView) findViewById(android.R.id.empty);
        TextView textViewTime = (TextView) findViewById(R.id.textViewTime);
        // featureAdapter = new FeatureAdapter(this, listFeatures);

        layout = (LinearLayout) findViewById(R.id.linear);

 IMAGE_COUNT=0;
        MultiBitmapLoader multiBitmapLoader = new MultiBitmapLoader(FeatureActivity.this);

        TextView dependentName = (TextView) findViewById(R.id.textViewHeaderTaskDetail);

        jsonArrayImagesAdded = new JSONArray();

  arrayListImageModel.clear();
        bitmaps.clear();
        try {

            Bundle b = getIntent().getExtras();
            /*intWhichScreen = b.getInt("WHICH_SCREEN", Config.intSimpleActivityScreen);*/

            act = (ActivityModel) b.getSerializable("ACTIVITY");

            utils = new Utils(FeatureActivity.this);

           /* if (act.getFeatures() == null || act.getFeatures().length <= 0)
                act.setFeatures(new String[]{"corn", "potato"});
            List<String> lstFeatures = new ArrayList<>(Arrays.asList(act.getFeatures()));

            dependentName.setText(act.getStrActivityDependentName());

            featureAdapter = new FeatureAdapter(this, lstFeatures);*/

            textViewTime.setText(utils.formatDateTime(act.getStrActivityDate()));

            //Utils.log(act.getStrActivityDate(), " Date ");
            //Utils.log(act.getFeatures().toString(),"Features");


            //
            //Utils.log(utils.replaceSpace(act.getStrActivityDependentName()), " NAME ");
            File fileImage = Utils.createFileInternal("images/" + utils.replaceSpace(act.getStrDependentID()));

            if (fileImage.exists()) {
                String filename = fileImage.getAbsolutePath();
                multiBitmapLoader.loadBitmap(filename, imgLogoHeaderTaskDetail);
            } else {
                imgLogoHeaderTaskDetail.setImageDrawable(getResources().getDrawable(R.drawable.mrs_hungal_circle2));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        //utils = new Utils(FeatureActivity.this);

        //String strCustomerImagePath = getFilesDir() + "/images/" + "feature_image";

        mProgress = new ProgressDialog(FeatureActivity.this);
        progressDialog = new ProgressDialog(FeatureActivity.this);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeatureActivity.IMAGE_COUNT = 0;
                if (arrayListImageModel.size() > 0)
                    if (FeatureAdapter.selectedStrings.size() > 0)
                        uploadImage();
                    else
                        utils.toast(2, 2, "Select a feature");
                else
                    utils.toast(2, 2, "Select a Image");
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


/* if (featuresList != null) {
        featuresList.setAdapter(featureAdapter);
 }
if (featuresList != null) {
        featuresList.setEmptyView(textViewEmpty);
 }*/

  if (back != null) {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               IMAGE_COUNT = 0;
                Intent intent = new Intent(FeatureActivity.this,DashboardActivity.class);
                //intent.putExtra("WHICH_SCREEN", intWhichScreen);
                Config.intSelectedMenu=Config.intDashboardScreen;
                startActivity(intent);
            }
        });
}

if (attach != null) {
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open popup window
                if (p != null)
 if(IMAGE_COUNT<4)
                    showStatusPopup(FeatureActivity.this, p);
             else{
     utils.toast(2, 2, "Maximum 4 Images only Allowed");
                        }
			
			}
        });
     }


        //Expandable Listview

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Appointment Confirmation");
        listDataHeader.add("Travel Update");
        listDataHeader.add("Activity closure");


        // Adding child data
        List<String> appointment = new ArrayList<String>();
        appointment.add("Lab Name");
        appointment.add("Test Performed");
        appointment.add("Date and Time");

        List<String> travel = new ArrayList<String>();
        travel.add("Self");
        travel.add("Accompany");
        travel.add("Car Booking");


        List<String> activity = new ArrayList<String>();
        activity.add("Comments");


        listDataChild.put(listDataHeader.get(0), appointment); // Header, Child data
        listDataChild.put(listDataHeader.get(1), travel);
        listDataChild.put(listDataHeader.get(2), activity);
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
        ImageView imageGallery = (ImageView) layout.findViewById(R.id.imageView2);
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

        strName = String.valueOf(new Date().getDate() + "" + new Date().getTime());
        strImageName = strName + ".jpeg";


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
                utils.selectImage(strImageName, null, FeatureActivity.this, false);

                //  Intent intent = new Intent();
                // intent.setType("image/*");
                //  intent.setAction(Intent.ACTION_CAMERA_BUTTON);
                //  startActivityForResult(Intent.createChooser(intent, "Select Picture"),0);
            }
        });
        imageGallery.setOnClickListener(new View.OnClickListener() {
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
                utils.selectImage(strImageName, null, FeatureActivity.this, false);

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
                //Utils.toast(1, 1, "Getting Image...");

                mProgress.setMessage(getString(R.string.loading));
mProgress.setCancelable(false);
                mProgress.show();
                switch (requestCode) {
                    case Config.START_CAMERA_REQUEST_CODE:

                        backgroundThreadHandler = new BackgroundThreadHandler();
                        strImageName = Utils.customerImageUri.getPath();
                        Utils.log(strImageName, " IMSGR ");
                        Thread backgroundThreadCamera = new BackgroundThreadCamera();
                        backgroundThreadCamera.start();
                        break;

                    case Config.START_GALLERY_REQUEST_CODE:
                        backgroundThreadHandler = new BackgroundThreadHandler();
                        // strCustomerImgName = Utils.customerImageUri.getPath();
                       // if (intent.getData() != null) {
                        //    uri = intent.getData();
						
						  imagePaths.clear();

                        String[] all_path = intent.getStringArrayExtra("all_path");

                        //Utils.log(String.valueOf(all_path.length), "size");

                        if(all_path.length+IMAGE_COUNT>4){

                            for (int i=0;i<(4-IMAGE_COUNT);i++) {
                                imagePaths.add(all_path[i]);
                            }

                        }else {

                            for (String string : all_path) {
                                imagePaths.add(string);
                            }
                        }
                            Thread backgroundThread = new BackgroundThread();
                            backgroundThread.start();

                           break;

                        /*backgroundThreadHandler = new BackgroundThreadHandler();
                        // strCustomerImgName = Utils.customerImageUri.getPath();
                        if (intent.getData() != null) {
                            uri = intent.getData();
                            Thread backgroundThread = new BackgroundThread();
                            backgroundThread.start();
                        }
                        break;*/
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

            if (utils.isConnectingToInternet()) {

                for (final ImageModel imageModel : arrayListImageModel) {

                    UploadService uploadService = new UploadService(this);

                    uploadService.uploadImageCommon(imageModel.getStrImageName(),
                            imageModel.getStrImageDesc(), imageModel.getStrImageDesc(),
                            Config.providerModel.getStrEmail(), UploadFileType.IMAGE,
                            new App42CallBack() {
 public void onSuccess(Object response) {

                            if (response != null) {

                                Upload upload = (Upload) response;
                                ArrayList<Upload.File> fileList = upload.getFileList();

                                if (fileList.size() > 0) {

                                    Upload.File file = fileList.get(0);

                                    JSONObject jsonObjectImages = new JSONObject();

                                    try {
                                        jsonObjectImages.put("image_name", imageModel.getStrImageDesc());
                                        //Log.e("Image URL : ", file.getUrl());
                                        jsonObjectImages.put("image_url", file.getUrl());
                                        jsonObjectImages.put("image_description", imageModel.getStrImageDesc());
                                        //Log.e("Time of image : ", imageModel.getStrImageTime());
                                        jsonObjectImages.put("image_taken", imageModel.getStrImageTime());

                                        jsonArrayImagesAdded.put(jsonObjectImages);

                                        arrayListImageModel.remove(imageModel);

                                        if (arrayListImageModel.size() <= 0)
                                            uploadCheckBox();
                                     //   uploadNotification();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    uploadImage();
                                    //   utils.toast(2, 2, ((Upload) response).getStrResponse());
                                }
                            } else {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onException(Exception e) {

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            if (e != null) {
                                Utils.log(e.toString(), "response");
                                //  utils.toast(2, 2, e.getMessage());
                                uploadImage();
                            } else {
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }
                    });
                }

            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                utils.toast(2, 2, getString(R.string.warning_internet));
            }

        }catch (Exception e){
            e.printStackTrace();
            if (progressDialog.isShowing())
                progressDialog.dismiss();

          //  uploadImage();
            utils.toast(2, 2, getString(R.string.error));
        }
    }

    public void uploadCheckBox() {

        if (utils.isConnectingToInternet()) {
            storageService = new StorageService(FeatureActivity.this);


            try {
                jsonArrayFeaturesDone = new JSONArray();

                for (String strings : FeatureAdapter.selectedStrings) {
                    jsonArrayFeaturesDone.put(strings);
                }

                Date doneDate = new Date();

                strDoneDate = utils.convertDateToString(doneDate);

            } catch (Exception e) {
                e.printStackTrace();
            }

            storageService.findDocsByIdApp42CallBack(Config.providerModel.getStrProviderId(), Config.collectionProvider, new App42CallBack() {
                @Override
                public void onSuccess(Object o) {

                    if (o != null) {

                        final Storage findObj = (Storage) o;

                        try {
                            responseJSONDoc = new JSONObject(findObj.getJsonDocList().get(0).getJsonDoc());

                            Utils.log(responseJSONDoc.toString(), " Res");
                            if (responseJSONDoc.has("activities")) {
                                JSONArray dependantsA = responseJSONDoc.
                                        getJSONArray("activities");

                                for (int i = 0; i < dependantsA.length(); i++) {

                                    JSONObject jsonObjectActivity = dependantsA.getJSONObject(i);

                                    if (jsonObjectActivity.getString("activity_date").equalsIgnoreCase(act.getStrActivityDate()) &&
                                            jsonObjectActivity.getString("activity_name").equalsIgnoreCase(act.getStrActivityName()) &&
                                            jsonObjectActivity.getString("activity_message").equalsIgnoreCase(act.getStrActivityDesc())) {


                                        jsonObjectActivity.put("activity_done_date", strDoneDate);
                                        jsonObjectActivity.put("status", "completed");

                                        jsonObjectActivity.put("features_done", jsonArrayFeaturesDone);
                                        jsonObjectActivity.put("images", jsonArrayImagesAdded);

                                        /*JSONArray jsonArrayFeatures = jsonObjectActivity.getJSONArray("features_done");

                                        jsonArrayFeatures.put(jsonArrayFeaturesDone);

                                        JSONArray jsonArrayImages = jsonObjectActivity.getJSONArray("images");

                                        jsonArrayImages.put(jsonArrayImagesAdded);*/
                                    }
                                }

                                //dependantsA.put(jsonObjectActCarla);

                            }
                        } catch (JSONException jSe) {
                            jSe.printStackTrace();
                            progressDialog.dismiss();
                        }

                        //Utils.log(responseJSONDoc.toString(), " onj 1 ");

                        if (utils.isConnectingToInternet()) {//TODO check activity added

                            storageService.updateDocs(responseJSONDoc, Config.providerModel.getStrProviderId(),
                                    Config.collectionProvider, new App42CallBack() {
                                @Override
                                public void onSuccess(Object o) {

                                    Config.jsonObject = responseJSONDoc;

                                    if (o != null) {

                                        storageService.findDocsByKeyValue(Config.collectionCustomer,
                                                "customer_id", act.getStrDependentID(),
                                                new AsyncApp42ServiceApi.App42StorageServiceListener() {
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
                                                            Utils.log(responseJSONDocCarla.toString(), " Res 1 ");

                                                            if (responseJSONDocCarla.has("dependents")) {

                                                                JSONArray dependantsA = responseJSONDocCarla.
                                                                        getJSONArray("dependents");

                                                                for (int i = 0; i < dependantsA.length(); i++) {

                                                                    JSONObject jsonObjectActivities = dependantsA.
                                                                            getJSONObject(i);

                                                                    if (jsonObjectActivities.getString("dependent_id").equalsIgnoreCase(act.getStrDependentID())) {

                                                                        if (jsonObjectActivities.has("activities")) {

                                                                            JSONArray dependantsActivities = jsonObjectActivities.
                                                                                    getJSONArray("activities");

                                                                            for (int j = 0; j < dependantsActivities.length(); j++) {

                                                                                JSONObject jsonObjectActivity = dependantsActivities.getJSONObject(j);

                                                                                if (jsonObjectActivity.getString("activity_date").equalsIgnoreCase(act.getStrActivityDate()) &&
                                                                                        jsonObjectActivity.getString("activity_name").equalsIgnoreCase(act.getStrActivityName()) &&
                                                                                        jsonObjectActivity.getString("activity_message").equalsIgnoreCase(act.getStrActivityDesc())) {

                                                                                    jsonObjectActivity.put("activity_done_date", strDoneDate);
                                                                                    jsonObjectActivity.put("status", "completed");

                                                                                    jsonObjectActivity.put("features_done", jsonArrayFeaturesDone);
                                                                                    jsonObjectActivity.put("images", jsonArrayImagesAdded);

                                                                                    /*JSONArray jsonArrayFeatures = jsonObjectActivity.getJSONArray("features_done");

                                                                                    jsonArrayFeatures.put(jsonArrayFeaturesDone);

                                                                                    JSONArray jsonArrayImages = jsonObjectActivity.getJSONArray("images");

                                                                                    jsonArrayImages.put(jsonArrayImagesAdded);*/
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                //dependantsA.put(jsonObjectActCarla);

                                                            }


                                                            //Utils.log(responseJSONDocCarla.toString(), " onj 2 ");

                                                            storageService.updateDocs(responseJSONDocCarla, strCarlaJsonId, Config.collectionCustomer, new App42CallBack() {
                                                                @Override
                                                                public void onSuccess(Object o) {

                                                                    if (o != null) {
                                                                      
                                                                   uploadNotification();

                                                                    } else {
                                                                        if (progressDialog.isShowing())
                                                                            progressDialog.dismiss();
                                                                        utils.toast(2, 2, getString(R.string.warning_internet));
                                                                    }
                                                                }

                                                                @Override
                                                                public void onException(Exception e) {
                                                                    if (progressDialog.isShowing())
                                                                        progressDialog.dismiss();
                                                                    if (e != null) {
                                                                        utils.toast(2, 2, e.getMessage());
                                                                    } else {
                                                                        utils.toast(2, 2, getString(R.string.warning_internet));
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
                                                    utils.toast(2, 2, getString(R.string.warning_internet));
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
                                                    utils.toast(2, 2, ex.getMessage());
                                                } else {
                                                    utils.toast(2, 2, getString(R.string.warning_internet));
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
                                        utils.toast(2, 2, e.getMessage());
                                    } else {
                                        utils.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                }
                            });


                        } else {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            utils.toast(2, 2, getString(R.string.warning_internet));
                        }

                    } else {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        utils.toast(2, 2, getString(R.string.warning_internet));
                    }
                }

                @Override
                public void onException(Exception e) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    if (e != null) {
                        utils.toast(2, 2, e.getMessage());
                    } else {
                        utils.toast(2, 2, getString(R.string.warning_internet));
                    }
                }
            });
        }
    }
    public void uploadNotification(){
        //act.getStrCustomerEmail()      getCustomer Email

        if (utils.isConnectingToInternet()) {

            //Utils.log(act.getStrCustomerEmail(), " mail ");
        storageService.findDocsByKeyValue(Config.collectionCustomer,
                "customer_id",
                act.getStrDependentID(),
                new AsyncApp42ServiceApi.App42StorageServiceListener() {
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

                                for (int i = 0; i < dependantsA.length(); i++) {

                                    JSONObject jsonObjectActivities = dependantsA.
                                            getJSONObject(i);

                                    if (jsonObjectActivities.getString("dependent_id").equalsIgnoreCase(act.getStrDependentID())) {

                                        if (jsonObjectActivities.has("notifications")) {

                                            JSONArray dependantsNotification = jsonObjectActivities.
                                                    getJSONArray("notifications");

                                           // for (int j = 0; j < dependantsNotification.length(); j++) {

                                            //    jsonNotification = dependantsNotification.getJSONObject(j);
                                           // }

                                          
                                            jsonNotification.put("author", Config.providerModel.getStrName());
                                            jsonNotification.put("time", strDoneDate);
                                            jsonNotification.put("author_profile_url", "");
                                            jsonNotification.put("notification_message", "Service  Successfully");

                                            dependantsNotification.put(jsonNotification);
                                   }
                                }
                            }

                            storageService.updateDocs(responseJSONDocCarla, strCarlaJsonId, Config.collectionCustomer, new App42CallBack() {

                                @Override
                                public void onSuccess(Object o) {
                                    utils.toast(2, 2, "Notification Added Successfully");

                                              if (progressDialog.isShowing())
                                                progressDialog.dismiss();



                                            IMAGE_COUNT = 0;

                                    //utils.toast(2, 2, "Notification Added Successfully");

                                            Intent intent = new Intent(FeatureActivity.this, DashboardActivity.class);

                                    utils.toast(2, 2, getString(R.string.activity_closed));

                                            Config.intSelectedMenu=Config.intDashboardScreen;
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onException(Exception e) {
                                            if (progressDialog.isShowing())
                                                progressDialog.dismiss();
                                            if(e==null) {
                                                utils.toast(2, 2, getString(R.string.warning_internet));
                                            } else utils.toast(2, 2, getString(R.string.error));
                                        }
                            });
						}
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
}
                    }else{
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    utils.toast(2, 2, getString(R.string.warning_internet));
 }
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
 } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
 }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmaps.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        IMAGE_COUNT = 0;
        arrayListImageModel.clear();
        bitmaps.clear();
    }

    public class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

             layout.removeAllViews();

            for(int i=0;i<bitmaps.size();i++) {
                try {
                    //Utils.log(" 2 " + String.valueOf(bitmap.getHeight()), " IN ");
                    ImageView imageView = new ImageView(FeatureActivity.this);
                    imageView.setPadding(0, 0, 3, 0);
                    imageView.setImageBitmap(bitmap);
                    imageView.setTag(bitmaps.get(i));
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                  imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Dialog dialog = new Dialog(FeatureActivity.this);
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


                     layout.addView(imageView);
                  } catch (Exception | OutOfMemoryError e) {
                    //bitmap.recycle();
                    e.printStackTrace();
                }
            }

               mProgress.dismiss();
        }
    }

       public class BackgroundThread extends Thread {
        @Override
        public void run() {

            try {
                //System.out.println("URI IS : " + uri);
               // if (uri != null) {
                for(int i=0;i<imagePaths.size();i++) {
                    Calendar calendar = new GregorianCalendar();
                    String strFileName = String.valueOf(calendar.getTimeInMillis()) + ".jpeg";
                    File galleryFile = utils.createFileInternalImage(strFileName);
                    strImageName = galleryFile.getAbsolutePath();

                    //System.out.println("YOUR GALLERY PATH IS : " + strImageName);

                    Date date = new Date();

                    ImageModel imageModel = new ImageModel(strImageName, "", galleryFile.getName(), utils.convertDateToString(date));
                    arrayListImageModel.add(imageModel);

                  //  InputStream is = getContentResolver().openInputStream(uri);
                    //  utils.copyInputStreamToFile(is, galleryFile);

                    utils.copyFile(new File(imagePaths.get(i)), galleryFile);
                    bitmap = utils.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight);
                 IMAGE_COUNT++;
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (IOException |OutOfMemoryError e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class BackgroundThreadCamera extends Thread {
        @Override
        public void run() {
            try {
                if (strImageName != null && !strImageName.equalsIgnoreCase("")) {

                    Date date = new Date();
                    ImageModel imageModel = new ImageModel(strImageName, "", strName, utils.convertDateToString(date));
                    arrayListImageModel.add(imageModel);
                    bitmaps.add(utils.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight));
                }
                /*if (uri != null) {
                    Calendar calendar = new GregorianCalendar();
                    String strFileName = String.valueOf(calendar.getTimeInMillis()) + ".jpeg";
                    File cameraFile = utils.createFileInternal(strFileName);
                    strImageName = cameraFile.getAbsolutePath();
                    System.out.println("Your CAMERA path is : " + strImageName);

                    Date date = new Date();

                    ImageModel imageModel = new ImageModel(strImageName, "", cameraFile.getName(), utils.convertDateToString(date));
                    arrayListImageModel.add(imageModel);

                    InputStream is = getContentResolver().openInputStream(uri);
                    utils.copyInputStreamToFile(is, cameraFile);
                    bitmap = utils.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight);
                }*/
                 IMAGE_COUNT++;
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
    }
}