package com.example.swearVer2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_findFriend extends Fragment {

    EditText search_editText;
    TextView result_textView, result_small_textView;
    ImageView friend_profileImage_imageView;
    Button addBtn;
    ImageButton searchBtn;
    JSONArray friendList_arr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_findfriend_layout, container, false);

        Log.d("뷰페이저", "findFriends) onCreateView()");

        searchBtn = (ImageButton)view.findViewById(R.id.fragment_findfriend_search_imageBtn);

        result_textView = (TextView)view.findViewById(R.id.fragment_result_textView);
        result_small_textView = (TextView)view.findViewById(R.id.fragment_result_small_textView);
        friend_profileImage_imageView = (ImageView)view.findViewById(R.id.fragment_findfriend_profileImage);
        addBtn = (Button)view.findViewById(R.id.fragment_findfriend_add_button);

        result_textView.setVisibility(View.GONE);
        result_small_textView.setVisibility(View.GONE);
        friend_profileImage_imageView.setVisibility(View.GONE);
        addBtn.setVisibility(View.GONE);

        search_editText = (EditText)view.findViewById(R.id.fragment_findfriend_editText);
        search_editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    //execute our method for searching
                    if(search_editText.getText().toString() != null){
                        String userInput = search_editText.getText().toString();
                        search(userInput);
                        hideSoftKeyboard();
                    }
                }
                return false;
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(search_editText.getText().toString() != null){
                    String userInput = search_editText.getText().toString();
                    search(userInput);
                    hideSoftKeyboard();
                }
            }
        });

        return view;
    }

    private void search(final String input){

        SharedPreferences sp = getActivity().getSharedPreferences(SignUp.userInfoPreference, getActivity().MODE_PRIVATE);

        //있으면
        if(sp.contains(input)){

            //본인일 때
            if(input.equals(sp.getString("currentUser",""))){
                friend_profileImage_imageView.setVisibility(View.GONE);
                addBtn.setVisibility(View.GONE);
                result_small_textView.setVisibility(View.GONE);

                result_textView.setVisibility(View.VISIBLE);
                result_textView.setText("That's you!");

            //이미 친구 목록에 있을 때
            }else if(isMyFriend(input)){

                result_small_textView.setVisibility(View.VISIBLE);
                friend_profileImage_imageView.setVisibility(View.VISIBLE);
                result_textView.setVisibility(View.VISIBLE);
                addBtn.setVisibility(View.GONE);

                //친구의 사진을 보여준다
                setProfileImageAtView(input);
                result_textView.setText(getData(input, "username"));
                result_small_textView.setText("is your friend!");


            //새로운 사람일 때
            }else{
                result_small_textView.setVisibility(View.GONE);

                friend_profileImage_imageView.setVisibility(View.VISIBLE);
                result_textView.setVisibility(View.VISIBLE);
                addBtn.setVisibility(View.VISIBLE);

                //친구의 사진을 보여준다
                setProfileImageAtView(input);
                result_textView.setText(getData(input, "username"));

                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "친구 추가되었습니다", Toast.LENGTH_SHORT).show();
                        saveFriend(input);
                        addBtn.setVisibility(View.GONE);
                        //친구를 추가한다(저장하기)
                    }
                });

//            Intent intent = new Intent(getActivity(), CheckFriendsGoal.class);
//            intent.putExtra("friendsEmail", input);
//            startActivity(intent);
//
//            //친구가 열람중인지 표시 - CheckFriendsGoal화면에서 backpress 눌렀을 때 false로 바뀜
//            Main.isFreindMode = true;
            }

        //없으면
        }else{
            friend_profileImage_imageView.setVisibility(View.GONE);
            addBtn.setVisibility(View.GONE);

            //존재하지 않는 회원이라고 표시해주기
            result_textView.setVisibility(View.VISIBLE);
            result_small_textView.setVisibility(View.VISIBLE);

            result_textView.setText("User not found");
            result_small_textView.setText("Please check the Email and try again.");
        }
    }


    //검색 시 키보드를 숨긴다
    private void hideSoftKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search_editText.getWindowToken(),0);
    }


    public String getData(String userEmail, String key){

        String data = "데이터 없음";

        //현재 사용자의 email(회원 식별자)를 불러온다
        SharedPreferences sp = getActivity().getSharedPreferences(SignUp.userInfoPreference, getActivity().MODE_PRIVATE);

        //Preference에서 현재 사용자의 정보(string 형태로 변환된 json객체)를 불러온다
        String userInfo = sp.getString(userEmail, "");
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

    //저장소에서 프로필 사진 경로를 불러옴 -> 화면에 표시
    private void setProfileImageAtView(String userEmail){

        try {
            String imagePath = getData(userEmail,"profileImagePath");

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
                    friend_profileImage_imageView.setImageBitmap(rotatedBitmap);
                }
            }
        }catch(Exception e){

            Log.d("마이페이지","onCreate(), 사진없음");
        }
    }

    //저장소에서 프로필 사진 경로를 불러옴 -> 화면에 표시
    private Bitmap getProfileBitmapImage(String userEmail){

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

    private String getCurrentUser(){
        SharedPreferences infoPref = getActivity().getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        return infoPref.getString("currentUser","유저정보없음");
    }

    private void saveFriend(String friendsEmail){

        JSONObject friendInfo = new JSONObject();
        friendInfo.put("email", friendsEmail);

        String currentUser = getCurrentUser();

        SharedPreferences sp = getActivity().getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String userInfo_string = sp.getString(currentUser, ""); //친구의 정보(string)를 불러온다
        Log.d("친구", "json 파싱함. 사용자의 정보 전부 출력: "+userInfo_string);

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
            } else { //없다면 새로 만들어준다
                friendList_arr = new JSONArray();
            }

            //위에서 작성한 메시지(jsonObject)를 추가한다
            friendList_arr.add(0, friendInfo);

            userInfo_object.put("friendList", friendList_arr);

            //바뀐 객체를 SharedPreferences 에 저장한다
            editor.putString(currentUser, userInfo_object.toString());
            editor.commit();

            String username = getUserData(friendsEmail, "username");
            Fragment_friendList.itemList.add(Fragment_friendList.itemList.size(),
                    new FriendListItem(getProfileBitmapImage(friendsEmail), username, friendsEmail));
            Fragment_friendList.adapter.notifyItemInserted(Fragment_friendList.itemList.size());

            Log.d("친구", "json 파싱함. userInfo_object에 있는 정보 전부 출력: " + userInfo_object);

//            Map<String, ?> allEntries = sp.getAll();
//            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//                Log.d("메시지", "preference에 있는 데이터 전부출력: " + entry.getKey() + ": " + entry.getValue().toString());
//            }
        }catch (Exception e){

            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("친구",ex);
        }

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

    private boolean isMyFriend(String userInput) {
        String currentUser = getCurrentUser();

        SharedPreferences sp = getActivity().getSharedPreferences(SignUp.userInfoPreference, MODE_PRIVATE);

        String userInfo_string = sp.getString(currentUser, ""); //친구의 정보(string)를 불러온다
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

                        if (friendInfo_object.get("email").equals(userInput)) {

                            return true;
                        }
                    }
                }

            } else { //친구 리스트가 없다면
                return false;
            }

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String ex = sw.toString();

            Log.d("친구",ex);
        }
        return false;
    }
}
