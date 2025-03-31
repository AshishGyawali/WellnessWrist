package com.example.wellnesswrist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class WellnessService extends Service {

    private static final String CHANNEL_ID = "WellnessChannel";
    private SharedPreferences prefs;
    private Handler handler;
    private Runnable taskRunner;

    private PostureMonitor postureMonitor;
    private AlarmScheduler alarmScheduler;
    private AmbientNoiseDetector noiseDetector;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        prefs = getSharedPreferences("WellnessWristPrefs", MODE_PRIVATE);
        handler = new Handler();

        postureMonitor = new PostureMonitor(this);
        alarmScheduler = new AlarmScheduler(this);
        noiseDetector = new AmbientNoiseDetector(this);

        startForeground(1, buildNotification("Wellness Service Running"));

        taskRunner = () -> {
            if (prefs.getBoolean("monitorPosture", false)) {
                postureMonitor.checkInactivity();
            }

            if (prefs.getBoolean("drinkWater", false)) {
                alarmScheduler.checkAndNotifyWaterReminder();
            }

            if (prefs.getBoolean("sunriseAlarm", false)) {
                alarmScheduler.checkAndVibrateSunriseAlarm();
            }

            if (prefs.getBoolean("ambientNoise", false)) {
                noiseDetector.detectNoiseLevel();
            }

            handler.postDelayed(taskRunner, 60 * 1000); // every 1 minute
        };
        handler.post(taskRunner);
    }

    private Notification buildNotification(String message) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Wellness Wrist")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notification)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Wellness Notifications",
                    NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(taskRunner);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
