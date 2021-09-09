package com.example.androidmain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Alarm_Close extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent it = new Intent(context, MainActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(it);
        }
    }
}
