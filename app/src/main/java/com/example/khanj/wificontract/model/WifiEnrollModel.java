package com.example.khanj.wificontract.model;

/**
 * Created by jehug on 2018-10-07.
 */

public class WifiEnrollModel {

    private String mac="0";
    private String wifiname="0";
    private String endtime="0";
    private String wifiPassword="0";

    public WifiEnrollModel(){

    }
    public WifiEnrollModel(String mac, String wifiname, String endtime, String wifiPassword){
        this.mac = mac;
        this.wifiname = wifiname;
        this.endtime = endtime;
        this.wifiPassword = wifiPassword;
    }

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

    public void setWifiPassword(String wifiPassword) {
        this.wifiPassword = wifiPassword;
    }

    public String getWifiPassword() {
        return wifiPassword;
    }
}
