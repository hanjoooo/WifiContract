package com.example.khanj.wificontract.wallet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.khanj.wificontract.R;
import com.example.khanj.wificontract.model.WalletModel;
import com.example.khanj.wificontract.model.WalletViewHolerModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by minseop on 2018-06-11.
 */

public class SendEthToWalletActivity extends AppCompatActivity implements View.OnClickListener{

    private Spinner spn_walletAddress;
    private EditText edt_price;
    private Button btn_send;
    private Realm mRealm;
    private final List<WalletViewHolerModel> modelList = new ArrayList<>();
    private String[] list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        buildView();
        mRealm = Realm.getDefaultInstance();


    }

    private void getWalletInfo(){
        mRealm.beginTransaction();
        RealmResults<WalletModel> walletModels = mRealm.where(WalletModel.class).findAll();
        mRealm.commitTransaction();
        for (int i =0; i<walletModels.size(); i++){
            modelList.add(new WalletViewHolerModel("",walletModels.get(i).getWalletName(), walletModels.get(i).getWalletAddress(), ""));
            list[i] = modelList.get(i).getWalletName();
        }

    }

    private void buildView(){
        setContentView(R.layout.activity_sendethtowallet);
        spn_walletAddress = findViewById(R.id.spinner_wallet_address);
        edt_price = findViewById(R.id.edt_eth_price);
        btn_send = findViewById(R.id.btn_send_ethwallet);
    }


    private void buildSpinner(){

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.spinner_wallet_address){

        }else if (v.getId() == R.id.edt_eth_price){

        }else if (v.getId() == R.id.btn_send_ethwallet){

        }
    }
}
