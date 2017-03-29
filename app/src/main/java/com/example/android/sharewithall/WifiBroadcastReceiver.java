package com.example.android.sharewithall;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import java.nio.channels.Channel;
import java.util.List;

/**
 * Created by pussyhunter on 02/02/2017.
 */

public class WifiBroadcastReceiver extends BroadcastReceiver {
    MainActivity activity;
    WifiP2pManager mManager;
    WifiP2pManager.Channel channel;
    WifiP2pManager.PeerListListener mlistener;
    WifiP2pManager.ConnectionInfoListener mylistener;


    WifiBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiP2pManager.PeerListListener mlistener, MainActivity activity, WifiP2pManager.ConnectionInfoListener mylistener) {
        super();
        mManager = manager;
        this.channel = channel;
        this.mlistener = mlistener;
        this.activity = activity;
        this.mylistener = mylistener;
    }
    WifiBroadcastReceiver(WifiP2pManager manager,WifiP2pManager.Channel channel,WifiP2pManager.ConnectionInfoListener list){
        this.channel=channel;
        mManager=manager;
        mylistener=list;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //activity = (MainActivity) context;
        String data = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(data)) {
            //       Toast.makeText(context,"state changed",Toast.LENGTH_SHORT).show();
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Toast.makeText(context, "WIFI DIRECT ENABLED", Toast.LENGTH_SHORT).show();

            }

                //Toast.makeText(context, "state enabled", Toast.LENGTH_SHORT).show();
            //else
              //  Toast.makeText(context, "state disabled", Toast.LENGTH_SHORT).show();

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(data)) {
            if (mManager != null) {
                //Toast.makeText(context, "peers requested", Toast.LENGTH_SHORT).show();

                mManager.requestPeers(channel, mlistener);
            } else {
                //Toast.makeText(context, "manager null", Toast.LENGTH_SHORT).show();
            }
            // Toast.makeText(context,"peers changed",Toast.LENGTH_SHORT).show();


        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(data)) {
            //Toast.makeText(context, "connection changed", Toast.LENGTH_SHORT).show();
            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // We are connected with the other device, request connection
                // info to find group owner IP

                mManager.requestConnectionInfo(channel, mylistener);

            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(data)) {
              //  Toast.makeText(context, "this device changed", Toast.LENGTH_SHORT).show();

            }

        }
    }
}
