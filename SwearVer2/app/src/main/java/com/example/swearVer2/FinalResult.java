package com.example.swearVer2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.util.Map;

public class FinalResult extends AppCompatActivity {

    String preferenceName;

    RatingBar sincerity_ratingBar, realistic_ratingBar, cheer_ratingBar, improvement_ratingBar;
    float sincerity_rating, realistic_rating, cheer_rating, improvement_rating;

    public String getData(String key){

        String data = "데이터 없음";

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
            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) parser.parse(userInfo);

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


    //저장소에서 프로필 사진 경로를 불러옴 -> 화면에 표시
    private void setProfileImage(){

        ImageView profileImage = (ImageView)findViewById(R.id.finalReport_profile_imageView);

        try {
            String imagePath = getData("profileImagePath");

            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                if (myBitmap != null) {
                    ExifInterface ei = new ExifInterface(imagePath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap rotatedBitmap = null;
                    switch(orientation){
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(myBitmap,90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(myBitmap,180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(myBitmap,270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = myBitmap;
                    }
                    profileImage.setImageBitmap(rotatedBitmap);
                }
            }
        }catch(Exception e){

            Log.d("마이페이지","onCreate(), 사진없음");
        }
    }

    private Bitmap rotateImage(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source,0,0,source.getWidth(), source.getHeight(),matrix,true);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_result);

        //상단에 사용자의 프로필 이미지 넣기
        setProfileImage();

        TextView title = (TextView)findViewById(R.id.textView24);
        TextView period = (TextView)findViewById(R.id.textView25);
        sincerity_ratingBar = (RatingBar)findViewById(R.id.finalResult_ratingBar_sincerity);
        realistic_ratingBar = (RatingBar)findViewById(R.id.finalResult_ratingBar_realistic);
        cheer_ratingBar = (RatingBar)findViewById(R.id.finalResult_ratingBar_cheer);
        improvement_ratingBar = (RatingBar)findViewById(R.id.finalResult_ratingBar_improvement);

        //이전 화면에서 intent로 넘겨준 값을 받아 화면에 표시함
        Intent intent = getIntent();

        preferenceName = intent.getStringExtra("goalPreferenceName");

        String t = intent.getStringExtra("goalTitle");
        title.setText(t);

        String receivedPeriod = intent.getStringExtra("goalPeriod");
        period.setText(receivedPeriod);

        sincerity_ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                TextView sincerity_textView = (TextView)findViewById(R.id.finalResult_ratingBar_sincerity_textView);
                sincerity_textView.setText(String.valueOf(rating));
                sincerity_rating = rating;
            }
        });


        realistic_ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                TextView realistic_textView = (TextView)findViewById(R.id.finalResult_ratingBar_realistic_textView);
                realistic_textView.setText(String.valueOf(rating));
                realistic_rating = rating;
            }
        });

        cheer_ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                TextView cheer_textView = (TextView)findViewById(R.id.finalResult_ratingBar_cheer_textView);
                cheer_textView.setText(String.valueOf(rating));
                cheer_rating = rating;
            }
        });

        improvement_ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                TextView improvement_textView = (TextView)findViewById(R.id.finalResult_ratingBar_improvement_textView);
                improvement_textView.setText(String.valueOf(rating));
                improvement_rating = rating;
            }
        });


        //성공 눌렀을 때 -> 확인 다이얼로그 띄워야함 -> 그 다음에 값 저장
        Button succeeded_btn = (Button)findViewById(R.id.finalResult_succeeded_btn);
        succeeded_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){

                if(!isContentNull()){ //빈 칸이 있는지 확인

                    showDialogAtSucceededBtn();
                }

            }

        });

        //실패 눌렀을 때 -> 그대로 finish()
        Button failed_btn = (Button)findViewById(R.id.finalResult_failed_btn);
        failed_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){

                if(!isContentNull()){ //빈 칸이 있는지 확인

                    showDialogAtFailedBtn();

                }

            }

        });

    }


    private void showDialogAtSucceededBtn(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(FinalResult.this);
        builder.setTitle("결과 확정");

        builder.setMessage("최종 결과를 '성공'으로 확정하시겠습니까?")
                .setCancelable(false).setPositiveButton("확정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //---확정 버튼을 눌렀을 때 일어나는 일---//

                //성공여부를 저장한다
                setString("SuccessOrFailure", "성공");

                //별점을 저장한다
                setFloatToString("sincerity_rating", sincerity_rating);
                setFloatToString("realistic_rating", realistic_rating);
                setFloatToString("cheer_rating", cheer_rating);
                setFloatToString("improvement_rating", improvement_rating);

                //보증금을 돌려준다
                SharedPreferences sp = getSharedPreferences(preferenceName, MODE_PRIVATE);
                int goalDeposit = sp.getInt("goalDeposit", 0);
                int userPoint = Integer.parseInt(getData("point")) + goalDeposit; //현재 보유한 포인트에 보증금을 합침
                setData("point", String.valueOf(userPoint)); //업데이트된 포인트를 저장

                Toast.makeText(FinalResult.this, "축하합니다! 보증금 "+goalDeposit+"P가 반환되었습니다.", Toast.LENGTH_SHORT).show();


                //이 화면을 종료한다
                FinalResult.this.finish();
                dialog.dismiss();
            }
        })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //---취소 버튼을 눌렀을 때 일어나는 일--//

                        dialog.dismiss();
                    }
                });

        //대화상자를 만들고, 띄워주기
        AlertDialog dialog = builder. create();
        dialog.show();

    }

    private void showDialogAtFailedBtn(){
        AlertDialog.Builder builder = new AlertDialog.Builder(FinalResult.this);
        builder.setTitle("결과 확정");

        builder.setMessage("최종 결과를 '실패'로 확정하시겠습니까?")
                .setCancelable(false).setPositiveButton("확정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //성공여부를 저장한다
                setString("SuccessOrFailure", "실패");

                //별점을 저장한다
                setFloatToString("sincerity_rating", sincerity_rating);
                setFloatToString("realistic_rating", realistic_rating);
                setFloatToString("cheer_rating", cheer_rating);
                setFloatToString("improvement_rating", improvement_rating);

                Toast.makeText(FinalResult.this, "최종 결과가 확정되었습니다.", Toast.LENGTH_SHORT).show();

                //이 화면을 종료한다
                FinalResult.this.finish();
                dialog.dismiss();
            }
        })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //이 화면을 종료한다
                        dialog.dismiss();
                    }
                });

        //대화상자를 만들고, 띄워주기
        AlertDialog dialog = builder. create();
        dialog.show();

    }

    private void showDialogAfterPressingSucceededBtn(){
        AlertDialog.Builder builder = new AlertDialog.Builder(FinalResult.this);
        builder.setTitle("안내");

        builder.setMessage("최종 결과를 '실패'로 확정하시겠습니까?")
                .setCancelable(true);

        //대화상자를 만들고, 띄워주기
        AlertDialog dialog = builder. create();
        dialog.show();
    }


    //별점중에 입력하지 않은 칸이 있는지 확인한다
    private boolean isContentNull(){
        Log.d("최종결과", "isContentNull 밖");
        if(sincerity_rating > 0.0f && realistic_rating > 0.0f && cheer_rating > 0.0f && improvement_rating > 0.0f){
            Log.d("최종결과", "isContentNull 안(조건충족");
            return false;
        }else{
            Log.d("최종결과", "isContentNull 안(조건 미충족");
            Toast.makeText(this, "별점을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
            return true;
        }
    }


    //해당 약속의 sharedPreference에 값을 저장하는 함수
    private void setFloatToString(String key, float value){
        SharedPreferences sp = getSharedPreferences(preferenceName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, String.valueOf(value));
        editor.commit();
    }

    private void setString(String key, String value){
        SharedPreferences sp = getSharedPreferences(preferenceName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

}
