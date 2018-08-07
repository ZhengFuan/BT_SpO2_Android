package com.andy.spo2.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.andy.spo2.R;
import com.andy.spo2.adapter.OptionsAdapter;
import com.andy.spo2.adapter.TerminalAdapter;

import java.util.ArrayList;
import java.util.List;

public class OptionsFragment extends Fragment {
    private View currentView;
    private List groups = new ArrayList();
    private List<List> childs = new ArrayList();
    private ExpandableListView elv_options;
    OptionsAdapter optionsAdapter;

    List<String> msgList = new ArrayList<>();
    ListView lv_console;
    View terminalView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_options, container,false);

        elv_options = (ExpandableListView) currentView.findViewById(R.id.elv_options);

        groups.add("血氧機狀態");
        List list01 = new ArrayList();
        list01.add("配對血氧機");
        list01.add("同步血氧機時間");
        list01.add("電量:");
        childs.add(list01);

        groups.add("健康管理平台");
        List list02 = new ArrayList();
        list02.add("帳號登入");
        list02.add("本機歷史資料備份至平台");
        list02.add("開啟健康管理平台網頁");
        list02.add("下載備份資料");
        childs.add(list02);

        groups.add("BLE Terminal");
        List list03 = new ArrayList();
        list03.add("");
        childs.add(list03);

        setLvConsole();
        optionsAdapter = new OptionsAdapter(getActivity(),groups,childs,terminalView);
        elv_options.setAdapter(optionsAdapter);

        return currentView;
    }

    public Boolean terminal_isOpen(){
        return (optionsAdapter!=null)?optionsAdapter.ble_terminal_isOpen():false;
    }

    public void updateTerminalData(String data){
        msgList.add(data);
        TerminalAdapter terminalAdapter = new TerminalAdapter(getActivity());
        terminalAdapter.addAll(msgList);
        lv_console.setAdapter(terminalAdapter);
        lv_console.setSelection(msgList.size()-1);
    }

    private void setLvConsole(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        terminalView = inflater.inflate(R.layout.ble_terminal, null);
        lv_console = (ListView)terminalView.findViewById(R.id.lv_console);
        TerminalAdapter terminalAdapter = new TerminalAdapter(getActivity());
        terminalAdapter.addAll(msgList);
        lv_console.setAdapter(terminalAdapter);
    }
}
