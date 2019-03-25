package com.example.user.swap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DealAdapter extends BaseAdapter {
    ArrayList<DealEntity> dealItems;

    Context context;
    LayoutInflater inflater;

    class ViewHolder {
        public TextView sellerId;
        public TextView buyerId;
        public TextView sellAndBuyPd;
        public ImageView dealComplete;
    }
    public DealAdapter(Context context) {
        this.context = context;
        dealItems=new ArrayList<>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ArrayList<DealEntity> getArItem() {
        return dealItems;
    }

    public void setArItem(ArrayList<DealEntity> arItem) {
        this.dealItems = arItem;
    }

    @Override
    public int getCount() {
        return dealItems.size();
    }

    @Override
    public DealEntity getItem(int position) {
        return this.dealItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.listview_layout_deal,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.sellerId = (TextView)convertView.findViewById(R.id.seller);
            viewHolder.buyerId = (TextView)convertView.findViewById(R.id.buyer);
            viewHolder.sellAndBuyPd = (TextView)convertView.findViewById(R.id.sellAndBuyPd);
            viewHolder.dealComplete = (ImageView) convertView.findViewById(R.id.dealComplete);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DealEntity item = getItem(position);
        viewHolder.sellerId.setText("판매자 : "+item.getSellerId());
        viewHolder.buyerId.setText("구매자 : "+item.getBuyerId());
        if(item.getDealComplete()!=1) {
            viewHolder.sellAndBuyPd.setText("[" + item.getSellPd() + "] 과 [" + item.getBuyPd() + " ]를 교환중입니다.");
            viewHolder.dealComplete.setImageResource(R.drawable.handshake2);
        } else{
            viewHolder.sellAndBuyPd.setText("[" + item.getSellPd() + "] 과 [" + item.getBuyPd() + " ]를 교환완료 하였습니다.");
            viewHolder.dealComplete.setImageResource(R.drawable.handshake);
        }


        return convertView;
    }
}
