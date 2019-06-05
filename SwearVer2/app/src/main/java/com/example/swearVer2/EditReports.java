package com.example.swearVer2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditReports extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reports);


        //리사이클러뷰 아이템에서 제목, 위치값(position) 받아옴
        Intent t = getIntent();

        final EditText title = (EditText)findViewById(R.id.reportTitle);
        String rTitle = t.getStringExtra("title");
        final int rPosition = t.getIntExtra("itemPosition", 10000);
        title.setText(rTitle);


        Button confirm = (Button)findViewById(R.id.EditDone_btn);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //리사이클러뷰 아이템으로 '제목' 보내기
                Intent intent = new Intent();

                intent.putExtra("editReportTitle", title.getText().toString());
                intent.putExtra("position",rPosition);
                setResult(RESULT_OK, intent);

                finish();
            }
        });


        Button cancel = (Button)findViewById(R.id.CancelEdit_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
