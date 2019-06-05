package com.example.swearVer2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Setting extends AppCompatActivity {

    String mCurrentPhotoPath;
    ImageView profileImage;
    private static final int PICK_IMAGE_REQUEST = 2;
    static final int REQUEST_TAKE_PHOTO = 3;
    private static final int REQUEST_IMAGE_CROP = 4;
    Bitmap rotatedBitmap;
    Uri photoURI;

    EditText username_editText;

    Uri photoUri, albumUri;


    public String getData(String key){

        String data = "100";

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

    public Boolean getBooleanData(String key){
        boolean data = false;

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
                data = (boolean) jsonObject.get(key);
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
            JSONObject jsonObject = (JSONObject) parser.parse(userInfo);

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

    public void setBooleanData(String key, boolean newValue){
        SharedPreferences sp = getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String currentUser = sp.getString("currentUser","현재사용자 불러오기 실패");
        String userInfo = sp.getString(currentUser, "");

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(userInfo);

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Log.d("마이페이지", "onCreate()");


        profileImage = (ImageView)findViewById(R.id.myPage_profile_imageView);

        //저장소에서 프로필 사진 경로를 불러옴 -> 화면에 표시
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



        username_editText = (EditText)findViewById(R.id.myPage_username_editText);

        //저장소에서 불러온 회원 이름을 화면에 표시한다
        username_editText.setText(getData("username"));

        //닉네임 변경 버튼을 누르면 -> 변경된 닉네임이 저장된다
        Button changeUsername_btn = (Button)findViewById(R.id.myPage_changeUsername_btn);
        changeUsername_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String changedUsername = username_editText.getText().toString();

                //변경한 이름을 저장한다
                setData("username", changedUsername);

                Toast.makeText(Setting.this, "변경되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });


        //포인트 관리창으로 이동
       Button point = (Button)findViewById(R.id.myPage_managePoint_btn);
        point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserPoint.class);
                startActivity(intent);
            }
        });


        //스위치 on/off -> 알림메시지 수신 여부를 결정함
        switchAction();


//        //관심사 선택 버튼 클릭 -> 유저 관심사 선택창으로 전환
//        Button interest = (Button)findViewById(R.id.myPage_pickYourInterest_btn);
//        interest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), PickUserInterest.class);
//                startActivity(intent);
//            }
//        });

    }



    private void switchAction(){

                //스위치 버튼
        Switch sw = (Switch)findViewById(R.id.myPage_receiveNoti_switch);

        boolean isChecked = getBooleanData("isReceivingNoti"); //저장된 스위치 on/off상태 불러옴
        sw.setChecked(isChecked); //스위치 on/off 초기상태 지정

//        if(isChecked){
//            //스위치가 on 일때 발생하는 이벤트
//        }

        //스위치 클릭 리스너
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //스위치 ON/OFF에 따라 boolean값 변화
              if (isChecked) {
                  setBooleanData("isReceivingNoti", true);
                  Toast.makeText(Setting.this, "알림 메시지를 수신합니다.", Toast.LENGTH_SHORT).show();
              }else{
                  setBooleanData("isReceivingNoti", false);
                  Toast.makeText(Setting.this, "알림 메시지를 수신하지 않습니다.", Toast.LENGTH_SHORT).show();
              }

            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //직접 찍은 사진의 경로를 받아옴
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            try { //getBitmap 오류 방지
                File file = new File(mCurrentPhotoPath);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                if (bitmap != null) {
                    ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                    rotatedBitmap = null;
                    switch(orientation){
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(bitmap,90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(bitmap,180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(bitmap,270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = bitmap;
                    }

                    profileImage.setImageBitmap(rotatedBitmap);

                    //사진 경로 저장
                    setData("profileImagePath", mCurrentPhotoPath);
                }
            }catch (IOException ex){

            }
        }

        //갤러리에서 선택한 사진 받아옴
        else if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){

            try{

                File albumFile = null;
                albumFile = createImageFile();
                photoUri = data.getData();
                albumUri = Uri.fromFile(albumFile); // 해당 경로에 저장
                Log.d("오류찾는다","albumUri: "+albumUri);
                cropImage();

                Log.d("오류찾는다","mCurrentPhotoPath: "+mCurrentPhotoPath);

            }catch(Exception e){
                e.printStackTrace();
            }

        }

        //크롭한 사진
        else if(requestCode == REQUEST_IMAGE_CROP && resultCode == RESULT_OK) {

            galleryAddPic();
            profileImage.setImageURI(albumUri);

            //사진 경로 저장
            setData("profileImagePath", mCurrentPhotoPath);
        }
    }



    //이미지파일 만들기
    private File createImageFile() throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, //prefix
                "jpg", //suffix
                storageDir //directory
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image; //이미지 파일을 return
    }



    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            File photoFile = null;

            try{
                photoFile = createImageFile(); //이미지 파일을 생성
            }catch (IOException ex){

            }

            if(photoFile!=null){

                Uri photoURI = FileProvider.getUriForFile(this, "com.example.swearVer2.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    public void cropImage(){

        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoUri, "image/*");
        cropIntent.putExtra("aspectX",0);
        cropIntent.putExtra("aspectY",0);
        cropIntent.putExtra("output", albumUri);
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    private void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        File f= new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }


//    private void cropImage(Uri uri){
//        Intent cropPictureIntent = new Intent("com.android.camera.action.CROP");
//
//        //URI별 권한 - 없으면 access denied 됨
//        cropPictureIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        cropPictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//        cropPictureIntent.setDataAndType(uri, "image/*");
//        cropPictureIntent.putExtra("outputX", 200);
//        cropPictureIntent.putExtra("outputY", 200);
//        cropPictureIntent.putExtra("aspectX",1);
//        cropPictureIntent.putExtra("aspectY", 1);
//        cropPictureIntent.putExtra("scale",true);
//        cropPictureIntent.putExtra("return-data",true);
//
//
//        cropPictureIntent.putExtra("output", photoURI);//??
//
//
//        //여기서 CROP된 이미지를 파일화하고, shared preference에 저장하기
//        //-> RESUME에서 SP GET해서 이미지뷰에 보여주기
//
//
//       if(cropPictureIntent.resolveActivity(getPackageManager())!=null){
//            startActivityForResult(cropPictureIntent, REQUEST_IMAGE_CROP);
//        }
//    }


    private Bitmap rotateImage(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source,0,0,source.getWidth(), source.getHeight(),matrix,true);
    }


    //갤러리 열고 사진 가져오기
    public void callGallery(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    public boolean isCameraPermissionChecked() { //카메라 권한 있는지 확인

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        else {
            return true;
        }
    }


    //카메라 권한 없으면 요청하기
    public void requestCameraPermission() {

        if (isCameraPermissionChecked() == false) {

            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this).setTitle("알림").setMessage("저장소 권한이 거부되었습니다. 설정에서 해당 권한을 직접 허용하십시오.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(false).create().show();
            }
            else {
                ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        else {
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        //보유 포인트를 표시한다
        TextView currentPoint = (TextView)findViewById(R.id.myPage_point_textView);
        currentPoint.setText(getData("point")+" P");


        isCameraPermissionChecked(); //권한 허용 되었는지 확인
        requestCameraPermission(); //권한 없으면 -> 요청하기

        if(isCameraPermissionChecked()) { //권한 허용 되어 있어야, 사진 어떻게 가져올 것인지 선택하는 다이얼로그 띄워줌

            //화면 하단의 카메라 사진버튼을 누르면
            profileImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //카메라로 사진찍기 or 갤러리에서 사진 가져오기 선택
                    AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
                    View view = LayoutInflater.from(Setting.this).inflate(R.layout.camera_dialog, null, false);
                    builder.setView(view);

                    final Button camera = (Button) view.findViewById(R.id.camera_btn);
                    final Button gallery = (Button) view.findViewById(R.id.gallery_btn);

                    final AlertDialog dialog = builder.create();

                    camera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //카메라 버튼을 클릭했을 때 일어나는 일

                            dispatchTakePictureIntent();

                            dialog.dismiss();
                        }
                    });

                    gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //갤러리 버튼을 눌렀을 때 일어나는 일
                            callGallery();

                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                }
            });
        }
    }
}
