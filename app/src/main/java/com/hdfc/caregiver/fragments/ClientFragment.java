package com.hdfc.caregiver.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hdfc.adapters.ClientsAdapter;
import com.hdfc.caregiver.ClientProfileActivity;
import com.hdfc.caregiver.DashboardActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.models.ClientModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ClientFragment extends Fragment {

    ImageView profileImg;
    ListView listViewClients;

    public static ClientsAdapter clients_adapter;
    public TextView textViewEmpty;
    private static Handler backgroundThreadHandler;
    private Libs libs;
    private static ProgressDialog mProgress = null;

    public ClientFragment() {
        // Required empty public constructor

    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clients, container, false);

        textViewEmpty = (TextView) view.findViewById(android.R.id.empty);

        libs = new Libs(getActivity());
        profileImg = (ImageView)view.findViewById(R.id.imageClients);

        mProgress = new ProgressDialog(getActivity());

        listViewClients = (ListView)view.findViewById(R.id.listViewClients);

        clients_adapter = new ClientsAdapter(getContext(), DashboardActivity.activitiesModelArrayList);
        listViewClients.setAdapter(clients_adapter);
        listViewClients.setEmptyView(textViewEmpty);

         listViewClients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             public void onItemClick(AdapterView<?> parent, View view, int position, long id)
             {
                 ClientModel obj = (ClientModel)parent.getAdapter().getItem(position);
                 Intent intent  = new Intent(getActivity(),ClientProfileActivity.class);
                 Bundle bundle = new Bundle();
                 bundle.putSerializable("Client", obj);
                 intent.putExtras(bundle);
                 startActivity(intent);
             }
         });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
