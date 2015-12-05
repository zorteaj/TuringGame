package com.jzap.turing.turinggame.Session;


import android.os.Handler;
import android.util.Log;

import com.jzap.turing.turinggame.Message.LocalSessionMessageTypes;
import com.jzap.turing.turinggame.UI.MainActivity;
import com.jzap.turing.turinggame.UI.PlayersUIActivity;

/**
 * Created by JZ_W541 on 11/25/2015.
 *
 */

// Disseminates messages to PlayersUIActivity from
// other threads and other classes
public class SessionMessageHandler extends Handler {

    private static final String mTag = "SessionMessageHandler";

    private PlayersUIActivity mActivity;

    public SessionMessageHandler(PlayersUIActivity activity) {
        mActivity = activity;
    }

    @Override
    public void handleMessage(android.os.Message message) {

        switch(message.what) {
            case LocalSessionMessageTypes.CONTENT_QUESTION : mActivity.setQuestion((String) message.obj);
                break;
            case LocalSessionMessageTypes.CONTENT_SESSION : mActivity.setSession((Session) message.obj);
                break;
            case LocalSessionMessageTypes.CONTROL_ENABLE_ANSWER_BUTTON : mActivity.getSubmitAnswerButton().setEnabled(true);
                break;
            case LocalSessionMessageTypes.CONTROL_DISABLE_ANSWER_BUTTON: mActivity.getSubmitAnswerButton().setEnabled(false);
                break;
            case LocalSessionMessageTypes.CONTROL_ENABLE_VOTING: mActivity.getPlayersManager().enableVoting(true);
                break;
            case LocalSessionMessageTypes.CONTROL_DISABLE_VOTING: mActivity.getPlayersManager().enableVoting(false);
                break;
        }
    }
}
