package com.jzap.turing.turinggame.Player;

import com.jzap.turing.turinggame.Message.Message;

/**
 * Created by JZ_W541 on 12/2/2015.
 */
public abstract class AiPlayer extends Player {

    protected Message mAnswerMessage;

    public AiPlayer(PlayersManager playersManager) {
        super(playersManager, "AI", "AI");
        hideName();
    }

    public abstract Message answerQuestion();

    public abstract void postAnswerLocally();
}
