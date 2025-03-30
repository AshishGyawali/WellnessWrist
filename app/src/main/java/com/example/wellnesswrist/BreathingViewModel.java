package com.example.wellnesswrist;

import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BreathingViewModel extends ViewModel {
    public MutableLiveData<String> breathingText = new MutableLiveData<>("Ready");
    public MutableLiveData<Float> scale = new MutableLiveData<>(1f);
    public MutableLiveData<Boolean> isRunning = new MutableLiveData<>(false);

    private final Handler handler = new Handler(Looper.getMainLooper());
    private ValueAnimator currentAnimator;

    public void startBreathing() {
        if (isRunning.getValue() == Boolean.TRUE) return;
        isRunning.setValue(true);
        breathingText.setValue("Inhale"); // Ensure initial text is set
        animateInhale();
    }

    public void stopBreathing() {
        if (isRunning.getValue() == Boolean.FALSE) return;
        isRunning.setValue(false);
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }
        handler.removeCallbacksAndMessages(null);
        breathingText.setValue("Stopped");
        scale.setValue(1f); // Reset scale
    }

    private void animateInhale() {
        if (!isRunning.getValue()) return;
        breathingText.setValue("Inhale");
        currentAnimator = ValueAnimator.ofFloat(1f, 2f);
        currentAnimator.setDuration(4000);
        currentAnimator.addUpdateListener(animation -> {
            Float value = (Float) animation.getAnimatedValue();
            scale.setValue(value); // Update scale via LiveData
        });
        currentAnimator.start();
        handler.postDelayed(this::animateHold, 4000);
    }

    private void animateHold() {
        if (!isRunning.getValue()) return;
        breathingText.setValue("Hold");
        handler.postDelayed(this::animateExhale, 7000);
    }

    private void animateExhale() {
        if (!isRunning.getValue()) return;
        breathingText.setValue("Exhale");
        currentAnimator = ValueAnimator.ofFloat(2f, 1f);
        currentAnimator.setDuration(8000);
        currentAnimator.addUpdateListener(animation -> {
            Float value = (Float) animation.getAnimatedValue();
            scale.setValue(value); // Update scale via LiveData
        });
        currentAnimator.start();
        handler.postDelayed(this::animateInhale, 8000);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopBreathing();
        handler.removeCallbacksAndMessages(null);
    }
}