package com.jzap.turing.turinggame.UI;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jzap.turing.turinggame.Player.PlayersManager;
import com.jzap.turing.turinggame.R;
import com.jzap.turing.turinggame.Session.Session;
import com.jzap.turing.turinggame.Session.SessionManager;
import com.jzap.turing.turinggame.Session.SessionMessageHandler;
import com.jzap.turing.turinggame.WifiP2p.WifiP2pBroadcastReceiver;

public class MainActivity extends AppCompatActivity implements PeerDisplayActivity {

    private static final String mTag = "MainActivity";

    private WifiP2pManager mManager = null;
    private WifiP2pManager.Channel mChannel = null;
    private WifiP2pBroadcastReceiver mReceiver = null;

    private SessionManager mSessionManager = null;

    private PlayersManager mPlayersManager = null; // TODO : Test

    private SessionMessageHandler mHandler = null;
    private Session mSession = null;

    private LinearLayout mPlayers_LinearLayout;
    private TextView mQuestion_TextView;
    private Button mSubmitAnswer_Button;
    private EditText mAnswer_EditText;

    private boolean init = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        init();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        Log.i(mTag, "On Resume");
        super.onResume();
        if(!init) {
            init();  //TODO : This may simply be a workaround to a bug wherein the activity can be running, when init wasn't called (or at least, broadcast receiver wasn't registered) - or this may be the legit fix...
        }
    }

    @Override
    public void onPause() {
        Log.i(mTag, "On Pause");
        super.onPause();
        cleanUp();
    }

    private void init() {
        if(!init) {
            mHandler = new SessionMessageHandler(this);
            initializeUiViews();
            initializeWifiP2pNetwork();
            mPlayersManager = new PlayersManager(this); // TODO : Test
        }
        init = true;
    }

    public void cleanUp() {
        init = false;
        if(mReceiver != null) {
            try {
                unregisterReceiver(mReceiver); // TODO : Figure how I'm sometimes getting into a state where this is not registered here!!
            } catch(IllegalArgumentException e) {
                e.printStackTrace();
            }
            mReceiver.disconnect();
        }
        if(mSessionManager != null) {
            mSessionManager.terminate();
        }
    }

    private void initializeUiViews() {
        mPlayers_LinearLayout = (LinearLayout) findViewById(R.id.peers_LinearLayout);
        mQuestion_TextView = (TextView) findViewById(R.id.question_TextView);

        mSubmitAnswer_Button = (Button) findViewById(R.id.submitAnswer_Button);
        mSubmitAnswer_Button.setClickable(false);

        mAnswer_EditText = (EditText) findViewById(R.id.answer_EditText);
    }

    private void initializeWifiP2pNetwork() {
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        IntentFilter wifiP2pIntentFilter = new IntentFilter();
        configureIntentFilter(wifiP2pIntentFilter);

        mReceiver = new WifiP2pBroadcastReceiver(mManager, mChannel, this);
        registerReceiver(mReceiver, wifiP2pIntentFilter);
    }

    private void configureIntentFilter(IntentFilter intentFilter) {
        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public void discoverPeersClicked(View v) {
        Log.i(mTag, "Discover Peers Button Clicked");

        if(!init) {
            init(); // TODO : This may simply be a workaround to a bug wherein the activity can be running, when init wasn't called (or at least, broadcast receiver wasn't registered)
        }

        if(mManager != null) {
            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    // Code for when the discovery initiation is successful goes here.
                    // No services have actually been discovered yet, so this method
                    // can often be left blank.  Code for peer discovery goes in the
                    // onReceive method, detailed below.
                    Log.i(mTag, "Discovery Initiation Successful");
                }

                @Override
                public void onFailure(int reasonCode) {
                    // Code for when the discovery initiation fails goes here.
                    // Alert the user that something went wrong.
                    Log.d(mTag, "Discovery initiation Failed.  [Reason Code: " + reasonCode + "]");
                }
            });
        }
    }

    public void beginGameClicked(View v) {
        Log.i(mTag, "Begin Game Button Clicked");
        if(mReceiver != null) {
            mReceiver.connect();
        }
    }

    public void submitAnswerClicked(View v) {
        Log.i(mTag, "Submit Answer Button Clicked");
        if(mSession != null) {
            //mSession.setState(Session.SessionState.ANSWERED);
            mSession.setAnswer(mAnswer_EditText.getText().toString());
        }
    }

    @Override
    public void setQuestion(String question) {
        mQuestion_TextView.setText("Question: " + question); // TODO : There are nicer ways of doing this
    }

    @Override
    public void setSessionManager(SessionManager sessionManager) {
        mSessionManager = sessionManager;
    }

    @Override
    public SessionMessageHandler getSessionMessageHandler() {
        return mHandler;
    }

    @Override
    public Button getSubmitAnswerButton() {
        return mSubmitAnswer_Button;
    }

    @Override
    public void setSession(Session session) {
        mSession = session;
    }

    public Session getSession() {
        return mSession;
    }

    public PlayersManager getPlayersManager() {
        return mPlayersManager;
    }

    public LinearLayout getPlayersLinearLayout() {
        return mPlayers_LinearLayout;
    }

}
