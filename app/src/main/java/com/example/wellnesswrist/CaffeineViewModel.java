package com.example.wellnesswrist;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CaffeineViewModel extends ViewModel {
    public MutableLiveData<String> coffeeType = new MutableLiveData<>();
    public MutableLiveData<String> coffeeSize = new MutableLiveData<>();
    public MutableLiveData<Float> caffeineContent = new MutableLiveData<>(0f);

    public void calculateCaffeine() {
        String type = coffeeType.getValue();
        String size = coffeeSize.getValue();
        if (type == null || size == null) return;

        float baseCaffeine = 0f;
        float sizeMultiplier = 1f;

        // Determine base caffeine content based on coffee type
        switch (type) {
            case "Espresso":
                baseCaffeine = 63f; // Base caffeine for a single shot
                if ("Double".equals(size)) {
                    sizeMultiplier = 2f; // Double shot
                }
                break;
            case "Americano":
                baseCaffeine = 95f; // Base caffeine for a small Americano
                break;
            case "Latte":
                baseCaffeine = 95f; // Base caffeine for a small Latte
                break;
            case "Cappuccino":
                baseCaffeine = 95f; // Base caffeine for a small Cappuccino
                break;
            case "Black Coffee":
                baseCaffeine = 95f; // Base caffeine for a small Black Coffee
                break;
        }

        // Adjust caffeine content based on size (for non-Espresso types)
        if (!"Espresso".equals(type)) {
            switch (size) {
                case "S":
                    sizeMultiplier = 1f;
                    break;
                case "M":
                    sizeMultiplier = 1.5f;
                    break;
                case "L":
                    sizeMultiplier = 2f;
                    break;
                case "XL":
                    sizeMultiplier = 2.5f;
                    break;
            }
        }

        caffeineContent.setValue(baseCaffeine * sizeMultiplier);
    }
}