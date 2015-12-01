package com.jzap.turing.turinggame.UI;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jzap.turing.turinggame.Player.Player;
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

    private LinearLayout mMain_LinearLayout;
    private LinearLayout mPlayers_LinearLayout;
    private LinearLayout mNameInput_LinearLayout;
    private EditText mPlayerName_EditText;
    private Button mBeginGame_Button;
    private TextView mQuestion_TextView;
    private Button mSubmitAnswer_Button;
    private Button mDiscoverPeers_Button;
    private EditText mAnswer_EditText;
    private ProgressBar mDiscoverPeers_ProgressBar;
    private TextView mDiscoverPeers_TextView;

    private boolean mIsReady = false;
    private boolean mInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        startPeerDiscovery();
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
        if(!mInit) {
            init();  //TODO : This may simply be a workaround to a bug wherein the activity can be running, when mInit wasn't called (or at least, broadcast receiver wasn't registered) - or this may be the legit fix...
        }
    }

    @Override
    public void onPause() {
        Log.i(mTag, "On Pause");
        super.onPause();
        cleanUp();
    }

    private void init() {
        if(!mInit) {
            mHandler = new SessionMessageHandler(this);
            initializeUiViews();
            mPlayersManager = new PlayersManager(this); // TODO : Test
            Player thisPlayer = new Player(mPlayersManager, "You", true);
            initializeWifiP2pNetwork();
        }
        mInit = true;
    }

    public void cleanUp() {
        mInit = false;
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
        mMain_LinearLayout = (LinearLayout) findViewById(R.id.main_LinearLayout);

        mPlayers_LinearLayout = (LinearLayout) findViewById(R.id.peers_LinearLayout);

        mNameInput_LinearLayout = (LinearLayout) findViewById(R.id.nameInput_LinearLayout);

        mPlayerName_EditText = (EditText) findViewById(R.id.nameInput_EditText);
        setupPlayerNameEditText();

        mBeginGame_Button = (Button) findViewById(R.id.beginGame_Button);
        mBeginGame_Button.setEnabled(false);

        mQuestion_TextView = (TextView) findViewById(R.id.question_TextView);

        mSubmitAnswer_Button = (Button) findViewById(R.id.submitAnswer_Button);
        mSubmitAnswer_Button.setEnabled(false);

        mAnswer_EditText = (EditText) findViewById(R.id.answer_EditText);

        mDiscoverPeers_Button = (Button) findViewById(R.id.discoverPeers_button);
        mDiscoverPeers_ProgressBar = (ProgressBar) findViewById(R.id.discoverPeers_ProgressBar);
        mDiscoverPeers_TextView = (TextView) findViewById(R.id.discoverPeers_TextView);
    }

    public void setupPlayerNameEditText() {
        mPlayerName_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    mBeginGame_Button.setEnabled(true);
                } else {
                    mBeginGame_Button.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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
        if(mDiscoverPeers_ProgressBar == null) {
           mDiscoverPeers_ProgressBar = new ProgressBar(this);
            mMain_LinearLayout.addView(mDiscoverPeers_ProgressBar);
        }
        startPeerDiscovery();
    }

    public void startPeerDiscovery() {
        if(!mInit) {
            init(); // TODO : This may simply be a workaround to a bug wherein the activity can be running, when mInit wasn't called (or at least, broadcast receiver wasn't registered)
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
        mPlayersManager.getThisPlayer().setName(mPlayerName_EditText.getText().toString());
        mIsReady = true;
        editUIForGameplay();
    }

    // Changes UI from setup mode to game-play mode (these are loose terms, no such concrete states exist)
    public void editUIForGameplay() {
        if(mNameInput_LinearLayout != null) {
            mPlayerName_EditText.setEnabled(false);
        }
        if(mBeginGame_Button != null) {
            ((ViewGroup) mBeginGame_Button.getParent()).removeView(mBeginGame_Button);
        }
        if(mDiscoverPeers_Button != null) {
            mDiscoverPeers_Button.setEnabled(false);
            ((ViewGroup) mDiscoverPeers_Button.getParent()).removeView(mDiscoverPeers_Button);
        }
    }

    public void submitAnswerClicked(View v) {
        Log.i(mTag, "Submit Answer Button Clicked");
        if(mSession != null && mAnswer_EditText != null) {
            mSession.setAnswer(mAnswer_EditText.getText().toString());
            mAnswer_EditText.setText("");
        }
        hideSoftKeyboard();
        mPlayersManager.hideNames();
        // Check if no view has focus:
    }

    private void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    public boolean isReady() {
        return mIsReady;
    }

    public void removeProgressBar() {

        if(mDiscoverPeers_ProgressBar != null) {
            ((ViewGroup) mDiscoverPeers_ProgressBar.getParent()).removeView(mDiscoverPeers_ProgressBar); // TODO : This can't be good design
            mDiscoverPeers_ProgressBar = null;
        }

        if(mDiscoverPeers_TextView != null) {
            ((ViewGroup) mDiscoverPeers_TextView.getParent()).removeView(mDiscoverPeers_TextView); // TODO : This can't be good design
            mDiscoverPeers_TextView = null;
        }
    }

}
