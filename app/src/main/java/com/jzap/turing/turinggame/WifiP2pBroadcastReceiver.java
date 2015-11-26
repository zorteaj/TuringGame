package com.jzap.turing.turinggame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JZ_W541 on 11/25/2015.
 */

public class WifiP2pBroadcastReceiver extends BroadcastReceiver {

    private String mTag = "WifiP2pBroadcastReceiver";

    private PeerDisplayActivity mActivity;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    private boolean mConnected = false;
    private List mPeers = new ArrayList();


    public WifiP2pBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, PeerDisplayActivity activity) {

        super();

        Log.i(mTag, "WifiP2pBroadcastReceiver constructed");

        mActivity = activity;
        mManager = manager;
        mChannel = channel;
    }

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {

        private String mTag = "WifiP2pMgr.PeerListListener";

        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            // Out with the old, in with the new.
            mPeers.clear();
            mPeers.addAll(peerList.getDeviceList());

            Log.i(mTag, mPeers.size() + " Peers Available");

            mActivity.setPeers(mPeers);
        }
    };

    public void disconnect() {
        if(mConnected) {
            mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.i(mTag, "Successfully removed group");
                }

                @Override
                public void onFailure(int reason) {
                    Log.i(mTag, "Failed to removed group");
                }
            });
        }
    }

    // @Override
    public void connect() {

        Log.i(mTag, "Checking to see if I'm already connected..."); // TODO : Get rid of all these logs if no longer useful

        if (!mConnected) {

            Log.i(mTag, "I'm NOT already connected...");
            Log.i(mTag, "Do I have peers?..");

            if (mPeers.size() > 0) {
                Log.i(mTag, "Yes, I have peers - connecting to peer 0");

                WifiP2pDevice device = (WifiP2pDevice) mPeers.get(0);

                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;

                mManager.connect(mChannel, config, new WifiP2pConnectionActionListener(this));
            } else {
                Log.i(mTag, "No, I don't have any peers");
            }
            // config.groupOwnerIntent = 15; // TODO : This may be key to multiple connections
        } else {
            Log.i(mTag, "I'm already connected...");
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            Log.i(mTag, "WIFI_P2P_STATE_CHANGED_ACTION");
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // It may be desirable to record this for debugging
            } else {
                // It may be desirable to record this for debugging
            }
        } else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.i(mTag, "WIFI_P2P_PEERS_CHANGED_ACTION");

            // The peer list has changed!  We should probably do something about
            // that.

            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                Log.i(mTag, "Requesting peers");
                mManager.requestPeers(mChannel, peerListListener);
            }
        } else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed!  We should probably do something about
            // that.
            Log.i(mTag, "WIFI_P2P_CONNECTION_CHANGED_ACTION");

            if(mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if(networkInfo.isConnected()) {
                Log.i(mTag, "Connected!");
                mConnected = true;

                // We are connected with the other device, request connection
                // info to find group owner IP
                WifiP2pSessionManager sessionManager = new WifiP2pSessionManager((MainActivity) mActivity,  mActivity.getSessionMessageHandler());  // TODO : Only for test
                mActivity.setSessionManager(sessionManager); // So that we can kill session from MainActivity (i.e., from onPause)
                mManager.requestConnectionInfo(mChannel, sessionManager);
            } else {
                mConnected = false;
            }
        } else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            Log.i(mTag, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
        }
    }
}
