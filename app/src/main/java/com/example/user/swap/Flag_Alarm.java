package com.example.user.swap;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Flag_Alarm extends Fragment{
    View v;
    ListView listView;
    AlarmAdapter adapter;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.flag_alarm, container, false);
        ((MainActivity)getActivity()).spinnerView(0);
        listView = (ListView)v.findViewById(R.id.alarm_listview);
        adapter=new AlarmAdapter(getActivity());
        getAlarm();
        listView.setAdapter(adapter);
        return v;
    }

    public void getAlarm(){
        AQuery aQuery = new AQuery(getActivity());
        String url = Config.HOME_URL + "AlarmList";

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("userId",LoginActivity.Current_user);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jReponse, AjaxStatus status) {
                if(jReponse == null){
                }
                else{
                    Gson gson = new Gson();
                    if (jReponse != null && !jReponse.isNull("result")) {
                        JSONArray jarBoard = jReponse.optJSONArray("result");
                        ArrayList<AlarmEntity> arItem = new ArrayList<AlarmEntity>();
                        for(int i=0; i<jarBoard.length(); i++){
                            AlarmEntity alarmEntity = gson.fromJson(jarBoard.optJSONObject(i).toString(), AlarmEntity.class);

                            if(alarmEntity != null){
                                arItem.add(alarmEntity);
                            }
                        }
                        adapter.setArItem(arItem);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
