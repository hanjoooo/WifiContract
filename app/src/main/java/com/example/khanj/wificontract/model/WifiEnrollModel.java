package com.example.khanj.wificontract.model;

import android.graphics.drawable.Drawable;

/**
 * Created by jehug on 2018-10-07.
 */

public class WifiEnrollModel {

    private String mac;
    private String wifiname;
    private String endtime;
    private int enollnum;

    public String getMac(){
        return mac;
    }
    public void setMac(String mac){
        this.mac = mac;
    }

    public String getWifiName(){
        return wifiname;
    }
    public void setWifiName(String wifiname){
        this.wifiname = wifiname;
    }

    // 종료시간 -> 일단 String 형태로
    public String getEndTime(){
        return endtime;
    }
    public void setEndTime(String endtime){
        this.endtime = endtime;
    }

    public int getEnrollNum(){
        return enollnum;
    }
    public void setEnrollNum(int enollnum){
        this.enollnum = enollnum;
    }




}
