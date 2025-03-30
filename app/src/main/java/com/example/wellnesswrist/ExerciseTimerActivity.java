package com.example.wellnesswrist;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.databinding.DataBindingUtil;
import com.example.wellnesswrist.databinding.ActivityExerciseTimerBinding;

public class ExerciseTimerActivity extends ComponentActivity {
    private ActivityExerciseTimerBinding binding;
    private ExerciseViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_exercise_timer);
        viewModel = new ExerciseViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        // Retrieve data from intent
        String exerciseType = getIntent().getStringExtra("exercise_type");
        float height = getIntent().getFloatExtra("height", 0f);
        float weight = getIntent().getFloatExtra("weight", 0f);

        // Set exercise type and start the timer
        viewModel.exerciseType.setValue(exerciseType);
        viewModel.startTimer(height, weight);

        binding.pauseButton.setOnClickListener(v -> {
            if (viewModel.isPaused.getValue() != null && viewModel.isPaused.getValue()) {
                viewModel.resumeTimer();
            } else {
                viewModel.pauseTimer();
            }
        });

        binding.stopButton.setOnClickListener(v -> {
            viewModel.stopTimer();
            // Do not finish the activity here; let the user view the results
        });

        binding.backToExerciseListButton.setOnClickListener(v -> {
            // Navigate back to ExerciseListActivity
            Intent intent = new Intent(ExerciseTimerActivity.this, ExerciseListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}