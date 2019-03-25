package com.example.user.swap;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Flag_SubMenu extends Fragment {
    View v;
    TextView category,tv_Noboard;
    ListView listView;
    MyAdapter adapter;
    int layoutId;
    int dealstateId;
    String searchText = null;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        layoutId = getArguments().getInt("id");
        dealstateId = getArguments().getInt("dealstate_id");
        searchText = getArguments().getString("searchText");

        adapter=new MyAdapter(getActivity());
        ((MainActivity)getActivity()).spinnerView(1);
        if(searchText != null) {
            v = inflater.inflate(R.layout.flag_mainlayout, container, false);
            listView = (ListView) v.findViewById(R.id.main_listview);
            search(searchText);
        } else {
            //메인화면
            if (layoutId == 0) {
                v = inflater.inflate(R.layout.flag_mainlayout, container, false);
                tv_Noboard = (TextView) v.findViewById(R.id.tv_Noboard);
                listView = (ListView) v.findViewById(R.id.main_listview);
                getBoardList("all");
            }//서브 카테고리화면
            else {
                v = inflater.inflate(R.layout.flag_submenu, container, false);
                category = (TextView) v.findViewById(R.id.category);
                tv_Noboard = (TextView) v.findViewById(R.id.tv_Noboard);
                listView = (ListView) v.findViewById(R.id.sub_listview);
                switch (layoutId) {
                    case 1:
                        category.setText("생활용품");
                        break;
                    case 2:
                        category.setText("전자기기");
                        break;
                    case 3:
                        category.setText("패션");
                        break;
                    case 4:
                        category.setText("기타");
                        break;
                }
                getBoardList(category.getText().toString());
            }
        }

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BoardEntity item = adapter.getItem(position);
                //클릭한 게시물 정보 가져와서 bundle로 넘김
                Fragment f= new Flag_BoardView();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putSerializable("boardData",item);
                f.setArguments(bundle);
                String fragmentTag = "게시물상세";
                getFragmentManager().popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ft.replace(R.id.content_main, f).addToBackStack(fragmentTag).commit();
            }
        });
        return v;
    }

   public void getBoardList(String catagory) {
        AQuery aQuery = new AQuery(getActivity());
        String url = Config.HOME_URL + "BoardList";

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("category",catagory);
       params.put("dealstate", dealstateId);

        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jReponse, AjaxStatus status) {
                if(jReponse == null){

                }
                else{
                    try {
                        Gson gson = new Gson();
                        if (jReponse != null && !jReponse.isNull("result")) {
                            JSONArray jarBoard = jReponse.optJSONArray("result");

                            ArrayList<BoardEntity> arItem = new ArrayList<BoardEntity>();
                            for(int i=0; i<jarBoard.length(); i++){
                                BoardEntity boardEntity = gson.fromJson(jarBoard.optJSONObject(i).toString(), BoardEntity.class);
                                if(boardEntity != null){
                                    arItem.add(boardEntity);
                                }
                            }
                            adapter.setArItem(arItem);
                            adapter.notifyDataSetChanged();
                        }else{ tv_Noboard.setText("게시물이 없습니다."); }
                    } catch (Exception e) {
                    }
                }

            }
        });
    }

    public void search(String searchText) {
        AQuery aQuery = new AQuery(getActivity());
        String url = Config.HOME_URL + "Search";

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("searchText",searchText);

        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jReponse, AjaxStatus status) {
                if(jReponse == null){
                }
                else{
                    try {
                        Gson gson = new Gson();
                        if (jReponse != null && !jReponse.isNull("result")) {
                            JSONArray jarBoard = jReponse.optJSONArray("result");

                            ArrayList<BoardEntity> arItem = new ArrayList<BoardEntity>();
                            for(int i=0; i<jarBoard.length(); i++){
                                BoardEntity boardEntity = gson.fromJson(jarBoard.optJSONObject(i).toString(), BoardEntity.class);
                                if(boardEntity != null){
                                    arItem.add(boardEntity);
                                }
                            }

                            adapter.setArItem(arItem);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                    }
                }

            }
        });
    }

    @Override
    public void onResume() {
        layoutId = getArguments().getInt("id");
        if(layoutId==0)
            getBoardList("all");
        else
            getBoardList(category.getText().toString());
        adapter.notifyDataSetChanged();
        super.onResume();
    }

}
