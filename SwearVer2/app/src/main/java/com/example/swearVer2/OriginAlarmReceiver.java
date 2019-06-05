package com.example.swearVer2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


//서비스 없이 노티피케이션을 띄울 때 사용하는 broadcastReceiver이다(앱이 죽으면 무용지물)
//현재 사용하지 않음
public class OriginAlarmReceiver extends BroadcastReceiver {

    String preferenceName, goalTitle;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("알람", "AlarmReceiver 클래스, onReceive 들어옴");

        preferenceName = intent.getStringExtra("preferenceName");
        String originalGoalTitle = intent.getStringExtra("goalTitle");
        goalTitle = originalGoalTitle.substring(0,6);

        if(originalGoalTitle.length()>6) {
            showNotification(context, "약속 종료 안내", "[" + goalTitle + "..]의 최종 결과를 확정해주세요.");
        }
        else{
            showNotification(context, "약속 종료 안내", "[" + originalGoalTitle + "]의 최종 결과를 확정해주세요.");
        }

    }



    public void showNotification(Context context, String heading, String description){

        Log.d("알람", "AlarmReceiver 클래스, showNotification 들어옴");

        createChannel(context);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,"channelID")
                .setSmallIcon(R.drawable.pinky_promise)
                .setContentTitle(heading)
                .setContentText(description)
                .setAutoCancel(true);

        Intent openThePageIntent = new Intent(context, CheckGoals.class);
        openThePageIntent.putExtra("preferenceName", preferenceName);
        openThePageIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


// TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
// taskStackBuilder.addParentStack(CheckGoals.class);
// taskStackBuilder.addNextIntent(openThePageIntent);
//
// PendingIntent pendingIntent = taskStackBuilder.getPendingIntent((int)(System.currentTimeMillis()/1000),
// PendingIntent.FLAG_ONE_SHOT);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int)(System.currentTimeMillis()/1000), openThePageIntent, PendingIntent.FLAG_ONE_SHOT);

        notificationBuilder.setContentIntent(pendingIntent);

// int notificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        int notificationId = (int)(System.currentTimeMillis()/1000);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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





