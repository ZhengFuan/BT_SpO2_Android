package com.andy.spo2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.andy.spo2.R;

public class TerminalAdapter extends ArrayAdapter {
    private Context context;
    public TerminalAdapter(Context context){
        super(context, R.layout.lv_console_item);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.lv_console_item, null);

        try{
            TextView tv_msg = (TextView)convertView.findViewById(R.id.tv_msg);

            tv_msg.setText((String)getItem(position));

        }catch (Exception e){

        }

        return convertView;
    }
}
