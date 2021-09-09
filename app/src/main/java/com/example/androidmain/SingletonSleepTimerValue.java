package com.example.androidmain;

public class SingletonSleepTimerValue {
    private static final SingletonSleepTimerValue instance = new SingletonSleepTimerValue();

    private int sleepCounter =0;
    private int shakeCounter=0;
    private String gapSleepTime ="";
    private float maxLuxValue = 0;

    private String hour;
    private String min;

    private SingletonSleepTimerValue(){

    }

    public static SingletonSleepTimerValue getInstance() {
        return instance;
    }

    public String getHour() {
        return hour;
    }

    public String getMin() {
        return min;
    }

    public float getMaxLuxValue() {
        return maxLuxValue;
    }

    public int getShakeCounter() {
        return shakeCounter;
    }

    public int getSleepCounter() {
        return sleepCounter;
    }

    public String getGapSleepTime() {
        return gapSleepTime;
    }

    public void setGapSleepTime(String gapSleepTime) {
        this.gapSleepTime = gapSleepTime;
    }

    public void setMaxLuxValue(float maxLuxValue) {
        this.maxLuxValue = maxLuxValue;
    }

    public void setShakeCounter(int shakeCounter) {
        this.shakeCounter = shakeCounter;
    }

    public void setSleepCounter(int sleepCounter) {
        this.sleepCounter = sleepCounter;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public void setMin(String min) {
        this.min = min;
    }
}
