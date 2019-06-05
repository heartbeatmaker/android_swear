package com.example.swearVer2;

import android.graphics.Bitmap;

public class MessageItem {

    private Bitmap messageProfile;
    private String messageName;
    private String messageContent;
    private String messageTime;
    private String messageCount;

    public Bitmap getMessageProfile() {
        return messageProfile;
    }

    public void setMessageProfile(Bitmap messageProfile) {
        this.messageProfile = messageProfile;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(String messageCount) {
        this.messageCount = messageCount;
    }

    public MessageItem(Bitmap messageProfile, String messageName, String messageContent, String messageTime, String messageCount) {
        this.messageProfile = messageProfile;
        this.messageName = messageName;
        this.messageContent = messageContent;
        this.messageTime = messageTime;
        this.messageCount = messageCount;



    }
}
