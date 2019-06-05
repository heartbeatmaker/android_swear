package com.example.swearVer2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;

//사용하지 않는 클래스 - 실험용으로 써 본 것. 매니페스트에서 삭제함
public class StartService extends Service {

    private static final String TAG = "MyService";

    NotificationManager Notifi_M;
    ServiceThread thread;
    Notification Notifi ;

    String preferenceName, originalGoalTitle, goalTitle;

    @Override
    public void onCreate() { //서비스가 최초 생성되었을 때 한 번 실행된다
        super.onCreate();
        try{

            Log.d("알람","알람 서비스 onCreate()");
            Toast.makeText(this, "service Created", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("알람",ex);
        }
        //TODO: Start ongoing notification here to make service foreground
    }


    //백그라운드에서 실행되는 동작이 들어가는 곳
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("알람","알람 서비스 onStartCommand()");

        try {
            preferenceName = intent.getStringExtra("preferenceName");
            Log.d("알람", "서비스) 인텐트를 건네받음 preferenceName: " + preferenceName);
            originalGoalTitle = intent.getStringExtra("goalTitle");

            goalTitle = originalGoalTitle;
            if(originalGoalTitle.length()>5) {
                goalTitle = originalGoalTitle.substring(0, 6);
            }
            Log.d("알람", "서비스) 인텐트를 건네받음 goalTitle: " + goalTitle);

            //노티피케이션에 표시해야 하는 정보를 저장해둠
            SharedPreferences sp = getSharedPreferences("Service", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            if(sp.contains("preferenceName")){
                editor.remove("preferenceName");
                editor.remove("goalTitle");
            }
            editor.putString("preferenceName", preferenceName);
            editor.putString("goalTitle", goalTitle);
            editor.commit();
            Log.d("알람", "서비스) 노티에 필요한 정보를 sp에 저장함");

            Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            myServiceHandler handler = new myServiceHandler();
            thread = new ServiceThread(handler);
            thread.start();

        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("알람",ex);
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("알람","알람 onBind()");
        return null;
    }

    //서비스가 종료될 때 실행된다
    @Override
    public void onDestroy() {
        //TODO: cancel the notification
        Log.d("알람","알람 onDestroy()");

        try {
//            thread.stopForever();
            ServiceThread.isRun = false;
            Intent broadcastIntent = new Intent(this, ServiceReceiver.class);
            sendBroadcast(broadcastIntent);
            Log.d("알람","알람 서비스 onDestroy(), 리시버로 broadcast 보냄 ");

        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("알람",ex);
        }
    }

    class myServiceHandler extends Handler {

//        String heading;
//        String description;
//
//        public void setHeading(String title){
//            heading = title;
//        }
//
//        public void setDescription(String content){
//            description = content;
//        }

        @Override
        public void handleMessage(android.os.Message msg) {
            Log.d("알람","myServiceHandler");

            showNotification("약속 종료 안내", "[" + goalTitle + "..]의 최종 결과를 확정해주세요.");

            Log.d("알람","myServiceHandler, notify()");

        }


        public void showNotification(String heading, String description){

            createChannel(StartService.this);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(StartService.this,"channelID")
                    .setSmallIcon(R.drawable.pinky_promise)
                    .setContentTitle(heading)
                    .setContentText(description)
                    .setAutoCancel(true);

            Intent openThePageIntent = new Intent(StartService.this, CheckGoals.class);
            openThePageIntent.putExtra("preferenceName", preferenceName);
            openThePageIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(StartService.this, (int)(System.currentTimeMillis()/1000), openThePageIntent, PendingIntent.FLAG_ONE_SHOT);

            notificationBuilder.setContentIntent(pendingIntent);

            int notificationId = (int)(System.currentTimeMillis()/1000);
            NotificationManager notificationManager = (NotificationManager) StartService.this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationId, notificationBuilder.build());
        }

        public void createChannel(Context context){
            if (Build.VERSION.SDK_INT < 26) {
                return;
            }
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("channelID","name", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Description");
            notificationManager.createNotificationChannel(channel);
        }
    };


}
