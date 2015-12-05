package com.jzap.turing.turinggame.Player;

import com.jzap.turing.turinggame.Message.SessionMessage;

/**
 * Created by JZ_W541 on 12/2/2015.
 */
public abstract class AiPlayer extends Player {

    protected SessionMessage mAnswerSessionMessage;

    public AiPlayer(PlayersManager playersManager) {
        super(playersManager, "AI", "AI");
        hideName();
    }

    public abstract SessionMessage answerQuestion();

    public abstract void postAnswerLocally();
}
