package com.example.android.sharewithall;

import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pussyhunter on 07/02/2017.
 */

public class WifiCustomAdapter extends ArrayAdapter<Wifi_device> {
    Context context;
    WifiP2pManager manager;
    List<Wifi_device> list;
    List<WifiP2pDevice> list2;
    WifiP2pManager.Channel mchannel;
    //int resource;

    public WifiCustomAdapter(Context context, List<Wifi_device> list,List<WifiP2pDevice> list2, WifiP2pManager.Channel mchannel,WifiP2pManager manager) {

        super(context, 0,list);
        this.context=context;
        this.list=list;
        this.list2=list2;
        this.mchannel=mchannel;
        this.manager=manager;

    }

    @Nullable
    @Override
    public Wifi_device getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v=convertView;
        final Wifi_device device=list.get(position);
        if(v==null) {
            v = LayoutInflater.from(context).inflate(R.layout.custom_adapter, parent, false);
        }

            TextView name=(TextView) v.findViewById(R.id.device);
            Button button=(Button) v.findViewById(R.id.connect_button);
        ProgressBar progressBar=(ProgressBar)v.findViewById(R.id.pb1);
//        TextView percentage=(TextView) v.findViewById(R.id.perce);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Button button1=(Button)v;

                Toast.makeText(context,device.getName(),Toast.LENGTH_SHORT).show();
                final WifiP2pDevice device=list2.get(position);
                WifiP2pConfig config=new WifiP2pConfig();
                config.deviceAddress=device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                config.groupOwnerIntent=15;
                manager.connect(mchannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context,"connected to" +device.deviceAddress,Toast.LENGTH_SHORT).show();

        //                button1.setText("CONNECTED");
          //              button1.setEnabled(false);
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(context,"Failure in Connection to" +device.deviceAddress,Toast.LENGTH_SHORT).show();
                        //button1.setText("CONNECT");
                        //button1.setEnabled(true);
                    }
                });
            }
        });
         name.setText(device.getName());
//        progressBar.setProgress(device.getProgress());
        device.setProgressBar(progressBar);

        return v;
    }

    public void updateProgress(int pos,int pro)
    {
        this.getItem(pos).setProgress(pro);
        this.getItem(pos).setName(""+pro);
    }
}
