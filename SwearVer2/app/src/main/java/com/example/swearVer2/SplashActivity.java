package com.example.swearVer2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, SignUp.class);
        intent.putExtra("state", "launch");
        startActivity(intent);
        finish();
    }
}
