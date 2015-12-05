package com.jzap.turing.turinggame.Player;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.widget.LinearLayout;

import com.jzap.turing.turinggame.UI.MainActivity;
import com.jzap.turing.turinggame.Message.SessionMessage;

import java.util.ArrayList;
import java.util.Collections;
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

    public void setPeers(List peers) {
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

    public void processAnswer(SessionMessage sessionMessage) {
        Player player = null;
        if ((player = findPlayerById(sessionMessage.getPlayerId())) != null) {
            player.setName(sessionMessage.getPlayerName());
            player.setAnswer(sessionMessage.getBody());
        } else {
            // TODO : Add player??
            Log.i(mTag, "Add failed");
        }
    }

    public void setThisPlayer(Player player) {
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

    public void shufflePlayersAndHideNames() {
        shufflePlayers();
        annonymizePlayers();
    }

    public void shufflePlayers() {
        mPlayers_LinearLayout.removeAllViews();
        Collections.shuffle(mPlayersList);
        for(int i = 0; i < mPlayersList.size() ; i++) {
            mPlayers_LinearLayout.addView(mPlayersList.get(i).getPlayerView());
        }
    }

    public void annonymizePlayers() {
        for(int i = 0; i < mPlayersList.size(); i++) {
            mPlayersList.get(i).annonymize();
        }
    }
}
