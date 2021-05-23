package com.vic.notification;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    static android.support.v7.app.NotificationCompat.Builder mBuilder;
    static String displayString;
    static int finalMinute = 0;
    static int finalHour = 0;
    static int snoozeTime = 0;
    static int ifNoRingtone = 0;
    static Boolean chosenVibrator;
    static Bitmap icon;
    static Boolean chosenRepeat;
    static TextView ringtoneName;
    static Uri uri;
    static int infoIndicator;
    private Uri uriIndicator = null;
    private int noRingtoneIndicator = 0;
    private Button set;
    private int setMinute = 0;
    private int setHour = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        infoIndicator = 0;
        startActivity(new Intent(this, WelcomeActivity.class));
        setContentView(R.layout.activity_main);

        showNotification();

        ringtoneName = (TextView) findViewById(R.id.ringtoneName);
        Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Ringtone rp = RingtoneManager.getRingtone(this, defaultUri);
        String defaultName = rp.getTitle(this);
        ringtoneName.setText(defaultName);

        set = (Button) findViewById(R.id.setTime);
        set.setBackground(ContextCompat.getDrawable(this, R.drawable.original_set_color));

        IntentFilter enableSetButton = new IntentFilter();
        enableSetButton.addAction("enableSet");
        BroadcastReceiver enableSetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                set.setEnabled(true);
            }
        };
        registerReceiver(enableSetReceiver, enableSetButton);

        IntentFilter disableSetButton = new IntentFilter();
        disableSetButton.addAction("disableSet");
        BroadcastReceiver disableSetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                set.setEnabled(false);
            }
        };
        registerReceiver(disableSetReceiver, disableSetButton);

    }

    public void showNotification(){
        icon = BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.ic_launcher);

        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.setAction(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent homePendingIntent = PendingIntent.getActivity(this, 0, homeIntent, 0);

        mBuilder = (android.support.v7.app.NotificationCompat.Builder) new android.support.v7.app.NotificationCompat.Builder(this)
                        .setLargeIcon(icon)
                        .setSmallIcon(R.drawable.small_icon)
                        .setContentTitle(getString(R.string.no_alarm))
                        .setColor(0x00c1f7)
                        .setContentIntent(homePendingIntent)
                        .setShowWhen(false);

        NotificationManagerCompat.from(this).notify(123, mBuilder.build());

    }

    public void ringtoneRow(View view) {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            String ringToneName;
            if (resultCode == RESULT_OK) {
                if(data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI) == null) {
                    ringToneName = getString(R.string.no_ringtone);
                    noRingtoneIndicator = 1;
                }
                else {
                    uriIndicator = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    Ringtone name = RingtoneManager.getRingtone(this, uriIndicator);
                    ringToneName = name.getTitle(this);

                }

                ringtoneName.setText(ringToneName);

            }
        }
    }

    public void pickTime(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.dialogStyle);
        View view =  LayoutInflater.from(MainActivity.this).inflate(R.layout.timepicker_dialog, null);
        builder.setView(view);
        final NumberPicker minutePicker = (NumberPicker) view.findViewById(R.id.minutePicker);
        minutePicker.setWrapSelectorWheel(true);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(setMinute);
        final NumberPicker hourPicker = (NumberPicker) view.findViewById(R.id.hourPicker);
        hourPicker.setWrapSelectorWheel(true);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(11);
        hourPicker.setValue(setHour);
        setDividerColor(minutePicker, 0xff09c5ff);
        setDividerColor(hourPicker, 0xff09c5ff);

        builder.setPositiveButton(getString(R.string.ok_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String minuteString = Integer.toString(minutePicker.getValue());
                String hourString = Integer.toString(hourPicker.getValue());
                setMinute = minutePicker.getValue();
                setHour = hourPicker.getValue();
                if(setMinute < 10) {
                    minuteString = "0" + Integer.toString(setMinute);
                }
                if(setHour < 10){
                    hourString = "0" + Integer.toString(setHour);
                }
                TextView displayTime= (TextView) findViewById(R.id.displayTime);
                displayTime.setText(hourString + ":" + minuteString);
            }
        }).setNegativeButton(getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        builder.show();

    }

    public void snoozeRow(View v) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.dialogStyle);
        View view =  LayoutInflater.from(MainActivity.this).inflate(R.layout.snoozetimepicker, null);
        final NumberPicker snoozePicker = (NumberPicker) view.findViewById(R.id.snoozePicker);
        snoozePicker.setMinValue(0);
        snoozePicker.setMaxValue(59);
        snoozePicker.setValue(snoozeTime);
        snoozePicker.setWrapSelectorWheel(true);
        setDividerColor(snoozePicker, 0xff09c5ff);
        builder.setView(view);
        AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.3F);
        v.startAnimation(buttonClick);

        builder.setPositiveButton(getString(R.string.ok_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                snoozeTime =  snoozePicker.getValue();
                System.out.println(snoozeTime);
                TextView displaySnoozeTime = (TextView) findViewById(R.id.displaySnoozeTime);
                if(snoozeTime > 1) {
                    displaySnoozeTime.setText(Integer.toString(snoozeTime) + " " + getString(R.string.minutes));
                }
                else{
                    displaySnoozeTime.setText(Integer.toString(snoozeTime) + " " + getString(R.string.minutes));
                }
            }
        }).setNegativeButton(getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });

        builder.show();

    }

    public void setTime(View view){
        finalMinute = setMinute;
        finalHour = setHour;
        if(finalMinute == 0 && finalHour == 0){
            Toast.makeText(this, getString(R.string.select_duration), Toast.LENGTH_SHORT).show();
            return;
        }
        uri = uriIndicator;
        ifNoRingtone = noRingtoneIndicator;
        set.setBackground(ContextCompat.getDrawable(this,R.drawable.change_set_color));
        Switch vibrateSwitch = (Switch) findViewById(R.id.vibrateSwitch);
        chosenVibrator = vibrateSwitch.isChecked();
        Switch repeatSwitch = (Switch) findViewById(R.id.repeatSwitch);
        chosenRepeat = repeatSwitch.isChecked();
        System.out.print(snoozeTime);

        if(finalHour == 0){
            displayString = Integer.toString(finalMinute) + " " + getString(R.string.min);
        }
        else if(finalMinute == 0){
            displayString = Integer.toString(finalHour) + " " + getString(R.string.hr);
        }
        else{
            displayString = Integer.toString(finalHour) + " " + getString(R.string.hr) + " " + Integer.toString(finalMinute) + " " + getString(R.string.min);
        }
        Intent timeSetIntent = new Intent(MainActivity.this, TimeSet.class);
        PendingIntent detailsPendingIntent = PendingIntent.getService(this, 123, timeSetIntent, 0);

        mBuilder.mActions.clear();
        mBuilder.setShowWhen(false)
                .addAction(R.drawable.start_alarm, getString(R.string.start_alarm) , detailsPendingIntent)
                .setContentTitle(getString(R.string.alarm_duration))
                .setContentText(displayString)
                .setPriority(Notification.PRIORITY_MAX);
        NotificationManagerCompat.from(this).notify(123, mBuilder.build());

    }

    public void showInfo(View view){
        infoIndicator = 1;
        startActivity(new Intent(this, WelcomeActivity.class));

    }

    private void setDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


}