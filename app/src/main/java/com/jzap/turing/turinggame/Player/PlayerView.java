package com.jzap.turing.turinggame.Player;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jzap.turing.turinggame.R;
import com.jzap.turing.turinggame.UI.MainActivity;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
@TargetApi(16)
public class PlayerView extends LinearLayout {

    private static final String mTag = "PlayerView";

    private String mPlayerId;
    private String mPlayerName;
    private TextView mPlayerName_TextView;
    private TextView mAnswer_TextView;
    private TextView mPoints_TextView;
    private ViewGroup.LayoutParams mLayoutParams;
    private MainActivity mActivity;

    public PlayerView(Context context, String playerId, String playerName) {
        super(context);

        mPlayerId = playerId;
        mPlayerName = playerName;
        mPlayerName_TextView = new TextView(context);
        mAnswer_TextView = new TextView(context);
        mPoints_TextView = new TextView(context);

        mLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mPlayerName_TextView.setText(mPlayerName);

        mPlayerName_TextView.setLayoutParams(mLayoutParams);
        mPlayerName_TextView.setTextColor(Color.BLACK);
        mAnswer_TextView.setLayoutParams(mLayoutParams);
        mPoints_TextView.setLayoutParams(mLayoutParams);

        setAnswer("");
        setPoints(0);

        mActivity = (MainActivity) context;

        setUpOnClickListener();
        setClickable(false);

        createView();
    }

    private void createView() {
        configureView();
        this.addView(mPlayerName_TextView);
        this.addView(mAnswer_TextView);
        this.addView(mPoints_TextView);
    }

    private void configureView() {
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.player_border_disabled));
        } else {
            this.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.player_border_disabled));
        }
        this.setOrientation(VERTICAL);
        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setAnswer(String answer) {
        mAnswer_TextView.setText("Answer: " + answer);
    }

    public void setName(String name) {
        mPlayerName = name;
        mPlayerName_TextView.setText(mPlayerName);
    }

    public void setPlayerId(String id) {
        mPlayerId = id;
    }

    public void setPoints(int points) {
        Log.i(mTag, "Setting points");
        mPoints_TextView.setText("Points: " + String.valueOf(points));
    }

    public void hidePoints() {
        mPoints_TextView.setText("Points:");
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

    @Override
    public void setEnabled(boolean enabled) {
        final int sdk = android.os.Build.VERSION.SDK_INT;

        Drawable background;

        if(enabled) {
            background = ContextCompat.getDrawable(mActivity, R.drawable.player_border_enabled);
        } else {
            background = ContextCompat.getDrawable(mActivity, R.drawable.player_border_disabled);
        }

        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackgroundDrawable(background);
        } else {
            this.setBackground(background);
        }
    }
}
