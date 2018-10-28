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
import android.util.Base64;
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

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import contract.EtherWifiToken;
import io.realm.Realm;
import io.realm.RealmResults;


public class WiFiListFragment extends LoadingFragment implements SwipeRefreshLayout.OnRefreshListener {

    private WifiListAdapter adapter;
    private List<ScanResult> scanResults;
    private WifiManager wifiManager;
    private SwipeRefreshLayout pullToRefresh;

    // RecyclerView를 Fragment에 추가하기 위한 코드
    private RecyclerView rvwifilist;
    private ArrayList<WifiListModel> mItems = new ArrayList<>();

    private EtherWifiToken contract;
    private WalletModel walletModel = new WalletModel();
    private String contractAddress = "0x49d4dd5d50b0f6bfd5f08fbc4734023d02feda44";
    private final String KEY = "201110911220131220652012122335";
    private Web3j web3j;
    private Credentials credential;
    private Realm mRealm;
    private String walletBalance;

    private Boolean cancels = false;

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

                    String price = item.getBssid();
                    Drawable icon = item.getIcon();

                    WifiConfiguration wfc = new WifiConfiguration();
                    wfc.SSID = "\"".concat(ssid).concat("\"");
                    wfc.status = WifiConfiguration.Status.DISABLED;
                    wfc.priority = 40;

                    if (item.getAvai()) {
                        Toast.makeText(getContext(), item.getPassword(), Toast.LENGTH_SHORT).show();
                    } else if (item.getSecurityMode().contains("[OPEN]")) {
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
                        wfc.wepKeys[0] = "seed0518";
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
                        } else {
                        }
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
                                } else {
                                    Toast.makeText(getContext(), ssid, Toast.LENGTH_SHORT).show();
                                }
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
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                getActivity().sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
            }
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
                        String password = decrypt(contractWifiInfo.getValue1(), KEY);
                        Log.d("COLOR UPDATED", mItems.get(position).getSsid());
                        mItems.get(position).setPassword(password);
                        mItems.get(position).setAvai(true);
                        adapter.notifyItemChanged(position);
                        progressOFF();
                    }
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
        wifiScan();
        cancels = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(receiver);
        cancels = true;

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
                adapter.notifyDataSetChanged();
                progressOFF();
            }
        }, 2000);
    }

    private static String decrypt(String text, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8");
        int len = b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] results = cipher.doFinal(Base64.decode(text, 0));
        return new String(results, "UTF-8");
    }

    public void startProgresss() {
        progressON(this.getActivity(), "와이파이 찾는중...");
    }

}