package com.shopearn.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.shopearn.receiver.PushReceiver;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by apple on 16/01/17.
 */

public class TimerService extends IntentService {

    private Date futureDate;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public TimerService() {
        super("");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        futureDate = new Date(new Date().getTime() + 86400000);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("TIMER SERVICE" , "isServiceRunning " + "TRUE ");

        sharedPreferences = this.getSharedPreferences("TIMERSERVICE", 0);
        editor = sharedPreferences.edit();

        editor.putBoolean("isServiceRunning", true);

        editor.commit();

        AlarmManager alarmMgr = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        Intent intentReceiver = new Intent(this, PushReceiver.class);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intentReceiver, 0);

// Set the alarm to start at 8:30 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.SECOND , 0);

// setRepeating() lets you specify a precise custom interval--in this case,
// 1 seconds .
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000*60*60*6, alarmIntent);

    }
}
