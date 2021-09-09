package com.example.androidmain;

import android.os.AsyncTask;
import android.util.Log;

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
import java.util.ArrayList;


public class GetMyDailyDataTask extends AsyncTask<String, Integer, String> {

        InputStream is = null;
        String getMsg = "";
        String ficon = "";

        CustomTodayMemoAdapter mAdapter;

        @Override
        protected String doInBackground(String... strings) {
            try {

                ficon = strings[0];

                ArrayList<NameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("ficon", ficon));

                Log.i("ficon" , ficon);

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://192.168.91.1/getDateFeelingdata.php");
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
                    //String ficon = content.getString("ficon");
                    String mydate = content.getString("mydate");
                    String hcmemo = content.getString("hcmemo");

                    mAdapter.addData(ficon, mydate, hcmemo);
                    mAdapter.notifyDataSetChanged();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


