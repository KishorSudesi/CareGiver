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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FeatureActivity extends AppCompatActivity implements Serializable{

    public static int IMAGE_COUNT = 0;
    private static String strImageName = "";
    //private static Bitmap bitmap = null;
    private static StorageService storageService;
    private static ArrayList<ImageModel> arrayListImageModel = new ArrayList<>();
    private static Handler backgroundThreadHandler;
    private static ProgressDialog mProgress = null;
    private static String strAlert;
    private static JSONObject jsonObject;
    private static ActivityModel act;
    private static LinearLayout layout;
    private static String strName, strPushMessage, strDependentMail;
    private static ArrayList<String> imagePaths = new ArrayList<>();
    private static ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private static Dialog dialog;
    private final Context context = this;
    private Utils utils;
    private ProgressDialog progressDialog;
    private Point p;
    private JSONArray jsonArrayImagesAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_features);
        //listFeatures = new ArrayList<>();
        ImageView attach = (ImageView) findViewById(R.id.imgAttachHeaderTaskDetail);
        ImageView imgLogoHeaderTaskDetail = (ImageView) findViewById(R.id.imgLogoHeaderTaskDetail);
        Button done = (Button) findViewById(R.id.buttonVegetibleDone);
        ImageView back = (ImageView) findViewById(R.id.imgBackHeaderTaskDetail);
        LinearLayout linearLayoutAttach = (LinearLayout) findViewById(R.id.linearLayout);

        layout = (LinearLayout) findViewById(R.id.linear);

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

            if (dependentName != null) {
                dependentName.setText(name);
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

        mProgress = new ProgressDialog(FeatureActivity.this);
        progressDialog = new ProgressDialog(FeatureActivity.this);
        storageService = new StorageService(FeatureActivity.this);

        if (done != null) {
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IMAGE_COUNT = 0;

                    if (arrayListImageModel.size() > 0)
                        uploadImage();
                    else
                        utils.toast(2, 2, "Select a Image");
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
        //TextView textViewLabel = (TextView) findViewById(R.id.textViewLabel);

        final ActivityModel activityModel = act;

        try {

            // if (textViewLabel != null)
            //    textViewLabel.append(activityModel.getStrServiceName());

            final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.milestoneLayout);

            for (final MilestoneModel milestoneModel : activityModel.getMilestoneModels()) {

                TextView textViewName = new TextView(FeatureActivity.this);
                textViewName.setTextAppearance(this, R.style.MilestoneStyle);
                textViewName.setText(milestoneModel.getStrMilestoneName());
                textViewName.setTextColor(getResources().getColor(R.color.colorWhite));
                textViewName.setPadding(10, 10, 10, 10);
                textViewName.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_success));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30, 1);
                params.setMargins(10, 10, 10, 10);
                textViewName.setTag(milestoneModel);

                textViewName.setLayoutParams(params);

                if (linearLayout != null) {
                    linearLayout.addView(textViewName);
                }

                textViewName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MilestoneModel milestoneModel = (MilestoneModel) v.getTag();

                        int i = 0;

                        View view = getLayoutInflater().inflate(R.layout.dialog_view, null, false);

                        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.linearLayout);

                        Button button = (Button) view.findViewById(R.id.dialogButtonOK);
                        button.setTag(milestoneModel.getiMilestoneId());

                        for (FieldModel fieldModel : milestoneModel.getFieldModels()) {

                            final FieldModel finalFieldModel = fieldModel;

                            i++;

                            LinearLayout linearLayout1 = new LinearLayout(context);
                            linearLayout1.setOrientation(LinearLayout.HORIZONTAL);

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(10, 10, 10, 10);
                            linearLayout1.setLayoutParams(layoutParams);

                            TextView textView = new TextView(context);
                            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
                            textView.setText(fieldModel.getStrFieldLabel());

                            linearLayout1.addView(textView);

                            if (fieldModel.getStrFieldType().equalsIgnoreCase("text")
                                    || fieldModel.getStrFieldType().equalsIgnoreCase("number")
                                    || fieldModel.getStrFieldType().equalsIgnoreCase("datetime")
                                    || fieldModel.getStrFieldType().equalsIgnoreCase("date")
                                    || fieldModel.getStrFieldType().equalsIgnoreCase("time")) {

                                final EditText editText = new EditText(context);

                                editText.setId(fieldModel.getiFieldID());
                                editText.setTag(fieldModel.isFieldRequired());
                                editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                                editText.setText(fieldModel.getStrFieldData());

                                if (fieldModel.isFieldView())
                                    editText.setEnabled(false);

                                if (fieldModel.getStrFieldType().equalsIgnoreCase("text"))
                                    editText.setInputType(InputType.TYPE_CLASS_TEXT);

                                if (fieldModel.getStrFieldType().equalsIgnoreCase("number"))
                                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                                //todo check type
                                try {
                                    if (fieldModel.getStrFieldType().equalsIgnoreCase("datetime")
                                            || fieldModel.getStrFieldType().equalsIgnoreCase("date")
                                            || fieldModel.getStrFieldType().equalsIgnoreCase("time")) {

                                        editText.setInputType(1);

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
                                           /*
                                            _strDate = Utils.readFormat.format(date);
                                            dateAnd.setText(strDate);*/
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
                                spinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
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

                                                        EditText editText = (EditText) layout.findViewById(finalFieldModel.getiChildfieldID()[i]);

                                                        String strValue = editText.getText().toString().trim();

                                                        if (finalFieldModel.getStrChildCondition()[i].equalsIgnoreCase("equals")) {

                                                            if (strValue.equalsIgnoreCase(finalFieldModel.getStrChildValue()[i])) {
                                                                //editText.setVisibility(View.VISIBLE);
                                                                editText.setEnabled(true);
                                                            } else {
                                                                editText.setEnabled(false);
                                                            }
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


                                linearLayout1.addView(spinner);
                            }

                            if (fieldModel.getStrFieldType().equalsIgnoreCase("array")) {

                                final LinearLayout linearLayoutArray = new LinearLayout(context);
                                linearLayoutArray.setOrientation(LinearLayout.HORIZONTAL);

                                LinearLayout.LayoutParams layoutArrayParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutArrayParams.setMargins(10, 10, 10, 10);
                                linearLayoutArray.setLayoutParams(layoutArrayParams);


                                for (int j = 0; j < finalFieldModel.getiArrayCount(); j++) {

                                    final EditText editTextArray = new EditText(context);

                                    //editTextArray.setId(fieldModel.getiFieldID());
                                    //editTextArray.setTag(fieldModel.isFieldRequired());
                                    editTextArray.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                                    //editTextArray.setText(fieldModel.);

                                    if (fieldModel.getStrFieldType().equalsIgnoreCase("text"))
                                        editTextArray.setInputType(InputType.TYPE_CLASS_TEXT);

                                    linearLayoutArray.addView(editTextArray);

                                    Button buttonAdd = new Button(context);
                                    buttonAdd.setBackgroundDrawable(getResources().getDrawable(R.drawable.add_icon));
                                    buttonAdd.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            try {
                                                //
                                                for (int j = 0; j < finalFieldModel.getiArrayCount(); j++) {

                                                    final EditText editTextArray = new EditText(context);

                                                    //editTextArray.setId(fieldModel.getiFieldID());
                                                    //editTextArray.setTag(fieldModel.isFieldRequired());
                                                    editTextArray.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                                                    //editTextArray.setText(fieldModel.);

                                                    if (finalFieldModel.getStrFieldType().equalsIgnoreCase("text"))
                                                        editTextArray.setInputType(1);

                                                    linearLayoutArray.addView(editTextArray);
                                                }
                                                //
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });

                                }


                                linearLayout1.addView(linearLayoutArray);
                            }

                            layout.addView(linearLayout1);

                        }

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int id = (int) v.getTag();
                                traverseEditTexts(layout, id);
                            }
                        });

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(milestoneModel.getStrMilestoneName());
                        builder.setView(view);
                        dialog = builder.create();

                        dialog.show();

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        for (ImageModel imageModel : act.getImageModels()) {
            if (imageModel.getStrImageName() != null && !imageModel.getStrImageName().equalsIgnoreCase("")) {
                bitmaps.add(utils.getBitmapFromFile(imageModel.getStrImageName(), Config.intWidth, Config.intHeight));
                IMAGE_COUNT++;
            }
        }
        //
    }

    private void goBack() {
        arrayListImageModel.clear();
        bitmaps.clear();
        IMAGE_COUNT = 0;
        Intent intent = new Intent(FeatureActivity.this, DashboardActivity.class);
        Config.intSelectedMenu = Config.intDashboardScreen;
        startActivity(intent);
        finish();
    }

    private void traverseEditTexts(ViewGroup v, int iMileStoneId) {

        boolean b = true;

        try {

            for (MilestoneModel milestoneModel : act.getMilestoneModels()) {

                if (milestoneModel.getiMilestoneId() == iMileStoneId) {

                    for (FieldModel fieldModel : milestoneModel.getFieldModels()) {

                        //text
                        if (fieldModel.getStrFieldType().equalsIgnoreCase("text")
                                || fieldModel.getStrFieldType().equalsIgnoreCase("number")
                                || fieldModel.getStrFieldType().equalsIgnoreCase("datetime")
                                || fieldModel.getStrFieldType().equalsIgnoreCase("date")
                                || fieldModel.getStrFieldType().equalsIgnoreCase("time")) {

                            EditText editText = (EditText) v.findViewById(fieldModel.getiFieldID());

                            boolean b1 = (Boolean) editText.getTag();
                            String data = editText.getText().toString().trim();

                            if (editText.isEnabled()) {
                                if (b1 && !data.equalsIgnoreCase("")) {
                                    fieldModel.setStrFieldData(data);
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

                        }
                        //
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (b) {

            utils.toast(1, 1, getString(R.string.milestone_updated));

            dialog.dismiss();

            uploadJson();

            for (MilestoneModel milestoneModel : act.getMilestoneModels()) {

                for (FieldModel fieldModel : milestoneModel.getFieldModels()) {

                    Utils.log(fieldModel.getStrFieldLabel() + " ~ " + fieldModel.getStrFieldData(), " DATA ");

                }
            }
        }

    }

    private void updateMileStones(JSONObject jsonToUpdate) {

        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (utils.isConnectingToInternet()) {

            storageService.updateDocs(jsonToUpdate,
                    act.getStrActivityID(),
                    Config.collectionActivity, new App42CallBack() {
                        @Override
                        public void onSuccess(Object o) {
                            insertNotification();
                        }

                        @Override
                        public void onException(Exception e) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            utils.toast(2, 2, getString(R.string.warning_internet));
                        }
                    });
        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
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

    private void uploadImage() {

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

                                        if (arrayListImageModel.size() <= 0) {
                                            goToActivityList(getString(R.string.image_upload));
                                        } else {
                                            uploadImage();
                                        }
                                        //

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
                                //Utils.log(e.toString(), "response");
                                utils.toast(2, 2, getString(R.string.error));
                                //uploadImage();
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
                            jsonObjectField.put("values", utils.intToJsonArray(fieldModel.getiChildfieldID()));
                    }

                    jsonArrayFields.put(jsonObjectField);

                    jsonObjectMilestone.put("fields", jsonArrayFields);
                }
                jsonArrayMilestones.put(jsonObjectMilestone);
            }
            ////////////////////

            jsonObjectMileStone.put("milestones", jsonArrayMilestones);
            jsonObjectMileStone.put("status", "inprocess");

            Date date = new Date();
            String strDate = utils.convertDateToString(date);

            strPushMessage = Config.providerModel.getStrName() + getString(R.string.has_updated) +
                    act.getStrActivityName() + getString(R.string.on) + strDate;

            jsonObject = new JSONObject();

            try {

                jsonObject.put("created_by", Config.providerModel.getStrProviderId());
                jsonObject.put("time", strDate);
                jsonObject.put("user_type", "dependent");
                jsonObject.put("user_id", act.getStrDependentID());
                jsonObject.put("created_by_type", "provider");
                jsonObject.put("notification_message", strPushMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
        //super.onBackPressed();
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


                Utils.log(" 2 " + String.valueOf(i), " IN ");

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

            pushNotificationService.sendPushToUser(strDependentMail, strPushMessage,
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

        if (progressDialog.isShowing())
            progressDialog.dismiss();

        //utils.toast(2, 2, getString(R.string.milestone_updated));

        utils.toast(2, 2, strAlert);

        /*Intent intent = new Intent(FeatureActivity.this, DashboardActivity.class);
        Config.intSelectedMenu = Config.intDashboardScreen;
        startActivity(intent);*/
    }

    private class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            addImages();

            mProgress.dismiss();
        }
    }

    private class BackgroundThread extends Thread {
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
                    Date date = new Date();

                    ImageModel imageModel = new ImageModel(strImageName, "", galleryFile.getName(), utils.convertDateToString(date));
                    arrayListImageModel.add(imageModel);

                    utils.copyFile(new File(imagePaths.get(i)), galleryFile);

                    //
                    utils.compressImageFromPath(strImageName, Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);
                    //

                    //bitmap = utils.getBitmapFromFile(strImageName, Config.intWidth, Config.intHeight);

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

            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
            backgroundThreadHandler.sendEmptyMessage(0);
        }
    }
}