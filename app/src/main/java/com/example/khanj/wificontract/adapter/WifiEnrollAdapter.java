package com.example.khanj.wificontract.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.khanj.wificontract.R;
import com.example.khanj.wificontract.model.WifiEnrollModel;
import com.example.khanj.wificontract.model.WifiListModel;

import java.util.ArrayList;

/**
 * Created by jehug on 2018-10-07.
 */

public class WifiEnrollAdapter extends RecyclerView.Adapter<WifiEnrollAdapter.ViewHolder> {

    Context getclass;

    public ArrayList<WifiEnrollModel> Recycleritemlist = new ArrayList<>();

    // ListViewAdapter의 생성자
    public WifiEnrollAdapter(Context getclass) {
        this.getclass = getclass;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.f2_listview_custom, viewGroup, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Data Set(listViewItemRecycler)에서 position에 위치한 데이터 참조 획득
        WifiEnrollModel wifiListModel = Recycleritemlist.get(position);

        //viewHolder.tv_enrollnum.setText(wifiListModel.getEnrollNum());
        viewHolder.tv_wifiname.setText(wifiListModel.getWifiName());
        viewHolder.tv_mac.setText(wifiListModel.getMac());
        viewHolder.tv_endtime.setText(wifiListModel.getEndTime());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //public TextView tv_enrollnum;
        public TextView tv_wifiname;
        public TextView tv_mac;
        public TextView tv_endtime;

        public ViewHolder(View itemView) {
            super(itemView);
           // tv_enrollnum = itemView.findViewById(R.id.tv_enroll_num);
            tv_wifiname = itemView.findViewById(R.id.wifi_name);
            tv_mac = itemView.findViewById(R.id.mac_address);
            tv_endtime = itemView.findViewById(R.id.end_time);
        }

    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    public int getItemCount() {
        return Recycleritemlist.size();
    }


    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능. (와이파이 상태)
    public void addItem(String wifiname, String mac, String endtime) {
        WifiEnrollModel item = new WifiEnrollModel();

        //item.setEnrollNum(wifinum);
        item.setWifiName(wifiname);
        item.setMac(mac);
        item.setEndTime(endtime);
        Recycleritemlist.add(item);

    }


}
