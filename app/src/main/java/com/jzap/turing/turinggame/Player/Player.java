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
    private String mName;

    private int mPoints = 0;

    private PlayerHandler mPlayerHandler;

    // TODO : Make order of constructor params consistent

    public Player(WifiP2pDevice device, PlayersManager playersManager, boolean thisPlayer) {
        this(device.deviceAddress, null, "?", playersManager, thisPlayer);
    }

    public Player(WifiP2pDevice device, PlayersManager playersManager) {
        this(device, playersManager, false);
    }

    public Player(String id, String answer, PlayersManager playersManager) {
        this(id, answer, "?", playersManager, false);
    }

    public Player(PlayersManager playersManager, String id, String name) {
        this(id, null, name, playersManager, false);
    }

    public Player(String id, PlayersManager playersManager) {
        this(id, null, "?", playersManager, false);
    }

    public Player(PlayersManager playersManager, String name, boolean thisPlayer) {
        this(null, null, name, playersManager, thisPlayer);
    }

    public Player(String id, String answer, String name, PlayersManager playersManager, boolean thisPlayer) {
        if(name != null) {
            setName(name);
        }
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

        // TODO : Make switch statement
        @Override
        public void handleMessage(android.os.Message message) {
            if (message.what == MessageTypes.CONTENT_ANSWER) {
                mPlayer.getPlayerView().setAnswer((String) message.obj);
            } else if (message.what == MessageTypes.CONTROL_ADD_PLAYER) {
                mPlayerView = new PlayerView(mPlayersManager.getActivity(), mId, mName);
                mPlayersManager.getPlayersList().add(mPlayer);
                mPlayersManager.getPlayersLinearLayout().addView(mPlayerView);
            } else if (message.what == MessageTypes.INFO_THIS_PLAYER_SCORED) {
                Toast.makeText(mPlayersManager.getActivity(), "Correct! You now have " + (Integer) message.obj + " points!",
                        Toast.LENGTH_LONG).show();
            } else if(message.what == MessageTypes.INFO_THIS_PLAYER_DID_NOT_SCORE) {
                Toast.makeText(mPlayersManager.getActivity(), "Wrong!",
                        Toast.LENGTH_LONG).show();
             }else if (message.what == MessageTypes.INFO_POINT_SCORED) {
                mPlayer.getPlayerView().setPoints((Integer) message.obj);
            } else if(message.what == MessageTypes.CONTROL_REVEAL_NAME) {
                mPlayer.getPlayerView().setName((String) message.obj);
            } else if(message.what == MessageTypes.CONTROL_HIDE_NAME) {
                mPlayer.getPlayerView().setName("?");
            }
        }
    }

    private void addSelfToPlayersManager() {
        mPlayerHandler.obtainMessage(MessageTypes.CONTROL_ADD_PLAYER).sendToTarget();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
        mPlayerView.setPlayerId(mId); // TODO : This seems to be a lot of indirection
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public void revealName() {
        mPlayerHandler.obtainMessage(MessageTypes.CONTROL_REVEAL_NAME, mName).sendToTarget();
    }

    public void hideName() {
        mPlayerHandler.obtainMessage(MessageTypes.CONTROL_HIDE_NAME, mName).sendToTarget();
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

    public void guessedWrong() {
        if(this == mPlayersManager.getThisPlayer()) {
            mPlayerHandler.obtainMessage(MessageTypes.INFO_THIS_PLAYER_DID_NOT_SCORE).sendToTarget();
        }
    }

/*    protected void postAnswerLocally() {
        mPlayersManager.processAnswer(mAnswerMessage);
    }*/

}
