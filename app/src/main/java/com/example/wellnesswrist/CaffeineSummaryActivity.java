package com.example.wellnesswrist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.ComponentActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CaffeineSummaryActivity extends ComponentActivity {
    private static final String PREFS_NAME = "CaffeinePrefs";
    private static final String KEY_TOTAL_CAFFEINE = "total_caffeine";
    private static final String KEY_LAST_RESET = "last_reset";
    private static final float DAILY_LIMIT = 400f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caffeine_summary);

        // Get views
        TextView totalCaffeineView = findViewById(R.id.total_caffeine_text);
        TextView riskLevelView = findViewById(R.id.risk_level_text);
        TextView progressView = findViewById(R.id.progress_text);
        ProgressBar progressBar = findViewById(R.id.progress_bar);

        // Get caffeine from intent
        float newCaffeine = getIntent().getFloatExtra("caffeine_amount", 0f);

        // Load and update total caffeine
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        checkAndResetCaffeine(prefs);
        float totalCaffeine = prefs.getFloat(KEY_TOTAL_CAFFEINE, 0f) + newCaffeine;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(KEY_TOTAL_CAFFEINE, totalCaffeine);
        editor.apply();

        // Display total caffeine
        totalCaffeineView.setText(String.format(Locale.getDefault(), "Total Caffeine: %.1f mg", totalCaffeine));

        // Calculate and display risk level
        String riskLevel = getRiskLevel(totalCaffeine);
        riskLevelView.setText("Risk Level: " + riskLevel);

        // Calculate and display progress
        float progress = (totalCaffeine / DAILY_LIMIT) * 100f;
        progressView.setText(String.format(Locale.getDefault(), "Progress: %.1f%% of daily limit", progress));

        // Set the progress bar value
        progressBar.setProgress((int) progress);
    }

    private void checkAndResetCaffeine(SharedPreferences prefs) {
        long lastReset = prefs.getLong(KEY_LAST_RESET, 0);
        String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String lastResetDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date(lastReset));

        if (!currentDate.equals(lastResetDate)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat(KEY_TOTAL_CAFFEINE, 0f);
            editor.putLong(KEY_LAST_RESET, System.currentTimeMillis());
            editor.apply();
        }
    }

    private String getRiskLevel(float totalCaffeine) {
        if (totalCaffeine <= 200f) return "Low";
        if (totalCaffeine <= 400f) return "Moderate";
        if (totalCaffeine <= 600f) return "High";
        return "Very High";
    }
}