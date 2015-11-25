package com.jzap.turing.turinggame;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class PeerView extends LinearLayout {

    private TextView mDeviceName;
    private TextView mAnswer;

    public PeerView(Context context, String devicename, String answer) {
        super(context);
        mDeviceName.setText(devicename);
        mAnswer.setText(answer);
        createView();
    }

    private void createView() {
        this.addView(mDeviceName);
        this.addView(mAnswer);
    }


}
