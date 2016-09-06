package com.hdfc.caregiver;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ayz4sci.androidfactory.permissionhelper.PermissionHelper;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.hdfc.app42service.App42GCMService;
import com.hdfc.app42service.PushNotificationService;
import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UploadService;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.AppUtils;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Utils;
import com.hdfc.libs.simpleTooltip.SimpleTooltip;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.FieldModel;
import com.hdfc.models.FileModel;
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
import java.util.Collections;
import java.util.Date;

import pl.tajchert.nammu.PermissionCallback;

public class MilestoneActivity extends AppCompatActivity {

    private static ArrayList<FileModel> fileModels = new ArrayList<>();
    private static ArrayList<String> imagePaths = new ArrayList<>();
    private static ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private static String strActivityStatus = "inprocess";
    private static String strActivityDoneDate = "";
    private static String strAlert;
    private static JSONObject jsonObject;
    private static ActivityModel act;
    private static String strCustomerEmail;
    private static String strName1;
    private static String strImageName1 = "";
    private static StorageService storageService;
    private static Handler backgroundThreadHandler, backgroundThreadHandlerLoad;
    private static boolean bEnabled, mImageChanged;
    private static int bWhichScreen;//isAllowed
    private final Context context = this;
    private int iValidFlag = 0;
    private RelativeLayout loadingPanel;
    private int mImageCount, mImageUploadCount;
    private LinearLayout layout;
    private SimpleTooltip simpleTooltip;
    private Utils utils;
    private PermissionHelper permissionHelper;
    private String strCloseUser, strCloseStatus, strDependentName;
    private Date selectedDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milestone);

        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        Bundle b = getIntent().getExtras();

        permissionHelper = PermissionHelper.getInstance(this);

        act = (ActivityModel) b.getSerializable("Act");
        bWhichScreen = b.getInt("WHICH_SCREEN", 3);
        strCustomerEmail = b.getString("CUSTOMER_EMAIL", "");
        strDependentName = b.getString("DEPENDENT_NAME", "");

        strCloseStatus = "";
        strCloseUser = "";

        bitmaps.clear();

        mImageUploadCount = 0;
        mImageCount = 0;
        mImageChanged = false;

        final MilestoneModel milestoneModelObject = (MilestoneModel) b.getSerializable("Milestone");

        utils = new Utils(MilestoneActivity.this);
        storageService = new StorageService(MilestoneActivity.this);

        if (milestoneModelObject != null) {
            bEnabled = !milestoneModelObject.getStrMilestoneStatus().equalsIgnoreCase("completed");
        }

        layout = (LinearLayout) findViewById(R.id.dialogLinear);

        final LinearLayout layoutDialog = (LinearLayout) findViewById(R.id.linearLayoutDialog);

        final Button button = (Button) findViewById(R.id.dialogButtonOK);
        Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
        Button buttonDone = (Button) findViewById(R.id.buttonDone);
        Button buttonAttach = (Button) findViewById(R.id.dialogButtonAttach);

        simpleTooltip = null;

        if (milestoneModelObject != null) {

            if (button != null) {
                button.setTag(milestoneModelObject.getiMilestoneId());
            }
            if (buttonDone != null) {
                buttonDone.setTag(milestoneModelObject.getiMilestoneId());
            }

            if (!milestoneModelObject.getStrMilestoneDate().equalsIgnoreCase("")) {
                if (button != null) {
                    button.setText(getString(R.string.update));
                }
                if (buttonDone != null) {
                    buttonDone.setVisibility(View.VISIBLE);
                    simpleTooltip = new SimpleTooltip.Builder(MilestoneActivity.this)
                            .anchorView(buttonDone)
                            .text(getString(R.string.completed_task))
                            .gravity(Gravity.BOTTOM)
                            .build();
                    if (!milestoneModelObject.getStrMilestoneStatus().
                            equalsIgnoreCase("completed")) {
                        simpleTooltip.show();
                    }
                }
            }

            if (milestoneModelObject.getiMilestoneId() == act.getMilestoneModels().size()) {
                if (button != null) {
                    button.setVisibility(View.GONE);
                }
                if (buttonDone != null) {
                    buttonDone.setVisibility(View.VISIBLE);
                    simpleTooltip = new SimpleTooltip.Builder(MilestoneActivity.this)
                            .anchorView(buttonDone)
                            .text(getString(R.string.completed_task))
                            .gravity(Gravity.BOTTOM)
                            .build();
                    if (!milestoneModelObject.getStrMilestoneStatus().
                            equalsIgnoreCase("completed")) {
                        simpleTooltip.show();
                    }
                }
            }


            if (!bEnabled) {
                if (button != null) {
                    button.setVisibility(View.GONE);
                }
                if (buttonDone != null) {
                    buttonDone.setVisibility(View.GONE);
                    if (simpleTooltip != null && simpleTooltip.isShowing())
                        simpleTooltip.dismiss();
                }
                if (buttonAttach != null) {
                    buttonAttach.setVisibility(View.GONE);
                }
            }

            TextView milestoneName = (TextView) findViewById(R.id.milestoneName);
            if (milestoneName != null) {
                milestoneName.setText(milestoneModelObject.getStrMilestoneName());

            }

            fileModels = milestoneModelObject.getFileModels();

            if (!milestoneModelObject.getStrMilestoneDate().equalsIgnoreCase("")
                    && milestoneModelObject.getStrMilestoneScheduledDate() != null
                    && !milestoneModelObject.getStrMilestoneScheduledDate().equalsIgnoreCase("")) {
                if (button != null) {
                    button.setText(getString(R.string.reschedule));
                }
            }

            for (final FieldModel fieldModel : milestoneModelObject.getFieldModels()) {

                LinearLayout linearLayout1 = new LinearLayout(MilestoneActivity.this);
                linearLayout1.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10, 10, 10, 10);
                linearLayout1.setLayoutParams(layoutParams);

                TextView textView = new TextView(MilestoneActivity.this);
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 3));
                textView.setText(fieldModel.getStrFieldLabel());
                textView.setTextAppearance(this, R.style.LabelStyle);

                linearLayout1.addView(textView);

                if (fieldModel.getStrFieldType().equalsIgnoreCase("text")
                        || fieldModel.getStrFieldType().equalsIgnoreCase("number")
                        || fieldModel.getStrFieldType().equalsIgnoreCase("datetime")
                        || fieldModel.getStrFieldType().equalsIgnoreCase("date")
                        || fieldModel.getStrFieldType().equalsIgnoreCase("time")) {

                    final EditText editText = new EditText(MilestoneActivity.this);

                    editText.setId(fieldModel.getiFieldID());
                    editText.setTag(R.id.one, fieldModel.isFieldRequired());
                    editText.setTextAppearance(this, R.style.EditTextStyle);
                    editText.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT, 2));
                    editText.setText(fieldModel.getStrFieldData());

                    if (fieldModel.isFieldView())
                        editText.setEnabled(false);

                    if (fieldModel.getStrFieldType().equalsIgnoreCase("text"))
                        editText.setInputType(InputType.TYPE_CLASS_TEXT);

                    if (fieldModel.getStrFieldType().equalsIgnoreCase("number")) {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        Utils.setEditTextMaxLength(editText, 10);
                    }

                    try {
                        if (fieldModel.getStrFieldType().equalsIgnoreCase("datetime")
                                || fieldModel.getStrFieldType().equalsIgnoreCase("date")) {

                            editText.setCompoundDrawablesWithIntrinsicBounds(getResources().
                                            getDrawable(R.drawable.calendar_date_picker),
                                    null, null, null);

                            editText.setFocusableInTouchMode(false);

                            final SlideDateTimeListener listener = new SlideDateTimeListener() {

                                @Override
                                public void onDateTimeSet(Date date) {
                                    // Do something with the date. This Date object contains
                                    // the date and time that the user has selected.
                                    selectedDate = date;

                                    String strDate = "";

                                    if (fieldModel.getStrFieldType().
                                            equalsIgnoreCase("datetime"))
                                        strDate = Utils.writeFormat.format(date);

                                  /*  if (fieldModel.getStrFieldType().equalsIgnoreCase("time"))
                                        strDate = Utils.writeFormatTime.format(date);*/

                                    if (fieldModel.getStrFieldType().equalsIgnoreCase("date"))
                                        strDate = Utils.writeFormatDate.format(date);

                                    editText.setTag(R.id.two, Utils.readFormat.format(date));
                                    editText.setText(strDate);
                                }

                                @Override
                                public void onDateTimeCancel() {
                                }

                            };

                            if (act.getMilestoneModels().size() == milestoneModelObject.
                                    getiMilestoneId()) {

                                Calendar cal = Calendar.getInstance(); // creates calendar
                                cal.setTime(new Date()); // sets calendar time/date
                                cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour

                                Date date = cal.getTime();
                                String date2 = Utils.writeFormat.format(date);
                                editText.setText(date2);
                                //bEnabled=false;
                                editText.setEnabled(false);
                                editText.setFocusable(false);
                                editText.setClickable(false);

                            } else {

                                editText.setEnabled(true);
                                //editText.setFocusable(true);
                                //editText.setClickable(true);

                                if (milestoneModelObject.getStrMilestoneScheduledDate() != null
                                        && !milestoneModelObject.getStrMilestoneScheduledDate().
                                        equalsIgnoreCase("")) {
                                    editText.setTag(R.id.two,
                                            milestoneModelObject.getStrMilestoneScheduledDate());
                                }

                                editText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Date setDate = new Date();

                                        // Unparseable date: "2016-07-02T07:12:20.725Z" (at offset 4)
                                        if (fieldModel.getStrFieldType().equalsIgnoreCase("datetime")) {
                                            if (fieldModel.getStrFieldData() != null
                                                    && !fieldModel.getStrFieldData().
                                                    equalsIgnoreCase("")) {
                                                try {
                                                    setDate = Utils.writeFormat.parse(fieldModel.
                                                            getStrFieldData());
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        if (fieldModel.getStrFieldType().equalsIgnoreCase("date")) {
                                            if (fieldModel.getStrFieldData() != null
                                                    && !fieldModel.getStrFieldData().
                                                    equalsIgnoreCase("")) {
                                                try {
                                                    setDate = Utils.writeFormatDate.parse(
                                                            fieldModel.getStrFieldData());
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                       /* if (fieldModel.getStrFieldType().equalsIgnoreCase("time")) {
                                            if (fieldModel.getStrFieldData() != null
                                                    && !fieldModel.getStrFieldData().
                                                    equalsIgnoreCase("")) {
                                                try {
                                                    setDate = Utils.writeFormatTime.parse(fieldModel.getStrFieldData());
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }*/

                                        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                                                .setListener(listener)
                                                .setInitialDate(selectedDate)
                                                .build()
                                                .show();
                                    }
                                });
                            }
                        }

                        //for time
                        if (fieldModel.getStrFieldType().equalsIgnoreCase("time")) {

                            editText.setCompoundDrawablesWithIntrinsicBounds(getResources().
                                            getDrawable(R.drawable.calendar_date_picker),
                                    null, null, null);

                            editText.setFocusableInTouchMode(false);

                            editText.setEnabled(true);

                            editText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Calendar mcurrentTime = Calendar.getInstance();
                                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                                    int minute = mcurrentTime.get(Calendar.MINUTE);

                                    if (fieldModel.getStrFieldData() != null
                                            && !fieldModel.getStrFieldData().
                                            equalsIgnoreCase("")) {
                                        try {
                                            String[] setDate = fieldModel.getStrFieldData().split(":");

                                            if (setDate.length > 0) {
                                                hour = Integer.parseInt(setDate[0]);
                                                minute = Integer.parseInt(setDate[1]);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    TimePickerDialog mTimePicker;
                                    mTimePicker = new TimePickerDialog(MilestoneActivity.this,
                                            new TimePickerDialog.OnTimeSetListener() {
                                                @Override
                                                public void onTimeSet(TimePicker timePicker,
                                                                      int selectedHour,
                                                                      int selectedMinute) {
                                                    try {

                                                        String strHour = String.valueOf(
                                                                selectedHour);
                                                        String strMinute = String.valueOf(
                                                                selectedMinute);

                                                        if (selectedHour <= 9)
                                                            strHour = "0" + strHour;

                                                        if (selectedMinute <= 9)
                                                            strMinute = "0" + strMinute;


                                                        editText.setText(strHour + ":" + strMinute);
                                                        /*editText.setTag(R.id.two, Utils.writeFormatTime.
                                                                format(selectedHour + ":" + selectedMinute));*/
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }, hour, minute, true);//Yes 24 hour time
                                    mTimePicker.setTitle(getString(R.string.select_time));
                                    mTimePicker.show();

                                    //
                                }
                            });
                        }

                        editText.setEnabled(bEnabled);
                       /* editText.setFocusable(bEnabled);
                        editText.setClickable(bEnabled);*/

                        linearLayout1.addView(editText);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (fieldModel.getStrFieldType().equalsIgnoreCase("radio")
                        || fieldModel.getStrFieldType().equalsIgnoreCase("dropdown")) {
                    final Spinner spinner = new Spinner(MilestoneActivity.this);

                    spinner.setId(fieldModel.getiFieldID());
                    spinner.setTag(fieldModel.isFieldRequired());

                    spinner.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT, 2));

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MilestoneActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            fieldModel.getStrFieldValues());

                    spinner.setAdapter(adapter);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                                   long id) {
                            try {

                                if (fieldModel.isChild()) {

                                    for (int i = 0; i < fieldModel.getiChildfieldID().length;
                                         i++) {

                                        if (fieldModel.getStrChildType()[i].
                                                equalsIgnoreCase("text")) {

                                            EditText editTextChild = (EditText) layoutDialog.
                                                    findViewById(fieldModel.
                                                            getiChildfieldID()[i]);

                                            if (editTextChild != null) {

                                                String strValue = spinner.getSelectedItem().
                                                        toString();

                                                if (fieldModel.getStrChildCondition()[i].
                                                        equalsIgnoreCase("equals")) {

                                                    if (strValue.equalsIgnoreCase(fieldModel.
                                                            getStrChildValue()[i])) {
                                                        //editText.setVisibility(View.VISIBLE);

                                                        if (!bEnabled)
                                                            editTextChild.setEnabled(false);
                                                        else
                                                            editTextChild.setEnabled(true);

                                                        break;
                                                    } else {
                                                        editTextChild.setEnabled(false);
                                                    }
                                                } else
                                                    editTextChild.setEnabled(false);
                                            }
                                        } else if (fieldModel.getStrChildType()[i].
                                                equalsIgnoreCase("dropdown")) {

                                            if (fieldModel.getStrChildCondition()[i].
                                                    equalsIgnoreCase("equals")) {
                                                String strValue = spinner.getSelectedItem().
                                                        toString();

                                                Spinner spinnerChild = null;
                                                if (layoutDialog != null) {
                                                    spinnerChild = (Spinner) layoutDialog.
                                                            findViewById(fieldModel.
                                                                    getiChildfieldID()[i]);
                                                }

                                                if (spinnerChild != null) {
                                                    if (strValue.equalsIgnoreCase("UnSuccessFul")) {
                                                        spinnerChild.setVisibility(View.VISIBLE);
                                                        spinnerChild.setSelection(0);
                                                        strCloseStatus = "UnSuccessFul";

                                                    } else if (strValue.
                                                            equalsIgnoreCase("SuccessFul")) {
                                                        spinnerChild.setVisibility(View.GONE);
                                                        strCloseStatus = "SuccessFul";
                                                    }
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

                    if (fieldModel.getStrFieldData() != null && !fieldModel.getStrFieldData().
                            equalsIgnoreCase("")) {

                        int iSelected = adapter.getPosition(fieldModel.getStrFieldData());
                        spinner.setSelection(iSelected);
                    }

                    spinner.setEnabled(bEnabled);
                    linearLayout1.addView(spinner);
                }


                if (fieldModel.getStrFieldType().equalsIgnoreCase("array")) {

                    final LinearLayout linearLayoutParent = new LinearLayout(MilestoneActivity.this);
                    linearLayoutParent.setOrientation(LinearLayout.VERTICAL);
                    linearLayoutParent.setTag(R.id.linearparent);

                    LinearLayout.LayoutParams layoutParentParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParentParams.setMargins(10, 10, 10, 10);
                    linearLayoutParent.setLayoutParams(layoutParentParams);

                    try {

                        JSONObject jsonObjectMedicines = new JSONObject(fieldModel.getStrArrayData());

                        JSONArray jsonArrayMedicines = jsonObjectMedicines.getJSONArray("array_data");

                        for (int i = 0; i < jsonArrayMedicines.length(); i++) {

                            JSONObject jsonObjectMedicine =
                                    jsonArrayMedicines.getJSONObject(i);

                            final LinearLayout linearLayoutArrayExist = new LinearLayout(
                                    MilestoneActivity.this);
                            linearLayoutArrayExist.setOrientation(LinearLayout.HORIZONTAL);

                            LinearLayout.LayoutParams layoutArrayExistParams = new LinearLayout.
                                    LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutArrayExistParams.setMargins(10, 10, 10, 10);
                            linearLayoutArrayExist.setLayoutParams(layoutArrayExistParams);


                            EditText editMedicineName = new EditText(MilestoneActivity.this);
                            editMedicineName.setLayoutParams(new LinearLayout.LayoutParams(0,
                                    ViewGroup.LayoutParams.WRAP_CONTENT, 2));
                            editMedicineName.setHint(getString(R.string.medicine_name));
                            editMedicineName.setTag("medicine_name");
                            editMedicineName.setInputType(InputType.TYPE_CLASS_TEXT);
                            editMedicineName.setText(jsonObjectMedicine.getString("medicine_name"));
                            editMedicineName.setEnabled(bEnabled);
                            editMedicineName.setTextAppearance(this, R.style.EditTextStyle);
                            linearLayoutArrayExist.addView(editMedicineName);

                            EditText editMedicineQty = new EditText(MilestoneActivity.this);
                            editMedicineQty.setLayoutParams(new LinearLayout.LayoutParams(0,
                                    ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                            editMedicineQty.setHint(getString(R.string.qunatity));
                            editMedicineQty.setTextAppearance(this, R.style.EditTextStyle);
                            editMedicineQty.setTag("medicine_qty");
                            editMedicineQty.setInputType(InputType.TYPE_CLASS_NUMBER);
                            Utils.setEditTextMaxLength(editMedicineQty, 4);
                            editMedicineQty.setText(String.valueOf(jsonObjectMedicine.
                                    getInt("medicine_qty")));
                            editMedicineQty.setEnabled(bEnabled);
                            linearLayoutArrayExist.addView(editMedicineQty);


                            Button buttonDel = new Button(MilestoneActivity.this);
                            buttonDel.setText("X");
                            buttonDel.setTextColor(Color.BLACK);
                            buttonDel.setPadding(5, 5, 5, 5);
                            buttonDel.setEnabled(bEnabled);
                            buttonDel.setTextAppearance(this, R.style.ButtonStyle);
                            buttonDel.setBackgroundDrawable(getResources().getDrawable(R.drawable.
                                    circle));
                            buttonDel.setLayoutParams(new LinearLayout.LayoutParams(64, 64, 0));
                            buttonDel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    try {
                                        if (bEnabled) {
                                            LinearLayout linearLayout = (LinearLayout) v.getParent();
                                            linearLayout.removeAllViews();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            linearLayoutArrayExist.addView(buttonDel);

                            linearLayoutParent.addView(linearLayoutArrayExist);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    final LinearLayout linearLayoutArray = new LinearLayout(MilestoneActivity.this);
                    linearLayoutArray.setOrientation(LinearLayout.HORIZONTAL);

                    LinearLayout.LayoutParams layoutArrayParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutArrayParams.setMargins(10, 10, 10, 10);
                    linearLayoutArray.setLayoutParams(layoutArrayParams);


                    for (int j = 0; j < fieldModel.getiArrayCount(); j++) {

                        EditText editTextArray = new EditText(MilestoneActivity.this);

                        editTextArray.setTextAppearance(this, R.style.EditTextStyle);

                        if (j == 0) {
                            editTextArray.setLayoutParams(new LinearLayout.LayoutParams(0,
                                    ViewGroup.LayoutParams.WRAP_CONTENT, 2));
                            editTextArray.setHint(getString(R.string.medicine_name));
                            editTextArray.setTag("medicine_name");
                            editTextArray.setInputType(InputType.TYPE_CLASS_TEXT);
                        }

                        if (j == 1) {
                            editTextArray.setLayoutParams(new LinearLayout.LayoutParams(0,
                                    ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                            editTextArray.setHint(getString(R.string.qunatity));
                            editTextArray.setTag("medicine_qty");
                            Utils.setEditTextMaxLength(editTextArray, 4);
                            editTextArray.setInputType(InputType.TYPE_CLASS_NUMBER);
                        }

                        editTextArray.setEnabled(bEnabled);

                        linearLayoutArray.addView(editTextArray);
                    }

                    final boolean finalBEnabled = bEnabled;

                    Button buttonAdd = new Button(MilestoneActivity.this);
                    buttonAdd.setBackgroundDrawable(getResources().getDrawable(R.drawable.add_icon));
                    buttonAdd.setLayoutParams(new LinearLayout.LayoutParams(64, 64, 0));
                    buttonAdd.setTextAppearance(this, R.style.ButtonStyle);
                    buttonAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {

                                if (bEnabled) {

                                    final LinearLayout linearLayoutArrayInner = new LinearLayout(
                                            MilestoneActivity.this);
                                    linearLayoutArrayInner.setOrientation(LinearLayout.HORIZONTAL);

                                    LinearLayout.LayoutParams layoutArrayInnerParams =
                                            new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                                    layoutArrayInnerParams.setMargins(10, 10, 10, 10);
                                    linearLayoutArrayInner.setLayoutParams(layoutArrayInnerParams);

                                    for (int j = 0; j < fieldModel.getiArrayCount(); j++) {

                                        EditText editTextArray = new EditText(MilestoneActivity.this);

                                        editTextArray.setTextAppearance(MilestoneActivity.this,
                                                R.style.EditTextStyle);

                                        if (j == 0) {
                                            editTextArray.setLayoutParams(new LinearLayout.
                                                    LayoutParams(0,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT, 2));
                                            editTextArray.setHint(getString(R.string.medicine_name));
                                            editTextArray.setTag("medicine_name");
                                            editTextArray.setInputType(InputType.TYPE_CLASS_TEXT);
                                        }

                                        if (j == 1) {
                                            editTextArray.setLayoutParams(new LinearLayout.
                                                    LayoutParams(0,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                                            editTextArray.setHint(getString(R.string.qunatity));
                                            editTextArray.setTag("medicine_qty");
                                            Utils.setEditTextMaxLength(editTextArray, 4);
                                            editTextArray.setInputType(InputType.TYPE_CLASS_NUMBER);
                                        }

                                        editTextArray.setEnabled(finalBEnabled);
                                        linearLayoutArrayInner.addView(editTextArray);
                                    }

                                    Button buttonDel = new Button(MilestoneActivity.this);
                                    buttonDel.setText("X");
                                    buttonDel.setTextColor(Color.BLACK);
                                    buttonDel.setEnabled(finalBEnabled);
                                    buttonDel.setTextAppearance(MilestoneActivity.this,
                                            R.style.ButtonStyle);
                                    buttonDel.setPadding(5, 5, 5, 5);
                                    buttonDel.setBackgroundDrawable(getResources().
                                            getDrawable(R.drawable.circle));
                                    buttonDel.setLayoutParams(new LinearLayout.
                                            LayoutParams(64, 64, 0));
                                    buttonDel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            try {
                                                LinearLayout linearLayout = (LinearLayout) v.
                                                        getParent();
                                                linearLayout.removeAllViews();

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    linearLayoutArrayInner.addView(buttonDel);

                                    linearLayoutParent.addView(linearLayoutArrayInner);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    linearLayoutArray.addView(buttonAdd);

                    linearLayoutParent.addView(linearLayoutArray);

                    linearLayout1.addView(linearLayoutParent);
                }
                if (layoutDialog != null) {
                    layoutDialog.addView(linearLayout1);
                }
            }

            if (button != null) {

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        iValidFlag = 0;

                        if (simpleTooltip != null && simpleTooltip.isShowing())
                            simpleTooltip.dismiss();
                        int id = (int) v.getTag();
                        LinearLayout linearLayout = null;
                        if (layoutDialog != null) {
                            linearLayout = (LinearLayout) layoutDialog.findViewWithTag(
                                    R.id.linearparent);
                        }

                        if (button.getText().toString().equalsIgnoreCase(getString(R.string.update))
                                || button.getText().toString().equalsIgnoreCase
                                (getString(R.string.reschedule))) {
                            iValidFlag = 1;
                        }

                        traverseEditTexts(layoutDialog, id, linearLayout, 1);
                    }
                });
            }

            if (buttonAttach != null) {
                buttonAttach.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (simpleTooltip != null && simpleTooltip.isShowing())
                            simpleTooltip.dismiss();

                        Calendar calendar = Calendar.getInstance();

                        strName1 = String.valueOf(calendar.getTimeInMillis()) + ".jpeg";

                        strImageName1 = strName1;

                        if (!isFinishing()) {
                            permissionHelper.verifyPermission(
                                    new String[]{getString(R.string.access_storage)},
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    new PermissionCallback() {
                                        @Override
                                        public void permissionGranted() {
                                            //action to perform when permission granteed
                                            //isAllowed = true;

                                            utils.selectImage(strImageName1, null,
                                                    MilestoneActivity.this, false);
                                        }

                                        @Override
                                        public void permissionRefused() {
                                            //action to perform when permission refused
                                            //isAllowed = false;
                                        }
                                    }
                            );
                        }

                    }
                });
            }

            if (buttonDone != null) {

                buttonDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (simpleTooltip != null && simpleTooltip.isShowing())
                            simpleTooltip.dismiss();
                        int id = (int) v.getTag();
                        LinearLayout linearLayout = null;
                        if (layoutDialog != null) {
                            linearLayout = (LinearLayout) layoutDialog.findViewWithTag(
                                    R.id.linearparent);
                        }
                        iValidFlag = 1;
                        traverseEditTexts(layoutDialog, id, linearLayout, 2);
                    }
                });
            }

            if (buttonCancel != null) {
                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (simpleTooltip != null && simpleTooltip.isShowing())
                            simpleTooltip.dismiss();
                        goBack();
                    }
                });
            }

            loadingPanel.setVisibility(View.VISIBLE);

            if (Utils.isConnectingToInternet(MilestoneActivity.this)) {
                backgroundThreadHandlerLoad = new BackgroundThreadHandlerLoad();
                Thread backgroundThreadImagesLoad = new BackgroundThreadImagesLoad();
                backgroundThreadImagesLoad.start();
            } else {
                backgroundThreadHandler = new BackgroundThreadHandler();
                Thread backgroundThreadImages = new BackgroundThreadImages();
                backgroundThreadImages.start();
            }
        }
    }

    private void goBack() {
        if (mImageCount > 0 || mImageChanged) {
            AlertDialog.Builder alertbox =
                    new AlertDialog.Builder(MilestoneActivity.this);
            alertbox.setTitle(getString(R.string.delete_file));
            alertbox.setMessage(getString(R.string.confirm_unsaved_files));
            alertbox.setPositiveButton(getString(R.string.yes), new DialogInterface.
                    OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    goBackIntent();
                }
            });
            alertbox.setNegativeButton(getString(R.string.no), new DialogInterface.
                    OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            alertbox.show();
        } else {
            goBackIntent();
        }
    }

    private void goBackIntent() {
        Bundle args = new Bundle();
        Intent intent = new Intent(MilestoneActivity.this, FeatureActivity.class);
        args.putSerializable("ACTIVITY", act);
        args.putInt("WHICH_SCREEN", bWhichScreen);
        intent.putExtras(args);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void traverseEditTexts(ViewGroup v, int iMileStoneId, ViewGroup viewGroup, int iFlag) {

        boolean b = true, bClose = true;

        //todo place variables from loop here

        String mStrTaskMess1;

        try {

            Calendar calendar = Calendar.getInstance();

            String strScheduledDate;

            int iIndex = 0;
            int iClose = 0;
            int mFieldIndex = 0;

            Date date = calendar.getTime();

            boolean isDateChanged;

            for (MilestoneModel milestoneModel : act.getMilestoneModels()) {

                iIndex++;

                if (milestoneModel.getiMilestoneId() == iMileStoneId) {


                    strScheduledDate = "";
                    mStrTaskMess1 = "";

                    for (FieldModel fieldModel : milestoneModel.getFieldModels()) {

                        //text
                        mFieldIndex++;

                        if (fieldModel.getStrFieldType().equalsIgnoreCase("text")
                                || fieldModel.getStrFieldType().equalsIgnoreCase("number")
                                || fieldModel.getStrFieldType().equalsIgnoreCase("datetime")
                                || fieldModel.getStrFieldType().equalsIgnoreCase("date")
                                || fieldModel.getStrFieldType().equalsIgnoreCase("time")) {

                            EditText editText = (EditText) v.findViewById(fieldModel.getiFieldID());

                            editText.setError(null);

                            boolean b1 = (Boolean) editText.getTag(R.id.one);
                            String data = editText.getText().toString().trim();

                            if (editText.isEnabled()) {
                                if (b1 && !data.equalsIgnoreCase("")) {

                                    if (fieldModel.getStrFieldType().equalsIgnoreCase("datetime")
                                            || fieldModel.getStrFieldType().equalsIgnoreCase("date")
                                            ) {

                                    /*    ||
                                        fieldModel.getStrFieldType().equalsIgnoreCase("time")*/

                                        boolean bFuture = true;

                                        Date dateNow = null;
                                        String strdateCopy;
                                        Date enteredDate = null;

                                        try {

                                            if (fieldModel.getStrFieldType().
                                                    equalsIgnoreCase("datetime")) {
                                                strdateCopy = Utils.writeFormat.format(calendar.
                                                        getTime());
                                                dateNow = Utils.writeFormat.parse(strdateCopy);
                                                enteredDate = Utils.writeFormat.parse(data);
                                            }

                                            if (fieldModel.getStrFieldType().
                                                    equalsIgnoreCase("date")) {
                                                strdateCopy = Utils.writeFormatDate.format(calendar.
                                                        getTime());
                                                dateNow = Utils.writeFormatDate.parse(strdateCopy);
                                                enteredDate = Utils.writeFormatDate.parse(data);
                                            }

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        if (dateNow != null && enteredDate != null) {

                                            //Utils.log(String.valueOf(date + " ! " + enteredDate), " NOW ");

                                            if (enteredDate.compareTo(dateNow) < 0) {
                                                bFuture = false;
                                            }
                                        }

                                        String strDate = (String) editText.getTag(R.id.two);

                                        if (iValidFlag == 1) {

                                            isDateChanged = milestoneModel.
                                                    getStrMilestoneScheduledDate()
                                                    != null && !milestoneModel.
                                                    getStrMilestoneScheduledDate().
                                                    equalsIgnoreCase(strDate);

                                            if (!isDateChanged)
                                                bFuture = true;
                                        }


                                        /////////////////////////////

                                        if (iFlag == 2 && !bFuture) {
                                            if (dateNow.compareTo(enteredDate) < 0) {
                                                bClose = false;
                                                Utils.log(String.valueOf(dateNow + " ! "
                                                        + enteredDate), " NOW ");
                                            }
                                        }


                                        if (bFuture) {

                                            fieldModel.setStrFieldData(data);

                                            if ((milestoneModel.isReschedule()
                                                    || !milestoneModel.isReschedule())
                                                    && milestoneModel.getStrMilestoneScheduledDate()
                                                    != null
                                                    && (!milestoneModel.
                                                    getStrMilestoneScheduledDate().
                                                    equalsIgnoreCase("")
                                                    || milestoneModel.getStrMilestoneScheduledDate()
                                                    .equalsIgnoreCase(""))
                                                    && fieldModel.getStrFieldType().equalsIgnoreCase
                                                    ("datetime")
                                                    ) {

                                                if (milestoneModel.
                                                        getStrMilestoneScheduledDate() != null
                                                        && !milestoneModel.
                                                        getStrMilestoneScheduledDate().
                                                        equalsIgnoreCase(""))
                                                    milestoneModel.setReschedule(true);

                                                milestoneModel.setStrMilestoneScheduledDate(strDate);

                                                strScheduledDate = strDate;
                                            }

                                            if (iIndex == act.getMilestoneModels().size()
                                                    && iFlag == 2) {
                                                strActivityDoneDate = strDate;
                                            }

                                        } else {
                                            editText.setError(
                                                    context.getString(R.string.invalid_date));
                                            b = false;
                                        }
                                    } else {
                                        fieldModel.setStrFieldData(data);
                                    }

                                } else {
                                    editText.setError(context.getString(
                                            R.string.error_field_required));
                                    b = false;
                                }
                            }
                        }

                        //radio or dropdown
                        if (fieldModel.getStrFieldType().equalsIgnoreCase("radio")
                                || fieldModel.getStrFieldType().equalsIgnoreCase("dropdown")) {

                            Spinner spinner = (Spinner) v.findViewById(fieldModel.getiFieldID());

                            boolean b1 = (Boolean) spinner.getTag();

                            String data = fieldModel.getStrFieldValues()[spinner.
                                    getSelectedItemPosition()];

                            if (b1 && !data.equalsIgnoreCase("")) {
                                fieldModel.setStrFieldData(data);

                                if (iIndex == act.getMilestoneModels().size() && iFlag == 2) {
                                    if (fieldModel.getStrFieldLabel().equalsIgnoreCase("Reason")) {
                                        strCloseUser = data;
                                    }
                                }

                            } else {
                                utils.toast(2, 2, getString(R.string.error_field_required));
                                spinner.setBackgroundDrawable(getResources().getDrawable(
                                        R.drawable.edit_text));
                                b = false;
                            }
                        }
                        //

                        //array
                        if (fieldModel.getStrFieldType().equalsIgnoreCase("array")) {

                            ArrayList<String> strMedicineNames = utils.getEditTextValueByTag(
                                    viewGroup, "medicine_name");
                            ArrayList<String> strMedicineQty = utils.getEditTextValueByTag(
                                    viewGroup, "medicine_qty");

                            int j = 0;

                            if (strMedicineNames.size() > 0) {

                                JSONObject jsonObject = new JSONObject();

                                JSONArray jsonArray = new JSONArray();

                                for (int i = 0; i < strMedicineNames.size(); i++) {

                                    if (!strMedicineNames.get(i).equalsIgnoreCase("")
                                            && !strMedicineQty.get(i).equalsIgnoreCase("")) {
                                        j++;
                                        JSONObject jsonObjectMedicine = new JSONObject();

                                        jsonObjectMedicine.put("medicine_name", strMedicineNames
                                                .get(i));
                                        jsonObjectMedicine.put("medicine_qty",
                                                Integer.parseInt(strMedicineQty.get(i)));

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

                        if (milestoneModel.getiMilestoneId() == 1) {

                            if (act.getmServiceNo() == 101) {
                                //medical test
                                if (fieldModel.getStrFieldLabel() != null
                                        && !fieldModel.getStrFieldLabel().equalsIgnoreCase("")
                                        && fieldModel.getStrFieldData() != null
                                        && !fieldModel.getStrFieldData().equalsIgnoreCase("")
                                        && (fieldModel.getiFieldID() == 11
                                        || fieldModel.getiFieldID() == 14)) {

                                    if (fieldModel.getiFieldID() == 14) {
                                        mStrTaskMess1 += getString(R.string.on);
                                    }

                                    mStrTaskMess1 += fieldModel.getStrFieldLabel() + ":"
                                            + getString(R.string.space)
                                            + fieldModel.getStrFieldData()
                                            + getString(R.string.space);
                                }

                            } else if (act.getmServiceNo() == 203) {
                                //repairs and maintenance
                                if (fieldModel.getStrFieldLabel() != null
                                        && !fieldModel.getStrFieldLabel().equalsIgnoreCase("")
                                        && fieldModel.getStrFieldData() != null
                                        && !fieldModel.getStrFieldData().equalsIgnoreCase("")
                                        && (fieldModel.getiFieldID() == 12
                                        || fieldModel.getiFieldID() == 11
                                        || fieldModel.getiFieldID() == 13)) {

                                    if (fieldModel.getiFieldID() == 11) {
                                        mStrTaskMess1 += getString(R.string.for_text);
                                    }

                                    if (fieldModel.getiFieldID() == 13) {
                                        mStrTaskMess1 += getString(R.string.on);
                                    }

                                    if (fieldModel.getiFieldID() == 12) {
                                        mStrTaskMess1 += getString(R.string.with_text);
                                    }

                                    mStrTaskMess1 += fieldModel.getStrFieldLabel() + ":"
                                            + getString(R.string.space)
                                            + fieldModel.getStrFieldData()
                                            + getString(R.string.space);
                                }

                            } else if (act.getmServiceNo() == 103) {
                                //medical purchase
                                if (fieldModel.getStrFieldLabel() != null
                                        && !fieldModel.getStrFieldLabel().equalsIgnoreCase("")
                                        && fieldModel.getStrFieldData() != null
                                        && !fieldModel.getStrFieldData().equalsIgnoreCase("")
                                        && (fieldModel.getiFieldID() == 12
                                        || fieldModel.getiFieldID() == 13)) {

                                    if (fieldModel.getiFieldID() == 12) {
                                        mStrTaskMess1 += getString(R.string.with_text);
                                    }

                                    if (fieldModel.getiFieldID() == 13) {
                                        mStrTaskMess1 += getString(R.string.on);
                                    }

                                    mStrTaskMess1 += fieldModel.getStrFieldLabel() + ":"
                                            + getString(R.string.space)
                                            + fieldModel.getStrFieldData()
                                            + getString(R.string.space);
                                }

                            } else if (act.getmServiceNo() == 301) {
                                //Entertainment
                                if (fieldModel.getStrFieldLabel() != null
                                        && !fieldModel.getStrFieldLabel().equalsIgnoreCase("")
                                        && fieldModel.getStrFieldData() != null
                                        && !fieldModel.getStrFieldData().equalsIgnoreCase("")
                                        && (fieldModel.getiFieldID() == 11
                                        || fieldModel.getiFieldID() == 12)) {

                                    if (fieldModel.getiFieldID() == 12) {
                                        mStrTaskMess1 += getString(R.string.with_text);
                                    }

                                    if (fieldModel.getiFieldID() == 11) {
                                        mStrTaskMess1 += getString(R.string.on);
                                    }

                                    mStrTaskMess1 += fieldModel.getStrFieldLabel() + ":"
                                            + getString(R.string.space)
                                            + fieldModel.getStrFieldData()
                                            + getString(R.string.space);
                                }

                            } else if (act.getmServiceNo() == 301) {
                                //Entertainment
                                if (fieldModel.getStrFieldLabel() != null
                                        && !fieldModel.getStrFieldLabel().equalsIgnoreCase("")
                                        && fieldModel.getStrFieldData() != null
                                        && !fieldModel.getStrFieldData().equalsIgnoreCase("")
                                        && (fieldModel.getiFieldID() == 11
                                        || fieldModel.getiFieldID() == 12)) {

                                    if (fieldModel.getiFieldID() == 12) {
                                        mStrTaskMess1 += getString(R.string.with_text);
                                    }

                                    if (fieldModel.getiFieldID() == 11) {
                                        mStrTaskMess1 += getString(R.string.on);
                                    }

                                    mStrTaskMess1 += fieldModel.getStrFieldLabel() + ":"
                                            + getString(R.string.space)
                                            + fieldModel.getStrFieldData()
                                            + getString(R.string.space);
                                }

                            } else if (act.getmServiceNo() == 401) {
                                //social care
                                if (fieldModel.getStrFieldLabel() != null
                                        && !fieldModel.getStrFieldLabel().equalsIgnoreCase("")
                                        && fieldModel.getStrFieldData() != null
                                        && !fieldModel.getStrFieldData().equalsIgnoreCase("")
                                        && (fieldModel.getiFieldID() == 11
                                        || fieldModel.getiFieldID() == 12)) {

                                    if (fieldModel.getiFieldID() == 11) {
                                        mStrTaskMess1 += getString(R.string.on);
                                    }

                                    mStrTaskMess1 += fieldModel.getStrFieldLabel() + ":"
                                            + getString(R.string.space)
                                            + fieldModel.getStrFieldData()
                                            + getString(R.string.space);
                                }

                            } else if (act.getmServiceNo() == 402) {
                                //home visit
                                if (fieldModel.getStrFieldLabel() != null
                                        && !fieldModel.getStrFieldLabel().equalsIgnoreCase("")
                                        && fieldModel.getStrFieldData() != null
                                        && !fieldModel.getStrFieldData().equalsIgnoreCase("")
                                        && (fieldModel.getiFieldID() == 11
                                        || fieldModel.getiFieldID() == 12)) {

                                    if (fieldModel.getiFieldID() == 11) {
                                        mStrTaskMess1 += getString(R.string.on);
                                    }

                                    if (fieldModel.getiFieldID() == 12) {
                                        mStrTaskMess1 += "." + getString(R.string.space);
                                    }

                                    mStrTaskMess1 += fieldModel.getStrFieldLabel() + ":"
                                            + getString(R.string.space)
                                            + fieldModel.getStrFieldData()
                                            + getString(R.string.space);
                                }

                            } else if (act.getmServiceNo() == 102) {
                                //doctor visit
                                if (fieldModel.getStrFieldLabel() != null
                                        && !fieldModel.getStrFieldLabel().equalsIgnoreCase("")
                                        && fieldModel.getStrFieldData() != null
                                        && !fieldModel.getStrFieldData().equalsIgnoreCase("")
                                        && (fieldModel.getiFieldID() == 11
                                        || fieldModel.getiFieldID() == 12
                                        || fieldModel.getiFieldID() == 13)) {

                                    if (fieldModel.getiFieldID() == 13) {
                                        mStrTaskMess1 += getString(R.string.on);
                                    }

                                    mStrTaskMess1 += fieldModel.getStrFieldLabel() + ":"
                                            + getString(R.string.space)
                                            + fieldModel.getStrFieldData()
                                            + getString(R.string.space);
                                }

                            } else if (act.getmServiceNo() == 201) {
                                //maid service
                                if (fieldModel.getStrFieldLabel() != null
                                        && !fieldModel.getStrFieldLabel().equalsIgnoreCase("")
                                        && fieldModel.getStrFieldData() != null
                                        && !fieldModel.getStrFieldData().equalsIgnoreCase("")
                                        && (fieldModel.getiFieldID() == 11
                                        || fieldModel.getiFieldID() == 12
                                        || fieldModel.getiFieldID() == 14
                                        || fieldModel.getiFieldID() == 15)) {

                                    if (fieldModel.getiFieldID() == 12) {
                                        mStrTaskMess1 += getString(R.string.with_text);
                                    }

                                    mStrTaskMess1 += fieldModel.getStrFieldLabel() + ":"
                                            + getString(R.string.space)
                                            + fieldModel.getStrFieldData()
                                            + getString(R.string.space);
                                }

                            } else if (act.getmServiceNo() == 202) {
                                //grocery purchase
                                if (fieldModel.getStrFieldLabel() != null
                                        && !fieldModel.getStrFieldLabel().equalsIgnoreCase("")
                                        && fieldModel.getStrFieldData() != null
                                        && !fieldModel.getStrFieldData().equalsIgnoreCase("")
                                        && (fieldModel.getiFieldID() == 12
                                        || fieldModel.getiFieldID() == 13)) {

                                    if (fieldModel.getiFieldID() == 12) {
                                        mStrTaskMess1 += getString(R.string.with_text);
                                    }

                                    if (fieldModel.getiFieldID() == 13) {
                                        mStrTaskMess1 += getString(R.string.on);
                                    }

                                    mStrTaskMess1 += fieldModel.getStrFieldLabel() + ":"
                                            + getString(R.string.space)
                                            + fieldModel.getStrFieldData()
                                            + getString(R.string.space);
                                }

                            } else if (act.getmServiceNo() == 502) {
                                //non-medical emergency care
                                if (fieldModel.getStrFieldLabel() != null
                                        && !fieldModel.getStrFieldLabel().equalsIgnoreCase("")
                                        && fieldModel.getStrFieldData() != null
                                        && !fieldModel.getStrFieldData().equalsIgnoreCase("")
                                        && (fieldModel.getiFieldID() == 12)) {

                                    mStrTaskMess1 += fieldModel.getStrFieldLabel() + ":"
                                            + getString(R.string.space)
                                            + fieldModel.getStrFieldData()
                                            + getString(R.string.space);
                                }

                            } else {

                                if (fieldModel.getStrFieldLabel() != null
                                        && !fieldModel.getStrFieldLabel().equalsIgnoreCase("")) {

                                    if (fieldModel.getStrFieldData() != null
                                            && !fieldModel.getStrFieldData().equalsIgnoreCase("")) {
                                        mStrTaskMess1 += fieldModel.getStrFieldLabel() + ":"
                                                + getString(R.string.space)
                                                + fieldModel.getStrFieldData();

                                        if (mFieldIndex < milestoneModel.getFieldModels().size()) {
                                            mStrTaskMess1 += "," + getString(R.string.space);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (b) {

                        if (iFlag == 2 && bClose) {
                            milestoneModel.setStrMilestoneStatus("completed");
                        }

                        //Utils.log("strActivityStatus", strActivityStatus);

                        if (iIndex == act.getMilestoneModels().size() && iFlag == 2 && bClose) {
                            strActivityStatus = "completed";
                        } else {
                            strActivityStatus = "inprocess";
                        }

                        String strPushMessage = createPushNotification(milestoneModel,
                                strScheduledDate, mStrTaskMess1);

                        jsonObject = new JSONObject();

                        try {

                            String strDateNow;
                            Date dateNow = calendar.getTime();
                            strDateNow = Utils.convertDateToString(dateNow);

                            //ios start
                            JSONObject jsonObjectTemp = new JSONObject();
                            jsonObjectTemp.put("alert", strPushMessage);
                            jsonObject.put("aps", jsonObjectTemp);
                            //ios end

                            jsonObject.put("created_by", Config.providerModel.getStrProviderId());
                            jsonObject.put("time", strDateNow);
                            jsonObject.put("user_type", "dependent");
                            jsonObject.put("user_id", act.getStrDependentID());
                            jsonObject.put("activity_id", act.getStrActivityID());
                            //todo add for customer
                            jsonObject.put("created_by_type", "provider");
                            jsonObject.put(App42GCMService.ExtraMessage, strPushMessage);
                            jsonObject.put("alert", strPushMessage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (b) {
                        milestoneModel.setStrMilestoneDate(Utils.convertDateToString(date));
                    }
                    //break;
                }

                if (!milestoneModel.getStrMilestoneStatus().equalsIgnoreCase("completed")
                        && iIndex < act.getMilestoneModels().size()) {
                    iClose++;
                }

                if (iFlag == 2 && strActivityStatus.equalsIgnoreCase("completed") && iClose > 0) {
                    b = false;
                    utils.toast(2, 2, getString(R.string.close_error));
                    strActivityStatus = "inprocess";
                    milestoneModel.setStrMilestoneStatus("inactive");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (iFlag == 2 && !bClose) {
            b = false;
            utils.toast(2, 2, getString(R.string.close_date_error));
        }

        if (b) {
            uploadImage(v, iMileStoneId, viewGroup, iFlag);
        }
    }

    private String createPushNotification(MilestoneModel milestoneModel, String strScheduledDate,
                                          String mStrTaskMess1) {

        String strPushMessage = "";

        if (strActivityStatus.equalsIgnoreCase("completed")) {

            if (strCloseStatus.equalsIgnoreCase("successful")) {
                strPushMessage = getString(R.string.notification_closure_body_1)
                        + act.getStrActivityName()
                        + getString(R.string.notification_closure_body_2);

            } else if (strCloseStatus.equalsIgnoreCase("unsuccessful")) {
                if (strCloseUser.equalsIgnoreCase("vendor")) {
                    strPushMessage = getString(R.string.notification_closure_body_3)
                            + act.getStrActivityName()
                            + getString(R.string.notification_closure_body_provider_1);
                } else {
                    strPushMessage = getString(R.string.notification_closure_body_3)
                            + act.getStrActivityName()
                            + getString(R.string.notification_closure_body_dependnet_1)
                            + strDependentName + ".";
                }
            } else {
                strPushMessage = getString(R.string.notification_closure_body_1)
                        + act.getStrActivityName()
                        + getString(R.string.notification_closure_body_2);
            }

        } else {

            if (milestoneModel.getiMilestoneId() != 1) {
                strPushMessage = getString(R.string.notification_body_1)
                        + milestoneModel.getStrMilestoneName()
                        + getString(R.string.notification_body_2_1);

                if (strScheduledDate != null && !strScheduledDate.equalsIgnoreCase("")) {
                    strPushMessage += getString(R.string.scheduled_to)
                            + Utils.formatDate(strScheduledDate);
                }

                strPushMessage += getString(R.string.for_text) + strDependentName
                        + getString(R.string.notification_body_3) + act.getStrActivityName()
                        + getString(R.string.notification_body_4)
                        + Config.providerModel.getStrName();
            } else {

                if (act.getmServiceNo() == 501) {
                    //Emergency Care
                    strPushMessage = getString(R.string.specific_notification_1)
                            + strDependentName + getString(R.string.specific_notification_1_1);

                } else if (act.getmServiceNo() == 101) {
                    //medical test
                    strPushMessage = getString(R.string.specific_notification_2)
                            + strDependentName + getString(R.string.space);

                    if (!mStrTaskMess1.equalsIgnoreCase("")) {
                        strPushMessage += mStrTaskMess1;
                    }

                } else if (act.getmServiceNo() == 203) {
                    //repairs and maintenance
                    strPushMessage = getString(R.string.specific_notification_3)
                            + strDependentName + getString(R.string.notification_placed);

                    if (!mStrTaskMess1.equalsIgnoreCase("")) {
                        strPushMessage += mStrTaskMess1;
                    }

                } else if (act.getmServiceNo() == 103) {
                    //medical purchase
                    strPushMessage = getString(R.string.specific_notification_4)
                            + strDependentName + getString(R.string.notification_placed);

                    if (!mStrTaskMess1.equalsIgnoreCase("")) {
                        strPushMessage += mStrTaskMess1;
                    }

                } else if (act.getmServiceNo() == 401) {
                    //medical purchase
                    strPushMessage = getString(R.string.specific_notification_5)
                            + strDependentName + getString(R.string.notification_scheduled);

                    if (!mStrTaskMess1.equalsIgnoreCase("")) {
                        strPushMessage += mStrTaskMess1;
                    }
                } else if (act.getmServiceNo() == 302) {
                    //shopping
                    strPushMessage = getString(R.string.specific_notification_6)
                            + strDependentName + getString(R.string.notification_placed);
                } else if (act.getmServiceNo() == 301) {
                    //entertainment
                    strPushMessage = getString(R.string.specific_notification_7)
                            + strDependentName + getString(R.string.notification_placed);

                    if (!mStrTaskMess1.equalsIgnoreCase("")) {
                        strPushMessage += mStrTaskMess1;
                    }

                } else if (act.getmServiceNo() == 402) {
                    //home visit
                    strPushMessage = getString(R.string.specific_notification_8)
                            + strDependentName + getString(R.string.notification_scheduled);

                    if (!mStrTaskMess1.equalsIgnoreCase("")) {
                        strPushMessage += mStrTaskMess1;
                    }

                    strPushMessage += getString(R.string.specific_notification_8_1)
                            + strDependentName + getString(R.string.notification_for_service);

                } else if (act.getmServiceNo() == 102) {
                    //doctor visit
                    strPushMessage = getString(R.string.specific_notification_9)
                            + strDependentName + getString(R.string.notification_scheduled);

                    if (!mStrTaskMess1.equalsIgnoreCase("")) {
                        strPushMessage += mStrTaskMess1;
                    }
                } else if (act.getmServiceNo() == 201) {
                    //maid service
                    strPushMessage = getString(R.string.specific_notification_10)
                            + strDependentName + getString(R.string.notification_placed);

                    if (!mStrTaskMess1.equalsIgnoreCase("")) {
                        strPushMessage += mStrTaskMess1;
                    }
                } else if (act.getmServiceNo() == 202) {
                    //grocery purchase
                    strPushMessage = getString(R.string.specific_notification_11)
                            + strDependentName + getString(R.string.notification_placed);

                    if (!mStrTaskMess1.equalsIgnoreCase("")) {
                        strPushMessage += mStrTaskMess1;
                    }
                } else if (act.getmServiceNo() == 502) {
                    //non-medical emergency care
                    strPushMessage = getString(R.string.specific_notification_12)
                            + strDependentName + getString(R.string.notification_placed);

                    if (!mStrTaskMess1.equalsIgnoreCase("")) {
                        strPushMessage += mStrTaskMess1;
                    }

                    strPushMessage += getString(R.string.specific_notification_12_1);

                } else {
                    //general
                    strPushMessage = getString(R.string.notification_body_1)
                            + milestoneModel.getStrMilestoneName()
                            + getString(R.string.notification_body_2);

                    strPushMessage += getString(R.string.for_text)
                            + strDependentName
                            + getString(R.string.notification_body_3)
                            + act.getStrActivityName()
                            + getString(R.string.notification_body_4)
                            + Config.providerModel.getStrName() + "."
                            + getString(R.string.notification_body_5)
                            + mStrTaskMess1;

                    //
                }

                //for first tasks (appointment etc...)
               /* strPushMessage += getString(R.string.notification_body_1)
                        + milestoneModel.getStrMilestoneName()
                        + getString(R.string.notification_body_2);*/

               /* strPushMessage += getString(R.string.for_text) + strDependentName
                        + getString(R.string.notification_body_3)
                        + act.getStrActivityName()
                        + getString(R.string.notification_body_4)
                        + Config.providerModel.getStrName() + "."
                        + getString(R.string.notification_body_5)
                        + mStrTaskMess1;*/
                //

            }
        }

        return strPushMessage;
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
                jsonObjectMilestone.put("scheduled_date", milestoneModel.
                        getStrMilestoneScheduledDate());

                JSONArray jsonArrayFiles = new JSONArray();

                for (FileModel fileModel : milestoneModel.getFileModels()) {

                    JSONObject jsonObjectFile = new JSONObject();

                    jsonObjectFile.put("file_name", fileModel.getStrFileName());
                    jsonObjectFile.put("file_url", fileModel.getStrFileUrl());
                    jsonObjectFile.put("file_type", fileModel.getStrFileType());
                    jsonObjectFile.put("file_desc", fileModel.getStrFileDescription());
                    jsonObjectFile.put("file_path", fileModel.getStrFilePath());
                    jsonObjectFile.put("file_time", fileModel.getStrFileUploadTime());

                    jsonArrayFiles.put(jsonObjectFile);
                }

                jsonObjectMilestone.put("files", jsonArrayFiles);

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

                    if (fieldModel.getStrFieldValues() != null && fieldModel.getStrFieldValues().
                            length > 0)
                        jsonObjectField.put("values", Utils.stringToJsonArray(fieldModel.
                                getStrFieldValues()));

                    if (fieldModel.isChild()) {

                        jsonObjectField.put("child", fieldModel.isChild());

                        if (fieldModel.getStrChildType() != null && fieldModel.getStrChildType().
                                length > 0)
                            jsonObjectField.put("child_type", Utils.stringToJsonArray(fieldModel.
                                    getStrChildType()));

                        if (fieldModel.getStrChildValue() != null && fieldModel.getStrChildValue().
                                length > 0)
                            jsonObjectField.put("child_value", Utils.stringToJsonArray(fieldModel.
                                    getStrChildValue()));

                        if (fieldModel.getStrChildCondition() != null && fieldModel.
                                getStrChildCondition().length > 0)
                            jsonObjectField.put("child_condition", Utils.stringToJsonArray(
                                    fieldModel.getStrChildCondition()));

                        if (fieldModel.getiChildfieldID() != null && fieldModel.getiChildfieldID().
                                length > 0)
                            jsonObjectField.put("child_field", Utils.intToJsonArray(fieldModel.
                                    getiChildfieldID()));
                    }

                    //
                    if (fieldModel.getiArrayCount() > 0) {
                        jsonObjectField.put("array_fields", fieldModel.getiArrayCount());
                        jsonObjectField.put("array_type", Utils.stringToJsonArray(fieldModel.
                                getStrArrayType()));
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
            jsonObjectMileStone.put("activity_done_date", strActivityDoneDate);

            if (utils.isConnectingToInternet()) {

                loadingPanel.setVisibility(View.VISIBLE);

                updateMileStones(jsonObjectMileStone, jsonArrayMilestones);
            } else {
                utils.toast(2, 2, getString(R.string.warning_internet));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMileStones(JSONObject jsonToUpdate, final JSONArray jsonArrayMilestones) {

        Utils.log(jsonToUpdate.toString(), " JSON ");

        storageService.updateDocs(jsonToUpdate,
                act.getStrActivityID(),
                Config.collectionActivity, new App42CallBack() {
                    @Override
                    public void onSuccess(Object o) {

                        String selection = DbHelper.COLUMN_OBJECT_ID + " = ? and "
                                + DbHelper.COLUMN_COLLECTION_NAME + "=?";

                        String[] selectionArgs = {
                                act.getStrActivityID()
                                , Config.collectionActivity};

                        Cursor cursor = CareGiver.getDbCon().fetch(DbHelper.strTableNameCollection,
                                DbHelper.COLLECTION_FIELDS, selection, selectionArgs, null, "0, 1",
                                true, null, null
                        );

                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();

                            try {
                                JSONObject jsonObject = new JSONObject(cursor.getString(2));

                                jsonObject.put("milestones", jsonArrayMilestones);
                                jsonObject.put("status", strActivityStatus);
                                jsonObject.put("activity_done_date", strActivityDoneDate);

                                String values1[] = {act.getStrActivityID(),
                                        jsonObject.toString(),
                                        Config.collectionActivity, "1"};

                                // WHERE clause arguments
                                CareGiver.getDbCon().updateInsert(
                                        DbHelper.strTableNameCollection,
                                        selection, values1, new String[]{"object_id", "document",
                                                "collection_name", "new_updated"},
                                        selectionArgs);

                                AppUtils.insertActivityDate(act.getStrActivityID(),
                                        jsonObject.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        CareGiver.getDbCon().closeCursor(cursor);

                        ///
                        String selection1 = DbHelper.COLUMN_OBJECT_ID + " = ? and "
                                + DbHelper.COLUMN_COLLECTION_NAME + "=?";

                        String[] selectionArgs1 = {
                                act.getStrActivityID()
                                , Config.collectionActivity};

                        String values1[] = {"0"};

                        // WHERE clause arguments
                        CareGiver.getDbCon().updateInsert(
                                DbHelper.strTableNameCollection,
                                selection1, values1, new String[]{"new_updated"},
                                selectionArgs1);
                        //
                        insertNotification();
                    }

                    @Override
                    public void onException(Exception e) {
                        loadingPanel.setVisibility(View.GONE);
                        utils.toast(2, 2, getString(R.string.warning_internet));
                    }
                });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        permissionHelper.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) { //&& data != null
            try {

                loadingPanel.setVisibility(View.VISIBLE);
                switch (requestCode) {
                    case Config.START_CAMERA_REQUEST_CODE:

                        backgroundThreadHandler = new BackgroundThreadHandler();
                        strImageName1 = Utils.customerImageUri.getPath();
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

    private void addImages() {

        layout.removeAllViews();

        if (fileModels != null) {
            for (int i = 0; i < fileModels.size(); i++) {
                try {


                    ImageView imageView = new ImageView(MilestoneActivity.this);
                    imageView.setPadding(0, 0, 3, 0);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(10, 10, 10, 10);

                    imageView.setLayoutParams(layoutParams);
                    imageView.setImageBitmap(bitmaps.get(i));

                    imageView.setTag(fileModels.get(i));
                    imageView.setTag(R.id.three, i);

                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final FileModel mFileModel = (FileModel) v.getTag();

                            final int mPosition = (int) v.getTag(R.id.three);

                            final Dialog dialog = new Dialog(MilestoneActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            dialog.setContentView(R.layout.image_dialog_layout);

                            TouchImageView mOriginal = (TouchImageView) dialog.findViewById(
                                    R.id.imgOriginal);
                            TextView textViewClose = (TextView) dialog.findViewById(
                                    R.id.textViewClose);
                            Button buttonDelete = (Button) dialog.findViewById(
                                    R.id.textViewTitle);

                            if (!bEnabled)
                                buttonDelete.setVisibility(View.INVISIBLE);

                            textViewClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            buttonDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //
                                    final AlertDialog.Builder alertbox =
                                            new AlertDialog.Builder(MilestoneActivity.this);
                                    alertbox.setTitle(getString(R.string.delete_file));
                                    alertbox.setMessage(getString(R.string.confirm_delete_file));
                                    alertbox.setPositiveButton(getString(R.string.yes), new
                                            DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg0, int arg1) {

                                                    try {
                                                        File fDelete = utils.getInternalFileImages(
                                                                mFileModel.getStrFileName());

                                                        boolean success = true;

                                                        if (fDelete.exists()) {
                                                            success = fDelete.delete();

                                                            if (mFileModel.isNew())
                                                                mImageCount--;

                                                            mImageChanged = true;

                                                            fileModels.remove(mFileModel);

                                                            bitmaps.remove(mPosition);
                                                        }
                                                        if (success) {
                                                            utils.toast(2, 2, getString(
                                                                    R.string.file_deleted));
                                                        }
                                                        arg0.dismiss();
                                                        dialog.dismiss();
                                                        addImages();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                    alertbox.setNegativeButton(getString(R.string.no), new
                                            DialogInterface.OnClickListener() {
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
                            } catch (OutOfMemoryError oOm) {
                                oOm.printStackTrace();
                            }
                            dialog.setCancelable(true);

                            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT); //Controlling width and height.
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
        loadingPanel.setVisibility(View.GONE);

    }

    private void uploadImage(final ViewGroup v, final int iMileStoneId, final ViewGroup viewGroup,
                             final int iFlag) {

        if (mImageCount > 0) {

            loadingPanel.setVisibility(View.VISIBLE);

            if (mImageUploadCount < fileModels.size()) {

                final FileModel fileModel = fileModels.get(mImageUploadCount);

                if (fileModel.isNew()) {

                    UploadService uploadService = new UploadService(this);


                    uploadService.uploadImageCommon(fileModel.getStrFilePath(),
                            fileModel.getStrFileName(), fileModel.getStrFileDescription(),
                            Config.providerModel.getStrEmail(), UploadFileType.IMAGE,
                            new App42CallBack() {
                                public void onSuccess(Object response) {

                                    if (response != null) {

                                        //Utils.log(response.toString(), " Success ");

                                        Upload upload = (Upload) response;
                                        ArrayList<Upload.File> fileList = upload.getFileList();
                                        if (fileList.size() > 0) {

                                            Upload.File file = fileList.get(0);

                                            fileModels.get(mImageUploadCount).setNew(false);
                                            fileModels.get(mImageUploadCount).setStrFileUrl(file.
                                                    getUrl());

                                            try {
                                                mImageUploadCount++;
                                                if (mImageUploadCount >= fileModels.size()) {
                                                    updateImages(iMileStoneId);
                                                } else {
                                                    uploadImage(v, iMileStoneId, viewGroup, iFlag);
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        } else {

                                            mImageUploadCount++;
                                            if (mImageUploadCount >= fileModels.size()) {
                                                updateImages(iMileStoneId);
                                            } else {
                                                uploadImage(v, iMileStoneId, viewGroup, iFlag);
                                            }
                                        }
                                    } else {
                                        loadingPanel.setVisibility(View.GONE);
                                        utils.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                }

                                @Override
                                public void onException(Exception e) {

                                    if (e != null) {
                                        Utils.log(e.getMessage(), " Failure ");

                                        mImageUploadCount++;
                                        if (mImageUploadCount >= fileModels.size()) {

                                            updateImages(iMileStoneId);
                                        } else {
                                            uploadImage(v, iMileStoneId, viewGroup, iFlag);
                                        }
                                    } else {
                                        loadingPanel.setVisibility(View.GONE);
                                        utils.toast(2, 2, getString(R.string.warning_internet));
                                    }
                                }
                            });
                } else {
                    mImageUploadCount++;

                    if (mImageUploadCount >= fileModels.size()) {
                        updateImages(iMileStoneId);
                    } else {
                        uploadImage(v, iMileStoneId, viewGroup, iFlag);
                    }
                }
            } else {
                updateImages(iMileStoneId);
            }
        } else {
            loadingPanel.setVisibility(View.VISIBLE);

            if (mImageChanged) {
                updateImages(iMileStoneId);
            } else {
                uploadJson();
            }
        }

    }

    private void updateImages(int iMilestoneId) {

        if (utils.isConnectingToInternet()) {

            try {

                for (MilestoneModel milestoneModel : act.getMilestoneModels()) {

                    if (milestoneModel.getiMilestoneId() == iMilestoneId) {

                        milestoneModel.clearFileModel();

                        milestoneModel.setFileModels(fileModels);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            uploadJson();

        } else {
            loadingPanel.setVisibility(View.GONE);
            utils.toast(2, 2, getString(R.string.warning_internet));
        }
    }

    private void insertNotification() {

        if (utils.isConnectingToInternet()) {

           /* String strHtml="";

            Utils.sendEmail(MilestoneActivity.this, "balamscint@gmail.com", act.getStrActivityName(),
                    "test");*/

            storageService.insertDocs(Config.collectionNotification, jsonObject,
                    new AsyncApp42ServiceApi.App42StorageServiceListener() {

                        @Override
                        public void onDocumentInserted(Storage response) {
                            try {
                                if (response.isResponseSuccess()) {
                                    sendPushToDependent();
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

    private void sendPushToDependent() {

        if (utils.isConnectingToInternet()) {

            PushNotificationService pushNotificationService = new PushNotificationService(
                    MilestoneActivity.this);

            if (!strCustomerEmail.equalsIgnoreCase("")) {

                pushNotificationService.sendPushToUser(strCustomerEmail, jsonObject.toString(),
                        new App42CallBack() {

                            @Override
                            public void onSuccess(Object o) {

                                strAlert = getString(R.string.activity_updated);

                                if (o == null) {
                                    strAlert = getString(R.string.no_push_actiity_updated);
                                } else Utils.log(o.toString(), " PUSH 1");

                                goToActivityList(strAlert);
                            }

                            @Override
                            public void onException(Exception ex) {
                                if (ex != null)
                                    Utils.log(ex.getMessage(), " PUSH ");
                                strAlert = getString(R.string.no_push_actiity_updated);
                                goToActivityList(strAlert);
                            }
                        });
            } else {
                strAlert = getString(R.string.no_push_actiity_updated);

                goToActivityList(strAlert);
            }
        } else {
            strAlert = getString(R.string.no_push_actiity_updated);

            goToActivityList(strAlert);
        }
    }

    private void goToActivityList(String strAlert) {

        loadingPanel.setVisibility(View.GONE);

        utils.toast(1, 1, strAlert);

        mImageCount = 0;
        mImageChanged = false;
        mImageUploadCount = 0;

        Bundle args = new Bundle();
        Intent intent = new Intent(MilestoneActivity.this, FeatureActivity.class);
        args.putSerializable("ACTIVITY", act);
        args.putInt("WHICH_SCREEN", bWhichScreen);
        intent.putExtras(args);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        permissionHelper.finish();
        mImageCount = 0;
        mImageChanged = false;
        mImageUploadCount = 0;
    }

    private class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            addImages();
        }
    }

    private class BackgroundThreadHandlerLoad extends Handler {
        @Override
        public void handleMessage(Message msg) {
            backgroundThreadHandler = new BackgroundThreadHandler();
            Thread backgroundThreadImages = new BackgroundThreadImages();
            backgroundThreadImages.start();
        }
    }

    private class BackgroundThread extends Thread {
        @Override
        public void run() {

            try {
                for (int i = 0; i < imagePaths.size(); i++) {

                    Calendar calendar = Calendar.getInstance();
                    String strTime = String.valueOf(calendar.getTimeInMillis());
                    String strFileName = strTime + ".jpeg";
                    Date date = calendar.getTime();

                    File mCopyFile = utils.getInternalFileImages(strFileName);
                    utils.copyFile(new File(imagePaths.get(i)), mCopyFile);

                    FileModel fileModel = new FileModel(strFileName, "", "IMAGE", Utils.
                            convertDateToString(date), strFileName, mCopyFile.getAbsolutePath());

                    fileModel.setNew(true);

                    fileModels.add(fileModel);

                    utils.compressImageFromPath(mCopyFile.getAbsolutePath(),
                            Config.intCompressWidth, Config.intCompressHeight, Config.iQuality);

                    bitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(),
                            Config.intWidth, Config.intHeight));

                    mImageCount++;
                }
                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (IOException | OutOfMemoryError ignored) {
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    private class BackgroundThreadCamera extends Thread {
        @Override
        public void run() {
            try {
                if (strImageName1 != null && !strImageName1.equalsIgnoreCase("")) {

                    Calendar calendar = Calendar.getInstance();
                    String strTime = String.valueOf(calendar.getTimeInMillis());
                    String strFileName = strTime + ".jpeg";

                    File mCopyFile = utils.getInternalFileImages(strFileName);
                    utils.copyFile(new File(strImageName1), mCopyFile);
                    Date date = new Date();
                    FileModel fileModel = new FileModel(strFileName, "", "IMAGE", Utils.
                            convertDateToString(date), strFileName, mCopyFile.getAbsolutePath());
                    fileModel.setNew(true);

                    fileModels.add(fileModel);

                    utils.compressImageFromPath(mCopyFile.getAbsolutePath(), Config.intCompressWidth,
                            Config.intCompressHeight, Config.iQuality);

                    bitmaps.add(utils.getBitmapFromFile(mCopyFile.getAbsolutePath(),
                            Config.intWidth, Config.intHeight));

                    mImageCount++;

                }

            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
            backgroundThreadHandler.sendEmptyMessage(0);
        }
    }

    private class BackgroundThreadImagesLoad extends Thread {
        @Override
        public void run() {
            try {

                for (FileModel fileModel : fileModels) {
                    if (fileModel.getStrFileName() != null && !fileModel.getStrFileName().
                            equalsIgnoreCase("")) {

                        File file = utils.getInternalFileImages(fileModel.getStrFileName());

                        if (!file.exists() || file.length() <= 0) {
                            Utils.loadImageFromWeb(fileModel.getStrFileName(),
                                    fileModel.getStrFileUrl(), MilestoneActivity.this);
                        }

                    }
                }
            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
            backgroundThreadHandlerLoad.sendEmptyMessage(0);
        }
    }

    private class BackgroundThreadImages extends Thread {
        @Override
        public void run() {
            try {

                for (FileModel fileModel : fileModels) {
                    if (fileModel.getStrFileName() != null && !fileModel.getStrFileName().
                            equalsIgnoreCase("")) {
                        bitmaps.add(utils.getBitmapFromFile(utils.getInternalFileImages(
                                fileModel.getStrFileName()).getAbsolutePath(), Config.intWidth,
                                Config.intHeight));

                        fileModel.setNew(false);
                    }
                }
            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
            backgroundThreadHandler.sendEmptyMessage(0);
        }
    }
}