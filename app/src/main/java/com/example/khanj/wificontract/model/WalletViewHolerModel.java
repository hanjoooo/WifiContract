package com.example.khanj.wificontract.model;

/**
 * Created by minseop on 2018-06-07.
 */

public class WalletViewHolerModel {
    private String img;
    private String walletName;
    private String walletAddress;
    private String walletBalance;

    public WalletViewHolerModel(String img, String walletName, String walletAddress, String walletBalance){
        this.img = img;
        this.walletName = walletName;
        this.walletAddress = walletAddress;
        this.walletBalance = walletBalance;
    }

    public void setImg(String img){this.img = img;}
    public String getImg(){return img;}

    public void setWalletName(String walletName){this.walletName = walletName;}
    public String getWalletName(){return walletName;}

    public void setWalletAddress(String walletAddress){this.walletAddress = walletAddress;}
    public String getWalletAddress(){return walletAddress;}

    public void setWalletBalance(String walletBalance){this.walletBalance = walletBalance;}
    public String getWalletBalance(){return walletBalance;}
}
