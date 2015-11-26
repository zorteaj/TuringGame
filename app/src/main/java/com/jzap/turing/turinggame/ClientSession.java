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

        boolean once = true;

        // Synchronously designed protocol
        while (mSessionState != SessionState.TERMINATE) {  //  Todo: Make sure all blocking requests timeout so this condition is hit on termination, instead of app hanging

            if (mSessionState == SessionState.COLD) {
                requestQuestion();
            } else if (mSessionState == SessionState.WAITING_FOR_QUESTION) {
                listenForAndProcessQuestion();
            } else if (mSessionState == SessionState.ANSWERING) {
                if(once) {
                    mHandler.obtainMessage(MessageTypes.CONTROL_ENABLE_ANSWER_BUTTON).sendToTarget();
                    once = false; // TODO : This is a test and in general is quite bad.  For one, button should be disabled afterwards again, adn this once thing is bad design and would need to be reset anyway
                }
            } else if (mSessionState == SessionState.SENDING_ANSWER) {
                answerQuestion();
            } else if (mSessionState == SessionState.ANSWERED) {
                listenForAndProcessAnswers();
                setState(SessionState.TERMINATE); // TODO : Just a test
            }
        }

        end();

    }

    @Override
    protected void init() {
        mSocket = new Socket();
        try {
            if (mSocket != null) {
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
        super.answerQuestion();
    }

    @Override
    protected void castVote() {

    }

    @Override
    protected void sendMessage(Message message) {
        try {
            if(mOut != null) {
                mOut.writeObject(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processQuestion(String question) {
        mHandler.obtainMessage(MessageTypes.CONTENT_QUESTION, question).sendToTarget();
        setState(SessionState.ANSWERING);
    }

    private void listenForAndProcessQuestion() {
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
    }

    private void requestQuestion() {
        Message questionRequestMessage = new Message(Message.Type.QUESTION_REQUEST, "");
        sendMessage(questionRequestMessage);
        setState(SessionState.WAITING_FOR_QUESTION);
    }

}
