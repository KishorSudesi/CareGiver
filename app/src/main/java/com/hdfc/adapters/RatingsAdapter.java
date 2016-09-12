package com.hdfc.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hdfc.caregiver.FeatureActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.AppUtils;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.FeedBackModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Admin on 25-02-2016.
 */
public class RatingsAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context _context;
    private List<FeedBackModel> data1 = new ArrayList<>();
    private AppUtils appUtils;

    public RatingsAdapter(Context context, List<FeedBackModel> rating_models) {
        _context = context;
        appUtils = new AppUtils(context);
        data1 = rating_models;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data1.size();
    }

    @Override
    public Object getItem(int position) {
        return data1.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.rating_item, null);
            viewHolder = new ViewHolder();
            viewHolder.feedback = (TextView) convertView.findViewById(R.id.txtMessage);
            viewHolder.time = (TextView) convertView.findViewById(R.id.txtTime);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.imageViewRatingsItem);
            viewHolder.smiley = (ImageView) convertView.findViewById(R.id.imageViewRatingsSmily);
            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            viewHolder.ratings_item = (RelativeLayout) convertView.findViewById(R.id.ratings_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (data1.size() > 0) {

            try {

                String strTemp = data1.get(position).getStrActivityName() + " - "
                        + data1.get(position).getStrActivityDate();
                viewHolder.feedback.setText(strTemp);

                viewHolder.time.setText(data1.get(position).getStrFeedBackTime());


                if (data1.get(position).getIntFeedBackRating() == 1) {
                    viewHolder.smiley.setImageDrawable(_context.getResources().getDrawable(
                            R.drawable.smiley_1));
                } else if (data1.get(position).getIntFeedBackRating() == 2) {
                    viewHolder.smiley.setImageDrawable(_context.getResources().getDrawable(
                            R.drawable.smiley_2));
                } else if (data1.get(position).getIntFeedBackRating() == 3) {
                    viewHolder.smiley.setImageDrawable(_context.getResources().getDrawable(
                            R.drawable.smiley_3));
                } else if (data1.get(position).getIntFeedBackRating() == 4) {
                    viewHolder.smiley.setImageDrawable(_context.getResources().getDrawable(
                            R.drawable.smiley_4));
                } else {
                    viewHolder.smiley.setImageDrawable(_context.getResources().getDrawable(
                            R.drawable.smiley_5));
                }

                //navigate to activities
                viewHolder.ratings_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (data1.get(position).getStrActivityId() != null
                                    && !data1.get(position).getStrActivityId().
                                    equalsIgnoreCase("")) {
                                Cursor cursor = CareGiver.getDbCon().fetch(
                                        DbHelper.strTableNameCollection,
                                        new String[]{DbHelper.COLUMN_DOCUMENT},
                                        DbHelper.COLUMN_COLLECTION_NAME + "=? and "
                                                + DbHelper.COLUMN_OBJECT_ID + "=? ",
                                        new String[]{Config.collectionActivity,
                                                data1.get(position).getStrActivityId()},
                                        DbHelper.COLUMN_UPDATE_DATE + " desc",
                                        null, true, null, null);

                                if (cursor.getCount() > 0) {

                                    cursor.moveToFirst();
                                    JSONObject jsonObject = new JSONObject(cursor.getString(0));
                                    ActivityModel activityModel = appUtils.
                                            createActivityModelNotification(
                                                    jsonObject,
                                                    data1.get(position).getStrActivityId());


                                    if (activityModel != null) {
                                        Bundle args = new Bundle();
                                        //
                                        Intent intent = new Intent(_context, FeatureActivity.class);
                                        args.putSerializable("ACTIVITY", activityModel);
                                        args.putInt("WHICH_SCREEN", 2);
                                        intent.putExtras(args);
                                        _context.startActivity(intent);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


                //todo add for dependent

                if (!data1.get(position).getStrFeedBackBy().equalsIgnoreCase("")) {

                    String strId = data1.get(position).getStrFeedBackBy();

                    String strColumnName = Config.collectionCustomer;

                    Cursor cursor1 = CareGiver.getDbCon().fetch(
                            DbHelper.strTableNameCollection, new String[]{DbHelper.COLUMN_DOCUMENT},
                            DbHelper.COLUMN_COLLECTION_NAME + "=? and " + DbHelper.COLUMN_OBJECT_ID
                                    + "=?", new String[]{strColumnName, strId}, null, "0,1",
                            true, null, null
                    );

                    JSONObject jsonObject = null;

                    if (cursor1.getCount() > 0) {
                        cursor1.moveToFirst();
                        try {
                            if (cursor1.getString(0) != null
                                    && !cursor1.getString(0).equalsIgnoreCase("")) {
                                jsonObject = new JSONObject(cursor1.getString(0));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    CareGiver.getDbCon().closeCursor(cursor1);

                    try {
                        if (jsonObject != null
                                && jsonObject.getString("customer_profile_url") != null
                                && !jsonObject.getString("customer_profile_url").
                                equalsIgnoreCase("")) {

                            Utils.loadGlide(_context, jsonObject.getString("customer_profile_url")
                                    , viewHolder.image, viewHolder.progressBar);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return convertView;
    }

    private class ViewHolder {
        TextView feedback, time;
        ImageView image, smiley;
        ProgressBar progressBar;
        RelativeLayout ratings_item;
    }
}
