package com.example.swearVer2;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Map;


public class UserPoint extends AppCompatActivity {

    TextView curPoint_textView;
    EditText toppingPoint_editText;

    public String getData(String key){

        String data = "100";

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_point);

        curPoint_textView = (TextView)findViewById(R.id.currentPoint_textView);
        toppingPoint_editText = (EditText)findViewById(R.id.top_point_editText);

        try {

            curPoint_textView.setText(getData("point"));
        }catch(Exception e){

        }


        Button payBtn = (Button)findViewById(R.id.pay_btn);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (toppingPoint_editText.getText().length() > 0) {

                    //사용자가 입력한 금액
                    int toppingPoint = Integer.parseInt(toppingPoint_editText.getText().toString());
                    Log.d("마이페이지", "toppingPoint: " + toppingPoint);

                    //기존 포인트에 입력한 금액만큼 합친다
                    int currentPoint = Integer.parseInt(curPoint_textView.getText().toString()) + toppingPoint;
                    Log.d("마이페이지", "Integer.parseInt(curPoint_textView.getText().toString()): " + Integer.parseInt(curPoint_textView.getText().toString()));

                    setData("point", String.valueOf(currentPoint));

                    Log.d("마이페이지", "currentPoint: " + currentPoint);
                    //바뀐 금액을 화면에 표시한다
                    curPoint_textView.setText(String.valueOf(currentPoint));

                    Toast.makeText(UserPoint.this, toppingPoint+"P가 충전되었습니다.", Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(UserPoint.this, "충전할 금액을 입력하십시오.", Toast.LENGTH_SHORT).show();
                }



            }

        });
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}


