package com.andy.spo2.util;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppUtils {
    private static List list = new ArrayList();
    public static List<HashMap> testDataList(){
        if(list.size()==0){
            list.add(testMap("09:00:00","50","90"));
            list.add(testMap("09:10:00","95","70"));
            list.add(testMap("09:11:00","96","86"));
            list.add(testMap("09:12:00","98","97"));
            list.add(testMap("09:30:00","97","75"));
            list.add(testMap("10:05:00","99","80"));
            list.add(testMap("10:30:00","96","89"));
            list.add(testMap("10:33:00","99","99"));
            list.add(testMap("10:40:00","96","96"));
            list.add(testMap("10:45:00","99","98"));
            list.add(testMap("11:20:00","96","95"));
            list.add(testMap("11:30:00","97","110"));
            list.add(testMap("12:03:00","90","87"));
            list.add(testMap("13:15:00","92","100"));
            list.add(testMap("14:30:00","96","94"));
            list.add(testMap("14:50:00","99","95"));
            list.add(testMap("15:10:00","93","90"));
            list.add(testMap("15:15:00","90","96"));
            list.add(testMap("16:25:00","60","66"));
            list.add(testMap("17:00:00","98","97"));
            list.add(testMap("17:25:00","88","96"));
            list.add(testMap("17:45:00","86","94"));
            list.add(testMap("18:25:00","98","99"));
        }
        return list;
    }

    private static HashMap<String,String> testMap(String dateTime,String spo2,String pulse){
        HashMap<String,String> map = new HashMap<>();
        map.put("dateTime",dateTime);
        map.put("spo2",spo2);
        map.put("pulse",pulse);
        return map;
    }

    public static int px(float dipValue) {
        Resources r=Resources.getSystem();
        final float scale =r.getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
