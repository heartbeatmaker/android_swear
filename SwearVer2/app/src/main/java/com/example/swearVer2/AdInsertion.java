package com.example.swearVer2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdInsertion extends AppCompatActivity {



    protected String adContent() {

        List<String> rec = new ArrayList<String>();
        rec.add("아직도 혼자 지하철 타고 등산 가십니까? <등산가자>로 편안하게 자가용 타고 등산 가세요.");
        rec.add("당신이 흥얼거리는 그 멜로디, 내일의 히트곡이 될 수도 있습니다. <뮤직 디자이너>로 손 쉽게 작곡하세요.");
        rec.add("아직도 혼자 방탈출하러 가십니까? <이스케이프 룸 메이트>로 다른 사람과 함께 즐기세요.");
        rec.add("아직도 일요일에 약국 찾아 뛰어다니십니까? <당번 약국>으로 쉽게 찾으세요.");
        rec.add("아직도 보드게임 같이 할 친구를 찾아 헤메십니까? <테라포밍 마스>로 어디서나 1인 플레이를 즐겨보세요.");
        int ran = (int)(Math.random()*4+1);

        return rec.get(ran);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_insertion);


        TextView ranAd = (TextView)findViewById(R.id.adtext);
        ranAd.setText(adContent());

        ImageButton b = (ImageButton) findViewById(R.id.cancelbtn); //x버튼 누르면 해당 액티비티 사라짐
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                finish();
            }

        });

    }
}