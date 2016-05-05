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
import com.hdfc.app42service.StorageService;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.DependentModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 28-01-2016.
 */
public class CreatingTaskActivity extends AppCompatActivity {

    public static String valDateTime, valTitle, valSearch;
    public static String serviceName;
    public static DependentModel sDependentModel = new DependentModel();
    public static JSONArray jsonArrayFeatures;
    public static JSONObject jsonObjectActCarla;
    static Spinner spinner;
    private static StorageService storageService;
    private static ArrayList<DependentModel> dependentModels = new ArrayList<>();
    private static ArrayList<ActivityModel> activityModels = new ArrayList<>();
    private static String products="";
    String _strDate;
    TextView createtaskDone;
    boolean cancel = false;
    View focusView = null;
    ImageView backImage;
    AutoCompleteTextView inputSearch;
    private AsyncApp42ServiceApi asyncService;
    private ProgressDialog progressDialog;
    private JSONObject responseJSONDoc, responseJSONDocCarla, jsonObjectCarla,jsonObjectAct;
    private JSONArray jsonArrayVideos,jsonArrayFeedbacks,jsonArrayImages;
    private Utils utils;
    private EditText dateTime, editTextTitle;
        private SlideDateTimeListener listener = new SlideDateTimeListener() {

            @Override
            public void onDateTimeSet(Date date) {
                // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+
                // date.getTime();
                // Do something with the date. This Date object contains
                // the date and time that the user has selected.

                String strDate = Utils.writeFormat.format(date);
                _strDate = Utils.readFormat.format(date);
                dateTime.setText(strDate);
            }

            @Override
            public void onDateTimeCancel() {
                // Overriding onDateTimeCancel() is optional.
            }

    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_task);
        dateTime = (EditText)findViewById(R.id.editTextDate);
        editTextTitle = (EditText)findViewById(R.id.editTextTitle);
        inputSearch = (AutoCompleteTextView) findViewById(R.id.inputSearch);

        utils = new Utils(CreatingTaskActivity.this);
        progressDialog = new ProgressDialog(CreatingTaskActivity.this);

        setItems();
        backImage = (ImageView)findViewById(R.id.imgBackCreatingTaskDetail);
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

        dateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        });

        createtaskDone = (TextView)findViewById(R.id.textViewDoneHeaderCreatingTask);

        createtaskDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valTitle = editTextTitle.getText().toString().trim();
                valDateTime = dateTime.getText().toString();
                valSearch = inputSearch.getText().toString().trim();

                if (TextUtils.isEmpty(valDateTime)) {
                    dateTime.setError(getString(R.string.error_field_required));
                    focusView = dateTime;
                    cancel = true;
                }
                if (TextUtils.isEmpty(valTitle)) {
                    editTextTitle.setError(getString(R.string.error_field_required));
                    focusView = editTextTitle;
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
                    //// TODO: 3/13/2016

                    uploadData();
                }
            }

        });
        parseData();

    }

    @Override
    public void onResume(){
        super.onResume();
    }

       public void setItems(){

        storageService = new StorageService(CreatingTaskActivity.this);
           storageService.findDocsByKeyValue(Config.collectionServices, "service_name", "Medical Checkup", new AsyncApp42ServiceApi.App42StorageServiceListener() {
               @Override
               public void onDocumentInserted(Storage response) {

               }

               @Override
               public void onUpdateDocSuccess(Storage response) {

               }

               @Override
               public void onFindDocSuccess(Storage response) throws JSONException {
                   if(response.getJsonDocList().size()>0){

                       Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);

                       String strDocument = jsonDocument.getJsonDoc();

                       try{
                           final List<String> list=new ArrayList<String>();
                           spinner= (Spinner) findViewById(R.id.spinner);
                           Config.jsonObject = new JSONObject(strDocument);
                           jsonArrayFeatures=Config.jsonObject.getJSONArray("features");
                           String service_name = Config.jsonObject.getString("service_name");
                           list.add(service_name);
                           ArrayAdapter<String> adapter=new ArrayAdapter<String>(CreatingTaskActivity.this, android.R.layout.simple_list_item_1,list);
                           spinner.setAdapter(adapter);
                       } catch (Exception e){
                           e.printStackTrace();
                       }
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
    }

    public void parseData() {

        StorageService storageService = new StorageService(this);

        storageService.findDocsByKeyValue(Config.collectionDependent, "dependent_name",
                "dsfdsfsdf", new AsyncApp42ServiceApi.App42StorageServiceListener() {
                    //Config.dependent_name
            @Override
            public void onDocumentInserted(Storage response) {

            }

            @Override
            public void onUpdateDocSuccess(Storage response) {

            }

            @Override
            public void onFindDocSuccess(Storage response) {

                Utils.log(String.valueOf(response.getJsonDocList().size()), " count ");

                if (response.getJsonDocList().size() > 0) {

                    Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);

                    String strDocument = jsonDocument.getJsonDoc();

                    try {

                        Config.jsonObject = new JSONObject(strDocument);

                        Storage storage = response;
                        ArrayList<Storage.JSONDocument> fileSize = storage.getJsonDocList();

                        activityModels.clear();
                        ActivityModel activityModel = new ActivityModel();

                        dependentModels.clear();

                        if (Config.jsonObject.has("services")) {

                            //JSONArray jsonArrayNotifications = Config.jsonObject.getJSONArray("");
//                            products = new String();

//                            for (int j = 0; j < Config.jsonOb; j++) {

                            //  JSONObject jsonObjectNotification = jsonArrayNotifications.getJSONObject(j);

                            DependentModel dependentModel = new DependentModel();

//                                dependentModel.setStrCustomerEmail(jsonObjectNotification.getString("feedback_by"));
//                                dependentModel.setStrDependentName(jsonObjectNotification.getString("dependent_name"));

                            System.out.println("YEDAPAT  : "+Config.jsonObject.getString("dependent_name"));
                            dependentModel.setStrName(Config.jsonObject.getString("dependent_name"));

                            products=Config.jsonObject.getString("dependent_name");

                            dependentModels.add(dependentModel);
//                            }
                            //String[] producto = new String[]{};
                            List<String> producto= new ArrayList<String>();
                            producto.add(products);

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreatingTaskActivity.this,android.R.layout.select_dialog_item,producto);
                            //Getting the instance of AutoCompleteTextView
                            //AutoCompleteTextView actv= (AutoCompleteTextView)findViewById(R.id.inputSearch);
                            inputSearch.setThreshold(1);//will start working from first character
                            inputSearch.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView*/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
    }

    public void uploadData() {

        jsonObjectAct = new JSONObject();
        jsonObjectActCarla = new JSONObject();
        jsonObjectCarla = Config.jsonObject;
        jsonArrayVideos = new JSONArray();
        jsonArrayFeedbacks = new JSONArray();
        jsonArrayImages = new JSONArray();
        jsonArrayFeatures = new JSONArray();

        if (utils.isConnectingToInternet()) {

            for(DependentModel dependentModelFind: dependentModels){
                if (dependentModelFind.getStrDependentID().equalsIgnoreCase(valSearch)) {
                    sDependentModel = dependentModelFind;
                }
            }



            storageService = new StorageService(CreatingTaskActivity.this);

                    try {
                        jsonObjectAct.put("activity_message", editTextTitle.getText().toString());
                        jsonObjectAct.put("activity_name", editTextTitle.getText().toString());
                        jsonObjectAct.put("dependent_name", inputSearch.getText().toString());
                        jsonObjectAct.put("activity_date", dateTime.getText().toString());
                        jsonObjectAct.put("dependent_id","");
                        jsonObjectAct.put("provider_id","");
                        jsonObjectAct.put("activity_done_date","");
                        jsonObjectAct.put("status","");

                        jsonObjectAct.put("videos",jsonArrayVideos);
                        jsonObjectAct.put("feedbacks",jsonArrayFeedbacks);
                        jsonObjectAct.put("images",jsonArrayImages);


                        storageService.findDocsByKeyValue(Config.collectionServices, "service_name", "Medical Checkup", new AsyncApp42ServiceApi.App42StorageServiceListener() {
                            @Override
                            public void onDocumentInserted(Storage response) {

                            }

                            @Override
                            public void onUpdateDocSuccess(Storage response) {

                            }

                            @Override
                            public void onFindDocSuccess(Storage response) throws JSONException {
                                if(response.getJsonDocList().size()>0){

                                    Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);

                                    String strDocument = jsonDocument.getJsonDoc();

                                    try{
                                        Config.jsonObject = new JSONObject(strDocument);
                                        jsonArrayFeatures=Config.jsonObject.getJSONArray("features");
                                        System.out.println("Bangalore : "+jsonArrayFeatures);

                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
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


                        jsonObjectAct.put("service_id","");
                        jsonObjectAct.put("service_name",spinner.getItemAtPosition(spinner.getSelectedItemPosition()));
                        jsonObjectAct.put("customer_id","");
                        asyncService = new AsyncApp42ServiceApi(CreatingTaskActivity.this);


                        asyncService.insertJSONDoc(Config.dbName, Config.collectionActivity, jsonObjectAct, new AsyncApp42ServiceApi.App42StorageServiceListener() {
                            @Override
                            public void onDocumentInserted(Storage response) {
                                System.out.println("Inserted");
                            }

                            @Override
                            public void onUpdateDocSuccess(Storage response) {

                            }

                            @Override
                            public void onFindDocSuccess(Storage response) {

                            }

                            @Override
                            public void onInsertionFailed(App42Exception ex) {
                                ex.printStackTrace();
                                System.out.println("Insertion Failed");
                            }

                            @Override
                            public void onFindDocFailed(App42Exception ex) {

                            }

                            @Override
                            public void onUpdateDocFailed(App42Exception ex) {

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }




                 else {
            utils.toast(2, 2, getString(R.string.warning_internet));
        }


                System.out.println("WIKIPEDIA : "+jsonObjectCarla);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();


            if (jsonObjectAct != null) {

                storageService.findDocsByIdApp42CallBack(Config.providerModel.getStrProviderId(), Config.collectionProvider, new App42CallBack() {
                    @Override
                    public void onSuccess(Object o) {

                        if (o != null) {

                            final Storage findObj = (Storage) o;


                            if (inputSearch.getText().toString().equals(sDependentModel.getStrName())) {
                            try {

                                responseJSONDoc = new JSONObject(findObj.
                                        getJsonDocList().get(0).getJsonDoc());
                                System.out.println("responseJSONDoc: "+responseJSONDoc);


                               // if (responseJSONDoc.has("activities")) {
//                                    JSONArray dependantsA = responseJSONDoc.
//                                            getJSONArray("activities");
                               // }
                            } catch (JSONException jSe) {
                                jSe.printStackTrace();
                                progressDialog.dismiss();
                            }
                            }else{
                                utils.toast(2, 2, "Enter Valid Client Name");
                            }


                            Utils.log(responseJSONDoc.toString(), " onj 1 ");


                            if (utils.isConnectingToInternet()) {//TODO check activity added

                                System.out.println("Here is Object on App42");

                                asyncService.insertJSONDoc(Config.dbName, Config.collectionProvider, responseJSONDoc, new AsyncApp42ServiceApi.App42StorageServiceListener() {
                                    @Override
                                    public void onDocumentInserted(Storage response) {
                                        if (response != null) {

                                            if (response.getJsonDocList().size() > 0) {

                                                Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);

                                                final String strCarlaJsonId = response.getJsonDocList().get(0).getDocId();

                                                String strDocument = jsonDocument.getJsonDoc();

                                                try {
                                                    responseJSONDocCarla = new JSONObject(strDocument);

                                                    System.out.println(responseJSONDocCarla);


                                                    // JSONArray dependantsA = responseJSONDocCarla.getJSONArray("dependents");

                                                    //TODO

                                                    //products = new String[jsonArrayNotifications.length()];
                                                    // for (int i = 0; i < dependantsA.length(); i++) {

                                                    // JSONObject jsonObjectDependent = dependantsA.getJSONObject(i);
                                                    JSONObject jsonObjectDependent = responseJSONDocCarla;
                                                    //System.out.println("EMPTY POT : "+(inputSearch.getText().toString().equalsIgnoreCase(jsonObjectDependent.getString("dependent_name"))));
                                                    /*
                                                    if (inputSearch.getText().toString().equalsIgnoreCase(jsonObjectDependent.getString("dependent_name"))) {
                                                        System.out.println("HASATAY KARAT");
                                                        String test= jsonObjectDependent.getString("dependent_id");

                                                        JSONArray jsonArrayActivities = jsonObjectDependent.getJSONArray("activities");

                                                        jsonArrayActivities.put(jsonObjectAct);
                                                        System.out.println("bye "+jsonArrayActivities);
                                                    }*/
                                                    // }

                                                    Utils.log(responseJSONDocCarla.toString(), " onj 2 ");

                                                    storageService.updateDocs(responseJSONDocCarla, strCarlaJsonId, Config.collectionProvider, new App42CallBack() {
                                                        @Override
                                                        public void onSuccess(Object o) {
                                                            System.out.println("Success");

                                                            if (o != null) {
                                                                System.out.println("Success2");
                                                                Intent intent = new Intent(CreatingTaskActivity.this, DashboardActivity.class);
                                                                if (progressDialog.isShowing())
                                                                    progressDialog.dismiss();
                                                                Config.intSelectedMenu = Config.intDashboardScreen;
                                                                startActivity(intent);
                                                                finish();

                                                            } else {
                                                                System.out.println("Success3");
                                                                if (progressDialog.isShowing())
                                                                    progressDialog.dismiss();
                                                                utils.toast(2, 2, getString(R.string.warning_internet));
                                                            }
                                                        }

                                                        @Override
                                                        public void onException(Exception e) {
                                                            System.out.println("FAILURE");
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
                                        }
                                    }

                                    @Override
                                    public void onUpdateDocSuccess(Storage response) {

                                    }

                                    @Override
                                    public void onFindDocSuccess(Storage response) throws JSONException {



                                    }

                                    @Override
                                    public void onInsertionFailed(App42Exception ex) {

                                    }

                                    @Override
                                    public void onFindDocFailed(App42Exception ex) {
                                        System.out.println("Failed");
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
                                /*storageService.insertDocs(responseJSONDoc, new AsyncApp42ServiceApi.App42StorageServiceListener() {
                                    @Override
                                    public void onDocumentInserted(Storage response) {

                                    }
                                    @Override
                                    public void onUpdateDocSuccess(Storage response) {

                                    }
                                    @Override
                                    public void onFindDocSuccess(Storage response) {

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
                                });*/


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


            } else utils.toast(2, 2, getString(R.string.error));

        }


}
