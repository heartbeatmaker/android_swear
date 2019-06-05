package com.example.swearVer2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.simple.parser.JSONParser;

import org.json.JSONException;
import org.json.simple.*;
import org.json.simple.parser.ParseException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

public class SignIn extends AppCompatActivity {

    public static String shared_db = "shared_db";

    EditText email_editText;
    EditText password_editText;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);



        //이메일, 비밀번호 입력하는 editbox
        email_editText = (EditText)findViewById(R.id.editText3);
        password_editText = (EditText)findViewById(R.id.password);


        TextView text = (TextView)findViewById(R.id.signupbtn); //회원가입 버튼 누르면 화면전환
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });


        //로그인 버튼 누르면 -> 로그인 성공 시 메인으로 이동
        TextView tv = (TextView)findViewById(R.id.loginbtn);
        tv.setOnClickListener(new View.OnClickListener() { //로그인 버튼 누르면 메인으로 이동
            @Override
            public void onClick(View v) {
                Log.d("로그인", "로그인 버튼 클릭함");

                Log.d("로그인", "로그인 조건을 충족하는지 검사를 하겠음");
                Log.d("로그인", "isContentNull(): "+isContentNull());
                Log.d("로그인", "isEmailValid():"+isEmailValid());
                try {
                    Log.d("로그인", "isPasswordValid():" + isPasswordValid());
                }catch (Exception e){

                }

                try {
                    if (!isContentNull() && isEmailValid() && isPasswordValid()) { //빈칸이 없고, 이메일이 존재하고, 패스워드가 일치하면

                        Log.d("로그인", "로그인 조건을 충족함. 로그인 성공");

                        //현재 로그인 한 사용자가 누구인지 저장(식별자 = email)
                        //이 key는 로그아웃 할 때 삭제해야함
                        SharedPreferences sspp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sspp.edit();
                        editor.putString("currentUser", email_editText.getText().toString());

                        //자동로그인을 위한 플래그
                        editor.putBoolean("isLogin", true);
                        editor.commit();


                        Intent intent = new Intent(getApplicationContext(), Main.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(SignIn.this, ("로그인 성공! 환영합니다"), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.d("로그인", "오류남");
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String ex = sw.toString();

                    Log.d("로그인", ex);
                }

            }
        });

    }


    private boolean isEmailValid(){
        Log.d("로그인", "isEmailValid() 들어옴. 존재하는 이메일인지 확인한다");

        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);

        Map<String, ?> allEntries = sp.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("로그인", "preference에 있는 데이터 전부출력. "+entry.getKey() + ": " + entry.getValue().toString());
        }

        if(sp.contains(email_editText.getText().toString())){
            Log.d("로그인", "해당 이메일이 저장소에 존재한다고 함. valid");

            return true;
        }
        else {
            Log.d("로그인", "해당 이메일이 저장소에 존재하지 않는다고 함. invalid");
            Toast.makeText(this, "존재하지 않는 이메일입니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }



    private boolean isPasswordValid() throws JSONException, ParseException {
        Log.d("로그인", "isPasswordValid() 들어옴. 패스워드가 일치하는지 확인한다");

        String email = email_editText.getText().toString();
        Log.d("로그인", "입력한 email: "+email);

        //Preference에서 email(회원 식별자)에 해당하는 값(string 형태로 변환된 json객체)을 불러온다
        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        String userInfo = sp.getString(email, "");

        Log.d("로그인", "json 파싱함. userInfo에 있는 정보 전부 출력: "+userInfo);

        //파싱한다
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject)parser.parse(userInfo);
        Log.d("로그인", "json 파싱함. object에 있는 정보 전부 출력: "+jsonObject);

        //저장되어 있는 패스워드와 현재 입력한 패스워드가 일치하는지 확인한다
        Log.d("로그인", "저장되어 있는 패스워드와 현재 입력한 패스워드가 일치하는지 확인한다");

        String password = password_editText.getText().toString();
        Log.d("로그인", "입력한 패스워드: "+ password);

        if(jsonObject.get("password").equals(password)){
            Log.d("로그인", "패스워드가 일치한다고 함");
            return true;
        }
        else
            Log.d("로그인", "패스워드가 일치하지 않는다고 함");
            Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
            return false;
    }


    //입력하지 않은 칸이 있는지 확인
    private boolean isContentNull(){
        Log.d("로그인", "isContentNull() 들어옴. 입력하지 않은 칸이 있는지 검사한다");

        Log.d("로그인", "email_editText.getText().length(): "+email_editText.getText().length());
        Log.d("로그인", "password_editText.getText().length(): "+password_editText.getText().length());

        if(email_editText.getText().length()>0 && password_editText.getText().length()>0){
            Log.d("로그인", "빈칸이 없다고 함");

            return false;
        }
        else {
            Log.d("로그인", "빈칸이 있다고 함");
            Toast.makeText(SignIn.this, "이메일과 패스워드를 모두 입력하십시오", Toast.LENGTH_SHORT).show();
            return true;
        }
    }


    //데이터 저장
    public void setPreferenceString(String key, String value){
        SharedPreferences sp = getSharedPreferences(shared_db, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void setPreferenceBoolean(String key, Boolean value){
        SharedPreferences sp = getSharedPreferences(shared_db, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


    //데이터 불러오기
    public String getPreferenceString(String key){
        SharedPreferences sp = getSharedPreferences(shared_db, MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public Boolean getPreferenceBoolean(String key){
        SharedPreferences sp = getSharedPreferences(shared_db, MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }



    @Override
    protected void onResume() {
        super.onResume();

    }


//    public void isChecked(boolean check){
//        isChecked = check;
//    }

    private void switchAction(){

        //        //스위치 버튼
//        Switch sw = (Switch)findViewById(R.id.switch2);
//
//        isChecked = getPreferenceBoolean("SignIn_isChecked"); //저장된 스위치 on/off상태 불러옴
//        sw.setChecked(isChecked); //스위치 on/off 초기상태 지정
//
//        //스위치가 on이면 저장한 이메일, 비밀번호를 editbot에 적어줌
//        if(isChecked){
//            email_editText.setText(getPreferenceString("email"));
//            password_editText.setText(getPreferenceString("password"));
//        }
//
//        //스위치 클릭 리스너
//        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                //스위치 ON/OFF에 따라 boolean값 변화
//              if (isChecked) {
//                  isChecked(true);
//              }else{
//                  isChecked(false);
//              }
//
//            }
//        });
    }


    //화면이 정지되면, 입력된 데이터를 저장함
    @Override
    protected void onPause() {
        super.onPause();

//        setPreferenceBoolean("SignIn_isChecked", isChecked); //스위치 on/off여부 저장
//
//        //스위치가 on이면 아이디, 비밀번호를 저장
//        if(isChecked){
//            String email= email_editText.getText().toString();
//            String password= password_editText.getText().toString();
//
//            setPreferenceString("email", email);
//            setPreferenceString("password", password);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();

//        if (isChecked) {
//            final EditText password = (EditText) findViewById(R.id.password); //패스워드 초기화
//            password.setHint("초기화 되었습니다.");
//            password.setText("");
//        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}


