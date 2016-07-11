package com.hdfc.adapters;

/**
 * Created by Aniket Kadam on 5/4/2016.
 */


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hdfc.caregiver.CheckInCareProcess;
import com.hdfc.caregiver.ClientProfileActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.Config;
import com.hdfc.models.CustomerModel;
import com.hdfc.models.DependentModel;

import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater inf;
    private Context _context;
    private List<CustomerModel> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<CustomerModel, List<DependentModel>> _listDataChild;
    //private Utils utils;
    //private MultiBitmapLoader multiBitmapLoader;

    public ExpandableListAdapter(Context context, List<CustomerModel> listDataHeader,
                                 HashMap<CustomerModel, List<DependentModel>> listChildData) {
        this._context = context;
        //utils = new Utils(_context);
        //multiBitmapLoader = new MultiBitmapLoader(_context);
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        inf = LayoutInflater.from(_context);
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
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

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
            public void onClick(View v) {
                Config.customerModel = (CustomerModel) v.getTag();
                Intent next = new Intent(_context, CheckInCareProcess.class);
                _context.startActivity(next);

            }
        });

        return convertView;

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
        LinearLayout linearTextHeader, linearTextChild;
    }
}