package com.example.wellnesswrist;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import com.example.wellnesswrist.databinding.ActivityMainBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends ComponentActivity implements SensorEventListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private SensorManager sensorManager;
    private Sensor heartRateSensor, accelSensor;
    private AudioRecord audioRecord;
    private long startTime, slouchStart;
    private float bmi = 25f;
    private String exerciseLevel = "Intermediate";
    private float totalCaffeine = 0;
    private boolean isRecording = false;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (binding == null) {
            Log.e(TAG, "Binding is null!");
            return;
        }

        viewModel = new MainViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this); // Required for LiveData updates
        Log.d(TAG, "ViewModel set: " + (viewModel != null));

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // Button Listeners
        binding.fitnessButton.setOnClickListener(v -> startFitness());
        binding.breathingButton.setOnClickListener(v -> startBreathing());
        binding.sunriseButton.setOnClickListener(v -> setSunriseAlarm());
        binding.postureButton.setOnClickListener(v -> startPostureMonitoring());
        binding.caffeineButton.setOnClickListener(v -> trackCaffeine());
        binding.noiseButton.setOnClickListener(v -> monitorNoise());
    }

    private void startFitness() {
        startTime = SystemClock.elapsedRealtime();
        sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        viewModel.status.setValue("Fitness Started");
    }

    private void startBreathing() {
        // Modern API for vibration pattern (4-7-8 pattern: 200ms on, 4000ms off, 100ms on, 7000ms off, 300ms on, 8000ms off)
        long[] pattern = {0, 200, 4000, 100, 7000, 300, 8000};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
        } else {
            vibrator.vibrate(pattern, -1); // Fallback (not needed since minSdk is 30)
        }
        viewModel.status.setValue("Breathing: Inhale 4s, Hold 7s, Exhale 8s");
    }

    private void setSunriseAlarm() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.sunrise-sunset.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SunriseApi api = retrofit.create(SunriseApi.class);
        api.getSunrise().enqueue(new Callback<SunriseResponse>() {
            @Override
            public void onResponse(Call<SunriseResponse> call, Response<SunriseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String sunrise = response.body().results.sunrise;
                    viewModel.status.setValue("Sunrise at: " + sunrise);
                    vibrateForDuration(200);
                }
            }

            @Override
            public void onFailure(Call<SunriseResponse> call, Throwable t) {
                viewModel.status.setValue("Sunrise API Failed");
            }
        });
    }

    private void startPostureMonitoring() {
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
        viewModel.status.setValue("Posture Monitoring Started");
    }

    private void trackCaffeine() {
        totalCaffeine += 95;
        if (totalCaffeine > 400) {
            vibrateForDuration(200);
            viewModel.status.setValue("Caffeine Limit Reached!");
        } else {
            viewModel.status.setValue("Caffeine: " + totalCaffeine + " mg");
        }
        new Thread(() -> {
            try {
                Thread.sleep(18000000);
                totalCaffeine /= 2;
                viewModel.status.postValue("Caffeine: " + totalCaffeine + " mg");
            } catch (InterruptedException e) {}
        }).start();
    }

    private void monitorNoise() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
            return;
        }

        startRecording();
    }

    private void startRecording() {
        int sampleRate = 44100;
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        try {
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
            audioRecord.startRecording();
            isRecording = true;

            new Thread(() -> {
                short[] buffer = new short[bufferSize];
                while (isRecording) {
                    audioRecord.read(buffer, 0, bufferSize);
                    double amplitude = 0;
                    for (short sample : buffer) amplitude += Math.abs(sample);
                    amplitude /= bufferSize;
                    double db = 20 * Math.log10(amplitude / 32768.0) + 90;
                    viewModel.status.postValue("Noise: " + (int)db + " dB");
                    if (db > 60) {
                        vibrateForDuration(200);
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();
        } catch (SecurityException e) {
            viewModel.status.setValue("Recording permission denied");
        }
    }

    private void vibrateForDuration(long duration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(duration); // Fallback (not needed since minSdk is 30)
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(this, "Permission to record audio denied", Toast.LENGTH_SHORT).show();
                viewModel.status.setValue("Permission to record audio denied");
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            float heartRate = event.values[0];
            long timeMin = (SystemClock.elapsedRealtime() - startTime) / 60000;
            float met = exerciseLevel.equals("Beginner") ? 3.0f : exerciseLevel.equals("Intermediate") ? 7.0f : 8.0f;
            float calories = met * (bmi * 3.5f) * timeMin / 200;
            viewModel.status.setValue("Time: " + timeMin + " min, Calories: " + (int)calories + " kcal");
            sensorManager.unregisterListener(this, heartRateSensor);
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float z = event.values[2];
            if (z < 5) {
                if (slouchStart == 0) slouchStart = SystemClock.elapsedRealtime();
                if ((SystemClock.elapsedRealtime() - slouchStart) > 30000) {
                    vibrateForDuration(200);
                    viewModel.status.setValue("Sit Up!");
                    slouchStart = 0;
                }
            } else {
                slouchStart = 0;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        sensorManager.unregisterListener(this);
    }
}