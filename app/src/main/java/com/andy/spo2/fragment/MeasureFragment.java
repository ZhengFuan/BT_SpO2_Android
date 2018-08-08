package com.andy.spo2.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andy.spo2.R;
import com.andy.spo2.view.DashboardView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class MeasureFragment extends Fragment{
    private View currentView;
    LinearLayout dashboard_layout;
    private DashboardView dash_spo2;
    private DashboardView dash_pulse;
    private final static int invs[] = {92, 8};
    private final static int[] colorRes = {R.color.arc2, R.color.arc3};
    private final static int invs1[] = {62, 40, 18};
    private final static int[] colorRes1 = {R.color.arc2, R.color.arc3, R.color.arc22};
    TextView tv_spo2,tv_pulse;
    public LineChart ecgChart;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_measure, container,false);
        dashboard_layout = (LinearLayout) currentView.findViewById(R.id.dashboard_layout);
        tv_spo2 = (TextView) currentView.findViewById(R.id.tv_spo2);
        tv_pulse = (TextView) currentView.findViewById(R.id.tv_pulse);

        dash_spo2 =  (DashboardView) currentView.findViewById(R.id.dash_spo2);
        String[] str = new String[]{"過低","正常"};
        dash_spo2.initDash(0, invs, str, "%", colorRes);
        //dash_spo2.setAngleWithAnim(0);
        dash_spo2.setAngle(0);
        tv_spo2.setText("血氧 SpO2 :    %");

        dash_pulse =  (DashboardView) currentView.findViewById(R.id.dash_pulse);
        String[] str2 = new String[]{"過低","正常","過高"};
        dash_pulse.initDash(0, invs1, str2, "bpm", colorRes1);
        //dash_pulse.setAngleWithAnim(0);
        dash_pulse.setAngle(0);
        tv_pulse.setText("脈搏 Pulse :    bpm");

        ecgChart = (LineChart)currentView.findViewById(R.id.ecgChart);
        initialChart(ecgChart);
        ecgChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "";
            }
        });

        return currentView;
    }

    public void updateSpO2(int spo2){
        //dash_spo2.setAngleWithAnim(spo2));
        dash_spo2.setAngle(spo2);
        tv_spo2.setText("血氧 SpO2 : "+spo2+" %");
    }

    public void updatePulse(int pulse){
        //dash_pulse.setAngleWithAnim(pulse);
        dash_pulse.setAngle(pulse);
        tv_pulse.setText("脈搏 Pulse : "+pulse+" bpm");
    }

    //初始化折線圖
    public void initialChart(LineChart mChart) {
        Description description = mChart.getDescription();
        description.setEnabled(false);
        //description.setText("時間");

        //可觸摸
        mChart.setTouchEnabled(true);
        //可拖曳
        mChart.setDragEnabled(true);
        // 可缩放
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(false);
        //設置圖表背景顏色
        mChart.setBackgroundColor(Color.TRANSPARENT);
        //mChart.setBackgroundResource(R.drawable.bg_line_chart);
        //設置圖表上下左右距離
        mChart.setExtraOffsets(0, 0, 0, 0);

        //X座標軸
        XAxis xl = mChart.getXAxis();
        //X軸顯示
        xl.setDrawAxisLine(false);
        xl.setTextColor(0xFF05E622);
        //X軸格線隱藏
        xl.setDrawGridLines(false);
        //xl.setAvoidFirstLastClipping(true);
        // 將X坐標軸放置在底部，默認是在頂部。
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        //設置X軸高度
        xl.setAxisLineWidth(0);
        //設置X軸刻度
        xl.setGranularity(1.0f);
        xl.setGranularityEnabled(true);

        // 圖表左邊的y坐標軸線
        YAxis leftAxis = mChart.getAxisLeft();
        //是否顯示Y軸值
        leftAxis.setDrawLabels(false);
        leftAxis.setTextColor(0xFF05E622);
        // 最大值
        //leftAxis.setAxisMaximum(ymax);
        // 最小值
        //leftAxis.setAxisMinimum(ymin);
        // 不一定要從0開始
        leftAxis.setStartAtZero(false);
        //Y軸格線是否顯示
        leftAxis.setDrawGridLines(false);
        //設置Y軸刻度
        //leftAxis.setGranularity(1.0f);
        //隱藏左邊Y軸
        mChart.getAxisLeft().setEnabled(false);

        //隱藏右邊Y軸
        mChart.getAxisRight().setEnabled(false);
        //繪製動畫
        //mChart.animateX(1000);

        mChart.getLegend().setEnabled(false);
    }

    public void add_ecg_LineDataSet(LineChart mChart) {
        LineData data = new LineData();

        data.addDataSet(create_ecg_LineDataSet());

        // 資料顯示的顏色
        // data.setValueTextColor(Color.WHITE)

        mChart.setData(data);
    }

    //初始化ecg折線圖
    private LineDataSet create_ecg_LineDataSet() {
        LineDataSet set = new LineDataSet(null, "");
        //平滑曲線
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 折線的顏色
        set.setColor(Color.WHITE);
        //顯示座標小圓點
        set.setDrawCircles(false);
        //不顯示座標點數據
        set.setDrawValues(false);

        return set;
    }

    //加一個座標點
    public void addEntry(LineChart mChart,float yval) {
        LineData data = mChart.getData();

        ILineDataSet t_LineDataSet = data.getDataSetByIndex(0);
        Entry entry_ecg = new Entry(t_LineDataSet.getEntryCount(),yval);
        data.addEntry(entry_ecg, 0);

        mChart.notifyDataSetChanged();

        mChart.setVisibleXRangeMaximum(1.0f*600);
        Log.d("ecgChart W",ecgChart.getWidth()+"");
        mChart.moveViewToX(data.getXMax());
    }
}
