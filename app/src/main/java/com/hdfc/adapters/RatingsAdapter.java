package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdfc.caregiver.R;
import com.hdfc.libs.Libs;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.models.RatingModel;
import com.hdfc.views.RoundedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Admin on 25-02-2016.
 */
public class RatingsAdapter extends BaseAdapter {

    Context _context;
    private static LayoutInflater inflater = null;
    List<RatingModel> data1= new ArrayList<>();
    private Libs libs;
    private MultiBitmapLoader multiBitmapLoader;

    public RatingsAdapter(Context context, List<RatingModel> rating_models) {
        _context = context;
        libs=new Libs(context);
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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(data1.size()>0) {

            RatingModel ratingModel = data1.get(position);

            viewHolder.feedback.setText(ratingModel.getMessage());

            File fileImage = libs.createFileInternal("images/" + libs.replaceSpace(ratingModel.getStrAuthorName()));

            if(fileImage.exists()) {
                String filename = fileImage.getAbsolutePath();
                multiBitmapLoader.loadBitmap(filename, viewHolder.image);
            }else{
                viewHolder.image.setImageDrawable(_context.getResources().getDrawable(R.drawable.hungal_circle));
            }

        }
        return convertView;
    }
    public class ViewHolder{
        TextView feedback;
        ImageView image;
    }
}
