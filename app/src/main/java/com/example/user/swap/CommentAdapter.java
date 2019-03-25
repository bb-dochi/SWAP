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
import android.widget.Switch;
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

public class CommentAdapter extends BaseAdapter {
    ArrayList<CommentEntity> arItem;
    BoardEntity boardItem;

    Context context;
    LayoutInflater inflater;

    int num = 0; //마이페이지 확인 변수

    class ViewHolder {
        public ImageView list_img;
        public TextView list_title;
        public TextView list_writer;
        public TextView list_content;
        public Button list_dealBtn;
        public RelativeLayout list_back;
        public int mode;
    }

    public CommentAdapter(Context context, BoardEntity boardEntity, int num) {
        this.context = context;
        this.boardItem = boardEntity; //게시물 거래여부 등 거래조작을 위해 게시물 정보 받아옴
        arItem = new ArrayList<>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.num = num;
    }

    public ArrayList<CommentEntity> getArItem() {
        return arItem;
    }

    public void setArItem(ArrayList<CommentEntity> arItem) {
        this.arItem = arItem;
    }

    @Override
    public int getCount() {
        return arItem.size();
    }

    @Override
    public CommentEntity getItem(int position) {
        return arItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_layout_c, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.list_img = (ImageView) convertView.findViewById(R.id.listC_img);
            viewHolder.list_title = (TextView) convertView.findViewById(R.id.listC_title);
            viewHolder.list_writer = (TextView) convertView.findViewById(R.id.listC_writer);
            viewHolder.list_content = (TextView) convertView.findViewById(R.id.listC_content);
            viewHolder.list_dealBtn = (Button) convertView.findViewById(R.id.dealBtn);
            viewHolder.list_back = (RelativeLayout) convertView.findViewById(R.id.listBack_c);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final CommentEntity item = getItem(position);
        String imageUrl = Config.HOME_URL + "Image?id=" + item.getImage_id();
        Picasso.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.product_img)
                .into(viewHolder.list_img);
        viewHolder.list_title.setText("[" + item.category + "]" + item.pName);
        viewHolder.list_writer.setText("작성자 : " + item.getUserId());
        viewHolder.list_content.setText(item.getpContent());

        if (num == 1) {//마이페이지에선 버튼 안보이게함
            viewHolder.list_dealBtn.setVisibility(View.INVISIBLE);
        } else if (num == 0) {
            //글작성자==현재유저 일 때만 버튼 보이게
            if (boardItem.getUserId().equals(LoginActivity.Current_user)) {
                viewHolder.list_dealBtn.setVisibility(View.VISIBLE);
                viewHolder.mode = 1;
                if (item.getSelectOk() == 1 || item.getSelectOk() == 2)  //선택된 버튼은
                    viewHolder.list_back.setBackgroundColor(0xFFE8D9FF);
                else
                    viewHolder.list_back.setBackgroundColor(0xFFFFFFFF);
            } else {
                //현재유저 == 덧글 작성자 && 덧글이 수락된 상태 > 최종수락 버튼이 보임
                if (item.getUserId().equals(LoginActivity.Current_user) && item.getSelectOk() == 1) {
                    viewHolder.list_dealBtn.setVisibility(View.VISIBLE);
                    viewHolder.mode = 2;
                    viewHolder.list_dealBtn.setText("최종수락");
                } else {
                    viewHolder.list_dealBtn.setVisibility(View.INVISIBLE);
                }
            }
        }

        viewHolder.list_dealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.mode == 1) {//수락버튼일때
                    if (boardItem.getDealState() != 0) { //거래중에 버튼 클릭 시 경고문
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        if (boardItem.getDealState() == 1) builder.setMessage("이미 거래중입니다.");
                        if (boardItem.getDealState() == 2) builder.setMessage("거래가 완료된 게시물입니다.");
                        builder.setNegativeButton("닫기", null);
                        builder.show();
                    } else { //처음 거래버튼 누를때
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("교환확인메세지");
                        builder.setMessage("[" + item.getpName() + "] 와 교환하시겠습니까?\n수락은 게시물 당 1회만 가능합니다.");
                        builder.setPositiveButton("수락", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deal(item.getUserId(), item.getId(), item.getBoardId());
                                viewHolder.list_back.setBackgroundColor(0xFFE8D9FF);
                            }
                        });
                        builder.setNegativeButton("거절", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                viewHolder.list_back.setBackgroundColor(0xFFDDDDFF);

                            }
                        });
                        builder.show();
                    }
                } else {//최종수락버튼 일때
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("거래시작메세지");
                    builder.setMessage("최종수락 하시겠습니까?\n수락 시 거래가 시작됩니다.");
                    builder.setPositiveButton("수락", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LastDealCheck(item.getUserId(), item.getId(), item.getBoardId(), "accept");
                        }
                    });
                    builder.setNegativeButton("거절", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LastDealCheck(item.getUserId(), item.getId(), item.getBoardId(), "refuse");
                        }
                    });
                    builder.show();
                }
            }
        });

        return convertView;
    }

    public void deal(String userId, int id, int boardId) {
        AQuery aQuery = new AQuery(context);
        String url = Config.HOME_URL + "Deal";

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("userId", userId); //덧글작성자
        params.put("id", id); //덧글id
        params.put("boardId", boardId); //게시물id

        aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String result, AjaxStatus status) {
                try {
                    JSONObject jReponse = new JSONObject(result);
                    if (jReponse != null && !jReponse.isNull("result")) {
                        Toast.makeText(context, "수락하였습니다. 상대가 확인할 때까지 기다려주세요", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "수락하는 중 오류가 발생 하였습니다" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void LastDealCheck(String userId, int id, int boardId, final String check) {
        AQuery aQuery = new AQuery(context);
        String url = Config.HOME_URL + "DealCheck";

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", id); //덧글id
        params.put("boardId", boardId); //게시물id
        params.put("check", check); //덧글 수락 여부

        aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String result, AjaxStatus status) {
                try {
                    JSONObject jReponse = new JSONObject(result);
                    if (jReponse != null && !jReponse.isNull("result")) {
                        if (check.equals("accept"))
                            Toast.makeText(context, "최종 수락하였습니다. 거래가 시작됩니다.", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, "거절하셨습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "수락하는 중 오류가 발생 하였습니다" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
