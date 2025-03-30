package com.example.wellnesswrist;

import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BreathingViewModel extends ViewModel {
    public MutableLiveData<String> breathingText = new MutableLiveData<>("Inhale");
    public MutableLiveData<Float> scale = new MutableLiveData<>(1f);

    private final Handler handler = new Handler(Looper.getMainLooper());

    public void startBreathing() {
        // 4-7-8 breathing: Inhale 4s, Hold 7s, Exhale 8s
        animateInhale();
    }

    private void animateInhale() {
        breathingText.setValue("Inhale");
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 2f);
        animator.setDuration(4000);
        animator.addUpdateListener(animation -> scale.setValue((Float) animation.getAnimatedValue()));
        animator.start();
        handler.postDelayed(this::animateHold, 4000);
    }

    private void animateHold() {
        breathingText.setValue("Hold");
        handler.postDelayed(this::animateExhale, 7000);
    }

    private void animateExhale() {
        breathingText.setValue("Exhale");
        ValueAnimator animator = ValueAnimator.ofFloat(2f, 1f);
        animator.setDuration(8000);
        animator.addUpdateListener(animation -> scale.setValue((Float) animation.getAnimatedValue()));
        animator.start();
        handler.postDelayed(this::animateInhale, 8000);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacksAndMessages(null);
    }
}