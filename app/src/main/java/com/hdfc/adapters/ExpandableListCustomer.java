package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdfc.caregiver.R;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.libs.Utils;
import com.hdfc.models.CustomerModel;
import com.hdfc.models.DependentModel;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ExpandableListCustomer extends BaseExpandableListAdapter  {
    private Context _context;
    private List<CustomerModel> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<CustomerModel, List<DependentModel>> _listDataChild;
    private Utils utils;
    private MultiBitmapLoader multiBitmapLoader;

    public ExpandableListCustomer(Context context, List<CustomerModel> listDataHeader,
                                 HashMap<CustomerModel, List<DependentModel>> listChildData) {
        this._context = context;
        utils = new Utils(_context);
        multiBitmapLoader = new MultiBitmapLoader(_context);
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
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
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_dependents, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.textViewName);
            viewHolder.age = (TextView) convertView.findViewById(R.id.textViewClient_age);
            viewHolder.problem = (TextView) convertView.findViewById(R.id.textViewClient_problem);
            viewHolder.premium = (TextView) convertView.findViewById(R.id.textViewPremium);
            viewHolder.address = (TextView) convertView.findViewById(R.id.textViewAddress);
            viewHolder.client = (ImageView) convertView.findViewById(R.id.imageClients);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(dependentModel.getStrName());
   //     viewHolder.age.setText(dependentModel.getIntAge());
        System.out.println("LOL : "+dependentModel.getIntAge());
        viewHolder.age.setText("13");

        viewHolder.problem.setText(dependentModel.getStrIllness().length()>8 ? dependentModel.getStrIllness().substring(0,5)+"..":dependentModel.getStrIllness());
        viewHolder.premium.setText(dependentModel.getStrNotes().length()>8 ? dependentModel.getStrNotes().substring(0,5)+"..":dependentModel.getStrNotes());

        File fileImage = Utils.createFileInternal("images/" + utils.replaceSpace(dependentModel.getStrName()));

        if(fileImage.exists()) {
            String filename = fileImage.getAbsolutePath();
            multiBitmapLoader.loadBitmap(filename, viewHolder.client);
        }else{
            viewHolder.client.setImageDrawable(_context.getResources().getDrawable(R.drawable.hungal_circle));
        }

        viewHolder.address.setText(dependentModel.getStrAddress());

//        TextView txtListChild = (TextView) convertView
//                .findViewById(R.id.lblListItem);
//
//        txtListChild.setText(childText);
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
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_customers, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.textViewName);
            viewHolder.address = (TextView) convertView.findViewById(R.id.textViewAddress);
            viewHolder.contact = (TextView)convertView.findViewById(R.id.textViewContact);
            viewHolder.client = (ImageView) convertView.findViewById(R.id.imageClients);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(customerModel.getStrName());
        viewHolder.contact.setText(customerModel.getStrContacts());
        viewHolder.address.setText(customerModel.getStrAddress());

        File fileImage = Utils.createFileInternal("images/" + utils.replaceSpace(customerModel.getStrName()));

        if(fileImage.exists()) {
            String filename = fileImage.getAbsolutePath();
            multiBitmapLoader.loadBitmap(filename, viewHolder.client);
        }else{
            viewHolder.client.setImageDrawable(_context.getResources().getDrawable(R.drawable.hungal_circle));
        }
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

    public  class ViewHolder{
        TextView name,age,problem,address,premium,contact;
        ImageView client;

    }
}
