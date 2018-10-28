package com.example.khanj.wificontract.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.khanj.wificontract.R;
import com.example.khanj.wificontract.model.WifiListModel;

import java.util.ArrayList;


public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.ItemViewHolder> {

    public ArrayList<WifiListModel> mItems = new ArrayList<>();
    private WifiListAdapter.OnItemClickListener mListener;

    public WifiListAdapter(ArrayList<WifiListModel> mitem) {
        mItems = mitem;
    }

    @Override
    public WifiListAdapter.ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.f1_listview_custom, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WifiListAdapter.ItemViewHolder viewHolder, int position) {
        WifiListModel wifiListModel = mItems.get(position);
        viewHolder.iconImageView.setImageDrawable(wifiListModel.getIcon());
        viewHolder.titleTextView.setText(wifiListModel.getSsid());
        viewHolder.titleTextView.setTextColor(Color.BLACK);
        // viewHolder.descTextView.setText(wifiListModel.getSsid());

        if (wifiListModel.getAvai()) {
            viewHolder.background.setBackgroundColor(Color.parseColor("#cc99ff"));
        }else{
            viewHolder.background.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView descTextView;
        public ImageView iconImageView;
        public LinearLayout background;

        public ItemViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.ssid);
            descTextView = itemView.findViewById(R.id.price);
            iconImageView = itemView.findViewById(R.id.icon);
            background = itemView.findViewById(R.id.itembackground);
        }

    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    public int getItemCount() {
        return mItems.size();
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    public WifiListModel getObject(int position) {
        return mItems.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(WifiListAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

}
