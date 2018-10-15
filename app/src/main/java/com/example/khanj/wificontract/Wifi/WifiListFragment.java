package com.example.khanj.wificontract.Wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.khanj.wificontract.R;
import com.example.khanj.wificontract.adapter.WifiListAdapter;
import com.example.khanj.wificontract.model.WifiEnrollModel;
import com.example.khanj.wificontract.model.WifiListModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class WifiListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private WifiListAdapter adapter;
    private List<ScanResult> scanDatas;
    private WifiManager wifiManager;
    private SwipeRefreshLayout pullToRefresh;

    // RecyclerView를 Fragment에 추가하기 위한 코드
    private RecyclerView rvwifilist;
    private ArrayList<WifiListModel> mItems = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 최초에 Fragment가 호출되었을 때 와이파이가 꺼져있으면 강제로 켜도록 함
        // 우리는 wifi를 굳이 킬 필요가 있을까?

        View view = inflater.inflate(R.layout.fragment1_wifi_list, container, false);

        // adpater 추가
        adapter = new WifiListAdapter(mItems);

        // RecyclerView를 Fragment에 추가하기 위한 코드
        rvwifilist = view.findViewById(R.id.wifilist);
        rvwifilist.setHasFixedSize(true);
        rvwifilist.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvwifilist.scrollToPosition(0);


        rvwifilist.setAdapter(adapter);
        //rvwifilist.setItemAnimator(new DefaultItemAnimator());

        // pull to Refresh
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(this);

        // 왜 여기서 멈추지???
        // wifiScan();

        // Touch 이벤트
        // getContext()와 getActivity()의 차이
        final GestureDetector gestureDetector = new GestureDetector(this.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        rvwifilist.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                //손으로 터치한 곳의 좌표를 토대로 해당 Item의 View를 가져옴
                View childView = rv.findChildViewUnder(e.getX(), e.getY());

                //터치한 곳의 View가 RecyclerView 안의 아이템이고 그 아이템의 View가 null이 아니라
                //정확한 Item의 View를 가져왔고, gestureDetector에서 한번만 누르면 true를 넘기게 구현했으니
                //한번만 눌려서 그 값이 true가 넘어왔다면
                if (childView != null && gestureDetector.onTouchEvent(e)) {

                    //현재 터치된 곳의 position을 가져오고
                    int currentPosition = rv.getChildAdapterPosition(childView);

                    // itemlist의 item 변수 생성
                    WifiListModel item = adapter.getObject(currentPosition);

                    String ssid = item.getSsid();

                    String price = item.getPrice();
                    Drawable icon = item.getIcon();

                    WifiConfiguration wfc = new WifiConfiguration();
                    wfc.SSID = "\"".concat(ssid).concat("\"");
                    wfc.status = WifiConfiguration.Status.DISABLED;
                    wfc.priority = 40;


                    if (item.getSecurityMode().contains("[OPEN]")) {
                        wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                        wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                        wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                        wfc.allowedAuthAlgorithms.clear();
                        wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                        WifiManager wfMgr = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        int networkId = wfMgr.addNetwork(wfc);
                        if (networkId != -1) {
                            wfMgr.enableNetwork(networkId, true);
                            // success, can call wfMgr.enableNetwork(networkId, true) to connect
                        } else {
                            Toast.makeText(getContext(), ssid, Toast.LENGTH_SHORT).show();
                        }
                    } else if (item.getSecurityMode().contains("[WEP]")) {

                        wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                        wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                        wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                        wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                        wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                        wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

                        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                        wfc.wepKeys[0] = "seed0518";
                        wfc.wepTxKeyIndex = 0;
                        WifiManager wfMgr = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        int networkId = wfMgr.addNetwork(wfc);
                        if (networkId != -1) {
                            wfMgr.enableNetwork(networkId, true);
                            // success, can call wfMgr.enableNetwork(networkId, true) to connect
                            Toast.makeText(getContext(), ssid, Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getContext(), ssid, Toast.LENGTH_SHORT).show();
                        }
                    } else if (item.getSecurityMode().equals("[ESS]")) {
                        Toast.makeText(getContext(), item.getSecurityMode(), Toast.LENGTH_SHORT).show();

                        wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                        wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                        wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                        wfc.allowedAuthAlgorithms.clear();
                        wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                        WifiManager wfMgr = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        int networkId = wfMgr.addNetwork(wfc);
                        if (networkId != -1) {
                            wfMgr.enableNetwork(networkId, true);
                            // success, can call wfMgr.enableNetwork(networkId, true) to connect
                        } else {
                        }
                    } else {
                        AlertDialog.Builder ad = new AlertDialog.Builder(WifiListFragment.this.getActivity());

                        ad.setTitle("비밀번호");       // 제목 설정
                        ad.setMessage("비밀번호를 입력하시오");   // 내용 설정
                        // EditText 삽입하기
                        final EditText et = new EditText(WifiListFragment.this.getActivity());
                        ad.setView(et);

                        // 확인 버튼 설정
                        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Text 값 받아서 로그 남기기
                                String value = et.getText().toString();
                                Toast.makeText(getContext(), item.getSecurityMode(), Toast.LENGTH_SHORT).show();

                                wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                                wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                                wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                                wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                                wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                                wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                                wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

                                wfc.preSharedKey = "\"".concat(value).concat("\"");
                                WifiManager wfMgr = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                                int networkId = wfMgr.addNetwork(wfc);
                                if (networkId != -1) {
                                    wfMgr.enableNetwork(networkId, true);
                                    // success, can call wfMgr.enableNetwork(networkId, true) to connect
                                } else {
                                    Toast.makeText(getContext(), ssid, Toast.LENGTH_SHORT).show();
                                }

                                dialog.dismiss();     //닫기
                                // Event
                            }
                        });
                        // 취소 버튼 설정
                        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                                // Event
                            }
                        });
                        // 창 띄우기
                        ad.show();


                    }


                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        return view;
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                scanDatas = wifiManager.getScanResults();

                HashMap<String, Integer> wifimap = new HashMap<>();

                //scanDatas는 어떤형식이지? 배열?
                for (ScanResult select : scanDatas) {
                    String SSID = select.SSID;
                    String BSSID = select.BSSID;
                    int RSSI = select.level;
                    String SecurtyMode = select.capabilities;
                    // select.level로 신호세기(rssi) 측정가능

                    if (SSID.isEmpty()) continue;

                    // HashMap에서 같은 Value값 or null값의 ssid를 걸러냄
                    // HashMap은 Refresh할 때 마다 거르기 위함
                    if (!wifimap.containsKey(SSID)) {
                        wifimap.put(SSID, RSSI);
                        addItem(SSID, BSSID, RSSI, SecurtyMode);
                    }
                }

            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                getActivity().sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
            }
        }
    };

    public void addItem(String SSID, String BSSID, int RSSI, String SecurtyMode) {
        boolean available = true;
        boolean aval = false;

        for (int i = 0; i < mItems.size(); i++) {// mItems에서 중복되는것을 거르기 위함
            WifiListModel item = mItems.get(i);
            if (item.getSsid().equals(SSID)) {
                available = false;
                // ssid는 같지만 rssi가 다를 때
                if (item.getSsid().equals(SSID) && item.getRssi() != RSSI) {
                    available = true;
                    mItems.remove(i);
                }
                break;
            }
        }
        if(available) {
            Drawable ICON = null;
            if (RSSI >= -60) {
                ICON = ContextCompat.getDrawable(getActivity(), R.drawable.wifi_strength_3);
                aval= true;
            } else if (-75 <= RSSI && RSSI < -60) {
                ICON = ContextCompat.getDrawable(getActivity(), R.drawable.wifi_strength_2);
            } else if (-85 <= RSSI && RSSI < -75) {
                ICON = ContextCompat.getDrawable(getActivity(), R.drawable.wifi_strength_1);
            }
            if(ICON!=null) {
                mItems.add(new WifiListModel(SSID, BSSID, RSSI, ICON, aval, SecurtyMode));
                itemSort();
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void itemSort() {
        Collections.sort(mItems, new Comparator<WifiListModel>() {
            @Override
            public int compare(WifiListModel o1, WifiListModel o2) {
                if (o1.getRssi() < o2.getRssi()) {
                    return 1;
                } else if (o1.getRssi() > o2.getRssi()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    public void wifiScan() {
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(wifiManager.NETWORK_STATE_CHANGED_ACTION);
        getContext().registerReceiver(receiver, intentFilter);
        wifiManager.startScan(); //여기서 살짝 멈춤
    }


    @Override
    public void onResume() {
        super.onResume();
        wifiScan();
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(receiver);
    }


    @Override
    public void onRefresh() {
        pullToRefresh.setRefreshing(true);
        wifiScan();
        pullToRefresh.setRefreshing(false);
    }


    public void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 4000);
    }


}