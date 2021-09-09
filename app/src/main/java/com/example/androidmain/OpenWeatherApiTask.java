package com.example.androidmain;

import android.os.AsyncTask;

public class OpenWeatherApiTask extends AsyncTask<Integer, Void, Weather> {
    @Override
    protected Weather doInBackground(Integer... integers) {
        OpenWeatherApiClient client = new OpenWeatherApiClient();

        int lat = integers[0];//변경
        int lon = integers[1];//부분

        Weather w = client.getWeather(lat, lon);

        return w;
    }
}
