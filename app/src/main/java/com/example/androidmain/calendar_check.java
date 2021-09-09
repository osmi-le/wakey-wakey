package com.example.androidmain;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class calendar_check extends AppCompatActivity {
    Button back_Calender;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calender_check);

        back_Calender = (Button) findViewById(R.id.back_calender);

        back_Calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼이 클릭될 시 할 코드작성
                Intent back_Intent = new Intent(calendar_check.this, MainActivity.class);

                back_Intent.putExtras(back_Intent);
                startActivity(back_Intent);
                finish();

            }
        });

        // get the intent


        // if you want to store the data from the intent, use this line. The name should correspond to the passing activity's data name!
        //String passed_data = intent.getStringExtra("love letter from HyunWoo");

        TextView tvres = findViewById(R.id.tvres);
        List<String> eventStrings = new ArrayList<>();

        Intent intent = getIntent();

        List<EventData> eventData = (ArrayList<EventData>) intent.getSerializableExtra("eventlist");
        Log.d(calendar_check.class.getName(),String.valueOf(eventData));
        //List<String> eventStrings  = intent.getExtras().getStringArrayList("eventlist");
        for (EventData e : eventData) {
            String s;
            if (e.getStarttime().equals(" ")) {
                s = String.format(Locale.KOREA, "%s, %s일",
                        e.getSummary(), e.getStartdate());
            } else {
                s = String.format(Locale.KOREA, "%s, %s일 %s ~ %s",
                        e.getSummary(), e.getStartdate(), e.getStarttime(), e.getEndtime());
            }
            eventStrings.add(s);
        }

        tvres.setText(TextUtils.join("\n\n", eventStrings));
    }
}
