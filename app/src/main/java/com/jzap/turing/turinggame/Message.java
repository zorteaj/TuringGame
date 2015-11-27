package com.jzap.turing.turinggame;

import java.io.Serializable;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class Message implements Serializable { // TODO : Rename to deconflict with android.os.Message

    private String mPlayerId; // Device MAC Address
    private Type mType;
    private String mBody;

    public enum Type {QUESTION_REQUEST, QUESTION, ANSWER}

    public Message(Player player, Type type, String body) {
        mPlayerId = player.getDevice().deviceAddress;
        mType = type;
        mBody = body;
    }

    public String getPlayerId() {
        return mPlayerId;
    }

    public Type getType() {
        return mType;
    }

    public String getBody() {
        return mBody;
    }
}
