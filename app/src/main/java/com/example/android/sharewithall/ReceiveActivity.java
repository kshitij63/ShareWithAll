package com.example.android.sharewithall;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ReceiveActivity extends AppCompatActivity {
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    private WifiP2pManager.ConnectionInfoListener mylistener;
    private WifiBroadcastReceiver mReceiver;
    private IntentFilter wififilter;
    private  TextView status;
    private LinearLayout mlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disconnect();
        //oGroup_remove();
        setContentView(R.layout.activity_receive);
        mlayout=(LinearLayout) findViewById(R.id.lay1);
        status=(TextView) findViewById(R.id.usage);
     //   blink();
        mlayout.setVisibility(View.INVISIBLE);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        wififilter = new IntentFilter();
        wififilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wififilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wififilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wififilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(ReceiveActivity.this, "Ready to connect", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(ReceiveActivity.this, "Failed to initiate connection", Toast.LENGTH_SHORT).show();
            }
        });

        mylistener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                String r = info.groupOwnerAddress.getHostAddress();
                String name=info.groupOwnerAddress.getHostName();
                if (info.groupFormed && info.isGroupOwner) {
                    //Toast.makeText(ReceiveActivity.this, "Connected" + r, Toast.LENGTH_SHORT).show();
//                    ServerAsyncTask serverAsyncTask = new ServerAsyncTask(MainActivity.this, main_socket, socket_list);
//                    Toast.makeText(MainActivity.this, "your IP is" + serverAsyncTask.getIPAddress(), Toast.LENGTH_SHORT).show();
//                    serverAsyncTask.execute();
                } else if (info.groupFormed) {
//                    ServerAsyncTask serverAsyncTask = new ServerAsyncTask(MainActivity.this, main_socket, socket_list);
                    Toast.makeText(ReceiveActivity.this, "Connected to " +name , Toast.LENGTH_SHORT).show();
                    Receive receive = new Receive(r, 8080, ReceiveActivity.this,status,mlayout,mManager,mChannel);
//                    Toast.makeText(MainActivity.this, "your IP is" + serverAsyncTask.getIPAddress(), Toast.LENGTH_SHORT).show();
                    receive.execute();

                }
            }

        };

        TextView home=(TextView) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ReceiveActivity.this,HomeScreenActivity.class);
                startActivity(intent);
                disconnect();
         //       Group_remove();
                finish();
            }
        });

        TextView Gallery=(TextView ) findViewById(R.id.gal_but);
        Gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri=Uri.parse(Environment.getExternalStorageDirectory() + "/"
                        + "SHAREWITHALL" + "/wifip2pshared");
                intent.setDataAndType(uri,"*/*");
                startActivity(Intent.createChooser(intent,"open"));
                disconnect();
       //         Group_remove();
                finish();
            }
        });



    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this,"Transfer Cancelled",Toast.LENGTH_SHORT).show();
     //   Receive.flag=1;
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new WifiBroadcastReceiver(mManager, mChannel, mylistener);
        registerReceiver(mReceiver, wififilter);
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //Toast.makeText(ReceiveActivity.this, "Successfully discovered peers", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(ReceiveActivity.this, "Failure in discovered peers", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void disconnect() {

        if (mManager != null && mChannel != null) {
            mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null && mManager != null && mChannel != null
                            && group.isGroupOwner()) {
                        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                Toast.makeText(ReceiveActivity.this, "Ready to connect", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int reason) {
                                Toast.makeText(ReceiveActivity.this, "Failed to initiate connection", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    private void blink(){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 1000;    //in milissegunds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        status = (TextView) findViewById(R.id.usage);
                        if(status.getVisibility() == View.VISIBLE){
                            status.setVisibility(View.INVISIBLE);
                        }else{
                            status.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        }).start();
    }
    /*private void Group_remove(){
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(ReceiveActivity.this,"Groups removed succesfully",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(ReceiveActivity.this,"Groups removal Failed",Toast.LENGTH_SHORT).show();

            }
        });
    }*/


}
