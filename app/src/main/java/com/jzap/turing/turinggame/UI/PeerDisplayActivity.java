package com.jzap.turing.turinggame.UI;

import android.widget.Button;

import com.jzap.turing.turinggame.Session.Session;
import com.jzap.turing.turinggame.Session.SessionManager;
import com.jzap.turing.turinggame.Session.SessionMessageHandler;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
// TODO : Rename to something more descriptive; in fact I might be able to get rid of this altogether
public interface PeerDisplayActivity {
    void setSessionManager(SessionManager sessionManager);
    void setQuestion(String question);
    SessionMessageHandler getSessionMessageHandler();
    Button getSubmitAnswerButton(); // TODO : Ideally this interface would have the button, I think
    void setSession(Session session); // TODO : Same as above
}
