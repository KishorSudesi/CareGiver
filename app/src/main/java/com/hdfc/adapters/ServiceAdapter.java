package com.hdfc.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.hdfc.caregiver.R;
import com.hdfc.models.ServiceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sudesi infotech on 5/7/2016.
 */
public class ServiceAdapter extends BaseAdapter implements Filterable {

    private static LayoutInflater inflater = null;
    Context _context;
    List<ServiceModel> Filterdata = new ArrayList<>();
    private ItemFilter mFilter = new ItemFilter();


    public ServiceAdapter(Context context,List<ServiceModel> serviceModel){
        _context = context;
        Filterdata = serviceModel;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return Filterdata.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.service_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.serviceName = (TextView) convertView.findViewById(R.id.textService);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (Filterdata.size() > 0) {

            //ServiceModel serviceModel = Filterdata.get(position);
            viewHolder.serviceName.setText(""+Filterdata.get(position));

        }

            return convertView;
        }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class ViewHolder{
        TextView serviceName;
    }

    private class ItemFilter extends Filter {
        @SuppressLint("DefaultLocale")
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<ServiceModel> list = Filterdata;

            int count = list.size();
            final ArrayList<ServiceModel> nlist = new ArrayList<ServiceModel>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = "" + list.get(i).getStrServiceName();
                if (filterableString.toLowerCase().contains(filterString)) {
                    ServiceModel mYourCustomData = list.get(i);
                    nlist.add(mYourCustomData);
                }
            }

            results.values = nlist;
            results.count = nlist.size();


            return results;


        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Filterdata = (ArrayList<ServiceModel>) results.values;
            //CreatingTaskActivity.serviceAdapter.notifyDataSetChanged();


        }
    }

}
