package com.jzap.turing.turinggame.Session;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.jzap.turing.turinggame.Message.LocalSessionMessageTypes;
import com.jzap.turing.turinggame.Player.PlayersManager;


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

        mGroupOwnerAddress = info.groupOwnerAddress.getHostAddress();

        // After the group negotiation, we can determine the group owner.
        if (info.groupFormed && info.isGroupOwner) {
            mSession = new GroupOwnerSession(mPlayersManager, mHandler);
        } else if (info.groupFormed) {
            // The other device acts as the client.
            mSession = new ClientSession(mPlayersManager, mGroupOwnerAddress, mHandler);
        }

        if(mSession != null) {
            mHandler.obtainMessage(LocalSessionMessageTypes.CONTENT_SESSION, mSession).sendToTarget(); // Pass Session reference to PlayersUIActivity to sync UI state with Session state
            new Thread(mSession).start();
        }

    }

    public void terminate() {
        mSession.setState(Session.SessionState.TERMINATE);
    }

}