package com.example.khanj.wificontract.model;

/**
 * Created by jehug on 2018-10-07.
 */

public class WifiEnrollModel {

    private String mac="0";
    private String wifiname="0";
    private String wifiPassword="0";
    private int count=0;

    public WifiEnrollModel(){

    }
    public WifiEnrollModel(String mac, String wifiname,String wifiPassword,int count){
        this.mac = mac;
        this.wifiname = wifiname;
        this.wifiPassword = wifiPassword;
        this.count = count;
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

    public void setWifiPassword(String wifiPassword) {
        this.wifiPassword = wifiPassword;
    }
    public String getWifiPassword() {
        return wifiPassword;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public int getCount() {
        return count;
    }
}
