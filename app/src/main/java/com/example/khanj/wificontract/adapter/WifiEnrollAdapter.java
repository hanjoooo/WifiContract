package com.example.khanj.wificontract.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.khanj.wificontract.R;
import com.example.khanj.wificontract.model.WifiEnrollModel;

import java.util.ArrayList;

/**
 * Created by jehug on 2018-10-07.
 */

public class WifiEnrollAdapter extends RecyclerView.Adapter<WifiEnrollAdapter.ItemViewHolder> {

    private WalletRecyclerViewAdapter.OnItemClickListener mListener;

    public ArrayList<WifiEnrollModel> mItems;

    // ListViewAdapter의 생성자
    public WifiEnrollAdapter(ArrayList<WifiEnrollModel> mitem) {
        mItems = mitem;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        public ViewHolder(View view){
            super(view);
            this.view=view;
        }
    }
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(WalletRecyclerViewAdapter.OnItemClickListener listener){
        mListener = listener;
    }
    @Override
    public WifiEnrollAdapter.ItemViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.f2_listview_custom,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WifiEnrollAdapter.ItemViewHolder holder, int position) {
        WifiEnrollModel wifiListModel = mItems.get(position);

        //viewHolder.tv_enrollnum.setText(wifiListModel.getEnrollNum());
        holder.tv_wifiname.setText(wifiListModel.getWifiName());

    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        //public TextView tv_enrollnum;
        public TextView tv_wifiname;


        public ItemViewHolder(View itemView) {
            super(itemView);
           // tv_enrollnum = itemView.findViewById(R.id.tv_enroll_num);
            tv_wifiname = itemView.findViewById(R.id.wifi_name);

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


    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능. (와이파이 상태)


}
