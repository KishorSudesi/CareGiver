package com.hdfc.caregiver.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hdfc.adapters.RatingsAdapter;
import com.hdfc.app42service.StorageService;
import com.hdfc.caregiver.MyProfileActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Libs;
import com.hdfc.models.FeedBackModel;
import com.hdfc.views.RoundedImageView;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class RatingsFragment extends Fragment {

    public static Bitmap bitmap = null;
    public static RatingsAdapter ratingsAdapter;
    static List<FeedBackModel> activityFeedBackModels;
    private static int intWhichScreen;
    private static Handler backgroundThreadHandler;
    private static ProgressDialog mProgress = null;
    private static Libs libs;
    private static double iRatings = 0;
    private static LinearLayout layout;
    public TextView textViewName, textViewEmpty;
    ImageView mytask, clients, feedback;
    RoundedImageView imageProfilePic;
    RelativeLayout myprofile;
    ListView listratings;

    public RatingsFragment(){
        activityFeedBackModels = new ArrayList<>();
    }

    public static RatingsFragment newInstance() {
        RatingsFragment fragment = new RatingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_ratings, container, false);
        mytask = (ImageView) view.findViewById(R.id.buttonMyTasks);
        clients = (ImageView) view.findViewById(R.id.buttonClients);
        feedback = (ImageView) view.findViewById(R.id.buttonFeedback);
        listratings = (ListView) view.findViewById(R.id.listViewRatings);
        textViewName = (TextView) view.findViewById(R.id.name);
        textViewEmpty = (TextView) view.findViewById(android.R.id.empty);

        layout = (LinearLayout) view.findViewById(R.id.linearLayoutRatings);

        imageProfilePic = (RoundedImageView)view.findViewById(R.id.img);
        mProgress = new ProgressDialog(getActivity());
        libs = new Libs(getActivity());
        intWhichScreen = Config.intRatingsScreen;

        if (Config.myProfileModel.getStrName() != null)
            textViewName.setText(Config.myProfileModel.getStrName());

        myprofile = (RelativeLayout) view.findViewById(R.id.relativelayoutRatings);

        myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProfileActivity.class);
                intent.putExtra("WHICH_SCREEN", intWhichScreen);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundThreadHandler = new BackgroundThreadHandler();
        StorageService storageService = new StorageService(getActivity());

        storageService.findDocsByKeyValue(Config.collectionActivity, "provider_id", Config.jsonDocId,
                new AsyncApp42ServiceApi.App42StorageServiceListener() {
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
                    try {
                        Config.jsonObject = new JSONObject(strDocument);
                        iRatings = 0;

                        activityFeedBackModels.clear();

                        if (Config.jsonObject.has("feedbacks")) {

                            JSONArray jsonArrayFeedback = Config.jsonObject.getJSONArray("feedbacks");

                            for (int k = 0; k < jsonArrayFeedback.length(); k++) {
                                JSONObject jsonObjectFeedback = jsonArrayFeedback.getJSONObject(k);
                                FeedBackModel feedBackModel = new FeedBackModel(
                                        jsonObjectFeedback.getString("feedback_message"),
                                        jsonObjectFeedback.getString("feedback_by"),
                                        jsonObjectFeedback.getInt("feedback_rating"),
                                        jsonObjectFeedback.getBoolean("feedback_report"),
                                        jsonObjectFeedback.getString("feedback_time"),
                                        jsonObjectFeedback.getString("feedback_by_type"));

                                iRatings += jsonObjectFeedback.getInt("feedback_rating");

                                activityFeedBackModels.add(feedBackModel);
                            }

                            if (jsonArrayFeedback.length() > 0)
                                iRatings = Libs.round(iRatings / jsonArrayFeedback.length(), 2);
                        }

                        //ratingsAdapter.notifyDataSetChanged();

                        backgroundThreadHandler = new BackgroundThreadHandler();
                        BackgroundThread backgroundThread = new BackgroundThread();
                        backgroundThread.start();

                        mProgress.setMessage(getString(R.string.loading));
                        mProgress.show();

                    } catch (JSONException e) {
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


    public class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();

            if (imageProfilePic != null && bitmap != null)
                imageProfilePic.setImageBitmap(bitmap);

            try {
                int i = (int) iRatings;
                layout.removeAllViews();

                int j, k;

                for (j = 0; j < i; j++) {

                    ImageView imageView = new ImageView(getActivity());

                    imageView.setPadding(0, 0, 10, 0);
                    imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                            R.mipmap.stars_white));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    layout.addView(imageView);
                }

                Libs.log(String.valueOf(i + " ! " + j), " R ");

                for (k = i; k < 5; k++) {

                    ImageView imageView = new ImageView(getActivity());

                    imageView.setPadding(0, 0, 10, 0);
                    imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                            R.mipmap.star_grey));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    layout.addView(imageView);
                }

                Libs.log(String.valueOf(i + " ! " + k), " R ");

                ratingsAdapter = new RatingsAdapter(getContext(), activityFeedBackModels);
                listratings.setAdapter(ratingsAdapter);
                listratings.setEmptyView(textViewEmpty);

            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {
                File f = libs.getInternalFileImages(Config.strCustomerImageName);

                if(f!=null&&f.exists())
                    bitmap = libs.getBitmapFromFile(f.getAbsolutePath(), Config.intWidth,
                            Config.intHeight);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (activityFeedBackModels != null) {
                    for (int i = 0; i < activityFeedBackModels.size(); i++) {
                        Libs.log(activityFeedBackModels.get(i).getStrFeedBackByUrl(), " URL ");
                        libs.loadImageFromWeb(activityFeedBackModels.get(i).getStrFeedBackBy(),
                                activityFeedBackModels.get(i).getStrFeedBackByUrl());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            backgroundThreadHandler.sendEmptyMessage(0);
        }
    }
}
