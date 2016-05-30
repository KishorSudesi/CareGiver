package com.hdfc.caregiver.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.hdfc.adapters.ClientAdapter;
import com.hdfc.caregiver.R;
import com.hdfc.config.Config;
import com.hdfc.models.ClientModel;
import com.hdfc.models.CustomerModel;
import com.hdfc.models.DependentModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ClientFragment extends Fragment {

    private static ExpandableListView expListView;
    private static List<CustomerModel> listDataHeader = new ArrayList<>();
    private static HashMap<CustomerModel, List<DependentModel>> listDataChild = new HashMap<>();
    private static ClientAdapter listAdapter;

    public static ClientFragment newInstance() {
        ClientFragment fragment = new ClientFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static void prepareListData() {
        try {
            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();

            if (expListView != null) {

                listDataHeader.clear();
                listDataChild.clear();


                if (Config.clientModels != null && Config.clientModels.size() > 0) {

                    for (ClientModel clientModel : Config.clientModels) {
                        listDataHeader.add(clientModel.getCustomerModel());
                        //Utils.log(String.valueOf(clientModel.getCustomerModel().getStrAddress()), " 1 ");
                        listDataChild.put(clientModel.getCustomerModel(), clientModel.getDependentModels());
                    }
                    listAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clients, container, false);

        TextView textViewEmpty = (TextView) view.findViewById(android.R.id.empty);
        expListView = (ExpandableListView) view.findViewById(R.id.listExp);
        prepareListData();

        listAdapter = new ClientAdapter(getActivity(), listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);
        expListView.setEmptyView(textViewEmpty);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
