package com.example.khanj.wificontract.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by minseop on 2018-06-07.
 */

public class WalletModel extends RealmObject {
    @PrimaryKey
    public String walletName;

    private String walletAddress;
    private String password;
    private String detailPath;

    public void setWalletName(String walletName){this.walletName = walletName;}
    public String getWalletName(){return walletName;}

    public void setWalletAddress(String walletAddress){this.walletAddress = walletAddress;}
    public String getWalletAddress(){return walletAddress;}

    public void setPassword(String password){this.password = password;}
    public String getPassword(){return password;}

    public void setDetailPath(String detailPath){this.detailPath = detailPath;}
    public String getDetailPath(){return detailPath;}
}
