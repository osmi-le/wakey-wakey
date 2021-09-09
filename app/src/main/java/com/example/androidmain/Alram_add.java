package com.example.androidmain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Alram_add extends AppCompatActivity {
    Button back_Alram;
    static TextView tvres;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alram_add);

        tvres = findViewById(R.id.tvres);
        back_Alram = (Button) findViewById(R.id.back_Alram);


        AlarmListAsyncTask alarmListAsyncTask = new AlarmListAsyncTask();
        alarmListAsyncTask.execute(2);

        // get the intent

        // if you want to store the data from the intent, use this line. The name should correspond to the passing activity's data name!

        back_Alram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼이 클릭될 시 할 코드작성
                Intent back_Intent = new Intent(Alram_add.this, MainActivity.class);

                back_Intent.putExtras(back_Intent);
                startActivity(back_Intent);
                finish();

            }
        });


    }
}
