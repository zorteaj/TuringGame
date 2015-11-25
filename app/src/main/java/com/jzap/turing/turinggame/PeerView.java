package com.jzap.turing.turinggame;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class PeerView extends LinearLayout {

    private TextView mDeviceName;
    private TextView mAnswer;
    private ViewGroup.LayoutParams mLayoutParams;

    // TODO : Give these a boarder so visually distinguishable

    public PeerView(Context context, String deviceName, String answer) {
        super(context);

        this.setBackgroundColor(Color.GREEN);
        this.setOrientation(VERTICAL);

        mDeviceName = new TextView(context);
        mAnswer = new TextView(context);

        mLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mDeviceName.setText(deviceName);
        mAnswer.setText(answer);

        mDeviceName.setLayoutParams(mLayoutParams);
        mAnswer.setLayoutParams(mLayoutParams);

        createView();
    }

    private void createView() {
        this.addView(mDeviceName);
        this.addView(mAnswer);
    }
}
