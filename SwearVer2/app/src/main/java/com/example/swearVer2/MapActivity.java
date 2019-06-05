package com.example.swearVer2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.libraries.places.api.Places;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
//import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.model.Place;
//import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback , GoogleApiClient.OnConnectionFailedListener {

    public static final String apiKey = "AIzaSyAUsu9SAQg4FDKO48qxaP9Qu8y9mg92WQs";

    private static final String TAG = "위치찾기";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
//    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
//            new LatLng(-40, -168), new LatLng(71, 136));

    private boolean locationPermissionGranted = false;
    private GoogleMap myMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    ImageView myGps;
    ImageButton searchBtn;

    String name, address;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Log.d(TAG, "onCreate");

        //위치정보를 사용하겠다고 사용자에게 권한을 요청한다
        getLocationPermission();
        Log.d(TAG, "getLocationPermission() 끝");

//        searchBtn = (ImageButton)findViewById(R.id.MapActivity_search_ImageBtn);
//        searchText = (EditText) findViewById(R.id.MapActivity_search_EditText);
        myGps = (ImageView)findViewById(R.id.MapActivity_gps_icon_imageView);

        Places.initialize(getApplicationContext(), apiKey);

        PlacesClient placesClient = Places.createClient(this);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setPlaceFields(
                Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS ));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                try {
                    name = place.getName();
                    latLng = place.getLatLng();
                    address = place.getAddress();

                    Log.d(TAG, "autoComplete is successful");
                    Log.d(TAG, "Name: " + name);
                    Log.d(TAG, "LatLng: " + latLng);
                    Log.d(TAG, "Address: " + address);
                    if(latLng != null) {
                        moveCamera(latLng, name, address);
                    }

                }catch (Exception e){
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String ex = sw.toString();

                    Log.d(TAG,ex);
                }
            }

            @Override
            public void onError(Status status) {
                Log.d(TAG, "autoComplete error: "+status);
            }
        });
    }


    //위치정보를 알 수 있는 권한을 달라고 사용자에게 요청한다
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission들어옴");

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

//권한이 이미 승인되어 있으면 -> 맵을 활성화한다
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                initMap();
            }
//권한이 막혀있으면 -> 권한을 달라고 요청한다
            else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    //사용자가 권한을 승인했는지 확인한다. 권한이 승인되면 -> 맵을 활성화한다
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionGranted = false;
                            return;
                        }
                    }
                    locationPermissionGranted = true;
                    //map을 활성화한다
                    initMap();
                }
            }
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap 들어옴");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.newReports_map_fragment);
        mapFragment.getMapAsync(MapActivity.this);
    }

    //initMap()에서 getMapAsync가 호출되었을 때(맵이 준비되었을 때)의 콜백
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady 들어옴");

        Log.d(TAG, "map is ready");

        //변수를 참조한다
        myMap = googleMap;

        if (locationPermissionGranted) {

            //기기의 위치를 받아온다 + 카메라를 이 위치로 움직인다
            getDeviceLocation();

            //이건 뭐지?
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            //현재 위치를 점으로 표시한다
            myMap.setMyLocationEnabled(true);

            //다른 위치를 탐색하다가도, 이 버튼을 누르면 내 위치로 자동으로 이동한다. 이것을 비활성화 시킨다.
            //상단에 검색창이 있으면(이따가 만들 것임) 이 버튼이 가려지기 때문에, 굳이 필요 없다.
            //이 버튼은 위치를 옮길 수 없다. 상단 우측에 고정되어있음
            myMap.getUiSettings().setMyLocationButtonEnabled(false);

            //지도를 회전할 수 있게 만든다
            myMap.getUiSettings().isRotateGesturesEnabled();

            //우측 상단의 GPS 아이콘을 누르면 -> 사용자의 현재 위치로 카메라를 이동
            myGps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDeviceLocation();
                }
            });

//            //검색창에 원하는 장소를 입력하면, 그에 맞는 주소를 찾는다
//            init();
        }
    }

    //카메라를 파라미터의 좌표로 움직이고, 마커를 찍고, 이름을 붙여준다
    private void moveCamera(final LatLng latLngInput, final String titleInput, final String contentInput){
        Log.d(TAG, "moveCamera: moving the camera to "+titleInput);

        //해당 좌표로 카메라를 움직인다
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngInput, DEFAULT_ZOOM));

        if(!titleInput.equals("My Location")) {//현재 위치에는 주소 제목을 붙여주지 않음
            //특정 좌표에 마커를 찍고, 이름을 붙여주는 객체

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLngInput)
                    .title(titleInput)
                    .snippet(contentInput);

            //전에 찍었던 마커를 삭제한다
            myMap.clear();

            //구글이 지원하는 인포어댑터 - active하지 않음. 버튼 삽입 불가
//            myMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapActivity.this));

            //좌표에 마커를 찍어준다
            myMap.addMarker(markerOptions);

            //마커를 누르면 대화상자를 보여준다(정보, 장소 선택버튼)
            myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    try {

                        final AlertDialog.Builder alertadd = new AlertDialog.Builder(MapActivity.this);
                        LayoutInflater factory = LayoutInflater.from(MapActivity.this);
                        final View view = factory.inflate(R.layout.map_dialog, null);
                        alertadd.setView(view);

                        TextView placeTitle = (TextView)view.findViewById(R.id.mapDialog_title_textView);
                        TextView placeAddress = (TextView)view.findViewById(R.id.mapDialog_address_textView);
                        Button saveBtn = (Button)view.findViewById(R.id.mapDialog_save_btn);

                        placeTitle.setText(name);
                        placeAddress.setText(address);
                        saveBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Double latitude = latLng.latitude;
                                Double longitude = latLng.longitude;

                                Toast.makeText(MapActivity.this, "선택 완료", Toast.LENGTH_SHORT).show();
                                Intent result = new Intent();
                                result.putExtra("placeName", name);
                                result.putExtra("placeAddress", address);
                                result.putExtra("placeLat", latitude);
                                result.putExtra("placeLng", longitude);
                                setResult(RESULT_OK, result);
                                finish();
                            }
                        });

                        alertadd.show();
                    }catch (Exception e){
                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        String ex = sw.toString();

                        Log.d(TAG,ex);
                    }

                    return true;
                }
            });
        }

        //키보드 숨김
//        hideSoftKeyboard();
    }


    //사용자의 현재 위치를 구하고, 그곳으로 이동하고, 지도 위에 마커를 놓는다
    private void getDeviceLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(locationPermissionGranted){
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found Location.");
                            Location currentLocation = (Location)task.getResult();
                            Log.d(TAG, "onComplete: found Location. + currentLocation:"+currentLocation);

                            //내 현재위치로 카메라를 옮기고, 마커를 찍고, "My Location"이라고 제목을 표시해준다
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),"My Location", "");
                        }else{
                            Log.d(TAG, "onComplete: unable to get location");
                        }
                    }
                });
            }
        }catch (SecurityException e){ }
    }


    //사용하지 않음
//    private void geoLocate(){
//        String searchString = searchText.getText().toString();
//        Geocoder geocoder = new Geocoder(MapActivity.this);
//        List<Address> list = new ArrayList<>();
//        try{
//            list = geocoder.getFromLocationName(searchString, 1);
//        }catch (IOException e){
//
//        }
//        if(list.size() > 0){ //인풋값이 있다는 뜻
//            address = list.get(0); //인풋을 하나만 받을 것이므로 0번 값을 가져온다
//            Log.d(TAG, "geoLocate: found a location: Name: "+address.getFeatureName());
//            Log.d(TAG, "geoLocate: found a location: "+address.toString());
//
////해당 좌표로 카메라를 이동하고, 마커를 찍는다. 주소를 표시해준다
//            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),DEFAULT_ZOOM, address.getAddressLine(0));
//        }
//    }


    private void init(){

//        //엔터를 누르거나, 찾기버튼을 누르면 해당 텍스트에 해당하는 주소를 찾는다
//        searchBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                geoLocate();
//            }
//        });
//
//        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
//                if(actionId == EditorInfo.IME_ACTION_SEARCH
//                        || actionId == EditorInfo.IME_ACTION_DONE
//                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
//                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
//
//                    //execute our method for searching
//                    geoLocate();
//                }
//
//                return false;
//            }
//        });

//        myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//
//                showDialog(searchText.getText().toString(), address.getAddressLine(0));
//
//                return true;
//            }
//        });


//우측 상단의 GPS 아이콘을 누르면 -> 사용자의 현재 위치로 카메라를 이동
        myGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDeviceLocation();
            }
        });

//키보드 숨김
//        hideSoftKeyboard();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }


    //검색 시 키보드를 숨긴다
//    private void hideSoftKeyboard(){
//        InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(searchText.getWindowToken(),0);

//아래 코드는 동작하지 않음
// this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }

//    private void showDialog(String title, String address){
//
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
//        View view = LayoutInflater.from(this).inflate(R.layout.map_dialog, null, false);
//        builder.setView(view);
//
//        final Button saveBtn = (Button)view.findViewById(R.id.mapDialog_save_btn);
//
//        final AlertDialog dialog = builder.create();
//        dialog.setCancelable(true);
//
//        saveBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //newReports에 인텐트로 정보를 담아서 보냄
//
//                dialog.dismiss();
//            }
//        });
//    }

}