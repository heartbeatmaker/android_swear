package com.example.swearVer2;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class ReadMessage extends AppCompatActivity implements View.OnLongClickListener{

    private ArrayList<MessageItem> mItemArrayList = new ArrayList<>();
    ArrayList<MessageItem> selection_list = new ArrayList<>(); //지울
    ArrayList<Integer> selected_position = new ArrayList<>(); //지우려고 선택한 아이템의 위치
    int counter = 0;

    Toolbar toolbar;

    boolean is_in_action_mode = false;
    TextView counter_text_view;
    MessageAdapter adapter;
    String currentUser;

    JSONArray receivedMessage_arr;



    //저장소에서 프로필 사진 경로를 불러옴 -> 화면에 표시
    private Bitmap setProfileImage(String userEmail){

        try {
            String imagePath = getUserData(userEmail, "profileImagePath");

            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                if (myBitmap != null) {
                    ExifInterface ei = new ExifInterface(imagePath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap rotatedBitmap = null;
                    switch(orientation){
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(myBitmap,90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(myBitmap,180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(myBitmap,270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = myBitmap;
                    }

                    return rotatedBitmap;
                }
            }
        }catch(Exception e){

            Log.d("마이페이지","onCreate(), 사진없음");
        }

        return null;
    }

    private Bitmap rotateImage(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source,0,0,source.getWidth(), source.getHeight(),matrix,true);
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
        Log.d("마이페이지", "json 파싱함. userInfo에 있는 정보 전부 출력: "+userInfo);

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(userInfo);
            Log.d("마이페이지", "json 파싱함. object에 있는 정보 전부 출력: " + jsonObject);

            if(jsonObject.containsKey(key)) {
                data = (String) jsonObject.get(key);
            }

        }catch(Exception e){

        }
        return data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_message);

        currentUser = getCurrentUser();
        toolbar =(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initRecyclerView();

        counter_text_view = (TextView)findViewById(R.id.counter_text);
        counter_text_view.setVisibility(View.GONE);




        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String userInfo_string = sp.getString(currentUser, ""); //사용자의 정보(string)를 불러온다
        Log.d("메시지", "json 파싱함. 사용자의 정보 전부 출력: "+userInfo_string);

        JSONParser parser = new JSONParser();
        try {
            //사용자의 정보(string)을 json 형태로 파싱한다
            JSONObject userInfo_object = (JSONObject) parser.parse(userInfo_string);

            //이미 키가 있다면
            if(userInfo_object.containsKey("receivedMessage")){
                //친구의 정보에 있던 receivedMessage array를 불러온다
                Object receivedMessage_object = userInfo_object.get("receivedMessage");
                receivedMessage_arr = (JSONArray)receivedMessage_object;
                Log.d("메시지", "json 파싱함. receivedMessage_arr에 있는 정보 전부 출력: " + receivedMessage_arr);

                for(int i=0; i<receivedMessage_arr.size(); i++){
                    JSONObject user_object = (JSONObject)receivedMessage_arr.get(i);

                    String email = (String)user_object.get("email");
                    String message = (String)user_object.get("message");
                    String time = (String)user_object.get("time");

                    String username = getUserData(email, "username");

                    //int messageProfile, String messageName, String messageContent, String messageTime, String messageCount
                    mItemArrayList.add(i, new MessageItem(setProfileImage(email), username,
                            message , time,""));
                }

            }
            else{ //불러올 메시지가 없다면(키가 없음)
                Toast.makeText(this, "메시지 함이 비었습니다", Toast.LENGTH_SHORT).show();
            }



            Log.d("메시지", "json 파싱함. userInfo_object에 있는 정보 전부 출력: " + userInfo_object);

            Map<String, ?> allEntries = sp.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d("메시지", "preference에 있는 데이터 전부출력: "+entry.getKey() + ": " + entry.getValue().toString());
            }
        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("메시지",ex);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        Log.d("알람", "ReadMessage.class onPause() - clear_numberOfUnreadMessage()");
        clear_numberOfUnreadMessage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("알람", "ReadMessage.class onStop()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.item_menu,menu);
        return true;
    }


    private String date(){
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm");
        String date = format.format(d);

        return date;
    }


    public void clear_numberOfUnreadMessage(){
        Log.d("알람","ReadMessage) clear_numberOfUnreadMessage 들어옴");

        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String currentUser = sp.getString("currentUser","현재사용자 불러오기 실패");
        String userInfo = sp.getString(currentUser, "");

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(userInfo);

            //안읽은 메시지 초기화
            jsonObject.put("unreadMessage", 0);
            Log.d("알람","unreadMessage 를 초기화한다");

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


    private void initRecyclerView(){

        RecyclerView rcv = findViewById(R.id.message_recyclerView);
        adapter = new MessageAdapter(mItemArrayList, this);
        rcv.setAdapter(adapter);
        rcv.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public boolean onLongClick(View v) {

        toolbar.getMenu().clear(); //길게누르면 -> 툴바 clear
        toolbar.inflateMenu(R.menu.menu_delete);
        counter_text_view.setVisibility(View.VISIBLE);
        is_in_action_mode = true;
        adapter.notifyDataSetChanged();

        return true;
    }

    public void prepareSelection(View view, final int position) {

        if (((CheckBox)view).isChecked()) { //체크 됐을 때
            selection_list.add(mItemArrayList.get(position));
            selected_position.add(position); //해당 아이템의 위치를 리스트에 담는다
            counter = counter + 1;
            updateCounter(counter);

            Log.d("메시지","삭제) "+position+"을 삭제하려고 고름");
            Log.d("메시지","삭제) "+position+"을 selectedList에 담음: "+selected_position);
        } else { //isChecked()=false 일 때 (uncheck 됐을 때)
            selection_list.remove(mItemArrayList.get(position));
//            selected_position.removeIf(p -> p.equals(position)); //해당 아이템의 위치값을 리스트에서 제거한다
            counter = counter - 1;
            updateCounter(counter);
                try {
                    for(int i=selected_position.size()-1; i>=0; i--){
                        if(position == selected_position.get(i)){
                            selected_position.remove(i);
                        }
                    }

                    Log.d("메시지", "삭제) " + position + "을 삭제 취소함");
                    Log.d("메시지", "삭제) " + position + "을 selectedList에서 지움: " + selected_position);
                }catch (Exception e){
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String ex = sw.toString();

                    Log.d("메시지",ex);
                }
            }

        }


    public void updateCounter(int counter){
       if(counter == 0){
           counter_text_view.setText("0 item selected");
       }
       else{
           counter_text_view.setText(counter+" items selected");
       }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.item_delete){ //지우기 아이콘을 누르면


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.activity_delete_dialog, null, false);
            builder.setView(view);

            final Button confirm = (Button)view.findViewById(R.id.button11);
            final Button cancel = (Button)view.findViewById(R.id.button12);

            final AlertDialog dialog = builder.create();

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MessageAdapter messageAdapter = (MessageAdapter) adapter;
                    messageAdapter.updateAdapter(selection_list); //selection_list에 있는 아이템을 지움
                    clearActionMode(); //액션바, 각종 변수를 초기화

                    removeItemFromJSONArray(); //저장소에서 해당 아이템을 삭제함
                    selected_position.clear();

                    dialog.dismiss();
                    Toast.makeText(ReadMessage.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();


        }
        return super.onOptionsItemSelected(item);
    }

    public void clearActionMode(){
        is_in_action_mode = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.item_menu);
        counter_text_view.setVisibility(View.GONE);

        //변수 초기화
        counter_text_view.setText("0 item selected");
        counter = 0;
        selection_list.clear();
    }


    public void removeItemFromJSONArray(){
        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String userInfo_string = sp.getString(currentUser, ""); //사용자의 정보(string)를 불러온다
//        Log.d("메시지", "json 파싱함. 사용자의 정보 전부 출력: "+userInfo_string);

        JSONParser parser = new JSONParser();
        try {
            //사용자의 정보(string)을 json 형태로 파싱한다
            JSONObject userInfo_object = (JSONObject) parser.parse(userInfo_string);


            //친구의 정보에 있던 receivedMessage array를 불러온다
            Object receivedMessage_object = userInfo_object.get("receivedMessage");
            receivedMessage_arr = (JSONArray)receivedMessage_object;
//            Log.d("메시지", "json 파싱함. receivedMessage_arr에 있는 정보 전부 출력: " + receivedMessage_arr);

            //선택한 위치값을 내림차순으로 정렬한다
            Collections.sort(selected_position);
            Collections.reverse(selected_position);

            try {
                Log.d("메시지","삭제) selected_position_list: "+selected_position);
                //0부터 올라가면 순서가 엉키기 때문에 위에서부터 내려오면서 삭제한다

                int i, j;
                for (i = receivedMessage_arr.size() - 1; i >= 0; i--) {

                    for (j = 0; j < selected_position.size(); j++) {
                        Log.d("메시지","receivedMessage_arr: "+i+"번째, selected_position 값: "+selected_position.get(j));
                        if (selected_position.get(j) == i) {
                            receivedMessage_arr.remove(i);
                            Log.d("메시지","jsonArray에서"+i+"번째 아이템이 삭제됨");
                        }
                    }
                }

                userInfo_object.put("receivedMessage", receivedMessage_arr);
                editor.putString(currentUser, userInfo_object.toString());
                editor.commit();


            }catch (Exception e){
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String ex = sw.toString();

                Log.d("메시지",ex);
            }


            Log.d("메시지", "json 파싱함. userInfo_object에 있는 정보 전부 출력: " + userInfo_object);

            Map<String, ?> allEntries = sp.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d("메시지", "preference에 있는 데이터 전부출력: "+entry.getKey() + ": " + entry.getValue().toString());
            }
        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("메시지",ex);
        }

    }


    //action mode에서 뒤로가기 눌렀을 때, 해당 액티비티 자체를 벗어남. 이 문제를 해결하기 위한 메소드
    @Override
    public void onBackPressed(){
        if(is_in_action_mode){
            clearActionMode();
            selected_position.clear();
            adapter.notifyDataSetChanged();
        }
        else{
            super.onBackPressed();

            finish();
        }

    }


}
