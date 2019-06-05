package com.example.swearVer2;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;


public class ScreenReceiver extends Application {

    public static int screenFlag;

    private BroadcastReceiver screenOnReceiver;
    private BroadcastReceiver screenOffReceiver;
    private IntentFilter screenOnFilter;
    private IntentFilter screenOffFilter;

    @Override
    public void onCreate() {
        super.onCreate();


        screenOnReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(this.getClass().getName(), "Screen on");
                Toast.makeText(getApplicationContext(),"Screen on", Toast.LENGTH_LONG).show();
                screenFlag = 1;
                Log.d(this.getClass().getName(), String.valueOf(screenFlag));
            }
        };


        screenOnFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);


        screenOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getApplicationContext(),"Screen off", Toast.LENGTH_LONG).show();
                screenFlag = 0;
                Log.d(this.getClass().getName(), "Screen off");
                Log.d(this.getClass().getName(), String.valueOf(screenFlag));
            }
        };

        screenOffFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);

        registerReceiver(screenOnReceiver, screenOnFilter);
        registerReceiver(screenOffReceiver, screenOffFilter);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        unregisterReceiver(screenOnReceiver);
        unregisterReceiver(screenOffReceiver);
    }
}




//        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
//           screenOff = true;
//           Intent i = new Intent(context, LockScreen.class);
//           i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//           context.startActivity(i);
//        }



