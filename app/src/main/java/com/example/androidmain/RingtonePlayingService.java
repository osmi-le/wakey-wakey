package com.example.androidmain;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.example.androidmain.Main_Alarm.radio_check;

public class RingtonePlayingService extends Service{


    MediaPlayer mediaPlayer;
    int startId;
    boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "default";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("알람시작")
                    .setContentText("알람음이 재생됩니다.")
                    .setSmallIcon(R.mipmap.ic_launcher)

                    .build();

            startForeground(1, notification);

        }
    }
    ////////////////////////////

    //////////////////////////////////

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String getState = intent.getExtras().getString("state");

        assert getState != null;
        switch (getState) {
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                break;
            default:
                startId = 2;//변경 사항
                break;
        }

        // 알람음 재생 X , 알람음 시작 클릭
        if(!this.isRunning && startId == 1) {

            Toast.makeText(RingtonePlayingService.this,"1번 확인",Toast.LENGTH_SHORT).show();

            Intent back_Intent;
            Intent mid_Intent;
            //back_Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //startActivity(back_Intent);
            //back_Intent = new Intent(getBaseContext(), MainActivity.class);
            //
            if (radio_check == 0) {
                back_Intent = new Intent(getBaseContext(), origin_alarm_end.class);
                back_Intent.setAction(Intent.ACTION_VIEW);
                back_Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                back_Intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                //startService(back_Intent);
                getBaseContext().startActivity(back_Intent);
                mediaPlayer = MediaPlayer.create(this, R.raw.first);
                mediaPlayer.start();

                this.isRunning = true;
                this.startId = 0;
                // instantiate intent object
            }
            else if (radio_check == 1) {
                back_Intent = new Intent(getBaseContext(), Nfc_Close.class);
                back_Intent.setAction(Intent.ACTION_VIEW);
                back_Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                back_Intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                //startService(back_Intent);
                getBaseContext().startActivity(back_Intent);
                mediaPlayer = MediaPlayer.create(this, R.raw.first);
                mediaPlayer.start();

                this.isRunning = true;
                this.startId = 0;
                // instantiate intent object
            }
            else if (radio_check == 2) {
                /*
                mid_Intent = new Intent(getBaseContext(), HealthCare.class);
                mid_Intent.setAction(Intent.ACTION_VIEW);
                mid_Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mid_Intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                //startActivity(back_Intent);
                getBaseContext().startActivity(mid_Intent);
*/
                back_Intent = new Intent(getBaseContext(), ShakyAlarmFinisher.class);
                back_Intent.setAction(Intent.ACTION_VIEW);
                back_Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                back_Intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                //startActivity(back_Intent);
                getBaseContext().startActivity(back_Intent);

                mediaPlayer = MediaPlayer.create(this, R.raw.first);
                mediaPlayer.start();
                this.isRunning = true;
                this.startId = 0;
            }

        }

        // 알람음 재생 O , 알람음 종료 버튼 클릭
        else if(this.isRunning && startId == 0) {

            Toast.makeText(RingtonePlayingService.this,"2번 확인",Toast.LENGTH_SHORT).show();
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();

            this.isRunning = false;
            this.startId = 0;

        }

        // 알람음 재생 X , 알람음 종료 버튼 클릭
        else if(!this.isRunning && startId == 0) {
            Toast.makeText(RingtonePlayingService.this,"3번 확인",Toast.LENGTH_SHORT).show();
            this.isRunning = false;
            this.startId = 0;

        }

        // 알람음 재생 O , 알람음 시작 버튼 클릭
        else if(this.isRunning && startId == 1){
            Toast.makeText(RingtonePlayingService.this,"4번 확인",Toast.LENGTH_SHORT).show();
            this.isRunning = true;
            this.startId = 1;
        }

        else {
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("onDestory() 실행", "서비스 파괴 확인");

    }
}
