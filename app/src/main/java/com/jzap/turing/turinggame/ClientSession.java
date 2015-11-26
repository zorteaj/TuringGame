package com.jzap.turing.turinggame;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class ClientSession extends Session {

    private static final String mTag = "ClientSession";

    private String mGroupOwnerAddress;

    public ClientSession(String groupOwnerAddress, SessionMessageHandler handler) {
        super(handler);
        mGroupOwnerAddress = groupOwnerAddress;
    }

    @Override
    public void run() {

        Log.i(mTag, "Client Session Run");

        init();

        while(mState != STATE.TERMINATE) {

            Message questionRequestMessage = new Message(Message.Type.QUESTION_REQUEST, "Just for test");

            try {
                if(mOut != null) {
                    mOut.writeObject(questionRequestMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Message questionMessage =  (Message) mIn.readObject();
                if(questionMessage.getType() == Message.Type.QUESTION) {
                    processQuestion(questionMessage.getBody());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            setState(STATE.TERMINATE); // TODO : Just a test
        }

        end();

    }

    @Override
    protected void init() {
        mSocket = new Socket();
        try {
            if(mSocket != null) {
                mSocket.connect(new InetSocketAddress(mGroupOwnerAddress, mPort)); // TODO : Add timeout?
                mOut = new ObjectOutputStream(mSocket.getOutputStream());
                mIn = new ObjectInputStream(mSocket.getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void end() {

    }

    @Override
    protected void answerQuestion() {

    }

    @Override
    protected void castVote() {

    }

    @Override
    protected void sendMessage() {

    }

    private void processQuestion(String question) {
        mHandler.obtainMessage(MessageTypes.QUESTION, question).sendToTarget();
    }

}
