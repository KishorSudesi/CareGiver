package com.hdfc.caregiver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.hdfc.app42service.PushNotificationService;
import com.hdfc.app42service.StorageService;
import com.hdfc.config.Config;
import com.hdfc.libs.AppUtils;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Utils;
import com.hdfc.models.DependentModel;
import com.hdfc.models.FieldModel;
import com.hdfc.models.MilestoneModel;
import com.hdfc.models.ServiceModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Admin on 28-01-2016.
 */
public class CreatingTaskActivity extends AppCompatActivity {

    private static String valDateTime, valTitle, valSearch, strServiceName;
    //private static ServiceAdapter serviceAdapter;
    private static StorageService storageService;
    private boolean cancel = false;
    private View focusView = null;
    private AutoCompleteTextView inputSearch, inputSearchServices;
    private String _strDate, strAlert, strPushMessage, strSelectedCustomer, strDate;
    private ProgressDialog progressDialog;
    private Utils utils;
    private AppUtils appUtils;
    private EditText editTextTitle, dateAnd;
    //private TextView dateTime;
    private JSONObject jsonObject;

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
                // Overriding onDateTimeCancel() is optional.
            }

    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_task);
        dateAnd = (EditText) findViewById(R.id.editDob);
        editTextTitle = (EditText)findViewById(R.id.editTextTitle);
        inputSearch = (AutoCompleteTextView) findViewById(R.id.inputSearch);
        inputSearchServices = (AutoCompleteTextView)findViewById(R.id.inputSearchServices);
        Spinner dependentlist = (Spinner) findViewById(R.id.spindependentList);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(CreatingTaskActivity.this, android.R.layout.select_dialog_item, Config.strDependentNames);
        //Getting the instance of AutoCompleteTextView
        //AutoCompleteTextView actv= (AutoCompleteTextView)findViewById(R.id.inputSearch);
        // dependentlist.setThreshold(1);//will start working from first character
        dependentlist.setAdapter(adapter1);//setting the adapter data into the AutoCompleteTextView*/

        utils = new Utils(CreatingTaskActivity.this);
        appUtils = new AppUtils(CreatingTaskActivity.this);
        progressDialog = new ProgressDialog(CreatingTaskActivity.this);

        storageService = new StorageService(CreatingTaskActivity.this);

       /* ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(CreatingTaskActivity.this,android.R.layout.select_dialog_item, Config.servicelist);
        //Getting the instance of AutoCompleteTextView
        //AutoCompleteTextView actv= (AutoCompleteTextView)findViewById(R.id.inputSearch);
        inputSearchServices.setThreshold(1);//will start working from first character
        inputSearchServices.setAdapter(adapter);*/

        //setItems();
        ImageView backImage = (ImageView) findViewById(R.id.imgBackCreatingTaskDetail);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreatingTaskActivity.this,DashboardActivity.class);
                Config.intSelectedMenu=Config.intDashboardScreen;
                startActivity(intent);
            }
        });
        /*Bundle b = getIntent().getExtras();
        intWhichScreen = b.getInt("WHICH_SCREEN", Config.intDashboardScreen);*/

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

        TextView createtaskDone = (TextView) findViewById(R.id.textViewDoneHeaderCreatingTask);

        createtaskDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valTitle = editTextTitle.getText().toString().trim();
                valDateTime = dateAnd.getText().toString().trim();
                valSearch = inputSearch.getText().toString().trim();
                strServiceName = inputSearchServices.getText().toString().trim();

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

                if (TextUtils.isEmpty(strServiceName)) {
                    inputSearchServices.setError(getString(R.string.error_field_required));
                    focusView = inputSearchServices;
                    cancel = true;
                }

                if (TextUtils.isEmpty(valSearch)) {
                    inputSearch.setError(getString(R.string.error_field_required));
                    focusView = inputSearch;
                    cancel = true;
                }


                if (cancel) {
                    focusView.requestFocus();
                } else {

                    if (utils.isConnectingToInternet()) {

                        progressDialog.setMessage(getString(R.string.loading));
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        int iServicePosition = Config.servicelist.indexOf(strServiceName);
                        ServiceModel serviceModel = Config.serviceModels.get(iServicePosition);

                        int iDependentPosition = Config.strDependentNames.indexOf(valSearch);
                        DependentModel dependentModel = Config.dependentModels.get(iDependentPosition);

                        uploadData(serviceModel, dependentModel);

                    } else utils.toast(2, 2, getString(R.string.warning_internet));

                }
            }

        });
    }

    @Override
    public void onResume(){
        super.onResume();

        refreshClients();

        storageService.findAllDocs(Config.collectionService,
                new App42CallBack() {
                    @Override
                    public void onSuccess(Object o) {

                        if (o != null) {

                            Storage storage = (Storage) o;

                            ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();

                            for (int i = 0; i < jsonDocList.size(); i++) {

                                Storage.JSONDocument jsonDocument = jsonDocList.get(i);

                                String strDocumentId = jsonDocument.getDocId();

                                String strServices = jsonDocument.getJsonDoc();

                                try {

                                    JSONObject jsonObjectServcies = new JSONObject(strServices);

                                    if (jsonObjectServcies.has("unit"))
                                        appUtils.createServiceModel(strDocumentId, jsonObjectServcies);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                               }
                           }

                        } else {
                            utils.toast(2, 2, getString(R.string.warning_internet));
                       }

                        refreshServices();
                    }

                    @Override
                    public void onException(Exception e) {

                        try {
                            refreshServices();
                            if (e != null) {
                                JSONObject jsonObject = new JSONObject(e.getMessage());
                                JSONObject jsonObjectError = jsonObject.getJSONObject("app42Fault");
                                String strMess = jsonObjectError.getString("details");

                                utils.toast(2, 2, strMess);
                            } else {
                                utils.toast(2, 2, getString(R.string.warning_internet));
                           }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                       }
                    }
                });
    }

    public void refreshClients(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreatingTaskActivity.this, android.R.layout.select_dialog_item, Config.strCustomerNames);
        //Getting the instance of AutoCompleteTextView
        //AutoCompleteTextView actv= (AutoCompleteTextView)findViewById(R.id.inputSearch);
        inputSearch.setThreshold(1);//will start working from first character
        inputSearch.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView*/
    }

    public void refreshServices() {
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(CreatingTaskActivity.this, android.R.layout.select_dialog_item, Config.servicelist);
        //Getting the instance of AutoCompleteTextView
        //AutoCompleteTextView actv= (AutoCompleteTextView)findViewById(R.id.inputSearch);
        inputSearchServices.setThreshold(1);//will start working from first character
        inputSearchServices.setAdapter(adapter);
    }

    public void uploadData(ServiceModel serviceModel, DependentModel dependentModel) {

        addActivity(serviceModel, dependentModel);

    }

    public void addActivity(final ServiceModel serviceModel, final DependentModel dependentModel) {

        JSONObject jsonObjectServices = null;

        try {
            jsonObjectServices = new JSONObject();
            //String strDate = utils.convertDateToString(new Date());

            jsonObjectServices.put("service_id", serviceModel.getStrServiceId());
            jsonObjectServices.put("service_name", serviceModel.getStrServiceName());
            jsonObjectServices.put("service_no", serviceModel.getiServiceNo());
            jsonObjectServices.put("service_type", serviceModel.getStrServiceType());
            jsonObjectServices.put("category_name", serviceModel.getStrCategoryName());

            jsonObjectServices.put("customer_id", dependentModel.getStrCustomerID());
            jsonObjectServices.put("dependent_id", dependentModel.getStrDependentID());
            jsonObjectServices.put("provider_id", Config.providerModel.getStrProviderId());

            int iPosition = Config.customerIdsAdded.indexOf(dependentModel.getStrCustomerID());

            strSelectedCustomer = Config.clientModels.get(iPosition).getCustomerModel().getStrEmail();

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
                jsonObjectMilestone.put("scheduled_date", milestoneModel.getStrMilestoneScheduledDate());

                JSONArray jsonArrayFields = new JSONArray();

                strPushMessage = Config.providerModel.getStrName() + getString(R.string.has_created) +
                        serviceModel.getStrServiceName() + getString(R.string.to) +
                        dependentModel.getStrName() +
                        getString(R.string.on) + strDate;

                jsonObject = new JSONObject();

                try {
                    Date date = new Date();
                    String strDate = utils.convertDateToString(date);

                    jsonObject.put("created_by", Config.providerModel.getStrProviderId());
                    jsonObject.put("time", strDate);
                    jsonObject.put("user_type", "dependent");
                    jsonObject.put("user_id", dependentModel.getStrDependentID());
                    jsonObject.put("created_by_type", "provider");
                    jsonObject.put("notification_message", strPushMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
                                    //   strInsertedDocumentId = response.getJsonDocList().get(0).getDocId();
                                    //   iUpdateFlag = iActivityCreated;
                                    //fetchService(serviceModel, dependentModel);
                                    insertNotification();
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    utils.toast(2, 2, getString(R.string.error));
                                }
                            } else {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
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
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
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
        //,
        //      Config.collectionActivity);
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
    public void sendPushToProvider() {

        if (utils.isConnectingToInternet()) {

            PushNotificationService pushNotificationService = new PushNotificationService(CreatingTaskActivity.this);

            pushNotificationService.sendPushToUser(strSelectedCustomer, strPushMessage,
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
                            strAlert = getString(R.string.no_push_actiity_added);
                            goToActivityList(strAlert);
                        }
                    });
        } else {
            strAlert = getString(R.string.no_push_actiity_added);

            goToActivityList(strAlert);
        }
    }

    public void goToActivityList(String strAlert) {

        Intent newIntent = new Intent(CreatingTaskActivity.this, DashboardActivity.class);

        if (progressDialog.isShowing())
            progressDialog.dismiss();

        Config.intSelectedMenu = Config.intDashboardScreen;
        utils.toast(2, 2, strAlert);

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

    public void insertNotification() {

        if (utils.isConnectingToInternet()) {

            storageService.insertDocs(Config.collectionNotification, jsonObject,
                    new AsyncApp42ServiceApi.App42StorageServiceListener() {

                        @Override
                        public void onDocumentInserted(Storage response) {
                            try {
                                if (response.isResponseSuccess()) {
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

}