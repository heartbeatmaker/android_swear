package com.example.swearVer2;

import com.dinuscxj.progressbar.CircleProgressBar;

public class Item {

    private int rate;
    private String title;
    private String date;

    private String preferenceName;
    private CircleProgressBar progressBar;

    public Item(int rate, String title, String date, String preferenceName) {
        this.rate = rate;
        this.title = title;
        this.date = date;
        this.preferenceName = preferenceName;
    }


    public String getPreferenceName() {
        return preferenceName;
    }

    public void setPreferenceName(String preferenceName) {
        this.preferenceName = preferenceName;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



    public void setProgressBar(CircleProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public CircleProgressBar getProgressBar() {
        return progressBar;
    }
}
