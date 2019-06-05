package com.example.swearVer2;

public class AlertItem {

    private String alertContent;
    private String alertTime;

    public AlertItem(String alertContent, String alertTime) {
        this.alertContent = alertContent;
        this.alertTime = alertTime;
    }


    public String getAlertContent() {
        return alertContent;
    }

    public void setAlertContent(String alertContent) {
        this.alertContent = alertContent;
    }

    public String getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(String alertTime) {
        this.alertTime = alertTime;
    }

}
