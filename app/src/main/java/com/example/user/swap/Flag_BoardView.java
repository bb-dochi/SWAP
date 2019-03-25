package com.example.user.swap;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Flag_BoardView extends Fragment{
    public static int isPreVisible =0;
    View v;
    ImageView board_img;
    TextView board_title,board_content,board_writer,board_pWant;
    Button writeComment,board_delBtn;
    ListView commentList;
    CommentAdapter adapter;
    int boardId;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.flag_board, container, false);
        final BoardEntity item = (BoardEntity)getArguments().get("boardData");

        ((MainActivity)getActivity()).spinnerView(0);
        adapter=new CommentAdapter(getActivity(),item,0);
        board_img=(ImageView)v.findViewById(R.id.board_img);
        board_title=(TextView)v.findViewById(R.id.board_title);
        board_content=(TextView)v.findViewById(R.id.board_content);
        board_writer=(TextView)v.findViewById(R.id.board_writer);
        board_pWant=(TextView)v.findViewById(R.id.board_pWant);
        board_delBtn=(Button)v.findViewById(R.id.board_delBtn);
        writeComment=(Button)v.findViewById(R.id.writeComment);
        commentList=(ListView)v.findViewById(R.id.comment_list);

        boardId = item.getId();
        getComments(boardId);
        commentList.setAdapter(adapter);


        String imageUrl = Config.HOME_URL+ "Image?id=" + item.getImage_id();
        Picasso.with(getActivity())
                .load(imageUrl)
                .placeholder(R.drawable.product_img)
                .into(board_img);
        board_title.setText("["+item.getCategory()+"]"+item.getpName());
        board_content.setText(item.getpContent());
        board_writer.setText("작성자 : "+item.getUserId());
        board_pWant.setText("선호대상 : "+item.getpWant());

        if(LoginActivity.Current_user.equals(item.getUserId())) {
            board_delBtn.setVisibility(View.VISIBLE);
            writeComment.setVisibility(View.INVISIBLE);
        }
        board_delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delteBoard(item.getId(),item.getImage_id());
            }
        });
        writeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.getDealState()==2){
                    Toast.makeText(getActivity(),"거래가 완료된 게시물 입니다.",Toast.LENGTH_LONG).show();
                }else {
                    Intent it = new Intent(getActivity(), WriteComment.class);
                    it.putExtra("boardId", item.getId());
                    startActivity(it);
                }
            }
        });
        return v;
    }

    public void getComments(int boardId){
        AQuery aQuery = new AQuery(getActivity());
        String url = Config.HOME_URL + "CommentList";

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("boardId",boardId);
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
                            adapter.setArItem(arItem);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
        });
    }
    public void delteBoard(int id,int image_id){
        AQuery aQuery = new AQuery(getActivity());
        String url = Config.HOME_URL + "BoardDelete";

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id);
        params.put("image_id", image_id);

        aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String result, AjaxStatus status) {
                try {
                    JSONObject jReponse = new JSONObject(result);
                    if (jReponse != null && !jReponse.isNull("result")) {
                        Toast.makeText(getActivity(), "삭제 되었습니다", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "삭제 하는데 오류가 발생 하였습니다"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        getComments(boardId);
        super.onResume();
    }
}
