package com.example.androidmain;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class Analyze extends AppCompatActivity {

    String f_happy = "happy";
    String f_smile = "smile";
    String f_confused = "confused";
    String f_disappointed = "disappointed";
    String f_sad = "sad";
    String f_dead = "dead";

    ImageView iv_happy;
    ImageView iv_smile;
    ImageView iv_confused;
    ImageView iv_disappointed;
    ImageView iv_sad;
    ImageView iv_dead;


    ListView custommemolist;
    CustomTodayMemoAdapter mAdapter;
    InputStream is = null;
    String getMsg = "";

    GetMyDailyDataTask task = null;
    GetSleepdataTask sleeptask = null;
    LineChart lineChart;

    ArrayList<Sleepdata> sleepdatalist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceSate){
        super.onCreate(savedInstanceSate);
        setContentView(R.layout.dataanalyze);

        mAdapter = new CustomTodayMemoAdapter();
        custommemolist = findViewById(R.id.custommemolist);
        custommemolist.setAdapter(mAdapter);

        lineChart = findViewById(R.id.sleeptimechart);

        sleeptask = new GetSleepdataTask();
        sleeptask.execute();

        try {
            iv_happy = findViewById(R.id.iv_happy);
            iv_smile = findViewById(R.id.iv_smile);
            iv_confused = findViewById(R.id.iv_confused);
            iv_disappointed = findViewById(R.id.iv_disappointed);
            iv_sad = findViewById(R.id.iv_sad);
            iv_dead = findViewById(R.id.iv_dead);

            iv_happy.setImageBitmap(getFileImage(f_happy));
            iv_smile.setImageBitmap(getFileImage(f_smile));
            iv_confused.setImageBitmap(getFileImage(f_confused));
            iv_disappointed.setImageBitmap(getFileImage(f_disappointed));
            iv_sad.setImageBitmap(getFileImage(f_sad));
            iv_dead.setImageBitmap(getFileImage(f_dead));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void setSleepdata(){
        List<Entry> entries = new ArrayList<>();

        for(int i = 0 ; i < sleepdatalist.size(); i++){

            float date = Float.parseFloat(sleepdatalist.get(i).getId());
            float time = Float.parseFloat(sleepdatalist.get(i).changeSleepdata());
            String log_id = sleepdatalist.get(i).getId();
            Log.i("date", log_id);
            Log.i("time", sleepdatalist.get(i).changeSleepdata());

            entries.add(new Entry(date,time));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "수면 시간");

        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        //lineDataSet.setCircleColorHole(Color.BLUE);
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(description);
        //lineChart.animateY(2000, Easing.EasingFunction easing);
        lineChart.invalidate();
    }

    //기분 이미지 화면 출력
    public Bitmap getFileImage(String str) throws IOException {
        AssetManager am = getResources().getAssets();
        InputStream is = null;
        Bitmap bm = null;
        try {
            is = am.open(str + ".png");
            bm = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (is != null) {
            is.close();
        }
        return bm;
    }

    public void setDateMemoData(View view) {

        task = new GetMyDailyDataTask();
        switch (view.getId()) {
            case R.id.iv_smile:
                task.execute("smile");
                break;
            case R.id.iv_happy:
                task.execute("happy");
                break;
            case R.id.iv_confused:
                task.execute("confused");
                break;
            case R.id.iv_sad:
                task.execute("sad");
                break;
            case R.id.iv_dead:
                task.execute("dead");
                break;
        }

    }


    /*date와 feeling을 가지고 오는 코드*/
    class GetMyDailyDataTask extends AsyncTask<String, Integer, String> {

        String ficon = "";

        @Override
        protected String doInBackground(String... strings) {
            try {

                ficon = strings[0];
                ArrayList<NameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("ficon", ficon));

                //Log.i("ficon" , ficon);

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://113.198.234.124/getDateFeelingdata.php");
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
            try{
                JSONObject root = new JSONObject(_result);
                JSONArray results = new JSONArray(root.getString("results"));
                for(int i = 0 ; i <results.length() ; i++){

                    JSONObject content = results.getJSONObject(i);
                    String n_ficon = content.getString("ficon");

                    String mydate = content.getString("mydate");
                    String hcmemo = URLDecoder.decode(content.getString("hcmemo"), "UTF-8");

                    /*
                    Log.i("ficon in json", n_ficon);
                    Log.i("mydate", mydate);
                    Log.i("hcmemo", hcmemo);
                     */

                    mAdapter.addData(n_ficon, mydate, hcmemo);
                    mAdapter.notifyDataSetChanged();
                }
                task = null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class GetSleepdataTask extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... strings) {
            try {

                ArrayList<NameValuePair> list = new ArrayList<>();
                //list.add(new BasicNameValuePair("ficon", ficon));

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://113.198.234.124/getSleepdata.php");
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
            try{
                JSONObject root = new JSONObject(_result);
                JSONArray results = new JSONArray(root.getString("results"));
                for(int i = 0 ; i <results.length() ; i++){

                    JSONObject content = results.getJSONObject(i);
                    String id = content.getString("id");
                    String sleephour = content.getString("sleephour");
                    String sleepminute = content.getString("sleepminute");

/*
                    Log.i("id", id);
                    Log.i("sleephour", sleephour);
                    Log.i("sleepmiute", sleepminute);


                    mAdapter.addData(n_ficon, mydate, hcmemo);
                    mAdapter.notifyDataSetChanged();*/
                    sleepdatalist.add(new Sleepdata(id, sleephour, sleepminute));
                    /*
                    String newid = sleepdatalist.get(i).getId();
                    String newhour = sleepdatalist.get(i).getSleephour();
                    Log.i("newid", newid);
                    Log.i("newhour", newhour);*/
                }
                task = null;
                setSleepdata();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
