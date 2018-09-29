package com.example.khanj.wificontract.generation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.khanj.wificontract.R;
import com.example.khanj.wificontract.model.WalletModel;

import io.realm.Realm;
import io.realm.RealmResults;

public class GenerationActivity extends Activity implements GenerationContract.View, View.OnClickListener {

    public static final String TAG = GenerationActivity.class.getName();
    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 0;
    private GenerationContract.Presenter mWalletPresenter;
    private Button mGenerateWalletButton;
    private String mWalletAddress;
    private EditText mPassword, mWalletName;
    private String detailPath;
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allowExternalStorageAccess();

        setActivitySize();

        mGenerateWalletButton = (Button) findViewById(R.id.generate_wallet_button);
        mPassword = (EditText) findViewById(R.id.password);
        mWalletName = findViewById(R.id.edt_generate_wallet_name);

        mRealm = Realm.getDefaultInstance();
        mGenerateWalletButton.setOnClickListener(this);
    }

    private void allowExternalStorageAccess(){
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_WRITE_STORAGE);
        }
    }

    private void setActivitySize(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams  layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags  = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount  = 0.7f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.activity_generation);
        getWindow().getAttributes().width   = (int)( this.getResources().getDisplayMetrics().widthPixels * 0.9);
        getWindow().getAttributes().height  = (int)(this.getResources().getDisplayMetrics().heightPixels * 0.4);
    }

    @Override
    public void setPresenter(GenerationContract.Presenter presenter) {
        mWalletPresenter = presenter;
        Log.d("TAG","im in!");
    }

    @Override
    public void showGeneratedWallet(String address, String detailPath) {
        mWalletAddress = address;
        this.detailPath = detailPath;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.generate_wallet_button){
            Log.d("TAG",mWalletName.getText().toString());
            if (mWalletName.getText().toString().replaceAll(" ","").getBytes().length > 0 && mPassword.getText().toString().replaceAll(" ","").getBytes().length > 0) {
                mWalletPresenter = new GenerationPresenter(GenerationActivity.this, mPassword.getText().toString());
                mWalletPresenter.generateWallet(mPassword.getText().toString());
                createObject(mWalletAddress, mPassword.getText().toString(), detailPath);
            }
            else Toast.makeText(GenerationActivity.this, "양식을 모두 채워주십시오.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createObject(String mWalletAddress, String mPassword, String detailPath){
        mRealm.beginTransaction();
        Intent returnIntent = new Intent();
        RealmResults <WalletModel> walletModels = mRealm.where(WalletModel.class).findAll();
        WalletModel walletModel;
        try {
            walletModel = mRealm.createObject(WalletModel.class, mWalletName.getText().toString()); //primary key
            walletModel.setWalletAddress(mWalletAddress);
            walletModel.setPassword(mPassword);
            walletModel.setDetailPath(detailPath);
            Log.d("TAG","your account is set:  "+walletModel);
            setResult(Activity.RESULT_OK,returnIntent);
        }catch (Exception e){
            Toast.makeText(GenerationActivity.this, "the Name already exist", Toast.LENGTH_SHORT).show();
        }
        mRealm.commitTransaction();
        finish();
    }
}
