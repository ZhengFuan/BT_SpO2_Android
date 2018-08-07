package com.andy.spo2.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.andy.spo2.R;

public class ReportAdapter extends SimpleCursorAdapter {
    private Context context;
    private int layout;
    private Cursor cr;
    private final LayoutInflater inflater;
    public ReportAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags){
        super(context, layout, c, from, to, flags);
        this.layout=layout;
        this.context = context;
        this.inflater=LayoutInflater.from(context);
        this.cr=c;
    }
    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        TextView tv_time = (TextView)view.findViewById(R.id.tv_time);
        TextView tv_spo2 = (TextView)view.findViewById(R.id.tv_spo2);
        TextView tv_pulse = (TextView)view.findViewById(R.id.tv_pulse);

        tv_time.setText(cursor.getString(1).split(" ")[1]);
        tv_spo2.setText(cursor.getString(2));
        tv_pulse.setText(cursor.getString(3));
    }
}
