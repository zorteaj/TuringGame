package com.jzap.turing.turinggame.Message;

import com.jzap.turing.turinggame.Player.Player;

import java.io.Serializable;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class SessionMessage implements Serializable {

    private String mPlayerId; // Device MAC Address OR "AI" for AI
    private String mPlayerName;
    private NetType mNetType;
    private String mBody;

    public enum NetType {QUESTION_REQUEST, QUESTION, ANSWER, VOTE}

    public SessionMessage(Player player, NetType netType, String body) {
        this(player.getId(), player.getName(), netType, body);
    }

    public SessionMessage(String playerId, String playerName, NetType netType, String body) {
        mPlayerId = playerId;
        mPlayerName = playerName;
        mNetType = netType;
        mBody = body;
    }

    public String getPlayerId() {
        return mPlayerId;
    }

    public String getPlayerName()  {return mPlayerName;}

    public NetType getType() {
        return mNetType;
    }

    public String getBody() {
        return mBody;
    }
}
