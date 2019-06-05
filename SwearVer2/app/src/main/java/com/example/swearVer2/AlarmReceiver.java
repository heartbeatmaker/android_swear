package com.example.swearVer2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

//알람매니저가 보내는 방송을 받는 리시버
//방송은 약속 종료일에 발송된다
//약속을 수행하는 사용자 이름, 약속이름, 약속 세부사항이 저장된 xml파일의 이름을 전달받는다
public class AlarmReceiver extends BroadcastReceiver {

    String userEmail, preferenceName, goalTitle;
    Context ctx;

    JSONArray alarmMessage_arr;


    public boolean getUserBooleanData(String email, String key){

        boolean data = false;

        //현재 사용자의 email(회원 식별자)를 불러온다
        SharedPreferences sp = ctx.getSharedPreferences(SignUp.userInfoPreference, ctx.MODE_PRIVATE);

        //Preference에서 해당 사용자의 정보(string 형태로 변환된 json객체)를 불러온다
        String userInfo = sp.getString(email, "");
        Log.d("마이페이지", "json 파싱함. userInfo에 있는 정보 전부 출력: "+userInfo);

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(userInfo);
            Log.d("마이페이지", "json 파싱함. object에 있는 정보 전부 출력: " + jsonObject);

            if(jsonObject.containsKey(key)) {
                data = (Boolean) jsonObject.get(key);
            }

        }catch(Exception e){

        }
        return data;
    }

    public void setNumberOfUnreadAlarmMessage(String email){
        Log.d("알람","setNumberOfUnreadAlarmMessage 들어옴");

        SharedPreferences sp = ctx.getSharedPreferences(SignUp.userInfoPreference, ctx.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String userInfo = sp.getString(email, "");

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(userInfo);

            //초기값 설정
            int numberOfMessage = 0;

            //저장된 값이 있다면 그것을 불러온다
            if(jsonObject.containsKey("unreadAlarmMessage")){
                numberOfMessage = ((Long)jsonObject.get("unreadAlarmMessage")).intValue();
                Log.d("알람","저장된 값이 있다고 함. numberOfMessage: "+numberOfMessage);
            }

            //새로운 값(+1)을 넣어준다
            //number++하면 늘어난 값이 object에 반영되지 않음!!!!!!!!! 연산자 위치 주의
            jsonObject.put("unreadAlarmMessage", ++numberOfMessage);
            Log.d("알람","+1 해줌. numberOfMessage: "+numberOfMessage);

            //값을 string 형으로 변환한 후, sharedPreference에 저장한다
            editor.putString(email, jsonObject.toString());
            editor.commit();
            Log.d("알람", "object에 있는 정보 전부 출력: " + jsonObject);

//            Map<String, ?> allEntries = sp.getAll();
//            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//                Log.d("마이페이지", "preference에 있는 데이터 전부출력: "+entry.getKey() + ": " + entry.getValue().toString());
//            }

        }catch(Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("알람",ex);
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
    ctx = context;

        Log.d("알람", "AlarmReceiver 클래스, onReceive 들어옴");

        try {
            userEmail = intent.getStringExtra("userEmail");
            preferenceName = intent.getStringExtra("preferenceName");
            goalTitle = intent.getStringExtra("goalTitle");
            Log.d("알람", "username: "+userEmail+" preferenceName: "+preferenceName+" goalTitle: "+goalTitle);

            //사용자의 메시지함에 알람메시지를 저장한다
            saveMessage();

            //해당 사용자가 받은 알람메시지의 개수를 +1 한다 (메시지 읽었는지 안 읽었는지 확인하는 용도)
            setNumberOfUnreadAlarmMessage(userEmail);


            //사용자에게 푸쉬 알람을 띄운다 - 현재 로그인 상태일 때 + 알람수신 on 상태일 때
            SharedPreferences infoPref = context.getSharedPreferences(SignUp.userInfoPreference, Context.MODE_PRIVATE);
            String currentUserEmail = infoPref.getString("currentUser","유저정보없음");
            Log.d("알람", "currentUsername: "+currentUserEmail);

            if(userEmail.equals(currentUserEmail) && getUserBooleanData(userEmail,"isReceivingNoti")){
                Intent startServiceIntent = new Intent(context, SensorService.class);
                startServiceIntent.putExtra("preferenceName", preferenceName);
                startServiceIntent.putExtra("goalTitle", goalTitle);

                context.startService(startServiceIntent);
                Log.d("알람", "리시버, startForegroundService");
            }

        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("알람",ex);
        }

//        preferenceName = intent.getStringExtra("preferenceName");
//        String originalGoalTitle = intent.getStringExtra("goalTitle");
//        goalTitle = originalGoalTitle.substring(0,6);
//
//        if(originalGoalTitle.length()>6) {
//            showNotification(context, "약속 종료 안내", "[" + goalTitle + "..]의 최종 결과를 확정해주세요.");
//        }
//        else{
//            showNotification(context, "약속 종료 안내", "[" + originalGoalTitle + "]의 최종 결과를 확정해주세요.");
//        }

    }

    //현재 날짜, 시간을 구하기
    public String getCurrentTime(){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd aaa hh:mm");
        String date = format.format(System.currentTimeMillis());
        return date;
    }

    public void saveMessage(){

        String message = "약속 종료 안내 : [" + goalTitle + "]의 최종 결과를 확정해주세요.";

        JSONObject messageInfo = new JSONObject();
        messageInfo.put("message", message); //메시지 내용
        messageInfo.put("time", getCurrentTime()); //보낸 시각


        SharedPreferences sp = ctx.getSharedPreferences(SignUp.userInfoPreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String userInfo_string = sp.getString(userEmail, ""); //사용자의 정보(string)를 불러온다
        Log.d("메시지", "json 파싱함. 사용자의 정보 전부 출력: "+userInfo_string);

        JSONParser parser = new JSONParser();
        try {
            //사용자의 정보(string)을 json 형태로 파싱한다
            JSONObject userInfo_object = (JSONObject) parser.parse(userInfo_string);

            //이미 키가 있다면
            if(userInfo_object.containsKey("alarmMessage")){
                //사용자 정보에 있던 receivedMessage array를 불러온다
                Object alarmMessage_object = userInfo_object.get("alarmMessage");
                alarmMessage_arr = (JSONArray)alarmMessage_object;
                Log.d("메시지", "json 파싱함. alarmMessage_arr에 있는 정보 전부 출력: " + alarmMessage_arr);
            }
            else{ //없다면 새로 만들어준다
                alarmMessage_arr = new JSONArray();
            }

            //위에서 작성한 메시지(jsonObject)를 추가한다
            alarmMessage_arr.add(0, messageInfo);
            Log.d("메시지", "json 파싱함. messageInfo에 있는 정보 전부 출력: " + messageInfo);

            userInfo_object.put("alarmMessage", alarmMessage_arr);

            //바뀐 객체를 SharedPreferences 에 저장한다
            editor.putString(userEmail, userInfo_object.toString());
            editor.commit();

            Log.d("메시지", "json 파싱함. userInfo_object에 있는 정보 전부 출력: " + userInfo_object);

//            Map<String, ?> allEntries = sp.getAll();
//            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//                Log.d("메시지", "preference에 있는 데이터 전부출력: "+entry.getKey() + ": " + entry.getValue().toString());
//            }
        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("메시지",ex);
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


//        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
//        taskStackBuilder.addParentStack(CheckGoals.class);
//        taskStackBuilder.addNextIntent(openThePageIntent);
//
//        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent((int)(System.currentTimeMillis()/1000),
//                PendingIntent.FLAG_ONE_SHOT);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int)(System.currentTimeMillis()/1000), openThePageIntent, PendingIntent.FLAG_ONE_SHOT);

        notificationBuilder.setContentIntent(pendingIntent);

//        int notificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
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
