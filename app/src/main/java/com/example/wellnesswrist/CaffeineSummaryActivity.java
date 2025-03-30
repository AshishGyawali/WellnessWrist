package com.example.wellnesswrist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.ComponentActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CaffeineSummaryActivity extends ComponentActivity {
    private static final String PREFS_NAME = "CaffeinePrefs";
    private static final String KEY_TOTAL_CAFFEINE = "total_caffeine";
    private static final String KEY_LOG_TIMESTAMPS = "log_timestamps"; // Store timestamps
    private static final String KEY_LAST_RESET = "last_reset";
    private static final float DAILY_LIMIT = 400f;
    private static final float HALF_LIFE_HOURS = 5f; // Caffeine half-life in hours

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caffeine_summary);

        TextView totalCaffeineView = findViewById(R.id.total_caffeine_text);
        TextView riskLevelView = findViewById(R.id.risk_level_text);
        TextView progressView = findViewById(R.id.progress_text);
        ProgressBar progressBar = findViewById(R.id.progress_bar);

        float newCaffeine = getIntent().getFloatExtra("caffeine_amount", 0f);
        if (newCaffeine > 0) {
            saveNewLog(newCaffeine);
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        checkAndResetCaffeine(prefs);
        float totalCaffeine = calculateRemainingCaffeine(prefs);

        totalCaffeineView.setText(String.format(Locale.getDefault(), "Total Caffeine: %.1f mg", totalCaffeine));
        String riskLevel = getRiskLevel(totalCaffeine);
        riskLevelView.setText("Risk Level: " + riskLevel);
        float progress = (totalCaffeine / DAILY_LIMIT) * 100f;
        progressView.setText(String.format(Locale.getDefault(), "Progress: %.1f%% of daily limit", progress));
        progressBar.setProgress((int) progress);
    }

    private void saveNewLog(float caffeine) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String timestamps = prefs.getString(KEY_LOG_TIMESTAMPS, "");
        List<Float> logAmounts = new ArrayList<>();
        List<Long> logTimes = new ArrayList<>();
        if (!timestamps.isEmpty()) {
            String[] parts = timestamps.split(";");
            for (String part : parts) {
                String[] data = part.split(",");
                if (data.length == 2) {
                    logAmounts.add(Float.parseFloat(data[0]));
                    logTimes.add(Long.parseLong(data[1]));
                }
            }
        }
        logAmounts.add(caffeine);
        logTimes.add(System.currentTimeMillis());
        StringBuilder newTimestamps = new StringBuilder();
        for (int i = 0; i < logAmounts.size(); i++) {
            newTimestamps.append(logAmounts.get(i)).append(",").append(logTimes.get(i));
            if (i < logAmounts.size() - 1) newTimestamps.append(";");
        }
        prefs.edit().putString(KEY_LOG_TIMESTAMPS, newTimestamps.toString()).apply();
    }

    private float calculateRemainingCaffeine(SharedPreferences prefs) {
        String timestamps = prefs.getString(KEY_LOG_TIMESTAMPS, "");
        float totalCaffeine = 0f;
        if (!timestamps.isEmpty()) {
            String[] parts = timestamps.split(";");
            for (String part : parts) {
                String[] data = part.split(",");
                if (data.length == 2) {
                    float amount = Float.parseFloat(data[0]);
                    long timestamp = Long.parseLong(data[1]);
                    float hoursElapsed = (System.currentTimeMillis() - timestamp) / (1000f * 60 * 60); // Hours since log
                    float remaining = amount * (float) Math.pow(0.5, hoursElapsed / HALF_LIFE_HOURS);
                    totalCaffeine += Math.max(0f, remaining); // Ensure no negative values
                }
            }
        }
        prefs.edit().putFloat(KEY_TOTAL_CAFFEINE, totalCaffeine).apply();
        return totalCaffeine;
    }

    private void checkAndResetCaffeine(SharedPreferences prefs) {
        long lastReset = prefs.getLong(KEY_LAST_RESET, 0);
        String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String lastResetDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date(lastReset));

        if (!currentDate.equals(lastResetDate)) {
            prefs.edit().putFloat(KEY_TOTAL_CAFFEINE, 0f).putString(KEY_LOG_TIMESTAMPS, "").putLong(KEY_LAST_RESET, System.currentTimeMillis()).apply();
        }
    }

    private String getRiskLevel(float totalCaffeine) {
        if (totalCaffeine <= 200f) return "Low";
        if (totalCaffeine <= 400f) return "Moderate";
        if (totalCaffeine <= 600f) return "High";
        return "Very High";
    }
}