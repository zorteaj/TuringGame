package com.jzap.turing.turinggame.Player;

import android.net.wifi.p2p.WifiP2pDevice;

import java.io.Serializable;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.jzap.turing.turinggame.Message.MessageTypes;

/**
 * Created by JZ_W541 on 11/27/2015.
 */

// Player represents a question answerer and, except for AI, a voter
// New players automatically register themselves with  (add themselves to) the PlayersManager
public class Player implements Serializable {

    protected PlayersManager mPlayersManager;
    private PlayerView mPlayerView;
    private String mId;

    private int mPoints = 0;

    private PlayerHandler mPlayerHandler;

    public Player(WifiP2pDevice device, PlayersManager playersManager, boolean thisPlayer) {
        this(device.deviceAddress, null, playersManager, thisPlayer);
    }

    public Player(WifiP2pDevice device, PlayersManager playersManager) {
        this(device, playersManager, false);
    }

    public Player(String id, String answer, PlayersManager playersManager) {
        this(id, answer, playersManager, false);
    }

    public Player(String id, PlayersManager playersManager) {
        this(id, null, playersManager, false);
    }

    public Player(String id, String answer, PlayersManager playersManager, boolean thisPlayer) {
        mId = id;
        mPlayersManager = playersManager;
        mPlayerHandler = new PlayerHandler(this);
        mPoints = 0;
        //if(!thisPlayer) {
            addSelfToPlayersManager(); // TODO : Testing - is there a reason not to add self? Is it bad to show up in the list?
        //}
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
                mPlayersManager.getPlayersList().add(mPlayer);
                mPlayersManager.getPlayersLinearLayout().addView(mPlayerView);
            } else if(message.what == MessageTypes.INFO_THIS_PLAYER_SCORED) {
                Toast.makeText(mPlayersManager.getActivity(), "Point Scored! Total: " + (Integer) message.obj,
                        Toast.LENGTH_LONG).show();
            } else if (message.what == MessageTypes.INFO_POINT_SCORED) {
                mPlayer.getPlayerView().setPoints((Integer) message.obj);
            }
        }
    }

    private void addSelfToPlayersManager() {
        mPlayerHandler.obtainMessage(MessageTypes.CONTROL_ADD_PLAYER).sendToTarget();

        /*mPlayerView = new PlayerView(mPlayersManager.getActivity(), mId);
        mPlayersManager.getPlayersLinearLayout().addView(mPlayerView);*/ // TODO : Does this have something to do with the flickering on begin game/incoming question?
    }

    public String getId() {
        return mId;
    }

    public void setAnswer(String answer) {
        mPlayerHandler.obtainMessage(MessageTypes.CONTENT_ANSWER, answer).sendToTarget();
    }

    public PlayerView getPlayerView() {
        return mPlayerView;
    }

    public void addPoint() {
        mPoints++;
        mPlayerHandler.obtainMessage(MessageTypes.INFO_POINT_SCORED, mPoints).sendToTarget();
        // If this player (who scored the point) is the player on this device, send a toast on this device
        if(this == mPlayersManager.getThisPlayer()) {
            mPlayerHandler.obtainMessage(MessageTypes.INFO_THIS_PLAYER_SCORED, mPoints).sendToTarget();
        }
    }

}
