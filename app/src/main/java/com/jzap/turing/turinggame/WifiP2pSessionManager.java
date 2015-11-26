package com.jzap.turing.turinggame;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class WifiP2pSessionManager implements WifiP2pManager.ConnectionInfoListener {

    private static final String mTag = "WConnectionInfoListener";

    private Session mSession;
    private String mGroupOwnerAddress;
    private SessionMessageHandler mHandler;

    public WifiP2pSessionManager(SessionMessageHandler handler) {
        super();
        mHandler = handler;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

        Log.i(mTag, "Connection Info Available...");

        mGroupOwnerAddress = info.groupOwnerAddress.getHostAddress();

        // After the group negotiation, we can determine the group owner.
        if (info.groupFormed && info.isGroupOwner) {
            Log.i(mTag, "I'm the group owner");
            mSession = new GroupOwnerSession(mHandler);
        } else if (info.groupFormed) {
            // The other device acts as the client.
            Log.i(mTag, "I'm not the group owner");
            mSession = new ClientSession(mGroupOwnerAddress, mHandler);
        }

        if(mSession != null) {
            new Thread(mSession).start();
        }

    }

    public void terminate() {
        mSession.setState(Session.STATE.TERMINATE);
    }

}