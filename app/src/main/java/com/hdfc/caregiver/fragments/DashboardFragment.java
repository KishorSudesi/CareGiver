package com.hdfc.caregiver.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hdfc.caregiver.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    //private static int iMenuLength=2;

    private static Button buttonActivity, buttonTask;

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

        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);

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

        buttonClicked(0);


        SimpleActivityFragment fragment = SimpleActivityFragment.newInstance();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutDashboard, fragment);
        transaction.addToBackStack(null);
        transaction.commit();


        return view;
    }

    public void buttonClicked(int iPosition) {

        try {

            buttonActivity.setBackgroundResource(R.drawable.button_back_trans);
            buttonActivity.setTextColor(getActivity().getResources().getColor(R.color.colorAccentDark));

            buttonTask.setBackgroundResource(R.drawable.button_back_trans);
            buttonTask.setTextColor(getActivity().getResources().getColor(R.color.colorAccentDark));

            if (iPosition == 0) {
                buttonActivity.setBackgroundResource(R.drawable.one_side_border);
                buttonActivity.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
            }

            if (iPosition == 1) {
                buttonTask.setBackgroundResource(R.drawable.one_side_border);
                buttonTask.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}