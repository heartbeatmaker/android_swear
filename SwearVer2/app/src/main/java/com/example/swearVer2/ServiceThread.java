package com.example.swearVer2;

import android.os.Handler;
import android.util.Log;

public class ServiceThread extends Thread{
    Handler handler;
    public static boolean isRun = true;

    public ServiceThread(Handler handler){
        this.handler = handler;
    }

    public void stopForever(){
//        synchronized (this){
//            this.isRun = false;
//        }

        isRun = false;
    }

    public void run(){
        while(isRun){
            Log.d("알람","serviceThread, run()");
            handler.sendEmptyMessage(0);
            try{
                Log.d("알람","serviceThread, run(), Sleep()");
                Thread.sleep(5);
            }catch (Exception e){}
        }

    }



}
