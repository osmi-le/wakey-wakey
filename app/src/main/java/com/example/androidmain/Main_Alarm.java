package com.example.androidmain;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.TimePicker;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Optional;

public class Main_Alarm extends AppCompatActivity {
    static int radio_check = 0 ;

    private RadioButton[] radioButtons = new RadioButton[3];
    AlarmManager alarm_manager;
    TimePicker alarm_timepicker;
    Context context;
    //PendingIntent pendingIntent;
    ////////////////////////////////
    TextView tv;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private static String tagNum = null;
    private TextView tagDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_main);
        radioButtons[0] = findViewById(R.id.origin_radio);
        radioButtons[1] = findViewById(R.id.nfc_radio);
        radioButtons[2] = findViewById(R.id.shake_radio);

/////////////////////

/////////////////////////
        this.context = this;

        // 알람매니저 설정
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // 타임피커 설정
        alarm_timepicker = findViewById(R.id.time_picker);

        // Calendar 객체 생성
        final Calendar calendar = Calendar.getInstance();

        // 알람리시버 intent 생성
        final Intent my_intent = new Intent(this.context, Alarm_Receiver.class);

        // 알람 저장 버튼
        Button alarm_on = findViewById(R.id.btn_start);

        alarm_on.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                int i = 0;
                Optional<RadioButton> optionalRadioButton = Arrays.stream(radioButtons).filter(CompoundButton::isChecked).findFirst();
                if (optionalRadioButton.isPresent()) {
                    for (i = 0; i < radioButtons.length; i++) {
                        if (optionalRadioButton.get().equals(radioButtons[i]))

                            break;
                    }
                } else {
                    // 라디오버튼 안 눌렀을 때 처리
                    Toast.makeText(getApplicationContext(),"알람의 종류를 선택해 주세요",Toast.LENGTH_LONG).show();
                    return;
                }
                // 이제 i를 사용
                if (i == 0){
                    Toast.makeText(Main_Alarm.this, "1 예정 ", Toast.LENGTH_SHORT).show();
                    radio_check = 0 ;
                }
                else if (i == 1){
                    Toast.makeText(Main_Alarm.this, "2 예정 " , Toast.LENGTH_SHORT).show();
                    radio_check = 1;
                }
                else if (i == 2){
                    Toast.makeText(Main_Alarm.this, "3 예정 ", Toast.LENGTH_SHORT).show();
                    radio_check = 2;
                }

                // calendar에 시간 셋팅
                calendar.set(Calendar.HOUR_OF_DAY, alarm_timepicker.getHour());
                calendar.set(Calendar.MINUTE, alarm_timepicker.getMinute());


                // 시간 가져옴
                DatePicker datePicker = findViewById(R.id.date_alarm);
                //Calendar today = Calendar.getInstance();
                String years = String.valueOf(datePicker.getYear());
                String month = String.valueOf(datePicker.getMonth()+1);
                String day = String.valueOf(datePicker.getDayOfMonth());

                String hour = String.valueOf(alarm_timepicker.getHour());
                String minute = String.valueOf(alarm_timepicker.getMinute());

                Toast.makeText(Main_Alarm.this,"Alarm 예정 " + hour + "시 " + minute + "분" + years + "년" + month + "월" + day + "일",Toast.LENGTH_SHORT).show();

                // 데이터 베이스에 알람 추가하기
                AlarmAsyncTask alarmAsyncTask = new AlarmAsyncTask();
                alarmAsyncTask.execute(hour, minute, years, month, day);

                // reveiver에 string 값 넘겨주기
                my_intent.putExtra("state", "alarm on");

                pendingIntent = PendingIntent.getBroadcast(Main_Alarm.this, 0, my_intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                // 알람셋팅
                alarm_manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        pendingIntent);

                //boolean checked = ((RadioButton) v).isChecked();


            }
        });

        // 알람 정지 버튼
        Button alarm_off = findViewById(R.id.btn_finish);
        alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Main_Alarm.this, "Alarm 종료", Toast.LENGTH_SHORT).show();
                // 알람매니저 취소
                alarm_manager.cancel(pendingIntent);

                my_intent.putExtra("state", "alarm off");
                // 알람취소
                Main_Alarm.this.sendBroadcast(my_intent);
            }
        });


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Button back_home = findViewById(R.id.back_home);

        back_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼이 클릭될 시 할 코드작성
                Intent back_Intent = new Intent(Main_Alarm.this, MainActivity.class);

                back_Intent.putExtras(back_Intent);
                startActivity(back_Intent);
                finish();

            }
        });


    }
}
