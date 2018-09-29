package com.example.khanj.wificontract.generation;


import com.example.khanj.wificontract.BasePresenter;
import com.example.khanj.wificontract.BaseView;

/**
 * Created by eirlis on 29.06.17.
 */

public interface GenerationContract {

    interface View extends BaseView<Presenter> {

        void showGeneratedWallet(String walletAddress, String detailPath);
    }

    interface Presenter extends BasePresenter {

        void generateWallet(String password);

        void start();
    }


}
