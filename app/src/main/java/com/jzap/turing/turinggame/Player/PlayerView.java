package com.jzap.turing.turinggame.Player;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jzap.turing.turinggame.UI.MainActivity;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class PlayerView extends LinearLayout {

    private static final String mTag = "PlayerView";

    //private WifiP2pDevice mDevice = null;
    private String mPlayerId;
    private String mPlayerName;
    private TextView mDeviceName_TextView;
    private TextView mAnswer_TextView;
    private TextView mPoints_TextView;
    private ViewGroup.LayoutParams mLayoutParams;
    private MainActivity mActivity;

    // TODO : Give these a boarder so visually distinguishable

    public PlayerView(Context context, String playerId, String playerName) {
        super(context);

        mPlayerId = playerId;
        mPlayerName = playerName;
        mDeviceName_TextView = new TextView(context);
        mAnswer_TextView = new TextView(context);
        mPoints_TextView = new TextView(context);

        mLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

       // mDeviceName_TextView.setText(mPlayerId);
        mDeviceName_TextView.setText(mPlayerName); // TODO : If continued to use this way, change name (it's not device name, it's player name)

        mDeviceName_TextView.setLayoutParams(mLayoutParams);
        mAnswer_TextView.setLayoutParams(mLayoutParams);
        mPoints_TextView.setLayoutParams(mLayoutParams);

        mActivity = (MainActivity) context; // TODO : Test

        setUpOnClickListener();
        setClickable(false);

        createView();
    }

    private void createView() {
        configureView();
        this.addView(mDeviceName_TextView);
        this.addView(mAnswer_TextView);
        this.addView(mPoints_TextView);
    }

    private void configureView() {
        this.setBackgroundColor(0xf0d2f4); // TODO : UI Update
        this.setOrientation(VERTICAL);
        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setAnswer(String answer) {
        mAnswer_TextView.setText("Answer: " + answer); // TODO : There are nicer ways to do this
    }

    public void setName(String name) {
        mPlayerName = name;
        mDeviceName_TextView.setText(mPlayerName);
    }

    public void setPlayerId(String id) {
        mPlayerId = id;
    }

    public void setPoints(int points) {
        Log.i(mTag, "Setting points");
        mPoints_TextView.setText("Points: " + String.valueOf(points)); // TODO : There are nicer ways to do this
    }

    public void setUpOnClickListener() {
        setOnClickListener(new View.OnClickListener() {

            // This a vote cast
            @Override
            public void onClick(View v) {
                mActivity.getSession().castVote(mPlayerId);
                mActivity.getPlayersManager().revealNames();
            }
        });
    }
}
