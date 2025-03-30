package com.example.wellnesswrist;

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

        viewModel.startBreathing();
    }
}