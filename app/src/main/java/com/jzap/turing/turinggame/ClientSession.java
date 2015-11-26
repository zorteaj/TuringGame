package com.jzap.turing.turinggame;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
public class ClientSession extends Session {

    private static final String mTag = "ClientSession";

    private String mGroupOwnerAddress;

    ClientSession(String groupOwnerAddress, SessionMessageHandler handler) {
        super(handler);
        mGroupOwnerAddress = groupOwnerAddress;
    }

    @Override
    public void run() {

        Log.i(mTag, "Before init...");
        init();
        Log.i(mTag, "After init...");

        Log.i(mTag, "Entering loop...");

        while(mState != STATE.TERMINATE) {

            Message message = new Message("Connection", "This the body");

            try {
                if(mOut != null) {
                    Log.i(mTag, "Sending message...");
                    mOut.writeObject(message);
                } else {
                    Log.i(mTag, "mOut is null...");
                }
            } catch (IOException e) {
                Log.i(mTag, "IO Exception");
                e.printStackTrace();
            }

            Log.i(mTag, "Sent message to server");

            setState(STATE.TERMINATE); // TODO : Just a test
        }

        end();

    }


    @Override
    protected void init() {
        Log.i(mTag, "Init...");
        mSocket = new Socket();
        try {
            if(mSocket != null) {
                mSocket.connect(new InetSocketAddress(mGroupOwnerAddress, mPort)); // TODO : Add timeout?
            }
            //mIn = new ObjectInputStream(mSocket.getInputStream());
            mOut = new ObjectOutputStream(mSocket.getOutputStream());
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

}
