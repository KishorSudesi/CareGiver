package com.hdfc.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hdfc.caregiver.R;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.Utils;
import com.hdfc.models.NotificationModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sudesi infotech on 5/26/2016.
 */
public class NotificationAdapter extends BaseAdapter {

    private LayoutInflater inflater = null;
    private Context _context;
    private ArrayList<NotificationModel> adapterNotificationModels;
    //private Utils utils;
    //private MultiBitmapLoader multiBitmapLoader;

    public NotificationAdapter(Context ctxt, ArrayList<NotificationModel> d) {
        _context = ctxt;
        adapterNotificationModels = d;
        //utils = new Utils(ctxt);
        //multiBitmapLoader = new MultiBitmapLoader(ctxt);
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
            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            viewHolder.roundedImageView = (ImageView) convertView.
                    findViewById(R.id.roundedImageView);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.activityList);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterNotificationModels.size() > 0) {

            String strId = adapterNotificationModels.get(position).getStrCreatedByID();

            String strName = "", strMess, strUrl = "";
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

            if (adapterNotificationModels.get(position).getiNew() == 1)
                viewHolder.textViewText.setTypeface(Typeface.DEFAULT_BOLD);
            else
                viewHolder.textViewText.setTypeface(Typeface.DEFAULT);

            viewHolder.textViewText.setText(strMess);

            viewHolder.textReadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strMessage = (String) v.getTag();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                    builder.setTitle(_context.getString(R.string.menu_notification));
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

            if (adapterNotificationModels.get(position).getStrCreatedByType().
                    equalsIgnoreCase("provider")) {
                strName = Config.providerModel.getStrName();
                strUrl = Config.providerModel.getStrImgUrl();
            }

            String strColumnName = "";

            if (adapterNotificationModels.get(position).getStrCreatedByType().
                    equalsIgnoreCase("dependent")) {
                strColumnName = Config.collectionDependent;
            }

            if (adapterNotificationModels.get(position).getStrCreatedByType().
                    equalsIgnoreCase("customer")) {
                strColumnName = Config.collectionCustomer;
            }

            Cursor cursor = null;
            JSONObject jsonObject = null;

            if (!strColumnName.equalsIgnoreCase("")) {
                cursor = CareGiver.getDbCon().fetch(
                        DbHelper.strTableNameCollection, new String[]{DbHelper.COLUMN_DOCUMENT},
                        DbHelper.COLUMN_COLLECTION_NAME + "=? and " + DbHelper.COLUMN_OBJECT_ID + "=?",
                        new String[]{strColumnName, strId}, null, "0,1", true, null, null
                );

                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    try {
                        if (cursor.getString(0) != null && !cursor.getString(0).equalsIgnoreCase("")) {
                            jsonObject = new JSONObject(cursor.getString(0));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            CareGiver.getDbCon().closeCursor(cursor);

            if (jsonObject != null) {
                try {
                    if (adapterNotificationModels.get(position).getStrCreatedByType().
                            equalsIgnoreCase("dependent")) {
                        if (jsonObject.getString("dependent_profile_url") != null
                                && !jsonObject.getString("dependent_profile_url").
                                equalsIgnoreCase("")) {
                            strUrl = jsonObject.getString("dependent_profile_url");
                        }
                        if (jsonObject.getString("dependent_name") != null
                                && !jsonObject.getString("dependent_name").equalsIgnoreCase("")) {
                            strName = jsonObject.getString("dependent_name");
                        }
                    }

                    if (adapterNotificationModels.get(position).getStrCreatedByType().
                            equalsIgnoreCase("customer")) {
                        if (jsonObject.getString("customer_profile_url") != null
                                && !jsonObject.getString("customer_profile_url").
                                equalsIgnoreCase("")) {
                            strUrl = jsonObject.getString("customer_profile_url");
                        }
                        if (jsonObject.getString("customer_name") != null
                                && !jsonObject.getString("customer_name").equalsIgnoreCase("")) {
                            strName = jsonObject.getString("customer_name");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            try {
                //String strDate = adapterNotificationModels.get(position).getStrDateTime();
                String strDisplayDate = _context.getResources().getString(R.string.space) +
                        _context.getResources().getString(R.string.at) +
                        _context.getResources().getString(R.string.space) +
                        adapterNotificationModels.get(position).getStrDisplayDate();

                viewHolder.textViewTime.setText(strDisplayDate);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (strName != null && !strName.equalsIgnoreCase(""))
                viewHolder.textViewName.setText(strName);

            if (strUrl != null && !strUrl.equalsIgnoreCase("")) {

                Utils.loadGlide(_context, strUrl, viewHolder.roundedImageView,
                        viewHolder.progressBar);

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
        ProgressBar progressBar;
    }
}
