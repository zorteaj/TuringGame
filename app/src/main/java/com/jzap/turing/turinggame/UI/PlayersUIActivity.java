package com.jzap.turing.turinggame.UI;

import android.widget.Button;
import android.widget.LinearLayout;

import com.jzap.turing.turinggame.Player.PlayersManager;
import com.jzap.turing.turinggame.Session.Session;
import com.jzap.turing.turinggame.Session.SessionManager;
import com.jzap.turing.turinggame.Session.SessionMessageHandler;

/**
 * Created by JZ_W541 on 11/25/2015.
 */
public interface PlayersUIActivity {
    void setSessionManager(SessionManager sessionManager);
    void setQuestion(String question);
    SessionMessageHandler getSessionMessageHandler();
    Button getSubmitAnswerButton();
    void setSession(Session session);
    LinearLayout getPlayersLinearLayout();
    boolean isReady();
    PlayersManager getPlayersManager();
}
