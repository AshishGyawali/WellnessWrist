package com.example.wellnesswrist;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExerciseViewModel extends ViewModel implements SensorEventListener {
    public MutableLiveData<String> exerciseType = new MutableLiveData<>();
    public MutableLiveData<String> timerText = new MutableLiveData<>("00:00");
    public MutableLiveData<String> resultText = new MutableLiveData<>();
    public MutableLiveData<Boolean> isTimerRunning = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isPaused = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> showResult = new MutableLiveData<>(false);
    public MutableLiveData<String> intensityText = new MutableLiveData<>("Intensity: Low");

    private long startTime;
    private long elapsedTime;
    private boolean isRunning;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private float heightCm;
    private float weightLbs;
    private float bmi;
    private float caloriesBurned;
    private float intensityMultiplier = 1.0f; // 1.0 (low), 1.5 (moderate), 2.0 (high)
    private SensorManager sensorManager;

    // MET values for each exercise at different intensities
    private static final float MET_RUNNING_LOW = 7.0f;
    private static final float MET_RUNNING_MODERATE = 9.0f;
    private static final float MET_RUNNING_HIGH = 11.0f;
    private static final float MET_CYCLING_LOW = 4.0f;
    private static final float MET_CYCLING_MODERATE = 6.0f;
    private static final float MET_CYCLING_HIGH = 8.0f;
    private static final float MET_WALKING_LOW = 3.5f;
    private static final float MET_WALKING_MODERATE = 4.5f;
    private static final float MET_WALKING_HIGH = 5.0f;
    private static final float MET_SWIMMING_LOW = 6.0f;
    private static final float MET_SWIMMING_MODERATE = 8.0f;
    private static final float MET_SWIMMING_HIGH = 10.0f;
    private static final float MET_BADMINTON_LOW = 4.5f;
    private static final float MET_BADMINTON_MODERATE = 5.5f;
    private static final float MET_BADMINTON_HIGH = 7.0f;

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

    public void startTimer(Context context, float height, float weight) {
        this.heightCm = height;
        this.weightLbs = weight;
        calculateBmi();
        startTime = System.currentTimeMillis() - elapsedTime;
        isRunning = true;
        isTimerRunning.setValue(true);
        isPaused.setValue(false);
        showResult.setValue(false);
        handler.post(timerRunnable);

        // Initialize sensor for intensity detection
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
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
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        calculateCalories();
        resultText.setValue(String.format("Elapsed Time: %s\nBMI: %.1f\nCalories Burned: %.1f kcal\nIntensity: %s",
                timerText.getValue(), bmi, caloriesBurned,
                intensityMultiplier == 2.0f ? "High" : intensityMultiplier == 1.5f ? "Moderate" : "Low"));
        showResult.setValue(true);
    }

    private void calculateBmi() {
        float heightM = heightCm / 100;
        float weightKg = weightLbs * 0.453592f;
        bmi = weightKg / (heightM * heightM);
    }

    private void calculateCalories() {
        float weightKg = weightLbs * 0.453592f;
        float durationHours = elapsedTime / (1000.0f * 60.0f * 60.0f);
        float met = getMETValue();
        // Calorie burn formula: Calories = MET * weight (kg) * duration (hours) * intensity multiplier
        caloriesBurned = met * weightKg * durationHours * intensityMultiplier;
    }

    private float getMETValue() {
        String type = exerciseType.getValue();
        if (type == null) return 0;

        switch (type) {
            case "Running":
                if (intensityMultiplier >= 2.0f) return MET_RUNNING_HIGH;
                else if (intensityMultiplier >= 1.5f) return MET_RUNNING_MODERATE;
                else return MET_RUNNING_LOW;
            case "Cycling":
                if (intensityMultiplier >= 2.0f) return MET_CYCLING_HIGH;
                else if (intensityMultiplier >= 1.5f) return MET_CYCLING_MODERATE;
                else return MET_CYCLING_LOW;
            case "Walking":
                if (intensityMultiplier >= 2.0f) return MET_WALKING_HIGH;
                else if (intensityMultiplier >= 1.5f) return MET_WALKING_MODERATE;
                else return MET_WALKING_LOW;
            case "Swimming":
                if (intensityMultiplier >= 2.0f) return MET_SWIMMING_HIGH;
                else if (intensityMultiplier >= 1.5f) return MET_SWIMMING_MODERATE;
                else return MET_SWIMMING_LOW;
            case "Badminton":
                if (intensityMultiplier >= 2.0f) return MET_BADMINTON_HIGH;
                else if (intensityMultiplier >= 1.5f) return MET_BADMINTON_MODERATE;
                else return MET_BADMINTON_LOW;
            default:
                return 0;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float acceleration = (float) Math.sqrt(x * x + y * y + z * z);

            // Determine intensity based on acceleration
            if (acceleration > 12) {
                intensityMultiplier = 2.0f; // High intensity
                intensityText.setValue("Intensity: High");
            } else if (acceleration > 10) {
                intensityMultiplier = 1.5f; // Moderate intensity
                intensityText.setValue("Intensity: Moderate");
            } else {
                intensityMultiplier = 1.0f; // Low intensity
                intensityText.setValue("Intensity: Low");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        handler.removeCallbacksAndMessages(null);
    }
}