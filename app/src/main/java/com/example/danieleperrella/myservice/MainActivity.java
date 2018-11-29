package com.example.danieleperrella.myservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private IntentFilter mIntent;
    private BroadcastReceiver statusReceiver;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Intent intent = new Intent();
        String packageName = getApplicationContext().getPackageName();
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (pm.isIgnoringBatteryOptimizations(packageName))
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        else {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
        }
        getApplicationContext().startActivity(intent);


        System.out.println("-------- activity " + pm.isIgnoringBatteryOptimizations(this.getPackageName())+" --------");
        */

        startService(new Intent(this, LogService.class));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String text = intent.getStringExtra("MSG");





            if(text.equalsIgnoreCase("-")) {
                notifyUserTest();
            }
            else {
                notifyUser(text);
                Toast.makeText(getApplication(), "Ciao "+text, Toast.LENGTH_LONG).show();
            }

        }

    };




    public void notifyUser(String text) {

        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);


        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "myapp:mywakelock");
        wakeLock.acquire();
        wakeLock.release();

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.screenBrightness = 1;
        getWindow().setAttributes(params);

        // Notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        Intent ii = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("SMARTNOTIFY");
        bigText.setBigContentTitle(text);

        Notification notification = new Notification();
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        mBuilder.setDefaults(notification.defaults);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("SmartNotify");
        mBuilder.setContentText(text);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        mBuilder.setAutoCancel(true);

        //Vibration
        mBuilder.setVibrate(new long[] { 1000, 1000});

        mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "YOUR_CHANNEL_ID";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());

    }





    public void notifyUserTest() {

        //Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        //v.vibrate(500);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "myapp:mywakelock");
        wakeLock.acquire();
        wakeLock.release();

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.screenBrightness = 0;
        getWindow().setAttributes(params);


    }


    @Override
    protected void onResume()
    {
        super.onResume();
        //registerReceiver(statusReceiver,mIntent);
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(broadcastReceiver, new IntentFilter("NOW"));
    }

    @Override
    protected void onPause() {
        if(mIntent != null) {
            unregisterReceiver(statusReceiver);
            mIntent = null;
        }
        super.onPause();
    }





    public void startService(View v)
    {
        LogService.vai=true;
        startService(new Intent(this,LogService.class));
    }
    public void stopService(View v)
    {
        LogService.vai=false;
        stopService(new Intent(this,LogService.class));
    }
}



