package com.jzap.turing.turinggame;

import java.util.List;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
// TODO : Rename to something more descriptive
public interface PeerDisplayActivity {
    void setPeers(List peers);
    void setSessionManager(WifiP2pSessionManager sessionManager);
    void setQuestion(String question);
    SessionMessageHandler getSessionMessageHandler();
}
