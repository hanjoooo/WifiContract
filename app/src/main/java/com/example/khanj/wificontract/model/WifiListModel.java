package com.example.khanj.wificontract.model;

import android.graphics.drawable.Drawable;

public class WifiListModel {
    private String ssid;
    private String bssid;
    private int rssi;
    private Drawable icon;
    private boolean avai=false;
    private String securityMode;

    public WifiListModel(){

    }
    public WifiListModel(String ssid, String bssid,int rssi, Drawable icon, boolean avai, String securityMode){
        this.ssid = ssid;
        this.bssid = bssid;
        this.rssi = rssi;
        this.icon = icon;
        this.avai = avai;
        this.securityMode = securityMode;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable iconSignal) {
        this.icon = iconSignal;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }


    public boolean getAvai() {
        return avai;
    }
    public void setAvai(boolean avai){this.avai = avai;}


    public void setSecurityMode(String securityMode) {
        this.securityMode = securityMode;
    }
    public String getSecurityMode() {
        return securityMode;
    }
}












