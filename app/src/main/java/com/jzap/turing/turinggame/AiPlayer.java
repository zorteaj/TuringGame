package com.jzap.turing.turinggame;

import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Created by JZ_W541 on 11/28/2015.
 */
public class AiPlayer extends Player {

    private Message mAnswerMessage;

    AiPlayer(PlayersManager playersManager) {
        super("AI", playersManager);
    }

    public Message answerQuestion() {
        mAnswerMessage = new Message(this, Message.Type.ANSWER, calculateAnswer());
        return mAnswerMessage;
    }

    private String calculateAnswer() {
        String answer = "AI Smart Answer!";
        return answer;
    }

    public void postAnswerLocally() {
        mPlayersManager.processAnswer(mAnswerMessage);
    }

}
