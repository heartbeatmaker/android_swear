package com.example.swearVer2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class CheckGoals extends AppCompatActivity {


public long period;

private String receivedPreferenceName;
TextView title_textView, description_textView, startDate_textView, endDate_textView, numberOfDays_textView,
        deposit_textView, secretMode_textView, contractDate_textView, goalProgress_textView, goalProgress_title_textView;
TextView finalResult_title_textView, rating_sincerity, rating_realistic, rating_cheer, rating_improvement;
String goalPeriod, endDate;
Button setFinalResult_btn;

TableLayout tableLayout_finalResult;
ToggleButton setSecretMode_toggleBtn;


    public String getData(String key){

        String data = "데이터 없음";

        //현재 사용자의 email(회원 식별자)를 불러온다
        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        String currentUser = sp.getString("currentUser","현재사용자 불러오기 실패");

        //Preference에서 현재 사용자의 정보(string 형태로 변환된 json객체)를 불러온다
        String userInfo = sp.getString(currentUser, "");
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

    public void setData(String key, String newValue){

        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String currentUser = sp.getString("currentUser","현재사용자 불러오기 실패");
        String userInfo = sp.getString(currentUser, "");

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(userInfo);

            //기존에 해당 키가 있으면 삭제한다
            if(jsonObject.containsKey(key)){
                jsonObject.remove(key);
            }

            //새로운 값을 넣어준다
            jsonObject.put(key, newValue);

            //값을 string 형으로 변환한 후, sharedPreference에 저장한다
            editor.putString(currentUser, jsonObject.toString());
            Log.d("마이페이지", "닉네임 변경함. object에 있는 정보 전부 출력: " + jsonObject);

            editor.commit();

            Map<String, ?> allEntries = sp.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d("마이페이지", "preference에 있는 데이터 전부출력: "+entry.getKey() + ": " + entry.getValue().toString());
            }



        }catch(Exception e){

        }
    }

    private void setUsername(){
        TextView username_textView = (TextView) findViewById(R.id.checkGoals_setScreenTitle);

        if(Main.isFreindMode){
            username_textView.setText("약속 확인");
        }else {
            username_textView.setText(getData("username") + "의 약속");
        }
    }

    private String getCurrentUser(){
        SharedPreferences infoPref = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        return infoPref.getString("currentUser","유저정보없음");
    }

    //현재 날짜를 구하기
    public String getCurrentTime(){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(System.currentTimeMillis());
        return date;
    }

    //두 날짜 사이의 차이 계산
    public boolean isTheGoalExpired(){

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date formattedEndDate = format.parse(endDate);
            Date formattedToday = format.parse(getCurrentTime());

            //현재 날짜에서 종료일을 뺀다
            long calDate = formattedToday.getTime() - formattedEndDate.getTime();
            long days = calDate / (24 * 60 * 60 * 1000);
            int result = (int)days;

            //현재날짜가 종료일과 같거나, 날짜가 지났으면 -> true를 반환한다
            if(result >= 0){
                return true;
            }

        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("날짜계산 오류",ex);
        }

        return false;
    }


    public void setNumberOfUnreadAlarmMessage(){
        Log.d("알람","CheckGoals) setNumberOfUnreadAlarmMessage 들어옴");

        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String currentUser = sp.getString("currentUser","현재사용자 불러오기 실패");
        String userInfo = sp.getString(currentUser, "");

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(userInfo);

            //초기값 설정
            int numberOfMessage = 0;

            //저장된 값을 불러온다
            numberOfMessage = ((Long)jsonObject.get("unreadAlarmMessage")).intValue();
            Log.d("알람","저장된 unreadAlarmMessage: "+numberOfMessage);

            //@@@@@@오류발견
            //메시지 확인창 - isClicked는 공존할 수 없음
            //메인화면의 메시지 확인창을 누르면 -> 안 읽은 메시지가 0이 된다
            //그런데 이 때 노티피케이션 바에 알림메시지가 남아있을 경우, 그 메시지를 눌러 이 화면에 들어오면 -> 또 -1이 된다
            if(numberOfMessage > 0){
                //새로운 값(-1)을 넣어준다: 읽었으므로 메시지 개수에서 1을 뺀다
                jsonObject.put("unreadAlarmMessage", --numberOfMessage);
                Log.d("알람","-1 해줌. unreadAlarmMessage: "+numberOfMessage);

                //값을 string 형으로 변환한 후, sharedPreference에 저장한다
                editor.putString(currentUser, jsonObject.toString());
                Log.d("알람", "object에 있는 정보 전부 출력: " + jsonObject);

                editor.commit();
            }

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_goals);

        setUsername();

        //내용을 담을 텍스트뷰를 설정
        title_textView = (TextView)findViewById(R.id.checkGoalTitle_textView);
        description_textView = (TextView)findViewById(R.id.checkGoalDescription_textView);
        startDate_textView = (TextView)findViewById(R.id.checkGoal_startDate_textView);
        endDate_textView = (TextView)findViewById(R.id.checkGoal_endDate_textView);
        numberOfDays_textView = (TextView)findViewById(R.id.checkGoal_numberOfDays_textView);
        deposit_textView = (TextView)findViewById(R.id.checkGoalDeposit_textView);
        contractDate_textView = (TextView)findViewById(R.id.checkGoal_contractDate_textView);
        goalProgress_textView = (TextView)findViewById(R.id.checkGoal_progress_textView);


        //나중에 최종결과를 입력하면 내용이 바뀌는 곳(onResume() 에서)
        goalProgress_title_textView = (TextView)findViewById(R.id.checkGoal_progress_title_textView);
        finalResult_title_textView = (TextView)findViewById(R.id.checkGoal_finalResult_title);
        rating_sincerity = (TextView)findViewById(R.id.checkGoal_rating_sincerity);
        rating_realistic = (TextView)findViewById(R.id.checkGoal_rating_realistic);
        rating_cheer = (TextView)findViewById(R.id.checkGoal_rating_cheer);
        rating_improvement = (TextView)findViewById(R.id.checkGoal_rating_improvement);
        tableLayout_finalResult = (TableLayout)findViewById(R.id.tableLayout_checkGoals_finalResult);
        tableLayout_finalResult.setVisibility(View.GONE);


        //친구가 볼 경우를 대비하여 일단 INVISIBLE로 놓는다
        secretMode_textView = (TextView)findViewById(R.id.checkGoals_secretMode_textView);
        secretMode_textView.setVisibility(View.INVISIBLE);

        setSecretMode_toggleBtn = (ToggleButton)findViewById(R.id.checkGoals_toggleButton);
        setSecretMode_toggleBtn.setVisibility(View.INVISIBLE);
        setSecretMode_toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //사용자가 비밀글 체크했을 때 -> 비밀글 on 상태를 저장
                if(setSecretMode_toggleBtn.isChecked()){
                    setSecretMode_toggleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_lock_black_24dp));
                    secretMode_textView.setText("비밀글 ON");

                    showInfoOfSecretMode();

                    SharedPreferences sp = getSharedPreferences(receivedPreferenceName, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("isLocked", true);
                    editor.commit();

                    Toast.makeText(CheckGoals.this, "비밀글로 설정되었습니다", Toast.LENGTH_SHORT).show();

                }
                //사용자가 비밀글 해제했을 때 -> 비밀글 off 상태를 저장
                else{
                    setSecretMode_toggleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_lock_open_black_24dp));
                    secretMode_textView.setText("비밀글 OFF");

                    SharedPreferences sp = getSharedPreferences(receivedPreferenceName, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("isLocked", false);
                    editor.commit();

                    Toast.makeText(CheckGoals.this, "비밀글이 해제되었습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //푸쉬 알람을 클릭해서 이 화면을 열었다면 -> 해당 메시지를 읽은 것으로 간주한다
        if(getIntent().hasExtra("isClicked")){
            Log.d("알람","checkGoals) 푸쉬 알람을 클릭해서 이 화면을 열었음");
            setNumberOfUnreadAlarmMessage();
        }

        //'약속 새로만들기' 창 or 메인어댑터에서 보낸 메시지(약속내용을 저장한 preference의 이름)를 전달받음
        if(getIntent().hasExtra("preferenceName")){
            receivedPreferenceName = getIntent().getStringExtra("preferenceName");

            try{
                SharedPreferences sp = getSharedPreferences(receivedPreferenceName, MODE_PRIVATE);
                title_textView.setText(sp.getString("goalTitle","제목 없음"));
                description_textView.setText(sp.getString("goalDescription","내용 없음"));
                startDate_textView.setText(sp.getString("goalStartDate","시작일 없음"));
                endDate_textView.setText(sp.getString("goalEndDate","종료일 없음"));
                numberOfDays_textView.setText(String.valueOf(sp.getInt("numberOfDays",1234))+" 일");
                deposit_textView.setText(String.valueOf(sp.getInt("goalDeposit",8282)));
                contractDate_textView.setText(sp.getString("contractDate","체결일 없음"));

                if(!Main.isFreindMode) { //친구가 읽을 때에는 이 버튼이 없어야함

                    secretMode_textView.setVisibility(View.VISIBLE);
                    setSecretMode_toggleBtn.setVisibility(View.VISIBLE);

                    boolean isLocked = sp.getBoolean("isLocked", false);
                    setSecretMode_toggleBtn.setChecked(isLocked);

                    if (isLocked) {
                        setSecretMode_toggleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_lock_black_24dp));
                        secretMode_textView.setText("비밀글 ON");
                    } else {
                        setSecretMode_toggleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_lock_open_black_24dp));
                        secretMode_textView.setText("비밀글 OFF");
                    }
                }

                endDate = sp.getString("goalEndDate","종료일 없음"); //종료일과 현재날짜를 비교할 것임
                goalPeriod = sp.getString("goalStartDate","시작일 없음")+" ~ "
                        +sp.getString("goalEndDate", "종료일 없음");

            }catch(Exception e){
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String ex = sw.toString();

                Log.d("CheckGoals.class 오류",ex);
            }

        }



        //하단 바의 '홈' 아이콘을 누르면 -> 메인 화면으로 전환
        ImageButton b =(ImageButton)findViewById(R.id.imageButton9);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Main.isFreindMode = false;
                Intent intent = new Intent(getApplicationContext(), Main.class);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        //'최종결과 입력하기' 버튼을 누르면 -> 최종결과 보고 화면으로 전환
        setFinalResult_btn = (Button)findViewById(R.id.checkGoals_setFinalResult_btn);
        setFinalResult_btn.setVisibility(View.GONE);

        //오늘이 약속종료일이거나 종료일이 지났으면 && 지금 이 화면을 보는 사람이 본인이 맞으면(친구가 아니면)
        // -> 최종결과 입력버튼 활성화
        if(isTheGoalExpired() && !Main.isFreindMode){
            setFinalResult_btn.setVisibility(View.VISIBLE);
        }

        setFinalResult_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){

                Intent intent = new Intent(getApplicationContext(),FinalResult.class);
                intent.putExtra("goalPreferenceName", receivedPreferenceName);
                intent.putExtra("goalTitle", title_textView.getText().toString());
                intent.putExtra("goalPeriod", goalPeriod);
                intent.putExtra("goalIndex", receivedPreferenceName);
                startActivity(intent);
            }

        });


        //하단 바의 '보고서' 아이콘을 누르면 -> 보고서 목록 화면으로 전환
        ImageButton report_btn = (ImageButton)findViewById(R.id.report_btn);
        report_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(),CheckReports.class);

                intent.putExtra("goalPreferenceName", receivedPreferenceName); //해당 약속이 저장된 xml 이름을 넘김
                intent.putExtra("goalTitle", title_textView.getText().toString());
                intent.putExtra("goalPeriod", goalPeriod);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }

        });


        //화면 상단의 '공유하기' 아이콘을 누르면 -> 휴대폰 내에서 텍스트를 공유 가능한 앱이 뜬다
        // -> 이메일, 카톡 등으로 공유 메시지를 보낼 수 있다
        ImageButton share_btn = (ImageButton)findViewById(R.id.share_btn);

        //지금 이 화면을 보는 사람이 친구라면 -> 공유 버튼 없애기
        if(Main.isFreindMode){
            share_btn.setVisibility(View.GONE);
        }
        share_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String title = title_textView.getText().toString();

                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_SUBJECT, "I need your help with my commitment to "+title);
                share.putExtra(Intent.EXTRA_TEXT, "I just created <"+title
                        +"> Commitment Contract and thinks you would be a great Referee. Find me on Swear. My ID: "
                        +getCurrentUser());
                share.setType("text/plain");
                String share_title = "Share via";
                Intent chooserIntent = Intent.createChooser(share,share_title);

                if(share.resolveActivity(getPackageManager())==null){
                    Toast.makeText(CheckGoals.this, "공유 가능한 앱이 없습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(chooserIntent);
                }
            }

        });

    } // onCreate()



    private void showInfoOfSecretMode(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("비밀글 모드").setMessage("앞으로 다른 사용자에게 이 약속이 공개되지 않습니다.")
                .setCancelable(true);

        //대화상자를 만들고, 띄워주기
        AlertDialog dialog = builder. create();
        dialog.show();
    }



    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = getSharedPreferences(receivedPreferenceName, MODE_PRIVATE);

        //최종결과를 입력했다면, 결과를 표시한다
        //최종결과 입력은 단 한번만 가능하기 때문에, '이 key가 있다 = 약속이행이 끝났다'로 해석 가능
        if(sp.contains("SuccessOrFailure")){
            tableLayout_finalResult.setVisibility(View.VISIBLE);

            //이행 결과를 표시한다(성공 or 실패)
            goalProgress_textView.setText(sp.getString("SuccessOrFailure", "값 못받음"));

            //별점을 표시한다
            rating_sincerity.setText(sp.getString("sincerity_rating","값 못받음")+"점");
            rating_realistic.setText(sp.getString("realistic_rating","값 못받음")+"점");
            rating_cheer.setText(sp.getString("cheer_rating","값 못받음")+"점");
            rating_improvement.setText(sp.getString("improvement_rating","값 못받음")+"점");

            //최종결과 입력버튼 잠금
            setFinalResult_btn.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //이 코드 넣은 이유
        // : notification 메시지를 클릭해서 이 페이지에 들어오면, back key를 눌렀을 때 앱이 꺼짐(메인 화면을 통해서 들어온 것이 아니라서)
        //앱 사용에 어색함이 없도록 메인화면으로 보내줌
        //플래그 넣은 이유: 메인화면의 onCreate()를 막기 위해

        //노티피케이션을 눌러서 이 화면에 들어왔을 때만 해당!!!
        if(getIntent().hasExtra("isClicked")) {
            Intent intent = new Intent(getApplicationContext(), Main.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }


}
