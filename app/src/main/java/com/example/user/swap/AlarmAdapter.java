package com.example.user.swap;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class AlarmAdapter extends BaseAdapter {
    ArrayList<AlarmEntity> arItem;
    AlarmEntity alarmEntity;

    Context context;
    LayoutInflater inflater;

    class ViewHolder {
        public TextView alarmTitle;
        public TextView alarmContent;
    }
    public AlarmAdapter(Context context) {
        this.context = context;
        arItem = new ArrayList<>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ArrayList<AlarmEntity> getArItem() {
        return arItem;
    }

    public void setArItem(ArrayList<AlarmEntity> arItem) {
        this.arItem = arItem;
    }

    @Override
    public int getCount() {
        return arItem.size();
    }

    @Override
    public AlarmEntity getItem(int position) {
        return this.arItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.listview_layout_alarm,parent,false);

            viewHolder = new ViewHolder();
            viewHolder.alarmTitle = (TextView)convertView.findViewById(R.id.alarm_title);
            viewHolder.alarmContent = (TextView)convertView.findViewById(R.id.alarm_content);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AlarmEntity item = getItem(position);
        viewHolder.alarmTitle.setText(""+item.getAlarmTitle());
        viewHolder.alarmContent.setText(""+item.getAlarmContent());
        return convertView;
    }

}
