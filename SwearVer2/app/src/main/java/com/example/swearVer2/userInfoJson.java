package com.example.swearVer2;

import org.json.JSONException;
import org.json.JSONObject;

public class userInfoJson {

    //회원가입 창에서 사용자가 입력한 정보를 저장한다
    public void saveUserInfo(String userName, String email, String password) throws JSONException {

        JSONObject userInfo = new JSONObject();

        userInfo.put("userName", userName);
        userInfo.put("email", email);
        userInfo.put("password", password);

        String info = userInfo.toString();
    }





}
