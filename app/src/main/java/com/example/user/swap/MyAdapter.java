package com.example.user.swap;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MyAdapter extends BaseAdapter {
    ArrayList<BoardEntity> arItem;

    Context context;
    LayoutInflater inflater;

    class ViewHolder {
        public ImageView list_img;
        public ImageView list_mark;
        public TextView list_title;
        public TextView list_writer;
        public TextView list_pWant;
        public TextView list_CommentCnt;
        public TextView list_dealState;
    }

    public MyAdapter(Context context) {
        this.context = context;
        arItem = new ArrayList<>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ArrayList<BoardEntity> getArItem() {
        return arItem;
    }

    public void setArItem(ArrayList<BoardEntity> arItem) {
        this.arItem = arItem;
    }

    @Override
    public int getCount() {
        return arItem.size();
    }

    @Override
    public BoardEntity getItem(int position) {
        return arItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.listview_layout,parent,false);

            viewHolder = new ViewHolder();
            viewHolder.list_img = (ImageView)convertView.findViewById(R.id.list_img);
            viewHolder.list_mark = (ImageView)convertView.findViewById(R.id.list_mark);
            viewHolder.list_title = (TextView)convertView.findViewById(R.id.list_title);
            viewHolder.list_writer = (TextView)convertView.findViewById(R.id.list_writer);
            viewHolder.list_pWant = (TextView)convertView.findViewById(R.id.list_pWant);
            viewHolder.list_CommentCnt = (TextView)convertView.findViewById(R.id.list_CommentCnt);
            viewHolder.list_dealState = (TextView)convertView.findViewById(R.id.list_dealState);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final BoardEntity item = getItem(position);
        countComment(item.getId(),viewHolder.list_CommentCnt);
        markSelect(LoginActivity.Current_user, item.getId(), viewHolder.list_mark);


        String imageUrl = Config.HOME_URL+ "Image?id=" + item.getImage_id();
        Picasso.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.product_img)
                .into(viewHolder.list_img);
        viewHolder.list_title.setText("["+item.category+"]"+item.pName);
        if(item.getDealState()==0) viewHolder.list_dealState.setText("");
        else if(item.getDealState()==1) viewHolder.list_dealState.setText("거래중");
        else if(item.getDealState()==2) viewHolder.list_dealState.setText("거래완료");


        viewHolder.list_writer.setText("작성자 : "+item.userId);
        viewHolder.list_pWant.setText("선호대상 : "+item.pWant);

        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.list_mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());
                if(!view.isSelected()) {
                    finalViewHolder.list_mark.setImageResource(R.drawable.star1);
                    markDelete(LoginActivity.Current_user, item.getId());
                } else {
                    finalViewHolder.list_mark.setImageResource(R.drawable.star2);
                    markClicked(LoginActivity.Current_user, item.getId());
                }
            }
        });
        return convertView;
    }

    public void countComment(int id,TextView cnt){
        final TextView cc = cnt;
        //덧글 수 return해서 getView에서 설정하려고 했는데.. 자꾸 변수 문제때문에 큽..야매로 해놨으..
        AQuery aQuery = new AQuery(context);
        String url = Config.HOME_URL + "CountComment";

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id",id);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jReponse, AjaxStatus status) {
                if(jReponse == null){
                }
                else{
                    if (jReponse != null && !jReponse.isNull("result")) {
                        try {
                            cc.setText("댓글 수 : "+jReponse.getInt("result"));
                        }catch (JSONException e){Toast.makeText(context, "오류데스", Toast.LENGTH_LONG).show();}
                    }
                }
            }
        });
    }

    public void markSelect(String userId, long boardId, final ImageView iv) {
        AQuery aQuery = new AQuery(context);
        String url = Config.HOME_URL + "MarkSelect";

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("userId",userId);
        params.put("boardId",boardId);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jReponse, AjaxStatus status) {
                if(jReponse == null){}
                else{
                    try {
                        if (jReponse != null && !jReponse.isNull("result")) {
                            iv.setImageResource(R.drawable.star2);
                            iv.setSelected(true);
                        }else{
                            iv.setImageResource(R.drawable.star1);
                            iv.setSelected(false);
                        }
                    } catch(Exception e) {
                    }
                }
            }
        });
    }

    public void markClicked(String userId,long boardId) {
        AQuery aQuery = new AQuery(context);
        String url = Config.HOME_URL + "MarkInsert";

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("userId",userId);
        params.put("boardId",boardId);
        aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String result, AjaxStatus status) {
                try {
                    JSONObject jReponse = new JSONObject(result);

                    if (jReponse != null && !jReponse.isNull("result")) {
                        Toast.makeText(context, "저장", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "오류가 발생!!"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void markDelete(String userId,long boardId) {
        AQuery aQuery = new AQuery(context);
        String url = Config.HOME_URL + "MarkDelete";

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("userId",userId);
        params.put("boardId",boardId);
        aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String result, AjaxStatus status) {
                try {
                    JSONObject jReponse = new JSONObject(result);

                    if (jReponse != null && !jReponse.isNull("result")) {
                        Toast.makeText(context, "취소", Toast.LENGTH_SHORT).show();
                        if(((Activity)context).getFragmentManager().findFragmentByTag("mypage")!=null){
                            //마이페이지에서 즐겨찾기 취소한거면 리스트뷰 갱신해줘야됨
                            getMarkList();
                            notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "오류가 발생!!"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getMarkList() {
        AQuery aQuery = new AQuery(context);
        String url = Config.HOME_URL + "MarkList";

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("userId", LoginActivity.Current_user);

        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jReponse, AjaxStatus status) {
                if(jReponse == null){}
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
                            setArItem(arItem);
                            notifyDataSetChanged();
                        }else{
                            arItem = new ArrayList<BoardEntity>();
                            notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });
    }
}
