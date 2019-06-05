package com.example.swearVer2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class PickUserInterest extends AppCompatActivity {


    public void userInterest(){




    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_user_interest);



        Button career = (Button) findViewById(R.id.career);
        Button edu = (Button) findViewById(R.id.edu);
        Button exercise = (Button) findViewById(R.id.excer);
        Button relationship = (Button) findViewById(R.id.relationship);
        Button environ = (Button) findViewById(R.id.environ);
        Button health = (Button) findViewById(R.id.health);
        Button money = (Button) findViewById(R.id.nav_money);
        Button music = (Button) findViewById(R.id.music);
        Button recreation = (Button) findViewById(R.id.recreation);


//        final ArrayList<String> list = new ArrayList<String>();
//
//        String input;
//
//        public String input()
//
//            for (int i=0; i<list.size(); i++){
//             input = (list.get(i)+", ");
//            }
//
//            return input;
//        }
//
//
//        final TextView textView =findViewById(R.id.textView60);
//
//        career.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick (View view){
//                list.add("커리어");
//                textView.setText(userInput());
//            }
//        });



        Button b = (Button) findViewById(R.id.confirm_interest); //확인 누르면 메인으로 전환
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                    finish();
            }

        });


        Button cancel = (Button) findViewById(R.id.cancel); //확인 누르면 메인으로 전환
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }

        });



    }
}
