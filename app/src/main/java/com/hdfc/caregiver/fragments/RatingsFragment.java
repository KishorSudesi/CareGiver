package com.hdfc.caregiver.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hdfc.adapters.RatingsAdapter;
import com.hdfc.caregiver.MyProfileActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.models.RatingModel;
import com.hdfc.views.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class RatingsFragment extends Fragment {

    ImageView mytask,clients,feedback;
    RoundedImageView imageProfilePic;
    RelativeLayout myprofile;
    ListView listratings;
    public static Bitmap bitmap = null;
    private static int intWhichScreen;
    static List<RatingModel> activityFeedBackModels;
    private static Handler backgroundThreadHandler;
    public static RatingsAdapter ratingsAdapter;
    public TextView textViewName, textViewEmpty;
    private static ProgressDialog mProgress = null;
    private static Libs libs;


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

        imageProfilePic = (RoundedImageView)view.findViewById(R.id.img);
        mProgress = new ProgressDialog(getActivity());
       // Bundle b = getActivity().getIntent().getExtras();
        libs = new Libs(getActivity());
        intWhichScreen = Config.intRatingsScreen;//b.getInt("WHICH_SCREEN", Config.intRatingsScreen);

        if(Config.myProfileModel.getStrName()!=null)
            textViewName.setText(Config.myProfileModel.getStrName());

        ratingsAdapter = new RatingsAdapter(getContext(),activityFeedBackModels );
        listratings.setAdapter(ratingsAdapter);
        listratings.setEmptyView(textViewEmpty);

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

        try {

            activityFeedBackModels.clear();

            if (Config.jsonObject.has("feedbacks")) {

                JSONArray jsonArrayFeedback = Config.jsonObject.getJSONArray("feedbacks");

                for (int k = 0; k < jsonArrayFeedback.length(); k++) {
                    JSONObject jsonObjectFeedback = jsonArrayFeedback.getJSONObject(k);
                    RatingModel ratingModel = new RatingModel();
                    ratingModel.setMessage(jsonObjectFeedback.getString("feedback_message"));
                    ratingModel.setStrAuthorName(jsonObjectFeedback.getString("feedback_by"));

                    activityFeedBackModels.add(ratingModel);
                }
            }

            ratingsAdapter.notifyDataSetChanged();

            BackgroundThread backgroundThread = new BackgroundThread();
            backgroundThread.start();
            backgroundThreadHandler = new BackgroundThreadHandler();

            mProgress.setMessage("Loading...");
            mProgress.show();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();
            if (imageProfilePic != null && bitmap != null)
                imageProfilePic.setImageBitmap(bitmap);
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

                File f = libs.getInternalFileImages(Config.strCustomerImageName);

                if(f!=null&&f.exists())
                    bitmap = libs.getBitmapFromFile(f.getAbsolutePath(), Config.intWidth, Config.intHeight);

                backgroundThreadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
