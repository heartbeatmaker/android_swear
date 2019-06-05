package com.example.swearVer2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Search {
    private Context context;

    public Search(Context context){
        this.context = context;
    }

    public void callFunction(final TextView search_result){ //textview를 매개변수로 받음

        final Dialog dlg = new Dialog(context);

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dlg.setContentView(R.layout.activity_search);

        dlg.show();

        final EditText username = (EditText)dlg.findViewById(R.id.editText12);
        final EditText code = (EditText)dlg.findViewById(R.id.editText13);
        final Button ok = (Button)dlg.findViewById(R.id.button3);
        final Button cancel = (Button)dlg.findViewById(R.id.button4);

        ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                SharedPreferences sp = context.getSharedPreferences(SignUp.userInfoPreference, Activity.MODE_PRIVATE);
                String typedEmail = username.getText().toString();
                String typedCode = code.getText().toString();
                if(sp.contains(typedEmail)){

                    SharedPreferences friendsPref = context.getSharedPreferences(typedEmail+";goalList", Activity.MODE_PRIVATE);
                    if(friendsPref.contains(typedCode)){
                        Toast.makeText(context, typedEmail+"의 약속을 열람합니다.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(context, CheckGoals.class);
                        intent.putExtra("preferenceName", typedCode);
                        context.startActivity(intent);
                    }
                    else{
                        Toast.makeText(context, "잘못된 코드입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context, "존재하지 않는 이메일입니다.", Toast.LENGTH_SHORT).show();
                }
                dlg.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dlg.dismiss();
            }
        });




    }


}
