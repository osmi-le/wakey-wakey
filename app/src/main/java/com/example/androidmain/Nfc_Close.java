package com.example.androidmain;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Nfc_Close extends AppCompatActivity {
    Button home_back;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private static String tagNum = null;
    private TextView tagDesc;
    static ArrayList<String> nfcTagData = new ArrayList<>();
    static boolean endAsyncTask = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_close);
        home_back = (Button) findViewById(R.id.home_back) ;
        tagDesc = (TextView) findViewById(R.id.tag_add);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);



        /////////// nfc 읽어옴
        GetNfcAsyncTask getNfcAsyncTask = new GetNfcAsyncTask();
        getNfcAsyncTask.execute(tagNum);


        // get the intent

        // if you want to store the data from the intent, use this line. The name should correspond to the passing activity's data name!
        home_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼이 클릭될 시 할 코드작성

                Intent back_Intent = new Intent(Nfc_Close.this, MainActivity.class);

                back_Intent.putExtras(back_Intent);

                startActivity(back_Intent);
                finish();

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

        Intent my_End = new Intent(this, Alarm_Receiver.class);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            byte[] tagId = tag.getId();
            //tagDesc.setText("TagID: " + toHexString(tagId));
            tagNum = toHexString(tagId);
            //Log.d(Nfc_Close.class.getName(),toHexString(tagId));


            for(int i = 0 ; i<nfcTagData.size(); i++) {
                Log.d("nfctag card1", nfcTagData.get(i));
                Log.d("nfctag card2", tagNum);
                if (tagNum.equals("4B5B3BAB") || tagNum == "4B5B3BAB") {
                    Toast.makeText(Nfc_Close.this, "Alarm 종료 인식", Toast.LENGTH_SHORT).show();
                    // 알람매니저 취소
                    Intent back_Intent = new Intent(Nfc_Close.this, HealthCare.class);

                    back_Intent.putExtras(back_Intent);
                    startActivity(back_Intent);
                    super.onBackPressed();

                    my_End.putExtra("state", "alarm off");

                    // 알람취소
                    Nfc_Close.this.sendBroadcast(my_End);
                } else {
                    Toast.makeText(Nfc_Close.this, "올바르지 않은 카드 입니다.", Toast.LENGTH_SHORT).show();


                }
            }
        }
    }
    public static final String CHARS = "123456789ABCDEF";
    public static String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; ++i) {
            sb.append(CHARS.charAt((data[i] >> 4) & 0x0F)).append(
                    CHARS.charAt(data[1] & 0x0F));
        }
        return sb.toString();
    }
}
