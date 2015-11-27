package com.jzap.turing.turinggame;

/**
 * Created by JZ_W541 on 11/27/2015.
 */

// Structure for passing answers and deviceAddress of answerer to PeerDisplayActivity from Session
public class Answer {

    private String mAnswer;
    private String mDeviceAddress;

    public Answer(String answer, String deviceAddress) {
        mAnswer = answer;
        mDeviceAddress = deviceAddress;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public String getDeviceAddress() {
        return mDeviceAddress;
    }

}
