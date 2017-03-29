package com.example.android.sharewithall;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private WifiBroadcastReceiver mReceiver;
    public ArrayList<Socket> socket_list;
    private Button chooser;
    WifiP2pManager.ActionListener myActionlistener;
    private Button end;
    ArrayList<File> files;
    int mscrollstate= AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private IntentFilter wififilter;
    private ServerSocket main_socket;
    private WifiP2pManager mManager;

    private WifiP2pManager.Channel mChannel;
    private List<WifiP2pDevice> peerList;
    private static int SERVER_PORT=8080;
    private WifiP2pManager.PeerListListener mListener;
    private Button b1;
    private static final int FILE_CODE=89;
    private static final int DIALOUGE_BOX=9;
    private Wifi_device my_device;
    private ListView prog_listview;
    private static String FILES_TO_UPLOAD="upload";
    private ArrayList<Wifi_device> my_device_list;
    private ListView mlistview;
    private ProgressDialog pdiag;
    private WifiCustomAdapter mdapater;
    private WifiP2pManager.ConnectionInfoListener mylistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);disconnect();
        //Group_remove();


        try {
            main_socket = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            Toast.makeText(this, "ERROR" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        peerList = new ArrayList<>();

        socket_list = new ArrayList<>();
        my_device_list = new ArrayList<>();
        b1 = (Button) findViewById(R.id.searchforpeer);
        mlistview = (ListView) findViewById(R.id.list1);
        chooser = (Button) findViewById(R.id.chooser);
        chooser.setVisibility(View.INVISIBLE);


        wififilter = new IntentFilter();
        wififilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wififilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wififilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wififilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dicoverPeer();
            }
        });

        chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FileSelectionActivity.class);
                startActivityForResult(intent, FILE_CODE);
            }
        });

        getlistofPeerOnscreen();
        end=(Button) findViewById(R.id.client);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                socket_list.clear();
                my_device_list.clear();
                peerList.clear();
                disconnect();
        //        Group_remove();
                chooser.setVisibility(View.INVISIBLE);
            }
        });

        mylistener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                String r = info.groupOwnerAddress.getHostAddress();
                if (info.groupFormed && info.isGroupOwner) {
                    Toast.makeText(MainActivity.this, "Connected" , Toast.LENGTH_SHORT).show();
                    ServerAsyncTask serverAsyncTask = new ServerAsyncTask(MainActivity.this, main_socket, socket_list);
                    //Toast.makeText(MainActivity.this, "your IP is" + serverAsyncTask.getIPAddress(), Toast.LENGTH_SHORT).show();
                    serverAsyncTask.execute();
                    chooser.setVisibility(View.VISIBLE);


                } else if (info.groupFormed) {
                    //ServerAsyncTask serverAsyncTask = new ServerAsyncTask(MainActivity.this, main_socket, socket_list);
                    //Toast.makeText(MainActivity.this, "you are client.Group_owner IP=" + r, Toast.LENGTH_SHORT).show();
                    //Receive receive = new Receive(r, serverAsyncTask.SERVER_PORT, MainActivity.this);
                    //Toast.makeText(MainActivity.this, "your IP is" + serverAsyncTask.getIPAddress(), Toast.LENGTH_SHORT).show();
                    //receive.execute();
                }
            }

        };

        mlistview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mscrollstate=scrollState;
                //Toast.makeText(MainActivity.this, "Scroll stae changed", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Toast.makeText(MainActivity.this, "Scrolled", Toast.LENGTH_SHORT).show();

            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
      //  Receive.flag=1;
        Toast.makeText(this,"Transfer cancelled",Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FILE_CODE && resultCode == RESULT_OK){
             files = (ArrayList<File>) data.getSerializableExtra(FILES_TO_UPLOAD);

            for(int i=0;i<socket_list.size();i++) {
                Socket socket=socket_list.get(i);
//                ServerSendAsyncTask mytask = new ServerSendAsyncTask(MainActivity.this,socket,files,mdapater,i);
                ServerSendAsyncTask mytask = new ServerSendAsyncTask(MainActivity.this,socket,files,mlistview,mscrollstate,mManager,mChannel);
                mytask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new WifiBroadcastReceiver(mManager, mChannel, mListener, this,mylistener);
        registerReceiver(mReceiver, wififilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        disconnect();
      //  Group_remove();
        //unregisterReceiver(mReceiver);
        try {
            main_socket.close();
        } catch (IOException e) {
           // Toast.makeText(MainActivity.this,"Server closed",Toast.LENGTH_SHORT).show();
        }
    }

    private void dicoverPeer() {


        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener()
        {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Searching....", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Search Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getlistofPeerOnscreen() {
        mListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                peerList.clear();
                if(mdapater!=null){
                    mdapater.notifyDataSetChanged();
                }
                    peerList.addAll(peers.getDeviceList());
                    getdeviceName();
                    mdapater=new WifiCustomAdapter(MainActivity.this,my_device_list,peerList,mChannel,mManager);
                    mlistview.setAdapter(mdapater);
                    //Toast.makeText(MainActivity.this, "peers added and deivice name too", Toast.LENGTH_SHORT).show();
                    if (peerList.size() == 0)
                        Toast.makeText(MainActivity.this, "NO devices found", Toast.LENGTH_SHORT).show();
            }
        };
    }


    public void getdeviceName()
    {
        my_device_list.clear();
        for (int i = 0; i < peerList.size(); i++)
        {
            my_device=new Wifi_device(peerList.get(i).deviceName,"client/server");
            my_device_list.add(my_device);
        }
    }


    public  void disconnect() {

        if ( mManager!= null && mChannel != null){
            mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null && mManager != null && mChannel != null
                            && group.isGroupOwner()) {
                        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int reason) {
                                Toast.makeText(MainActivity.this, "Failure!!try again", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            });
        }
    }
//    private void Group_remove(){
  //      mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
    //        @Override
        //    public void onSuccess() {
      //          Toast.makeText(MainActivity.this,"Groups removed succesfully",Toast.LENGTH_SHORT).show();
          //  }

            //@Override
            //public void onFailure(int reason) {
              //  Toast.makeText(MainActivity.this,"Groups removal Failed",Toast.LENGTH_SHORT).show();

            //}
     //   });
    //}
}






