package com.hdfc.libs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UploadService;
import com.hdfc.app42service.UserService;
import com.hdfc.caregiver.DashboardActivity;
import com.hdfc.caregiver.LoginActivity;
import com.hdfc.caregiver.R;
import com.hdfc.caregiver.fragments.SimpleActivityFragment;
import com.hdfc.config.Config;
import com.hdfc.models.ActivityImageModel;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.ActivityVideoModel;
import com.hdfc.models.DependentModel;
import com.hdfc.models.FeedBackModel;
import com.hdfc.models.FileModel;
import com.hdfc.models.ImageModel;
import com.hdfc.models.ProviderModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.upload.Upload;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Admin on 4/25/2016.
 */
public class Utils {
    Libs libs;
    private static Context _ctxt;
    private static StorageService storageService;
    private static ProgressDialog progressDialog;
    public static int iDependentCount = 0;

    public Utils(Context context) {
        _ctxt = context;
        libs = new Libs(_ctxt);
        progressDialog = new ProgressDialog(_ctxt);
    }

   public void fetchProviders(final ProgressDialog progressDialog){

                    StorageService storageService = new StorageService(_ctxt);

                    storageService.findDocsByKeyValue(Config.collectionProvider, "provider_email", "carla1@gmail.com", new AsyncApp42ServiceApi.App42StorageServiceListener() {
                        @Override
                        public void onDocumentInserted(Storage response) {

                        }

                        @Override
                        public void onUpdateDocSuccess(Storage response) {

                        }

                        @Override
                        public void onFindDocSuccess(Storage response) {

                            Libs.log(String.valueOf(response.getJsonDocList().size()), " count ");

                            if (response.getJsonDocList().size() > 0) {

                                Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);

                                String strDocument = jsonDocument.getJsonDoc();

                                Config.jsonDocId = jsonDocument.getDocId();

                                try {
                                    Config.jsonObject = new JSONObject(strDocument);

                                    createProviderModel();


//provider details:get id-->fetch all activities
                                    // UploadService uploadService = new UploadService(LoginActivity.this);

                                        /*uploadService.getAllFilesByUser(Config.strUserName, new App42CallBack() {
                                            public void onSuccess(Object response) {

                                                Libs.log(response.toString(), "");

                                                Upload upload = (Upload) response;
                                                ArrayList<Upload.File> fileList = upload.getFileList();

                                                if (fileList.size() > 0) {

                                                    for (int i = 0; i < fileList.size(); i++) {
                                                      //  Config.fileModels.add(
                                                                new FileModel(fileList.get(i).getName()
                                                                        , fileList.get(i).getUrl(),
                                                                        fileList.get(i).getType());

                                                    }



                                                } else
                                                    libs.toast(2, 2, getString(R.string.error));

                                            }

                                            public void onException(Exception ex) {
                                                progressDialog.dismiss();
                                                libs.toast(2, 2, getString(R.string.error));
                                                Libs.log(ex.getMessage(), " ");
                                                //ex.printStackTrace();
                                            }
                                        });*/
                                    UploadService uploadService = new UploadService(_ctxt);

                                    uploadService.getAllFilesByUser(LoginActivity.userName, new App42CallBack() {
                                        public void onSuccess(Object response) {

                                            Libs.log(response.toString(), " Files Response ");

                                            Upload upload = (Upload) response;
                                            ArrayList<Upload.File> fileList = upload.getFileList();

                                            if (fileList.size() > 0) {

                                                for (int i = 0; i < fileList.size(); i++) {
                                                    Config.fileModels.add(
                                                            new FileModel(fileList.get(i).getName()
                                                                    , fileList.get(i).getUrl(),
                                                                    fileList.get(i).getType()));
                                                }
                                            }

                                            progressDialog.dismiss();

                                            libs.toast(1, 1, _ctxt.getString(R.string.success_login));
                                            Intent dashboardIntent = new Intent(_ctxt, DashboardActivity.class);
                                            //dashboardIntent.putExtra("WHICH_SCREEN", Config.intSimpleActivityScreen);
                                            Config.intSelectedMenu=Config.intDashboardScreen;
                                            //  Config.boolIsLoggedIn = true;
                                            _ctxt.startActivity(dashboardIntent);
                                            ((Activity) _ctxt).finish();
                                        }

                                        public void onException(Exception ex) {
                                            progressDialog.dismiss();
                                            libs.toast(2, 2, _ctxt.getString(R.string.error_load_images));
                                        }
                                    });

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else libs.toast(2, 2, _ctxt.getString(R.string.error));

                        }

                        @Override
                        public void onInsertionFailed(App42Exception ex) {

                        }

                        @Override
                        public void onFindDocFailed(App42Exception ex) {
                            progressDialog.dismiss();
                            libs.toast(2, 2, _ctxt.getString(R.string.error));
                            Libs.log(ex.getMessage(), " 321 ");
                        }

                        @Override
                        public void onUpdateDocFailed(App42Exception ex) {

                        }
                    });
   }

    public void createProviderModel() {
        try{
            if(Config.jsonObject.has("provider_email")) {
                //(String email, String number, String strAddress, String place)
                Config.providerModel = new ProviderModel(
                        Config.jsonDocId,
                        Config.jsonObject.getString("provider_email"),
                        Config.jsonObject.getString("provider_contact_no"),
                        Config.jsonObject.getString("provider_address"),
                        Config.jsonObject.getString("provider_name"),
                        Config.jsonObject.getString("provider_profile_url")
                );
            }
            fetchActivities();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _ctxt.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    //if the counter!=size
    public void fetchDependents(final ProgressDialog progressDialog){

        //if (iDependentCount < Config.dependentIds.size()) {

            if(isConnectingToInternet()){
                StorageService storageService = new StorageService(_ctxt);

                Query query = new QueryBuilder().build("_id", Config.dependentIds,
                        QueryBuilder.Operator.INLIST);

                storageService.findDocsByQuery(Config.collectionDependent, query,
                        new App42CallBack() {

                            @Override
                            public void onSuccess(Object o) {
                                try {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    if (o != null) {


                                        Storage storage = (Storage) o;

                                        if (storage.getJsonDocList().size() > 0) {

                                            for (int i = 0; i < storage.getJsonDocList().size(); i++) {

                                                Storage.JSONDocument jsonDocument = storage.
                                                        getJsonDocList().get(i);

                                                String strDocument = jsonDocument.getJsonDoc();
                                                String strDependentDocId = jsonDocument.
                                                        getDocId();
                                                createDependentModel(strDependentDocId, strDocument);
                                            }
                                        }
                                    } else {
                                        libs.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    libs.toast(2, 2, _ctxt.getString(R.string.error));
                                }

                            }

                            @Override
                            public void onException(Exception e) {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                try {
                                    //Utils.log(e.getMessage(), " Response Failure");

                                    if (e != null) {
                                        libs.toast(2, 2, _ctxt.getString(R.string.error));
                                    } else {
                                        libs.toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }

                        });
            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                libs.toast(2, 2, _ctxt.getString(R.string.warning_internet));
            }
        /*} else {

            if (iDependentCount == Config.dependentIds.size()) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }*/
    }

    public void createDependentModel(String strDocumentId, String strDocument) {
        try {

            JSONObject jsonObjectDependent = new JSONObject(strDocument);

            if (jsonObjectDependent.has("dependent_name")) {

                DependentModel dependentModel = new DependentModel(
                        jsonObjectDependent.getString("dependent_name"),
                        jsonObjectDependent.getString("dependent_illness"),
                        jsonObjectDependent.getString("dependent_relation"),
                        jsonObjectDependent.getString("dependent_profile_url"),
                        jsonObjectDependent.getString("dependent_address"),
                        jsonObjectDependent.getString("dependent_notes"),
                        jsonObjectDependent.getString("dependent_contact_no"),
                        jsonObjectDependent.getString("dependent_email"),
                        jsonObjectDependent.getString("dependent_age"),
                        strDocumentId);

                System.out.println("Laugh of Loud : "+dependentModel.getClass().toString());
                System.out.println("LOL "+dependentModel.getStrDependentName());
                if (!Config.dependentIdsAdded.contains(strDocumentId)) {

                    Config.dependentIdsAdded.add(strDocumentId);

                    Config.dependentModels.add(dependentModel);

                    Config.fileModels.add(new FileModel(strDocumentId,
                            jsonObjectDependent.getString("dependent_profile_url"), "IMAGE"));


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

        public void fetchActivities(){
        storageService = new StorageService(_ctxt);
        storageService.findDocsByKeyValue(Config.collectionActivity, "provider_id", Config.providerModel.getProviderId(), new AsyncApp42ServiceApi.App42StorageServiceListener() {
            @Override
            public void onDocumentInserted(Storage response) {

            }

            @Override
            public void onUpdateDocSuccess(Storage response) {

            }

            @Override
            public void onFindDocSuccess(Storage response) throws JSONException {

                if(response != null){

                    ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();

                    for(int i = 0 ; i < jsonDocList.size() ; i++ ){

                        Storage.JSONDocument jsonDocument = jsonDocList.get(i);

                        String strDocumentId = jsonDocument.getDocId();

                        String strServices = jsonDocument.getJsonDoc();

                        try {

                            JSONObject jsonObjectActivities = new JSONObject(strServices);

                            if (jsonObjectActivities.has("dependent_id"))
                                createActivityModel(strDocumentId, jsonObjectActivities);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    fetchDependents(progressDialog);
                } else {

                    libs.toast(2, 2, _ctxt.getString(R.string.warning_internet));

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
    public void createActivityModel(String strDocumentId, JSONObject jsonObject) {

        try {
            JSONArray jsonArr = jsonObject.getJSONArray("features");

            String[] arr = new String[jsonArr.length()];

            for (int j = 0; j < jsonArr.length(); j++)
                arr[j] = jsonArr.getString(j);

            JSONArray jsonArrDoneFeatures = jsonObject.getJSONArray("features_done");

            String[] arrDoneFeatures = new String[jsonArrDoneFeatures.length()];

            for(int k=0;k<jsonArrDoneFeatures.length();k++)
                arrDoneFeatures[k] = jsonArrDoneFeatures.getString(k);

            JSONArray jsonArrayImages = jsonObject.getJSONArray("images");

            for (int i = 0 ; i < jsonArrayImages.length() ; i++){
                JSONObject jsonObjectImages = jsonArrayImages.getJSONObject(i);
            }
            ArrayList<ActivityImageModel> activityImageModels = new ArrayList<>();
            ArrayList<FeedBackModel> feedBackModels = new ArrayList<>();
            ArrayList<ActivityVideoModel> videoModels = new ArrayList<>();

            for(int k=0;k<jsonArrDoneFeatures.length();k++)
                arrDoneFeatures[k] = jsonArrDoneFeatures.getString(k);

            ActivityModel activityModel = new ActivityModel();
            activityModel.setDoneFeatures(arr);
            activityModel.setDoneFeatures(arrDoneFeatures);
            activityModel.setiServiceId(jsonObject.getString("service_id"));
            activityModel.setStrServiceDesc(jsonObject.getString("service_desc"));
            activityModel.setStrServiceName(jsonObject.getString("service_name"));
            activityModel.setStrActivityId(strDocumentId);
            activityModel.setStrActivityName(jsonObject.getString("activity_name"));
            activityModel.setStrActivityMessage(jsonObject.getString("activity_message"));
            activityModel.setStrActivityDesc(jsonObject.getString("activity_description"));
            activityModel.setStrActivityDate(jsonObject.getString("activity_date"));
            activityModel.setStrActivityDoneDate(jsonObject.getString("activity_done_date"));
            activityModel.setStrDependentId(jsonObject.getString("dependent_id"));


            if (jsonObject.has("feedbacks")) {

                JSONArray jsonArrayFeedback = jsonObject.
                        getJSONArray("feedbacks");

                for (int k = 0; k < jsonArrayFeedback.length(); k++) {

                    JSONObject jsonObjectFeedback =
                            jsonArrayFeedback.getJSONObject(k);

                    FeedBackModel feedBackModel = new FeedBackModel(
                            jsonObjectFeedback.getString("feedback_message"),
                            jsonObjectFeedback.getString("feedback_by"),
                            jsonObjectFeedback.getInt("feedback_rating"),
                            jsonObjectFeedback.getBoolean("feedback_report"),
                            jsonObjectFeedback.getString("feedback_time"),
                            jsonObjectFeedback.getString("feedback_by_type"));

                    feedBackModels.add(feedBackModel);
                }
                activityModel.setFeedBackModels(feedBackModels);
            }

            if (jsonObject.has("videos")) {

                JSONArray jsonArrayVideos = jsonObject.
                        getJSONArray("videos");

                for (int k = 0; k < jsonArrayVideos.length(); k++) {

                    JSONObject jsonObjectVideo = jsonArrayVideos.
                            getJSONObject(k);

                    ActivityVideoModel videoModel = new ActivityVideoModel(
                            jsonObjectVideo.getString("video_name"),
                            jsonObjectVideo.getString("video_url"),
                            jsonObjectVideo.getString("video_description"),
                            jsonObjectVideo.getString("video_taken"));

                    Config.fileModels.add(new FileModel(jsonObjectVideo.getString("video_name"),
                            jsonObjectVideo.getString("video_url"), "VIDEO"));

                    videoModels.add(videoModel);
                }
                activityModel.setVideoModels(videoModels);
            }

            if (jsonObject.has("images")) {

                JSONArray jsonArrayVideos = jsonObject.
                        getJSONArray("images");

                for (int k = 0; k < jsonArrayVideos.length(); k++) {

                    JSONObject jsonObjectImage = jsonArrayVideos.
                            getJSONObject(k);

                    ActivityImageModel imageModel = new ActivityImageModel(
                            jsonObjectImage.getString("image_name"),
                            jsonObjectImage.getString("image_url"),
                            jsonObjectImage.getString("image_description"),
                            jsonObjectImage.getString("image_taken"));

                    Config.fileModels.add(new FileModel(jsonObjectImage.getString("image_name"),
                            jsonObjectImage.getString("image_url"), "IMAGE"));

                    activityImageModels.add(imageModel);
                }
                activityModel.setImageModels(activityImageModels);
            }

            Config.activityModels.add(activityModel);

            if(!Config.dependentIds.contains(jsonObject.getString("dependent_id")))
                Config.dependentIds.add(jsonObject.getString("dependent_id"));

           /* Config.activityModel = activityModel;
            ArrayList<ActivityModel> activityModelArrayList = new ArrayList<>();
            activityModelArrayList.add(activityModel);*/

            //Config.activityModels = activityModelArrayList;
//define a constant variable in config and access it wherever needed
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}