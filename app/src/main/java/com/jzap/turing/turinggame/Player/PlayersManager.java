package com.jzap.turing.turinggame.Player;

import android.graphics.Color;
import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.widget.LinearLayout;

import com.jzap.turing.turinggame.UI.MainActivity;
import com.jzap.turing.turinggame.Message.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by JZ_W541 on 11/27/2015.
 */
public class PlayersManager {

    private static final String mTag = "PlayersManager";

    // This device's player
    private Player mThisPlayer = null;
    private AppCompatActivity mActivity;
    private List<Player> mPlayersList;
    private LinearLayout mPlayers_LinearLayout;

    public PlayersManager(AppCompatActivity activity) {
        mActivity = activity;
        mPlayersList = new ArrayList();
        mPlayers_LinearLayout = ((MainActivity) mActivity).getPlayersLinearLayout(); // TODO : This can't be good design
    }

    // TODO : If user clicks set peers in the middle of the game, it will update the list and blank out answers.  Should probably disable this manually and automatically (in case
    // a new person tries to join - might mess things ups
    public void setPeers(List peers) {
        //mPlayers_LinearLayout.removeAllViews();
        clearPlayersListExceptThis();
        for(int i = 0; i < peers.size(); i++) {
            new Player((WifiP2pDevice) peers.get(i), this);
        }
    }

    // Clears out mPlayersList, except for this device's player
    private void clearPlayersListExceptThis() {
        Iterator<Player> iter = mPlayersList.iterator();
        while(iter.hasNext()) {
            Player player = iter.next();
            if(!(player.getId().equals(mThisPlayer.getId()))) {
                mPlayers_LinearLayout.removeView(player.getPlayerView());
                iter.remove();
            }
        }
    }

    public AppCompatActivity getActivity() {
        return mActivity;
    }

    public LinearLayout getPlayersLinearLayout(){
        return mPlayers_LinearLayout;
    }

    public void processAnswer(Message message) {
        Log.i(mTag, "Answer = " + message.getBody());
        Player player = null;
        if ((player = findPlayerById(message.getPlayerId())) != null) {
            player.setName(message.getPlayerName());
            player.setAnswer(message.getBody());
        } else {
            // TODO : Add player??
            Log.i(mTag, "Add failed");
        }
    }

    public void setThisPlayer(Player player) {
        Log.i(mTag, "This player is set");
        mThisPlayer = player;
    }

    public Player getThisPlayer() {
        return mThisPlayer;
    }

    public Player findPlayerById(String id) {
        Player player = null;
        for(int i = 0; i < mPlayersList.size(); i++) {
            if (id.equals(mPlayersList.get(i).getId())) {
                player = mPlayersList.get(i);
            }
        }
        return player;
    }

    List<Player> getPlayersList() {
        return mPlayersList;
    }

    public void enableVoting(boolean enable) {
        Log.i(mTag, "Enable = " + String.valueOf(enable));
        for(int i = 0; i < mPlayersList.size(); i++) {
            mPlayersList.get(i).getPlayerView().setClickable(enable);
            mPlayersList.get(i).getPlayerView().setEnabled(enable);
        }
    }

    public void revealNames() {
        for(int i = 0; i < mPlayersList.size(); i++) {
            mPlayersList.get(i).revealName();
        }
    }

    public void hideNames() {
        for(int i = 0; i < mPlayersList.size(); i++) {
            mPlayersList.get(i).hideName();
        }
    }
}
