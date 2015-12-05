package com.jzap.turing.turinggame.Player;

import com.jzap.turing.turinggame.Message.SessionMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by JZ_W541 on 11/28/2015.
 */
public class DumbAiPlayer extends AiPlayer {

    private List<String> mAnswers;

    public DumbAiPlayer(PlayersManager playersManager) {
        super(playersManager);
        mAnswers = new ArrayList();
        bootstrap();
        hideName();
    }

    public SessionMessage answerQuestion() {
        mAnswerSessionMessage = new SessionMessage(this, SessionMessage.Type.ANSWER, calculateAnswer());
        return mAnswerSessionMessage;
    }

    public void postAnswerLocally() {
        mPlayersManager.processAnswer(mAnswerSessionMessage);
    }

    private String calculateAnswer() {
        Random random = new Random();
        return mAnswers.get(random.nextInt(mAnswers.size()));
    }

    private void bootstrap() {
        mAnswers.add("Yes");
        mAnswers.add("No");
        mAnswers.add("I'd rather not");
        mAnswers.add("i'll get back to u on that");
        mAnswers.add("i dunno");
        mAnswers.add("Who's askin?");
    }

}
