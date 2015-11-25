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
import java.util.Random;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class WifiP2pBroadcastReceiver extends BroadcastReceiver {

    private String mTag = "WifiP2pBroadcastReceiver";
    private MainActivity activity;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    private boolean mConnected = false;

    private List peers = new ArrayList();


    public WifiP2pBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity theActivity) {

        super();

        Log.i(mTag, "WifiP2pBroadcastReceiver constructed");

        activity = theActivity;
        mManager = manager;
        mChannel = channel;

    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }

/*    // TODO : It seems from the tutorial that this shouldn't be here
    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {

        private String mTag = "WifiP2pBroadcastReceiverPL";

        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            // Out with the old, in with the new.
            peers.clear();
            peers.addAll(peerList.getDeviceList());

            Log.i(mTag, "PEERS AVAILABLE");

            Log.i(mTag, "Number of peers = " + peers.size());

            // Log.i(mTag, peers.toString());

            activity.setPeers(peers);


            // If an AdapterView is backed by this data, notify it
            // of the change.  For instance, if you have a ListView of available
            // peers, trigger an update.

            *//*
            ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
            if (peers.size() == 0) {
                Log.d(WiFiDirectActivity.TAG, "No devices found");
                return;
            }
            *//*
            // TODO : Figure this out later
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
            activity.setmConnected_text(false);
            activity.setmDisconnected_text(true);
        }
    }


    // @Override TODO : Clearly this isn't meant to be here
    public void connect() {

        Random random = new Random();
        int rand = random.nextInt(peers.size());

        Log.i(mTag, "Checking to see if I'm already connected...");

        if(!mConnected) {

            Log.i(mTag, "I'm NOT already connected...");

            Log.i(mTag, "Do I have peers?..");

            if(peers.size() > 0) {
                Log.i(mTag, "Yes, I have peers");
            } else {
                Log.i(mTag, "No, I don't have any peers");
            }

            // for (int i = 0; i < peers.size(); i++) {

            WifiP2pDevice device = (WifiP2pDevice) peers.get(rand);

            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;
            config.wps.setup = WpsInfo.PBC;

                *//*
                config.groupOwnerIntent = 15; // TODO : This may be key to multiple connections
                *//*

            Log.i(mTag, "Attempting connect with peer " + rand);

            mManager.connect(mChannel, config, new MyConnectionActionListener(this));
            // }
        } else {
            Log.i(mTag, "I'm already connected...");
        }
    }*/

/*
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(mTag, "onReceive called");



        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            Log.i(mTag, "WIFI_P2P_STATE_CHANGED_ACTION");
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                activity.wifiP2pEnabledToast();
                activity.setmInit_text(true);
            } else {
                activity.wifiP2pDisabledToast();
                activity.setmInit_text(false);
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.i(mTag, "WIFI_P2P_PEERS_CHANGED_ACTION");

            activity.peersChangedToast();

            // The peer list has changed!  We should probably do something about
            // that.

            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                Log.i(mTag, "Requesting peers");
                mManager.requestPeers(mChannel, peerListListener);
            }


        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed!  We should probably do something about
            // that.
            Log.i(mTag, "WIFI_P2P_CONNECTION_CHANGED_ACTION");

            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                mConnected = true;

                Log.i(mTag, " ** CONNECTED! **");

                activity.toastConnected();
                activity.setmConnected_text(true);
                activity.setmDisconnected_text(false);

                // We are connected with the other device, request connection
                // info to find group owner IP

                MyConnectionInfoListener connectionInfoListener = new MyConnectionInfoListener(activity);

                mManager.requestConnectionInfo(mChannel, connectionInfoListener);
            } else {
                mConnected = false;
                activity.toastDisconnected();
                activity.setmConnected_text(false);
                activity.setmDisconnected_text(true);
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

            *//*
            DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
            *//*

            // TODO : Figure this out later
            Log.i(mTag, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
        } else {

            Log.i(mTag, "NONE OF THE ACTIONS YOU WERE EXPECTING");
        }

    }*/
}
