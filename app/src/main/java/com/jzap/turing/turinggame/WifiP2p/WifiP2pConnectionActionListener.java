package com.jzap.turing.turinggame.WifiP2p;

import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class WifiP2pConnectionActionListener implements WifiP2pManager.ActionListener {

    private static String mTag = "WifiP2pCnxActionListener";

    WifiP2pBroadcastReceiver mReceiver;

    public WifiP2pConnectionActionListener(WifiP2pBroadcastReceiver receiver) {
        super();
        mReceiver = receiver;
    }

    @Override
    public void onSuccess() {
        Log.i(mTag, "Connection initiation succeeded");
    }

    @Override
    public void onFailure(int reason) {
        Log.i(mTag, "Connection initiation failed because " + reason);
    }
}
