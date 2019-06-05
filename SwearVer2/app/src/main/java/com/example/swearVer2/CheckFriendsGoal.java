package com.example.swearVer2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Date;
import java.util.Map;

public class CheckFriendsGoal extends AppCompatActivity {

    RecyclerView rcv;
    LinearLayoutManager llm;
    public MainAdapter mainAdapter;
    public ArrayList<Item> itemArrayList;

    String friendsEmail;
    String friendsUsername;

    JSONArray receivedMessage_arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_friends_goal);

        Intent intent = getIntent();
        friendsEmail = intent.getStringExtra("friendsEmail");
        friendsUsername = getData("username");

        setProfileImage();
        setUsername();

        //리사이클러뷰
        rcv = (RecyclerView)findViewById(R.id.checkFriendsGoal_recyclerView);
        llm = new LinearLayoutManager(this);

        rcv.setHasFixedSize(true); //리사이클러뷰 자체의 크기가 변하지 않을 때 - 성능개선(?) - 무슨 뜻?
        rcv.setLayoutManager(llm); //리사이클러뷰의 레이아웃을 linear로 맞춘다

        itemArrayList = new ArrayList<>(); //Item 클래스 형의 데이터를 리스트화 시킨다

        //현재 액티비티와 item리스트를 매개변수로 갖는 어댑터 객체를 만든다
        mainAdapter = new MainAdapter(this, itemArrayList);

        //어댑터 객체를 현 액티비티의 리사이클러뷰와 연결시킨다
        rcv.setAdapter(mainAdapter);


        SharedPreferences sp = getSharedPreferences(friendsEmail+";goalList", MODE_PRIVATE);
        int listSize = sp.getInt("size", 0);
        Log.d("알람", "리스트 사이즈를 불러옴. 리스트 사이즈 : "+listSize);

        if(listSize>0){
            int j=0;
            for(int i=0; i<listSize; i++){

                String itemPreference = sp.getString(String.valueOf(i),""); //약속별 preference 이름을 불러오기
                Log.d("알람", "약속별 preference 이름을 불러옴."+i+"번째 아이템의 xml이름: "+sp.getString(String.valueOf(i),""));

                SharedPreferences sspp = getSharedPreferences(itemPreference, MODE_PRIVATE);

                boolean isLocked = sspp.getBoolean("isLocked", false);

                //비밀글 상태가 아니면
                if(!isLocked) {

                    String goalTitle = sspp.getString("goalTitle", "제목없음"); //약속의 제목
                    String startDate = sspp.getString("goalStartDate", "시작x"); //약속 시작날짜
                    String endDate = sspp.getString("goalEndDate", "끝x"); //약속 종료날짜

                    //진행률 계산
                    int numberOfDays = sspp.getInt("numberOfDays", 0); //총 이행기간
                    int passedDays = calDateBetweenDates(startDate); //오늘까지 지난 기간
                    double progressRate = passedDays / (double) numberOfDays; //int끼리 계산하면 소수점자리를 잃는다 -> 결과값이 0이 나옴. 실수로 형변환하여 계산해야함
                    int finalProgressRate = (int) (Math.round(progressRate * 100));
                    if (finalProgressRate > 100) {
                        finalProgressRate = 100;
                    }

                    itemArrayList.add(j, new Item(finalProgressRate,
                            goalTitle, startDate + "~" + endDate, itemPreference));

                    j++;
                }
                //비밀글이라면
                else{
                    Log.d("비밀글", i+"번째 글은 비밀글입니다");
                }
            }
        }

        //지금 열람하고있는 사용자에게 메시지를 보내는 버튼 -> 클릭하면 대화상자가 뜬다
        Button leaveMessageBtn = (Button)findViewById(R.id.checkFriendsGoal_leaveMessage);
        leaveMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CheckFriendsGoal.this);

                final EditText editText = new EditText(CheckFriendsGoal.this);
                editText.setText("잘 하고 있어!");

                builder.setMessage("친구에게 하고싶은 말을 입력하세요").setView(editText)
                        .setPositiveButton("보내기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(CheckFriendsGoal.this, "메시지 전송 완료", Toast.LENGTH_SHORT).show();

                                String message = editText.getText().toString();
                                String time = getCurrentTime();
//                                ArrayList<String> messageInfoArrayList = new ArrayList<>();
//                                messageInfoArrayList.add(0, message);
//                                messageInfoArrayList.add(1, time);

                                JSONObject messageInfo = new JSONObject();
                                messageInfo.put("email", getCurrentUser()); //보낸사람
                                messageInfo.put("message", message); //메시지 내용
                                messageInfo.put("time", time); //보낸 시각


                                SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();

                                String friendInfo_string = sp.getString(friendsEmail, ""); //친구의 정보(string)를 불러온다
                                Log.d("메시지", "json 파싱함. 친구의 정보 전부 출력: "+friendInfo_string);

                                JSONParser parser = new JSONParser();
                                try {
                                    //친구의 정보(string)을 json 형태로 파싱한다
                                    JSONObject friendInfo_object = (JSONObject) parser.parse(friendInfo_string);

                                    //이미 키가 있다면
                                    if(friendInfo_object.containsKey("receivedMessage")){
                                        //친구의 정보에 있던 receivedMessage array를 불러온다
//                                    String receivedMessage_string = (String) friendInfo_object.get("receivedMessage");
//                                    JSONArray receivedMessage_arr = (JSONArray) parser.parse(receivedMessage_string);
                                        Object receivedMessage_object = friendInfo_object.get("receivedMessage");
                                        receivedMessage_arr = (JSONArray)receivedMessage_object;
                                        Log.d("메시지", "json 파싱함. receivedMessage_arr에 있는 정보 전부 출력: " + receivedMessage_arr);
                                    }
                                    else{ //없다면 새로 만들어준다
                                        receivedMessage_arr = new JSONArray();
                                    }

                                    //위에서 작성한 메시지(jsonObject)를 추가한다
                                    receivedMessage_arr.add(0, messageInfo);
                                    Log.d("메시지", "json 파싱함. messageInfo에 있는 정보 전부 출력: " + messageInfo);

                                    friendInfo_object.put("receivedMessage", receivedMessage_arr);

                                    //------------안 읽은 메시지 개수 저장
                                    //초기값 설정
                                    int numberOfMessage = 0;

                                    //저장된 값이 있다면 그것을 불러온다
                                    if(friendInfo_object.containsKey("unreadMessage")){
                                        numberOfMessage = ((Long)friendInfo_object.get("unreadMessage")).intValue();
                                        Log.d("알람","안 읽은 메시지 개수 저장) 저장된 값이 있다고 함. numberOfMessage: "+numberOfMessage);
                                    }

                                    //새로운 값(+1)을 넣어준다
                                    friendInfo_object.put("unreadMessage", ++numberOfMessage);
                                    Log.d("알람","+1 해줌. numberOfMessage: "+numberOfMessage);

                                    //------------------------------------


                                    //바뀐 객체를 SharedPreferences 에 저장한다
                                    editor.putString(friendsEmail, friendInfo_object.toString());
                                    editor.commit();

                                    Log.d("메시지", "json 파싱함. friendInfo_object에 있는 정보 전부 출력: " + friendInfo_object);

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
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();


            }
        });
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

    private String getCurrentUser(){
        SharedPreferences infoPref = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        return infoPref.getString("currentUser","유저정보없음");
    }

    public String getData(String key){

        String data = "데이터 없음";

        //친구의 email(회원 식별자)를 불러온다
        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);

        //Preference에서 친구의 정보(string 형태로 변환된 json객체)를 불러온다
        String userInfo = sp.getString(friendsEmail, "");
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

    //저장소에서 프로필 사진 경로를 불러옴 -> 화면에 표시
    private void setProfileImage(){

        ImageView profileImage = (ImageView)findViewById(R.id.firnedsProfile_ImageView);

        try {
            String imagePath = getData("profileImagePath");

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
                    profileImage.setImageBitmap(rotatedBitmap);
                }
            }
        }catch(Exception e){

            Log.d("마이페이지","onCreate(), 사진없음");
        }
    }

    private Bitmap rotateImage(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source,0,0,source.getWidth(), source.getHeight(),matrix,true);
    }

    private void setUsername(){
        TextView username_textView = (TextView)findViewById(R.id.friendsUsername_textView);
        username_textView.setText(friendsUsername);
    }


    //현재 날짜, 시간을 구하기
    public String getCurrentTime(){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd aaa hh:mm");
        String date = format.format(System.currentTimeMillis());
        return date;
    }

    //두 날짜 사이의 차이 계산
    public int calDateBetweenDates(String startDate){

        int result = 0;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date formattedStartDate = format.parse(startDate);
            Date formattedEndDate = format.parse(getCurrentTime());

            long calDate = formattedStartDate.getTime() - formattedEndDate.getTime();
            long days = Math.abs(calDate / (24 * 60 * 60 * 1000));
            result = (int)days;

        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("날짜계산 오류",ex);
        }

        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Main.isFreindMode = false;
    }
}
