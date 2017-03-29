package com.example.android.sharewithall;

import android.content.ContentResolver;
import android.content.Context;
import android.location.Address;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by pussyhunter on 04/02/2017.
 */

public class ServerAsyncTask extends AsyncTask {


    Context context;
    ArrayList<Socket> mlist;
    ServerSocket mySocket;

    Handler handler = new Handler();

    ServerAsyncTask(Context context, ServerSocket mySocket,ArrayList<Socket> mlist) {
        this.context = context;
        this.mySocket = mySocket;
        this.mlist=mlist;
    }

    public static int SERVER_PORT = 8080;

    @Override
    protected  Object doInBackground(Object[] params) {

        Socket socket = null;

        try {
            socket = mySocket.accept();
            mlist.add(socket);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(context, "socket added sucessfully size of list is " +mlist.size(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (final IOException e1) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Error in connection" + e1.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        return null;

    }




    public String getIPAddress() {
        byte[] address = null;
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaceEnumeration.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
                while (inetAddressEnumeration.hasMoreElements()) {
                    InetAddress inetAddress = inetAddressEnumeration.nextElement();
                    String iface = networkInterface.getName();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (inetAddress instanceof Inet4Address) {
                            address = inetAddress.getAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return getDottedDecimalIP(address);

    }

    private static String getDottedDecimalIP(byte[] ipAddr) {
        String ipAddrStr = "";
        for (int i = 0; i < ipAddr.length; i++) {
            if (i > 0) {
                ipAddrStr += ".";
            }
            ipAddrStr += ipAddr[i] & 0xFF;
        }
        return ipAddrStr;
    }

}