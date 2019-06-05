package com.example.swearVer2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;


public class ReportView extends AppCompatActivity implements OnMapReadyCallback{

    TextView title_textView, progress_textView, goodPoint_textView, badPoint_textView, reportTime_textView, editedTime_textView;
    TextView image_title_textView, placeInfo_title_textView, placeName_textView, placeAddress_textView;
    ImageView reportImage;
    String title, progress, goodPoint, badPoint, receivedPreferenceName, receivedGoalPreferenceName, placeAddress;
    String reportImagePath;
    Button confirm, edit;
//    ImageButton linkToMap_btn;
    private int reportItemPosition;

    Double latitude, longitude;

    public static int editFlag; // 수정버튼 누르면 -> 1
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private static final float DEFAULT_ZOOM = 16f;

    MapView mapView;
    boolean placeInfoExist = false;

    @Override
    public void onMapReady(GoogleMap map) {

        try {
            LatLng coordinate = new LatLng(latitude, longitude);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, DEFAULT_ZOOM));
            MarkerOptions markerOptions = new MarkerOptions().position(coordinate);
            map.addMarker(markerOptions);

            map.getUiSettings().setZoomControlsEnabled(true);
        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("지도",ex);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_view);

        mapView = (MapView) findViewById(R.id.ReportView_mapView);
        mapView.setVisibility(View.GONE);

        reportTime_textView = (TextView)findViewById(R.id.reportView_createdTime); //보고서 최초 작성 시각
        editedTime_textView = (TextView)findViewById(R.id.reportView_editedTime); //보고서 수정 시각

        title_textView = (TextView)findViewById(R.id.reportTitle_textView);
        progress_textView = (TextView)findViewById(R.id.report_progress_textView);
        goodPoint_textView = (TextView)findViewById(R.id.report_goodPoint_textView);
        badPoint_textView = (TextView)findViewById(R.id.report_badPoint_textView);
        image_title_textView = (TextView)findViewById(R.id.reportView_image_title_textView);
        reportImage = (ImageView)findViewById(R.id.report_imageView);

        placeInfo_title_textView = (TextView)findViewById(R.id.reportView_locationInfo_title_textView);
        placeName_textView = (TextView)findViewById(R.id.reportView_placeName_textView);
        placeAddress_textView = (TextView)findViewById(R.id.reportView_placeAddress_textView);

        //주소, 이미지는 안에 넣을 정보가 있을 때까지 숨겨둔다
        image_title_textView.setVisibility(View.GONE);
        reportImage.setVisibility(View.GONE);
        placeInfo_title_textView.setVisibility(View.GONE);
        placeName_textView.setVisibility(View.GONE);
        placeAddress_textView.setVisibility(View.GONE);


        //'보고서 새로만들기' 창에서 보고서 내용을 저장한 preference의 이름을 전달받음
        //보고서 목록에서 해당 보고서가 몇 번째에 위치하는지, 목록창으로부터 아이템의 위치값을 전달받는다.
        //-> 수정버튼을 누르면 이것을 전달해줌
        if(getIntent().hasExtra("preferenceName")&&getIntent().hasExtra("itemPosition")){

            receivedGoalPreferenceName = getIntent().getStringExtra("goalPreferenceName");
            receivedPreferenceName = getIntent().getStringExtra("preferenceName");
            reportItemPosition = Integer.parseInt(getIntent().getStringExtra("itemPosition"));


                SharedPreferences sp = getSharedPreferences(receivedPreferenceName, MODE_PRIVATE);

                title_textView.setText(sp.getString("reportTitle","제목 없음"));
                progress_textView.setText(sp.getString("progress","진행사항 없음"));
                goodPoint_textView.setText(sp.getString("goodPoint","칭찬 없음"));
                badPoint_textView.setText(sp.getString("badPoint","반성 없음"));
                reportTime_textView.setText("작성시각 : "+sp.getString("reportTime","작성시각 없음"));

                //보고서 수정을 했을 경우, 수정시각이 표시된다
                //해당 인텐트는 NewReports(수정하기)에서 온다
                if(sp.getString("reportEditTime", "").length()>0) {
                    editedTime_textView.setText("마지막 수정시각 : "+sp.getString("reportEditTime", "수정시각 못받음"));
                }

                if(sp.contains("placeName")){
                    placeInfoExist = true;

                    placeInfo_title_textView.setVisibility(View.VISIBLE);
                    placeName_textView.setVisibility(View.VISIBLE);
                    placeAddress_textView.setVisibility(View.VISIBLE);

                    placeName_textView.setText(sp.getString("placeName","장소이름 못받음"));
                    placeAddress_textView.setText(sp.getString("placeAddress","주소 못받음"));
                    placeAddress= sp.getString("placeAddress","주소 못받음");

                    String placeLatLng = sp.getString("placeLatLng", "");

                    JSONParser parser = new JSONParser();
                    try {
                        JSONObject jsonObject = (JSONObject) parser.parse(placeLatLng);

                        latitude = (Double) jsonObject.get("placeLat");
                        longitude = (Double) jsonObject.get("placeLng");

                    }catch(Exception e){ }
                }


            try{
                if(sp.contains("reportImagePath")) {
                    reportImagePath = sp.getString("reportImagePath", null);

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

                            image_title_textView.setVisibility(View.VISIBLE);
                            reportImage.setVisibility(View.VISIBLE);
                            reportImage.setImageBitmap(rotatedBitmap);
                        }

                    }
                }

            }catch(Exception e){
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String ex = sw.toString();

                Log.d("오류찾는다- ReportView.class",ex);
            }


        if(placeInfoExist) {

            Log.d("지도", "placeInfoExist:"+placeInfoExist);
            mapView.setVisibility(View.VISIBLE);

            Bundle mapViewBundle = null;
            if (savedInstanceState != null) {
                mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
            }

            mapView.onCreate(mapViewBundle);

            mapView.getMapAsync(this);
        }

        }


        //맵뷰에서 이 기능을 지원해 줌(해당 주소를 구글맵 앱에서 열기). 그래서 사용하지 않음
//        linkToMap_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                    LatLng coordinate = new LatLng(latitude, longitude);
////                    String geoUri = "http://maps.google.com/maps?q=loc:" + latitude + "," +longitude;
//
//                    String map = "http://maps.google.co.in/maps?q=" + placeAddress;
//                    Uri uri = Uri.parse(map);
//
//                    Log.d("지도","uri: "+uri);
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    startActivity(intent);
//            }
//        });


        confirm = (Button)findViewById(R.id.reportView_confirm_btn);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //사용자가 화면 하단의 수정버튼을 누르면 -> 수정 화면이 나타난다
        edit = (Button)findViewById(R.id.reportView_edit_btn);

        SharedPreferences shared = getSharedPreferences(receivedGoalPreferenceName, MODE_PRIVATE);
        //최종결과가 확정된 이후 or 이 화면을 보는 사람이 친구일 경우에는 수정 버튼이 사라진다
        if(shared.contains("SuccessOrFailure") | Main.isFreindMode){
            edit.setVisibility(View.GONE);
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editFlag = 1;// 수정 상태일 경우 플래그를 1로 바꾼다
                Intent intent = new Intent(getApplicationContext(), NewReports.class);

                //해당 보고서가 저장된 xml파일의 이름을 보낸다
                intent.putExtra("reportPreferenceName", receivedPreferenceName);
                //목록에서 해당 보고서가 몇 번째에 있는지, 위치값을 전달해준다
                intent.putExtra("reportItemPosition", String.valueOf(reportItemPosition));
                startActivity(intent);
                finish();
            }
        });
    }


    public static Bitmap rotateImage(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source,0,0,source.getWidth(), source.getHeight(),matrix,true);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(placeInfoExist) {
            mapView.onResume();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(placeInfoExist) {
            mapView.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(placeInfoExist) {
            mapView.onStop();
        }
    }

    @Override
    protected void onPause() {
        if(placeInfoExist) {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(placeInfoExist) {
            mapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if(placeInfoExist) {
            mapView.onLowMemory();
        }
    }


}
