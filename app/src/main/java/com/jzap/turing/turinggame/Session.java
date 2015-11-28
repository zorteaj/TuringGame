package com.jzap.turing.turinggame;

import android.os.Handler;
import android.util.Log;

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

    protected enum SessionState {COLD, WAITING_FOR_QUESTION, ANSWERING, SENDING_ANSWER, ANSWERED, TERMINATE} // TODO : Keep an eye on whether these get used
    protected SessionState mSessionState;

    protected PlayersManager mPlayersManager;
    protected Handler mHandler;

    protected Socket mSocket = null;
    protected ObjectInputStream mIn = null;
    protected ObjectOutputStream mOut = null;

    protected String mAnswer = null;

    protected Session(PlayersManager manager, Handler handler) {
        mPlayersManager = manager;
        mHandler = handler;
        mSessionState = SessionState.COLD; // TODO : Use this?
    }

    abstract protected void init();

    protected abstract void answerQuestion();

    public void setAnswer(String answer) {
        mAnswer = answer;
        setState(SessionState.SENDING_ANSWER);
    }

/*    protected void listenForAndProcessAnswers() { // TODO : Make this a list of answers for multiple peers case
        Log.i(mTag, "Listening for and processing answers");
        try {
            Message answersMessage = (Message) mIn.readObject();
            Log.i(mTag, "Answer = " + answersMessage.getBody());
            if(answersMessage.getType() == Message.Type.ANSWER) {
                Log.i(mTag, "Answer = " + answersMessage.getBody());
                mPlayersManager.processAnswer(answersMessage);
                //processAnswers(answersMessage.getBody()); // TODO : Handle else
            } else {
                Log.i(mTag, "Not answer type");
            }
        } catch (ClassNotFoundException e) {
            Log.i(mTag, "Class exception");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(mTag, "IO exception");
            e.printStackTrace();
        }
    }*/

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
    }

    abstract protected void castVote();

    protected void sendMessage(Message message) {
        try {
            if(mOut != null) {
                mOut.writeObject(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void sendMessages(List<Message> messages) { // TODO : Test
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

}
