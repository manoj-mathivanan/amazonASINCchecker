package com.example.manoj.amazoncheck;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class TimeService extends Service {
    // constant
    public static final long NOTIFY_INTERVAL = 900 * 1000;

    public int counter=0;
    public static String[] lines;
    public TimeService(Context applicationContext) {
        super();
        //lines = intent.getStringExtra("lines").split("\\r?\\n");

        Log.i("HERE", "here I am!");
    }

    public TimeService() {
    }

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            String channelName = "My Background Service";
            String NOTIFICATION_CHANNEL_ID = "my_channel";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("App is running in background")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);
        }
        else {
            startForeground(1, new Notification());
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        lines = Helper.lines;
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        //registerReceiver(new RestartBroadcastReceiver(),
          //      new IntentFilter("com.example.manoj.amazoncheck.broadcast"));
        Intent broadcastIntent = new Intent("com.example.manoj.amazoncheck.broadcast");
        sendBroadcast(broadcastIntent);
        stoptimertask();
        Log.i("EXIT", "ondestroydone!");
    }



    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 0, NOTIFY_INTERVAL); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                new NetworkCall(getApplicationContext()).execute(lines);
            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}
