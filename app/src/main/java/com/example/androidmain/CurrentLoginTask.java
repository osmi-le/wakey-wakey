package com.example.androidmain;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CurrentLoginTask extends AppCompatActivity {
    GoogleAccountCredential credential;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    AlertDialog.Builder oDialog;
    String mJsonString;



    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_main);

        oDialog = new AlertDialog.Builder(this);
        //////////// 구글 인증창 띄우는 부분 //////////
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        chooseAccount();

    }

    /////////////////////////////////////// 가입하기 버튼 누르면 실행되는 메소드
    public void signInUser(){
        String email = credential.getSelectedAccountName();

        //////////////////////////// AsyncTask 실행한 후 서버 연결합니다
        InsertData task = new InsertData();
        task.execute("http://113.198.234.124/login.php", email);
    }

    //////////////////////////PHP 서버로 데이터 보내는 코드 ////////////////////////////////
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog; // 뭔지 모르겠습니다 아마 로딩창 같습니다

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(CurrentLoginTask.this,
                    "Please Wait", null, true, true);

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null){
                credential = null;
                UserData.setCredential(null);
                Log.d("currentlogintask", "fail");
                oDialog.setMessage("가입되지 않은 계정입니다.\n 새로 생성하시겠습니까?")
                        .setTitle("로그인")
                        .setPositiveButton("아니오", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Intent it = new Intent(getApplicationContext(), SubLogin.class);
                                startActivity(it);
                                finish();
                            }
                        })
                        .setNeutralButton("예", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Intent it = new Intent(getApplicationContext(), AccLogin.class);
                                startActivity(it);
                                finish();
                            }
                        })
                        .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                        .show();
            }
            else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("login");
                    Log.d("JSONCurrentLogin", "success");
                    for(int i=0;i<jsonArray.length();i++){

                        JSONObject item = jsonArray.getJSONObject(i);

                        String email = item.getString("email");
                        String name = item.getString("name");

                        UserData.setUserEmail(email);
                        UserData.setUserName(name);
                        UserData.setCredential(credential);
                    }
                    Intent it = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(it);
                    finish();
                } catch (JSONException e) {

                    Log.d("JSONCurrentLogin", "showResult : ", e);
                }

            }

            progressDialog.dismiss();
            Log.d("OnPostExecute state", "POST response  - " + result);

        }


        @Override
        protected String doInBackground(String... params) {

            String email = params[1];
            String serverURL = params[0];
            // 전송할 String 문자열 postParameters에 저장
            String postParameters = "email=" + email;

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

                String line = null;
                StringBuilder stringBuilder = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(line);
                    Log.d("line", line);
                }

/*
                if(line.equals("success")){
                    UserData.setCredential(credential);

                    Log.d("line2", line);
                    //UserData.setUserEmail(line);
                    Log.d("line", line);
                    //UserData.setUserName(line);
                    Log.d("currentlogintask", "success");
                    Intent it = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(it);
                    finish();
                }else{
                    credential = null;
                    UserData.setCredential(null);
                    Log.d("currentlogintask", "fail");
                    oDialog.setMessage("가입되지 않은 계정입니다.\n 새로 생성하시겠습니까?")
                            .setTitle("로그인")
                            .setPositiveButton("아니오", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Intent it = new Intent(getApplicationContext(), SubLogin.class);
                                    startActivity(it);
                                    Log.i("Dialog", "취소");
                                }
                            })
                            .setNeutralButton("예", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Intent it = new Intent(getApplicationContext(), AccLogin.class);
                                    startActivity(it);
                                }
                            })
                            .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                            .show();
                    finish();
                }

                */
                bufferedReader.close();
                inputStream.close();

                return stringBuilder.toString().trim();

            } catch (Exception e) {
                Log.d("error", "InsertData: Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }
    //////////////////////////PHP 서버로 데이터 보내는 코드 ////////////////////////////////

    //////////////////////////// 이 밑으로는 서치한 구글 코드인데 안 중요합니다////////////

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == RESULT_OK) {
                } else {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                } else {
                    chooseAccount();
                }
                break;
        }
        signInUser();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    private void chooseAccount() {
        startActivityForResult(
                credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);


    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                googleAPI.isGooglePlayServicesAvailable(this);
        if(connectionStatusCode != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(connectionStatusCode)) {
                googleAPI.getErrorDialog(this, connectionStatusCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }

        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(() -> {
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            apiAvailability.getErrorDialog(this,
                    connectionStatusCode,
                    REQUEST_GOOGLE_PLAY_SERVICES).show();

        });
    }
}
