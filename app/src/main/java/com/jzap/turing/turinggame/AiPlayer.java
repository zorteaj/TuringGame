package com.jzap.turing.turinggame;

import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Created by JZ_W541 on 11/28/2015.
 */
public class AiPlayer extends Player {

    AiPlayer(WifiP2pDevice device, PlayersManager playersManager) {
        super(device, playersManager, false);
    }
}
