package com.example.khanj.wificontract.Wifi;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.khanj.wificontract.R;
import com.example.khanj.wificontract.WifiEnrollActivity;
import com.example.khanj.wificontract.adapter.WifiEnrollAdapter;
import com.example.khanj.wificontract.model.WifiEnrollModel;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class WifiEnrollFragment extends Fragment {
    private LinearLayout noListData;
    private SwipeRefreshLayout pullToRefresh;
    private WifiEnrollAdapter adapter;

    // RecyclerView를 Fragment에 추가하기 위한 코드
    private RecyclerView rv_rollWifiList;

    // wifi 등록 수,이름, 맥주소, 시간, 비밀번호
    private TextView tv_wifienrollnum;
    private ArrayList<WifiEnrollModel> mItems =new ArrayList<>();
    private TextView txWifinum;
    //블록체인에서 가져올 값들
    int numtemp = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment2_enroll, container, false);

        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(clickFab);

        noListData = v.findViewById(R.id.no_listdata);
        txWifinum = (TextView)v.findViewById(R.id.txwifinum);

        //        pullToRefresh = v.findViewById(R.id.pullToRefresh);
        //        pullToRefresh.setOnRefreshListener(this);
        adapter = new WifiEnrollAdapter(mItems);
        rv_rollWifiList = (RecyclerView) v.findViewById(R.id.rv_enroll_list);
        rv_rollWifiList.setAdapter(adapter);
        rv_rollWifiList.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        WifiEnrollModel wifiEnrollModel =new WifiEnrollModel("12313","한주","2시간","113");
        WifiEnrollModel wifiEnrollModel1 =new WifiEnrollModel("12313","주혁","2시간","113");

        mItems.clear();
        mItems.add(wifiEnrollModel);
        adapter.notifyDataSetChanged();
        mItems.add(wifiEnrollModel1);
        adapter.notifyDataSetChanged();
        noListData.setVisibility(View.GONE);
        txWifinum.setText(""+mItems.size());
        return v;
    }


    private void buildRecyclerView(View v) {

        tv_wifienrollnum = v.findViewById(R.id.tv_enroll_num);
        rv_rollWifiList = v.findViewById(R.id.rv_enroll_list);
        rv_rollWifiList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new WifiEnrollAdapter(mItems);
        rv_rollWifiList.setItemAnimator(new DefaultItemAnimator());
        rv_rollWifiList.setNestedScrollingEnabled(false);
        rv_rollWifiList.setAdapter(adapter);

        tempSetting();
        setAddItem(v);

    }

    private void tempSetting(){

    }
    private void setAddItem(View v) {

    }


    //플로팅버튼 클릭 함수
    private View.OnClickListener clickFab = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), WifiEnrollActivity.class);
            startActivity(intent);
        }

    };

//    @Override
//    public void onRefresh(){
//        pullToRefresh.setRefreshing(true);
//        refreshContent();
//    }
//    public void refreshContent(){
//        new Handler().postDelayed(new Runnable() {
//            @Override public void run() {
//                adapter.notifyDataSetChanged();
//
//            }
//        }, 500);
//        pullToRefresh.setRefreshing(false);
//    }


}
