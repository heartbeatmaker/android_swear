package com.example.swearVer2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;


//사용하지 않는 클래스
public class ServiceReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("알람", "ServiceReceiver 클래스, onReceive 들어옴");

        try {

//            context.startService(new Intent(context, SensorService.class));
            Log.d("알람", "context.startForegroundService");

//            Intent startServiceIntent = new Intent(context, StartService.class);
//
//            SharedPreferences sp = context.getSharedPreferences("Service", Context.MODE_PRIVATE);
//            String preferenceName = sp.getString("preferenceName", "preferenceName 못받음");
//            String goalTitle = sp.getString("goalTitle", "goalTitle 못받음");
//
//            Log.d("알람", "리시버, sp에서 정보 꺼내옴. preferenceName: "+preferenceName);
//            Log.d("알람", "리시버, sp에서 정보 꺼내옴. goalTitle: "+goalTitle);
//
//            startServiceIntent.putExtra("preferenceName", preferenceName);
//            startServiceIntent.putExtra("goalTitle", goalTitle);
//
//            context.startForegroundService(startServiceIntent);
//            Log.d("알람", "리시버, startForegroundService");
//            Log.d("알람", "ServiceReceiver 클래스 끝");

        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("알람",ex);
        }

    }
}
