package com.hdfc.caregiver.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hdfc.app42service.StorageService;
import com.hdfc.caregiver.CreatingTaskActivity;
import com.hdfc.caregiver.FeatureActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class SimpleActivityFragment extends Fragment implements SlideAndDragListView.OnListItemLongClickListener,
        SlideAndDragListView.OnDragListener, SlideAndDragListView.OnSlideListener,
         SlideAndDragListView.OnMenuItemClickListener, SlideAndDragListView.OnListItemClickListener,
        SlideAndDragListView.OnItemDeleteListener {

    private static final String TAG = "";
    private static final int PICK_CONTACT = 979;
    private static StorageService storageService;
    public ArrayList<ActivityModel> activityModels = Config.activityModels;
    private MultiBitmapLoader multiBitmapLoader;
    private Menu mMenu;
    private SlideAndDragListView<ApplicationInfo> mListView;
    private Utils utils;
    public BaseAdapter mAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return activityModels.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomViewHolder cvh;

            if (convertView == null) {
                cvh = new CustomViewHolder();

                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.my_tasks_item, null);
                cvh.imageTiming = (TextView) convertView.findViewById(R.id.imageTiming);
                cvh.textMessage = (TextView) convertView.findViewById(R.id.task_message);
                cvh.textSubject = (TextView) convertView.findViewById(R.id.task_subject);
                cvh.textTime = (TextView) convertView.findViewById(R.id.task_time);
                cvh.imagePerson = (ImageView) convertView.findViewById(R.id.imagePerson);
                convertView.setTag(cvh);

            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }

            if (Config.activityModels.size() > 0) {

                ActivityModel activityModel = Config.activityModels.get(position);

                //System.out.println("Message value: "+activityModel.getStrActivityMessage());
                String strMessage = activityModel.getStrActivityDesc();

                if (strMessage != null && strMessage.length() > 20)
                    strMessage = activityModel.getStrActivityDesc().substring(0, 18) + "..";

                String strName = activityModel.getStrActivityName();

                if (strName.length() > 20)
                    strName = activityModel.getStrActivityName().substring(0, 18) + "..";

                cvh.textSubject.setText(strName);

                cvh.textMessage.setText(strMessage);

                cvh.textTime.setText(utils.formatDate(activityModel.getStrActivityDate()));

                if (!activityModel.getStrActivityStatus().equalsIgnoreCase("completed")) {
                    cvh.imageTiming.setBackgroundResource(R.drawable.circle);
                    cvh.imageTiming.setText(utils.formatDateTime(activityModel.getStrActivityDate()));
                    cvh.imageTiming.setTextColor(getResources().getColor(R.color.gray_holo_dark));
                }else{
                    cvh.imageTiming.setBackgroundResource(R.drawable.done);
                    cvh.imageTiming.setTextColor(getResources().getColor(R.color.colorWhite));
                    cvh.imageTiming.setText("");
                }
               // cvh.imagePerson.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.mrs_hungal_circle2));

                //System.out.println("Rahul : "+activityModel.getStrActivityDependentName());
                File fileImage = Utils.createFileInternal("images/" + utils.replaceSpace(activityModel.getStrDependentID().trim()));

                if (fileImage.exists()) {
                    String filename = fileImage.getAbsolutePath();
                    multiBitmapLoader.loadBitmap(filename, cvh.imagePerson);
                } else {
                    cvh.imagePerson.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.person_icon));
                }
            }
            return convertView;
        }

        class CustomViewHolder {
            public TextView imageTiming;
            public TextView textSubject;
            public ImageView imagePerson;
            public TextView textMessage;
            public TextView textTime;
        }
    };
    // public ActivityModel activityModel = Config.activityModel;
    private JSONObject responseJSONDoc, responseProvider;
    private JSONObject responseJSONDocCarla;
    private ProgressDialog progressDialog;

    public static SimpleActivityFragment newInstance() {
        SimpleActivityFragment fragment = new SimpleActivityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_simple_activity, container, false);

       /* System.out.println("TOTAL COUNT  : "+Config.activityModels.size());
        for(int i = 0 ; i < Config.activityModels.size(); i++){
            System.out.println("Value : "+Config.activityModel.getStrActivityMessage());
        }*/

        initMenu();
        progressDialog = new ProgressDialog(getActivity());
        utils = new Utils(getActivity());
        multiBitmapLoader = new MultiBitmapLoader(getActivity());

        ImageButton add = (ImageButton) view.findViewById(R.id.add_button);

        TextView textViewEmpty = (TextView) view.findViewById(android.R.id.empty);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreatingTaskActivity.class);
                Config.intSelectedMenu=Config.intDashboardScreen;
                startActivity(intent);
            }
        });



            mListView = (SlideAndDragListView)view.findViewById(R.id.listViewEdit);

            mListView.setMenu(mMenu);
            mListView.setAdapter(mAdapter);
            mListView.setEmptyView(textViewEmpty);
            mListView.setOnListItemLongClickListener(this);
        //mListView.setOnDragListener(this, mAppList);
            //mListView.setOnListItemClickListener(this);
            mListView.setOnSlideListener(this);
            mListView.setOnMenuItemClickListener(this);
            mListView.setOnItemDeleteListener(this);

           /* mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                  *//*  intent.putExtra("WHICH_SCREEN",intWhichScreen);
                    System.out.println("Data contained in model class is : "+activityModels.get(position)+" Parent item is "+parent.getItemAtPosition(position));
                    intent.putExtra("WHICH_SCREEN",activityModels.get(position));
                    startActivity(intent);*//*

                }
            });*/

          //  parseData();

       /* StorageService storageService = new StorageService(getActivity());
        HashMap<String, String> otherMetaHeaders = new HashMap<String, String>();
        otherMetaHeaders.put("orderByDescending", "activity_done_date");// Use orderByDescending
        storageService.setOtherMetaHeaders(otherMetaHeaders);
*/
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        //refreshData();
    }

    private void refreshData() {
        if (utils.isConnectingToInternet()) {
            progressDialog  =new ProgressDialog(getContext());

            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();

            StorageService storageService = new StorageService(getActivity());

            storageService.findDocsByKeyValue(Config.collectionActivity, "dependent_id",
                    Config.providerModel.getStrProviderId(), new AsyncApp42ServiceApi.App42StorageServiceListener() {
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

                                    String strDocument = jsonDocument.getJsonDoc();

                                    try {
                                        Config.jsonObject = new JSONObject(strDocument);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //parseDataPM();
                                        progressDialog.dismiss();
                                }

                            } else {

                                if(progressDialog.isShowing())
                                    progressDialog.dismiss();

                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onInsertionFailed(App42Exception ex) {
                        }

                        @Override
                        public void onFindDocFailed(App42Exception ex) {
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();

                            //utils.toast(2, 2, getString(R.string.warning_internet));
                        }

                        @Override
                        public void onUpdateDocFailed(App42Exception ex) {
                        }
                    });
        }
    }

   /* public void parseDataPM(){

 //       threadHandler = new ThreadHandler();
        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();

    }
*/
    /*public class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            if(progressDialog.isShowing())
                progressDialog.dismiss();

                try {
                activityModels.clear();

                    JSONObject jsonObject = new JSONObject();

                        ActivityModel activityModel = new ActivityModel();
                        activityModel.setStrActivityMessage(Config.jsonObject.getString("activity_message"));
                        activityModel.setStrActivityName(Config.jsonObject.getString("activity_name"));
                        activityModel.setStrActivityDesc(Config.jsonObject.getString("activity_description"));
      //                  activityModel.setiServiceId(Config.jsonObject.getInt("service_id"));
                        //activityModel.setStrActivityDate(Config.jsonObject.getString("activity_date"));
                        activityModel.setStrActivityDoneDate(Config.jsonObject.getString("activity_done_date"));

                        activityModel.setStrActivityStatus(Config.jsonObject.getString("status"));

                    System.out.println("KKKKKKK "+activityModel.toString());
                      //  activityModel.setStrActivityDependentName(Config.jsonObject.getString("dependent_name"));

                        String featuresDone[];
                        String features[];

                        if (jsonObject.has("features")) {
                            features = new String[jsonObject.getJSONArray("features").length()];

                            for (int i = 0; i < jsonObject.getJSONArray("features").length(); i++) {
                                features[i] = jsonObject.getJSONArray("features").getString(i);
                            }

                            activityModel.setFeatures(features);
                        }

                        if (jsonObject.has("features_done")) {
                            featuresDone = new String[jsonObject.getJSONArray("features_done").length()];

                            for (int i = 0; i < jsonObject.getJSONArray("features_done").length(); i++) {
                                featuresDone[i] = jsonObject.getJSONArray("features_done").getString(i);
                            }

                            activityModel.setDoneFeatures(featuresDone);
                        }


//                        activityModel.setStrCustomerEmail(jsonObject.getString("customer_email"));
                        activityModels.add(activityModel);

                mListView.setAdapter(mAdapter);



            }catch (Exception e){
                e.printStackTrace();
            }
            //
        }
    }*/

    public void updateData() {

        storageService = new StorageService(getActivity());
        storageService.findDocsByIdApp42CallBack(Config.providerModel.getStrProviderId(), Config.collectionProvider, new App42CallBack() {
            @Override
            public void onSuccess(Object o) {

                if (o != null) {

                    final Storage findObj = (Storage) o;
                    try {
                        responseJSONDoc = new JSONObject(findObj.getJsonDocList().get(0).getJsonDoc());
                        if (responseJSONDoc.has("activities")) {
                            JSONArray dependantsA = responseJSONDoc.
                                    getJSONArray("activities");

                            for (int i = 0; i < dependantsA.length(); i++) {

                                JSONObject jsonObjectActivity = dependantsA.getJSONObject(i);

                                jsonObjectActivity.put("status", "upcoming");
                            }
                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onException(Exception e) {

            }
        });


        if (utils.isConnectingToInternet()) {//TODO check activity added

            storageService.findDocsByIdApp42CallBack(Config.providerModel.getStrProviderId(), Config.collectionProvider, new App42CallBack() {
                @Override
                public void onSuccess(Object o) {

                    if (o != null) {

                        final Storage findObj = (Storage) o;

                        try {
                            responseJSONDoc = new JSONObject(findObj.getJsonDocList().get(0).getJsonDoc());
                            if (responseJSONDoc.has("activities")) {
                                JSONArray dependantsA = responseJSONDoc.
                                        getJSONArray("activities");

                                for (int i = 0; i < dependantsA.length(); i++) {

                                    JSONObject jsonObjectActivity = dependantsA.getJSONObject(i);
                                    jsonObjectActivity.put("status", "completed");

                                        /*JSONArray jsonArrayFeatures = jsonObjectActivity.getJSONArray("features_done");

                                        jsonArrayFeatures.put(jsonArrayFeaturesDone);

                                        JSONArray jsonArrayImages = jsonObjectActivity.getJSONArray("images");

                                        jsonArrayImages.put(jsonArrayImagesAdded);*/

                                }

                                //dependantsA.put(jsonObjectActCarla);

                            }
                        } catch (JSONException jSe) {
                            jSe.printStackTrace();
                            progressDialog.dismiss();
                        }

                        //Utils.log(responseJSONDoc.toString(), " onj 1 ");

                        if (utils.isConnectingToInternet()) {//TODO check activity added

                            storageService.updateDocs(responseJSONDoc, Config.providerModel.getStrProviderId(), Config.collectionProvider, new App42CallBack() {
                                @Override
                                public void onSuccess(Object o) {

                                    Config.jsonObject = responseJSONDoc;

                                    if (o != null) {

                                        storageService.findDocsByKeyValue(Config.collectionCustomer, "customer_email", "manmurugan@yahoo.co.in", new AsyncApp42ServiceApi.App42StorageServiceListener() {
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

                                                                    if (jsonObjectActivities.getString("dependent_name").equalsIgnoreCase("bala")) {

                                                                        if (jsonObjectActivities.has("activities")) {

                                                                            JSONArray dependantsActivities = jsonObjectActivities.
                                                                                    getJSONArray("activities");

                                                                            for (int j = 0; j < dependantsActivities.length(); j++) {

                                                                                JSONObject jsonObjectActivity = dependantsActivities.getJSONObject(j);


                                                                                jsonObjectActivity.put("status", "completed");


                                                                                    /*JSONArray jsonArrayFeatures = jsonObjectActivity.getJSONArray("features_done");

                                                                                    jsonArrayFeatures.put(jsonArrayFeaturesDone);

                                                                                    JSONArray jsonArrayImages = jsonObjectActivity.getJSONArray("images");

                                                                                    jsonArrayImages.put(jsonArrayImagesAdded);*/

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

                                                                        if (progressDialog.isShowing())
                                                                            progressDialog.dismiss();

                                                                        utils.toast(2, 2, getString(R.string.activity_closed));

                                                                        Config.intSelectedMenu = Config.intDashboardScreen;


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
  /*  public void parseData() {

        StorageService storageService = new StorageService(getActivity());

        storageService.findDocsByKeyValue(Config.collectionActivity, "provider_id", Config.jsonDocId, new AsyncApp42ServiceApi.App42StorageServiceListener() {
            @Override
            public void onDocumentInserted(Storage response) {

            }

            @Override
            public void onUpdateDocSuccess(Storage response) {

            }

            @Override
            public void onFindDocSuccess(Storage response) {
                Utils.log(String.valueOf(response.getJsonDocList().size()), " count ");

                Storage  storage  = (Storage )response;
                //This will return JSONObject list, however since Object Id is unique, list will only have one object
                ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();
                if (response.getJsonDocList().size() > 0) {

                    Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);

                    String strDocument = jsonDocument.getJsonDoc();


                    try {

                        Config.jsonObject = new JSONObject(strDocument);
                        storage = (Storage) response;
                        ArrayList<Storage.JSONDocument> fileSize = storage.getJsonDocList();

                        activityModels.clear();

                        ActivityModel activityModel = new ActivityModel();

                        System.out.println("Size he "+jsonDocList.size());
                        JSONArray jsonArray = Config.jsonObject.getJSONArray("activities");
                        for (int i = 0 ; i < jsonDocList.size() ; i++) {
                            JSONObject jObject = jsonArray.getJSONObject(i);
                           // activityModel.setStrActivityMessage(jObject.getString("activity_message"));
                            activityModel.setStrActivityMessage(Config.jsonObject.getString("activity_message"));
                            System.out.println("your name is : "+jObject.getString("activity_name"));
                            //  activityModel.setStrActivityName(jObject.getString("activity_name"));
                            activityModel.setStrActivityDesc(Config.jsonObject.getString("activity_description"));
                            //   activityModel.setiServiceId(Config.jsonObject.getInt("service_id"));
                            //  activityModel.setStrActivityDate(Config.jsonObject.getString("activity_date"));
                            activityModel.setStrActivityDoneDate(jObject.getString("activity_done_date"));
                            activityModel.setStrActivityStatus(Config.jsonObject.getString("status"));
                            activityModel.setStrActivityDependentName(jObject.getString("dependent_name"));
                        }

                                String featuresDone[];
                                String features[];

                                if (Config.jsonObject.has("features")) {
                                    features = new String[Config.jsonObject.getJSONArray("features").length()];

                                    for (int i = 0; i < Config.jsonObject.getJSONArray("features").length(); i++) {
                                        features[i] = Config.jsonObject.getJSONArray("features").getString(i);
                                    }

                                    activityModel.setFeatures(features);
                                }

                                if (Config.jsonObject.has("features_done")) {
                                    featuresDone = new String[Config.jsonObject.getJSONArray("features_done").length()];

                                    for (int i = 0; i < Config.jsonObject.getJSONArray("features_done").length(); i++) {
                                        featuresDone[i] = Config.jsonObject.getJSONArray("features_done").getString(i);
                                    }

                                    activityModel.setDoneFeatures(featuresDone);
                                }


                              //  activityModel.setStrCustomerEmail(Config.jsonObject.getString("customer_email"));
                                activityModels.add(activityModel);



                        mListView.setAdapter(mAdapter);

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
    }*/

    public void initMenu() {

        mMenu = new Menu(new ColorDrawable(Color.LTGRAY), true);

        //swipe right

        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img))
                .setBackground(getActivity().getResources().getDrawable(R.color.polygonViewCircleStrokeColor))
                .setIcon(getActivity().getResources().getDrawable(R.drawable.pen))
                .build());

        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width) * 2)
                .setBackground(getActivity().getResources().getDrawable(R.color.polygonViewCircleStrokeColor))
                .setText("Done")
                .setTextColor(Color.WHITE)
                .setTextSize((int) getResources().getDimension(R.dimen.txt_size))
                .build());

       /* mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width) * 2)
                .setBackground(getActivity().getResources().getDrawable(R.color.polygonViewCircleStrokeColor))
                .setText("Undo")
                .setTextColor(Color.WHITE)
                .setTextSize((int) getResources().getDimension(R.dimen.txt_size))
                .build());*/

        /*mMenu.addItem(new MenuItem.Builder()
                .setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img)+50 )
                .setBackground(getActivity().getResources().getDrawable(R.color.polygonViewCircleStrokeColor))
                .setIcon(getActivity().getResources().getDrawable(R.drawable.circle))
                .build());*/

        //swipe left

        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img)-30)
                .setBackground(getActivity().getResources().getDrawable(R.color.polygonViewCircleStrokeColor))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(getResources().getDrawable(R.mipmap.map))
                .build());

        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img) - 30)
                .setBackground(getActivity().getResources().getDrawable(R.color.polygonViewCircleStrokeColor))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(getResources().getDrawable(R.mipmap.message))
                .build());

        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img)-30)
                .setBackground(getActivity().getResources().getDrawable(R.color.polygonViewCircleStrokeColor))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(getResources().getDrawable(R.mipmap.call1))
                .build());

       /* mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img) - 30)
                .setBackground(getActivity().getResources().getDrawable(R.color.polygonViewCircleStrokeColor))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(getResources().getDrawable(R.drawable.person_icon))
                .build());*/
    }

    @Override
    public void onListItemLongClick(View view, int position) {
        Log.i(TAG, "onListItemLongClick   " + position);
    }

    @Override
    public void onDragViewStart(int position) {
        Log.i(TAG, "onDragViewStart   " + position);
    }

    @Override
    public void onDragViewMoving(int position) {
        Log.i("yuyidong", "onDragViewMoving   " + position);
    }

    @Override
    public void onDragViewDown(int position) {
        Log.i(TAG, "onDragViewDown   " + position);
    }

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {
        Log.i(TAG, "onSlideOpen   " + position);
    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {

        Log.i(TAG, "onSlideClose   " + position);
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        //todo add string
        switch (direction) {
            case MenuItem.DIRECTION_LEFT:
                switch (buttonPosition) {
                    case 0:
                        if (activityModels.size() > 0) {
                            Bundle args = new Bundle();
                            args.putSerializable("ACTIVITY", activityModels.get(itemPosition));

                            if (!activityModels.get(itemPosition).getStrActivityStatus().equalsIgnoreCase("completed")) {
                                ActivityModel obj = activityModels.get(itemPosition);
                                Intent intent = new Intent(getActivity(), FeatureActivity.class);
                                args.putSerializable("ACTIVITY", obj);
                                intent.putExtras(args);
                                startActivity(intent);
                            } else {
                                utils.toast(2, 2, "Activity is Closed");
                            }
                        }
                        return Menu.ITEM_SCROLL_BACK;
                    case 1:
                        if (activityModels.size() > 0) {
                            Bundle args = new Bundle();
                            args.putSerializable("ACTIVITY", activityModels.get(itemPosition));

                            if (!activityModels.get(itemPosition).getStrActivityStatus().equalsIgnoreCase("completed")) {
                                /*ActivityModel obj = activityModels.get(itemPosition);
                                Intent intent = new Intent(getActivity(), FeatureActivity.class);
                                args.putSerializable("ACTIVITY", obj);
                                intent.putExtras(args);
                                startActivity(intent);*/
                            } else {
                                utils.toast(2, 2, "Activity is Closed");
                            }
                        }
                        return Menu.ITEM_SCROLL_BACK;
                    case 2:

                       /* if (activityModels.size() > 0) {
                            Bundle args = new Bundle();
                            args.putSerializable("ACTIVITY", activityModels.get(itemPosition));
                            if (activityModels.get(itemPosition).getStrActivityStatus().equalsIgnoreCase("completed")) {
                                //  updateData();
                            } else {
                                utils.toast(2, 2, "Error. Try Again!!!");
                            }
                        }*/

                        return Menu.ITEM_SCROLL_BACK;
                    case 3:
                        return Menu.ITEM_NOTHING;
                }
                break;
            case MenuItem.DIRECTION_RIGHT:
                ActivityModel activityModel = activityModels.get(itemPosition);
                switch (buttonPosition) {
                    case 0:

                        int iPosition3 = Config.dependentIds.indexOf(activityModel.getStrDependentID());
                        String strNo4 = Config.dependentModels.get(iPosition3).getStrAddress();
                        Toast.makeText(getContext(), strNo4, Toast.LENGTH_LONG).show();
                        return Menu.ITEM_SCROLL_BACK;
                    case 1:
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);

                        int iPosition = Config.dependentIds.indexOf(activityModel.getStrDependentID());
                        String strNo2 = Config.dependentModels.get(iPosition).getStrContacts();

                        sendIntent.putExtra("sms_body", activityModel != null ? activityModel.getStrActivityName() : "Activity Name");
                        sendIntent.putExtra("address", activityModel != null ? strNo2 : "0000000000");
                        sendIntent.setType("vnd.android-dir/mms-sms");
                        startActivity(sendIntent);
                        return Menu.ITEM_SCROLL_BACK;
                    case 2:

                        int iPosition2 = Config.dependentIds.indexOf(activityModel.getStrDependentID());
                        String strNo3 = Config.dependentModels.get(iPosition2).getStrContacts();

                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        String strNo1 = "tel:" + String.valueOf(activityModel != null ? strNo3 : "0000000000");
                        callIntent.setData(Uri.parse(strNo1));
                        startActivity(callIntent);
                        return Menu.ITEM_SCROLL_BACK;
                    case 3:
                        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, PICK_CONTACT);

                        return Menu.ITEM_SCROLL_BACK;
                }


        }
        return Menu.ITEM_NOTHING;
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case PICK_CONTACT:
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        System.out.println(name);
                        // TODO Whatever you want to do with the selected contact name.
                    }
                }
                break;
        }
    }

    @Override
    public void onItemDelete(View view, int position) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(View v, int position) {

    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
//            threadHandler.sendEmptyMessage(0);
        }
    }

}
