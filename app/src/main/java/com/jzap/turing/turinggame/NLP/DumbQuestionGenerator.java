package com.jzap.turing.turinggame.NLP;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by JZ_W541 on 12/2/2015.
 */
public class DumbQuestionGenerator implements QuestionGenerator {
    List<String> mQuestions;

    public DumbQuestionGenerator() {
        bootstrap();
    }

    // Hard-coded set of questions. Alternative to
    // bootstrapping may be configuration via automatic load
    // from a database or simply a UI input
    private void bootstrap() {
        mQuestions = new ArrayList<>();
        mQuestions.add("Why is the sky blue?");
        mQuestions.add("How are you feeling today?");
        mQuestions.add("Will you please entertain me?");
    }

    @Override
    public String fetchQuestion() {
        Random random = new Random();
        return mQuestions.get(random.nextInt(mQuestions.size()));
    }
}
