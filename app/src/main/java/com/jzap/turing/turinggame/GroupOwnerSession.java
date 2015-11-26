package com.jzap.turing.turinggame;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

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

    GroupOwnerSession(MainActivity activity, Handler handler) {
        super(activity, handler); // TODO : Only for test
    }

    private static final String mTag = "GroupOwnerSession";

    private ServerSocket mServerSocket;

    @Override
    public void run() {

        init();

        while(mState != STATE.TERMINATE) {

          //  final Message message = null;

            try {
                if(mSocket.isConnected()) { // TODO : Not so sure about this
                    final Message message = (Message) mIn.readObject();
                    Log.i(mTag, "Received message: " + message.getBody());

                    mHandler.obtainMessage(MessageTypes.QUESTION, message.getBody()).sendToTarget();

                    // TODO : Only for testing
                    mMainActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(mMainActivity.getBaseContext(), message.getBody(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });


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

}
