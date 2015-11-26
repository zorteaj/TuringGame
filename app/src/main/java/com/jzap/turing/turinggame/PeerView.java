package com.jzap.turing.turinggame;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class PeerView extends LinearLayout {

    private WifiP2pDevice mDevice = null;
    private TextView mDeviceName_TextView;
    private TextView mAnswer_TextView;
    private ViewGroup.LayoutParams mLayoutParams;

    // TODO : Give these a boarder so visually distinguishable

    public PeerView(Context context, WifiP2pDevice device) {
        super(context);

        this.setBackgroundColor(0xf0d2f4); // TODO : UI Update
        this.setOrientation(VERTICAL);

        mDevice = device;

        mDeviceName_TextView = new TextView(context);
        mAnswer_TextView = new TextView(context);

        mLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mDeviceName_TextView.setText(mDevice.deviceName);

        mDeviceName_TextView.setLayoutParams(mLayoutParams);
        mAnswer_TextView.setLayoutParams(mLayoutParams);

        createView();
    }

    private void createView() {
        this.addView(mDeviceName_TextView);
        this.addView(mAnswer_TextView);
    }

    public WifiP2pDevice getDevice() {
        return mDevice;
    }

    public void setAnswer(String answer) {
        mAnswer_TextView.setText("Answer: " + answer); // TODO : There are nicer ways to do this
    }
}
