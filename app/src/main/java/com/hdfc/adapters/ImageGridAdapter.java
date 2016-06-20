package com.hdfc.adapters;

import java.io.File;
import java.util.ArrayList;
import com.hdfc.caregiver.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageGridAdapter extends BaseAdapter implements OnClickListener{
	
	Context context;
	ViewHolder holder;
	LayoutInflater inflater;
	ArrayList<File> bmp_array ;
	
	public ImageGridAdapter(Context context, ArrayList<File> fileNameArray) {
		this.context = context;
		this.bmp_array = fileNameArray;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		
		return bmp_array.size();
	}

	@Override
	public File getItem(int position) {
		
		return bmp_array.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			holder = new ViewHolder();			
			convertView = inflater.inflate(R.layout.image_grid_item, null);
			holder.captured_img = (ImageView)convertView.findViewById(R.id.captured_img);
			//holder.cancel_img = (ImageView)convertView.findViewById(R.id.cancel_img);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
					
		//Bitmap bm = getItem(position);
		File file = getItem(position);
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 8;
		Bitmap bm = BitmapFactory.decodeFile(file.getPath(),options);
		holder.captured_img.setImageBitmap(bm);
		
//		 if(bm!=null)
//		   {
//		     bm.recycle();
//		     bm=null;
//		    }
		//holder.cancel_img.setTag(new Integer(position));
		return convertView;
	}
	
	private class ViewHolder{
		ImageView  captured_img ; 
		//ImageView cancel_img;
	}

	@Override
	public void onClick(View v) {
		int pos = (Integer) v.getTag();
		switch(v.getId()){
//		case R.id.cancel_img:
//			bmp_array.remove(getItem(pos));
//			RaiseTicket.updateList(bmp_array);
//			ImageGridAdapter.this.notifyDataSetChanged();
//			break;

		}
	}
	
	
}
