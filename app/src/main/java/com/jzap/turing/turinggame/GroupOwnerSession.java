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
public class GroupOwnerSession extends Session {

    private static final String mTag = "GroupOwnerSession";

    private ServerSocket mServerSocket;

    @Override
    public void run() {

        init();

        while(mState != STATE.TERMINATE) {

            Message message = null;

            try {
                message = (Message) mIn.readObject();
                Log.i(mTag, "Received message: " + message.getBody());
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

}
