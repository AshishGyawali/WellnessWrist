package com.example.wellnesswrist;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.ComponentActivity;
import android.widget.Button;

public class BreathingMenuActivity extends ComponentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathing_menu);

        Button startBreathingButton = findViewById(R.id.start_breathing_button);
        startBreathingButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, BreathingActivity.class);
            startActivity(intent);
        });
    }
}