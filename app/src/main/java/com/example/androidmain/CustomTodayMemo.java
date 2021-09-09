package com.example.androidmain;

public class CustomTodayMemo {

    String ficon, mydate, hcmemo;

    CustomTodayMemo(String _ficon , String _mydate, String _hcmemo){
        ficon = _ficon;
        mydate = _mydate;
        hcmemo = _hcmemo;
    }

    public String getFicon() {
        return ficon;
    }

    public String getHcmemo() {
        return hcmemo;
    }

    public String getMydate() {
        return mydate;
    }
}
