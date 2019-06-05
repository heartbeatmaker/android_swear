package com.example.swearVer2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LockScreen extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED //안드로이드 잠금화면보다 위
//                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); //안드로이드 잠금화면 없애기
//
//        ImageButton b = (ImageButton) findViewById(R.id.????); //해제하기 누르면 해당 액티비티 사라짐 -- 새로만들기
//        b.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
//                finish();
//            }
//
//        });

    }
}