package com.andy.spo2;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.andy.spo2.adapter.DeviceListAdapter;
import com.andy.spo2.adapter.ReportAdapter;
import com.andy.spo2.dialog.DeviceScanDialog;
import com.andy.spo2.fragment.HistoryFragment;
import com.andy.spo2.fragment.MeasureFragment;
import com.andy.spo2.fragment.OptionsFragment;
import com.andy.spo2.fragment.ReportFragment;
import com.andy.spo2.sqlite.MyDBHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;

public class MainActivity extends AppCompatActivity{
    ImageView img_history,img_measure,img_report,img_options;
    FrameLayout mainPage;
    Fragment fragment;
    BluetoothClient mClient;

    private DeviceListAdapter deviceListAdapter;
    private List<SearchResult> mDevices = new ArrayList<SearchResult>();
    String deviceName = "";
    String MAC = "";
    UUID serviceUUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    UUID characterUUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");

    public SQLiteDatabase db;
    //List list = new ArrayList();    //濾波暫存用List

    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        mainPage = (FrameLayout)findViewById(R.id.frag_main);
        //系統狀態欄透明但狀態欄高度會沒有
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            mainPage.setPadding(0,getStatusBarHeight(),0,0);    //額外空出狀態欄高度
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            mainPage.setPadding(0,getStatusBarHeight(),0,0);    //額外空出狀態欄高度
        }else{
            mainPage.setPadding(0,0,0,0);
        }

        img_measure = (ImageView)findViewById(R.id.img_measure);
        img_measure.setSelected(true);
        img_history = (ImageView)findViewById(R.id.img_history);
        img_report = (ImageView)findViewById(R.id.img_report);
        img_options = (ImageView)findViewById(R.id.img_options);

        gotoPage(R.id.frag_main,new MeasureFragment(),"Measure");

        img_measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_measure.setSelected(true);
                img_history.setSelected(false);
                img_report.setSelected(false);
                img_options.setSelected(false);
                gotoPage(R.id.frag_main,new MeasureFragment(),"Measure");
            }
        });
        img_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_measure.setSelected(false);
                img_history.setSelected(true);
                img_report.setSelected(false);
                img_options.setSelected(false);
                gotoPage(R.id.frag_main,new HistoryFragment(),"History");
            }
        });
        img_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_measure.setSelected(false);
                img_history.setSelected(false);
                img_report.setSelected(true);
                img_options.setSelected(false);
                gotoPage(R.id.frag_main,new ReportFragment(),"Report");
            }
        });
        img_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_measure.setSelected(false);
                img_history.setSelected(false);
                img_report.setSelected(false);
                img_options.setSelected(true);
                gotoPage(R.id.frag_main,new OptionsFragment(),"Options");
            }
        });

        MyDBHelper dbhelper = new MyDBHelper(this);
        db = dbhelper.getWritableDatabase();

        requestPermission();
    }

    public void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // API  level 大於等於 23 (Android 6.0)
            //判斷是否具有權限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //可向用戶解釋為什麼需要申請該權限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    //Toast.makeText(MainActivity.this, "需要打開位置權限才可以搜索到BLE設備", Toast.LENGTH_SHORT).show();
                }
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }else{
                initBLE();
            }
        }else{
            initBLE();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initBLE();
            } else {
                requestPermission();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void gotoPage(int Rid,Fragment fragment,String tag){
        //list.clear();   //切頁面就清空濾波用暫存list
        this.fragment = fragment;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(Rid,fragment,tag);
        transaction.commit();
    }

    public int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }


    public void initBLE(){
        mClient = new BluetoothClient(this);
        mClient.registerBluetoothStateListener(mBluetoothStateListener);
        mClient.registerBluetoothBondListener(mBluetoothBondListener);

        if(!mClient.isBluetoothOpened()){
            mClient.openBluetooth();
        }else{
            searchDevice();
        }
    }

    //監聽藍芽開關
    private final BluetoothStateListener mBluetoothStateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
            if(openOrClosed){
                searchDevice();
            }
        }

    };

    //監聽藍芽連接
    private final BleConnectStatusListener mBleConnectStatusListener = new BleConnectStatusListener() {

        @Override
        public void onConnectStatusChanged(String mac, int status) {
            if (status == STATUS_CONNECTED) {
                Toast.makeText(MainActivity.this, "連線成功...抓取數據中...", Toast.LENGTH_LONG).show();
            } else if (status == STATUS_DISCONNECTED) {
                bleConnect(MAC);
            }
        }
    };

    //監聽藍芽配對
    private final BluetoothBondListener mBluetoothBondListener = new BluetoothBondListener() {
        @Override
        public void onBondStateChanged(String mac, int bondState) {

        }
    };

    public void searchDevice() {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 3)   // 先扫BLE设备3次，每次3s
                .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
                .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
                .build();
        mClient.search(request, mSearchResponse);
    }

    public void bleConnect(final String MAC){
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)   // 连接如果失败重试3次
                .setConnectTimeout(30000)   // 连接超时30s
                .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
                .setServiceDiscoverTimeout(20000)  // 发现服务超时20s
                .build();
        mClient.connect(MAC, options,new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile profile) {
                if (code == REQUEST_SUCCESS) {
                    bleRead(MAC);
                }
            }
        });
    }

    public void bleRead(String MAC){
        mClient.notify(MAC, serviceUUID, characterUUID, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                try {
                    String data = new String(value, "UTF-8").trim();
                    Log.d("READ",data);
                    parserData(data);
                    if(fragment.getTag().equals("Options")){
                        if(((OptionsFragment)fragment).terminal_isOpen()){
                            ((OptionsFragment)fragment).updateTerminalData(data);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {

                }
            }
        });
    }

    private void parserData(String msg){
        if(msg.contains("%")){
            int spo2 = Integer.parseInt(msg.split("%")[0].trim());
            int pulse = Integer.parseInt(msg.split("%")[1].trim());
            if(spo2>0 && pulse>0){
                insertData(spo2,pulse);
                if(fragment.getTag().equals("Measure")){    //收到資料就更動指針數值
                    ((MeasureFragment)fragment).isRun = true;
                    ((MeasureFragment)fragment).mringView.setVisibility(View.VISIBLE);
                    ((MeasureFragment)fragment).updateSpO2(spo2);
                    ((MeasureFragment)fragment).updatePulse(pulse);
                }else if(fragment.getTag().equals("History")){  //在折線圖頁面且選擇是當天日期就刷新
                    if(getDateTime1().equals(((HistoryFragment)fragment).mTitle.getText().toString())){
                        gotoPage(R.id.frag_main,new HistoryFragment(),"History");
                    }
                }else if(fragment.getTag().equals("Report")){   //在 list 頁面且選擇是當天日期就刷新
                    if(getDateTime1().equals(((ReportFragment)fragment).mTitle.getText().toString())){
                        gotoPage(R.id.frag_main,new ReportFragment(),"Report");
                    }
                }
            }
        }else if(msg.contains("off") && fragment.getTag().equals("Measure")) { //字串含有off波形線隱藏
            if(((MeasureFragment)fragment).isRun){
                ((MeasureFragment)fragment).isRun = false;
                ((MeasureFragment)fragment).mringView.setVisibility(View.GONE);
            }
        }
//        else if(msg.contains(" ") && fragment.getTag().equals("Measure")){ //字串含有空格且在畫脈搏波形頁面
//            float rl = Float.parseFloat(msg.split(" ")[0].trim());
//            float ir = Float.parseFloat(msg.split(" ")[1].trim());
//            if(rl>0 && ir>0){
//                list.add(rl);
//            }else{
//                return;
//            }
//            if(list.size()>7){
//                list.remove(0); //超過7個點就刪掉第一個
//            }
//            if(list.size()==7){
//                if(((MeasureFragment)fragment).ecgChart.getData()==null){
//                    ((MeasureFragment)fragment).add_ecg_LineDataSet(((MeasureFragment)fragment).ecgChart);
//                }
//                float sum = 0;
//                for(int i=0;i<7;i++){
//                    sum+=(float)list.get(i);
//                }
//                ((MeasureFragment)fragment).addEntry(((MeasureFragment)fragment).ecgChart,sum/7.0f);
//            }
//        }
    }

    public void insertData(int spo2,int pulse){
        ContentValues cv = new ContentValues();
        cv.put("dateTime", getDateTime());
        cv.put("spo2", spo2+"");
        cv.put("pulse", pulse+"");
        cv.put("user", "guest");
        cv.put("deviceName", deviceName);
        cv.put("deviceMAC", MAC);
        db.insert("spo2Table", null, cv);
    }

    public ReportAdapter queryData(String dateTime){
        Cursor cur = db.rawQuery("SELECT * FROM spo2Table WHERE dateTime LIKE +"+"'"+dateTime+"%'"+
                " ORDER BY dateTime DESC", null);
        String[] FROM = new String[]{"dateTime","spo2","pulse"};
        int[] to = new int[]{R.id.tv_time,R.id.tv_spo2,R.id.tv_pulse};
        ReportAdapter reportAdapter = new ReportAdapter(this,R.layout.lv_report_item,cur,
                FROM,to,0);
        return reportAdapter;
    }

    public void updateEntry(String dateTime){
        Cursor cur = db.rawQuery("SELECT * FROM spo2Table WHERE dateTime LIKE +"+"'"+dateTime+"%'"+
                " ORDER BY dateTime ASC", null);
        LineChart spo2Chart = ((HistoryFragment)fragment).spo2Chart;
        LineChart pulseChart = ((HistoryFragment)fragment).pulseChart;
        if(cur.getCount()>0){
            spo2Chart.setVisibility(View.VISIBLE);
            pulseChart.setVisibility(View.VISIBLE);
            ((HistoryFragment)fragment).add_SpO2_LineDataSet(spo2Chart);
            ((HistoryFragment)fragment).add_Pulse_LineDataSet(pulseChart);
            spo2Chart.fitScreen();  //重置界限恢復到符合View寬
            pulseChart.fitScreen(); //重置界限恢復到符合View寬

            for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()){
                ((HistoryFragment)fragment).addEntry(spo2Chart,
                        ((HistoryFragment)fragment).xval_UuixDateTime(cur.getString(1).split(" ")[1]),
                        Float.parseFloat(cur.getString(2)));
                ((HistoryFragment)fragment).addEntry(pulseChart,
                        ((HistoryFragment)fragment).xval_UuixDateTime(cur.getString(1).split(" ")[1]),
                        Float.parseFloat(cur.getString(3)));
            }
        }else{
            spo2Chart.setVisibility(View.GONE);
            pulseChart.setVisibility(View.GONE);
        }
    }

    private final SearchResponse mSearchResponse = new SearchResponse() {

        @Override
        public void onSearchStarted() {
            mDevices.clear();
            deviceListAdapter = new DeviceListAdapter(MainActivity.this);
            DeviceScanDialog deviceScanDialog = new DeviceScanDialog(MainActivity.this,deviceListAdapter){
                @Override
                public void onClickDeviceItem(int position) {
                    deviceName = deviceListAdapter.getmDataList().get(position).getName();
                    MAC = deviceListAdapter.getmDataList().get(position).getAddress();
                    mClient.stopSearch();
                    dismiss();
                    bleConnect(MAC);
                    mClient.registerConnectStatusListener(MAC, mBleConnectStatusListener);
                    Toast.makeText(MainActivity.this, "連線中請稍後...", Toast.LENGTH_LONG).show();
                }
            };
            deviceScanDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            deviceScanDialog.show();
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            if (!mDevices.contains(device)) {
                mDevices.add(device);
                deviceListAdapter.setDataList(mDevices);
            }

        }

        @Override
        public void onSearchStopped() {

        }

        @Override
        public void onSearchCanceled() {

        }
    };



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClient.unregisterBluetoothStateListener(mBluetoothStateListener);
        mClient.unregisterBluetoothBondListener(mBluetoothBondListener);
        if(!MAC.equals("")){
            mClient.unnotify(MAC, serviceUUID, characterUUID, new BleUnnotifyResponse() {
                @Override
                public void onResponse(int code) {
                    if (code == REQUEST_SUCCESS) {

                    }
                }
            });
            mClient.registerConnectStatusListener(MAC, mBleConnectStatusListener);
            mClient.disconnect(MAC);
        }

        if(db!=null)db.close();
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getDateTime1() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy/MM/dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
