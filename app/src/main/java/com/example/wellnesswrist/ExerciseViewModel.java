package com.example.wellnesswrist;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExerciseViewModel extends ViewModel {
    public MutableLiveData<String> exerciseType = new MutableLiveData<>();
    public MutableLiveData<String> timerText = new MutableLiveData<>("00:00");
    public MutableLiveData<String> resultText = new MutableLiveData<>();
    public MutableLiveData<Boolean> isTimerRunning = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isPaused = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> showResult = new MutableLiveData<>(false);

    private long startTime;
    private long elapsedTime;
    private boolean isRunning;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private float heightCm;
    private float weightLbs;
    private float bmi;
    private float caloriesBurned;

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                elapsedTime = System.currentTimeMillis() - startTime;
                long seconds = elapsedTime / 1000;
                long minutes = seconds / 60;
                seconds = seconds % 60;
                timerText.setValue(String.format("%02d:%02d", minutes, seconds));
                handler.postDelayed(this, 1000);
            }
        }
    };

    public void startTimer(float height, float weight) {
        this.heightCm = height;
        this.weightLbs = weight;
        calculateBmi();
        startTime = System.currentTimeMillis() - elapsedTime;
        isRunning = true;
        isTimerRunning.setValue(true);
        isPaused.setValue(false);
        showResult.setValue(false);
        handler.post(timerRunnable);
    }

    public void pauseTimer() {
        isRunning = false;
        isPaused.setValue(true);
        handler.removeCallbacks(timerRunnable);
    }

    public void resumeTimer() {
        startTime = System.currentTimeMillis() - elapsedTime;
        isRunning = true;
        isPaused.setValue(false);
        handler.post(timerRunnable);
    }

    public void stopTimer() {
        isRunning = false;
        isTimerRunning.setValue(false);
        handler.removeCallbacks(timerRunnable);
        calculateCalories();
        resultText.setValue(String.format("Elapsed Time: %s\nBMI: %.1f\nCalories Burned: %.1f kcal",
                timerText.getValue(), bmi, caloriesBurned));
        showResult.setValue(true);
    }

    private void calculateBmi() {
        float heightM = heightCm / 100;
        float weightKg = weightLbs * 0.453592f;
        bmi = weightKg / (heightM * heightM);
    }

    private void calculateCalories() {
        long minutes = (elapsedTime / 1000) / 60;
        float met = 3.0f; // MET for light exercise
        caloriesBurned = met * (bmi * 3.5f) * minutes / 200;
    }
}