package com.jzap.turing.turinggame;

import android.os.Handler;
import android.util.Log;

import com.jzap.turing.turinggame.NLP.QuestionGenerator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;


/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class GroupOwnerSession extends Session {

    private static final String mTag = "GroupOwnerSession";

    private ServerSocket mServerSocket = null;
    private QuestionGenerator mQuestionGenerator = null;

    public GroupOwnerSession(Handler handler) {
        super(handler); // TODO : Only for test
        mQuestionGenerator = new QuestionGenerator();
    }

    @Override
    public void run() {

        Log.i(mTag, "Owner Run");

        init();

        while(mState != STATE.TERMINATE) {

            Message message = null;

            try {
                if(mSocket.isConnected()) { // TODO : Not so sure about this check

                    message = (Message) mIn.readObject();
                    Message.Type messageType = message.getType();

                    mHandler.obtainMessage(MessageTypes.QUESTION, message.getBody()).sendToTarget(); // TODO : Just for testing

                    Log.i(mTag, "Entering Owner switch");

                    switch(messageType) {
                        case QUESTION_REQUEST :
                                Log.i(mTag, "QUESTION REQUEST RECEIVED");
                                processQuestionRequest();
                            break;
                        case ANSWER : processAnswer(message);
                            break;
                    } // TODO : Add all types
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        end();

    }

    @Override
    protected void init() {
        try {
            mServerSocket = new ServerSocket(mPort);
            mSocket = mServerSocket.accept(); // This can be extended for multiple clients
            mIn = new ObjectInputStream(mSocket.getInputStream());
            mOut = new ObjectOutputStream(mSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void end() {
        super.end();
        try {
            if(mServerSocket != null) {
                mServerSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void processQuestionRequest() {
        Log.i(mTag, "Processing Question Request");
        if(mQuestionGenerator == null) {
            return;
        }
        String question = mQuestionGenerator.fetchQuestion();
        publishQuestion(question);
    }

    private void publishQuestion(String question) {
        // Display on this device
        mHandler.obtainMessage(MessageTypes.QUESTION, question).sendToTarget();

        // Create question Message and publish to peer device(s)
        Message questionMessage = new Message(Message.Type.QUESTION, question);
        try {
            mOut.writeObject(questionMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processAnswer(Message message) {
        // For inter-thread communication, so question can be displayed on UI
        mHandler.obtainMessage(MessageTypes.ANSWER, message.getBody()).sendToTarget();
    }

}
