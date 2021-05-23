package com.vic.notification;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationManagerCompat;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.util.Calendar;


public class TimeSet extends IntentService {

    public TimeSet(){
        super("TimeSet");
    }

    Calendar calendar;
    @Override
    protected void onHandleIntent(Intent intent) {

        int snooze;
        MainActivity obj = new MainActivity();
        Bundle extras =  intent.getExtras();
        calendar = Calendar.getInstance();


        try{
            snooze = extras.getInt("time");
            calendar.add(Calendar.MINUTE, snooze);
        }
        catch(Exception e){
            int minuteValue = obj.finalMinute;
            int hourValue = obj.finalHour;
            calendar.add(Calendar.HOUR, hourValue);
            calendar.add(Calendar.MINUTE, minuteValue);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(TimeSet.this, getString(R.string.alarm_set_to) + " " + DateFormat.format("hh:mm a", calendar.getTime()).toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        Intent disableSet = new Intent();
        disableSet.setAction("disableSet");
        this.sendBroadcast(disableSet);
        Intent detailsIntent = new Intent(TimeSet.this, CancelAlarm.class);
        PendingIntent cancelIntent = PendingIntent.getService(this, 123, detailsIntent, 0);
        obj.mBuilder.mActions.clear();
        obj.mBuilder.setContentTitle(getString(R.string.alarm_set_to))
                .setContentText(DateFormat.format("hh:mm a", calendar.getTime()).toString())
                .addAction(R.drawable.cancel_alarm, getString(R.string.cancel_alarm), cancelIntent);
        NotificationManagerCompat.from(this).notify(123, obj.mBuilder.build());

        Intent i = new Intent(this, Alarm.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),135,i,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }
        else {
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }

    }

}
