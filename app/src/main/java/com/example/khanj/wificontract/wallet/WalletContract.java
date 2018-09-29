package com.example.khanj.wificontract.wallet;


import com.example.khanj.wificontract.BasePresenter;
import com.example.khanj.wificontract.BaseView;

/**
 * Created by eirlis on 29.06.17.
 */

public class WalletContract {

    interface View extends BaseView<Presenter> {

        void showBalance();

        void showWalletAddress();
    }

    interface Presenter extends BasePresenter {

        void getWalletBalance();

        void getWalletAddress();
    }
}
