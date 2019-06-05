package com.example.swearVer2;

public class ReportItem  {

    private String reportTitle;
    private String reportTime;
    private String reportPreferenceName;
//    private Bitmap reportImage;

    public ReportItem(String reportTitle, String reportTime, String reportPreferenceName) {
        this.reportTitle = reportTitle;
        this.reportTime = reportTime;
        this.reportPreferenceName = reportPreferenceName;
//        this.reportImage = reportImage;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getReportPreferenceName() {
        return reportPreferenceName;
    }

    public void setReportPreferenceName(String reportPreferenceName) {
        this.reportPreferenceName = reportPreferenceName;
    }



//    public Bitmap getReportImage() {
//        return reportImage;
//    }
//
//    public void setReportImage(Bitmap reportImage) {
//        this.reportImage = reportImage;
//    }

}
