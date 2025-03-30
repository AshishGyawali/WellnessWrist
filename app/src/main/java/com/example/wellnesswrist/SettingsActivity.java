package com.example.wellnesswrist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.databinding.DataBindingUtil;
import com.example.wellnesswrist.databinding.ActivitySettingsBinding;

public class SettingsActivity extends ComponentActivity {
    private ActivitySettingsBinding binding;
    private SettingsViewModel viewModel;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        viewModel = new SettingsViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("WellnessWristPrefs", MODE_PRIVATE);

        // Handle reset button click
        binding.resetHeightWeightButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("height");
            editor.remove("weight");
            editor.apply();
            Toast.makeText(this, "Height and Weight reset", Toast.LENGTH_SHORT).show();
        });
    }
}