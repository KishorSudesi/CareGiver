package com.hdfc.caregiver.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdfc.adapters.ClientAdapter;
import com.hdfc.caregiver.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.models.ClientModel;
import com.hdfc.models.CustomerModel;
import com.hdfc.models.DependentModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ClientFragment extends Fragment {

    public static ExpandableListView expListView;
    private static List<CustomerModel> listDataHeader = new ArrayList<>();
    private static HashMap<CustomerModel, List<DependentModel>> listDataChild = new HashMap<>();
    public TextView textViewEmpty;
    ImageView profileImg;
    private ClientAdapter listAdapter;
    private Utils utils;

    private ProgressDialog progressDialog;

    public static ClientFragment newInstance() {
        ClientFragment fragment = new ClientFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clients, container, false);

        textViewEmpty = (TextView) view.findViewById(android.R.id.empty);

        utils = new Utils(getActivity());
        profileImg = (ImageView)view.findViewById(R.id.imageClients);
        expListView = (ExpandableListView) view.findViewById(R.id.listExp);

        progressDialog = new ProgressDialog(getActivity());

        //listAdapter = new ClientAdapter();

        prepareListData();

        listAdapter = new ClientAdapter(getActivity(), listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);

        return view;
    }


    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        if (expListView != null) {

            listDataHeader.clear();
            listDataChild.clear();

            for (ClientModel clientModel : Config.clientModels) {
                listDataHeader.add(clientModel.getCustomerModel());
                Utils.log(String.valueOf(clientModel.getCustomerModel().getStrAddress()), " 1 ");
                listDataChild.put(clientModel.getCustomerModel(),clientModel.getDependentModels());
            }
            //listAdapter.notifyDataSetChanged();
        }

      /*  if (progressDialog.isShowing())
            progressDialog.dismiss();*/
    }

    @Override
    public void onResume() {
        super.onResume();

       /* progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        prepareListData();*/

    }
}
