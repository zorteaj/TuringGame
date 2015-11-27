package com.jzap.turing.turinggame;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JZ_W541 on 11/27/2015.
 */
public class PlayersManager {

    private static final String mTag = "PlayersManager";

    // This device's player
    private Player mThisPlayer = null;
    private AppCompatActivity mActivity;
    private List<Player> mPlayers;
    private LinearLayout mPlayers_LinearLayout;

    public PlayersManager(AppCompatActivity activity) {
        mActivity = activity;
        mPlayers = new ArrayList();
        mPlayers_LinearLayout = ((MainActivity) mActivity).getPlayersLinearLayout(); // TODO : This can't be good design
    }

    // TODO : If user clicks set peers in the middle of the game, it will update the list and blank out answers.  Should probably disable this manually and automatically (in case
    // a new person tries to join - might mess things ups
    public void setPeers(List peers) {
        mPlayers_LinearLayout.removeAllViews();
        mPlayers.clear();
        for(int i = 0; i < peers.size(); i++) {
            mPlayers.add(new Player((WifiP2pDevice) peers.get(i), this));
        }
    }

    public AppCompatActivity getActivity() {
        return mActivity;
    }

    public LinearLayout getPlayersLinearLayout(){
        return mPlayers_LinearLayout;
    }

    public void processAnswer(Message message) {
        Log.i(mTag, "Answer = "  + message.getBody());
    }

    public void setThisPlayer(Player player) {
        Log.i(mTag, "This Player has been set");
        mThisPlayer = player;
    }

    public Player getThisPlayer() {
        return mThisPlayer;
    }
}
