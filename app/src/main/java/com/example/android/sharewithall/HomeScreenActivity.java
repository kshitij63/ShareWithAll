package com.example.android.sharewithall;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class HomeScreenActivity extends AppCompatActivity {
    TextView send;
    TextView receive;
   //private  static final int READ_PERM=100;
    //private  static final int WRITE_PERM=101;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_screen);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
        }

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        send=(TextView) findViewById(R.id.send_home);
        receive=(TextView) findViewById(R.id.receive_home);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(HomeScreenActivity.this,MainActivity.class);
                startActivity(intent);
            }


        });

        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(HomeScreenActivity.this,"Ready to receive",Toast.LENGTH_SHORT).show();
                Intent i= new Intent(HomeScreenActivity.this,ReceiveActivity.class);
                startActivity(i);

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED&& grantResults[1]==PackageManager.PERMISSION_GRANTED)
            {

            }
            else
            {
Toast.makeText(this,"PERMISSION DENIED",Toast.LENGTH_LONG).show();
        finish();
            }
        }
    }
}
