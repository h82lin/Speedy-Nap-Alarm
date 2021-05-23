package com.vic.notification;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by user on 2017-11-02.
 */

public class CancelAlarm extends IntentService {

    public CancelAlarm(){
        super("CancelAlarm");
    }

    protected void onHandleIntent(Intent intent) {

        MainActivity obj = new MainActivity();
        Intent i = new Intent(this, Alarm.class);
        PendingIntent pi = PendingIntent.getActivity(this ,135,i,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pi);

        Intent enableSet = new Intent();
        enableSet.setAction("enableSet");
        this.sendBroadcast(enableSet);

        Intent detailsIntent = new Intent(CancelAlarm.this, TimeSet.class);
        PendingIntent detailsPendingIntent = PendingIntent.getService(this, 123, detailsIntent, 0);
        obj.mBuilder.mActions.clear();
        obj.mBuilder.addAction(R.drawable.start_alarm, getString(R.string.start_alarm) , detailsPendingIntent)
                     .setContentTitle(getString(R.string.alarm_duration))
                     .setContentText(obj.displayString);
        NotificationManagerCompat.from(this).notify(123, obj.mBuilder.build());
    }
}
