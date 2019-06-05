package com.example.swearVer2;

import android.graphics.Bitmap;

public class FriendListItem {

    private Bitmap profileImage;
    private String username;
    private String friendEmail;

    public FriendListItem(Bitmap profileImage, String username, String friendEmail) {
        this.profileImage = profileImage;
        this.username = username;
        this.friendEmail = friendEmail;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }

    public String getFriendEmail() {
        return friendEmail;
    }

    public void setFriendEmail(String friendEmail) {
        this.friendEmail = friendEmail;
    }
}
