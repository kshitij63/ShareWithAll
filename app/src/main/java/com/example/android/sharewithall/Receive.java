package com.example.android.sharewithall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by pussyhunter on 04/02/2017.
 */

public class Receive extends AsyncTask {
    Socket socket;
    Handler handler = new Handler();
//    static int flag;
    String IP;
    int count=0;
    TextView status;
    int filesCount;
    int port;
    int flag;
    Context context;
    ProgressDialog  mDialog;
    LinearLayout layout;
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;


    //Receive(String IP, int port, Context context) {
        //this.IP = IP;
      //  this.port = port;
       // this.context = context;
        //this.status=status;
    //}


    Receive(String IP, int port, Context context, TextView status, LinearLayout layout,WifiP2pManager manager,WifiP2pManager.Channel channel) {
        this.IP = IP;
        this.port = port;
        this.context = context;
        this.status=status;
        this.layout=layout;
        this.channel=channel;
        this.manager=manager;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(Looper.myLooper()==null){
            Looper.prepare();
        }
        mDialog=new ProgressDialog(context);
        mDialog.setMessage("Receiving file...");
        mDialog.setIndeterminate(false);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setCancelable(true);
        mDialog.show(); 

    }

    @Override
    protected Object doInBackground(Object[] params) {
        //flag=1;

        String Path=Environment.getExternalStorageDirectory() + "/"
                + "SHAREWITHALL" + "/wifip2pshared";
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        try {
            socket = new Socket();
            handler.post(new Runnable() {
                @Override
                public void run() {
                   // Toast.makeText(context, "socket made", Toast.LENGTH_SHORT).show();
                }
            });
            socket.bind(null);
            socket.connect(new InetSocketAddress(IP, port));

            handler.post(new Runnable() {
                @Override
                public void run() {
                   // Toast.makeText(context, "request send", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (final IOException e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    flag=1;
                    Toast.makeText(context, "Failed to connect to host!", Toast.LENGTH_SHORT).show();
                }
            });

        }
       //  File[] dirs= new File(Path).listFiles();

        //if (!dirs.exists())
            //dirs.mkdirs();
        try {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(context," directory created" ,Toast.LENGTH_SHORT).show();
                }
            });


        } catch (final Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    flag=1;
                    Toast.makeText(context,"ERROR TRY AGAIN",Toast.LENGTH_SHORT).show();
                }
            });
        }


        try {
            InputStream in=socket.getInputStream();
            BufferedInputStream bf=new BufferedInputStream(in);
            DataInputStream ds=new DataInputStream(bf);

            filesCount = ds.readInt();


            File[] files = new File[filesCount];

            for(int i = 0; i < filesCount; i++)
            {

                    long fileLength = ds.readLong();
                    String fileName = ds.readUTF();

                    files[i] = new File(Path + "/" + fileName);
                    files[i].getParentFile().mkdirs();
//                if(!files[i].exists())
//                files[i].mkdirs();
//                files[i].createNewFile();

                    FileOutputStream fos = new FileOutputStream(files[i]);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);

                    for (int j = 0; j < fileLength; j++)
                        bos.write(bf.read());
                    mDialog.setProgress((i * 100) / filesCount);

                    bos.close();
                }

            mDialog.dismiss();

            ds.close();
            socket.close();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,"File received!!",Toast.LENGTH_SHORT).show();


                }
            });

        } catch (final IOException e)

    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                flag=1;
                Toast.makeText(context, "Failed to tranfer" + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

    }

        return null;


}

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            inputStream.close();



        } catch (IOException e) {


            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

       // flag=0;
        if(flag==0) {
            status.setText("Successfully received file!!");

            //  status.setVisibility(View.VISIBLE);
            layout.setVisibility(View.VISIBLE);
        }
        disconnect();
        mDialog.dismiss();
        //manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
          //  @Override
            //public void onSuccess() {
              //  Toast.makeText(context,"Group removed successfully",Toast.LENGTH_SHORT).show();
            //}

            //@Override
            //public void onFailure(int reason) {
              //  Toast.makeText(context,"Group removal failure",Toast.LENGTH_SHORT).show();
            //}
        //});

    }

    public  void disconnect() {

        if ( manager!= null && channel != null){
            manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null && manager != null && channel != null
                            && group.isGroupOwner()) {
                        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int reason) {
                                Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            });
        }
    }}

