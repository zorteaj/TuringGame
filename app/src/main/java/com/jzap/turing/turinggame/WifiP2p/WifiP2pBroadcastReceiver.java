package com.jzap.turing.turinggame.WifiP2p;

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

import com.jzap.turing.turinggame.UI.MainActivity;
import com.jzap.turing.turinggame.UI.PlayersUIActivity;
import com.jzap.turing.turinggame.Player.PlayersManager;
import com.jzap.turing.turinggame.Session.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JZ_W541 on 11/25/2015.
 */

public class WifiP2pBroadcastReceiver extends BroadcastReceiver {

    private String mTag = "WifiP2pBroadcastReceiver";

    private PlayersUIActivity mActivity;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    private boolean mConnected = false;
    private List mPeers = new ArrayList();


    public WifiP2pBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, PlayersUIActivity activity) {

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
            if(!mConnected) { // TODO : I haven't thought this all the way through

                // Out with the old, in with the new.
                mPeers.clear();
                mPeers.addAll(peerList.getDeviceList());

                Log.i(mTag, mPeers.size() + " Peers Available");

                //mActivity.setPeers(mPeers);
                ((MainActivity) mActivity).getPlayersManager().setPeers(mPeers); // TODO : This can't be good design

                ((MainActivity) mActivity).removeProgressBar();
            }
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

    public void connect() {
        Log.i(mTag, "Connect called");
        if (!mConnected) {
            Log.i(mTag, "Not yet connected");
            if (mPeers.size() > 0) {
                WifiP2pDevice device = (WifiP2pDevice) mPeers.get(0);

                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;

                Log.i(mTag, "Trying to connect to " + device.deviceAddress);

                mManager.connect(mChannel, config, new WifiP2pConnectionActionListener(this));
            } else {
                Log.i(mTag, "No peers with which to connect");
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
            Log.i(mTag, intent.toString());

            if(mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            Log.i(mTag, networkInfo.toString());

            if(networkInfo.isConnected()) {
                Log.i(mTag, "Connected!");
                mConnected = true;

                // We are connected with the other device, request connection
                // info to find group owner IP
                SessionManager sessionManager = new SessionManager(mActivity.getSessionMessageHandler(), ((MainActivity) mActivity).getPlayersManager()); // TODO : Bad design
                mActivity.setSessionManager(sessionManager); // So that we can kill session from MainActivity (i.e., from onPause)
                mManager.requestConnectionInfo(mChannel, sessionManager);
            } else {
                Log.i(mTag, "Connection changed, but not connected...");
                mConnected = false;
            }
        } else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            Log.i(mTag, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");

            PlayersManager playersManager = ((MainActivity) mActivity).getPlayersManager(); // TODO : This must be bad design (the cast)
            playersManager.getThisPlayer().setId(((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)).deviceAddress);

           /* Player thisPlayer = new Player((WifiP2pDevice)intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE), playersManager, true);
            playersManager.setThisPlayer(thisPlayer);*/
        }
    }
}
