package com.jzap.turing.turinggame.Session;

import android.os.Handler;
import android.util.Log;

import com.jzap.turing.turinggame.Message.Message;
import com.jzap.turing.turinggame.Message.MessageTypes;
import com.jzap.turing.turinggame.Player.PlayersManager;
import com.jzap.turing.turinggame.Player.AiPlayer;
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

    protected static final int mPort = 8886; // TODO : Is this number okay?

    protected enum SessionState {COLD, READY, WAITING_FOR_QUESTION, ANSWERING, SENDING_ANSWER, ANSWERED, VOTING, WAITING_FOR_VOTES, TERMINATE} // TODO : Keep an eye on whether these get used
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
        mAiPlayer = new AiPlayer(mPlayersManager);
        mSessionState = SessionState.COLD; // TODO : Use this?
    }

    abstract protected void init();

    protected abstract void answerQuestion();

    public void setAnswer(String answer) {
        mAnswer = answer;
        setState(SessionState.SENDING_ANSWER);
    }

    protected void listenForAndProcessAnswers() { // TODO : Make this a list of answers for multiple peers case // TODO : TEST
        Log.i(mTag, "Listening for and processing answers");
        try {
            List<Message> answersMessages = (List<Message>) mIn.readObject();

            for(int i = 0; i < answersMessages.size(); i++) {
                if (answersMessages.get(i).getType() == Message.Type.ANSWER) {
                    Log.i(mTag, "Answer = " + answersMessages.get(i).getBody());
                    mPlayersManager.processAnswer(answersMessages.get(i));
                    //processAnswers(answersMessage.getBody()); // TODO : Handle else
                } else {
                    Log.i(mTag, "Not answer type");
                }
            }
        } catch (ClassNotFoundException e) {
            Log.i(mTag, "Class exception");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(mTag, "IO exception");
            e.printStackTrace();
        }
        setState(SessionState.VOTING);
    }

    public void castVote(String playerId) {
        Log.i(mTag, "Voted for " + playerId); // TODO : Actually send this
        // Process the vote from this player locally
        processVote(mPlayersManager.getThisPlayer().getId(), playerId);
        // Publish the vote from this player onto the network
        publishVote(playerId);
        setState(SessionState.WAITING_FOR_VOTES);
         // TODO : **** I THINK THAT WITH SOME BAD TIMING THIS IS A CRITICAL PROBLEM *****
    }

    protected void publishVote(String playerId) {
        Message voteMessage = new Message(mPlayersManager.getThisPlayer(), Message.Type.VOTE, playerId);
        sendMessage(voteMessage);
    }

    protected void listenForAndProcessVotes() {
        try {
            Message voteMessage = (Message) mIn.readObject();

            if(voteMessage.getType() == Message.Type.VOTE) {
                processVote(voteMessage.getPlayerId(), voteMessage.getBody());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void processVote(String voter, String vote) {
        Log.i(mTag, "Processing Vote");
        if(vote.equals(mAiPlayer.getId())) {
            mPlayersManager.findPlayerById(voter).addPoint();
        } else {
            mPlayersManager.findPlayerById(voter).guessedWrong();
        }
    }

    protected void sendMessage(Message message) {
        try {
            if(mOut != null) {
                mOut.writeObject(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void sendMessages(List<Message> messages) {
        try {
            if(mOut != null) {
                mOut.writeObject(messages);
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
            mHandler.obtainMessage(MessageTypes.CONTROL_ENABLE_ANSWER_BUTTON).sendToTarget();
            mAnsweringEnabled = true;
        } else {
            mHandler.obtainMessage(MessageTypes.CONTROL_DISABLE_ANSWER_BUTTON).sendToTarget();
            mAnsweringEnabled = false;
        }
    }

    protected void enableVoting(boolean enable) {
        if(enable) {
            mHandler.obtainMessage(MessageTypes.CONTROL_ENABLE_VOTING).sendToTarget();
            mVotingEnabled = true;
        } else {
            mHandler.obtainMessage(MessageTypes.CONTROL_DISABLE_VOTING).sendToTarget();
            mVotingEnabled = false;
        }
    }

    protected void checkForReadyState() {
        if(((MainActivity) mPlayersManager.getActivity()).isReady()) { // TODO : Bad design (cast)
            setState(SessionState.READY);
        }
    }

    protected void postAnswersLocally(List<Message> messages) {
        for(int i = 0; i < messages.size(); i++) {
            mPlayersManager.processAnswer(messages.get(i));
        }
    }

}
