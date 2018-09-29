package com.example.khanj.wificontract.wallet;

import com.squareup.otto.Bus;

/**
 * Created by minseop on 2018-06-08.
 */

public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}

