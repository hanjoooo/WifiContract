package com.example.khanj.wificontract.service;

import android.os.Handler;
import android.util.Log;

public class ServiceThread extends Thread {

    private final String TAG = this.getName();
    Handler handler;
    boolean isRun = true;

    public ServiceThread(Handler handler) {
        this.handler = handler;
    }

    public void stopForever() {
        synchronized (this) {
            this.isRun = false;
        }
    }

    public void run() {
        while (isRun) {
            handler.sendEmptyMessage(0);
            try {
                Thread.sleep(1000); // 1초 간격
            } catch (Exception e) {
                Log.d(TAG, "Thread Sleep Error Occured");
            }
        }
    }
}
