package com.example.androidmain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class origin_alarm_end extends AppCompatActivity {
    Button alarmend;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.origin_close);


        alarmend = (Button) findViewById(R.id.origin_end);
        Intent my_End = new Intent(this, Alarm_Receiver.class);

        // get the intent
        Intent intent = getIntent();

        // if you want to store the data from the intent, use this line. The name should correspond to the passing activity's data name!
        String passed_data = intent.getStringExtra("love letter from HyunWoo");

        alarmend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 버튼이 클릭될 시 할 코드작성
                Toast.makeText(origin_alarm_end.this, "Alarm 종료", Toast.LENGTH_SHORT).show();
                // 알람매니저 취소
                my_End.putExtra("state", "alarm off");
                // 알람취소
                origin_alarm_end.this.sendBroadcast(my_End);

            }
        });
    }

}
