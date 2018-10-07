package com.example.khanj.wificontract.model;

import android.graphics.drawable.Drawable;

public class WifiListModel {
    private Drawable icon;
    private String ssid;
    private String price;
    private int rssi;
    private boolean avai;
    private String securityMode;

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


    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
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
