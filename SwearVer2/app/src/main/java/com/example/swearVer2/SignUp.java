package com.example.swearVer2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Map;

public class SignUp extends AppCompatActivity {

    public static String userInfoPreference = "userInfo";

    String username, email, password;

    EditText username_editText, email_editText, password_editText;
    TextView verifyMessage_textView;

    private boolean isEmailVerified;

    public void setData(String key, String newValue){

        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String currentUser = sp.getString("currentUser","현재사용자 불러오기 실패");
        String userInfo = sp.getString(currentUser, "");

        JSONParser parser = new JSONParser();
        try {
            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) parser.parse(userInfo);

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


    private void isLogin(){
        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);

        //로그인상태가 true이면 -> 바로 메인화면으로 전환
        if(sp.getBoolean("isLogin",false)){
            Intent intent = new Intent(getApplicationContext(), Main.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        //로그인상태가 true이면 -> 바로 메인화면으로 전환
        isLogin();


        //입력창
        username_editText = (EditText)findViewById(R.id.signup_username_editText);
        email_editText = (EditText)findViewById(R.id.signup_email_editText);
        password_editText = (EditText)findViewById(R.id.signup_password_editText);
        verifyMessage_textView = (TextView)findViewById(R.id.signup_verify_textView);
        Button verifyEmail_btn = (Button)findViewById(R.id.signup_verify_btn);

        username = username_editText.getText().toString();
        email = email_editText.getText().toString();
        password = password_editText.getText().toString();



        verifyEmail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email_editText = (EditText)findViewById(R.id.signup_email_editText);
                verifyEmail(email_editText.getText().toString()); //이메일 중복검사를 한다
                Log.d("회원가입", "이메일 중복검사 끝남");
            }
        });

        //로그인 버튼을 누르면, 회원가입이 완료된다. 메인화면이 뜬다
        //텍스트뷰지만 버튼 역할을 함
        TextView login = (TextView)findViewById(R.id.signup_cofirm_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("회원가입", "로그인 버튼 누름");
                Log.d("회원가입", "회원가입 조건을 충족하는지 확인하겠음");
                Log.d("회원가입", "isContentFilled():"+isContentFilled());
                Log.d("회원가입", "isEmailVerified:"+isEmailVerified);
                Log.d("회원가입", "isEmailChanged()"+isEmailChanged());

                //모든 칸을 채웠고, 이메일 중복검사를 마쳤고, 최초의 중복검사 후에 이메일을 바꿔쓰지 않았다면
                // -> 회원가입에 성공

                if(isContentFilled()){
                    if(isEmailVerified && !isEmailChanged()){

                        Log.d("회원가입", "회원가입의 모든 조건을 충족한다고 함. 정보 저장만 남았음");
                        Log.d("회원가입", "isContentFilled():"+isContentFilled());
                        Log.d("회원가입", "isEmailVerified:"+isEmailVerified);
                        Log.d("회원가입", "isEmailChanged()"+isEmailChanged());

                        //입력한 정보를 저장한다
                        try {
                            saveUserInfo();
                        } catch (Exception e) {

                        }
                        Toast.makeText(SignUp.this, ("가입을 축하드립니다. 10000포인트가 지급되었습니다."), Toast.LENGTH_SHORT).show();



                        //현재 로그인 한 사용자가 누구인지 저장(식별자 = email)
                        //이 key는 로그아웃 할 때 삭제해야함
                        SharedPreferences sspp = getSharedPreferences(userInfoPreference, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sspp.edit();
                        editor.putString("currentUser", email_editText.getText().toString());

                        //자동로그인을 위한 플래그
                        editor.putBoolean("isLogin", true);
                        editor.commit();

                        Intent intent = new Intent(getApplicationContext(), Main.class);
                        startActivity(intent);
                        finish();

                    }
                    else {

                        Log.d("회원가입", "이메일 중복검사;isEmailVerified;가 false라고 함.");
                        Toast.makeText(SignUp.this, "이메일 중복검사가 필요합니다.", Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });


        //'이미 계정이 있습니다'를 클릭하면, 로그인 화면이 뜬다
        TextView tv = (TextView)findViewById(R.id.signup_alreadyHaveAccount_textView);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
            }
        });

    }


    private boolean isContentFilled(){
        Log.d("회원가입", "빈 칸 있는지 확인하러 들어옴");
        Log.d("회원가입", "지금 입력되어 있는 값을 확인해보자");
        Log.d("회원가입", "username_editText.getText().length(): "+username_editText.getText().length());
        Log.d("회원가입", "email_editText.getText().length():"+email_editText.getText().length());
        Log.d("회원가입", "password_editText.getText().length():"+password_editText.getText().length());

        //입력창에서 빈칸이 있으면 false
        if(username_editText.getText().length()>0 && email_editText.getText().length()>0 && password_editText.getText().length()>0){
            Log.d("회원가입", "빈 칸이 없음");
            return true;
        }
        else{
            Toast.makeText(this, "입력하지 않은 칸이 있습니다.", Toast.LENGTH_SHORT).show();
            Log.d("회원가입", "빈 칸이 있음");
            return false;
        }
    }


    //중복확인 후에 이메일을 바꿔 썼는지 확인
    private boolean isEmailChanged(){
        Log.d("회원가입", "isEmailChanged() 들어옴");

        SharedPreferences sp = getSharedPreferences(userInfoPreference, MODE_PRIVATE);

        Map<String, ?> allEntries = sp.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("회원가입", "preference에 있는 데이터 전부출력: "+entry.getKey() + ": " + entry.getValue().toString());
        }

        //바꿔썼으면
        //아래의 값 비교할 때 == 쓰면 안 됨!!!!
        if(!String.valueOf(sp.getString("firstEmail","")).equals(email_editText.getText().toString())){
            Log.d("회원가입", "처음 쓴 이메일과 지금의 이메일이 일치하지 않는다고 함. firstEmail: "+sp.getString("firstEmail","값 못받음"));
            Log.d("회원가입", "지금 입력되어 있는 값: "+email_editText.getText().toString());

            Toast.makeText(this, "이메일 중복검사가 다시 필요합니다.", Toast.LENGTH_SHORT).show();
            isEmailVerified = false;
            Log.d("회원가입", "isEmailVerified = false로 바꿈. 진짠지 확인 isEmailVerified="+isEmailVerified);

            return true;
        }
        else
            Log.d("회원가입", "처음 쓴 이메일과 지금의 이메일이 일치한다고 함. firstEmail: "+sp.getString("firstEmail","값 못받음"));
            return false;
    }

    private void verifyEmail(String email){
        SharedPreferences sp = getSharedPreferences(userInfoPreference, MODE_PRIVATE);

        Log.d("회원가입", "이메일 중복검사 하러 들어옴");
        Log.d("회원가입", "들어온 email 값:"+email);

        if(email_editText.getText().length()>0) {


            Map<String, ?> allEntries = sp.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d("회원가입", "preference에 있는 데이터 전부출력: "+entry.getKey() + ": " + entry.getValue().toString());
            }

            //email = sharedPreference의 key
            if (sp.contains(email)) { //저장소 안에 해당 키가 이미 있으면

                Log.d("회원가입", "저장소 안에 해당 키가 이미 있다고 함");

                verifyMessage_textView.setTextColor(Color.parseColor("#DC143C")); //빨간색 글씨
                verifyMessage_textView.setText("이미 가입된 이메일입니다.");
                isEmailVerified = false;
                Log.d("회원가입", "isEmailVerified: "+isEmailVerified);

            } else if(!sp.contains(email)){// 없으면

                Log.d("회원가입", "저장소 안에 해당 키가 없다고 함");

                verifyMessage_textView.setTextColor(Color.parseColor("#000000"));
                verifyMessage_textView.setText("사용 가능한 이메일입니다.");
                isEmailVerified = true;
                Log.d("회원가입", "isEmailVerified: "+isEmailVerified);

                SharedPreferences sspp = getSharedPreferences(userInfoPreference, MODE_PRIVATE);
                SharedPreferences.Editor editor = sspp.edit();
                if(sp.contains("firstEmail")){

                    Log.d("회원가입", "원래 있던 firstEmail값: "+sp.getString("firstEmail","값 못받음"));
                    editor.remove("firstEmail");
                    editor.commit();
                    Log.d("회원가입", "해당 값을 지움. 현재 firstEmail값: "+sp.getString("firstEmail","잘 지워졌음"));
                }

                editor.putString("firstEmail", String.valueOf(email));
                editor.commit();
                Log.d("회원가입", "수정한 firstEmail값: "+sp.getString("firstEmail","값 못받음"));
            }
        }
        else{
            verifyMessage_textView.setTextColor(Color.parseColor("#000000"));
            verifyMessage_textView.setText("이메일을 입력하십시오.");
        }
    }


    //사용자가 입력한 정보를 저장한다
    public void saveUserInfo() throws JSONException {

        SharedPreferences sp = getSharedPreferences(userInfoPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        JSONObject userInfo = new JSONObject();

        userInfo.put("username", username_editText.getText().toString());
        userInfo.put("password", password_editText.getText().toString());
        userInfo.put("point",String.valueOf(10000)); //가입과 동시에 10000포인트 지급

//        JSONArray arr = new JSONArray();
//        userInfo.put("receivedMessage", arr); //받은메시지를 저장할 jsonArrayList를 미리 지정해놓는다

        //사용자 정보를 저장
        //key = 이메일, value = 닉네임/비밀번호

        editor.putString(email_editText.getText().toString(), userInfo.toString());
        editor.commit();
    }


}
