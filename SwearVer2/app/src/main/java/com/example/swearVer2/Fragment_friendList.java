package com.example.swearVer2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_friendList extends Fragment implements View.OnClickListener{

    RecyclerView rcv;
    public static ArrayList<FriendListItem> itemList;
    public static Adapter_FriendList adapter;
    GridLayoutManager layoutManager;

    String currentUser;
    JSONArray friendList_arr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friendlist_layout, container, false);

        currentUser = getCurrentUser();

        rcv = (RecyclerView)view.findViewById(R.id.friendlist_recyclerView);
        layoutManager = new GridLayoutManager(getActivity(), 3);
        rcv.setHasFixedSize(true);
        rcv.setLayoutManager(layoutManager);
        itemList = new ArrayList<>();
        adapter = new Adapter_FriendList(itemList, getActivity());
        rcv.setAdapter(adapter);

        setFriendListData();

        Log.d("뷰페이저", "friendList) onCreateView()");

        return view;
    }


    public String getUserData(String email, String key){

        String data = "데이터 없음";

        //현재 사용자의 email(회원 식별자)를 불러온다
        SharedPreferences sp = getActivity().getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);

        //Preference에서 해당 사용자의 정보(string 형태로 변환된 json객체)를 불러온다
        String userInfo = sp.getString(email, "");
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

    private String getCurrentUser(){
        SharedPreferences infoPref = getActivity().getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        return infoPref.getString("currentUser","유저정보없음");
    }

    private void setFriendListData(){
        SharedPreferences sp = getActivity().getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String userInfo_string = sp.getString(currentUser, ""); //사용자의 정보(string)를 불러온다
        Log.d("친구", "json 파싱함. 사용자의 정보 전부 출력: " + userInfo_string);

        JSONParser parser = new JSONParser();
        try {
            //사용자의 정보(string)을 json 형태로 파싱한다
            JSONObject userInfo_object = (JSONObject) parser.parse(userInfo_string);

            //이미 키가 있다면
            if (userInfo_object.containsKey("friendList")) {
                //친구의 정보에 있던 receivedMessage array를 불러온다
//                                    String receivedMessage_string = (String) friendInfo_object.get("receivedMessage");
//                                    JSONArray receivedMessage_arr = (JSONArray) parser.parse(receivedMessage_string);
                Object friendList_object = userInfo_object.get("friendList");
                friendList_arr = (JSONArray)friendList_object;
                Log.d("메시지", "json 파싱함. friendList_arr에 있는 정보 전부 출력: " + friendList_arr);

                if(friendList_arr.size()>0) {
                    for (int i = 0; i < friendList_arr.size(); i++) {

                        JSONObject friendInfo_object = (JSONObject)friendList_arr.get(i);

                        String friendEmail = (String)friendInfo_object.get("email");
                        String username = getUserData(friendEmail, "username");

                        itemList.add(0, new FriendListItem(setProfileImage(friendEmail), username, friendEmail));
                    }
                    adapter.notifyDataSetChanged();
                }

            } else { //친구 리스트가 없다면
                Toast.makeText(getActivity(), "친구가 없습니다", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("친구",ex);
        }
    }


    //저장소에서 프로필 사진 경로를 불러옴 -> 화면에 표시
    private Bitmap setProfileImage(String userEmail){

        try {
            String imagePath = getUserData(userEmail, "profileImagePath");

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

                    return rotatedBitmap;
                }
            }
        }catch(Exception e){

            Log.d("마이페이지","onCreate(), 사진없음");
        }

        return null;
    }

    private Bitmap rotateImage(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source,0,0,source.getWidth(), source.getHeight(),matrix,true);
    }


    @Override
    public void onClick(View v) {

    }
}
