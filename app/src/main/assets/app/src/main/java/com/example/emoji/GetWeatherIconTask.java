package com.example.emoji;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class GetWeatherIconTask extends AsyncTask<String, Void, Bitmap> {

    ImageView iv_icon;

    public GetWeatherIconTask(ImageView iv_icon){
        this.iv_icon = iv_icon;
    }
    @Override
    protected Bitmap doInBackground(String... strings) {
        String micon = strings[0];
        String iconurl = "http://openweathermap.org/img/wn/" + micon + "@2x.png";
        Bitmap wicon = null;

        try{
            URL url = new URL(iconurl);
            URLConnection conn = url.openConnection();
            conn.connect();

            int iconSize = conn.getContentLength();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), iconSize);
            wicon = BitmapFactory.decodeStream(bis);
            bis.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wicon;
    }

    protected void onPostExecute(Bitmap bitmap){
        iv_icon.setImageBitmap(bitmap);
    }
}
