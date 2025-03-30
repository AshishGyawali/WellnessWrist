package com.example.wellnesswrist;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.databinding.DataBindingUtil;
import com.example.wellnesswrist.databinding.ActivityBreathingBinding;

public class BreathingActivity extends ComponentActivity {
    private ActivityBreathingBinding binding;
    private BreathingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_breathing);
        viewModel = new BreathingViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        // Set up stop button to navigate back to BreathingMenuActivity
        binding.stopExerciseButton.setOnClickListener(v -> {
            viewModel.stopBreathing();
            Intent intent = new Intent(this, BreathingMenuActivity.class);
            startActivity(intent);
            finish(); // Close this activity
        });

        // Start breathing when activity is launched (triggered by BreathingMenuActivity)
        viewModel.startBreathing();
    }
}