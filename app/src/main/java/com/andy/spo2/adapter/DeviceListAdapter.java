package com.andy.spo2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.andy.spo2.R;
import com.inuker.bluetooth.library.search.SearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DeviceListAdapter extends BaseAdapter implements Comparator<SearchResult> {
    Context context;

    private List<SearchResult> mDataList;

    public DeviceListAdapter(Context context) {
        this.context = context;
        mDataList = new ArrayList<SearchResult>();
    }

    public void setDataList(List<SearchResult> datas) {
        mDataList.clear();
        mDataList.addAll(datas);
        Collections.sort(mDataList, this);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int compare(SearchResult lhs, SearchResult rhs) {
        return rhs.rssi - lhs.rssi;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.listitem_device, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final SearchResult result = (SearchResult) getItem(position);
        viewHolder.deviceAddress.setText(result.getAddress());
        if(!result.getName().equals("NULL")){
            viewHolder.deviceName.setText(result.getName());
        }else{
            viewHolder.deviceName.setText("Unknown device");
        }



        return view;
    }

    class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    public List<SearchResult> getmDataList() {
        return mDataList;
    }
}
