package com.hdfc.caregiver.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdfc.adapters.ClientAdapter;
import com.hdfc.caregiver.ClientProfileActivity;
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
/*
    public ClientFragment() {
        // Required empty public constructor
    }*/

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

        progressDialog = new ProgressDialog(getActivity());

//        listViewClients = (ListView)view.findViewById(R.id.listViewClients);

         /*listViewClients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             public void onItemClick(AdapterView<?> parent, View view, int position, long id)
             {
                 ClientModel obj = (ClientModel)parent.getAdapter().getItem(position);
                 Intent intent  = new Intent(getActivity(),ClientProfileActivity.class);
                 Bundle bundle = new Bundle();
                 bundle.putSerializable("Client", obj);
                 intent.putExtras(bundle);
                 startActivity(intent);
             }
         });*/
        //expandable list

        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        expListView = (ExpandableListView) view.findViewById(R.id.listExp);

        // preparing list data
        listAdapter = new ClientAdapter(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                CustomerModel obj = (CustomerModel) parent.getAdapter().getItem(groupPosition);
                Config.customerModel = obj;
                ClientFragment fragment = new ClientFragment();
                Intent intent = new Intent(getActivity(), ClientProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("Client1", true);
                bundle.putSerializable("Client", Config.customerModel);
                fragment.setArguments(bundle);
                startActivity(intent);
                return true;
            }
        });

       /* expListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClientModel obj = (ClientModel) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), ClientProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Client", obj);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
*/
        prepareListData();

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
            //listAdapter = new ClientAdapter(getActivity(), listDataHeader, listDataChild);
            listAdapter.notifyDataSetChanged();
        }

        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
