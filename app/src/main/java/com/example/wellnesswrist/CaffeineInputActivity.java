package com.example.wellnesswrist;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.databinding.DataBindingUtil;
import com.example.wellnesswrist.databinding.ActivityCaffeineInputBinding;

public class CaffeineInputActivity extends ComponentActivity {
    private ActivityCaffeineInputBinding binding;
    private CaffeineViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_caffeine_input);
        viewModel = new CaffeineViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        String[] coffeeTypes = {"Espresso", "Cappuccino"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, coffeeTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.coffeeTypeSpinner.setAdapter(adapter);

        binding.calculateCaffeineButton.setOnClickListener(v -> {
            String sizeStr = binding.sizeInput.getText().toString();
            if (sizeStr.isEmpty()) {
                Toast.makeText(this, "Please enter size", Toast.LENGTH_SHORT).show();
                return;
            }
            float size = Float.parseFloat(sizeStr);
            String coffeeType = binding.coffeeTypeSpinner.getSelectedItem().toString();
            viewModel.calculateCaffeine(coffeeType, size);
        });
    }
}