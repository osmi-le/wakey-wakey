package com.example.androidmain;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Nfctag_add extends AppCompatActivity {

    Button Nfc_back;
    Button Nfc_add;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private static String tagNum = null;
    private TextView tagDesc;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfctag_add);

        tagDesc = (TextView) findViewById(R.id.tag_add);
        Nfc_add = findViewById(R.id.nfc_add);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Nfc_back = (Button) findViewById(R.id.nfc_back);

        // get the intent

        // if you want to store the data from the intent, use this line. The name should correspond to the passing activity's data name!

        Nfc_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /////////////// nfc 추가하는 asynctask 부르는 코드 //////////////
                NfcAddAsyncTask nfcAddAsyncTask = new NfcAddAsyncTask();
                nfcAddAsyncTask.execute(tagNum);
                /////////////////////////////////////////////////////////////////
            }
        });
    }
    @Override
    protected void onPause() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            byte[] tagId = tag.getId();
            tagDesc.setText("TagID: " + toHexString(tagId));
            tagNum = toHexString(tagId);
            Nfc_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 버튼이 클릭될 시 할 코드작성
                    Intent back_Intent = new Intent(Nfctag_add.this, MainActivity.class);
                    NfcAddAsyncTask nfcAddAsyncTask = new NfcAddAsyncTask();
                    nfcAddAsyncTask.execute(tagNum);

                    back_Intent.putExtras(back_Intent);
                    startActivity(back_Intent);
                    finish();

                }
            });
        }
    }
    public static final String CHARS = "123456789ABCDEF";
    public static String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; ++i){
            sb.append(CHARS.charAt((data[i] >> 4) & 0x0F)).append(
                    CHARS.charAt(data[1] & 0x0F));
        }

        return sb.toString();
    }
    /*
     * nfc 정보 등록하는 클래스
     *
     */
    public class NfcAddAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://113.198.234.124/addNFC.php";
            String nfcid = params[0];
            String email = UserData.getUserEmail();

            String postParameters = "email=" + email + "&" + "tag=" + nfcid;

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
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                ;

                if (sb.toString().equals("success")) {
                    Log.d("AlarmTask", "DB 삽입 success");

                } else {
                    Log.d("AlarmTask", "DB 삽입 fail");
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
    }
}

