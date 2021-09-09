package com.example.androidmain;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.androidmain.Alram_add.tvres;

/*
 * 알람 리스트를 출력하는 asynctask
 *
 */
public class AlarmListAsyncTask extends AsyncTask<Integer, Void, String> {
    int flag;

    @Override
    protected String doInBackground(Integer... params) {
        flag = params[0];

        String serverURL = "http://113.198.234.124/newalarmlist.php";
        String email = UserData.getUserEmail();

        String postParameters = "email=" + email;
        Log.d("AlarmListTask", Integer.toString(flag));

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

            String line = ""; //결과를 한 줄씩 읽어서 저장할 변수
            String getMsg = ""; //getMsg 변수 초기화(리스트 갱신 등의 이유로 기존의 값이 남아 있지 않도록 하기 위해)

            // 읽을 내용이 없을 때까지 반복
            while((line = bufferedReader.readLine())!=null){
                Log.d("Accountinfo", line);
                getMsg += line;
            }

            bufferedReader.close();
            inputStream.close();

            return getMsg;

        } catch (Exception e) {
            Log.d("error", "InsertData: Error ", e);
            return new String("Error: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result) {
        ArrayList<String> alarmData = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray results = new JSONArray(jsonObject.getString("results"));

            if (flag == 1){
                int size = 0;

                if(results.length()>3){
                    size = 3;
                }else{
                    size = results.length();
                }

                for (int i = 0; i < size; i++) {
                    JSONObject content = results.getJSONObject(i);
                    String hour = content.getString("hour");
                    String minute = content.getString("minute");
                    Log.d("Accountinfo", hour + minute);
                    String AlarmDate = content.getString("added_date");
                    alarmData.add(AlarmDate+"    "+hour + ":" + minute);
                }

                MainActivity.main_Alram.setText(TextUtils.join("\n\n", alarmData));
            }else if(flag == 2){
                for (int i = 0; i < results.length(); i++) {
                    JSONObject content = results.getJSONObject(i);
                    String hour = content.getString("hour");
                    String minute = content.getString("minute");
                    String AlarmDate = content.getString("added_date");
                    Log.d("Accountinfo", hour + minute);
                    alarmData.add(AlarmDate+"    "+hour + ":" + minute);
                }

                Alram_add.tvres.setText(TextUtils.join("\n\n", alarmData));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}