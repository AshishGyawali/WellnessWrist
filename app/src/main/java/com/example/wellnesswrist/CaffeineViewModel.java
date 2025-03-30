package com.example.wellnesswrist;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CaffeineViewModel extends ViewModel {
    public MutableLiveData<String> caffeineText = new MutableLiveData<>();
    public MutableLiveData<Float> currentCaffeine = new MutableLiveData<>(0f);
    public MutableLiveData<Boolean> showCaffeine = new MutableLiveData<>(false);

    private float totalCaffeine = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable decayRunnable = new Runnable() {
        @Override
        public void run() {
            totalCaffeine /= 2;
            currentCaffeine.setValue(totalCaffeine);
            caffeineText.setValue("Caffeine: " + totalCaffeine + " mg");
            if (totalCaffeine > 0) {
                handler.postDelayed(this, 18000000); // 5 hours
            }
        }
    };

    public void calculateCaffeine(String coffeeType, float sizeOz) {
        float caffeinePerOz = 0;
        switch (coffeeType) {
            case "Espresso":
                caffeinePerOz = 30; // 30 mg per oz
                break;
            case "Cappuccino":
                caffeinePerOz = 10; // 10 mg per oz
                break;
        }
        totalCaffeine += caffeinePerOz * sizeOz;
        currentCaffeine.setValue(totalCaffeine);
        if (totalCaffeine > 400) {
            caffeineText.setValue("Caffeine Limit Reached! (" + totalCaffeine + " mg)");
        } else {
            caffeineText.setValue("Caffeine: " + totalCaffeine + " mg");
        }
        showCaffeine.setValue(true);
        handler.postDelayed(decayRunnable, 18000000); // Start decay after 5 hours
    }
}