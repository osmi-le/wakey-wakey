package com.example.androidmain;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.androidmain.Main_Alarm;
import com.example.androidmain.UserData;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * 알람 추가하는 asynctask
 *
 */
public class AlarmAsyncTask extends AsyncTask<String, Void, String> {

    Main_Alarm main_alarm ;

    @Override
    protected String doInBackground(String... params) {

        String serverURL = "http://113.198.234.124/newaddalarm.php";
        String hour = params[0];
        String minute = params[1];
        String email = UserData.getUserEmail();
        String date = params[2]+"-"+params[3]+"-"+params[4];

        String postParameters = "email=" + email + "&" + "hour=" + hour + "&" + "minute=" + minute + "&" + "date=" +  date;
        Log.d("AlarmTask", postParameters);

        try {
            URL url = new URL(serverURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();

            ////////////// 전송 //////////////
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8")); //php로 전송
            outputStream.flush();
            outputStream.close();

            ////////////// php에서 echo같은걸로 보낸 데이터 받음 //////////////
            int responseStatusCode = httpURLConnection.getResponseCode();
            Log.d("code", "POST response code - " + responseStatusCode);

            InputStream inputStream;
            if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            }
            else{
                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line = null;

            while(( line = bufferedReader.readLine() )!=null){
                sb.append(line);
            };
            Log.d("line2", sb.toString());

            if (sb.toString().equals("success")){
                Log.d("line2", sb.toString());
                Log.d("AlarmTask","DB 삽입 success");
            }
            else{
                Log.d("line2", sb.toString());
                Log.d("AlarmTask","DB 삽입 fail");
            }

            bufferedReader.close();
            inputStream.close();

            return sb.toString();

        } catch (Exception e) {
            Log.d("error", "InsertData: Error ", e);
            return new String("Error: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }

    public void execute(int hour, int minute) {
    }
}