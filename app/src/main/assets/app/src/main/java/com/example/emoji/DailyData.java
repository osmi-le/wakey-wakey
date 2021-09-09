package com.example.emoji;

import java.util.Date;

public class DailyData {
    private String daily_id;//테이블 구조를 아직 제대로 설계를 못해서 쉽고 빠르게 주키 잡으려고 일단 넣음
    private Date daily_date;//오늘 날짜. 이게 주키가 되어야 하는걸까...?
    private String daily_sleep_hour;//오늘 잔 시간(시간)
    private String daily_sleep_minute;//오늘 잔 시간(분)
    private String daily_memo;//오늘 메모
    private String daily_icon;//기분 아이콘

    //setter
    public void setDaily_id(String daily_id) {
        this.daily_id = daily_id;
    }

    public void setDaily_date(Date daily_date) {
        this.daily_date = daily_date;
    }

    public void setDaily_icon(String daily_icon) {
        this.daily_icon = daily_icon;
    }

    public void setDaily_sleep_hour(String daily_sleep_hour) {
        this.daily_sleep_hour = daily_sleep_hour;
    }

    public void setDaily_sleep_minute(String daily_sleep_minute){
        this.daily_sleep_minute = daily_sleep_minute;
    }

    public void setDaily_memo(String daily_memo) {
        this.daily_memo = daily_memo;
    }

    //getter
    public String getDaily_id() {
        return daily_id;
    }

    public Date getDaily_date() {
        return daily_date;
    }

    public String getDaily_icon() {
        return daily_icon;
    }

    public String  getDaily_sleep_hour() {
        return daily_sleep_hour;
    }

    public String getDaily_sleep_minute() {
        return daily_sleep_minute;
    }

    public String getDaily_memo() {
        return daily_memo;
    }
}
