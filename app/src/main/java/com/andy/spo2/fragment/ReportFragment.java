package com.andy.spo2.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.andy.spo2.MainActivity;
import com.andy.spo2.R;
import com.andy.spo2.util.AppUtils;
import com.codbking.calendar.CaledarAdapter;
import com.codbking.calendar.CalendarBean;
import com.codbking.calendar.CalendarDateView;
import com.codbking.calendar.CalendarUtil;
import com.codbking.calendar.CalendarView;

import java.util.Date;

public class ReportFragment extends Fragment {
    private View currentView;
    ListView lv_report;
    CalendarDateView mCalendarDateView;
    public TextView mTitle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_report, container,false);

        mTitle = (TextView) currentView.findViewById(R.id.title);
        mCalendarDateView = (CalendarDateView)currentView.findViewById(R.id.calendarDateView);

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
                lv_report.setAdapter(((MainActivity)getActivity()).
                        queryData(bean.year+"-"+getDisPlayNumber(bean.moth)+"-"+getDisPlayNumber(bean.day)));
            }
        });

        int[] data = CalendarUtil.getYMD(new Date());
        mTitle.setText(data[0] + "/" + getDisPlayNumber(data[1]) + "/" + getDisPlayNumber(data[2]));

        lv_report = (ListView)currentView.findViewById(R.id.lv_report);
        lv_report.setAdapter(((MainActivity)getActivity()).
                queryData(data[0]+"-"+getDisPlayNumber(data[1])+"-"+getDisPlayNumber(data[2])));

        return currentView;
    }

    private String getDisPlayNumber(int num) {
        return num < 10 ? "0" + num : "" + num;
    }
}
