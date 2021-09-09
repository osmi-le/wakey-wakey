package com.example.androidmain;

public class CalendarData {

    private String summary;
    private String calId;

    public CalendarData(String calId, String summary) {
        this.summary = summary;
        this.calId = calId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCalId() {
        return calId;
    }

    public void setCalId(String calId) {
        this.calId = calId;
    }
}
