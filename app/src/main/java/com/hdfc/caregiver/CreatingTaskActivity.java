package com.hdfc.caregiver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.hdfc.app42service.App42GCMService;
import com.hdfc.app42service.PushNotificationService;
import com.hdfc.app42service.StorageService;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.AppUtils;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Utils;
import com.hdfc.models.DependentModel;
import com.hdfc.models.FieldModel;
import com.hdfc.models.MilestoneModel;
import com.hdfc.models.ServiceModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import net.sqlcipher.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Admin on 28-01-2016.
 */
public class CreatingTaskActivity extends AppCompatActivity {

    private static String valDateTime, valTitle, valSearch, strServiceName, valSearchService;
    private static StorageService storageService;
    private RelativeLayout loadingPanel;
    private Spinner dependentList, serviceCategoryList, inputSearchServices;
    private boolean isClicked = false;
    private View focusView = null;
    private AutoCompleteTextView inputSearch;
    private String _strDate, strAlert, strPushMessage, strSelectedCustomer, strDate,
            strSelectedDependent;

    private Utils utils;
    private AppUtils appUtils;
    private EditText editTextTitle, dateAnd;
    private JSONObject jsonObject;
    private int mDependentPosition = -1;
    private ProgressDialog progressDialog;

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+
            // date.getTime();
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.

            strDate = Utils.writeFormat.format(date);
            _strDate = Utils.readFormat.format(date);
            dateAnd.setText(strDate);
        }

        @Override
        public void onDateTimeCancel() {
        }

    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_task);
        dateAnd = (EditText) findViewById(R.id.editDob);
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        inputSearch = (AutoCompleteTextView) findViewById(R.id.inputSearch);
        inputSearchServices = (Spinner) findViewById(R.id.inputSearchServices);
        dependentList = (Spinner) findViewById(R.id.spindependentList);
        serviceCategoryList = (Spinner) findViewById(R.id.spinServiceList);
        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        progressDialog = new ProgressDialog(CreatingTaskActivity.this);

        utils = new Utils(CreatingTaskActivity.this);
        appUtils = new AppUtils(CreatingTaskActivity.this);
        appUtils.createCustomerModel();
        storageService = new StorageService(CreatingTaskActivity.this);

        dependentList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (Config.strDependentNames.size() > 0) {
                        String strName = (String) parent.getAdapter().getItem(position);
                        int iPosition = Config.strDependentNames.indexOf(strName);
                        if (iPosition > -1) {
                            strSelectedDependent = Config.strDependentNames.get(iPosition);
                        } else {
                            strSelectedDependent = "";
                        }
                    } else strSelectedDependent = "";
                } catch (Exception e) {
                    e.printStackTrace();
                    strSelectedDependent = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Button backImage = (Button) findViewById(R.id.buttonBack);
        if (backImage != null) {
            backImage.setVisibility(View.VISIBLE);
            backImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBack();
                }
            });
        }

        dateAnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        });

        inputSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ArrayList<String> names = new ArrayList<>();
                names.clear();
                names.add(getString(R.string.no_dependents));

                valSearch = inputSearch.getText().toString().trim();

                mDependentPosition = Config.strCustomerNames.indexOf(valSearch.trim());

                if (mDependentPosition > -1 && Config.clientNameModels.size() > 0
                        && mDependentPosition < Config.clientNameModels.size()) {
                    names.clear();
                    names = Config.clientNameModels.get(mDependentPosition).getStrDependentNames();
                }

                ArrayAdapter<String> adapter1 = new ArrayAdapter<>(CreatingTaskActivity.this,
                        android.R.layout.simple_dropdown_item_1line, names);
                dependentList.setAdapter(adapter1);
                //setting the adapter data into the AutoCompleteTextView*/

            }
        });

        inputSearchServices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> nameService = new ArrayList<>();
                nameService.clear();
                nameService.add(getString(R.string.no_services));

                try {
                    //mCategoryPosition = position;

                    if (Config.serviceNameModels.size() > 0) {
                        nameService.clear();
                        nameService.addAll(Config.serviceNameModels.get(Config.serviceCategorylist.
                                get(position)));
                    }

                    ArrayAdapter<String> adapter1 = new ArrayAdapter<>(CreatingTaskActivity.this,
                            android.R.layout.simple_dropdown_item_1line, nameService);
                    serviceCategoryList.setAdapter(adapter1);
                    //setting the adapter data into the AutoCompleteTextView*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        serviceCategoryList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                valSearchService = serviceCategoryList.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Button createtaskDone = (Button) findViewById(R.id.textViewDoneHeaderCreatingTask);

        if (createtaskDone != null) {
            createtaskDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isClicked) {
                        isClicked = true;

                        boolean cancel = false;

                        editTextTitle.setError(null);
                        dateAnd.setError(null);
                        inputSearch.setError(null);

                        valTitle = editTextTitle.getText().toString().trim();
                        valDateTime = dateAnd.getText().toString().trim();
                        valSearch = inputSearch.getText().toString().trim();
                        strServiceName = valSearchService;


                        if (TextUtils.isEmpty(valDateTime)) {
                            dateAnd.setError(getString(R.string.error_field_required));
                            focusView = dateAnd;
                            cancel = true;
                        }

                        if (TextUtils.isEmpty(valTitle)) {
                            editTextTitle.setError(getString(R.string.error_field_required));
                            focusView = editTextTitle;
                            cancel = true;
                        }

                        if (TextUtils.isEmpty(strSelectedDependent)) {
                            utils.toast(2, 2, getString(R.string.select_dependent));
                            focusView = dependentList;
                            cancel = true;
                        }

                        if (TextUtils.isEmpty(valSearch)) {
                            inputSearch.setError(getString(R.string.error_field_required));
                            focusView = inputSearch;
                            cancel = true;
                        }

                        if (cancel) {
                            focusView.requestFocus();
                            isClicked = false;
                        } else {

                            if (utils.isConnectingToInternet()) {

                                ////////////////////////////
                                boolean bFuture = true;

                                /////////////////////////////
                                Calendar calendar = Calendar.getInstance();
                                Date dateNow = null;
                                String strdateCopy;
                                Date enteredDate = null;

                                try {
                                    strdateCopy = Utils.writeFormat.format(calendar.getTime());
                                    dateNow = Utils.writeFormat.parse(strdateCopy);
                                    enteredDate = Utils.readFormat.parse(_strDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if (dateNow != null && enteredDate != null) {

                                    Utils.log(String.valueOf(dateNow + " ! " + enteredDate), "NOW");

                                    if (enteredDate.compareTo(dateNow) < 0) {
                                        bFuture = false;
                                    }
                                }

                                if (bFuture) {

                                    ServiceModel serviceModel = null;
                                    DependentModel dependentModel = null;

                                    int iServicePosition = Config.servicelist.
                                            indexOf(strServiceName);
                                    if (iServicePosition > -1)
                                        serviceModel = Config.serviceModels.get(iServicePosition);

                                    int iCustomerPosition = Config.strCustomerNames.
                                            indexOf(valSearch);
                                    if (iCustomerPosition > -1)
                                        strSelectedCustomer = Config.clientModels.
                                                get(iCustomerPosition).getCustomerModel().
                                                getStrEmail();

                                    int iDependentPosition = Config.clientNameModels.
                                            get(iCustomerPosition).getStrDependentNames().
                                            indexOf(strSelectedDependent);
                                    if (iDependentPosition > -1)
                                        dependentModel = Config.clientModels.get(iCustomerPosition).
                                                getDependentModels().get(iDependentPosition);


                                    if (iCustomerPosition > -1) {

                                        if (iServicePosition > -1) {

                                            loadingPanel.setVisibility(View.VISIBLE);

                                            uploadData(serviceModel, dependentModel);

                                        } else {
                                            isClicked = false;
                                            utils.toast(2, 2, getString(R.string.error_service));
                                        }

                                    } else {
                                        isClicked = false;
                                        utils.toast(2, 2, getString(R.string.error_client));
                                    }
                                } else {
                                    isClicked = false;
                                    utils.toast(2, 2, getString(R.string.invalid_date));
                                }

                            } else {
                                isClicked = false;
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }

                        }
                    }
                }

            });
        }
        refreshCustomerAdapter();

        //loadingPanel.setVisibility(View.VISIBLE);
        if (progressDialog != null) {
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        fetchServices();
    }

    private void refreshServiceAdapter() {
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(CreatingTaskActivity.this,
                android.R.layout.simple_dropdown_item_1line, Config.serviceCategorylist);

        //inputSearchServices.setThreshold(1);//will start working from first character
        inputSearchServices.setAdapter(adapter);
    }

    private void refreshCustomerAdapter() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(CreatingTaskActivity.this,
                android.R.layout.select_dialog_item, Config.strCustomerNames);
        //Getting the instance of AutoCompleteTextView
        //AutoCompleteTextView actv= (AutoCompleteTextView)findViewById(R.id.inputSearch);
        inputSearch.setThreshold(1);//will start working from first character
        inputSearch.setAdapter(arrayAdapter);//setting the adapter data into the AutoCompleteTextView*/
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void fetchServices() {

        if (Utils.isConnectingToInternet(CreatingTaskActivity.this)) {

            String strDate = "";

            Cursor cursor = CareGiver.getDbCon().getMaxDate(Config.collectionService);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                strDate = cursor.getString(0);
            }

            if (strDate == null || strDate.equalsIgnoreCase(""))
                strDate = DbHelper.DEFAULT_DB_DATE;

            CareGiver.getDbCon().closeCursor(cursor);

            Query q1 = QueryBuilder.build("_$updatedAt", strDate, QueryBuilder.Operator.
                    GREATER_THAN);

            storageService.findDocsByQueryOrderBy(Config.collectionService, q1, 30000, 0,
                    "_$updatedAt", 1,
                    new App42CallBack() {
                        @Override
                        public void onSuccess(Object o) {

                            if (o != null) {

                                Utils.log(o.toString(), " Service");

                                Storage storage = (Storage) o;

                                ArrayList<Storage.JSONDocument> jsonDocList = storage.
                                        getJsonDocList();

                                for (int i = 0; i < jsonDocList.size(); i++) {

                                    Storage.JSONDocument jsonDocument = jsonDocList.get(i);

                                    try {
                                        String values[] = {jsonDocument.getDocId(),
                                                jsonDocument.getUpdatedAt(),
                                                jsonDocument.getJsonDoc(),
                                                Config.collectionService, "0", "", "1"};

                                        String selection = DbHelper.COLUMN_OBJECT_ID + "=? and "
                                                + DbHelper.COLUMN_COLLECTION_NAME + "=?";

                                        // WHERE clause arguments
                                        String[] selectionArgs = {jsonDocument.getDocId(),
                                                Config.collectionService};
                                        CareGiver.getDbCon().updateInsert(
                                                DbHelper.strTableNameCollection,
                                                selection, values, DbHelper.COLLECTION_FIELDS,
                                                selectionArgs);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            refreshServices();
                        }

                        @Override
                        public void onException(Exception e) {
                            if (e != null)
                                Utils.log(e.getMessage(), " Service");
                            refreshServices();
                        }
                    });
        } else {
            refreshServices();
        }

    }


    private void refreshServices() {

        //loadingPanel.setVisibility(View.GONE);

        Config.strServcieIds.clear();
        Config.serviceModels.clear();
        Config.servicelist.clear();
        Config.serviceNameModels.clear();
        AppUtils.categorySet.clear();

        Cursor cursor = CareGiver.getDbCon().fetch(
                DbHelper.strTableNameCollection,
                DbHelper.COLLECTION_FIELDS,
                DbHelper.COLUMN_COLLECTION_NAME + "=?",
                new String[]{Config.collectionService},
                null, null, true, null, null
        );

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                while (!cursor.isAfterLast()) {
                    JSONObject jsonObject = new JSONObject(cursor.getString(2));

                    if (jsonObject.has("unit"))
                        appUtils.createServiceModel(cursor.getString(0),
                                jsonObject);
                    cursor.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        CareGiver.getDbCon().closeCursor(cursor);

        refreshServiceAdapter();
        //refreshCustomerAdapter();
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

    }

    private void uploadData(ServiceModel serviceModel, DependentModel dependentModel) {
        addActivity(serviceModel, dependentModel);
    }

    private void addActivity(final ServiceModel serviceModel, final DependentModel dependentModel) {

        JSONObject jsonObjectServices = null;

        try {
            jsonObjectServices = new JSONObject();

            jsonObjectServices.put("service_id", serviceModel.getStrServiceId());
            jsonObjectServices.put("service_name", serviceModel.getStrServiceName());
            jsonObjectServices.put("service_no", serviceModel.getiServiceNo());
            jsonObjectServices.put("service_type", serviceModel.getStrServiceType());
            jsonObjectServices.put("category_name", serviceModel.getStrCategoryName());

            jsonObjectServices.put("customer_id", dependentModel.getStrCustomerID());
            jsonObjectServices.put("dependent_id", dependentModel.getStrDependentID());
            jsonObjectServices.put("provider_id", Config.providerModel.getStrProviderId());

            jsonObjectServices.put("status", "new");
            jsonObjectServices.put("provider_status", "new");
            jsonObjectServices.put("provider_message", "");

            jsonObjectServices.put("activity_date", _strDate);
            jsonObjectServices.put("activity_done_date", "");
            jsonObjectServices.put("activity_name", serviceModel.getStrServiceName());
            jsonObjectServices.put("activity_desc", valTitle);
            jsonObjectServices.put("overdue", false);

            JSONArray jsonArray = new JSONArray();

            jsonArray.put("{\"0\":\"empty\"}");

            jsonObjectServices.put("feedbacks", jsonArray);
            //todo remove unwanted
            jsonObjectServices.put("videos", jsonArray);
            jsonObjectServices.put("images", jsonArray);


            JSONArray jsonArrayMilestones = new JSONArray();

            for (MilestoneModel milestoneModel : serviceModel.getMilestoneModels()) {

                JSONObject jsonObjectMilestone = new JSONObject();

                jsonObjectMilestone.put("id", milestoneModel.getiMilestoneId());
                jsonObjectMilestone.put("status", milestoneModel.getStrMilestoneStatus());
                jsonObjectMilestone.put("name", milestoneModel.getStrMilestoneName());
                jsonObjectMilestone.put("date", milestoneModel.getStrMilestoneDate());

                jsonObjectMilestone.put("show", milestoneModel.isVisible());
                jsonObjectMilestone.put("reschedule", milestoneModel.isReschedule());
                jsonObjectMilestone.put("scheduled_date", milestoneModel.
                        getStrMilestoneScheduledDate());

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
                        jsonObjectField.put("values", utils.stringToJsonArray(fieldModel.
                                getStrFieldValues()));

                    if (fieldModel.isChild()) {

                        jsonObjectField.put("child", fieldModel.isChild());

                        if (fieldModel.getStrChildType() != null && fieldModel.getStrChildType().
                                length > 0)
                            jsonObjectField.put("child_type", utils.stringToJsonArray(fieldModel.
                                    getStrChildType()));

                        if (fieldModel.getStrChildValue() != null && fieldModel.getStrChildValue().
                                length > 0)
                            jsonObjectField.put("child_value", utils.stringToJsonArray(fieldModel.
                                    getStrChildValue()));

                        if (fieldModel.getStrChildCondition() != null && fieldModel.
                                getStrChildCondition().length > 0)
                            jsonObjectField.put("child_condition", utils.stringToJsonArray(
                                    fieldModel.getStrChildCondition()));

                        if (fieldModel.getiChildfieldID() != null && fieldModel.getiChildfieldID().
                                length > 0)
                            jsonObjectField.put("child_field", utils.intToJsonArray(
                                    fieldModel.getiChildfieldID()));
                    }

                    if (fieldModel.getiArrayCount() > 0) {
                        jsonObjectField.put("array_fields", fieldModel.getiArrayCount());
                        jsonObjectField.put("array_type", utils.stringToJsonArray(fieldModel.
                                getStrArrayType()));
                        jsonObjectField.put("array_data", fieldModel.getStrArrayData());
                    }

                    jsonArrayFields.put(jsonObjectField);

                    jsonObjectMilestone.put("fields", jsonArrayFields);
                }
                jsonArrayMilestones.put(jsonObjectMilestone);
            }

            jsonObjectServices.put("milestones", jsonArrayMilestones);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        storageService.insertDocs(Config.collectionActivity, jsonObjectServices,
                new AsyncApp42ServiceApi.App42StorageServiceListener() {

                    @Override
                    public void onDocumentInserted(Storage response) {
                        try {
                            if (response != null) {

                                if (response.getJsonDocList().size() > 0) {

                                    String strDateNow;

                                    Calendar calendar = Calendar.getInstance();
                                    Date dateNow = calendar.getTime();

                                    strDateNow = Utils.convertDateToString(dateNow);

                                    String values[] = {response.getJsonDocList().get(0).getDocId(),
                                            "",
                                            response.getJsonDocList().get(0).getJsonDoc(),
                                            Config.collectionActivity, "0", "", "1"};

                                    String selection = DbHelper.COLUMN_OBJECT_ID + " = ? and "
                                            + DbHelper.COLUMN_COLLECTION_NAME + "=?";

                                    // WHERE clause arguments
                                    String[] selectionArgs = {
                                            response.getJsonDocList().get(0).getDocId()
                                            , Config.collectionActivity};
                                    CareGiver.getDbCon().updateInsert(
                                            DbHelper.strTableNameCollection,
                                            selection, values, DbHelper.COLLECTION_FIELDS,
                                            selectionArgs);

                                    AppUtils.insertActivityDate(
                                            response.getJsonDocList().get(0).getDocId(),
                                            response.getJsonDocList().get(0).getJsonDoc());

                                    strPushMessage = Config.providerModel.getStrName() +
                                            getString(R.string.has_created) +
                                            serviceModel.getStrServiceName()
                                            + getString(R.string.to) +
                                            dependentModel.getStrName() +
                                            getString(R.string.on) + strDate;

                                    jsonObject = new JSONObject();

                                    try {

                                        jsonObject.put("created_by", Config.providerModel.
                                                getStrProviderId());
                                        jsonObject.put("time", strDateNow);
                                        jsonObject.put("user_type", "dependent");
                                        jsonObject.put("user_id", dependentModel.
                                                getStrDependentID());
                                        jsonObject.put("activity_id", response.getJsonDocList().
                                                get(0).getDocId());
                                        jsonObject.put("created_by_type", "provider");
                                        jsonObject.put(App42GCMService.ExtraMessage,
                                                strPushMessage);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //

                                    insertNotification();
                                } else {
                                    loadingPanel.setVisibility(View.GONE);
                                    isClicked = false;
                                    utils.toast(2, 2, getString(R.string.error));
                                }
                            } else {
                                loadingPanel.setVisibility(View.GONE);
                                isClicked = false;
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            loadingPanel.setVisibility(View.GONE);
                            isClicked = false;
                            utils.toast(2, 2, getString(R.string.error));
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
                        isClicked = false;
                        loadingPanel.setVisibility(View.GONE);
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
    }

    /*  public void fetchService(final ServiceModel serviceModel, final DependentModel dependentModel) {

          if (utils.isConnectingToInternet()) {

              String key1 = "service_id";
              String value1 = serviceModel.getStrServiceId();

              String key2 = "customer_id";
              String value2 = dependentModel.getStrCustomerID();

              Query q1 = QueryBuilder.build(key1, value1, QueryBuilder.Operator.EQUALS);
              Query q2 = QueryBuilder.build(key2, value2, QueryBuilder.Operator.EQUALS);
              Query q3 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);

              storageService.findDocsByQueryOrderBy(Config.collectionServiceCustomer, q3, 1, 0,
                      "updated_date", 1,
                      new App42CallBack() {

                          @Override
                          public void onSuccess(Object o) {
                              try {
                                  if (o != null) {
                                      Utils.log(o.toString(), " 1 ");
                                      //57221947e4b0fa5b108f35dc
                                      //572b4f14e4b0492b68fbc9b1

                                      Storage response = (Storage) o;

                                      if (response.getJsonDocList().size() > 0) {

                                          Storage.JSONDocument jsonDocument = response.
                                                  getJsonDocList().get(0);

                                          String strDocument = jsonDocument.getJsonDoc();

                                          String strDocumentId = jsonDocument.getDocId();

                                          updateServiceDependent(strDocumentId, strDocument,
                                                  serviceModel);
                                      }
                                  } else {
                                      if (progressDialog.isShowing())
                                          progressDialog.dismiss();
                                      utils.toast(2, 2, getString(R.string.warning_internet));
                                  }

                              } catch (Exception e1) {
                                  if (progressDialog.isShowing())
                                      progressDialog.dismiss();
                                  utils.toast(2, 2, getString(R.string.error));
                                  e1.printStackTrace();
                              }
                          }

                          @Override
                          public void onException(Exception e) {
                              if (progressDialog.isShowing())
                                  progressDialog.dismiss();
                              try {
                                  if (e != null) {
                                      Utils.log(e.getMessage(), " 2 ");
                                      insertNotification();
                                      //utils.toast(2, 2, getString(R.string.error));
                                  } else {
                                      utils.toast(2, 2, getString(R.string.warning_internet));
                                  }
                              } catch (Exception e1) {
                                  e1.printStackTrace();
                              }
                          }
                      });

          } else {
              if (progressDialog.isShowing())
                  progressDialog.dismiss();
              utils.toast(2, 2, getString(R.string.warning_internet));
          }
      }

      public void updateServiceDependent(final String strDocumentId, final String strDocument,
                                         ServiceModel serviceModel) {

          if (utils.isConnectingToInternet()) {

              JSONObject jsonObjectServices = new JSONObject();
              String strDate = utils.convertDateToString(new Date());

              try {

                  JSONObject jsonObject = new JSONObject(strDocument);

                  //todo check single item units
                  jsonObjectServices.put("updated_date", strDate);
                  jsonObjectServices.put("unit", jsonObject.getInt("unit") -
                          serviceModel.getiUnitValue());

              } catch (JSONException e) {
                  e.printStackTrace();
              }

              storageService.updateDocs(jsonObjectServices, strDocumentId,
                      Config.collectionServiceCustomer,
                      new App42CallBack() {

                          @Override
                          public void onSuccess(Object o) {
                              try {
                                  if (o != null) {
                                      Utils.log(o.toString(), " 3 ");
                                      insertNotification();
                                  } else {
                                      if (progressDialog.isShowing())
                                          progressDialog.dismiss();
                                      utils.toast(2, 2, getString(R.string.warning_internet));
                                  }
                              } catch (Exception e1) {
                                  if (progressDialog.isShowing())
                                      progressDialog.dismiss();
                                  utils.toast(2, 2, getString(R.string.error));
                                  e1.printStackTrace();
                              }
                          }

                          @Override
                          public void onException(Exception ex) {
                              if (progressDialog.isShowing())
                                  progressDialog.dismiss();
                              try {
                                  if (ex != null) {
                                      Utils.log(ex.getMessage(), " 4 ");
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
                      });
          } else {
              if (progressDialog.isShowing())
                  progressDialog.dismiss();
              utils.toast(2, 2, getString(R.string.warning_internet));
          }
      }
  */
    private void sendPushToProvider() {

        if (utils.isConnectingToInternet()) {

            PushNotificationService pushNotificationService =
                    new PushNotificationService(CreatingTaskActivity.this);

            //strPushMessage

            pushNotificationService.sendPushToUser(strSelectedCustomer, jsonObject.toString(),
                    new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {

                            strAlert = getString(R.string.activity_added);

                            if (o == null)
                                strAlert = getString(R.string.no_push_actiity_added);

                            goToActivityList(strAlert);
                        }

                        @Override
                        public void onException(Exception ex) {
                            Utils.log(ex.getMessage(), " STRING ");
                            strAlert = getString(R.string.no_push_actiity_added);
                            goToActivityList(strAlert);
                        }
                    });
        } else {
            strAlert = getString(R.string.no_push_actiity_added);

            goToActivityList(strAlert);
        }
    }

    private void goToActivityList(String strAlert) {

        isClicked = false;

        Intent newIntent = new Intent(CreatingTaskActivity.this, DashboardActivity.class);

        loadingPanel.setVisibility(View.GONE);

        Config.intSelectedMenu = Config.intDashboardScreen;
        utils.toast(2, 2, strAlert);
        //newIntent.putExtra("CREATED", true);
        newIntent.putExtra("RETAIN_DATE", true);
        startActivity(newIntent);
        finish();

    }

   /* public void deleteCreatedActivity(){

        if (utils.isConnectingToInternet()) {

            storageService.deleteDocById(Config.collectionActivity, strInsertedDocumentId,
                    new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            if (o != null) {
                                utils.toast(2, 2, getString(R.string.activity_deleted));
                            } else {
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onException(Exception e) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            if (e != null) {
                                utils.toast(2, 2, getString(R.string.error));
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
    }*/

    private void insertNotification() {

        if (utils.isConnectingToInternet()) {

            storageService.insertDocs(Config.collectionNotification, jsonObject,
                    new AsyncApp42ServiceApi.App42StorageServiceListener() {

                        @Override
                        public void onDocumentInserted(Storage response) {
                            try {
                                if (response.isResponseSuccess()) {
                                    Utils.log(" 1 ", " Notify ");
                                    sendPushToProvider();
                                } else {
                                    strAlert = getString(R.string.no_push_actiity_added);
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
                            strAlert = getString(R.string.no_push_actiity_added);
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
            strAlert = getString(R.string.no_push_actiity_added);
            goToActivityList(strAlert);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        goBack();
    }

    private void goBack() {
        Intent intent = new Intent(CreatingTaskActivity.this, DashboardActivity.class);
        Config.intSelectedMenu = Config.intDashboardScreen;
        intent.putExtra("RETAIN_DATE", true);
        startActivity(intent);
        finish();
    }

}