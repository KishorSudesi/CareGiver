package com.hdfc.caregiver.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hdfc.adapters.RatingsAdapter;
import com.hdfc.caregiver.MyProfileActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.Config;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class RatingsFragment extends Fragment {

    public static Bitmap bitmap = null;

    //private static int intWhichScreen;
    //private static Handler backgroundThreadHandler;
    //private static ProgressDialog mProgress = null;
    //private Utils utils;

    public RatingsFragment(){
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
      /*  mytask = (ImageView) view.findViewById(R.id.buttonMyTasks);
        clients = (ImageView) view.findViewById(R.id.buttonClients);
        feedback = (ImageView) view.findViewById(R.id.buttonFeedback);*/
        ListView listratings = (ListView) view.findViewById(R.id.listViewRatings);
        TextView textViewName = (TextView) view.findViewById(R.id.name);
        TextView textViewEmpty = (TextView) view.findViewById(android.R.id.empty);
        Button logout = (Button) view.findViewById(R.id.buttonlogout);

        //  layout = (LinearLayout) view.findViewById(R.id.linearLayoutRatings);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

        ImageView imageProfilePic = (ImageView) view.findViewById(R.id.img);
       /* mProgress = new ProgressDialog(getActivity());
        utils = new Utils(getActivity());*/
        //intWhichScreen = Config.intRatingsScreen;

        if (Config.providerModel.getStrName() != null)
            textViewName.setText(Config.providerModel.getStrName());

       /* backgroundThreadHandler = new BackgroundThreadHandler();
        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();*/


        //myprofile = (RelativeLayout) view.findViewById(R.id.relativelayoutRatings);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProfileActivity.class);
                //intent.putExtra("WHICH_SCREEN", intWhichScreen);
                startActivity(intent);
            }
        });

        //int iRatings = 0;

        if (Config.iRatingCount > 0)

            ratingBar.setRating((float) (Config.iRatings / Config.iRatingCount));

        /*int i = iRatings;
        layout.removeAllViews();

        int j, k;

        for (j = 0; j < i; j++) {

            ImageView imageView = new ImageView(getActivity());

            imageView.setPadding(0, 0, 10, 0);
            imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.star_gold));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            layout.addView(imageView);
        }

        //Utils.log(String.valueOf(i + " ! " + j), " R ");

        for (k = i; k < 5; k++) {

            ImageView imageView = new ImageView(getActivity());

            imageView.setPadding(0, 0, 10, 0);
            imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                    R.mipmap.star_grey));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            layout.addView(imageView);
        }
*/
        //Utils.log(String.valueOf(i + " ! " + k), " R ");

        File file = null;

        String strImage;

        try {
            file = new File(Config.providerModel.getStrImgPath());

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (file != null && file.exists()) {
            strImage = Config.providerModel.getStrImgPath();
        } else {
            strImage = Config.providerModel.getStrImgUrl();
        }

        Glide.with(getActivity())
                .load(strImage)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(getActivity()))
                .placeholder(R.drawable.person_icon)
                .crossFade()
                .into(imageProfilePic);

        RatingsAdapter ratingsAdapter = new RatingsAdapter(getContext(), Config.feedBackModels);
        listratings.setAdapter(ratingsAdapter);
        listratings.setEmptyView(textViewEmpty);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    /*private class BackgroundThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();

            try {

                if (imageProfilePic != null && bitmap != null)
                    imageProfilePic.setImageBitmap(bitmap);


            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
    }

    private class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {
                File f = utils.getInternalFileImages(Config.providerModel.getStrProviderId());

                if(f!=null&&f.exists())
                    bitmap = utils.getBitmapFromFile(f.getAbsolutePath(), Config.intWidth,
                            Config.intHeight);

            } catch (Exception e) {
                e.printStackTrace();
            }

            *//*try {
                if (activityFeedBackModels != null) {
                    for (int i = 0; i < activityFeedBackModels.size(); i++) {
                        Utils.log(activityFeedBackModels.get(i).getStrFeedBackByUrl(), " URL ");
                        utils.loadImageFromWeb(activityFeedBackModels.get(i).getStrFeedBackBy(),
                                activityFeedBackModels.get(i).getStrFeedBackByUrl());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*//*
            backgroundThreadHandler.sendEmptyMessage(0);
        }
    }*/
}
