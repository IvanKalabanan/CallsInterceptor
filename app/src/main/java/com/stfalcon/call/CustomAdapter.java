package com.stfalcon.call;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by root on 07.06.15.
 */
public class CustomAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    public static final int TYPE_SEPARATOR = 1;

    private ArrayList<Calls> mData = new ArrayList<>();
    private TreeSet<Integer> sectionHeader = new TreeSet<>();

    private LayoutInflater mInflater;

    public CustomAdapter(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final Calls item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final Calls item) {
        mData.add(item);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Calls getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clearData(){
        mData.clear();
        sectionHeader.clear();
    }

    public int getPosition(int position) {
        return position - sectionHeader.headSet(position).size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.snippet_item1, null);
                    holder.name = (TextView) convertView.findViewById(R.id.textViewName);
                    holder.time = (TextView) convertView.findViewById(R.id.textViewTime);
                    holder.state = (TextView) convertView.findViewById(R.id.textViewState);
                    holder.time.setText(mData.get(position).time);
                    holder.state.setText(mData.get(position).state);
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.snippet_item2, null);
                    holder.name = (TextView) convertView.findViewById(R.id.textSeparator);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(mData.get(position).name);

        return convertView;
    }

    public static class ViewHolder {
        public TextView name;
        public TextView time;
        public TextView state;
    }

}
