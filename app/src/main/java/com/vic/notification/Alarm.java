package com.vic.notification;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class Alarm extends Activity {

    MediaPlayer selectedRingtone;
    Button dismissButton;
    Button snoozeButton;
    Intent homeIntents;
    Vibrator vib;
    Uri defaultUri;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timesupdialog);

        final MainActivity obj = new MainActivity();
        homeIntents= new Intent(Intent.ACTION_MAIN);
        homeIntents.addCategory(Intent.CATEGORY_HOME);
        homeIntents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if(obj.ifNoRingtone == 0) {
            MediaPlayer player;
            if (obj.uri == null) {
                defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                player = MediaPlayer.create(this, defaultUri);
                player.setLooping(true);
                selectedRingtone = player;
                selectedRingtone.start();
            } else {
                player = MediaPlayer.create(this, obj.uri);

            }
            player.setLooping(true);
            selectedRingtone = player;
            selectedRingtone.start();
        }


        Window wind;
        wind = this.getWindow();
        wind.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        wind.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        wind.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        if(obj.chosenVibrator) {
            long[] pattern = {0, 1000, 1000};
            vib.vibrate(pattern, 0);
        }



        dismissButton = (Button) findViewById(R.id.dismissButton);
        snoozeButton = (Button) findViewById(R.id.snoozeButton);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(obj.ifNoRingtone == 0) {
                    selectedRingtone.stop();
                }
                if (obj.chosenVibrator) {
                    vib.cancel();
                }

                Intent enableSet = new Intent();
                enableSet.setAction("enableSet");
                Alarm.this.sendBroadcast(enableSet);
                Intent detailsIntent = new Intent(Alarm.this, TimeSet.class);
                PendingIntent detailsPendingIntent = PendingIntent.getService(Alarm.this, 123, detailsIntent, 0);
                obj.mBuilder.mActions.clear();
                obj.mBuilder.setContentTitle(getString(R.string.alarm_duration))
                            .setContentText(obj.displayString)
                            .addAction(R.drawable.start_alarm, getString(R.string.start_alarm), detailsPendingIntent);
                NotificationManagerCompat.from(Alarm.this).notify(123, obj.mBuilder.build());

                if (obj.chosenRepeat == true) {
                    Intent repeatIntent = new Intent(Alarm.this, TimeSet.class);
                    startService(repeatIntent);
                }

                finish();
                startActivity(homeIntents);
            }

        });
        snoozeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int snoozeTime = obj.snoozeTime;
                Intent snoozeIntent = new Intent(Alarm.this, TimeSet.class);
                snoozeIntent.putExtra("time", snoozeTime);
                if(obj.ifNoRingtone == 0) {
                    selectedRingtone.stop();
                }
                if(obj.chosenVibrator) {
                    vib.cancel();
                }
                startService(snoozeIntent);
                finish();
                startActivity(homeIntents);
            }
        });

    }
}
