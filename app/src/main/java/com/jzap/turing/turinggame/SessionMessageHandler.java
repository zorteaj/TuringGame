package com.jzap.turing.turinggame;


import android.os.Handler;
import android.util.TimeUtils;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class SessionMessageHandler extends Handler {

    private PeerDisplayActivity mActivity;

    SessionMessageHandler(PeerDisplayActivity activity) {
        mActivity = activity;
    }

    @Override
    public void handleMessage(android.os.Message message) {

        if (message.what == MessageTypes.QUESTION) {
            mActivity.setQuestion((String) message.obj);
        } else if (message.what == MessageTypes.ANSWER) {
           // mActivity.setAnswer((String) message.obj); // TODO : Probably need more information for this one
        }
    }
}
