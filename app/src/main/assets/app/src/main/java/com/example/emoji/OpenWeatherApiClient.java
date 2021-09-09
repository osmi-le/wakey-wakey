package com.example.emoji;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class OpenWeatherApiClient {

    final static String openWeatherURL = "https://api.openweathermap.org/data/2.5/weather";

    public Weather getWeather(int lat, int lon){
        Weather w = new Weather();
        String urlString = openWeatherURL + "?lat=" + lat + "&lon=" + lon + "&appid=df0f022ca9b6565d31bfe22b9e1e4b9b";

        try{
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();//HTTP Connection 열고 호출
            urlConnection.setReadTimeout(3000);//CONNECT_TIMEOUT
            urlConnection.setConnectTimeout(3000);//DATARETRIEVAL_TIMEOUT

            InputStream is = new BufferedInputStream(urlConnection.getInputStream());//urlConnection으로부터 getInputStream을 통해 InputStream 리턴 받음

            JSONObject json = new JSONObject(getStringFromInputStream(is));//inputStream is를 String으로 변환 후 JSONObject로  변환

            //parse JSON
            w = parseJSON(json);
            w.setIon(lon);
            w.setLat(lat);

        } catch (MalformedURLException e) {
            System.err.println("Malformed URL");
            e.printStackTrace();
        } catch(JSONException e){
            System.err.println("URL Connection failed");
        } catch (IOException e) {
            System.err.println("JSON parsing error");
            e.printStackTrace();
        }

        return w;
    }

    private static String getStringFromInputStream(InputStream is){
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;

        try{
            br = new BufferedReader(new InputStreamReader(is));
            while((line=br.readLine())!=null)
                sb.append(line);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(br!=null){
                try{
                    br.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    private Weather parseJSON(JSONObject json) throws JSONException{
        Weather w = new Weather();
        JSONArray results = new JSONArray(json.getString("weather"));
        JSONObject content = results.getJSONObject(0);
        String micon = content.getString("icon");

        w.setTemperature(json.getJSONObject("main").getInt("temp"));//"main" json문서를 얻고
        w.setCity(json.getString("name"));
        w.setIcon(micon);


        return w;
    }
}
