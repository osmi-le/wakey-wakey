package com.example.androidmain;

import java.io.Serializable;

public class EventData implements Serializable {
    private String summary;
    private String starttime;
    private String endtime;
    private String startdate;

    public EventData(String summary, String starttime, String endtime, String startdate) {
        this.summary = summary;
        this.starttime = starttime;
        this.endtime = endtime;
        this.startdate = startdate;
    }

    public String getStarttime() {
        return starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }
}
