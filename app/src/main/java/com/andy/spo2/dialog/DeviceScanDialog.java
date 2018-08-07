package com.andy.spo2.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.andy.spo2.R;
import com.andy.spo2.adapter.DeviceListAdapter;

public abstract class DeviceScanDialog extends Dialog {
    Context context;
    private ListView lv_device;
    private DeviceListAdapter leDeviceListAdapter;

    public DeviceScanDialog(Context context,DeviceListAdapter leDeviceListAdapter){
        super(context);
        this.context = context;
        this.leDeviceListAdapter = leDeviceListAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.dialog_device_scan, null);
        setContentView(convertView);

        lv_device = (ListView)convertView.findViewById(R.id.lv_device);
        lv_device.setAdapter(leDeviceListAdapter);
        lv_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onClickDeviceItem(position);
            }
        });


        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public abstract void onClickDeviceItem(int pos);


}
