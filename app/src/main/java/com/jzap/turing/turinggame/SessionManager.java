package com.jzap.turing.turinggame;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;


/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class SessionManager implements WifiP2pManager.ConnectionInfoListener {

    private static final String mTag = "WConnectionInfoListener";

    private Session mSession;
    private String mGroupOwnerAddress;
    private SessionMessageHandler mHandler;
    private PlayersManager mPlayersManager;

    public SessionManager(SessionMessageHandler handler, PlayersManager playersManager) {
        super();
        mHandler = handler;
        mPlayersManager = playersManager;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

        Log.i(mTag, "Connection Info Available...");

        mGroupOwnerAddress = info.groupOwnerAddress.getHostAddress();

        // After the group negotiation, we can determine the group owner.
        if (info.groupFormed && info.isGroupOwner) {
            Log.i(mTag, "I'm the group owner");
            mSession = new GroupOwnerSession(mPlayersManager, mHandler);
        } else if (info.groupFormed) {
            // The other device acts as the client.
            Log.i(mTag, "I'm not the group owner");
            mSession = new ClientSession(mPlayersManager, mGroupOwnerAddress, mHandler);
        }

        if(mSession != null) {
            mHandler.obtainMessage(MessageTypes.CONTENT_SESSION, mSession).sendToTarget(); // Pass Session reference to PeerDisplayActivity to sync UI state with Session state
            new Thread(mSession).start();
        }

    }

    public void terminate() {
        mSession.setState(Session.SessionState.TERMINATE);
    }

}