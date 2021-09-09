package com.example.emoji;

import android.util.Log;

public class Sleepdata {
    private String id;
    private String sleephour;
    private String sleepminute;

    public Sleepdata(String _id, String _sleephour, String _sleepminute){
        id = _id;
        sleephour = _sleephour;
        sleepminute = _sleepminute;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSleephour(String sleephour) {
        this.sleephour = sleephour;
    }

    public void setSleepminute(String sleepminute) {
        this.sleepminute = sleepminute;
    }

    public String getId() {
        Log.i("id", id);
        return id;
    }

    public String getSleephour() {
        return sleephour;
    }

    public String getSleepminute() {
        return sleepminute;
    }

    public String changeSleepdata(){
        String sleeptime;
        sleeptime = sleephour + "." + sleepminute;

        Log.i("sleeptime", sleeptime);
        return sleeptime;
    }
}
