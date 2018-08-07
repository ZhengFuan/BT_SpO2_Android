package com.andy.spo2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.andy.spo2.R;

import java.util.HashMap;

public class ReportAdapter_test extends ArrayAdapter {
    private Context context;
    public ReportAdapter_test(Context context){
        super(context, R.layout.lv_report_item);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.lv_report_item, null);

        try{
            HashMap<String,String> map = (HashMap<String, String>) getItem(getCount()-position-1);
            TextView tv_time = (TextView)convertView.findViewById(R.id.tv_time);
            TextView tv_spo2 = (TextView)convertView.findViewById(R.id.tv_spo2);
            TextView tv_pulse = (TextView)convertView.findViewById(R.id.tv_pulse);

            tv_time.setText(map.get("dateTime"));
            tv_spo2.setText(map.get("spo2"));
            tv_pulse.setText(map.get("pulse"));

        }catch (Exception e){

        }

        return convertView;
    }
}
