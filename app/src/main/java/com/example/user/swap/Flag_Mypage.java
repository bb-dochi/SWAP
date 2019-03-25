package com.example.user.swap;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Flag_Mypage extends Fragment {
    View v;
    int layoutId;

    TextView btnUserEdit, btnLogout;
    Button mypage_btnboard, mypage_btncomment, mypage_btndeal,mypage_btnmark;
    ListView mypage_listview;
    TextView mypage_tvuser;

    MyAdapter adapter, madapter;
    CommentAdapter adapter2;
    DealAdapter dAdapter;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        layoutId = getArguments().getInt("mypage");
        v = inflater.inflate(R.layout.flag_mypage, container, false);

        ((MainActivity)getActivity()).spinnerView(0);
        mypage_listview = (ListView)v.findViewById(R.id.mypage_listview);
        mypage_tvuser = (TextView)v.findViewById(R.id.mypage_tvuser);
        mypage_btnboard = (Button)v.findViewById(R.id.mypage_btnboard);
        mypage_btncomment = (Button)v.findViewById(R.id.mypage_btncomment);
        mypage_btndeal = (Button)v.findViewById(R.id.mypage_btndeal);
        mypage_btnmark = (Button)v.findViewById(R.id.mypage_btnmark);
        btnUserEdit = (TextView)v.findViewById(R.id.btnUserEdit);
        btnLogout = (TextView)v.findViewById(R.id.btnLogout);

        adapter=new MyAdapter(getActivity());
        adapter2=new CommentAdapter(getActivity(), null, 1);
        madapter=new MyAdapter(getActivity());
        dAdapter=new DealAdapter(getActivity());

        mypage_tvuser.setText(LoginActivity.Current_user + "님  ");

        btnUserEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(v.getContext(), SignupActivity.class);
                i.putExtra("signup", 2);
                startActivity(i);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLogout();
            }
        });

        mypage_btnboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBoardList();
                mypage_listview.setAdapter(adapter);
                mypage_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        BoardEntity item = adapter.getItem(position);
                        //클릭한 게시물 정보 가져와서 bundle로 넘김
                        Fragment f = new Flag_BoardView();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("boardData", item);
                        f.setArguments(bundle);
                        ft.replace(R.id.content_main, f).addToBackStack(null).commit();
                    }
                });
            }
        });

        mypage_btncomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getComments();
                mypage_listview.setAdapter(adapter2);
            }
        });

        mypage_btnmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMarkList();
                mypage_listview.setAdapter(madapter);
            }
        });

        mypage_btndeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDealList();
                mypage_listview.setAdapter(dAdapter);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        System.out.print("onResume");
        super.onResume();
    }

    public void getBoardList() {
        AQuery aQuery = new AQuery(getActivity());
        String url = Config.HOME_URL + "UserBoardList";

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("userId", LoginActivity.Current_user);

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
    public void getDealList() {
        AQuery aQuery = new AQuery(getActivity());
        String url = Config.HOME_URL + "DealList";

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("userId", LoginActivity.Current_user);

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

                            ArrayList<DealEntity> arItem = new ArrayList<DealEntity>();
                            for(int i=0; i<jarBoard.length(); i++) {
                                DealEntity dealEntity = gson.fromJson(jarBoard.optJSONObject(i).toString(), DealEntity.class);
                                if (dealEntity != null) {
                                    arItem.add(dealEntity);
                                }
                            }
                            dAdapter.setArItem(arItem);
                            dAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                    }
                }

            }
        });
    }
    public void getComments(){
        AQuery aQuery = new AQuery(getActivity());
        String url = Config.HOME_URL + "UserCommentList";

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

                        ArrayList<CommentEntity> arItem = new ArrayList<CommentEntity>();
                        for(int i=0; i<jarBoard.length(); i++){
                            CommentEntity commentEntity = gson.fromJson(jarBoard.optJSONObject(i).toString(), CommentEntity.class);
                            if(commentEntity != null){
                                arItem.add(commentEntity);
                            }
                        }
                        adapter2.setArItem(arItem);
                        adapter2.notifyDataSetChanged();
                    }
                }
            }
        });
    }
    public void getMarkList() {
        AQuery aQuery = new AQuery(getActivity());
        String url = Config.HOME_URL + "MarkList";

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("userId", LoginActivity.Current_user);

        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jReponse, AjaxStatus status) {
                if(jReponse == null){System.out.println("없어요1");
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
                            madapter.setArItem(arItem);
                            madapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                    }
                }

            }
        });
    }

    private void onClickLogout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                startActivity(new Intent(v.getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });
    }
    private void onClickUnlink() {
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(getActivity())
                .setMessage(appendMessage)
                .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        Logger.e(errorResult.toString());
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        //redirectLoginActivity();
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        //redirectSignupActivity();
                                    }

                                    @Override
                                    public void onSuccess(Long userId) {
                                        Intent it = new Intent(getActivity(),LoginActivity.class);
                                        startActivity(it);
                                        //redirectLoginActivity();
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

    }
}
