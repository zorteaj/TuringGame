package com.jzap.turing.turinggame.Session;


import android.os.Handler;

import com.jzap.turing.turinggame.Message.MessageTypes;
import com.jzap.turing.turinggame.UI.MainActivity;
import com.jzap.turing.turinggame.UI.PeerDisplayActivity;

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
            case MessageTypes.CONTENT_SESSION : mActivity.setSession((Session) message.obj);
                break;
            case MessageTypes.CONTROL_ENABLE_ANSWER_BUTTON : mActivity.getSubmitAnswerButton().setClickable(true);
                break;
            case MessageTypes.CONTROL_DISABLE_ANSWER_BUTTON: mActivity.getSubmitAnswerButton().setClickable(false);
                break;
            case MessageTypes.CONTROL_ENABLE_VOTING: ((MainActivity) mActivity).getPlayersManager().enableVoting(true); // TODO : Bad design (cast)
                break;
            case MessageTypes.CONTROL_DISABLE_VOTING: ((MainActivity) mActivity).getPlayersManager().enableVoting(false);
                break;
        }
    }
}