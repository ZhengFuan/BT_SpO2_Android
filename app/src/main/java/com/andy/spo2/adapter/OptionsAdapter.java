package com.andy.spo2.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andy.spo2.R;

import java.util.List;

public class OptionsAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List groups;
    private List<List> childs;
    private Boolean ble_Terminal_isOpen = false;
    View terminalView;
    public OptionsAdapter(Context context,List groups,List<List> childs,View terminalView){
        this.context = context;
        this.groups = groups;
        this.childs = childs;
        this.terminalView = terminalView;
    }

    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    public int getGroupCount() {
        return groups.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.elv_options_group_item, null);

        TextView tv_group = (TextView)convertView.findViewById(R.id.tv_group);
        tv_group.setText(groups.get(groupPosition)+"");
        LinearLayout updivider = (LinearLayout) convertView.findViewById(R.id.updivider);
        if(groupPosition==0){
            updivider.setVisibility(View.GONE);
        }else{
            updivider.setVisibility(View.VISIBLE);
        }
        LinearLayout downdivider = (LinearLayout) convertView.findViewById(R.id.downdivider);

        ImageView img_arrow = (ImageView)convertView.findViewById(R.id.img_arrow);
        if(getChildrenCount(groupPosition)>0){
            if(!isExpanded){
                img_arrow.setBackgroundResource(R.drawable.ic_arrow_right);
                downdivider.setVisibility(View.VISIBLE);
                if(groupPosition==2){
                    ble_Terminal_isOpen=false;
                }
            }else{
                img_arrow.setBackgroundResource(R.drawable.ic_arrow_down);
                downdivider.setVisibility(View.GONE);
                if(groupPosition==2){
                    ble_Terminal_isOpen=true;
                }
            }
        }else{
            img_arrow.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return childs.get(groupPosition).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        int count=0;
        try{
            count = childs.get(groupPosition).size();
        }catch (Exception e){
            count = 0;
        }
        return count;
    }

    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if(groupPosition==2&&childPosition==0){
            return terminalView;

        }else{
            convertView = inflater.inflate(R.layout.elv_options_child_item, null);

            TextView tv_child = (TextView)convertView.findViewById(R.id.tv_child);
            String data = (String) getChild(groupPosition,childPosition);
            tv_child.setText(data);
        }

        return convertView;
    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public Boolean ble_terminal_isOpen() {
        return ble_Terminal_isOpen;
    }
}
