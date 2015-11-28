package com.jzap.turing.turinggame.Session;

import android.util.Log;

import com.jzap.turing.turinggame.Message.Message;
import com.jzap.turing.turinggame.Message.MessageTypes;
import com.jzap.turing.turinggame.Player.PlayersManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class ClientSession extends Session {

    private static final String mTag = "ClientSession";

    private String mGroupOwnerAddress;

    public ClientSession(PlayersManager playersManager, String groupOwnerAddress, SessionMessageHandler handler) {
        super(playersManager, handler);
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
                //Log.i(mTag, "Session state = COLD");
                requestQuestion();
            } else if (mSessionState == SessionState.WAITING_FOR_QUESTION) {
                //Log.i(mTag, "Session state = WAITING FOR QUESTION");
                listenForAndProcessQuestion();
            } else if (mSessionState == SessionState.ANSWERING) {
                //Log.i(mTag, "Session state = ANSWERING");
                if(once) {
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
        List<Message> answerMessages = new ArrayList<>();
        Message answerMessage = new Message(mPlayersManager.getThisPlayer(), Message.Type.ANSWER, mAnswer); // TODO : Consider making answerMessages a member, putting the main code in interface
        answerMessages.add(answerMessage);
        Log.i(mTag, "Sending answer: " + mAnswer);
        sendMessages(answerMessages);
        setState(SessionState.ANSWERED);
    }

    @Override
    protected void castVote() {

    }

    private void processQuestion(String question) {
        mHandler.obtainMessage(MessageTypes.CONTENT_QUESTION, question).sendToTarget();
        setState(SessionState.ANSWERING);
    }

    private void listenForAndProcessQuestion() {
        try {
            Message questionMessage = (Message) mIn.readObject();
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
        Message questionRequestMessage = new Message(mPlayersManager.getThisPlayer(), Message.Type.QUESTION_REQUEST, "");
        sendMessage(questionRequestMessage);
        setState(SessionState.WAITING_FOR_QUESTION);
    }

}
