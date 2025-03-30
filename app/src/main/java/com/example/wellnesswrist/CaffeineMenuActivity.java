package com.example.wellnesswrist;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.ComponentActivity;
import android.widget.Button;

public class CaffeineMenuActivity extends ComponentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caffeine_menu);

        Button viewSummaryButton = findViewById(R.id.view_summary_button);
        viewSummaryButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CaffeineSummaryActivity.class);
            startActivity(intent);
        });

        Button logCaffeineButton = findViewById(R.id.log_caffeine_button);
        logCaffeineButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CaffeineInputActivity.class);
            startActivity(intent);
        });
    }
}