package com.example.swearVer2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class NewReports extends AppCompatActivity {

    String mCurrentPhotoPath;

    private boolean isImageDeleted, isAddressDeleted;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static int PICK_IMAGE_REQUEST = 2;
    static final int REQUEST_TAKE_PHOTO = 3;
    private static final int REQUEST_IMAGE_CROP = 4;
//    static final int RESULT_OK = 2000;

    Uri photoUri, albumUri;

    ImageView iv; //찍은 사진을 보여줄 이미지뷰
    ImageButton btn, deleteBtn, openMapBtn, deleteAddressBtn; //카메라 버튼, 사진 지우는 버튼
    Bitmap rotatedBitmap;

    EditText reportTitle_editText, progress_editText, goodPoint_editText, badPoint_editText;

    TextView placeInfo_name_textView, placeInfo_address_textView;

    String reportTitle, progress, goodPoint, badPoint, reportTime;
    String editedReportTitle, editedProgress, editedGoodPoint, editedBadPoint, reportEditTime;
    String placeInfo_name, placeInfo_address;
    Double placeLat, placeLng;

    private int reportItemPosition;
    private int reportNumber; //보고서 별 preference를 구별하는 지표. 저장해야함

    String goalPreferenceName;
    String preferenceName; //보고서 별로 preference를 만든다

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int MAP_REQUEST = 1000;


    //구글맵 api가 정상인지 확인
    public boolean isServiceOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(NewReports.this);
        if(available== ConnectionResult.SUCCESS){
            //모든것이 정상
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //에러가 났지만 해결할 수 있을 때
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(NewReports.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reports);

        Log.d("오류찾는다","보고서 새로만들기 화면, onCreate()");


        //입력창을 지정한다
        reportTitle_editText = (EditText) findViewById(R.id.reportTitle_editText);
        progress_editText = (EditText)findViewById(R.id.progress_editText);
        goodPoint_editText = (EditText)findViewById(R.id.goodPoint_editText);
        badPoint_editText = (EditText)findViewById(R.id.badPoint_editText);

        placeInfo_name_textView = (TextView)findViewById(R.id.newReports_locationInfo_name_textView);
        placeInfo_address_textView = (TextView)findViewById(R.id.newReports_locationInfo_address_textView);
        TextView pageTitle = (TextView)findViewById(R.id.pageTitle_newReportClass); //해당 페이지 상단에 적혀있는 제목

        //버튼과 이미지
        Button confirm = (Button) findViewById(R.id.report_confirm_btn); //확인(수정 시에는 수정완료)버튼
        btn = (ImageButton) findViewById(R.id.imageButton6); //카메라 버튼
        iv = (ImageView) findViewById(R.id.upload_reportImage_imageView); //사진 띄워줄 이미지뷰
        deleteBtn = (ImageButton)findViewById(R.id.deleteImg_btn);//사진 지우는 버튼
        openMapBtn = (ImageButton)findViewById(R.id.newReports_mapBtn);
        deleteAddressBtn = (ImageButton)findViewById(R.id.newReports_deleteMapBtn);


        //지도버튼을 눌렀을 때, 구글맵 api가 정상이면 맵이 열린다
        if(isServiceOK()){

            openMapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    startActivityForResult(intent, MAP_REQUEST);
                }
            });

        }


        //주소지우기 버튼을 누르면 -> 주소가 삭제된다
        deleteAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeInfo_name_textView.setText("");
                placeInfo_address_textView.setText("");
                isAddressDeleted =true;
            }
        });


        Intent i = getIntent();

        //보고서 목록 화면에서 create를 눌러서 이 화면에 들어올 경우) 보고서가 속한 약속의 xml파일 이름을 전달받는다
        //-> 이것을 이용, 이 보고서를 저장할 preference name을 정한다
        if(getIntent().hasExtra("goalPreferenceName")) {
            goalPreferenceName = i.getStringExtra("goalPreferenceName");
            Log.d("오류찾는다","보고서 새로만들기 화면, 보고서가 속한 약속의 xml파일 이름을 전달받는다. goalPreferenceName: "+ goalPreferenceName);

            //reportNumber 지정하기
            SharedPreferences sp = getPreferences(MODE_PRIVATE);
            if (sp.contains(goalPreferenceName + ";reportNumber")) { //기존에 저장한 값이 있으면 -> 그것을 불러옴
                reportNumber = sp.getInt(goalPreferenceName + ";reportNumber", 100);
            } else {
                reportNumber = 0; //기존에 저장한 값이 없으면(앱 최초실행) -> 0부터 시작
            }

            //보고서 별 preference 이름을 지정한다
            preferenceName = goalPreferenceName +";report;" + Integer.toString(reportNumber);
        }


        //보고서 확인 화면에서 '수정하기'를 눌러서 이 화면에 들어왔을 경우
        if(getIntent().hasExtra("reportItemPosition")) {
            //보고서 목록에서 해당 보고서가 어느 위치에 있는지, 위치값을 전달받음
            reportItemPosition = Integer.parseInt(i.getStringExtra("reportItemPosition"));

            Log.d("오류찾는다","보고서 수정하기 화면, 보고서 목록에서 해당 보고서가 어느 위치에 있는지, 위치값을 전달받음. reportItemPosition: "+reportItemPosition);
        }


        //사용자가 '수정하기'를 눌러 이 화면에 들어왔을 때
        if(ReportView.editFlag == 1){

            //페이지의 텍스트를 일부 수정한다
            pageTitle.setText("보고서 수정");
            confirm.setText("수정 완료");

            //보고서 확인화면에서 보내 온 <보고서 xml파일 이름>을 받는다. 이 파일에 보고서의 원래 내용이 저장되어 있다.
            Intent intent = getIntent();
            preferenceName = intent.getStringExtra("reportPreferenceName");

            Log.d("오류찾는다","보고서 수정 화면, 보고서 확인화면에서 보내 온 <보고서 xml파일 이름>을 받는다. preferenceName: "+preferenceName);

            //보고서의 원래 내용을 텍스트뷰 위에 표시한다
            SharedPreferences sp = getSharedPreferences(preferenceName, MODE_PRIVATE);
            reportTitle_editText.setText(sp.getString("reportTitle","수정- 제목 못받음"));
            progress_editText.setText(sp.getString("progress", "수정- 진행사항 못받음"));
            goodPoint_editText.setText(sp.getString("goodPoint","수정- 칭찬내용 못받음"));
            badPoint_editText.setText(sp.getString("badPoint", "수정- 반성내용 못받음"));

            //앞에서 주소를 저장했을 경우에만 불러온다
            if(sp.contains("placeName")){
                placeInfo_name_textView.setText(sp.getString("placeName","수정-장소이름 못받음"));
                placeInfo_address_textView.setText(sp.getString("placeAddress","수정-주소 못받음"));
            }

            //이미지 경로를 앞에서 저장했을 경우에만 불러옴
            if(sp.contains("reportImagePath")) {
                try {
                    String reportImagePath = sp.getString("reportImagePath", null);

                    File imgFile = new File(reportImagePath);
                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                        if (myBitmap != null) {
                            ExifInterface ei = new ExifInterface(reportImagePath);
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
                            iv.setImageBitmap(rotatedBitmap);
                        }
                    }
                }catch(Exception e){

                }
            }

            Log.d("오류찾는다","보고서 수정 화면, 보고서의 원래 내용을 텍스트뷰 위에 표시한다");
        }


        //입력완료 or 수정완료 시
        //사용자가 화면 하단의 확인버튼을 누르면 -> 작성한 내용이 저장된다. 보고서 목록 화면이 나타남
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //수정완료 시
                if(ReportView.editFlag == 1){
                    Log.d("오류찾는다","수정하기 누름. ReportView.editFlag: "+ReportView.editFlag);

                    editThisReport(); //변경사항을 저장한다


                    Log.d("오류찾는다","이제 보고서 목록에 수정된 아이템을 넣어 줄 것임");

                    Log.d("오류찾는다","수정된 보고서 제목을 불러온다. editedReportTitle :"+editedReportTitle);
                    Log.d("오류찾는다","xml 파일 이름을 불러온다. preferenceName :"+preferenceName);

                    CheckReports.reportItemArrayList.remove(reportItemPosition);//보고서 목록에서 해당 인덱스에 있던 아이템을 삭제한다(원래 보고서)

                    Log.d("오류찾는다",reportItemPosition +" 번째 위치에 있던 기존 보고서를 삭제한다");

                    //삭제한 위치에 수정한 아이템을 넣어준다(수정된 보고서)
                    //제목=바뀐제목, 시각=수정 시각, xml이름= 보고서 확인 화면에서 인텐트로 전달받은 것(해당 보고서의 xml이름)
                    CheckReports.reportItemArrayList.add(reportItemPosition, new ReportItem(editedReportTitle, "마지막 수정 : "+reportEditTime, preferenceName));
                    Log.d("오류찾는다",reportItemPosition +" 번째 위치에 수정된 보고서를 넣어준다");

                    Toast.makeText(NewReports.this, "보고서가 수정되었습니다", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(getApplicationContext(), CheckReports.class);

                    ReportView.editFlag = 0;
                    Log.d("오류찾는다","수정이 끝났으니, 수정 플래그를 0으로 바꿔줌. editFlag : "+ReportView.editFlag);

                    intent.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                    Log.d("오류찾는다","수정화면 종료");

                }

                //입력완료 시
                else if(ReportView.editFlag == 0){
                    Log.d("오류찾는다","보고서 새로만들기 완료 버튼을 누름. ReportView.editFlag: "+ReportView.editFlag);

//                    int reportNumber; //보고서 별 preference를 구별하는 중요한 지표. 저장 필수
//
//                    //reportNumber 지정하기
//                    SharedPreferences sp = getPreferences(MODE_PRIVATE);
//                    if (sp.contains(goalPreferenceName + ";reportNumber")) { //기존에 저장한 값이 있으면 -> 그것을 불러옴
//                        reportNumber = sp.getInt(goalPreferenceName + ";reportNumber", 100);
//                    } else {
//                        reportNumber = 0; //기존에 저장한 값이 없으면(앱 최초실행) -> 0부터 시작
//                    }
//
//                    //보고서 별 preference 이름
//                    preferenceName = goalPreferenceName +";report;" + Integer.toString(reportNumber);

                    //보고서의 내용을 새로운 preference에 저장한다
                    saveThisReport();

                    reportNumber++; //약속 1 -> 약속 2 -> 약속 3... 다음 약속을 저장하는 Preference를 다르게 하기 위해

                    //reportNumber를 저장하기
                    // 저장하지 않으면 -> 화면이 켜질 때마다 이 변수가 한 값으로 초기화됨
                    //-> goalNumber 중복. 서로 다른 약속이 하나의 넘버를 공유함
                    SharedPreferences sspp = getPreferences(MODE_PRIVATE); ///////////////getPreference가 맞나?
                    SharedPreferences.Editor editor = sspp.edit();
                    editor.putInt(goalPreferenceName + ";reportNumber", reportNumber); //key값을 그냥 reportNumber가 아니고, 약속명+reportnumber 해야할듯
                    editor.commit();

                    //메인창의 목록에 이 약속을 추가하기
                    CheckReports.reportItemArrayList.add(0, new ReportItem(reportTitle, reportTime, preferenceName));


                    Toast.makeText(NewReports.this, "보고서가 저장되었습니다", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), CheckReports.class);
                    intent.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }

            }

        });


        //사용자가 화면 하단의 취소 버튼을 누르면 -> 이 화면 종료
        Button cancel = (Button) findViewById(R.id.report_cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportView.editFlag = 0; //수정 플래그를 원상태로 돌려놓는다
                finish();
            }
        });


        //사진 지우기 버튼 누르면 -> 이미지 뷰를 비움
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv.setImageBitmap(null);
                mCurrentPhotoPath = null; //저장된 사진 경로를 비운다
                isImageDeleted = true;
            }
        });

    }


    //사용자가 입력한 약속의 내용을, Shared Preference에 저장
    public void saveThisReport(){

        //사용자가 입력한 값을 추출한다
        reportTitle = reportTitle_editText.getText().toString();
        progress = progress_editText.getText().toString();
        goodPoint = goodPoint_editText.getText().toString();
        badPoint = badPoint_editText.getText().toString();

        //보고서 작성 시간
        reportTime = showDate();

        //추출한 값을 저장한다
        SharedPreferences sp = getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("reportTitle", reportTitle);
        editor.putString("progress", progress);
        editor.putString("goodPoint", goodPoint); //시작일, 종료일은 yy/M/dd 형식의 String임
        editor.putString("badPoint", badPoint);
        editor.putString("reportTime", reportTime);

        if(placeInfo_name != null) {
            editor.putString("placeName", placeInfo_name);
            editor.putString("placeAddress", placeInfo_address);

            JSONObject info = new JSONObject();
            try {
                info.put("placeLat", placeLat);
                info.put("placeLng", placeLng);
            } catch (Exception e) {
            }
            String placeLatLng = info.toString();

            editor.putString("placeLatLng", placeLatLng);
        }

        if(mCurrentPhotoPath!=null) {
            editor.putString("reportImagePath", mCurrentPhotoPath);
            Log.d("오류찾는다","mCurrentPhotoPath: "+mCurrentPhotoPath);
        }
        editor.commit();

    }

    //사용자가 입력한 약속의 내용을, Shared Preference에 저장
    public void editThisReport(){

        Log.d("오류찾는다","editThisReport() 들어옴");

        //사용자가 입력한 값으로 preference를 수정한다
        editedReportTitle = reportTitle_editText.getText().toString();
        editedProgress = progress_editText.getText().toString();
        editedGoodPoint = goodPoint_editText.getText().toString();
        editedBadPoint = badPoint_editText.getText().toString();

        Log.d("오류찾는다","사용자가 입력한 값으로 preference를 수정한다");
        Log.d("오류찾는다","editedReportTitle: "+editedReportTitle);
        Log.d("오류찾는다","editedProgress: "+editedProgress);
        Log.d("오류찾는다","editedGoodPoint: "+editedGoodPoint);
        Log.d("오류찾는다","editedBadPoint: "+editedBadPoint);

        //보고서 수정 시각
        reportEditTime = showDate();

        Log.d("오류찾는다","보고서 수정 시각: "+reportEditTime);

        //추출한 값을 저장한다
        SharedPreferences sp = getSharedPreferences(preferenceName, Context.MODE_PRIVATE);

        Log.d("오류찾는다","추출한 값을 저장한다. preferenceName: "+preferenceName);

        SharedPreferences.Editor editor = sp.edit();

        //키에 있던 기존 값을 삭제한다
        editor.remove("reportTitle");
        editor.remove("progress");
        editor.remove("goodPoint");
        editor.remove("badPoint");

        try {
            if (sp.contains("placeName")) { //기존에 저장된 주소가 있으면

                if (placeInfo_name != null) { //수정하면서 주소를 바꿨을 때
                    editor.remove("placeName");
                    editor.remove("placeAddress");
                    editor.remove("placeLatLng");

                    editor.putString("placeName", placeInfo_name);
                    editor.putString("placeAddress", placeInfo_address);

                    JSONObject info = new JSONObject();
                    try {
                        info.put("placeLat", placeLat);
                        info.put("placeLng", placeLng);
                    } catch (Exception e) {
                    }
                    String placeLatLng = info.toString();

                    editor.putString("placeLatLng", placeLatLng);

                } else if (placeInfo_name == null) { //주소 변수가 비어있을 때
                    if (isAddressDeleted) { //기존에 있던 주소를 '지우기'버튼을 눌러서 직접 지웠으면
                        editor.remove("placeName");
                        editor.remove("placeAddress");//저장된 주소를 지움. 다음에 보고서를 확인할 때 이 주소가 보이면 안 됨
                        editor.remove("placeLatLng");

                        isAddressDeleted = false;//변수 초기화
                    } else {
                        //수정하면서 주소는 건들지 않았을 때 : 기존 주소를 삭제하면 안 됨. 냅두기
                    }
                }
            } else { //보고서 최초 작성 시 주소를 안 넣었고, 수정하면서 넣었을 때
                editor.putString("placeName", placeInfo_name); //키값을 추가해준다
                editor.putString("placeAddress", placeInfo_address);

                JSONObject info = new JSONObject();
                try {
                    info.put("placeLat", placeLat);
                    info.put("placeLng", placeLng);
                } catch (Exception e) {
                }
                String placeLatLng = info.toString();

                editor.putString("placeLatLng", placeLatLng);
            }

        }catch(Exception e){ }

        try {
            if (sp.contains("reportImagePath")) { //기존에 저장된 사진이 있으면

                if (mCurrentPhotoPath != null) { //수정하면서 이미지도 바꿨을 때
                    editor.remove("reportImagePath");
                    editor.putString("reportImagePath", mCurrentPhotoPath);
                } else if (mCurrentPhotoPath == null) { //이미지 경로가 비어있을 때
                    if (isImageDeleted) { //기존에 있던 이미지를 '지우기'버튼을 눌러서 직접 지웠으면
                        editor.remove("reportImagePath"); //저장된 이미지 경로를 지움. 다음에 보고서를 확인할 때 이 사진이 보이면 안 됨
                        isImageDeleted = false;
                    } else {
                        //수정하면서 이미지는 건들지 않았을 때 : 기존 이미지를 삭제하면 안 됨. 냅두기
                    }
                }
            } else { //보고서 최초 작성 시 사진을 안 넣었고, 수정하면서 넣었을 때
                editor.putString("reportImagePath", mCurrentPhotoPath); //키값을 추가해준다
            }

        }catch(Exception e){ }

        //새로운 값을 넣는다
        editor.putString("reportTitle", editedReportTitle);
        editor.putString("progress", editedProgress);
        editor.putString("goodPoint", editedGoodPoint); //시작일, 종료일은 yy/M/dd 형식의 String임
        editor.putString("badPoint", editedBadPoint);

        if(sp.contains("reportEditTime")){
            editor.remove("reportEditTime");
        }
        editor.putString("reportEditTime", reportEditTime);

        editor.commit();
        Log.d("오류찾는다","수정사항을 저장함. editor.commit() 제대로 수정되었는지 확인해보자");
        Log.d("오류찾는다","reportTitle: "+sp.getString("reportTitle","값이 제대로 안 들어감"));
        Log.d("오류찾는다","progress: "+sp.getString("progress","값이 제대로 안 들어감"));
        Log.d("오류찾는다","goodPoint: "+sp.getString("goodPoint","값이 제대로 안 들어감"));
        Log.d("오류찾는다","badPoint: "+sp.getString("badPoint","값이 제대로 안 들어감"));
        Log.d("오류찾는다","reportEditTime: "+sp.getString("reportEditTime","값이 제대로 안 들어감"));

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

                            iv.setImageBitmap(rotatedBitmap);
                        }
                    }catch (IOException ex){

                    }
                }

                //갤러리에서 선택한 사진 받아옴
                else if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){

                    try{
//                        InputStream in = getContentResolver().openInputStream(data.getData());
//                        Bitmap img = BitmapFactory.decodeStream(in);
//                        in.close();
//                        iv.setImageBitmap(img);

                        File albumFile = null;
                        albumFile = createImageFile();
                        photoUri = data.getData();
                        albumUri = Uri.fromFile(albumFile); // 해당 경로에 저장
                        Log.d("오류찾는다","albumUri: "+albumUri);
                        cropImage();

//                        Log.d("오류찾는다","uri: "+uri);
//
//                        String path = uri.getPath();
//
//                        mCurrentPhotoPath = getPath(uri);
                        Log.d("오류찾는다","mCurrentPhotoPath: "+mCurrentPhotoPath);


                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }
                else if(requestCode == REQUEST_IMAGE_CROP && resultCode == RESULT_OK) {

                    galleryAddPic();
                    iv.setImageURI(albumUri);

                }
                else if(requestCode == MAP_REQUEST && resultCode == RESULT_OK) {

                    placeInfo_name = data.getStringExtra("placeName");
                    placeInfo_address = data.getStringExtra("placeAddress");
                    placeLat = data.getDoubleExtra("placeLat", 0);
                    placeLng = data.getDoubleExtra("placeLng", 0);
                    Log.d("지도","placeLat: "+placeLat);
                    Log.d("지도","placeLng: "+placeLng);

                    placeInfo_name_textView.setText(placeInfo_name);
                    placeInfo_address_textView.setText(placeInfo_address);
                }
        }



        public void cropImage(){

        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoUri, "image/*");
//B
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


        //갤러리에서 선택한 이미지의 실제 경로 가져오기
        //uri 스키마를 content:///에서 file:///로 변경한다 -- 뭔말??
//        public String getPath(Uri originalUri){
//
//        Log.d("오류찾는다","getPath 들어옴. uri="+originalUri);
//
//        String[] projection = {MediaStore.Images.Media.DATA};
//        Cursor cursor = this.getContentResolver().query(originalUri,projection, null,null,null);
//        cursor.moveToFirst();
//
//        int column_index = cursor.getColumnIndex(projection[0]);
//        String picturePath = cursor.getString(column_index);
//        cursor.close();
//
//        Log.d("오류찾는다","getPath 끝남. picturePath="+picturePath);
//        return picturePath;
//        }
//
//
//        private Uri getUri(){
//        String state = Environment.getExternalStorageState();
//            if(!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
//                return MediaStore.Images.Media.INTERNAL_CONTENT_URI;
//
//        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        }


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


//        private File createAnotherImageFile() throws IOException{
//
//            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//            String imageFileName = "JPEG_" + timeStamp + ".jpg";
//            File imageFile = null;
//            File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures",imageFileName);
//                if(!storageDir.exists()){
//                        storageDir.mkdirs();
//                }
//
//                imageFile = new File(storageDir, imageFileName);
//                mCurrentPhotoPath = imageFile.getAbsolutePath();
//
//                return imageFile;
//        }



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


        public static Bitmap rotateImage(Bitmap source, float angle){
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
    protected void onPause() {
        super.onPause();

    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onResume() {
        super.onResume();


        // onCreate에 카메라 기능 안 넣은 이유:
        // 사용자가 카메라,저장소 권한 deny 했을 때, 직접 설정창에서 권한 허용하고 돌아오면 -> onCreate면 isCameraPermissionChecked가 계속 false로 뜸 -> 카메라 다이얼로그 안 켜짐
        // 고칠점: 한번 deny해도 계속 물어봄(don't ask me again 눌러야 없어짐)

        isCameraPermissionChecked(); //권한 허용 되었는지 확인
        requestCameraPermission(); //권한 없으면 -> 요청하기

        if(isCameraPermissionChecked()) { //권한 허용 되어 있어야, 사진 어떻게 가져올 것인지 선택하는 다이얼로그 띄워줌

            //화면 하단의 카메라 사진버튼을 누르면
            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //카메라로 사진찍기 or 갤러리에서 사진 가져오기 선택
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewReports.this);
                    View view = LayoutInflater.from(NewReports.this).inflate(R.layout.camera_dialog, null, false);
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

    //현재 날짜를 구하기
    public String showDate(){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd aaa hh:mm");
        String date = format.format(System.currentTimeMillis());
        return date;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

