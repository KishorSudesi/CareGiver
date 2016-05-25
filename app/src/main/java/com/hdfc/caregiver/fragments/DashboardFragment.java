package com.hdfc.caregiver.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.hdfc.caregiver.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    //private static int iMenuLength=2;

    public static String strDate;
    public static String strStartDate, strEndDate;
    private Button buttonActivity, buttonTask;
    private TextView textView;
    //private AppUtils appUtils;
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            // selectedDateTime = date.getDate()+"-"+date.getMonth()+"-"+date.getYear()+" "+
            // date.getTime();
            // Do something with the date. This Date object contains
            // the date and time that the user has selected.

            strDate = Utils.writeFormatDateDB.format(date);

            strEndDate = strDate + "T23:59:59.999+0000";
            strStartDate = strDate + "T00:00:00.000+0000";

            //String _strDate = Utils.writeFormatDateDB.format(date);

            textView.setText(Utils.writeFormatDate.format(date));
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

        buttonActivity = (Button) view.findViewById(R.id.buttonActivity);
        buttonTask = (Button) view.findViewById(R.id.buttonTask);

        //appUtils = new AppUtils(getActivity());

        buttonActivity.setOnClickListener(new View.OnClickListener() {
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
        });

        textView = (TextView) view.findViewById(R.id.textViewDate);
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
/*
        SimpleActivityFragment fragment = SimpleActivityFragment.newInstance();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutDashboard, fragment);
        transaction.addToBackStack(null);
        transaction.commit();*/

        buttonClicked(0);

        return view;
    }

    private void buttonClicked(int iPosition) {

        try {

            buttonActivity.setBackgroundResource(R.drawable.button_back_trans);
            buttonActivity.setTextColor(getActivity().getResources().getColor(R.color.colorAccentDark));

            buttonTask.setBackgroundResource(R.drawable.button_back_trans);
            buttonTask.setTextColor(getActivity().getResources().getColor(R.color.colorAccentDark));

            if (iPosition == 0) {
                buttonActivity.setBackgroundResource(R.drawable.one_side_border);
                buttonActivity.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));

                SimpleActivityFragment fragment = SimpleActivityFragment.newInstance();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayoutDashboard, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

                SimpleActivityFragment.activityModels = Config.activityModels;
                SimpleActivityFragment.mAdapter.notifyDataSetChanged();
            }

            if (iPosition == 1) {
                buttonTask.setBackgroundResource(R.drawable.one_side_border);
                buttonTask.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));

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
