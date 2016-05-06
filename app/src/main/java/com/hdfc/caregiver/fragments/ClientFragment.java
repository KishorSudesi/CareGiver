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
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hdfc.adapters.ClientsAdapter;
import com.hdfc.adapters.ExpandableListAdapter;
import com.hdfc.adapters.ExpandableListCustomer;
import com.hdfc.app42service.StorageService;
import com.hdfc.caregiver.ClientProfileActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Utils;
import com.hdfc.models.ClientModel;
import com.hdfc.models.CustomerModel;
import com.hdfc.models.DependentModel;
import com.hdfc.models.FileModel;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ClientFragment extends Fragment {

    public static List<ClientModel> activitiesModelArrayList;
    public static ClientsAdapter clients_adapter;
    private static Handler threadHandler;
    private static Handler ThreadHandler;
    private static ProgressDialog mProgress = null;
    ExpandableListCustomer listAdapter;
    ExpandableListView expListView;
    List<CustomerModel> listDataHeader;
    HashMap<CustomerModel, List<DependentModel>> listDataChild;

    public TextView textViewEmpty;
    ImageView profileImg;
    ListView listViewClients;
    private Utils utils;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clients, container, false);

        textViewEmpty = (TextView) view.findViewById(android.R.id.empty);

        utils = new Utils(getActivity());
        profileImg = (ImageView)view.findViewById(R.id.imageClients);

        mProgress = new ProgressDialog(getActivity());

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
        expListView = (ExpandableListView) view.findViewById(R.id.listExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListCustomer(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        return view;
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<CustomerModel>();
        listDataChild = new HashMap<CustomerModel, List<DependentModel>>();

        if (expListView != null) {

            listDataHeader.clear();
            listDataChild.clear();

            for (ClientModel clientModel : Config.clientModels) {
                listDataHeader.add(clientModel.getCustomerModel());
                listDataChild.put(clientModel.getCustomerModel(),clientModel.getDependentModels());
            }

            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        StorageService storageService = new StorageService(getActivity());

        storageService.findDocsByKeyValue(Config.collectionDependent, "dependent_name", "komu", new AsyncApp42ServiceApi.App42StorageServiceListener() {
            @Override
            public void onDocumentInserted(Storage response) {

            }

            @Override
            public void onUpdateDocSuccess(Storage response) {

            }

            @Override
            public void onFindDocSuccess(Storage response) {
                Utils.log(String.valueOf(response.getJsonDocList().size()), " count ");
                if (response.getJsonDocList().size() > 0) {

                    Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);

                    String strDocument = jsonDocument.getJsonDoc();

                    try {
                        Config.jsonObject = new JSONObject(strDocument);
                        activitiesModelArrayList = new ArrayList<>();
                        activitiesModelArrayList.clear();



                                ClientModel clientsModel = new ClientModel();
                              /*  clientsModel.setAge( Config.jsonObject.getString("dependent_age"));
                                clientsModel.setAddress( Config.jsonObject.getString("dependent_address"));
                                clientsModel.setName( Config.jsonObject.getString("dependent_name"));
                                clientsModel.setPremium( Config.jsonObject.getString("dependent_notes"));
                                clientsModel.setProblem( Config.jsonObject.getString("dependent_illness"));
                                clientsModel.setStrMobile( Config.jsonObject.getString("dependent_contact_no"));
                                clientsModel.setStrClientImageUrl( Config.jsonObject.getString("dependent_profile_url"));*/

                                activitiesModelArrayList.add(clientsModel);


                        //

                        Utils.log(String.valueOf(activitiesModelArrayList.size()), " 1 ");
                        for (int i = 0; i < activitiesModelArrayList.size(); i++) {
                            ClientModel clientModel = activitiesModelArrayList.get(i);

                           /* if (clientModel.getStrClientImageUrl() != null && !clientModel.getStrClientImageUrl().equalsIgnoreCase("")) {

                                utils.loadImageFromWeb(clientModel.getName(), clientModel.getStrClientImageUrl());

                            }*/
                        }

                        threadHandler = new ThreadHandler();
                        BackgroundThread backgroundThread = new BackgroundThread();
                        backgroundThread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onInsertionFailed(App42Exception ex) {

            }

            @Override
            public void onFindDocFailed(App42Exception ex) {

            }

            @Override
            public void onUpdateDocFailed(App42Exception ex) {

            }
        });
    }


    public class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            if(mProgress.isShowing())
                mProgress.dismiss();

            /*if (intWhichScreen == Config.intSimpleActivityScreen) {
                gotoSimpleActivity();
            }*/
            clients_adapter = new ClientsAdapter(getContext(), activitiesModelArrayList);
            listViewClients.setAdapter(clients_adapter);
            listViewClients.setEmptyView(textViewEmpty);
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

                for (int i = 0; i < Config.fileModels.size(); i++) {

                    FileModel fileModel = Config.fileModels.get(i);

                    if (fileModel != null && fileModel.getStrFileUrl() != null && !fileModel.getStrFileUrl().equalsIgnoreCase("")) {

                        utils.loadImageFromWeb(fileModel.getStrFileName(), fileModel.getStrFileUrl());

                       /* String strUrl = utils.replaceSpace(fileModel.getStrFileUrl());

                        String strFileName = utils.replaceSpace(fileModel.getStrFileName());

                        Utils.log(strFileName, "File Name");

                        File fileImage = utils.createFileInternal("images/" + strFileName);

                        if (fileImage.length() <= 0) {

                            InputStream input;
                            try {

                                URL url = new URL(strUrl); //URLEncoder.encode(fileModel.getStrFileUrl(), "UTF-8")
                                input = url.openStream();
                                byte[] buffer = new byte[1500];
                                OutputStream output = new FileOutputStream(fileImage);
                                try {
                                    int bytesRead;
                                    while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                                        output.write(buffer, 0, bytesRead);
                                    }
                                } finally {
                                    output.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }*/
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            threadHandler.sendEmptyMessage(0);
        }
    }
}
