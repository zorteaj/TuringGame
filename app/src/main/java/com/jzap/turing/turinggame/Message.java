package com.jzap.turing.turinggame;

import java.io.Serializable;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class Message implements Serializable { // TODO : Rename to deconflict with android.os.Message

    private String mType;
    private String mBody;

    private enum Type {QUESTION_REQUEST, ANSWER}

    Message(String type, String body) {
        mType = type;
        mBody = body;
    }

    public String getType() {
        return mType;
    }

    public String getBody() {
        return mBody;
    }
}
