package com.example.browser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import static com.example.browser.HistoryActivity.adapter;
import static com.example.browser.MainActivity.dbhandler;
import static com.example.browser.MainActivity.historyArrayList;

public class HistoryArrayAdapter  extends ArrayAdapter<History> implements Filterable {
    private Context mContext;
    private ArrayList<History> fitems;
    private ArrayList<History> original;
    private HistoryFilter filter;
    int mResource;
    public HistoryArrayAdapter(Context context, int resource, ArrayList<History> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource  = resource;
        this.fitems = new ArrayList<History>(objects);
        this.original = new ArrayList<History>(objects);
    }
    public void updateInternalData(int index, @Nullable History object, int opr){
        if(opr==1) {
            this.original.add(0, object);
        }
        else{
            this.original.remove(index);
        }
    }
    public void updateInternalData(){
        this.original.clear();
        this.fitems.clear();
    }
    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String HistoryValue = getItem(position).getHistory();
        String URLValue = getItem(position).getHistoryURL();
        LayoutInflater inflater  = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);
        TextView HistoryTextView = (TextView) convertView.findViewById(R.id.HistoryMainTextView);
        TextView HistoryURLTextView = (TextView) convertView.findViewById(R.id.URLMainTextView);
        ImageView HistoryDeleteButton = (ImageView) convertView.findViewById(R.id.deleteIthHistory);
        HistoryDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbhandler.deleteHistory(historyArrayList.get(position));
                historyArrayList.remove(position);
                original.remove(position);
                //adapter.updateInternalData(position, null, 0); //delete from original in adapter class
                adapter.notifyDataSetChanged();
            }
        });
        HistoryTextView.setText(HistoryValue);
        HistoryURLTextView.setText(URLValue);
        return convertView;
    }
    @Override
    public Filter getFilter(){
        if (filter == null){
            filter = new HistoryFilter();
        }
        return filter;
    }

    private class HistoryFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence constraint){
            FilterResults results = new FilterResults();
            String prefix = constraint.toString().toLowerCase();
            if (prefix == null || prefix.length() == 0){
                ArrayList<History> list = new ArrayList<History>(original);
                results.values = list;
                results.count = list.size();
            }
            else{
                final ArrayList<History> list = new ArrayList<History>(original);
                final ArrayList<History> nlist = new ArrayList<History>();
                int count = list.size();
                for (int i=0; i<count; i++){
                    final History History = list.get(i);
                    final String value = History.getHistory().toLowerCase();
                    if (value.contains(prefix)){
                        nlist.add(History);
                    }
                }
                results.values = nlist;
                results.count = nlist.size();
            }
            return results;
        }
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            fitems = (ArrayList<History>)results.values;
            notifyDataSetChanged();
            clear();
            int count = fitems.size();
            for (int i = 0; i < count; i++) {
                History History = (History) fitems.get(i);
                add(History);
            }
            notifyDataSetInvalidated();
        }
    }
}
