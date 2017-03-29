package com.example.android.sharewithall;

import android.widget.Button;
import android.widget.ProgressBar;

/**
 * Created by pussyhunter on 07/02/2017.
 */

public class Wifi_device {
    String name;
    String status;
    int progress;
    //Button button;
    ProgressBar progressBar;

    Wifi_device(String name,String status){
        this.name=name;
        this.status=status;
        progress=0;
        progressBar=null;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    String getName(){
        return name;
    }
   // public Button getButton(){return Button;}

    String getStatus(){
        return status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setName(String name) {
        this.name = name;
    }
}
