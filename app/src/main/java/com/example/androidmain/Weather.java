package com.example.androidmain;

public class Weather {
    int lat;
    int ion;
    int temperature;
    int cloudy;
    String city;
    String icon;

    public void setLat(int lat) {this.lat = lat;}
    public void setIon(int ion) {this.ion = ion;}
    public void setTemperature(int t){this.temperature = t;}
    public void setCloudy(int cloudy){this.cloudy = cloudy;}
    public void setCity(String city){this.city = city;}
    public void setIcon(String icon){this.icon = icon;}

    public int getLat(){ return lat;}
    public int getIon() { return ion;}
    public int getTemperature() { return temperature;}
    public int getCloudy() { return cloudy; }
    public String getCity() { return city; }
    public String getIcon() {
        return icon;
    }
}
