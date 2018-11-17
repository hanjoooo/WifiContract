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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.khanj.wificontract.R;
import com.example.khanj.wificontract.adapter.WifiListAdapter;
import com.example.khanj.wificontract.encryption.AESHelper;
import com.example.khanj.wificontract.loading.LoadingFragment;
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
import java.util.Timer;
import java.util.TimerTask;

import contract.EtherWifiToken;
import io.realm.Realm;
import io.realm.RealmResults;


public class WiFiListFragment extends LoadingFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final String TAG = WiFiListFragment.class.getName();
    private WifiListAdapter adapter;
    private List<ScanResult> scanResults;
    private WifiManager wifiManager;
    private SwipeRefreshLayout pullToRefresh;

    // RecyclerView를 Fragment에 추가하기 위한 코드
    private RecyclerView rvwifilist;
    private ArrayList<WifiListModel> mItems = new ArrayList<>();

    private EtherWifiToken contract;
    private WalletModel walletModel = new WalletModel();
    private String contractAddress = "0x31D05C8b7D054182f1Eb2922e8627d8511a663E1";
    private Web3j web3j;
    private Credentials credential;
    private Realm mRealm;
    private String walletBalance;

    private Boolean cancels = false;
    private Boolean refreshFlag = true;

    private WifiListModel item;
    private AlertDialog ad = null;
    private int num;
    private Button adButton;
    private Timer timer;
    Runnable runnable;
    Boolean isStopped = false;
    private final android.os.Handler handler = new android.os.Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment1_wifi_list, container, false);

        web3j = Web3jFactory.build(new HttpService("https://kovan.infura.io/v3/cab60b4fc0594563881813d8f5f5349b"));
        mRealm = Realm.getDefaultInstance();
        adapter = new WifiListAdapter(mItems);
        getWallet();

        rvwifilist = view.findViewById(R.id.wifilist);
        rvwifilist.setHasFixedSize(true);
        rvwifilist.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvwifilist.scrollToPosition(0);
        rvwifilist.setAdapter(adapter);

        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(this);
        refreshFlag = true;

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
                    int currentPosition = rv.getChildAdapterPosition(childView);
                    item = adapter.getObject(currentPosition);

                    String ssid = item.getSsid();
                    String password = item.getPassword();

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
                        wfc.wepKeys[0] = password;
                        wfc.wepTxKeyIndex = 0;
                        WifiManager wfMgr = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        int networkId = wfMgr.addNetwork(wfc);
                        if (networkId != -1) {
                            wfMgr.enableNetwork(networkId, true);
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
                        }
                    } else {
                        if (item.getAvai()) {
                            rentWiFi();
                        } else {
                            AlertDialog.Builder ad = new AlertDialog.Builder(WiFiListFragment.this.getActivity());
                            ad.setTitle("비밀번호");
                            ad.setMessage("비밀번호를 입력하시오");
                            final EditText et = new EditText(WiFiListFragment.this.getActivity());
                            ad.setView(et);
                            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Text 값 받아서 로그 남기기
                                    wfc.preSharedKey = "\"".concat(et.getText().toString()).concat("\"");
                                    WifiManager wfMgr = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                                    int networkId = wfMgr.addNetwork(wfc);
                                    wfMgr.disconnect();
                                    wfMgr.enableNetwork(networkId, true);
                                    Boolean isConnected = wfMgr.reconnect();
                                    dialog.dismiss();
                                }
                            });
                            ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            ad.show();
                        }
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

    private void getWallet() {
        mRealm.beginTransaction();
        RealmResults<WalletModel> walletModels = mRealm.where(WalletModel.class).findAll();
        mRealm.commitTransaction();
        if (walletModels.size() > 0) {
            walletModel = walletModels.get(0);
            readyForRequest(walletModel.getPassword(), walletModel.getDetailPath());
            getWalletBallance(walletModel.getWalletAddress());
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void readyForRequest(String pwd, String detailpath) {
        //start sending request
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    credential = WalletUtils.loadCredentials(pwd, path + "/" + detailpath);
                    contract = EtherWifiToken.load(contractAddress, web3j, credential, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                    Log.d("TAG", "done credential");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG", "failed !!!");
                }
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void getWalletBallance(String walletAddress) {
        //GetMyBalance
        new AsyncTask() {
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
                    Log.d("TAG", "failed !!! generate Wallet");
                }
                return null;
            }
        }.execute();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (refreshFlag) {
                mItems.clear();
                startProgresss();
                if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    scanResults = wifiManager.getScanResults();
                    HashMap<String, Integer> wifiMap = new HashMap<>();
                    Log.d("WIFI SCAN", "START");
                    for (ScanResult scanResult : scanResults) {
                        String SSID = scanResult.SSID;
                        String BSSID = scanResult.BSSID;
                        int RSSI = wifiManager.calculateSignalLevel(scanResult.level, 3);
                        String securityMode = scanResult.capabilities;

                        Log.d("scanResult: ", BSSID + "\t" + RSSI + "\t" + SSID);
                        if (SSID.isEmpty()) continue;
                        if (!wifiMap.containsKey(SSID)) {
                            wifiMap.put(SSID, mItems.size());
                            addItem(scanResult, mItems.size());
                        }
                    }
                    adapter.notifyDataSetChanged();
                    for (int i = 0; i < mItems.size(); ++i) {
                        getAvailabilityFromContract(mItems.get(i).getBssid(), i);
                    }
                    refreshFlag = false;
                }
            }
            /* @TODO 연결되었을 때 처리하기 (NetworkInfo를 얻어와서 item이랑 비교한 후 처리하기)
            else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                if () {
                    Toast.makeText(getContext(), "Connect Success", Toast.LENGTH_SHORT).show();
                }
            }
            */
        }
    };

    public void addItem(ScanResult scanResult, int position) {
        String SSID = scanResult.SSID;
        String BSSID = scanResult.BSSID;
        int RSSI = wifiManager.calculateSignalLevel(scanResult.level, 3);
        String securityMode = scanResult.capabilities;
        Drawable ICON = null;

        if (RSSI == 2) {
            ICON = ContextCompat.getDrawable(getActivity(), R.drawable.wifi_strength_3);
        } else if (RSSI == 1) {
            ICON = ContextCompat.getDrawable(getActivity(), R.drawable.wifi_strength_2);
        } else if (RSSI == 0) {
            ICON = ContextCompat.getDrawable(getActivity(), R.drawable.wifi_strength_1);
        }
        if (ICON != null) {
            mItems.add(new WifiListModel(SSID, BSSID, RSSI, ICON, false, securityMode));
            // getAvailabilityFromContract(mItems.get(position).getBssid(), position);
        }
    }

    public void getAvailabilityFromContract(String macAddress, int position) {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    if (cancels)
                        cancel(true);
                    // @notice contractWifiInfo : <1: 비밀번호, 2: 공유자 지갑 주소, 3: 사용 시간, 4: 활성여부>
                    contract = EtherWifiToken.load(contractAddress, web3j, credential, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                    Tuple4<String, String, BigInteger, Boolean> contractWifiInfo = contract.getAccessPoint(macAddress).send();
                    Log.d("TAG", macAddress + "\t" + contractWifiInfo.getValue4().toString() + "\t" + position + "\t" + mItems.get(position).getSsid());
                    if (contractWifiInfo.getValue4()) {
                        String password = AESHelper.decrypt(contractWifiInfo.getValue1());
                        Log.d("COLOR UPDATED", mItems.get(position).getSsid());
                        mItems.get(position).setPassword(password);
                        mItems.get(position).setAvai(true);
                        adapter.notifyItemChanged(position);
                    }
                    progressOFF();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                // if (index == mItems.size() - 1) adapter.notifyDataSetChanged();
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                return;
            }
        }.execute();
    }

    public void wifiScan() {
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(wifiManager.NETWORK_STATE_CHANGED_ACTION);
        getContext().registerReceiver(receiver, intentFilter);
        wifiManager.startScan();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFlag = true;
        wifiScan();
        cancels = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(receiver);
        if (ad != null) {
            ad.dismiss();
            isStopped = true;
        }
        cancels = true;
        refreshFlag = true;
    }

    @Override
    public void onRefresh() {
        pullToRefresh.setRefreshing(true);
        refreshFlag = true;
        wifiScan();
        pullToRefresh.setRefreshing(false);
    }

    public void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                progressOFF();
            }
        }, 2000);
    }

    public void rentWiFi() {
        final LinearLayout linear = (LinearLayout) View.inflate(getActivity(), R.layout.custom_dialog_advertisement, null);
        ImageView adImage = (ImageView) linear.findViewById(R.id.iv_advertisement);
        adButton = (Button) linear.findViewById(R.id.btn_connect_wifi);
        adButton.setEnabled(false);
        adButton.setOnClickListener(this);
        Drawable drawable = getResources().getDrawable(R.drawable.wifi_splash);
        adImage.setImageDrawable(drawable);

        num = 15;
        isStopped = false;
        ad = new AlertDialog.Builder(this.getActivity())
                .setTitle("광고 시청")
                .setView(linear)
                .setCancelable(false)
                .show();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                updateButton();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }

    private void updateButton() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (num > 0 && !isStopped) {
                    adButton.setText(num + "초 남음");
                    num--;
                } else {
                    adButton.setText("연결하기");
                    adButton.setEnabled(true);
                    num = 15;
                    timer.cancel();
                }
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_connect_wifi) {
            String ssid = item.getSsid();
            String password = item.getPassword();
            WifiConfiguration wfc = new WifiConfiguration();
            wfc.SSID = "\"".concat(ssid).concat("\"");
            wfc.status = WifiConfiguration.Status.DISABLED;
            wfc.priority = 40;
            wfc.preSharedKey = "\"".concat(password).concat("\"");
            WifiManager wfMgr = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int networkId = wfMgr.addNetwork(wfc);
            wfMgr.disconnect();
            wfMgr.enableNetwork(networkId, true);
            Boolean isConnected = wfMgr.reconnect();
            ad.dismiss();
        }
    }

    public void startProgresss() {
        progressON(this.getActivity(), "와이파이 찾는중...");
    }

}