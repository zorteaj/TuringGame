package com.jzap.turing.turinggame;

import android.net.wifi.p2p.WifiP2pDevice;

import java.io.Serializable;

/**
 * Created by JZ_W541 on 11/27/2015.
 */
public class Player implements Serializable {

    private PlayersManager mPlayersManager;
    private WifiP2pDevice mDevice;
    private PlayerView mPlayerView;

    Player(WifiP2pDevice device, PlayersManager playersManager, boolean thisPlayer) {
        mDevice = device;
        mPlayersManager = playersManager;
        if(!thisPlayer) {
            addSelfToPlayersManager();
        }
    }

    Player(WifiP2pDevice device, PlayersManager playersManager) {
       this(device, playersManager, false);
    }

    private void addSelfToPlayersManager() {
        mPlayerView = new PlayerView(mPlayersManager.getActivity(), mDevice);
        mPlayersManager.getPlayersLinearLayout().addView(mPlayerView);
    }

    public WifiP2pDevice getDevice() {
        return mDevice;
    }
}
