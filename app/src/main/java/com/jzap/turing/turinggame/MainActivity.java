package com.jzap.turing.turinggame;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PeerDisplayActivity {

    private static final String mTag = "MainActivity";

    private WifiP2pManager mManager = null;
    private WifiP2pManager.Channel mChannel = null;
    private WifiP2pBroadcastReceiver mReceiver = null;

    private List mPeers = new ArrayList();

    private LinearLayout mPeers_LinearLayout;

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

        initializeUiViews();
        initializeWifiP2pNetwork();
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
    }

    @Override
    public void onPause() {
        Log.i(mTag, "On Pause");
        super.onPause();
        if(mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    private void initializeUiViews() {
        mPeers_LinearLayout = (LinearLayout) findViewById(R.id.peers_LinearLayout);
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
    }

    public void submitAnswerClicked(View v) {
        Log.i(mTag, "Submit Answer Button Clicked");
    }

    @Override
    public void setPeers(List peers) {

        mPeers_LinearLayout.removeAllViews();

        for(int i = 0; i < peers.size(); i++) {
            TextView peerDeviceName_TextView = new TextView(this);
            WifiP2pDevice device = (WifiP2pDevice) peers.get(i);
            peerDeviceName_TextView.setText(device.deviceName);
            peerDeviceName_TextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            mPeers_LinearLayout.addView(peerDeviceName_TextView);
        }
    }
}
