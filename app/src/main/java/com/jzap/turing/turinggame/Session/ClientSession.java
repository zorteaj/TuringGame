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

        // Synchronously designed protocol
        while (mSessionState != SessionState.TERMINATE) {  //  Todo: Make sure all blocking requests timeout so this condition is hit on termination, instead of app hanging
            switch(mSessionState) {
                case COLD:
                    checkForReadyState();
                    break;
                case READY:
                    //Log.i(mTag, "READY");
                    requestQuestion();
                    break;
                case WAITING_FOR_QUESTION:
                    //Log.i(mTag, "WAITING_FOR_QUESTION");
                    listenForAndProcessQuestion();
                    break;
                case ANSWERING:
                    //Log.i(mTag, "ANSWERING");
                    if(!mAnsweringEnabled) {
                        enableAnswering(true);
                    }
                    break;
                case SENDING_ANSWER:
                    //Log.i(mTag, "SENDING_ANSWER");
                    enableAnswering(false);
                    answerQuestion();
                    break;
                case ANSWERED:
                    //Log.i(mTag, "ANSWERED");
                    listenForAndProcessAnswers();
                    postAnswersLocally();
                    break;
                case VOTING:
                    //Log.i(mTag, "VOTING");
                    if(!mVotingEnabled) {
                        enableVoting(true);
                    }
                    break;
                case WAITING_FOR_VOTES:
                    //Log.i(mTag, "WAITING_FOR_VOTES");
                    enableVoting(false);
                    listenForAndProcessVotes();
                    setState(SessionState.READY);
                    break;
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

        sendMessages(answerMessages);
        setState(SessionState.ANSWERED);
    }

    private void processQuestion(String question) {
        mHandler.obtainMessage(MessageTypes.CONTENT_QUESTION, question).sendToTarget();
        setState(SessionState.ANSWERING);
    }

    private void listenForAndProcessQuestion() { // TODO : Commonize these listenForAndProcess methods
        Log.i(mTag, "listening for a processing question");
        try {
            if(mIn != null) {
                Message questionMessage = (Message) mIn.readObject();
                if (questionMessage.getType() == Message.Type.QUESTION) {
                    processQuestion(questionMessage.getBody());
                } else {
                }
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
