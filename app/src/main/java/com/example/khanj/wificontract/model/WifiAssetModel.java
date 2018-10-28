package com.example.khanj.wificontract.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class WifiAssetModel extends RealmObject {
    @PrimaryKey
    private String macAddress;

    private String ssid;
    private String owner;

    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }

    public String getSsid() { return ssid; }
    public void setSsid(String ssid) { this.ssid = ssid; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

}
