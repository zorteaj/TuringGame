package com.jzap.turing.turinggame.Player;

import com.jzap.turing.turinggame.Message.Message;

/**
 * Created by JZ_W541 on 11/28/2015.
 */
public class AiPlayer extends Player {

    private Message mAnswerMessage;

    public AiPlayer(PlayersManager playersManager) {
        super( playersManager, "AI", "AI");
        hideName();
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
