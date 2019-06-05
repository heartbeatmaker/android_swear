package com.example.swearVer2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DeleteDialog extends AppCompatActivity {

    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_dialog);

        text = (TextView)findViewById(R.id.dialog_askAgain_textView);
    }

    public void setDialogMessage(String message){
        text.setText(message);
    }
}
