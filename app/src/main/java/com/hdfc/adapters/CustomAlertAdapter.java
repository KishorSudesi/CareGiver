package com.hdfc.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hdfc.caregiver.CheckInCareActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.Config;
import com.hdfc.models.CheckInCareModel;

import java.util.ArrayList;

/**
 * Created by Admin on 20-07-2016.
 */

public class CustomAlertAdapter extends BaseAdapter {

    public static String strImageName = "", strClientName = "";
    private Context ctx = null;
    private ArrayList listarray;
    private LayoutInflater mInflater = null;


    public CustomAlertAdapter(Context context, ArrayList list) {
        this.ctx = context;
        mInflater = LayoutInflater.from(ctx);
        this.listarray = list;
    }

    @Override
    public int getCount() {
        return listarray.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        // final CustomerModel customerModel = (CustomerModel)getItem(i);
        ViewHolder holder;

        CheckInCareModel checkInCareModel = (CheckInCareModel) listarray.get(position);
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.alertlistrow, null);

            holder.titlename = (TextView) convertView.findViewById(R.id.textView_titllename);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String datavalue = checkInCareModel.getStrName();
        holder.titlename.setText(datavalue);
        holder.titlename.setTag(checkInCareModel);


        holder.titlename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Config.checkInCareModel = (CheckInCareModel) v.getTag();

                Bundle bundle = new Bundle();
                bundle.putBoolean("editcheckincare", true);
                bundle.putInt("itemposition", position);
                Intent intent = new Intent(ctx, CheckInCareActivity.class);
                intent.putExtras(bundle);
                ctx.startActivity(intent);

            }
        });

        return convertView;


    }

    private static class ViewHolder {
        TextView titlename;

    }
}
