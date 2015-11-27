package com.jzap.turing.turinggame;

import android.net.wifi.p2p.WifiP2pDevice;

import java.io.Serializable;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by JZ_W541 on 11/27/2015.
 */
public class Player implements Serializable {

    private PlayersManager mPlayersManager;
    private WifiP2pDevice mDevice;
    private PlayerView mPlayerView;

    private PlayerHandler mPlayerHandler;

    Player(WifiP2pDevice device, PlayersManager playersManager, boolean thisPlayer) {
        mDevice = device;
        mPlayersManager = playersManager;
        mPlayerHandler = new PlayerHandler(this);
        if(!thisPlayer) {
            addSelfToPlayersManager();
        }
    }

    Player(WifiP2pDevice device, PlayersManager playersManager) {
        this(device, playersManager, false);
    }

    private class PlayerHandler extends Handler {

        private Player mPlayer;

        public PlayerHandler(Player player) {
            super(Looper.getMainLooper());
            mPlayer = player;
        }

        @Override
        public void handleMessage(android.os.Message message) {
            if (message.what == MessageTypes.CONTENT_ANSWER) {
                mPlayer.getPlayerView().setAnswer((String) message.obj);
            }
        }
    }

    private void addSelfToPlayersManager() {
        mPlayerView = new PlayerView(mPlayersManager.getActivity(), mDevice);
        mPlayersManager.getPlayersLinearLayout().addView(mPlayerView);
    }

    public WifiP2pDevice getDevice() {
        return mDevice;
    }

    public String getId() {
        return mDevice.deviceAddress;
    }

    public void setAnswer(String answer) {
        mPlayerHandler.obtainMessage(MessageTypes.CONTENT_ANSWER, answer).sendToTarget();
    }

    public PlayerView getPlayerView() {
        return mPlayerView;
    }
}
