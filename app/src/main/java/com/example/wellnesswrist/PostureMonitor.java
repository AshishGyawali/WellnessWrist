package com.example.wellnesswrist;

import android.content.Context;
import android.os.Vibrator;
import android.widget.Toast;

public class PostureMonitor {
    private long lastActivityTime;
    private Context context;

    public PostureMonitor(Context context) {
        this.context = context;
        lastActivityTime = System.currentTimeMillis();
    }

    public void checkInactivity() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastActivityTime) > 30 * 60 * 1000) { // 30 minutes
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) vibrator.vibrate(1000);
            Toast.makeText(context, "Time to stand and walk!", Toast.LENGTH_SHORT).show();
            lastActivityTime = currentTime;
        }
    }

    public void updateActivity() {
        lastActivityTime = System.currentTimeMillis();
    }
}
