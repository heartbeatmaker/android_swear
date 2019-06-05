package com.example.swearVer2;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


//죽지않는 서비스
//
public class SensorService extends Service {

    public int counter = 0;
    Context context;

    String preferenceName, goalTitle;

    public SensorService(Context applicationContext){
        super();
        context = applicationContext;
        Log.d("알람", "SensorService.class 생성자 안 - 서비스가 살아있습니다");
    }

    public SensorService(){}


        @Override
        public int onStartCommand (Intent intent,int flags, int startId){
        super.onStartCommand(intent, flags, startId);
            Log.d("알람", "SensorService - onStartCommand()");
        try {

            if(intent.hasExtra("preferenceName")) {
                preferenceName = intent.getStringExtra("preferenceName");
                String originalGoalTitle = intent.getStringExtra("goalTitle");

                goalTitle = originalGoalTitle;
                if (originalGoalTitle.length() > 5) {
                    goalTitle = originalGoalTitle.substring(0, 6);
                }

                showNotification("약속 종료 안내", "[" + goalTitle + "..]의 최종 결과를 확정해주세요.");
            }

//            startTimer();

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("알람", ex);
        }
        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        //앱이 꺼지면서 서비스가 종료될 때 -> broadcastReceiver로 인텐트 보냄
        Log.d("알람", "SensorService - onDestroy()");

        Intent broadcastIntent = new Intent(this, ServiceAlarmReceiver.class);
        sendBroadcast(broadcastIntent);

//        setAlarmTimer();
//        stopTimerTask();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private Timer timer;
    private TimerTask timerTask;
    long oldTime = 0;
    public void startTimer(){
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 1000);
    }

    public void initializeTimerTask(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d("알람", "SensorService, 타이머 안 - counter: "+(counter++));
            }
        };
    }

    public void stopTimerTask(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    protected void setAlarmTimer(){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 1);
        Intent intent = new Intent(this, ServiceAlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0,intent,0);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);

    }


    public void showNotification(String heading, String description){

        createChannel(this);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"channelID")
                .setSmallIcon(R.drawable.pinky_promise)
                .setContentTitle(heading)
                .setContentText(description)
                .setAutoCancel(true);

        Intent openThePageIntent = new Intent(this, CheckGoals.class);
        openThePageIntent.putExtra("preferenceName", preferenceName);
        openThePageIntent.putExtra("isClicked", true);//해당 메시지를 클릭해서 읽었는지 확인하기 위한 용도. true/false 값은 상관없음
        openThePageIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int)(System.currentTimeMillis()/1000), openThePageIntent, PendingIntent.FLAG_ONE_SHOT);

        notificationBuilder.setContentIntent(pendingIntent);

        int notificationId = (int)(System.currentTimeMillis()/1000);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
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

}
