package com.example.khanj.wificontract.Wifi;


import android.app.Activity;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.khanj.wificontract.R;
import com.example.khanj.wificontract.WifiEnrollActivity;
import com.example.khanj.wificontract.adapter.WifiEnrollAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class WifiEnrollFragment extends Fragment {
    private RelativeLayout noListData;
    private SwipeRefreshLayout pullToRefresh;
    private WifiEnrollAdapter adapter;

    // RecyclerView를 Fragment에 추가하기 위한 코드
    private RecyclerView rv_rollWifiList;
    private Context main_context = getActivity();

    // wifi 등록 수,이름, 맥주소, 시간, 비밀번호
    private int enrollNum;
    private char permision_time = 3;
    private TextView tv_wifienrollnum;
    private String wifi_id ;
    private String mac_address;
    // 나중에 데이터 타입 바꿔야....
    private String enroll_time ;
    private ArrayList<String> wifi_password = new ArrayList<>();

    //블록체인에서 가져올 값들
    int numtemp = 0;
    ArrayList<HashMap<String, String>> temp = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment2_enroll, container, false);

        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(clickFab);

        //        pullToRefresh = v.findViewById(R.id.pullToRefresh);
        //        pullToRefresh.setOnRefreshListener(this);
        noListData = v.findViewById(R.id.no_listdata);
        buildRecyclerView(v);

        return v;
    }


    private void buildRecyclerView(View v) {

        tv_wifienrollnum = v.findViewById(R.id.tv_enroll_num);
        rv_rollWifiList = v.findViewById(R.id.rv_enroll_list);
        rv_rollWifiList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new WifiEnrollAdapter(main_context);
        rv_rollWifiList.setItemAnimator(new DefaultItemAnimator());
        rv_rollWifiList.setNestedScrollingEnabled(false);
        rv_rollWifiList.setAdapter(adapter);

        tempSetting();
        setAddItem(v);

    }

    private void tempSetting(){
        numtemp=2;
        HashMap<String,String> hash1 = new HashMap<>();
        hash1.put("wifiID","a1");
        hash1.put("macAddress","b1");
        hash1.put("enrollTime","1");
        hash1.put("wifipassword","c1");
        temp.add(hash1);

        HashMap<String,String> hash2 = new HashMap<>();
        hash2.put("wifiID","a2");
        hash2.put("macAddress","b2");
        hash2.put("enrollTime","2");
        hash2.put("wifipassword","c2");

        temp.add(hash2);

    }
    private void setAddItem(View v) {
        enrollNum = numtemp;
        if (enrollNum == 0) {
            noListData.setVisibility(v.VISIBLE);
        } else {
            noListData.setVisibility(View.GONE);
            int num = enrollNum;
            // 이름, 맥주소, 시간, 비밀번호

            for (int i = 0; i < num; i++) {
                ArrayList<HashMap<String, String>> dataTotal = temp;

                for (HashMap<String, String> dataSet : dataTotal) {
                    for (Map.Entry<String, String> data : dataSet.entrySet()) {
                        if (data.getKey().equals("wifiID")) {
                            wifi_id = data.getValue();
                        } else if (data.getKey().equals("macAddress")) {
                            mac_address = data.getValue();
                        } else if (data.getKey().equals("enrollTime")) {
                            enroll_time = data.getValue();
                        } else if (data.getKey().equals("wifipassword")) {
                            wifi_password.add(data.getValue());
                        }
                    }
                }
                adapter.addItem(wifi_id, mac_address, enroll_time + permision_time );
            }
        }
    }


    //플로팅버튼 클릭 함수
    private View.OnClickListener clickFab = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), WifiEnrollActivity.class);
            startActivity(intent);
            ((Activity)v.getContext()).finish();
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
