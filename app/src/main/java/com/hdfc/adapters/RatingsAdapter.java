package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdfc.caregiver.R;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.libs.Utils;
import com.hdfc.models.FeedBackModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Admin on 25-02-2016.
 */
public class RatingsAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    Context _context;
    List<FeedBackModel> data1 = new ArrayList<>();
    private Utils utils;
    private MultiBitmapLoader multiBitmapLoader;

    public RatingsAdapter(Context context, List<FeedBackModel> rating_models) {
        _context = context;
        utils = new Utils(context);
        multiBitmapLoader = new MultiBitmapLoader(_context);
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
            viewHolder.image = (ImageView) convertView.findViewById(R.id.imageViewRatingsItem);
            viewHolder.smily = (ImageView)convertView.findViewById(R.id.imageViewRatingsSmily);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(data1.size()>0) {

            FeedBackModel feedBackModel = data1.get(position);

            viewHolder.feedback.setText(feedBackModel.getStrFeedBackMessage());

            if (feedBackModel.getIntFeedBackRating()== 1){
                viewHolder.smily.setImageDrawable(_context.getResources().getDrawable(R.mipmap.rate_icon_2));
            }else if(feedBackModel.getIntFeedBackRating()== 2)
            {
                viewHolder.smily.setImageDrawable(_context.getResources().getDrawable( R.mipmap.smiley_icon));
            }else if (feedBackModel.getIntFeedBackRating()== 3){
                viewHolder.smily.setImageDrawable(_context.getResources().getDrawable( R.mipmap.rate_icon_3));
            }else if (feedBackModel.getIntFeedBackRating()== 4){
                viewHolder.smily.setImageDrawable(_context.getResources().getDrawable( R.mipmap.rate_icon_2));
            }else {
                viewHolder.smily.setImageDrawable(_context.getResources().getDrawable( R.mipmap.smiley_icon));
            }
            File fileImage = Utils.createFileInternal("images/" + utils.replaceSpace(feedBackModel.getStrFeedBackBy()));

            if(fileImage.exists()) {
                String strFilePath = fileImage.getAbsolutePath();
                multiBitmapLoader.loadBitmap(strFilePath, viewHolder.image);
            }else{
                viewHolder.image.setImageDrawable(_context.getResources().getDrawable(R.drawable.person_icon));
            }

        }
        return convertView;
    }
    public class ViewHolder{
        TextView feedback;
        ImageView image,smily;
    }
}
