package com.jzap.turing.turinggame.Session;

import android.os.Handler;
import android.util.Log;

import com.jzap.turing.turinggame.Message.SessionMessage;
import com.jzap.turing.turinggame.Message.LocalSessionMessageTypes;
import com.jzap.turing.turinggame.NLP.DumbQuestionGenerator;
import com.jzap.turing.turinggame.NLP.QuestionGenerator;
import com.jzap.turing.turinggame.Player.PlayersManager;

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

    private ServerSocket mServerSocket;

    private QuestionGenerator mQuestionGenerator;

    public GroupOwnerSession(PlayersManager playersManager, Handler handler) {
        super(playersManager, handler);
        mQuestionGenerator = new DumbQuestionGenerator();
    }

    @Override
    public void run() {

        Log.i(mTag, "Owner Session Run");

        init();

        // Synchronously designed protocol
        while (mSessionState != SessionState.TERMINATE) {
            switch(mSessionState) {
                case COLD:
                    checkForReadyState();
                    break;
                case READY:
                    //Log.i(mTag, "READY");
                    listenForAndProcessQuestionRequest();
                    break;
                case ANSWERING:
                   // Log.i(mTag, "ANSWERING");
                    if (!mAnsweringEnabled) {
                       enableAnswering(true);
                    }
                    break;
                case SENDING_ANSWER:
                   // Log.i(mTag, "SENDING_ANSWER");
                   enableAnswering(false);
                    answerQuestion();
                    break;
                case ANSWERED:
                    //Log.i(mTag, "ANSWERED");
                    listenForAndProcessAnswers();
                    postAnswersLocally();
                case VOTING:
                   // Log.i(mTag, "VOTING");
                    if(!mVotingEnabled) {
                        enableVoting(true);
                    }
                    break;
                case WAITING_FOR_VOTES:
                    //Log.i(mTag, "WAITING_FOR_VOTES");
                    enableVoting(false);
                    listenForAndProcessVotes();
                    Log.i(mTag, "Setting to cold");
                    setState(SessionState.READY);
                    break;
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
        if(mAiPlayer == null) {
            return;
        }

        List<SessionMessage> answerSessionMessages = new ArrayList<>();

        SessionMessage playerAnswerSessionMessage = new SessionMessage(mPlayersManager.getThisPlayer(), SessionMessage.NetType.ANSWER, mAnswer);
        SessionMessage aiAnswerSessionMessage = mAiPlayer.answerQuestion();

        answerSessionMessages.add(playerAnswerSessionMessage);
        answerSessionMessages.add(aiAnswerSessionMessage);

        sendMessages(answerSessionMessages);
        setState(SessionState.ANSWERED);
    }

    @Override
    protected void listenForAndProcessAnswers() {
        super.listenForAndProcessAnswers();
        if(mAiPlayer != null) {
            mAiPlayer.postAnswerLocally();
        }
    }

    private void listenForAndProcessQuestionRequest() {
        SessionMessage questionSessionMessage = null;

        if (mIn != null) {
            try {
                questionSessionMessage = (SessionMessage) mIn.readObject();
                if (questionSessionMessage.getType() == SessionMessage.NetType.QUESTION_REQUEST) {
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
        mHandler.obtainMessage(LocalSessionMessageTypes.CONTENT_QUESTION, question).sendToTarget();

        // Create question SessionMessage and publish to peer device(s)
        SessionMessage questionSessionMessage = new SessionMessage(mPlayersManager.getThisPlayer(), SessionMessage.NetType.QUESTION, question);
        sendMessage(questionSessionMessage);

        setState(SessionState.ANSWERING);
    }

}
