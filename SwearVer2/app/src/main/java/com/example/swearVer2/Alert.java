package com.example.swearVer2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Alert extends AppCompatActivity {

    private static final String TAG = "Alert Activity";

    private ArrayList<AlertItem> alertItemArrayList = new ArrayList<>();
    AlertAdapter adapter;

    JSONArray alarmMessage_arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

//        initArrayList();
        setDataInArrayList();
        initRecyclerView();

        //홈화면으로 돌아가기
        ImageButton ib = (ImageButton)findViewById(R.id.homebtn);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        clearItem();
    }


    @Override
    protected void onPause() {
        super.onPause();

        Log.d("알람", "Alert.class onPause() - clear_numberOfUnreadAlarmMessage()");
        //안읽은 메시지 목록을 0으로 초기화한다 - 메시지를 모두 읽었으므로
        clear_numberOfUnreadAlarmMessage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("알람", "Alert.class onStop()");

    }

    public void clear_numberOfUnreadAlarmMessage(){
        Log.d("알람","CheckGoals) setNumberOfUnreadAlarmMessage 들어옴");

        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String currentUser = sp.getString("currentUser","현재사용자 불러오기 실패");
        String userInfo = sp.getString(currentUser, "");

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(userInfo);

            //안읽은 메시지 초기화
            jsonObject.put("unreadAlarmMessage", 0);
            Log.d("알람","unreadAlarmMessage 를 초기화한다");

            //값을 string 형으로 변환한 후, sharedPreference에 저장한다
            editor.putString(currentUser, jsonObject.toString());
            Log.d("알람", "object에 있는 정보 전부 출력: " + jsonObject);

            editor.commit();

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


    private void clearItem(){

        Button b = (Button)findViewById(R.id.clear_btn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertItemArrayList.clear();
                adapter.notifyDataSetChanged();
                removeItemFromJSONArray();
            }
        });

    }

    private String date(){
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm");
        String date = format.format(d);

        return date;
    }

    //현재 날짜, 시간을 구하기
    public String getCurrentTime(){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd aaa hh:mm");
        String date = format.format(System.currentTimeMillis());
        return date;
    }

    private String getCurrentUser(){
        SharedPreferences infoPref = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        return infoPref.getString("currentUser","유저정보없음");
    }

    public String getUserData(String email, String key){

        String data = "데이터 없음";

        //현재 사용자의 email(회원 식별자)를 불러온다
        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);

        //Preference에서 해당 사용자의 정보(string 형태로 변환된 json객체)를 불러온다
        String userInfo = sp.getString(email, "");

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(userInfo);

            if(jsonObject.containsKey(key)) {
                data = (String) jsonObject.get(key);
            }

        }catch(Exception e){ }
        return data;
    }


    //더미데이터 넣기
    private void initArrayList(){

        AlertItem item = new AlertItem("이런 목표는 어떠세요?: 하루에 만 원씩 저축하기", "3/22");
        AlertItem item_2 = new AlertItem("[하루에 코드 만 줄..] 2일차 입니다.", "4/3");
        AlertItem item_3 = new AlertItem("Gerge.B가 [하루에 코드 만 줄..]를 열람했습니다", "4/3");
        AlertItem item_4 = new AlertItem("Gerge.B가 [하루에 코드 만 줄..]에 메시지를 남겼습니다.", "4/3");
        AlertItem item_5 = new AlertItem("[하루에 코드 만 줄..] 3일차 입니다.", "4/4");
        AlertItem item_6 = new AlertItem("[하루에 코드 만 줄..] 4일차 입니다.", "4/5");
        AlertItem item_7 = new AlertItem("[하루에 코드 만 줄..] 5일차 입니다.", "4/6");
        AlertItem item_8 = new AlertItem("보고서를 하나도 작성하지 않으셨네요!", "어제");
        AlertItem item_9 = new AlertItem("[하루에 코드 만 줄..] 약속 마지막 날입니다.", "어제");
        AlertItem item_10 = new AlertItem("[하루에 코드 만 줄..]약속 이행기간이 종료되었습니다.", "3시간 전");

        alertItemArrayList.add(item_10);
        alertItemArrayList.add(item_9);
        alertItemArrayList.add(item_8);
        alertItemArrayList.add(item_7);
        alertItemArrayList.add(item_6);
        alertItemArrayList.add(item_5);
        alertItemArrayList.add(item_4);
        alertItemArrayList.add(item_3);
        alertItemArrayList.add(item_2);
        alertItemArrayList.add(item);
    }

    private void setDataInArrayList(){

        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String userInfo_string = sp.getString(getCurrentUser(), ""); //사용자의 정보(string)를 불러온다
        Log.d("메시지", "json 파싱함. 사용자의 정보 전부 출력: "+userInfo_string);

        JSONParser parser = new JSONParser();
        try {
            //사용자의 정보(string)을 json 형태로 파싱한다
            JSONObject userInfo_object = (JSONObject) parser.parse(userInfo_string);

            //이미 키가 있다면
            if(userInfo_object.containsKey("alarmMessage")){
                //사용자 정보에 있던 alarmMessage arrayList를 불러온다
                Object alarmMessage_object = userInfo_object.get("alarmMessage");
                alarmMessage_arr = (JSONArray)alarmMessage_object;
                Log.d("메시지", "json 파싱함. receivedMessage_arr에 있는 정보 전부 출력: " + alarmMessage_arr);

                for(int i=0; i<alarmMessage_arr.size(); i++){
                    JSONObject user_object = (JSONObject)alarmMessage_arr.get(i);

                    String message = (String)user_object.get("message");
                    String time = (String)user_object.get("time");

                    //String alertContent, String alertTime
                    alertItemArrayList.add(i, new AlertItem(message , time));
                }

            }
            else{ //불러올 메시지가 없다면(키가 없음)
                Toast.makeText(this, "메시지 함이 비었습니다", Toast.LENGTH_SHORT).show();
            }

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

    public void removeItemFromJSONArray(){
        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String userInfo_string = sp.getString(getCurrentUser(), ""); //사용자의 정보(string)를 불러온다
//        Log.d("메시지", "json 파싱함. 사용자의 정보 전부 출력: "+userInfo_string);

        JSONParser parser = new JSONParser();
        try {
            //사용자의 정보(string)을 json 형태로 파싱한다
            JSONObject userInfo_object = (JSONObject) parser.parse(userInfo_string);

            //사용자 정보에 있던 receivedMessage array를 불러온다
            Object alarmMessage_object = userInfo_object.get("alarmMessage");
            alarmMessage_arr = (JSONArray)alarmMessage_object;

            alarmMessage_arr.clear();
            Log.d("메시지", "alarmMessage_arr.clear() -> alarmMessage_arr: " + alarmMessage_arr);

            userInfo_object.put("alarmMessage", alarmMessage_arr);
            editor.putString(getCurrentUser(), userInfo_object.toString());
            editor.commit();


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


    private void initRecyclerView(){

        RecyclerView rcv = findViewById(R.id.alert_recyclerView);
        adapter = new AlertAdapter(this, alertItemArrayList);
        rcv.setAdapter(adapter);
        rcv.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
