package com.hdfc.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hdfc.app42service.StorageService;
import com.hdfc.caregiver.R;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.Utils;
import com.hdfc.models.NotificationModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sudesi infotech on 5/26/2016.
 */
public class NotificationAdapter extends BaseAdapter {

    private static StorageService storageService;
    private static ProgressDialog progressDialog;
    public boolean result = false;
    private LayoutInflater inflater = null;
    private Context _context;
    private ArrayList<NotificationModel> adapterNotificationModels;
    private Utils utils;
    private String docId = "";
    //private Utils utils;
    //private MultiBitmapLoader multiBitmapLoader;

    public NotificationAdapter(Context ctxt, ArrayList<NotificationModel> d) {
        _context = ctxt;
        adapterNotificationModels = d;
        utils = new Utils(ctxt);
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
            viewHolder.deleteNotification = (Button) convertView.findViewById(R.id.btndelete);
            viewHolder.deleteNotification.setTag(position);
            viewHolder.roundedImageView = (ImageView) convertView.
                    findViewById(R.id.roundedImageView);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.activityList);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterNotificationModels.size() > 0) {

            String strId = adapterNotificationModels.get(position).getStrCreatedByID();

//            String strDocId = adapterNotificationModels.get(position).getStrDocId();

            String strName = "", strMess, strUrl = "";
            //  String readMore = " READ MORE..";
            strMess = adapterNotificationModels.get(position).getStrMessage();

            String strMessage = strMess;

            if (strMess.length() > 75) {
                strMess = strMess.substring(0, 72);
                viewHolder.textReadMore.setVisibility(View.VISIBLE);
                viewHolder.textReadMore.setTag(strMessage);
            } else {
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

                String notiDate = adapterNotificationModels.get(position).getStrDisplayDate();

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

            viewHolder.deleteNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int itemidPos = (int) v.getTag();
                    docId = adapterNotificationModels.get(itemidPos).getStrDocId();
                    AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                    builder.setMessage("Are you sure you want to delete notification?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //MyActivity.this.finish();
                                    deleteNotification(docId, itemidPos);

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }

        return convertView;
    }

   /* public void deleteNotificationByDocId(String docId) {
        result = false;
        try {
            String selection = "object_id"+ " = ?";
// WHERE clause arguments
            String[] selectionArgs = {docId};
            result = CareGiver.getDbCon().delete(DbHelper.strTableNameCollection, selection, selectionArgs);
            if (result){
                Toast.makeText(_context, "Record has been deleted..!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void deleteNotification(final String docId, final int itemidPos) {

        if (utils.isConnectingToInternet()) {

            progressDialog = new ProgressDialog(_context);
            storageService = new StorageService(_context);

            storageService.deleteDocById(Config.collectionNotification, docId,
                    new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            if (o != null) {
                                String selection = "object_id" + " = ?";

                                String selectionArgs[] = {docId};
                                result = CareGiver.getDbCon().delete(DbHelper.strTableNameCollection, selection, selectionArgs);
                                //utils.refreshNotifications();
                                if (result) {
                                    adapterNotificationModels.remove(itemidPos);
                                    NotificationAdapter.this.notifyDataSetChanged();
                                    utils.toast(2, 2, "Notification Deleted");
                                }
                            } else {
                                utils.toast(2, 2, "Please check your internet connection and try again");
                            }
                        }

                        @Override
                        public void onException(Exception e) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            if (e != null) {
                                Utils.log(e.getMessage(), " Failure ");
                                utils.toast(2, 2, "Something went wrong. Please try Again!!!");
                            } else {
                                utils.toast(2, 2, "Please check your internet connection and try again");
                            }
                        }
                    });

        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            utils.toast(2, 2, "Please check your internet connection and try again");
        }
    }

    private static class ViewHolder {
        TextView textViewName;
        TextView textViewText;
        TextView textReadMore;
        TextView textViewTime;
        ImageView roundedImageView;
        LinearLayout linearLayout;
        ProgressBar progressBar;
        Button deleteNotification;
    }
}
