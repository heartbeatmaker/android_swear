package com.example.swearVer2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class CustomerService extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service);

        Button cs = (Button) findViewById(R.id.button7);
        cs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse("mailto:swear.app.cs@gmail.com");
                Intent it = new Intent(Intent.ACTION_SENDTO, uri);

                if(it.resolveActivity(getPackageManager())==null){
                    Toast.makeText(CustomerService.this, "고객님의 휴대폰에 이메일을 보낼 수 있는 앱이 없습니다. " +
                            "swear.app.cs@gmail.com으로 문의메일을 보내주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(it);
                }

            }

        });


        //상단 로고 누르면 액티비티 종료
        ImageView home = (ImageView) findViewById(R.id.imageView8);
        home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();

            }

        });


    }
}
