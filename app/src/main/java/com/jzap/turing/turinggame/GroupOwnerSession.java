package com.jzap.turing.turinggame;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Handler;
import android.util.Log;

import com.jzap.turing.turinggame.NLP.QuestionGenerator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class GroupOwnerSession extends Session {

    private static final String mTag = "GroupOwnerSession";

    private ServerSocket mServerSocket = null;
    private QuestionGenerator mQuestionGenerator = null;

    public GroupOwnerSession(PlayersManager playersManager, Handler handler) {
        super(playersManager, handler); // TODO : Only for test
        mQuestionGenerator = new QuestionGenerator();
    }

    @Override
    public void run() {

        Log.i(mTag, "Owner Session Run");

        init();

        boolean once = true;

        while (mSessionState != SessionState.TERMINATE) {

            if (mSessionState == SessionState.COLD) {
                //Log.i(mTag, "Session state = COLD");
                listenForAndProcessQuestionRequest();
            } else if (mSessionState == SessionState.ANSWERING) {
                //Log.i(mTag, "Session state = ANSWERING");
                if (once) {
                    mHandler.obtainMessage(MessageTypes.CONTROL_ENABLE_ANSWER_BUTTON).sendToTarget();
                    once = false; // TODO : This is a test and in general is quite bad.  For one, button should be disabled afterwards again, adn this once thing is bad design and would need to be reset anyway
                }
            } else if (mSessionState == SessionState.SENDING_ANSWER) {
                //Log.i(mTag, "Session state = SENDING_ANSWER");
                answerQuestion();
            } else if (mSessionState == SessionState.ANSWERED) {
                //Log.i(mTag, "Session state = ANSWERED");
                listenForAndProcessAnswers();
                setState(SessionState.COLD); // TODO : Just a test
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
            if (mServerSocket != null) {
                mServerSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void answerQuestion() {

        // TODO AI

        List<Message> answerMessages = new ArrayList<>();

        Message playerAnswerMessage = new Message(mPlayersManager.getThisPlayer(), Message.Type.ANSWER, mAnswer);
        Message aiAnswerMessage = new Message("AI", Message.Type.ANSWER, "AIs answer"); // TODO : Just a test

        answerMessages.add(playerAnswerMessage);
        answerMessages.add(aiAnswerMessage);

        Log.i(mTag, "Sending answer: " + mAnswer);
        sendMessages(answerMessages);
        setState(SessionState.ANSWERED);

    }

    @Override
    protected void castVote() {

    }

    private void listenForAndProcessQuestionRequest() {
        Message questionMessage = null;

        if (mIn != null) {
            try {
                questionMessage = (Message) mIn.readObject();
                if (questionMessage.getType() == Message.Type.QUESTION_REQUEST) {
                    processQuestionRequest();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processQuestionRequest() {
        Log.i(mTag, "Processing Question Request");
        if (mQuestionGenerator == null) {
            return;
        }
        String question = mQuestionGenerator.fetchQuestion();
        publishQuestion(question);
    }

    private void publishQuestion(String question) {
        // Display on this device
        mHandler.obtainMessage(MessageTypes.CONTENT_QUESTION, question).sendToTarget();

        // Create question Message and publish to peer device(s)
        Message questionMessage = new Message(mPlayersManager.getThisPlayer(), Message.Type.QUESTION, question);
        sendMessage(questionMessage);

        setState(SessionState.ANSWERING);
    }

}
