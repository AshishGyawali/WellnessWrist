package com.example.wellnesswrist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.activity.ComponentActivity;
import androidx.databinding.DataBindingUtil;
import com.example.wellnesswrist.databinding.ActivityMainBinding;

public class MainActivity extends ComponentActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (binding == null) {
            Log.e(TAG, "Binding is null!");
            return;
        }

        viewModel = new MainViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        binding.startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
        });
    }
}