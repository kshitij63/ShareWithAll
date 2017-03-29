package com.example.android.sharewithall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

/**
 * Created by pussyhunter on 07/02/2017.
 */

public class ServerSendAsyncTask extends AsyncTask {
     WifiCustomAdapter wifiCustomAdapte;
    Socket my_client_socket;
    int scrollstate;
    ProgressDialog mDialog;
    ArrayList<File> file_list;
    ListView mlistview;
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;

    int count = 1;
    Handler handler = new Handler();
    Context context;
    int pos;



    ServerSendAsyncTask(Context context, Socket my_client_socket, ArrayList<File> file_list, ListView mlistview,int scrollstate,WifiP2pManager manager,WifiP2pManager.Channel channel) {
        this.context = context;
        this.my_client_socket = my_client_socket;
        this.file_list = file_list;
        this.mlistview=mlistview;
        this.scrollstate=scrollstate;
        this.channel=channel;
        this.manager=manager;
    }

    @Override
    protected Object doInBackground(Object[] params) {
//Receive.flag=1;
        //int len;
        //byte[] buf = new byte[1024];
        try {
            OutputStream out = my_client_socket.getOutputStream(); //my_client_socket is serversocket
           // ContentResolver cr = context.getContentResolver();
           // InputStream inputStream = null;

            BufferedOutputStream bos = new BufferedOutputStream(out);
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeInt(file_list.size());
            for (File file : file_list) {

    long length = file.length();
    dos.writeLong(length);

    String name = file.getName();
    dos.writeUTF(name);
//                prog.setFile_name(name);

    FileInputStream fis = new FileInputStream(file);
    BufferedInputStream bis = new BufferedInputStream(fis);

    int theByte = 0;
    while ((theByte = bis.read()) != -1) bos.write(theByte);
//                prog.setPro((count * 100) / file_list.size());
    publishProgress((count * 100) / file_list.size());
    count++;
    bis.close();
}


            dos.close();

            out.close();
           //inputStream.close();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "File sent!!", Toast.LENGTH_SHORT).show();
                }
            });


        } catch (final IOException e)

    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "error sending.TRY AGAIN" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

        return null;
}

    @Override
    protected void onProgressUpdate(Object[] values) {
//        wifiCustomAdapte.updateProgress(pos,(int)values[0]);
        Integer val=(Integer)values[0];
        if(scrollstate== AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
            int start=mlistview.getFirstVisiblePosition();
            for(int i=start,j=mlistview.getLastVisiblePosition();i<=j;i++){
                View view=mlistview.getChildAt(i-start);
                ProgressBar bar=(ProgressBar) view.findViewById(R.id.pb1);
                bar.setProgress(val);
                TextView text=(TextView) view.findViewById(R.id.perce);
                text.setText(val +"%"); 
            }
        }

        //devices.getProgressBar().setProgress((int)val);
        //wifiCustomAdapte.notifyDataSetChanged();
        //Toast.makeText(context,"PRO:"+val.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        new AlertDialog.Builder(context)
                .setMessage("Tranfer completed")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity)context).finish();
                    }
                }).show();
       // Receive.flag=0;
        //manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
          //  @Override
            //public void onSuccess() {
              //  Toast.makeText(context,"Group removed successfully",Toast.LENGTH_SHORT).show();
            //}

            //@Override
            //public void onFailure(int reason) {
              //  Toast.makeText(context,"Group removal unsucessfull",Toast.LENGTH_SHORT).show();
            //}
        //});

    }
}







