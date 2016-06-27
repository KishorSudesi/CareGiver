package com.hdfc.caregiver.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.hdfc.caregiver.DashboardActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.Config;
import com.hdfc.libs.AppUtils;
import com.hdfc.libs.Utils;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    //private static int iMenuLength=2;

    public static String strDate, _strDate;
    public static String strStartDate, strEndDate;
    //private Button buttonActivity, buttonTask;
    private TextView textView;
    private Utils utils;
    private AppUtils appUtils;
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+
            // date.getTime();
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.

            strDate = Utils.writeFormatDateDB.format(date);
            //_strDate = Utils.writeFormatDateDB.format(date);

            strEndDate = utils.convertDateToStringQuery(utils.convertStringToDateQuery(strDate + "T23:59:59.999"));
            strStartDate = utils.convertDateToStringQuery(utils.convertStringToDateQuery(strDate + "T00:00:00.000"));

            textView.setText(Utils.writeFormatDate.format(date));

            if (utils.isConnectingToInternet()) {

                DashboardActivity.loadingPanel.setVisibility(View.VISIBLE);

                Config.dependentIds.clear();
                Config.strActivityIds.clear();
                Config.customerIds.clear();

                Config.dependentIdsAdded.clear();
                Config.customerIdsAdded.clear();

                Config.activityModels.clear();
                Config.dependentModels.clear();
                Config.customerModels.clear();

                //Config.clientModels.clear();

                Config.feedBackModels.clear();
                Config.milestoneModels.clear();

                // Calendar calendar = Calendar.getInstance();

          /*  int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH); // Note: zero based!
            int day = calendar.get(Calendar.DAY_OF_MONTH);*/

                //Date date = calendar.getTime();
/*
                strEndDate = strDate + "T23:59:59.999Z";
                strStartDate = strDate + "T00:00:00.000Z";*/

                strDate = Utils.writeFormatDate.format(date);

                Config.intSelectedMenu = Config.intDashboardScreen;

                appUtils.fetchActivities();

            } else {
                utils.toast(2, 2, getString(R.string.warning_internet));
            }

            //
        }

        @Override
        public void onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }

    };

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
/*
        buttonActivity = (Button) view.findViewById(R.id.buttonActivity);
        buttonTask = (Button) view.findViewById(R.id.buttonTask);*/

        appUtils = new AppUtils(getActivity());
        utils = new Utils(getActivity());

      /*  buttonActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked(0);
            }
        });

        buttonTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked(1);
            }
        });*/

        textView = (TextView) view.findViewById(R.id.textViewDate);
        ImageView calender = (ImageView) view.findViewById(R.id.calendar);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlideDateTimePicker.Builder(getActivity().getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        });

        textView.setText(strDate);

        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlideDateTimePicker.Builder(getActivity().getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        });
/*
        ActivityFragment fragment = ActivityFragment.newInstance();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutDashboard, fragment);
        transaction.addToBackStack(null);
        transaction.commit();*/

        buttonClicked(0);

        return view;
    }

    private void buttonClicked(int iPosition) {

        try {

          /*  buttonActivity.setBackgroundResource(R.drawable.button_back_trans);
            buttonActivity.setTextColor(getActivity().getResources().getColor(R.color.colorAccentDark));*/

           /* buttonTask.setBackgroundResource(R.drawable.button_back_trans);
            buttonTask.setTextColor(getActivity().getResources().getColor(R.color.colorAccentDark));*/

            if (iPosition == 0) {
               /* buttonActivity.setBackgroundResource(R.drawable.one_side_border);
                buttonActivity.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));*/

                ActivityFragment fragment = ActivityFragment.newInstance();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayoutDashboard, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

                ActivityFragment.activityModels = Config.activityModels;
                ActivityFragment.mAdapter.notifyDataSetChanged();
            }

            if (iPosition == 1) {
                /*buttonTask.setBackgroundResource(R.drawable.one_side_border);
                buttonTask.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));*/

                MileStoneFragment fragment = MileStoneFragment.newInstance();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayoutDashboard, fragment);
                transaction.addToBackStack(null);
                transaction.commit();


                MileStoneFragment.milestoneModels = Config.milestoneModels;
                MileStoneFragment.mAdapter.notifyDataSetChanged();

                /*DashboardActivity.loadingPanel.setVisibility(View.VISIBLE);
                appUtils.fetchMileStone(DashboardActivity.loadingPanel);*/
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
