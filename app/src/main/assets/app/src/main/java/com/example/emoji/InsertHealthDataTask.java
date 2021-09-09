package com.example.emoji;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

public class InsertHealthDataTask extends AsyncTask<DailyData, Void, String> {

    private WeakReference<Activity> mContext;//mainactivity에서 토스트 메시지를 띄우기 위한 약한 참조
    InputStream is = null;
    String getMsg = "";

    public InsertHealthDataTask(Activity context) {
        mContext = new WeakReference<>(context);
    }

    @Override
    protected String doInBackground(DailyData... dailydata) {

        DailyData dailyData = dailydata[0];
        String sleephour = dailyData.getDaily_sleep_hour();
        String sleepminute = dailyData.getDaily_sleep_minute();
        String hcmemo = dailyData.getDaily_memo();
        String ficon = dailyData.getDaily_icon();

        try {
            ArrayList<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("sleephour", sleephour));
            list.add(new BasicNameValuePair("sleepminute", sleepminute));
            list.add(new BasicNameValuePair("hcmemo", hcmemo));
            list.add(new BasicNameValuePair("ficon", ficon));

            Log.i("sleephour",sleephour);
            Log.i("sleepminute", sleepminute);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://113.198.234.124/InsertHealthData.php");
            httppost.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

            BufferedReader reader = new BufferedReader((new InputStreamReader(is, "UTF-8")));
            String line = "";
            getMsg = "";

            while ((line = reader.readLine()) != null) {
                getMsg += line;
            }
            is.close();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getMsg;
    }

    @Override
    protected void onPostExecute(String _result) {

        Activity activity = mContext.get();
        if (activity == null || activity.isFinishing())
            return;

        //json parsing 부분
        try {
            JSONObject jsondata = new JSONObject(_result);
            int code = jsondata.getInt("code");

            //성공하였을 때 code값은 1, 실패하였을 때의 code값은 0
            if (code == 1) {
                Toast.makeText(activity, "저장완료", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(activity, "저장실패", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
