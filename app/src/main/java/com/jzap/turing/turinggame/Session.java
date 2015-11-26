package com.jzap.turing.turinggame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
abstract public class Session implements Runnable {

    protected static final int mPort = 8886; // TODO : Is this number okay?

    protected enum STATE {COLD, WAITING_FOR_QUESTION, ANSWERING, ANSWERED, TERMINATE}
    protected STATE mState;

    protected Socket mSocket = null;
    protected ObjectInputStream mIn = null;
    protected ObjectOutputStream mOut = null;

    Session() {
        mState = STATE.COLD; // TODO : Use this?
    }

    abstract protected void init();

    abstract protected void answerQuestion();

    abstract protected void castVote();

    abstract protected void sendMessage();

    protected void end() {
        try {
            if(mSocket != null) {
                mSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void setState(STATE state) {
        mState = state;
    }

}
