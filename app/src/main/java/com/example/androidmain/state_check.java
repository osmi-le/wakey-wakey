package com.example.androidmain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class state_check extends AppCompatActivity {
    Button back_State;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.state_check);

        back_State = (Button) findViewById(R.id.back_state);

        // get the intent
        Intent intent = getIntent();

        // if you want to store the data from the intent, use this line. The name should correspond to the passing activity's data name!
        String passed_data = intent.getStringExtra("love letter from HyunWoo");

        back_State.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼이 클릭될 시 할 코드작성
                Intent back_Intent = new Intent(state_check.this, MainActivity.class);

                back_Intent.putExtras(back_Intent);
                startActivity(back_Intent);
                finish();

            }
        });
    }
}
