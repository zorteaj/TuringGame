package com.jzap.turing.turinggame.Message;

import com.jzap.turing.turinggame.Player.Player;

import java.io.Serializable;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class SessionMessage implements Serializable {

    private String mPlayerId; // Device MAC Address OR "AI" for AI
    private String mPlayerName;
    private Type mType;
    private String mBody;

    public enum Type {QUESTION_REQUEST, QUESTION, ANSWER, VOTE}

    public SessionMessage(Player player, Type type, String body) {
        this(player.getId(), player.getName(), type, body);
    }

    public SessionMessage(String playerId, String playerName, Type type, String body) {
        mPlayerName = playerName;
        mPlayerId = playerId;
        mType = type;
        mBody = body;
    }

    public String getPlayerId() {
        return mPlayerId;
    }

    public String getPlayerName()  {return mPlayerName;}

    public Type getType() {
        return mType;
    }

    public String getBody() {
        return mBody;
    }
}
