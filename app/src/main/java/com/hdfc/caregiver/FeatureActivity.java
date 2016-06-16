package com.hdfc.caregiver;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.hdfc.app42service.App42GCMService;
import com.hdfc.app42service.PushNotificationService;
import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UploadService;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.FieldModel;
import com.hdfc.models.ImageModel;
import com.hdfc.models.MilestoneModel;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FeatureActivity extends AppCompatActivity {

    public static int IMAGE_COUNT = 0;
    private static String strImageName = "";
    //private static Bitmap bitmap = null;
    private static StorageService storageService;
    private static ArrayList<ImageModel> arrayListImageModel = new ArrayList<>();
    private static Handler backgroundThreadHandler;
    //private static ProgressDialog mProgress = null;
    private static String strAlert;
    private static JSONObject jsonObject;
    private static ActivityModel act;
    private static LinearLayout layout;
    private static String strName, strPushMessage, strDependentMail;
    private static ArrayList<String> imagePaths = new ArrayList<>();
    private static ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private static Dialog dialog;
    private static RelativeLayout loadingPanel;
    private static boolean bLoad;
    private static boolean bViewLoaded;
    private static String strActivityStatus = "inprocess";
    private final Context context = this;
    private Utils utils;
    //private ProgressDialog progressDialog;
    private Point p;
    private JSONArray jsonArrayImagesAdded;
    private TextView textViewName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_features);

        //ImageView attach = (ImageView) findViewById(R.id.imgAttachHeaderTaskDetail);
        ImageView imgLogoHeaderTaskDetail = (ImageView) findViewById(R.id.imgLogoHeaderTaskDetail);
        Button done = (Button) findViewById(R.id.buttonVegetibleDone);
        ImageView back = (ImageView) findViewById(R.id.imgBackHeaderTaskDetail);
        LinearLayout linearLayoutAttach = (LinearLayout) findViewById(R.id.linearLayout);
        Button cancel = (Button) findViewById(R.id.buttonBack);
        TextView textViewActivityName = (TextView) findViewById(R.id.txtActivityName);
        TextView textViewTime = (TextView) findViewById(R.id.txtActivityTime);

        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        layout = (LinearLayout) findViewById(R.id.linear);
        bLoad = false;

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        IMAGE_COUNT = 0;
        MultiBitmapLoader multiBitmapLoader = new MultiBitmapLoader(FeatureActivity.this);

        TextView dependentName = (TextView) findViewById(R.id.textViewHeaderTaskDetail);

        jsonArrayImagesAdded = new JSONArray();
        arrayListImageModel.clear();
        bitmaps.clear();

        try {

            Bundle b = getIntent().getExtras();

            act = (ActivityModel) b.getSerializable("ACTIVITY");

            utils = new Utils(FeatureActivity.this);

            int iPosition = Config.dependentIdsAdded.indexOf(act.getStrDependentID());
            String name = Config.dependentModels.get(iPosition).getStrName();

            int iPositionCustomer = Config.customerIdsAdded.indexOf(act.getStrCustomerID());

            strDependentMail = Config.customerModels.get(iPositionCustomer).getStrEmail();

            if (name.length() > 20)
                name = name.substring(0, 18) + "..";

            if (dependentName != null) {
                dependentName.setText(name);
            }

            String strActivityName = act.getStrActivityName();

            if (strActivityName.length() > 20)
                strActivityName = strActivityName.substring(0, 18) + "..";

            if (textViewActivityName != null) {
                textViewActivityName.setText(strActivityName);
            }

            if (textViewTime != null) {
                textViewTime.setText(utils.formatDate(act.getStrActivityDate()));
            }

            File fileImage = Utils.createFileInternal("images/" + utils.replaceSpace(act.getStrDependentID()));

            if (fileImage.exists()) {
                String filename = fileImage.getAbsolutePath();
                multiBitmapLoader.loadBitmap(filename, imgLogoHeaderTaskDetail);
            } else {
                if (imgLogoHeaderTaskDetail != null) {
                    imgLogoHeaderTaskDetail.setImageDrawable(getResources().getDrawable(R.drawable.person_icon));
                }
            }

        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
        }

        storageService = new StorageService(FeatureActivity.this);

        if (done != null) {
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IMAGE_COUNT = 0;

                    if (arrayListImageModel.size() > 0) {

                        if (utils.isConnectingToInternet()) {
                            loadingPanel.setVisibility(View.VISIBLE);
                            uploadImage();
                        } else {
                            utils.toast(2, 2, getString(R.string.warning_internet));
                        }
                    } else utils.toast(2, 2, "Select a Image");
                }
            });
        }


        if (back != null) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBack();
                }
            });
        }

        if (linearLayoutAttach != null) {
            linearLayoutAttach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Open popup window
                    if (p != null)
                        if (IMAGE_COUNT < 4)
                            showStatusPopup(FeatureActivity.this, p);
                        else {
                        utils.toast(2, 2, "Maximum 4 Images only Allowed");
                        }

                }
            });
        }

        bViewLoaded = false;

        loadingPanel.setVisibility(View.VISIBLE);

        backgroundThreadHandler = new BackgroundThreadHandler();
        Thread backgroundThreadImages = new BackgroundThreadImages();
        backgroundThreadImages.start();
    }

    private void goBack() {
        arrayListImageModel.clear();
        bitmaps.clear();
        IMAGE_COUNT = 0;
        Intent intent = new Intent(FeatureActivity.this, DashboardActivity.class);
        Config.intSelectedMenu = Config.intDashboardScreen;
        intent.putExtra("LOAD", bLoad);
        startActivity(intent);
        finish();
    }

    private void traverseEditTexts(ViewGroup v, int iMileStoneId, ViewGroup viewGroup, int iFlag) {

        boolean b = true;

        try {

            Calendar calendar = Calendar.getInstance();

            String strScheduledDate = "";

            int iPosition = Config.strActivityIds.indexOf(act.getStrActivityID());

            Config.activityModels.get(iPosition).clearMilestoneModel();

            int iIndex = 0;

            for (MilestoneModel milestoneModel : act.getMilestoneModels()) {

                iIndex++;

                if (milestoneModel.getiMilestoneId() == iMileStoneId) {

                    if (iIndex == act.getMilestoneModels().size() && iFlag == 2) {
                        strActivityStatus = "completed";
                    }

                    strScheduledDate = "";

                    Date date = calendar.getTime();
                    milestoneModel.setStrMilestoneDate(utils.convertDateToString(date));

                    //if(iFlag==1)
                    //milestoneModel.setStrMilestoneStatus("opened");

                    if (iFlag == 2)
                        milestoneModel.setStrMilestoneStatus("completed");

                    for (FieldModel fieldModel : milestoneModel.getFieldModels()) {

                        //text
                        if (fieldModel.getStrFieldType().equalsIgnoreCase("text")
                                || fieldModel.getStrFieldType().equalsIgnoreCase("number")
                                || fieldModel.getStrFieldType().equalsIgnoreCase("datetime")
                                || fieldModel.getStrFieldType().equalsIgnoreCase("date")
                                || fieldModel.getStrFieldType().equalsIgnoreCase("time")) {

                            EditText editText = (EditText) v.findViewById(fieldModel.getiFieldID());

                            boolean b1 = (Boolean) editText.getTag(R.id.one);
                            String data = editText.getText().toString().trim();

                            if (editText.isEnabled()) {
                                if (b1 && !data.equalsIgnoreCase("")) {

                                    boolean bFuture = true;

                                    /////////////////////////////
                                    Date dateNow = null;
                                    String strdateCopy;
                                    Date enteredDate = null;

                                    try {
                                        strdateCopy = Utils.writeFormat.format(calendar.getTime());
                                        dateNow = Utils.writeFormat.parse(strdateCopy);
                                        enteredDate = Utils.writeFormat.parse(data);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    if (dateNow != null && enteredDate != null) {

                                        Utils.log(String.valueOf(date + " ! " + enteredDate), " NOW ");

                                        if (enteredDate.before(dateNow)) {
                                            bFuture = false;
                                        }
                                    }
                                    /////////////////////////////

                                    if (bFuture) {

                                        fieldModel.setStrFieldData(data);

                                        if ((milestoneModel.isReschedule() || !milestoneModel.isReschedule())
                                                && milestoneModel.getStrMilestoneScheduledDate() != null
                                                && (!milestoneModel.getStrMilestoneScheduledDate().equalsIgnoreCase("")
                                                || milestoneModel.getStrMilestoneScheduledDate().equalsIgnoreCase(""))
                                                && fieldModel.getStrFieldType().equalsIgnoreCase("datetime")
                                                ) {

                                            if (milestoneModel.getStrMilestoneScheduledDate() != null
                                                    && !milestoneModel.getStrMilestoneScheduledDate().
                                                    equalsIgnoreCase(""))
                                                milestoneModel.setReschedule(true);

                                            String strDate = (String) editText.getTag(R.id.two);
                                            milestoneModel.setStrMilestoneScheduledDate(strDate); //todo check possiblity for diff TZ

                                            strScheduledDate = strDate;
                                        }

                                    } else {
                                        editText.setError(context.getString(R.string.invalid_date));
                                        b = false;
                                    }

                                } else {
                                    editText.setError(context.getString(R.string.error_field_required));
                                    b = false;
                                }
                            }
                        }

                        //radio or dropdown
                        if (fieldModel.getStrFieldType().equalsIgnoreCase("radio")
                                || fieldModel.getStrFieldType().equalsIgnoreCase("dropdown")) {

                            Spinner spinner = (Spinner) v.findViewById(fieldModel.getiFieldID());

                            boolean b1 = (Boolean) spinner.getTag();

                            String data = fieldModel.getStrFieldValues()[spinner.getSelectedItemPosition()];

                            if (b1 && !data.equalsIgnoreCase("")) {
                                fieldModel.setStrFieldData(data);
                            } else {
                                utils.toast(2, 2, getString(R.string.error_field_required));
                                spinner.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text));
                                b = false;
                            }
                        }
                        //

                        //array
                        if (fieldModel.getStrFieldType().equalsIgnoreCase("array")) {

                            ArrayList<String> strMedicineNames = utils.getEditTextValueByTag(viewGroup, "medicine_name");
                            ArrayList<String> strMedicineQty = utils.getEditTextValueByTag(viewGroup, "medicine_qty");

                            int j = 0;

                            if (strMedicineNames.size() > 0) {

                                JSONObject jsonObject = new JSONObject();

                                JSONArray jsonArray = new JSONArray();

                                for (int i = 0; i < strMedicineNames.size(); i++) {

                                    if (!strMedicineNames.get(i).equalsIgnoreCase("") && !strMedicineQty.get(i).equalsIgnoreCase("")) {
                                        j++;
                                        JSONObject jsonObjectMedicine = new JSONObject();

                                        jsonObjectMedicine.put("medicine_name", strMedicineNames.get(i));
                                        jsonObjectMedicine.put("medicine_qty", Integer.parseInt(strMedicineQty.get(i)));

                                        jsonArray.put(jsonObjectMedicine);
                                    }
                                }
                                jsonObject.put("array_data", jsonArray);

                                fieldModel.setStrArrayData(jsonObject.toString());
                            }

                            if (j <= 0) {
                                b = false;
                                utils.toast(2, 2, getString(R.string.error_medicines));
                            }
                        }
                    }

                    //
                    //String strDate = milestoneModel.getStrMilestoneDate();

                    strPushMessage = Config.providerModel.getStrName()
                            + getString(R.string.has_updated)
                          /*  + getString(R.string.activity)
                            + getString(R.string.space)*/
                            + getString(R.string.space)
                            + act.getStrActivityName()
                            + getString(R.string.hyphen)
                           /* + getString(R.string.milestone)*/
                            + getString(R.string.space)
                            + milestoneModel.getStrMilestoneName();



                    if (strScheduledDate != null && !strScheduledDate.equalsIgnoreCase("")) {
                        strPushMessage = getString(R.string.scheduled_to) + strScheduledDate;
                    }

                    jsonObject = new JSONObject();

                    try {

                        String strDateNow = "";
                        Date dateNow = calendar.getTime();
                        strDateNow = utils.convertDateToString(dateNow);

                        jsonObject.put("created_by", Config.providerModel.getStrProviderId());
                        jsonObject.put("time", strDateNow);
                        jsonObject.put("user_type", "dependent");
                        jsonObject.put("user_id", act.getStrDependentID());
                        jsonObject.put("activity_id", act.getStrActivityID());//todo add to care taker
                        jsonObject.put("created_by_type", "provider");
                        jsonObject.put(App42GCMService.ExtraMessage, strPushMessage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //
                }

                //
                if (iPosition > -1) {
                    //Config.activityModels.get(iPosition).removeMilestoneModel(milestoneModel);
                    Config.activityModels.get(iPosition).setMilestoneModel(milestoneModel);
                }
                //
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (b) {

            for (MilestoneModel milestoneModel : act.getMilestoneModels()) {
                for (FieldModel fieldModel : milestoneModel.getFieldModels()) {
                    Utils.log(fieldModel.getStrFieldLabel() + " ~ " + fieldModel.getStrFieldData(), " DATA ");
                }
            }

            uploadJson();
        }
    }

    private void updateMileStones(JSONObject jsonToUpdate) {

        if (utils.isConnectingToInternet()) {

            dialog.dismiss();

            loadingPanel.setVisibility(View.VISIBLE);

            bLoad = true;

            Utils.log(jsonToUpdate.toString(), " JSON ");

            storageService.updateDocs(jsonToUpdate,
                    act.getStrActivityID(),
                    Config.collectionActivity, new App42CallBack() {
                        @Override
                        public void onSuccess(Object o) {
                            insertNotification();
                        }

                        @Override
                        public void onException(Exception e) {
                            loadingPanel.setVisibility(View.GONE);
                            utils.toast(2, 2, getString(R.string.warning_internet));
                        }
                    });
        } else {
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    private void updateImages() {

        if (utils.isConnectingToInternet()) {

            JSONObject jsonToUpdate = null;

            try {
                jsonToUpdate = new JSONObject();
                jsonToUpdate.put("images", jsonArrayImagesAdded);
            } catch (Exception e) {
                e.printStackTrace();
            }

            storageService.updateDocs(jsonToUpdate,
                    act.getStrActivityID(),
                    Config.collectionActivity, new App42CallBack() {
                        @Override
                        public void onSuccess(Object o) {
                            goToActivityList(getString(R.string.image_upload));
                        }

                        @Override
                        public void onException(Exception e) {
                            loadingPanel.setVisibility(View.GONE);
                            utils.toast(2, 2, getString(R.string.warning_internet));
                        }
                    });
        } else {
            loadingPanel.setVisibility(View.GONE);
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
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
        int OFFSET_Y = 155;

        //Clear the default translucent background
        changeStatusPopUp.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);
        ImageView imageCamera = (ImageView) layout.findViewById(R.id.imageView);
        ImageView imageGallery = (ImageView) layout.findViewById(R.id.imageView2);


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

                strName = String.valueOf(new Date().getDate() + "" + new Date().getTime());
                strImageName = strName + ".jpeg";

                utils.selectImage(strImageName, null, FeatureActivity.this, false);
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

                strName = String.valueOf(new Date().getDate() + "" + new Date().getTime());
                strImageName = strName + ".jpeg";

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
        if (attach != null) {
            attach.getLocationOnScreen(location);
        }

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

                loadingPanel.setVisibility(View.VISIBLE);
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
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if (arrayListImageModel.size() > 0) {

            bLoad = true;

            final ImageModel imageModel = arrayListImageModel.get(0);

            UploadService uploadService = new UploadService(this);

            uploadService.uploadImageCommon(imageModel.getStrImagePath(),
                    imageModel.getStrImageDesc(), imageModel.getStrImageDesc(),
                    Config.providerModel.getStrEmail(), UploadFileType.IMAGE,
                    new App42CallBack() {
                        public void onSuccess(Object response) {

                            if (response != null) {

                                //Utils.log(response.toString(), " Success ");

                                Upload upload = (Upload) response;
                                ArrayList<Upload.File> fileList = upload.getFileList();

                                if (fileList.size() > 0) {

                                    Upload.File file = fileList.get(0);

                                    JSONObject jsonObjectImages = new JSONObject();

                                    try {

                                        jsonObjectImages.put("image_name", imageModel.getStrImageName());
                                        jsonObjectImages.put("image_url", file.getUrl());
                                        jsonObjectImages.put("image_description", imageModel.getStrImageDesc());
                                        jsonObjectImages.put("image_taken", imageModel.getStrImageTime());

                                        jsonArrayImagesAdded.put(jsonObjectImages);

                                        arrayListImageModel.remove(imageModel);

                                        if (arrayListImageModel.size() <= 0) {
                                            updateImages();
                                        } else {
                                            uploadImage();
                                        }
                                        //

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    if (arrayListImageModel.size() <= 0) {
                                        updateImages();
                                    } else {
                                        uploadImage();
                                    }
                                }
                            } else {
                                loadingPanel.setVisibility(View.GONE);
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onException(Exception e) {

                            loadingPanel.setVisibility(View.GONE);

                            if (e != null) {
                                Utils.log(e.getMessage(), " Failure ");
                                if (arrayListImageModel.size() <= 0) {
                                    updateImages();
                                } else {
                                    uploadImage();
                        }
                            } else {
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                }
                    });
        } else {
            if (arrayListImageModel.size() <= 0) {
                updateImages();
            }
        }
    }

    private void uploadJson() {
        ///////////////////////
        JSONObject jsonObjectMileStone = new JSONObject();

        try {
            JSONArray jsonArrayMilestones = new JSONArray();

            for (MilestoneModel milestoneModel : act.getMilestoneModels()) {

                JSONObject jsonObjectMilestone = new JSONObject();

                jsonObjectMilestone.put("id", milestoneModel.getiMilestoneId());
                jsonObjectMilestone.put("status", milestoneModel.getStrMilestoneStatus());
                jsonObjectMilestone.put("name", milestoneModel.getStrMilestoneName());
                jsonObjectMilestone.put("date", milestoneModel.getStrMilestoneDate());

                jsonObjectMilestone.put("show", milestoneModel.isVisible());
                jsonObjectMilestone.put("reschedule", milestoneModel.isReschedule());
                jsonObjectMilestone.put("scheduled_date", milestoneModel.getStrMilestoneScheduledDate());

                JSONArray jsonArrayFields = new JSONArray();

                for (FieldModel fieldModel : milestoneModel.getFieldModels()) {

                    JSONObject jsonObjectField = new JSONObject();

                    jsonObjectField.put("id", fieldModel.getiFieldID());

                    if (fieldModel.isFieldView())
                        jsonObjectField.put("hide", fieldModel.isFieldView());

                    jsonObjectField.put("required", fieldModel.isFieldRequired());
                    jsonObjectField.put("data", fieldModel.getStrFieldData());
                    jsonObjectField.put("label", fieldModel.getStrFieldLabel());
                    jsonObjectField.put("type", fieldModel.getStrFieldType());

                    if (fieldModel.getStrFieldValues() != null && fieldModel.getStrFieldValues().length > 0)
                        jsonObjectField.put("values", utils.stringToJsonArray(fieldModel.getStrFieldValues()));

                    if (fieldModel.isChild()) {

                        jsonObjectField.put("child", fieldModel.isChild());

                        if (fieldModel.getStrChildType() != null && fieldModel.getStrChildType().length > 0)
                            jsonObjectField.put("child_type", utils.stringToJsonArray(fieldModel.getStrChildType()));

                        if (fieldModel.getStrChildValue() != null && fieldModel.getStrChildValue().length > 0)
                            jsonObjectField.put("child_value", utils.stringToJsonArray(fieldModel.getStrChildValue()));

                        if (fieldModel.getStrChildCondition() != null && fieldModel.getStrChildCondition().length > 0)
                            jsonObjectField.put("child_condition", utils.stringToJsonArray(fieldModel.getStrChildCondition()));

                        if (fieldModel.getiChildfieldID() != null && fieldModel.getiChildfieldID().length > 0)
                            jsonObjectField.put("child_field", utils.intToJsonArray(fieldModel.getiChildfieldID()));
                    }

                    //
                    if (fieldModel.getiArrayCount() > 0) {
                        jsonObjectField.put("array_fields", fieldModel.getiArrayCount());
                        jsonObjectField.put("array_type", utils.stringToJsonArray(fieldModel.getStrArrayType()));
                        jsonObjectField.put("array_data", fieldModel.getStrArrayData());
                    }
                    //

                    jsonArrayFields.put(jsonObjectField);

                    jsonObjectMilestone.put("fields", jsonArrayFields);
                }
                jsonArrayMilestones.put(jsonObjectMilestone);
            }
            ////////////////////

            jsonObjectMileStone.put("milestones", jsonArrayMilestones);

            jsonObjectMileStone.put("status", strActivityStatus);

            updateMileStones(jsonObjectMileStone);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        IMAGE_COUNT = 0;
        arrayListImageModel.clear();
        bitmaps.clear();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void addImages() {

        layout.removeAllViews();

        for (int i = 0; i < bitmaps.size(); i++) {
            try {
                //
                ImageView imageView = new ImageView(FeatureActivity.this);
                imageView.setPadding(0, 0, 3, 0);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(10, 10, 10, 10);
                // linearLayout1.setLayoutParams(layoutParams);

                imageView.setLayoutParams(layoutParams);
                imageView.setImageBitmap(bitmaps.get(i));
                imageView.setTag(bitmaps.get(i));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);


                //Utils.log(" 2 " + String.valueOf(i), " IN ");

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

    }

    private void insertNotification() {

        if (utils.isConnectingToInternet()) {

            storageService.insertDocs(Config.collectionNotification, jsonObject,
                    new AsyncApp42ServiceApi.App42StorageServiceListener() {

                        @Override
                        public void onDocumentInserted(Storage response) {
                            try {
                                if (response.isResponseSuccess()) {
                                    sendPushToProvider();
                                } else {
                                    strAlert = getString(R.string.no_push_actiity_updated);
                                    goToActivityList(strAlert);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                goToActivityList(strAlert);
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
                            strAlert = getString(R.string.no_push_actiity_updated);
                            goToActivityList(strAlert);
                        }

                        @Override
                        public void onFindDocFailed(App42Exception ex) {
                        }

                        @Override
                        public void onUpdateDocFailed(App42Exception ex) {
                        }
                    });
        } else {
            strAlert = getString(R.string.no_push_actiity_updated);

            goToActivityList(strAlert);
        }
    }

    private void sendPushToProvider() {

        if (utils.isConnectingToInternet()) {

            PushNotificationService pushNotificationService = new PushNotificationService(FeatureActivity.this);

            pushNotificationService.sendPushToUser(strDependentMail, jsonObject.toString(),
                    new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {

                            strAlert = getString(R.string.activity_updated);

                            if (o == null)
                                strAlert = getString(R.string.no_push_actiity_updated);

                            goToActivityList(strAlert);
                        }

                        @Override
                        public void onException(Exception ex) {
                            strAlert = getString(R.string.no_push_actiity_updated);
                            goToActivityList(strAlert);
                        }
                    });
        } else {
            strAlert = getString(R.string.no_push_actiity_updated);

            goToActivityList(strAlert);
        }
    }

    private void goToActivityList(String strAlert) {

        loadingPanel.setVisibility(View.GONE);

        //utils.toast(2, 2, getString(R.string.milestone_updated));

        utils.toast(2, 2, strAlert);

        reloadMilestones();

        /*Intent intent = new Intent(FeatureActivity.this, DashboardActivity.class);
        Config.intSelectedMenu = Config.intDashboardScreen;
        startActivity(intent);*/
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (!bViewLoaded) {

            bViewLoaded = true;

            reloadMilestones();
        }
    }

    private void reloadMilestones() {

        final ActivityModel activityModel = act;

        try {
            // if (textViewLabel != null)
            //    textViewLabel.append(activityModel.getStrServiceName());

            final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.milestoneLayout);

            linearLayout.removeAllViews();

            Drawable drawable = null;

            for (final MilestoneModel milestoneModel : activityModel.getMilestoneModels()) {

                textViewName = new TextView(FeatureActivity.this);
                textViewName.setTextAppearance(this, R.style.MilestoneStyle);
                textViewName.setText(milestoneModel.getStrMilestoneName());
                textViewName.setTextColor(getResources().getColor(R.color.colorWhite));
                textViewName.setPadding(25, 45, 0, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    textViewName.setGravity(View.TEXT_ALIGNMENT_CENTER);
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30, 1);
                params.setMargins(10, 10, 10, 10);
                textViewName.setTag(milestoneModel);
                //textViewName.setId(milestoneModel.getiMilestoneId());

                if (milestoneModel.getStrMilestoneScheduledDate() != null && !milestoneModel.getStrMilestoneScheduledDate().equalsIgnoreCase("")) {

                    String strDate = milestoneModel.getStrMilestoneScheduledDate();

                    Calendar calendar = Calendar.getInstance();

                    Date date = null;
                    String strdateCopy;
                    Date milestoneDate = null;

                    try {
                        strdateCopy = Utils.readFormat.format(calendar.getTime());
                        date = Utils.readFormat.parse(strdateCopy);
                        milestoneDate = utils.convertStringToDate(strDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (date != null && milestoneDate != null) {

                        //Utils.log(String.valueOf(date + " ! " + milestoneDate), " NOW ");

                        if (milestoneDate.before(date) && !milestoneModel.getStrMilestoneStatus().equalsIgnoreCase("completed"))
                            milestoneModel.setStrMilestoneStatus("pending");
                        else {
                            if (milestoneDate.after(date) && !milestoneModel.getStrMilestoneStatus().equalsIgnoreCase("completed"))
                                milestoneModel.setStrMilestoneStatus("inprocess");
                        }
                    } else milestoneModel.setStrMilestoneStatus("opened");
                }

                final String strMilestoneStatus = milestoneModel.getStrMilestoneStatus();

                Drawable drawableBg = null;

                if (strMilestoneStatus.equalsIgnoreCase("completed")) {
                    drawable = getResources().getDrawable(R.mipmap.tick);
                    drawableBg = getResources().getDrawable(R.drawable.button_success);
                }

                if (strMilestoneStatus.equalsIgnoreCase("pending")) {
                    drawable = getResources().getDrawable(R.drawable.error);
                    drawableBg = getResources().getDrawable(R.drawable.button_error);
                }

                if (strMilestoneStatus.equalsIgnoreCase("opened")
                        || milestoneModel.getStrMilestoneStatus().equalsIgnoreCase("reopened")) {
                    drawable = getResources().getDrawable(R.mipmap.star_grey);
                    drawableBg = getResources().getDrawable(R.drawable.button_orange);
                }

                if (strMilestoneStatus.equalsIgnoreCase("inactive")) {
                    drawable = getResources().getDrawable(R.mipmap.star_grey);
                    drawableBg = getResources().getDrawable(R.drawable.button_inactive);
                }

                if (strMilestoneStatus.equalsIgnoreCase("inprocess")) {
                    drawable = getResources().getDrawable(R.mipmap.star_gold);
                    drawableBg = getResources().getDrawable(R.drawable.button_orange);
                }

                if (drawable != null) {
                    textViewName.setCompoundDrawablesWithIntrinsicBounds(drawable, null,null , null);
                    textViewName.setCompoundDrawablePadding(30);
                }


                if (drawableBg != null) {
                    textViewName.setBackgroundDrawable(drawableBg);
                }

                textViewName.setLayoutParams(params);

                if (linearLayout != null) {
                    linearLayout.addView(textViewName);
                }

                textViewName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final MilestoneModel milestoneModelObject = (MilestoneModel) v.getTag();

                        boolean bEnabled = true;

                        if (milestoneModelObject.getStrMilestoneStatus().equalsIgnoreCase("completed"))
                            bEnabled = false;

                            //int i = 0;

                            View view = getLayoutInflater().inflate(R.layout.dialog_view, null, false);

                            final LinearLayout layoutDialog = (LinearLayout) view.findViewById(R.id.linearLayoutDialog);

                            Button button = (Button) view.findViewById(R.id.dialogButtonOK);
                            Button buttonCancel = (Button) view.findViewById(R.id.buttonCancel);
                            Button buttonDone = (Button) view.findViewById(R.id.buttonDone);

                            button.setTag(milestoneModelObject.getiMilestoneId());
                            buttonDone.setTag(milestoneModelObject.getiMilestoneId());

                        if (!milestoneModelObject.getStrMilestoneDate().equalsIgnoreCase("")) {
                            button.setText(getString(R.string.update));
                            buttonDone.setVisibility(View.VISIBLE);
                        }

                        if (!bEnabled) {
                            button.setVisibility(View.GONE);
                            buttonDone.setVisibility(View.GONE);
                        }

                            TextView milestoneName = (TextView) view.findViewById(R.id.milestoneName);
                            milestoneName.setText(milestoneModelObject.getStrMilestoneName());



                            if (!milestoneModelObject.getStrMilestoneDate().equalsIgnoreCase("")
                                    && milestoneModelObject.getStrMilestoneScheduledDate() != null
                                    && !milestoneModelObject.getStrMilestoneScheduledDate().equalsIgnoreCase("")) {
                                button.setText(getString(R.string.reschedule));
                            }

                            for (FieldModel fieldModel : milestoneModelObject.getFieldModels()) {

                                final FieldModel finalFieldModel = fieldModel;

                                //i++;

                                LinearLayout linearLayout1 = new LinearLayout(context);
                                linearLayout1.setOrientation(LinearLayout.HORIZONTAL);

                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(10, 10, 10, 10);
                                linearLayout1.setLayoutParams(layoutParams);

                                TextView textView = new TextView(context);
                                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3));
                                textView.setText(fieldModel.getStrFieldLabel());

                                linearLayout1.addView(textView);

                                if (fieldModel.getStrFieldType().equalsIgnoreCase("text")
                                        || fieldModel.getStrFieldType().equalsIgnoreCase("number")
                                        || fieldModel.getStrFieldType().equalsIgnoreCase("datetime")
                                        || fieldModel.getStrFieldType().equalsIgnoreCase("date")
                                        || fieldModel.getStrFieldType().equalsIgnoreCase("time")) {

                                    final EditText editText = new EditText(context);

                                    editText.setId(fieldModel.getiFieldID());
                                    editText.setTag(R.id.one, fieldModel.isFieldRequired());
                                    editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
                                    editText.setText(fieldModel.getStrFieldData());

                                    if (fieldModel.isFieldView())
                                        editText.setEnabled(false);

                                    if (fieldModel.getStrFieldType().equalsIgnoreCase("text"))
                                        editText.setInputType(InputType.TYPE_CLASS_TEXT);

                                    if (fieldModel.getStrFieldType().equalsIgnoreCase("number"))
                                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                                    try {
                                        if (fieldModel.getStrFieldType().equalsIgnoreCase("datetime")
                                                || fieldModel.getStrFieldType().equalsIgnoreCase("date")
                                                || fieldModel.getStrFieldType().equalsIgnoreCase("time")) {

                                            editText.setCompoundDrawables(getResources().getDrawable(R.drawable.date), null, null, null);

                                            //editText.setInputType(InputType.TYPE_CLASS_DATETIME);

                                            final SlideDateTimeListener listener = new SlideDateTimeListener() {

                                                @Override
                                                public void onDateTimeSet(Date date) {
                                                    // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+
                                                    // date.getTime();
                                                    // Do something with the date. This Date object contains
                                                    // the date and time that the user has selected.

                                                    String strDate = "";

                                                    if (finalFieldModel.getStrFieldType().equalsIgnoreCase("datetime"))
                                                        strDate = Utils.writeFormat.format(date);

                                                    if (finalFieldModel.getStrFieldType().equalsIgnoreCase("time"))
                                                        strDate = Utils.writeFormatTime.format(date);

                                                    if (finalFieldModel.getStrFieldType().equalsIgnoreCase("date"))
                                                        strDate = Utils.writeFormatDate.format(date);

                                                    editText.setTag(R.id.two, Utils.readFormat.format(date));
                                                    editText.setText(strDate);
                                                }

                                                @Override
                                                public void onDateTimeCancel() {
                                                    // Overriding onDateTimeCancel() is optional.
                                                }

                                            };

                                            editText.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    new SlideDateTimePicker.Builder(getSupportFragmentManager())
                                                            .setListener(listener)
                                                            .setInitialDate(new Date())
                                                            .build()
                                                            .show();
                                                }
                                            });
                                        }

                                        editText.setEnabled(bEnabled);

                                        linearLayout1.addView(editText);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (fieldModel.getStrFieldType().equalsIgnoreCase("radio")
                                        || fieldModel.getStrFieldType().equalsIgnoreCase("dropdown")) {

                                    final Spinner spinner = new Spinner(context);

                                    spinner.setId(fieldModel.getiFieldID());
                                    spinner.setTag(fieldModel.isFieldRequired());
                                    spinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
                                    //spinner.setText(fieldModel.getStrFieldData());

                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_item, fieldModel.getStrFieldValues());

                                    spinner.setAdapter(adapter);

                                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                            try {

                                                if (finalFieldModel.isChild()) {

                                                    for (int i = 0; i < finalFieldModel.getiChildfieldID().length; i++) {

                                                        if (finalFieldModel.getStrChildType()[i].equalsIgnoreCase("text")) {

                                                            EditText editTextChild = (EditText) layoutDialog.findViewById(finalFieldModel.getiChildfieldID()[i]);

                                                            if (editTextChild != null) {

                                                                String strValue = spinner.getSelectedItem().toString();

                                                                if (finalFieldModel.getStrChildCondition()[i].equalsIgnoreCase("equals")) {

                                                                    if (strValue.equalsIgnoreCase(finalFieldModel.getStrChildValue()[i])) {
                                                                        //editText.setVisibility(View.VISIBLE);
                                                                        editTextChild.setEnabled(true);
                                                                        break;
                                                                    } else {
                                                                        editTextChild.setEnabled(false);
                                                                    }
                                                                } else
                                                                    editTextChild.setEnabled(false);
                                                            }
                                                        }
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                        }
                                    });

                                    if (fieldModel.getStrFieldData() != null && !fieldModel.getStrFieldData().equalsIgnoreCase("")) {

                                        int iSelected = adapter.getPosition(fieldModel.getStrFieldData());
                                        spinner.setSelection(iSelected);
                                    }

                                    spinner.setEnabled(bEnabled);

                                    linearLayout1.addView(spinner);
                                }

                                if (fieldModel.getStrFieldType().equalsIgnoreCase("array")) {

                                    final LinearLayout linearLayoutParent = new LinearLayout(context);
                                    linearLayoutParent.setOrientation(LinearLayout.VERTICAL);
                                    linearLayoutParent.setTag(R.id.linearparent);

                                    LinearLayout.LayoutParams layoutParentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    layoutParentParams.setMargins(10, 10, 10, 10);
                                    linearLayoutParent.setLayoutParams(layoutParentParams);

                                    //
                                    try {

                                        JSONObject jsonObjectMedicines = new JSONObject(fieldModel.getStrArrayData());

                                        JSONArray jsonArrayMedicines = jsonObjectMedicines.getJSONArray("array_data");

                                        for (int i = 0; i < jsonArrayMedicines.length(); i++) {

                                            JSONObject jsonObjectMedicine =
                                                    jsonArrayMedicines.getJSONObject(i);

                                            final LinearLayout linearLayoutArrayExist = new LinearLayout(context);
                                            linearLayoutArrayExist.setOrientation(LinearLayout.HORIZONTAL);
                                            //linearLayoutArrayExist.setId(R.id.actionBarNotification);

                                            LinearLayout.LayoutParams layoutArrayExistParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            layoutArrayExistParams.setMargins(10, 10, 10, 10);
                                            linearLayoutArrayExist.setLayoutParams(layoutArrayExistParams);


                                            EditText editMedicineName = new EditText(context);
                                            editMedicineName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
                                            editMedicineName.setHint(getString(R.string.medicine_name));
                                            editMedicineName.setTag("medicine_name");
                                            editMedicineName.setInputType(InputType.TYPE_CLASS_TEXT);
                                            editMedicineName.setText(jsonObjectMedicine.getString("medicine_name"));
                                            editMedicineName.setEnabled(bEnabled);
                                            linearLayoutArrayExist.addView(editMedicineName);

                                            EditText editMedicineQty = new EditText(context);
                                            editMedicineQty.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                                            editMedicineQty.setHint(getString(R.string.qunatity));
                                            editMedicineQty.setTag("medicine_qty");
                                            editMedicineQty.setInputType(InputType.TYPE_CLASS_NUMBER);
                                            editMedicineQty.setText(String.valueOf(jsonObjectMedicine.getInt("medicine_qty")));
                                            editMedicineQty.setEnabled(bEnabled);
                                            linearLayoutArrayExist.addView(editMedicineQty);


                                            //
                                            Button buttonDel = new Button(context);
                                            buttonDel.setText("X");
                                            buttonDel.setTextColor(Color.BLACK);
                                            buttonDel.setEnabled(bEnabled);
                                            buttonDel.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle));
                                            buttonDel.setLayoutParams(new LinearLayout.LayoutParams(64, 64, 0));
                                            buttonDel.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    try {
                                                        LinearLayout linearLayout = (LinearLayout) v.getParent();
                                                        linearLayout.removeAllViews();

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            linearLayoutArrayExist.addView(buttonDel);

                                            linearLayoutParent.addView(linearLayoutArrayExist);
                                            ///
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //

                                    final LinearLayout linearLayoutArray = new LinearLayout(context);
                                    linearLayoutArray.setOrientation(LinearLayout.HORIZONTAL);

                                    LinearLayout.LayoutParams layoutArrayParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    layoutArrayParams.setMargins(10, 10, 10, 10);
                                    linearLayoutArray.setLayoutParams(layoutArrayParams);


                                    for (int j = 0; j < finalFieldModel.getiArrayCount(); j++) {

                                        EditText editTextArray = new EditText(context);

                                        if (j == 0) {
                                            editTextArray.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
                                            editTextArray.setHint(getString(R.string.medicine_name));
                                            editTextArray.setTag("medicine_name");
                                            editTextArray.setInputType(InputType.TYPE_CLASS_TEXT);
                                        }

                                        if (j == 1) {
                                            editTextArray.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                                            editTextArray.setHint(getString(R.string.qunatity));
                                            editTextArray.setTag("medicine_qty");
                                            editTextArray.setInputType(InputType.TYPE_CLASS_NUMBER);
                                        }

                                           /* if (fieldModel.getStrFieldType().equalsIgnoreCase("text"))


                                            if (fieldModel.getStrFieldType().equalsIgnoreCase("number"))*/

                                        editTextArray.setEnabled(bEnabled);

                                        linearLayoutArray.addView(editTextArray);
                                    }

                                    final boolean finalBEnabled = bEnabled;

                                    Button buttonAdd = new Button(context);
                                    buttonAdd.setBackgroundDrawable(getResources().getDrawable(R.drawable.add_icon));
                                    buttonAdd.setLayoutParams(new LinearLayout.LayoutParams(64, 64, 0));
                                    buttonAdd.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            try {

                                                final LinearLayout linearLayoutArrayInner = new LinearLayout(context);
                                                linearLayoutArrayInner.setOrientation(LinearLayout.HORIZONTAL);

                                                LinearLayout.LayoutParams layoutArrayInnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                layoutArrayInnerParams.setMargins(10, 10, 10, 10);
                                                linearLayoutArrayInner.setLayoutParams(layoutArrayInnerParams);

                                                for (int j = 0; j < finalFieldModel.getiArrayCount(); j++) {

                                                    EditText editTextArray = new EditText(context);

                                                    if (j == 0) {
                                                        editTextArray.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
                                                        editTextArray.setHint(getString(R.string.medicine_name));
                                                        editTextArray.setTag("medicine_name");
                                                        editTextArray.setInputType(InputType.TYPE_CLASS_TEXT);
                                                    }

                                                    if (j == 1) {
                                                        editTextArray.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                                                        editTextArray.setHint(getString(R.string.qunatity));
                                                        editTextArray.setTag("medicine_qty");
                                                        editTextArray.setInputType(InputType.TYPE_CLASS_NUMBER);
                                                    }

                                                        /*if (finalFieldModel.getStrFieldType().equalsIgnoreCase("text"))


                                                        if (finalFieldModel.getStrFieldType().equalsIgnoreCase("number"))*/

                                                    editTextArray.setEnabled(finalBEnabled);
                                                    linearLayoutArrayInner.addView(editTextArray);
                                                }

                                                //
                                                Button buttonDel = new Button(context);
                                                buttonDel.setText("X");
                                                buttonDel.setTextColor(Color.BLACK);
                                                buttonDel.setEnabled(finalBEnabled);
                                                buttonDel.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle));
                                                buttonDel.setLayoutParams(new LinearLayout.LayoutParams(64, 64, 0));
                                                buttonDel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        try {
                                                            LinearLayout linearLayout = (LinearLayout) v.getParent();
                                                            linearLayout.removeAllViews();

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });

                                                linearLayoutArrayInner.addView(buttonDel);
                                                ///

                                                linearLayoutParent.addView(linearLayoutArrayInner);
                                                //
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    linearLayoutArray.addView(buttonAdd);

                                    linearLayoutParent.addView(linearLayoutArray);

                                    linearLayout1.addView(linearLayoutParent);
                                }
                                layoutDialog.addView(linearLayout1);
                            }

                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int id = (int) v.getTag();
                                    LinearLayout linearLayout = (LinearLayout) layoutDialog.findViewWithTag(R.id.linearparent);
                                    traverseEditTexts(layoutDialog, id, linearLayout, 1);

                                }
                            });

                            buttonDone.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int id = (int) v.getTag();
                                    LinearLayout linearLayout = (LinearLayout) layoutDialog.findViewWithTag(R.id.linearparent);
                                    traverseEditTexts(layoutDialog, id, linearLayout, 2);
                                }
                            });

                            buttonCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setView(view);
                            dialog = builder.create();

                            dialog.show();

                        //}
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            addImages();

            loadingPanel.setVisibility(View.GONE);
        }
    }

    private class BackgroundThread extends Thread {
        @Override
        public void run() {

            try {
                for(int i=0;i<imagePaths.size();i++) {
                    Calendar calendar = new GregorianCalendar();
                    String strTime = String.valueOf(calendar.getTimeInMillis());
                    String strFileName = strTime + ".jpeg";
                    File galleryFile = utils.createFileInternalImage(strFileName);
                    strImageName = galleryFile.getAbsolutePath();
                    Date date = new Date();

                    ImageModel imageModel = new ImageModel(strFileName, "", strTime, utils.convertDateToString(date), galleryFile.getAbsolutePath());
                    arrayListImageModel.add(imageModel);

                    utils.copyFile(new File(imagePaths.get(i)), galleryFile);
                    //
                    utils.compressImageFromPath(strImageName, Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                    bitmaps.add(utils.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight));

                 IMAGE_COUNT++;
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (IOException |OutOfMemoryError e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class BackgroundThreadCamera extends Thread {
        @Override
        public void run() {
            try {
                if (strImageName != null && !strImageName.equalsIgnoreCase("")) {

                    utils.compressImageFromPath(strImageName, Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);
                    Date date = new Date();
                    ImageModel imageModel = new ImageModel(strName, "", strName, utils.convertDateToString(date), strImageName);
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

            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
            backgroundThreadHandler.sendEmptyMessage(0);
        }
    }

    private class BackgroundThreadImages extends Thread {
        @Override
        public void run() {
            try {
                //

                JSONObject jsonObjectImages = new JSONObject();

                for (ImageModel imageModel : act.getImageModels()) {
                    if (imageModel.getStrImageName() != null && !imageModel.getStrImageName().equalsIgnoreCase("")) {
                        bitmaps.add(utils.getBitmapFromFile(utils.getInternalFileImages(imageModel.getStrImageName()).getAbsolutePath(), Config.intWidth, Config.intHeight));
                        IMAGE_COUNT++;

                        jsonObjectImages.put("image_name", imageModel.getStrImageName());
                        jsonObjectImages.put("image_url", imageModel.getStrImageUrl());
                        jsonObjectImages.put("image_description", imageModel.getStrImageDesc());
                        jsonObjectImages.put("image_taken", imageModel.getStrImageTime());

                        jsonArrayImagesAdded.put(jsonObjectImages);
                    }
                }
                //
            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
            backgroundThreadHandler.sendEmptyMessage(0);
        }
    }
}