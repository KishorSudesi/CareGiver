package com.hdfc.caregiver.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
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
    private AppUtils appUtils;
    private RelativeLayout loadingPanel;
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+
            // date.getTime();
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.

            strDate = Utils.writeFormatDateDB.format(date);
            _strDate = Utils.writeFormatDateDB.format(date);

            strEndDate = Utils.convertDateToStringQuery(Utils.convertStringToDateQuery(strDate +
                    "T23:59:59.999"));
            strStartDate = Utils.convertDateToStringQuery(Utils.convertStringToDateQuery(strDate +
                    "T00:00:00.000"));

            textView.setText(Utils.writeFormatDate.format(date));

            loadingPanel.setVisibility(View.VISIBLE);

            Config.dependentIds.clear();
            Config.customerIds.clear();

            Config.dependentIdsAdded.clear();
            Config.customerIdsAdded.clear();


            Config.dependentModels.clear();
            Config.customerModels.clear();

            //Config.clientModels.clear();

            Config.feedBackModels.clear();

            strDate = Utils.writeFormatDate.format(date);

            Config.intSelectedMenu = Config.intDashboardScreen;

            String strStartDate = _strDate + " 00:00:00.000";
            String strEndDate = _strDate + " 24:00:00.000";

            appUtils.createActivityModel(strStartDate, strEndDate);
            ActivityFragment.activityModels = Config.activityModels;
            ActivityFragment.mAdapter.notifyDataSetChanged();

            loadingPanel.setVisibility(View.GONE);

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

        loadingPanel = (RelativeLayout) getActivity().findViewById(R.id.loadingPanel);

        appUtils = new AppUtils(getActivity());
        //Utils utils = new Utils(getActivity());

        LinearLayout layoutDate = (LinearLayout)view.findViewById(R.id.linearDate);

        textView = (TextView) view.findViewById(R.id.textViewDate);
        textView.setText(strDate);

        layoutDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlideDateTimePicker.Builder(getActivity().getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        });

        buttonClicked(0);

        return view;
    }

    private void buttonClicked(int iPosition) {

        try {

            if (iPosition == 0) {

                ActivityFragment fragment = ActivityFragment.newInstance();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().
                        beginTransaction();
                transaction.replace(R.id.frameLayoutDashboard, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

                ActivityFragment.activityModels = Config.activityModels;
                ActivityFragment.mAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
