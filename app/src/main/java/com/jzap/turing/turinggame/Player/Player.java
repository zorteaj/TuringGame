package com.jzap.turing.turinggame.Player;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;

import java.io.Serializable;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.jzap.turing.turinggame.Message.LocalSessionMessageTypes;

/**
 * Created by JZ_W541 on 11/27/2015.
 */

// Player represents a question answerer and, except for AI, a voter
// New players automatically register themselves with  (add themselves to) the PlayersManager
public class Player implements Serializable {

    protected PlayersManager mPlayersManager;
    private PlayerView mPlayerView;
    private String mId;
    private String mName;
    private PlayerHandler mPlayerHandler;

    private int mPoints = 0;

    public Player(PlayersManager playersManager, WifiP2pDevice device, boolean thisPlayer) {
        this(playersManager, device.deviceAddress, "?", null, thisPlayer);
    }

    public Player(PlayersManager playersManager, WifiP2pDevice device) {
        this(playersManager, device, false);
    }

    public Player(PlayersManager playersManager, String id, String name) {
        this(playersManager, id, name, null, false);
    }

    public Player(String id, PlayersManager playersManager) {
        this(playersManager, id, "?", null, false);
    }

    public Player(PlayersManager playersManager, String name, boolean thisPlayer) {
        this(playersManager, null, name, null, thisPlayer);
    }

    public Player(PlayersManager playersManager, String id, String name, String answer, boolean thisPlayer) {
        if(name != null) {
            setName(name);
        }
        mId = id;
        mPlayersManager = playersManager;
        mPlayerHandler = new PlayerHandler(this);
        mPoints = 0;
        addSelfToPlayersManager();
        if(answer != null) {
            setAnswer(answer);
        }
        if(thisPlayer) {
            mPlayersManager.setThisPlayer(this);
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
            if (message.what == LocalSessionMessageTypes.CONTENT_ANSWER) {
                mPlayer.getPlayerView().setAnswer((String) message.obj);
            } else if (message.what == LocalSessionMessageTypes.CONTROL_ADD_PLAYER) {
                mPlayerView = new PlayerView((Context) mPlayersManager.getActivity(), mId, mName);
                mPlayersManager.getPlayersList().add(mPlayer);
                mPlayersManager.getPlayersLinearLayout().addView(mPlayerView);
            } else if (message.what == LocalSessionMessageTypes.INFO_THIS_PLAYER_SCORED) {
                String pointString = (Integer) message.obj == 1 ? " point!" : " points!";
                Toast.makeText((Context) mPlayersManager.getActivity(), "Correct! You now have " + (Integer) message.obj + pointString,
                        Toast.LENGTH_LONG).show();
            } else if(message.what == LocalSessionMessageTypes.INFO_THIS_PLAYER_DID_NOT_SCORE) {
                Toast.makeText((Context) mPlayersManager.getActivity(), "Wrong!",
                        Toast.LENGTH_LONG).show();
             }else if (message.what == LocalSessionMessageTypes.INFO_POINT_SCORED) {
                mPlayer.getPlayerView().setPoints((Integer) message.obj);
            } else if(message.what == LocalSessionMessageTypes.CONTROL_REVEAL_PLAYER) {
                mPlayer.getPlayerView().setName((String) message.obj);
                mPlayer.getPlayerView().setPoints(mPoints);
            } else if(message.what == LocalSessionMessageTypes.CONTROL_ANNONYMIZE_PLAYER) {
                mPlayer.getPlayerView().setName("?");
                mPlayer.getPlayerView().setAnswer("");
                mPlayer.getPlayerView().hidePoints();
            }
        }
    }

    private void addSelfToPlayersManager() {
        mPlayerHandler.obtainMessage(LocalSessionMessageTypes.CONTROL_ADD_PLAYER).sendToTarget();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
        mPlayerView.setPlayerId(mId);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public void revealName() {
        mPlayerHandler.obtainMessage(LocalSessionMessageTypes.CONTROL_REVEAL_PLAYER, mName).sendToTarget();
    }

    public void hideName() {
        mPlayerHandler.obtainMessage(LocalSessionMessageTypes.CONTROL_ANNONYMIZE_PLAYER, mName).sendToTarget();
    }

    public void annonymize() {
        mPlayerHandler.obtainMessage(LocalSessionMessageTypes.CONTROL_ANNONYMIZE_PLAYER, mName).sendToTarget();
    }

    public void setAnswer(String answer) {
        mPlayerHandler.obtainMessage(LocalSessionMessageTypes.CONTENT_ANSWER, answer).sendToTarget();
    }

    public PlayerView getPlayerView() {
        return mPlayerView;
    }

    public void addPoint() {
        mPoints++;
        mPlayerHandler.obtainMessage(LocalSessionMessageTypes.INFO_POINT_SCORED, mPoints).sendToTarget();
        // If this player (who scored the point) is the player on this device, send a toast on this device
        if(this == mPlayersManager.getThisPlayer()) {
            mPlayerHandler.obtainMessage(LocalSessionMessageTypes.INFO_THIS_PLAYER_SCORED, mPoints).sendToTarget();
        }
    }

    public void guessedWrong() {
        if(this == mPlayersManager.getThisPlayer()) {
            mPlayerHandler.obtainMessage(LocalSessionMessageTypes.INFO_THIS_PLAYER_DID_NOT_SCORE).sendToTarget();
        }
    }
}
