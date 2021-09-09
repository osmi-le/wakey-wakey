package com.example.androidmain;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ShakyAlarmFinisher extends AppCompatActivity implements SensorEventListener {
    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;

    private static final int SHAKE_THRESHOLD = 800;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    private int mShakeCounter = 0;

    Intent intent_shakyFinisher;
    Intent intent_healthCare;

    TextView tv;
    TextView tv_result;

    ImageView img;
    AnimationDrawable ani;

    MediaPlayer mediaPlayer;

    private long currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shaky_alarm_ender);

        img = findViewById(R.id.img);
        ani = (AnimationDrawable)img.getDrawable();
        tv_result = findViewById(R.id.tv_result);
        //ani.setOneShot(true);//Frame Animation을 한번만 실행

        tv_result.setText(SingletonSleepTimerValue.getInstance().getGapSleepTime());

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        tv = (TextView) findViewById(R.id.tv);

        intent_shakyFinisher = getIntent();

        mediaPlayer = MediaPlayer.create(this, R.raw.first);
        mediaPlayer.start();

        currentTime = System.currentTimeMillis();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            // 350 -> 3.5초 길어질수록 카운트 세기가 어려워짐.
            if (gabOfTime > 300) {
                ani.start();

                lastTime = currentTime;
                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    // Shake 감지할 때마다 업데이트 될 부분.
                    mShakeCounter ++;
                    tv.setText( Integer.toString(mShakeCounter) );
                }
                else
                {
                    if(ani.isRunning())
                        ani.stop();
                }
                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }
        }
        if(mShakeCounter > 5)
        {

            long endTime = System.currentTimeMillis();
            long midTime;
            midTime = ((endTime - currentTime) / 1000)%60;
            Log.i("MOVER", "makeToast");

            // 끌때까지 얼마나 시간이 걸렸는가에 따라서 포인트 쌓음.
            //10sec 20sec 30sec
            if( midTime > 0 && midTime <= 10  )
                Toast.makeText(this, "대박! 포인트 100점 적립!", Toast.LENGTH_SHORT).show();
            else if( midTime > 10 && midTime <= 20)
                Toast.makeText(this, "와우! 포인트 65 적립!", Toast.LENGTH_SHORT).show();
            else if( midTime > 20 && midTime <= 30)
                Toast.makeText(this, "우와! 포인트 30 적립!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "우우! 늦잠꾸러기 포인트 0점 적립", Toast.LENGTH_SHORT).show();
            Log.i("endTime" , Long.toString(midTime));

            super.onBackPressed();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onStart() {
        super.onStart();
        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        Log.d("onDestory() 실행", "서비스 파괴 확인");
    }
}
