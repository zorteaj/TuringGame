package com.jzap.turing.turinggame.Player;

import com.jzap.turing.turinggame.Message.Message;

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

    public Message answerQuestion() {
        mAnswerMessage = new Message(this, Message.Type.ANSWER, calculateAnswer());
        return mAnswerMessage;
    }

    public void postAnswerLocally() {
        mPlayersManager.processAnswer(mAnswerMessage);
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
