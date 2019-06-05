package com.example.swearVer2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class Intro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ImageView home = (ImageView) findViewById(R.id.imageView);
        home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            finish();

            }

        });


    }
}
