package com.hdfc.adapters;

/**
 * Created by Aniket Kadam on 5/4/2016.
 */


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hdfc.caregiver.CheckInCareActivity;
import com.hdfc.caregiver.ClientProfileActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.AppUtils;
import com.hdfc.libs.SessionManager;
import com.hdfc.libs.Utils;
import com.hdfc.models.CustomerModel;
import com.hdfc.models.DependentModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    static final int CUSTOM_DIALOG_ID = 0;
    private static ArrayList<String> strings;
    private final LayoutInflater inf;
    ListView dialog_ListView;
    private Context _context;
    private Utils utils;
    private AppUtils appUtils;
    private SessionManager sessionManager = null;
    private List<CustomerModel> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<CustomerModel, List<DependentModel>> _listDataChild;
    private boolean bool[];
    private AlertDialog myalertDialog = null;
    //private Utils utils;
    //private MultiBitmapLoader multiBitmapLoader;


    public ExpandableListAdapter(Context context, List<CustomerModel> listDataHeader,
                                 HashMap<CustomerModel, List<DependentModel>> listChildData) {
        this._context = context;
        //utils = new Utils(_context);
        //multiBitmapLoader = new MultiBitmapLoader(_context);
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        bool = new boolean[listDataHeader.size()];
        inf = LayoutInflater.from(_context);
        appUtils = new AppUtils(_context);
        utils = new Utils(_context);
        sessionManager = new SessionManager(_context);

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final DependentModel dependentModel = (DependentModel) getChild(groupPosition, childPosition);
        ViewHolder viewHolder;

        if (convertView == null) {
           /* LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);*/
            convertView = inf.inflate(R.layout.list_item_dependents, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.textViewName);

            viewHolder.age = (TextView) convertView.findViewById(R.id.textViewClient_age);

            //viewHolder.problem = (TextView) convertView.findViewById(R.id.textViewClient_problem);
            //viewHolder.premium = (TextView) convertView.findViewById(R.id.textViewPremium);
            viewHolder.address = (TextView) convertView.findViewById(R.id.textViewAddress);
            viewHolder.customer = (ImageView) convertView.findViewById(R.id.imageClients);
            viewHolder.linearTextChild = (LinearLayout) convertView.findViewById(R.id.linearTextChild);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.linearTextChild.setTag(dependentModel);

        viewHolder.name.setText(dependentModel.getStrName());
        //viewHolder.age.setText(dependentModel.getIntAge());
        //System.out.println("LOL : "+dependentModel.getIntAge());
        viewHolder.age.setText(String.valueOf(dependentModel.getIntAge()));

        // viewHolder.problem.setText(dependentModel.getStrIllness().length()>8 ? dependentModel.getStrIllness().substring(0,5)+"..":dependentModel.getStrIllness());
        //viewHolder.premium.setText(dependentModel.getStrNotes().length()>8 ? dependentModel.getStrNotes().substring(0,5)+"..":dependentModel.getStrNotes());

        /*File fileImage = Utils.createFileInternal("images/" + utils.replaceSpace(dependentModel.getStrDependentID()));

        if(fileImage.exists()) {
            String filename = fileImage.getAbsolutePath();
            multiBitmapLoader.loadBitmap(filename, viewHolder.customer);
        }else{
            viewHolder.customer.setImageDrawable(_context.getResources().getDrawable(R.drawable.person_icon));
        }*/

        Glide.with(_context)
                .load(dependentModel.getStrImageUrl())
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(_context))
                .placeholder(R.drawable.person_icon)
                .crossFade()
                .into(viewHolder.customer);

        viewHolder.address.setText(dependentModel.getStrAddress());

        viewHolder.linearTextChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Config.dependentModel = (DependentModel) v.getTag();
                Config.customerModel = null;

                Intent intent = new Intent(_context, ClientProfileActivity.class);
                _context.startActivity(intent);
            }
        });

        return convertView;

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, final ViewGroup parent) {

        final CustomerModel customerModel = (CustomerModel)getGroup(groupPosition);
        ViewHolder viewHolder;

        if (convertView == null) {
/*
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
*/
            convertView = inf.inflate(R.layout.list_group_customers, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.textViewName);
            viewHolder.address = (TextView) convertView.findViewById(R.id.textViewAddress);
            viewHolder.contact = (TextView)convertView.findViewById(R.id.textViewContact);
            viewHolder.client = (ImageView) convertView.findViewById(R.id.imageClients);
            viewHolder.insert = (ImageButton)convertView.findViewById(R.id.insert);
            viewHolder.linearTextHeader = (LinearLayout) convertView.findViewById(R.id.linearText);

            viewHolder.imageWrapper = (LinearLayout) convertView.findViewById(R.id.imageWrapper);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(customerModel.getStrName());
        viewHolder.contact.setText(customerModel.getStrContacts());
        viewHolder.address.setText(customerModel.getStrAddress());

        viewHolder.linearTextHeader.setTag(customerModel);
        viewHolder.insert.setTag(customerModel);

        /*File fileImage = Utils.createFileInternal("images/" + utils.replaceSpace(customerModel.getStrCustomerID()));

        if(fileImage.exists()) {
            String filename = fileImage.getAbsolutePath();
            multiBitmapLoader.loadBitmap(filename, viewHolder.client);
        }else{
            viewHolder.client.setImageDrawable(_context.getResources().getDrawable(R.drawable.person_icon));
        }*/

        Glide.with(_context)
                .load(customerModel.getStrImgUrl())
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(_context))
                .placeholder(R.drawable.person_icon)
                .crossFade()
                .into(viewHolder.client);

        viewHolder.imageWrapper.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (bool[groupPosition]) {
                    bool[groupPosition] = false;
                    ((ExpandableListView) parent).collapseGroup(groupPosition);
                } else {
                    bool[groupPosition] = true;
                    ((ExpandableListView) parent).expandGroup(groupPosition, true);
                }

            }
        });


        viewHolder.linearTextHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Config.customerModel = (CustomerModel) v.getTag();
                Config.dependentModel = null;
                Intent intent = new Intent(_context, ClientProfileActivity.class);
                _context.startActivity(intent);
            }
        });

        viewHolder.insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Config.customerModel = (CustomerModel) v.getTag();
                Calendar c = Calendar.getInstance();

                int iyear = c.get(Calendar.YEAR);
                int imonth = c.get(Calendar.MONTH) + 1;

                try {

                    //fetchCheckInCareName(imonth, iyear, Config.customerModel.getStrCustomerID(), Config.providerModel.getStrProviderId());

                    fetchAllCheckInCares(imonth, iyear);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        return convertView;

    }

    private void fetchAllCheckInCares(int iMonth, int iYear) {

        Config.checkInCareModels.clear();

        String strEndDate = Utils.getCurrentMonthLastDate();

        String strMonth = String.valueOf(iMonth);

        if (iMonth <= 9)
            strMonth = "0" + strMonth;

        String strStartDate = String.valueOf(iYear) + "-" + strMonth + "-01" + " 00:00:00.000";

        //Utils.log(strStartDate, " SDATE ");

        String strQuery = "SELECT a." + DbHelper.COLUMN_DOCUMENT + " AS C1 , b."
                + DbHelper.COLUMN_MILESTONE_ID + " AS C2, b." + DbHelper.COLUMN_OBJECT_ID
                + " AS C3 FROM " + DbHelper.strTableNameCollection + " AS a INNER JOIN "
                + DbHelper.strTableNameMilestone + " AS b ON a.object_id=b.object_id  WHERE b."
                + DbHelper.COLUMN_MILESTONE_DATE + ">= Datetime('" + strStartDate + "') AND b."
                + DbHelper.COLUMN_MILESTONE_DATE + "<= Datetime('" + strEndDate + "') AND b."
                + DbHelper.COLUMN_CUSTOMER_ID + "='" + Config.customerModel.getStrCustomerID()
                + "' AND b." + DbHelper.COLUMN_MILESTONE_ID
                + "=-1 AND a." + DbHelper.COLUMN_COLLECTION_NAME + "='" + Config.collectionCheckInCare + "'"
                + " ORDER BY b." + DbHelper.COLUMN_MILESTONE_DATE + " DESC";

        //LIMIT 0, 30000

        Utils.log(strQuery, " QUERY ");

        Cursor newCursor = CareGiver.getDbCon().rawQuery(strQuery);

        if (newCursor.getCount() > 0) {

            newCursor.moveToFirst();

            try {

                while (!newCursor.isAfterLast()) {

                    //JSONObject jsonObject = new JSONObject(newCursor.getString(0));
                    appUtils.createCheckInCareModel(newCursor.getString(2), newCursor.getString(0));
                    //createActivityModel(jsonObject, newCursor.getString(2), isActivity);

                    newCursor.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        CareGiver.getDbCon().closeCursor(newCursor);

        if (Config.checkInCareModels.size() <= 0) {
            final CharSequence[] items = {_context.getString(R.string.create_new),
                    _context.getString(R.string.cancel)};

            AlertDialog.Builder builder = new AlertDialog.Builder(_context);

            builder.setTitle(_context.getString(R.string.check_in_care));
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {

                    if (items[item].equals(_context.getString(R.string.create_new))) {
                        Intent i = new Intent(_context, CheckInCareActivity.class);
                        _context.startActivity(i);

                    } else if (items[item].equals(_context.getString(R.string.cancel))) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        } else {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(_context);
            View convertView = inf.inflate(R.layout.custom_dialog, null);

            alertDialog.setTitle(_context.getString(R.string.check_in_care));

            ListView listview = (ListView) convertView.findViewById(R.id.dialoglist);
            Button create = (Button) convertView.findViewById(R.id.createnew);
            Button cancel = (Button) convertView.findViewById(R.id.cancel);
            CustomAlertAdapter arrayAdapter = new CustomAlertAdapter(_context,
                    Config.checkInCareModels);
            listview.setAdapter(arrayAdapter);

            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(_context, CheckInCareActivity.class);
                    _context.startActivity(i);

                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myalertDialog.dismiss();

                }
            });

            alertDialog.setView(convertView);
            myalertDialog = alertDialog.show();
        }
        //

    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class ViewHolder {
        TextView name, age, address, contact;
        ImageView client, customer;
        ImageButton insert;
        LinearLayout linearTextHeader, linearTextChild, imageWrapper;
    }
}