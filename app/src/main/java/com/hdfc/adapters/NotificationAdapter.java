package com.hdfc.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hdfc.caregiver.R;
import com.hdfc.config.Config;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.libs.Utils;
import com.hdfc.models.NotificationModel;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

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
            viewHolder.textReadMore = (TextView) convertView.findViewById(R.id.textReadMore);
            viewHolder.roundedImageView = (ImageView) convertView.
                    findViewById(R.id.roundedImageView);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.activityList);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterNotificationModels.size() > 0) {

            String strId = adapterNotificationModels.get(position).getStrCreatedByID();

            String strName = "", strMess = "", strUrl = "";
          //  String readMore = " READ MORE..";
            strMess = adapterNotificationModels.get(position).getStrMessage();

            String strMessage = strMess;

            if (strMess.length() > 75) {
                strMess = strMess.substring(0, 72);
                viewHolder.textReadMore.setVisibility(View.VISIBLE);
                viewHolder.textReadMore.setTag(strMessage);
            }else {
                viewHolder.textReadMore.setVisibility(View.GONE);
                viewHolder.textReadMore.setEnabled(false);
            }
            viewHolder.textViewText.setText(strMess);

            viewHolder.textReadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strMessage = (String) v.getTag();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                    builder.setTitle("Notification");
                    builder.setMessage(strMessage);
                    builder.setPositiveButton(_context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });

            if (adapterNotificationModels.get(position).getStrCreatedByType().equalsIgnoreCase("provider")) {
                strName = Config.providerModel.getStrName();
                strUrl = Config.providerModel.getStrImgUrl();
            }

            if (adapterNotificationModels.get(position).getStrCreatedByType().equalsIgnoreCase("dependent")) {
                if (Config.dependentIdsAdded.contains(strId)) {
                    strName = Config.dependentModels.get(Config.dependentIdsAdded.
                            indexOf(strId)).getStrName();
                    strUrl = Config.dependentModels.get(Config.dependentIdsAdded.
                            indexOf(strId)).getStrImageUrl();
                }
            }

            if (adapterNotificationModels.get(position).getStrCreatedByType().equalsIgnoreCase("customer")) {
                if (Config.customerIdsAdded.contains(strId)) {
                    strName = Config.customerModels.get(Config.customerIdsAdded.
                            indexOf(strId)).getStrName();
                    strUrl = Config.customerModels.get(Config.customerIdsAdded.
                            indexOf(strId)).getStrImgUrl();
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
               /* File f = utils.getInternalFileImages(utils.replaceSpace(strId));

                //Utils.log(f.getAbsolutePath(), " P ");

                if (f.exists())
                    multiBitmapLoader.loadBitmap(f.getAbsolutePath(), viewHolder.roundedImageView);*/

                Glide.with(_context)
                        .load(strUrl)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(_context))
                        .placeholder(R.drawable.person_icon)
                        .crossFade()
                        .into(viewHolder.roundedImageView);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView textViewName;
        TextView textViewText;
        TextView textReadMore;
        TextView textViewTime;
        ImageView roundedImageView;
        LinearLayout linearLayout;
    }
}
