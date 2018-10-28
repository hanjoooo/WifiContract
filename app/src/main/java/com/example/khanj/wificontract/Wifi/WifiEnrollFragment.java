package com.example.khanj.wificontract.Wifi;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khanj.wificontract.R;
import com.example.khanj.wificontract.adapter.WifiEnrollAdapter;
import com.example.khanj.wificontract.loading.LoadingFragment;
import com.example.khanj.wificontract.model.WalletModel;
import com.example.khanj.wificontract.model.WifiAssetModel;
import com.example.khanj.wificontract.model.WifiEnrollModel;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import contract.EtherWifiToken;
import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 */
public class WifiEnrollFragment extends LoadingFragment {
    private LinearLayout noListData;
    private SwipeRefreshLayout pullToRefresh;
    private WifiEnrollAdapter adapter;

    // RecyclerView를 Fragment에 추가하기 위한 코드
    private RecyclerView rv_rollWifiList;

    // wifi 등록 수,이름, 맥주소, 시간, 비밀번호
    private TextView tv_wifienrollnum;
    private ArrayList<WifiEnrollModel> mItems = new ArrayList<>();
    private TextView txWifinum;
    //블록체인에서 가져올 값들

    private Realm mRealm;

    private Web3j web3j;
    private Credentials credential;
    private String contractAddress = "0x49d4dd5d50b0f6bfd5f08fbc4734023d02feda44";
    private final String KEY = "201110911220131220652012122335";
    private EtherWifiToken contract;
    private WalletModel walletModel = new WalletModel();
    private String walletBalance;

    WifiEnrollModel wifiEnrollModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment2_enroll, container, false);
        //web3j = Web3jFactory.build(new HttpService("https://ropsten.infura.io/wd7279F18YpzuVLkfZTk"));
        web3j = Web3jFactory.build(new HttpService("https://kovan.infura.io/v3/cab60b4fc0594563881813d8f5f5349b"));

        mRealm = Realm.getDefaultInstance();
        getWallet();
        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(clickFab);

        noListData = v.findViewById(R.id.no_listdata);
        txWifinum = (TextView) v.findViewById(R.id.txwifinum);
        //        pullToRefresh = v.findViewById(R.id.pullToRefresh);
        //        pullToRefresh.setOnRefreshListener(this);
        adapter = new WifiEnrollAdapter(mItems);
        rv_rollWifiList = (RecyclerView) v.findViewById(R.id.rv_enroll_list);
        rv_rollWifiList.setAdapter(adapter);
        rv_rollWifiList.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        mItems.clear();
        getWifiAssetObject();
        getWifiInfoFromContract();
        noListData.setVisibility(View.GONE);
        txWifinum.setText("" + mItems.size());
        return v;
    }

    private void getWifiAssetObject() {
        mRealm.beginTransaction();
        RealmResults<WifiAssetModel> wifiAssetModel = mRealm.where(WifiAssetModel.class).findAll();
        mRealm.commitTransaction();
        Log.d("getWifi", Integer.toString(wifiAssetModel.size()));
        for (int i = 0; i < wifiAssetModel.size(); i++) {
            Log.d("getWifi",wifiAssetModel.get(i).getMacAddress() + wifiAssetModel.get(i).getSsid());
            mItems.add(new WifiEnrollModel(wifiAssetModel.get(i).getMacAddress(), wifiAssetModel.get(i).getSsid(), "1", 0));
            Log.d("TAG", String.valueOf(mItems.get(i)));
        }
        if (wifiAssetModel.size() > 0) {
            adapter.notifyDataSetChanged();
        }
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

    private static String encrypt(String text, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8");
        int len = b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
        return Base64.encodeToString(results, 0);
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

    private void tempSetting() {
    }

    private void setAddItem(View v) {
    }

    //플로팅버튼 클릭 함수
    private View.OnClickListener clickFab = new View.OnClickListener() {
        public void onClick(View v) {
            final LinearLayout linear = (LinearLayout) View.inflate(getActivity(), R.layout.custom_dialog, null);
            TextView etwifiname = (TextView) linear.findViewById(R.id.wifi_name);
            TextView etmac = (TextView) linear.findViewById(R.id.mac_address);
            EditText etpassword = (EditText) linear.findViewById(R.id.wifi_password);

            setWiFiStatus(getActivity(), etmac, etwifiname);
            new AlertDialog.Builder(getActivity())
                    .setTitle("와이파이등록")
                    .setIcon(R.drawable.wifi)
                    .setView(linear)
                    .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startProgresss();
                            try {
                                String password = encrypt(etpassword.getText().toString(), KEY);
                                wifiEnrollModel.setWifiPassword(password);
                                registWifi(wifiEnrollModel);
                                createWifiAssetObject(wifiEnrollModel.getMac(), wifiEnrollModel.getWifiName(), credential.getAddress());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getContext(), "와이파이 등록을 취소하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
        }
    };

    private void createWifiAssetObject(String macAddress, String ssid, String owner) {
        mRealm.beginTransaction();
        // RealmResults<WifiAssetModel> wifiAssetModels = mRealm.where(WifiAssetModel.class).findAll();
        WifiAssetModel wifiAssetModel;
        try {
            wifiAssetModel = mRealm.createObject(WifiAssetModel.class, macAddress);
            wifiAssetModel.setSsid(ssid.substring(1, ssid.length() - 1));
            wifiAssetModel.setOwner(owner);
            Log.d("Wifi Asset Registration", wifiAssetModel.toString());
        } catch (Exception e) {
            Toast.makeText(getContext(), "the Name already exist", Toast.LENGTH_SHORT).show();
        }
        mRealm.commitTransaction();
    }

    public void setWiFiStatus(Context mContext, TextView etmac, TextView etwifiname) {
        WifiManager manager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = manager.getConnectionInfo();
        wifiEnrollModel = new WifiEnrollModel(wifiInfo.getBSSID(), wifiInfo.getSSID(), "", 0);
        etwifiname.setText(wifiInfo.getSSID().replace('\"', '\0'));
        etmac.setText(wifiInfo.getBSSID());
    }

    private void registWifi(WifiEnrollModel wifiEnrollModel) {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    contract = EtherWifiToken.load(contractAddress, web3j, credential, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                    Log.d("TAG", wifiEnrollModel.getMac());
                    TransactionReceipt tr = contract.addAccessPoint(wifiEnrollModel.getMac(), wifiEnrollModel.getWifiName(), wifiEnrollModel.getWifiPassword()).send();
                    progressOFF();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public void getWifiInfoFromContract() {
        for (int i = 0; i < mItems.size(); ++i) {
            getAvailabilityFromContract(mItems.get(i).getMac(), i);
        }
    }

    public void getAvailabilityFromContract(String macAddress, int position) {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    // @notice contractWifiInfo : <1: 비밀번호, 2: 공유자 지갑 주소, 3: 사용 시간, 4: 활성여부>
                    contract = EtherWifiToken.load(contractAddress, web3j, credential, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                    Tuple4<String, String, BigInteger, Boolean> contractWifiInfo = contract.getAccessPoint(macAddress).send();
                    Log.d("TAG", macAddress + "\t" + contractWifiInfo.getValue4().toString() + "\t" + position + "\t" + mItems.get(position).getWifiName());
                    Boolean isEnable = contractWifiInfo.getValue4();
                    if (isEnable) {
                        String password = decrypt(contractWifiInfo.getValue1(), KEY);
                        mItems.get(position).setWifiPassword(password);
                        mItems.get(position).setEnable(isEnable);
                        adapter.notifyItemChanged(position);
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
        progressON(this.getActivity(), "올리는중...");
    }
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
