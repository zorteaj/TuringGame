package com.jzap.turing.turinggame.Session;

import android.os.Handler;
import android.util.Log;

import com.jzap.turing.turinggame.Message.SessionMessage;
import com.jzap.turing.turinggame.Message.LocalSessionMessageTypes;
import com.jzap.turing.turinggame.Player.AiPlayer;
import com.jzap.turing.turinggame.Player.PlayersManager;
import com.jzap.turing.turinggame.Player.DumbAiPlayer;
import com.jzap.turing.turinggame.UI.MainActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
abstract public class Session implements Runnable {

    private static final String mTag = "Session";

    protected static final int mPort = 8886;

    protected enum SessionState {COLD, READY, WAITING_FOR_QUESTION, ANSWERING, SENDING_ANSWER, ANSWERED, VOTING, WAITING_FOR_VOTES, TERMINATE}
    protected SessionState mSessionState;

    protected PlayersManager mPlayersManager;
    protected Handler mHandler;

    protected AiPlayer mAiPlayer = null;

    protected Socket mSocket = null;
    protected ObjectInputStream mIn = null;
    protected ObjectOutputStream mOut = null;

    protected boolean mAnsweringEnabled = false;
    protected boolean mVotingEnabled = false;

    protected String mAnswer = null;

    protected Session(PlayersManager manager, Handler handler) {
        mPlayersManager = manager;
        mHandler = handler;
        mAiPlayer = new DumbAiPlayer(mPlayersManager);
        mSessionState = SessionState.COLD;
    }

    abstract protected void init();

    protected abstract void answerQuestion();

    public void setAnswer(String answer) {
        mAnswer = answer;
        setState(SessionState.SENDING_ANSWER);
    }

    protected void listenForAndProcessAnswers() {
        try {
            List<SessionMessage> answersSessionMessages = (List<SessionMessage>) mIn.readObject();

            for(int i = 0; i < answersSessionMessages.size(); i++) {
                if (answersSessionMessages.get(i).getType() == SessionMessage.NetType.ANSWER) {
                    mPlayersManager.processAnswer(answersSessionMessages.get(i));
                } else {
                    Log.i(mTag, "Unexpected SessionMessage type");
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setState(SessionState.VOTING);
    }

    public void castVote(String playerId) {
        // Process the vote from this player locally
        processVote(mPlayersManager.getThisPlayer().getId(), playerId);
        // Publish the vote from this player onto the network
        publishVote(playerId);
        setState(SessionState.WAITING_FOR_VOTES);
    }

    protected void publishVote(String playerId) {
        SessionMessage voteSessionMessage = new SessionMessage(mPlayersManager.getThisPlayer(), SessionMessage.NetType.VOTE, playerId);
        sendMessage(voteSessionMessage);
    }

    protected void listenForAndProcessVotes() {
        try {
            SessionMessage voteSessionMessage = (SessionMessage) mIn.readObject();

            if(voteSessionMessage.getType() == SessionMessage.NetType.VOTE) {
                processVote(voteSessionMessage.getPlayerId(), voteSessionMessage.getBody());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void processVote(String voter, String vote) {
        if(vote.equals(mAiPlayer.getId())) {
            mPlayersManager.findPlayerById(voter).addPoint();
        } else {
            mPlayersManager.findPlayerById(voter).guessedWrong();
        }
    }

    protected void sendMessage(SessionMessage sessionMessage) {
        try {
            if(mOut != null) {
                mOut.writeObject(sessionMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void sendMessages(List<SessionMessage> sessionMessages) {
        try {
            if(mOut != null) {
                mOut.writeObject(sessionMessages);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void end() {
        try {
            if(mSocket != null) {
                mSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void setState(SessionState sessionState) {
        mSessionState = sessionState;
    }

    protected void enableAnswering(boolean enable) {
        if(enable) {
            mHandler.obtainMessage(LocalSessionMessageTypes.CONTROL_ENABLE_ANSWER_BUTTON).sendToTarget();
            mAnsweringEnabled = true;
        } else {
            mHandler.obtainMessage(LocalSessionMessageTypes.CONTROL_DISABLE_ANSWER_BUTTON).sendToTarget();
            mAnsweringEnabled = false;
        }
    }

    protected void enableVoting(boolean enable) {
        if(enable) {
            mHandler.obtainMessage(LocalSessionMessageTypes.CONTROL_ENABLE_VOTING).sendToTarget();
            mVotingEnabled = true;
        } else {
            mHandler.obtainMessage(LocalSessionMessageTypes.CONTROL_DISABLE_VOTING).sendToTarget();
            mVotingEnabled = false;
        }
    }

    protected void checkForReadyState() {
        if(mPlayersManager.getActivity().isReady()) {
            setState(SessionState.READY);
        }
    }

    protected void postAnswersLocally() {
        SessionMessage thisPlayersAnswerSessionMessage = new SessionMessage(mPlayersManager.getThisPlayer(), SessionMessage.NetType.ANSWER, mAnswer);
        mPlayersManager.processAnswer((thisPlayersAnswerSessionMessage));
    }

}
