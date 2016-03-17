package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.hdfc.caregiver.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 3/4/2016.
 */
public class FeatureAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    Context context;
    List<String> features;
   public ArrayList<String> selectedStrings = new ArrayList<>();

    public FeatureAdapter(Context context, List<String> features){
            this.context = context;
            this.features = features;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return features.size();
    }

    @Override
    public Object getItem(int position) {
        return features.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.feature_item,null);
            viewHolder = new ViewHolder();
            viewHolder.checkBox = (CheckBox)convertView.findViewById(R.id.checkbox22);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.checkBox.setTag(features.get(position));
        viewHolder.checkBox.setText(features.get(position));


        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    selectedStrings.add(viewHolder.checkBox.getText().toString());
                    System.out.println("array contains : "+selectedStrings);
                } else {
                    selectedStrings.remove(viewHolder.checkBox.getText().toString());
                }
            }
        });
        return convertView;
    }

    public class ViewHolder {
        CheckBox checkBox;
    }
}
