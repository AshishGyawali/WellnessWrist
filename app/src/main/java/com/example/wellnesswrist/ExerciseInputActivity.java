package com.example.wellnesswrist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.databinding.DataBindingUtil;
import com.example.wellnesswrist.databinding.ActivityExerciseInputBinding;

public class ExerciseInputActivity extends ComponentActivity {
    private ActivityExerciseInputBinding binding;
    private ExerciseViewModel viewModel;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_exercise_input);
        viewModel = new ExerciseViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("WellnessWristPrefs", MODE_PRIVATE);

        // Load saved height and weight
        float savedHeight = sharedPreferences.getFloat("height", 0f);
        float savedWeight = sharedPreferences.getFloat("weight", 0f);
        if (savedHeight != 0f) {
            binding.heightInput.setText(String.valueOf(savedHeight));
        }
        if (savedWeight != 0f) {
            binding.weightInput.setText(String.valueOf(savedWeight));
        }

        String exerciseType = getIntent().getStringExtra("exercise_type");
        viewModel.exerciseType.setValue(exerciseType);

        binding.startExerciseButton.setOnClickListener(v -> {
            String heightStr = binding.heightInput.getText().toString();
            String weightStr = binding.weightInput.getText().toString();
            if (heightStr.isEmpty() || weightStr.isEmpty()) {
                Toast.makeText(this, "Please enter height and weight", Toast.LENGTH_SHORT).show();
                return;
            }
            float height = Float.parseFloat(heightStr);
            float weight = Float.parseFloat(weightStr);

            // Save height and weight to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("height", height);
            editor.putFloat("weight", weight);
            editor.apply();

            // Start the timer activity
            Intent intent = new Intent(ExerciseInputActivity.this, ExerciseTimerActivity.class);
            intent.putExtra("exercise_type", exerciseType);
            intent.putExtra("height", height);
            intent.putExtra("weight", weight);
            startActivity(intent);
        });
    }
}