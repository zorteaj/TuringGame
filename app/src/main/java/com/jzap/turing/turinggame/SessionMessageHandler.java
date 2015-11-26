package com.jzap.turing.turinggame;


import android.os.Handler;

/**
 * Created by JZ_W541 on 11/25/2015.
 *
 */

// Disseminates messages to PeerDisplayActivity from
// other threads and other classes
public class SessionMessageHandler extends Handler {

    private PeerDisplayActivity mActivity;

    public SessionMessageHandler(PeerDisplayActivity activity) {
        mActivity = activity;
    }

    @Override
    public void handleMessage(android.os.Message message) {

        switch(message.what) {
            case MessageTypes.CONTENT_QUESTION : mActivity.setQuestion((String) message.obj);
                break;
            case MessageTypes.CONTENT_ANSWER : mActivity.setAnswer((String) message.obj); // TODO : Support multiple answers!! This is a test for now...
                break;
            case MessageTypes.CONTENT_SESSION : mActivity.setSession((Session) message.obj);
                break;
            case MessageTypes.CONTROL_ENABLE_ANSWER_BUTTON : mActivity.getSubmitAnswerButton().setClickable(true);
                break;
            case MessageTypes.CONTROL_DISABLE_ANSWER_BUTTON: mActivity.getSubmitAnswerButton().setClickable(false);
        }
    }
}
