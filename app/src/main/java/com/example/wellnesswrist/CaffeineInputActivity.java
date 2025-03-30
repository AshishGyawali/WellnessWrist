package com.example.wellnesswrist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.databinding.DataBindingUtil;
import com.example.wellnesswrist.databinding.ActivityCaffeineInputBinding;
import java.util.Arrays;

public class CaffeineInputActivity extends ComponentActivity {
    private ActivityCaffeineInputBinding binding;
    private CaffeineViewModel viewModel;
    private ArrayAdapter<String> sizeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_caffeine_input);
        viewModel = new CaffeineViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        binding.coffeeTypeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                viewModel.coffeeType.setValue(selectedType);
                updateSizeSpinner(selectedType);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        sizeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.coffeeSizeSpinner.setAdapter(sizeAdapter);
        binding.coffeeSizeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedSize = parent.getItemAtPosition(position).toString();
                viewModel.coffeeSize.setValue(selectedSize);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        binding.coffeeTypeSpinner.setSelection(0);

        binding.logCaffeineButton.setOnClickListener(v -> {
            viewModel.calculateCaffeine();
            Float caffeine = viewModel.caffeineContent.getValue();
            if (caffeine != null && caffeine > 0) {
                Toast.makeText(this, "Logged " + caffeine + " mg of caffeine", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, CaffeineSummaryActivity.class);
                intent.putExtra("caffeine_amount", caffeine);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Please select a coffee type and size", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSizeSpinner(String coffeeType) {
        sizeAdapter.clear();
        if ("Espresso".equals(coffeeType)) {
            sizeAdapter.addAll(Arrays.asList(getResources().getStringArray(R.array.coffee_sizes_espresso)));
        } else {
            sizeAdapter.addAll(Arrays.asList(getResources().getStringArray(R.array.coffee_sizes_standard)));
        }
        sizeAdapter.notifyDataSetChanged();
        binding.coffeeSizeSpinner.setSelection(0);
    }
}