package com.andy.spo2.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andy.spo2.MainActivity;
import com.andy.spo2.R;
import com.andy.spo2.util.AppUtils;
import com.codbking.calendar.CaledarAdapter;
import com.codbking.calendar.CalendarBean;
import com.codbking.calendar.CalendarDateView;
import com.codbking.calendar.CalendarUtil;
import com.codbking.calendar.CalendarView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HistoryFragment extends Fragment {
    private View currentView;
    public LineChart spo2Chart;
    public LineChart pulseChart;
    CalendarDateView mCalendarDateView;
    public TextView mTitle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_history, container,false);

        mTitle = (TextView) currentView.findViewById(R.id.title);
        mCalendarDateView = (CalendarDateView)currentView.findViewById(R.id.calendarDateView);

        spo2Chart = (LineChart)currentView.findViewById(R.id.spo2Chart);
        pulseChart = (LineChart)currentView.findViewById(R.id.pulseChart);

        initialChart(spo2Chart,1.0f);
        initialChart(pulseChart,5.0f);

        int[] data = CalendarUtil.getYMD(new Date());
        mTitle.setText(data[0] + "/" + getDisPlayNumber(data[1]) + "/" + getDisPlayNumber(data[2]));

        ((MainActivity)getActivity()).updateEntry(data[0]+"-"+getDisPlayNumber(data[1])+"-"+getDisPlayNumber(data[2]));


        spo2Chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String dateStr = sdf.format(new Date((long)value));
                return dateStr;
            }
        });

        pulseChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String dateStr = sdf.format(new Date((long)value));
                return dateStr;
            }
        });

        mCalendarDateView.setAdapter(new CaledarAdapter() {
            @Override
            public View getView(View convertView, ViewGroup parentView, CalendarBean bean) {
                TextView view;
                if (convertView == null) {
                    convertView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.item_calendar, null);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(AppUtils.px(48), AppUtils.px(48));
                    convertView.setLayoutParams(params);
                }

                view = (TextView) convertView.findViewById(R.id.text);

                view.setText("" + bean.day);
                if (bean.mothFlag != 0) {
                    view.setTextColor(0xff9299a1);
                } else {
                    view.setTextColor(0xffffffff);
                }

                return convertView;
            }
        });

        mCalendarDateView.setOnItemClickListener(new CalendarView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion, CalendarBean bean) {
                mTitle.setText(bean.year + "/" + getDisPlayNumber(bean.moth) + "/" + getDisPlayNumber(bean.day));
                ((MainActivity)getActivity()).updateEntry(
                        bean.year+"-"+getDisPlayNumber(bean.moth)+"-"+getDisPlayNumber(bean.day));
            }
        });



        return currentView;
    }


    //初始化折線圖
    public void initialChart(LineChart mChart,float granularity) {
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
        mChart.setExtraOffsets(10, 10, 10, 0);

        //X座標軸
        XAxis xl = mChart.getXAxis();
        //X軸顯示
        xl.setDrawAxisLine(true);
        xl.setTextColor(0xFF05E622);
        //X軸格線隱藏
        xl.setDrawGridLines(false);
        //xl.setAvoidFirstLastClipping(true);
        // 將X坐標軸放置在底部，默認是在頂部。
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        //設置X軸高度
        xl.setAxisLineWidth(1);
        //設置X軸刻度
        xl.setGranularity(1800);

        // 圖表左邊的y坐標軸線
        YAxis leftAxis = mChart.getAxisLeft();
        //是否顯示Y軸值
        leftAxis.setDrawLabels(true);
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
        leftAxis.setGranularity(granularity);

        //隱藏右邊Y軸
        mChart.getAxisRight().setEnabled(false);
        //繪製動畫
        //mChart.animateX(1000);

        mChart.getLegend().setTextColor(Color.WHITE);
    }

    public void add_SpO2_LineDataSet(LineChart mChart) {
        LineData data = new LineData();

        data.addDataSet(create_SpO2_LineDataSet());

        // 資料顯示的顏色
        // data.setValueTextColor(Color.WHITE)

        mChart.setData(data);
    }

    public void add_Pulse_LineDataSet(LineChart mChart) {
        LineData data = new LineData();

        data.addDataSet(create_Pulse_LineDataSet());

        // 資料顯示的顏色
        // data.setValueTextColor(Color.WHITE)

        mChart.setData(data);
    }

    //初始化血氧折線圖
    private LineDataSet create_SpO2_LineDataSet() {
        LineDataSet set = new LineDataSet(null, "SpO2");
        //平滑曲線
        //set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 折線的顏色
        set.setColor(Color.RED);
        //顯示座標小圓點
        set.setDrawCircles(true);
        //不顯示座標點數據
        set.setDrawValues(false);

        return set;
    }

    //初始化脈搏折線圖
    private LineDataSet create_Pulse_LineDataSet() {
        LineDataSet set = new LineDataSet(null, "Pulse");
        //平滑曲線
        //set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 折線的顏色
        set.setColor(Color.MAGENTA);
        //顯示座標小圓點
        set.setDrawCircles(true);
        //不顯示座標點數據
        set.setDrawValues(false);

        return set;
    }

    //加一個座標點
    public void addEntry(LineChart mChart,float xval,float yval) {
        LineData data = mChart.getData();

        //ILineDataSet t_LineDataSet = data.getDataSetByIndex(0);
        //Entry entry_spo2 = new Entry(t_LineDataSet.getEntryCount(),yval);
        Entry entry_spo2 = new Entry(xval,yval);
        data.addEntry(entry_spo2, 0);

        mChart.notifyDataSetChanged();

        mChart.setVisibleXRangeMaximum(1800*4);
        mChart.moveViewToX(data.getXMax());
    }

    public float xval_UuixDateTime(String dateStr){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            Date date = sdf.parse(dateStr);
            return (float) date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String getDisPlayNumber(int num) {
        return num < 10 ? "0" + num : "" + num;
    }
}
