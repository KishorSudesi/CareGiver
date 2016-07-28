package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hdfc.caregiver.R;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.Utils;
import com.hdfc.models.FeedBackModel;

import net.sqlcipher.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * Created by Admin on 25-02-2016.
 */
public class RatingsAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context _context;
    private List<FeedBackModel> data1 = new ArrayList<>();
    private Utils utils;
    //private MultiBitmapLoader multiBitmapLoader;

    public RatingsAdapter(Context context, List<FeedBackModel> rating_models) {
        _context = context;
        utils = new Utils(context);
        //multiBitmapLoader = new MultiBitmapLoader(_context);
        data1 = rating_models;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return data1.size();
    }

    @Override
    public Object getItem(int position) { return data1.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.rating_item, null);
            viewHolder = new ViewHolder();
            viewHolder.feedback = (TextView) convertView.findViewById(R.id.txtMessage);
            viewHolder.time = (TextView) convertView.findViewById(R.id.txtTime);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.imageViewRatingsItem);
            viewHolder.smiley = (ImageView) convertView.findViewById(R.id.imageViewRatingsSmily);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(data1.size()>0) {

            //FeedBackModel feedBackModel = data1.get(position);

            try {

                viewHolder.feedback.setText(data1.get(position).getStrFeedBackMessage());
                try {
                    viewHolder.time.setText(Utils.writeFormat.format(
                            Utils.convertStringToDate(data1.get(position).getStrFeedBackTime())));
                } catch (Exception e) {
                    viewHolder.time.setText(data1.get(position).getStrFeedBackTime());
                }

                if (data1.get(position).getIntFeedBackRating() == 1) {
                    viewHolder.smiley.setImageDrawable(_context.getResources().getDrawable(R.drawable.smiley_1));
                } else if (data1.get(position).getIntFeedBackRating() == 2) {
                    viewHolder.smiley.setImageDrawable(_context.getResources().getDrawable(R.drawable.smiley_2));
                } else if (data1.get(position).getIntFeedBackRating() == 3) {
                    viewHolder.smiley.setImageDrawable(_context.getResources().getDrawable(R.drawable.smiley_3));
                } else if (data1.get(position).getIntFeedBackRating() == 4) {
                    viewHolder.smiley.setImageDrawable(_context.getResources().getDrawable(R.drawable.smiley_4));
                } else {
                    viewHolder.smiley.setImageDrawable(_context.getResources().getDrawable(R.drawable.smiley_5));
                }

                //
           /* File fileImage = Utils.createFileInternal("images/" + utils.replaceSpace(feedBackModel.getStrFeedBackBy()));

            if(fileImage.exists()) {
                String strFilePath = fileImage.getAbsolutePath();
                multiBitmapLoader.loadBitmap(strFilePath, viewHolder.image);
            }else{
                viewHolder.image.setImageDrawable(_context.getResources().getDrawable(R.drawable.person_icon));
            }*/

                //todo add for dependent

                //String strId;

                if (!data1.get(position).getStrFeedBackBy().equalsIgnoreCase("")) {

                    String strId = data1.get(position).getStrFeedBackBy();

                    String strColumnName = Config.collectionCustomer;

                    Cursor cursor1 = CareGiver.getDbCon().fetch(
                            DbHelper.strTableNameCollection, new String[]{DbHelper.COLUMN_DOCUMENT},
                            DbHelper.COLUMN_COLLECTION_NAME + "=? and " + DbHelper.COLUMN_OBJECT_ID + "=?",
                            new String[]{strColumnName, strId}, null, "0,1", true, null, null
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
                            Glide.with(_context)
                                    .load(jsonObject.getString("customer_profile_url"))
                                    .centerCrop()
                                    .bitmapTransform(new CropCircleTransformation(_context))
                                    .placeholder(R.drawable.person_icon)
                                    .crossFade()
                                    .into(viewHolder.image);
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
    }
}
