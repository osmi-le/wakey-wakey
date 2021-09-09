package com.example.androidmain;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SubLogin extends AppCompatActivity {
    private Button login_btn; //회원가입 버튼
    private TextView login_tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        login_btn = findViewById(R.id.login_btn);
        login_tv = findViewById(R.id.login_tv);
        login_tv.setText(Html.fromHtml("<u>" + "기존 아이디로 로그인하기" + "</u>"));
    }

    // 버튼 눌렀을 때
    public void doLogin(View view) { //창 전환시 이 창은 종료해야함
        Intent it = new Intent(getApplicationContext(), AccLogin.class);
        startActivity(it);
        finish();
    }

    // 로그인 텍뷰 눌렀을 때
    public void usingLogin(View view) {
        Intent it = new Intent(getApplicationContext(), CurrentLoginTask.class);
        startActivity(it);
        finish();
    }
}
