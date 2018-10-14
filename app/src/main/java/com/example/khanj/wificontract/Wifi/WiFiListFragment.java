package com.example.khanj.wificontract.Wifi;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.khanj.wificontract.R;
import com.example.khanj.wificontract.adapter.WifiListAdapter;
import com.example.khanj.wificontract.model.WalletModel;
import com.example.khanj.wificontract.model.WifiListModel;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import contract.EtherWifiToken;
import io.realm.Realm;
import io.realm.RealmResults;


public class WiFiListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private WifiListAdapter adapter;
    private List<ScanResult> scanDatas;
    private WifiManager wifiManager;
    private SwipeRefreshLayout pullToRefresh;

    // RecyclerView를 Fragment에 추가하기 위한 코드
    private RecyclerView rvwifilist;
    private RecyclerView.LayoutManager mLayoutManager;

    private Context main_context = getActivity();
    private EtherWifiToken contract;
    private WalletModel walletModel = new WalletModel();
    private String contractAddress = "0x2466f0f59aa8ffb83a7425ad9d7ad02f5e27ba06";
    private Web3j web3j;
    private Credentials credential;
    private Realm mRealm;
    private String walletBalance;

    private ArrayList<Tuple4> contractWifiList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 최초에 Fragment가 호출되었을 때 와이파이가 꺼져있으면 강제로 켜도록 함
        // 우리는 wifi를 굳이 킬 필요가 있을까?

        View view = inflater.inflate(R.layout.fragment1_wifi_list, container, false);
        //web3j = Web3jFactory.build(new HttpService("https://ropsten.infura.io/wd7279F18YpzuVLkfZTk"));
        web3j = Web3jFactory.build(new HttpService("https://kovan.infura.io/v3/cab60b4fc0594563881813d8f5f5349b"));

        mRealm = Realm.getDefaultInstance();
        getWallet();
        // RecyclerView를 Fragment에 추가하기 위한 코드
        rvwifilist = view.findViewById(R.id.wifilist);
        rvwifilist.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvwifilist.setLayoutManager(mLayoutManager);
        rvwifilist.scrollToPosition(0);
        adapter = new WifiListAdapter(main_context);
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


                    if(item.getSecurityMode().contains("[OPEN]")){
                        wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                        wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                        wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                        wfc.allowedAuthAlgorithms.clear();
                        wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                        WifiManager wfMgr = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        int networkId = wfMgr.addNetwork(wfc);
                        if (networkId != -1) {
                            wfMgr.enableNetwork(networkId,true);
                            // success, can call wfMgr.enableNetwork(networkId, true) to connect
                        }else{
                            Toast.makeText(getContext(), ssid, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if(item.getSecurityMode().contains("[WEP]")){

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
                            wfMgr.enableNetwork(networkId,true);
                            // success, can call wfMgr.enableNetwork(networkId, true) to connect
                            Toast.makeText(getContext(), ssid, Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(getContext(), ssid, Toast.LENGTH_SHORT).show();
                        }
                    }else if(item.getSecurityMode().equals("[ESS]")){
                        Toast.makeText(getContext(), item.getSecurityMode(), Toast.LENGTH_SHORT).show();

                        wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                        wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                        wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                        wfc.allowedAuthAlgorithms.clear();
                        wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                        WifiManager wfMgr = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        int networkId = wfMgr.addNetwork(wfc);
                        if (networkId != -1) {
                            wfMgr.enableNetwork(networkId,true);
                            // success, can call wfMgr.enableNetwork(networkId, true) to connect
                        }else{
                        }
                    }
                    else{
                        AlertDialog.Builder ad = new AlertDialog.Builder(WiFiListFragment.this.getActivity());

                        ad.setTitle("비밀번호");       // 제목 설정
                        ad.setMessage("비밀번호를 입력하시오");   // 내용 설정
                        // EditText 삽입하기
                        final EditText et = new EditText(WiFiListFragment.this.getActivity());
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
                                    wfMgr.enableNetwork(networkId,true);
                                    // success, can call wfMgr.enableNetwork(networkId, true) to connect
                                }else{
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

    private void getWallet(){
        mRealm.beginTransaction();
        RealmResults<WalletModel> walletModels = mRealm.where(WalletModel.class).findAll();
        mRealm.commitTransaction();
        if (walletModels.size()>0){
            walletModel = walletModels.get(0);
            readyForRequest(walletModel.getPassword(), walletModel.getDetailPath());
            getWalletBallance(walletModel.getWalletAddress());
        }
    }

    public void getWifiContract(String macAddress){
        Boolean returns = false;
        new AsyncTask()
        {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    contract = EtherWifiToken.load(contractAddress,web3j,credential, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                    Tuple4<String,String,BigInteger,Boolean> contractWifiInfo = contract.getAccessPoint(macAddress).send();
                    if(contractWifiInfo.getValue4()){
                        return true;
                    }
                    Log.d("TAG","잘했어요");
                }catch (Exception e){
                    e.printStackTrace();
                }
                return false;
            }
        }
        .execute();
    }
    @SuppressLint("StaticFieldLeak")
    private void readyForRequest(String pwd, String detailpath){
        //start sending request
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    credential = WalletUtils.loadCredentials(pwd, path+"/"+detailpath);
                    contract = EtherWifiToken.load(contractAddress, web3j, credential, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                    Log.d("TAG","done credential");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG","failed !!!");
                }
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void getWalletBallance(String walletAddress) {
        //GetMyBalance
        new AsyncTask () {
            @Override
            protected Object doInBackground(Object[] objects) {
                BigInteger ethGetBalance = null;
                try {
                    ethGetBalance = web3j
                            .ethGetBalance(walletAddress, DefaultBlockParameterName.LATEST)
                            .send()
                            .getBalance();
                    BigInteger wei = ethGetBalance;
                    walletBalance = wei.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("TAG","failed !!! generate Wallet");
                }
                return null;
            }
        }.execute();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                scanDatas = wifiManager.getScanResults();


                HashMap<String, Integer> wifimap = new HashMap<>();
                int count = 0;
                //scanDatas는 어떤형식이지? 배열?
                for (ScanResult select : scanDatas) {

                    String SSID = select.SSID;
                    String BSSID = select.BSSID;
                    String SecurtyMode = select.capabilities;
                    int RSSI = select.level;
                    Integer integerRSSI = new Integer(RSSI);
                    // select.level로 신호세기(rssi) 측정가능


                    if (SSID.isEmpty()) continue;
                    ++count;
                    //수정개선사항: wifi list 찾을 때 중복되는 것 때문에 시간이 좀 걸린다
                    // 이것 때문에 하나씩 계속 delay되며 추가되었던 것
                    // 왜 receiver가 두번 작동하지?
                    // HashMap에서 같은 Value값 or null값의 ssid를 걸러냄
                    if (!wifimap.containsKey(SSID)) {
                        wifimap.put(SSID, RSSI);
                        if (RSSI >= -60) {
                            adapter.addItem(SSID, BSSID, RSSI, ContextCompat.getDrawable(getActivity(), R.drawable.wifi_strength_3),SecurtyMode,true);
                        } else if (-75 <= RSSI && RSSI < -60) {
                            adapter.addItem(SSID, BSSID, RSSI, ContextCompat.getDrawable(getActivity(), R.drawable.wifi_strength_2),SecurtyMode,false);
                        } else if (-85 <= RSSI && RSSI < -75) {
                            adapter.addItem(SSID, BSSID, RSSI, ContextCompat.getDrawable(getActivity(), R.drawable.wifi_strength_1),SecurtyMode,false);
                        }
                        // adapter.notifyDataSetChanged();를 add할 때 마다 하면 기존에 것에서 계속 덧붙여져서 문제가 된다.

                    } else {
                        --count;
                    }
                }
                //adapter.itemSort();
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                getActivity().sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
            }
        }
    };


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








