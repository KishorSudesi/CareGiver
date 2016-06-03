package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdfc.caregiver.R;
import com.hdfc.config.Config;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.libs.Utils;
import com.hdfc.models.NotificationModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Sudesi infotech on 5/26/2016.
 */
public class NotificationAdapter extends BaseAdapter {

    private LayoutInflater inflater = null;
    private Context _context;
    private ArrayList<NotificationModel> adapterNotificationModels;
    private Utils utils;
    private MultiBitmapLoader multiBitmapLoader;

    public NotificationAdapter(Context ctxt, ArrayList<NotificationModel> d) {
        System.out.println("Ullu");
        _context = ctxt;
        adapterNotificationModels = d;
        utils = new Utils(ctxt);
        multiBitmapLoader = new MultiBitmapLoader(ctxt);
    }

    @Override
    public int getCount() {
        return adapterNotificationModels.size();
    }


    @Override
    public Object getItem(int position) {
        return adapterNotificationModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (inflater == null)
            inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.notification_activity_list_item, null);

            viewHolder = new ViewHolder();

            viewHolder.textViewName = (TextView) convertView.findViewById(R.id.textViewName);
            viewHolder.textViewText = (TextView) convertView.findViewById(R.id.textViewText);
            viewHolder.textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);
            viewHolder.roundedImageView = (ImageView) convertView.
                    findViewById(R.id.roundedImageView);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.activityList);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterNotificationModels.size() > 0) {

            String strId = adapterNotificationModels.get(position).getStrCreatedByID();

            String strName = "";

            viewHolder.textViewText.setText(adapterNotificationModels.get(position).getStrMessage());

            System.out.println("Sudesi Infotech Pvt Ltd 1 years : "+adapterNotificationModels.get(position).getStrMessage());

            if (strName.length() > 20)
                strName = adapterNotificationModels.get(position).getStrMessage().substring(0, 18) + "..";

            if (adapterNotificationModels.get(position).getStrCreatedByType().equalsIgnoreCase("provider")) {
                strName = Config.providerModel.getStrName();
            }

            if (adapterNotificationModels.get(position).getStrCreatedByType().equalsIgnoreCase("dependent")) {
                if (Config.dependentIdsAdded.contains(strId)) {
                    strName = Config.dependentModels.get(Config.dependentIdsAdded.
                            indexOf(strId)).getStrName();
                }
            }

            if (adapterNotificationModels.get(position).getStrCreatedByType().equalsIgnoreCase("customer")) {
                if (Config.customerIdsAdded.contains(strId)) {
                    strName = Config.customerModels.get(Config.customerIdsAdded.
                            indexOf(strId)).getStrName();
                }
            }

            try {
                String strDate = adapterNotificationModels.get(position).getStrDateTime();
                String strDisplayDate = _context.getResources().getString(R.string.space) +
                        _context.getResources().getString(R.string.at) +
                        _context.getResources().getString(R.string.space) +
                        utils.formatDate(strDate);

                viewHolder.textViewTime.setText(strDisplayDate);
            } catch (Exception e) {
                e.printStackTrace();
            }

            viewHolder.textViewName.setText(strName);

            try {
                File f = utils.getInternalFileImages(utils.replaceSpace(strId));

                //Utils.log(f.getAbsolutePath(), " P ");

                if (f.exists())
                    multiBitmapLoader.loadBitmap(f.getAbsolutePath(), viewHolder.roundedImageView);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView textViewName;
        TextView textViewText;
        TextView textViewTime;
        ImageView roundedImageView;
        LinearLayout linearLayout;
    }
}
