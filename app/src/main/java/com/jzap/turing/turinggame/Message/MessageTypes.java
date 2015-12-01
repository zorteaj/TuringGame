package com.jzap.turing.turinggame.Message;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class MessageTypes {
    // Content
    public static final int CONTENT_QUESTION = 0;
    public static final int CONTENT_ANSWER = 1;
    public static final int CONTENT_SESSION = 2;
    // Control
    public static final int CONTROL_ENABLE_ANSWER_BUTTON = 3;
    public static final int CONTROL_DISABLE_ANSWER_BUTTON = 4;
    public static final int CONTROL_ENABLE_VOTING = 5;
    public static final int CONTROL_DISABLE_VOTING = 6;
    public static final int CONTROL_ADD_PLAYER = 7;
    public static final int CONTROL_REVEAL_NAME = 8;
    public static final int CONTROL_HIDE_NAME = 9;
    // Info
    public static final int INFO_POINT_SCORED = 10;
    public static final int INFO_THIS_PLAYER_SCORED = 11;
    public static final int INFO_THIS_PLAYER_DID_NOT_SCORE = 12;
}
