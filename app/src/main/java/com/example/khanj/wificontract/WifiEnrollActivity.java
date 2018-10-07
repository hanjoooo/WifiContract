package com.example.khanj.wificontract;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.khanj.wificontract.R;

public class WifiEnrollActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_enroll);
    }


//    public void onBackPressed() {
//        boolean acc_modify = getIntent().getBooleanExtra("acc_modify",false);
//        if(acc_modify){
//            Intent intent = new Intent(add_accident.this, detail_accident.class);
//            intent.putExtra("acc_position",getIntent().getIntExtra("acc_position", 1));
//            startActivity(intent);
//            finish();
//        }else{
//            Intent intent = new Intent(add_accident.this, MainActivity.class);
//            intent.putExtra("add_finish", true);
//            startActivity(intent);
//            finish();
//        }
//    }


}
