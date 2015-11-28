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
    private PlayerView mPlayerView;
    private String mId;

    private PlayerHandler mPlayerHandler;

    Player(WifiP2pDevice device, PlayersManager playersManager, boolean thisPlayer) {
        this(device.deviceAddress, null, playersManager, thisPlayer);
    }

    Player(WifiP2pDevice device, PlayersManager playersManager) {
        this(device, playersManager, false);
    }

    Player(String id, PlayersManager playersManager) {
        this(id, null, playersManager, false);
    }

    Player(String id, String answer, PlayersManager playersManager) {
        this(id, answer, playersManager, false);
    }

    Player(String id, String answer, PlayersManager playersManager, boolean thisPlayer) {
        mId = id;
        mPlayersManager = playersManager;
        mPlayerHandler = new PlayerHandler(this);
        if(!thisPlayer) {
            addSelfToPlayersManager();
        }
        if(answer != null) {
            setAnswer(answer);
        }
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
            } else if(message.what == MessageTypes.CONTROL_ADD_PLAYER) {
                mPlayerView = new PlayerView(mPlayersManager.getActivity(), mId);
                mPlayersManager.getPlayersLinearLayout().addView(mPlayerView);
            }
        }
    }

    private void addSelfToPlayersManager() {
        mPlayerHandler.obtainMessage(MessageTypes.CONTROL_ADD_PLAYER).sendToTarget();

        /*mPlayerView = new PlayerView(mPlayersManager.getActivity(), mId);
        mPlayersManager.getPlayersLinearLayout().addView(mPlayerView);*/
    }

  /*  //public WifiP2pDevice getDevice() {
        return mDevice;
    }*/

    public String getId() {
        return mId;
    }

    public void setAnswer(String answer) {
        mPlayerHandler.obtainMessage(MessageTypes.CONTENT_ANSWER, answer).sendToTarget();
    }

   /* public void setPlayerView(PlayerView playerView) {
        mPlayerView = playerView;
    }*/

    public PlayerView getPlayerView() {
        return mPlayerView;
    }

/*    public PlayersManager getPlayersManager() {
        return mPlayersManager;
    }*/

}
